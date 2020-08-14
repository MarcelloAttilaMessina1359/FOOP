package server;

import helper.Logger;
import helper.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Ein Server der per TCP-Verbindung Anfragen an einen PrimeManager ermöglicht. Der Server kann
 * hierbei mit beliebige vielen Clients gleichzeitig kommunzieren. Die Kommunikation zwischen Client
 * und Server verläuft stets synchron. Clients dürfen sich allerdings nicht gegenseitig blockieren.
 * 
 * @author kar, mhe, Marcello
 * 
 */
public class PrimeServer implements Logger {

    /**
     * Der zu nutzene TCP-Port des Servers
     */
    private final int port;

    /**
     * Die Paritionsgröße für den PrimeGenerator
     */
    private int partitionSize = 0;

    /**
     * Der Server Log
     */
    private List<String> log = new LinkedList<>();

    /**
     * Der PrimeManager des PrimeServers, berechnet die Primzahlen
     * die benötigt werden um Client seitige anfragen zu beantworten
     */
    private PrimeManager primeManager = null;

    /**
     * Das Serversocket des PrimesServers
     */
    private final ServerSocket primeServerSocket;

    /**
     * Lauscht der Server oder nicht
     */
    private volatile boolean listening = false;

    /**
     * Die IDs Thread sicher vergeben
     */
    private AtomicInteger idCounter = new AtomicInteger(1);

    /**
     * Die Liste der aktiven primeClientHandler
     */
    private List<Thread> primeClientHandlers = new LinkedList<>();

    /**
     * Konstruktor.
     * 
     * @pre partitionSize größer 0
     * 
     * @param port Der zu nutzene TCP-Port
     * @param partitionSize Paritionsgröße für den PrimeGenerator
     * 
     * @throws IOException Netzwerkfehler
     */
    public PrimeServer(int port, int partitionSize) throws IOException {
        assert partitionSize >= 1 : "PartitionSize muss >= 1 sein.";
        this.port = port;
        this.partitionSize = partitionSize;
        //Das Serversocket des Primeservers mit der Portzahl initzialiseren
        primeServerSocket = new ServerSocket(port);
    }

    /**
     * Startet den Server und den Worker des PrimeGenerators, welcher die Berechnungen übernimmt,
     * mit dem entsprechenden delay. Diese Methode muss sofort zurückkehren.
     * 
     * Für jede Clientverbindung wird ein eigener Thread gestartet.
     * 
     * @pre delay ist größer gleich 0
     * @param delay Das delay in ms für den PrimeGenerator
     * @throws IOException Netzwerkfehler
     */
    public void startServer(long delay) throws IOException {
        assert delay >= 0 : "Delay muss > 0 sein!";
        //PrimeManager initzialiseren
        primeManager = new PrimeManager(partitionSize);
        primeManager.startWorker(delay);

        //Der Server soll nun Lauschen, listening = true
        listening = !listening;

        //Thread für den Server initzialiseren und Starten.
        Thread primeServer = new Thread((() -> {

            //Der ClientHandler
            Thread primeClientHandler = null;
            try {
                while (listening) {
                    //Den Handler initzialisieren & starten
                    primeClientHandler =
                           new Thread(new PrimeClientHandler(primeServerSocket.accept()));
                    primeClientHandler.start();
                    //Der Liste aller Handler hinzufuegen
                    primeClientHandlers.add(primeClientHandler);
                }

            } catch (IOException e) {
                System.err.println("Das ServerSocket ist geschlossen,"
                        + " es akzeptiert keine Verbindungen mehr");
            }
        }));

        primeServer.start();
    }

    /**
     * Stoppt den Server. Es werden keine neuen Verbindungen mehr angenommen. Bereits bestehende
     * Verbindungen laufen jedoch normal weiter und Anfragen von bereits verbundenen Clients werden
     * noch abgearbeitet. Die Methode kehrt erst zurück, wenn alle Clients die Verbindung beendet
     * haben.
     * 
     * Erst, wenn alle Clients ihre Verbindung beendet haben, wird auch der PrimeGenerator gestoppt
     * um zu verhindern, dass ein Client bis "in alle Ewigkeit" auf eine Antwort wartet.
     * 
     * @throws IOException Netzwerkfehler
     */
    public void stopServer() throws IOException {
        //Stoppt den Server. Es werden keine neuen Verbindungen mehr angenommen.
        listening = !listening;

        //Auf bestehende Verbindungen Warten bis alle Clients abgemeldet sind
        for (Thread pch: primeClientHandlers) {
            try {
                pch.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        primeServerSocket.close();
        primeManager.stopWorker();
    }

    @Override
    public void addEntry(String e) {
        synchronized (log) {
            log.add(e);
        }
    }

    @Override
    public List<String> getLog() {
        return new LinkedList<>(log);
    }

    /**
     * Enum der die Verschieden Parameter einer PrimeClient Anfrage
     * aufzählt
     */
    private enum CommandParameters {
        ID, REQUEST, NUMBER
    }

    /**
     * Klasse die Vom Server genutzt wird um mit unterschiedlichen Clients
     * Kommunizieren zu können. Der Server "leitet" ein neuen Client an
     * einen PrimeClientHandler weiter um dessen Anfragen beantworten zu
     * können.
     */
    private class PrimeClientHandler implements Runnable {

        /**
         * Das Socket über dem die Verbindung zum primeClient besteht
         */
        private Socket primeClient = null;

        /**
         * Der OutputStream des Sockets
         */
        private PrintWriter out = null;

        /**
         * Der InputStream des Sockets
         */
        private BufferedReader in = null;

        /**
         * Die ID die dem Client vom Server vergeben wird
         */
        private int clientId = -1;

        /**
         * ttqw
         */
        private final long test = 6;

        /**
         * Konstruktor
         * @param primeClient das Socket über dem die Verbindung zum
         *                    primeClient besteht
         */
        PrimeClientHandler(Socket primeClient) {
            this.primeClient = primeClient;
        }

        @Override
        public void run() {
            //Streams Initzialiseren
            try {
                out = new PrintWriter(primeClient.getOutputStream(), true);
            } catch (IOException e) {
                System.err.println("Es konnte kein OutputStream zwischen"
                        + "Client und Server hergestellt werden");
            }
            try {
                in = new BufferedReader(new InputStreamReader(primeClient.getInputStream()));
            } catch (IOException e) {
                System.err.println("Es konnte kein InputStream zwischen"
                        + "Client und Server hergestellt werden");
            }

            //Der vom Client gesendete Input
            String inputLine = null;


            //Die Verbindung herstellen
            try {
                inputLine = in.readLine();
                //Die Verbindung wurde hergestellt, der Server v3ergibt eine ID
                clientId = idCounter.getAndIncrement();
                //Loggen
                addEntry("client connected," + clientId);
                //Dem Client die ID senden
                out.println(clientId);
                System.out.flush();
            } catch (IOException e) {
                System.err.println("Bei der Verbindung zum Client ist was schief gelaufen");
            }

            //Asulesen und beantworten von Clientseitigen Anfragen
            try {
                while (((inputLine = in.readLine()) != null)) {
                    //Die Parameter der Anfrage

                    String[] parameters = inputLine.split(",");
                    String id = parameters[CommandParameters.ID.ordinal()],
                            request = parameters[CommandParameters.REQUEST.ordinal()],
                            number = parameters[CommandParameters.NUMBER.ordinal()];

                    if (request.equals(MessageType.NEXTPRIME.toString())) {

                        //Nächste Primzahl anfragen
                        Long nextPrime = primeManager.nextPrime(Long.parseLong(number));
                        //Loggen
                        addEntry("requested: " + id + ",nextprime," + number + "," + nextPrime);
                        //Dem Client Antworten
                        out.println(nextPrime);
                    } else {

                        //Primfaktorenzerlegung Anfragen
                        List<Long> primeFactors = primeManager.primeFactors(Long.parseLong(number));
                        //Loggen
                        addEntry("requested: " + id + ",primefactors" + ","
                                + number + ","
                                + Arrays.toString(primeFactors.toArray()).replace(" ", ""));
                        //Dem Client Antworten
                        out.println(primeFactors.toString()
                                .replace(",", "")
                                .replace("[", "")
                                .replace("]", ""));
                    }
                }
                primeClient.close();
                addEntry("client disconnected," + clientId);
            } catch (IOException e) {
                System.err.println("Bei Lesen der Anfrage des "
                        + "Clients ist etwas scheif gelaufen");
            }
        }
    }

}
