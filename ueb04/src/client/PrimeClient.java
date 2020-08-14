package client;

import helper.Logger;
import helper.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Ein Client, der Anfragen an einen {@link server.PrimeServer} stellen kann. Die Anfragen
 * blockieren hierbei so lange bis eine Antwort vom Server eingegangen ist.
 * 
 * @author kar, mhe, Marcello
 */
public class PrimeClient implements Logger {

    /**
     * Das Socket des Clients
     */
    private final Socket primeServer;

    /**
     * Der Writer über dem der Client dem Server schreibt
     */
    private PrintWriter out = null;

    /**
     * Der Reader über dem der Client aus dem Server liest
     */
    private BufferedReader in = null;

    /**
     * Die ID des Clients
     */
    private int id = 0;

    /**
     * Host-Adresse des Hosts mit dem verbunden werden soll
     */
    private final String host;

    /**
     * Port Port auf dem verbunden werden soll
     */
    private final int port;

    /**
     * Der Log des Clients
     */
    private List<String> log = new LinkedList<>();

    /**
     * Konstruktor.
     * 
     * @param host Host-Adresse des Hosts mit dem verbunden werden soll
     * @param port Port Port auf dem verbunden werden soll
     * @throws IOException Verbindungsfehler
     */
    public PrimeClient(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        //Die verbindung zum Server über ein Socket herstellen
        primeServer = new Socket(host, port);
    }

    /**
     * Diese Methode muss vor allen anderen die mit dem Server kommunizieren einmalig aufgerufen
     * werden. Sie initialisiert die Kommunikation mit dem Server.
     * 
     * Es wird eine HALLO-Nachricht gesendet und es wird dann auf eine Antwort mit der zugeteilten
     * ID gewartet.
     * 
     * @throws IOException falls es ein Problem mit der Verbindung gibt
     */
    public void connect() throws IOException {
        //Loggen "connecting"
        addEntry("connecting");

        //Den Input und Output Stream zum Kommunizieren über das Socket
        //initzialisieren
        out = new PrintWriter(primeServer.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(primeServer.getInputStream()));

        //Kommunikation mit dem Server initzialisieren
        out.println(MessageType.HALLO);
        //Die vom Server an diesen Client vergebene ID auslesen
        id = Integer.parseInt(in.readLine());

        //Loggen "connected,id"
        addEntry("connected," + id);
    }

    /**
     * Beendet die Verbindung zum Server. Kehrt sofort zurück.
     * 
     * @throws IOException falls beim Schließen ein Netzwerkfehler auftritt (unwahrscheinlich).
     */
    public void disconnect() throws IOException {
        addEntry("disconnecting");
        primeServer.close();
    }

    /**
     * Fordert die nächste Primzahl zur übergebenen Zahl an.
     * 
     * @pre Es soll nur für positive Ganzzahlen die nächste Primzahl angefordert werden
     * @param q die Zahl, die geprüft werden soll
     * @return verwendet das die nächstgrößere Primzahl, oder die zahl selbst, falls sie prim ist
     * @throws IOException Netzwerkfehler
     */
    public long nextPrime(long q) throws IOException {
        assert q >= 0 : "Es dürfen nur positive Zahlen (>= 0) angefragt werden.";

        //Loggen "requesting: nextprime,N"
        addEntry("requesting: nextprime," + q);

        //Anfrage an den Server Stellen "ID,NEXTPRIME,N"
        out.println(id + "," + MessageType.NEXTPRIME + "," + q);

        //Auf Antowrt des Servers warten und die Nächste Primzahl auslesen
        long nextPrime = Integer.parseInt(in.readLine());

        //Loggen "response: nextprime,Nn"
        addEntry("response: nextprime," + nextPrime);

        return nextPrime;
    }

    /**
     * Fordert die Primfaktorzerlegung der übergebenen Zahl an.
     * 
     * @pre Es dürfen nur positive Ganzzahlen geprüft werden, die größer als eins sind (siehe
     *      Definition Primzahlen)
     * @param q die Zahl, die geprüft werden soll
     * @return die Liste der Primfaktoren von q, aufsteigend sortiert
     * @throws IOException Netzwerkfehler
     */
    public List<Long> primeFactors(long q) throws IOException {
        assert q > 1 : "Es dürfen nur positive Zahlen (> 1) angefragt werden.";

        //Loggen "requesting: primefactors,N"
        addEntry("requesting: primefactors," + q);

        //Anfrage an den Server stellen "ID,PRIMEFACTORS,N"
        out.println(id + "," + MessageType.PRIMEFACTORS + "," + q);

        //Auf die Antwort des Servers warten und dann die Anrtwort auslesen
        String primeFactors = in.readLine();
        //Array für die Einzelnen Primzahlen der Zerlegung
        String[] primeFactorsArray = primeFactors.split(" ");
        //liste für die Einzelnen Primzahlen der Zerlegung;
        List<Long> primeFactorsList = new LinkedList<>();
        //Der Liste die Einzelnen Primfaktoren der Zerlegung hinzufügen
        for (int pf = 0; pf < primeFactorsArray.length; pf++) {
            primeFactorsList.add(Long.parseLong(primeFactorsArray[pf]));
        }

        //Loggen "response: primefactors,Nl"
        addEntry("response: primefactors," + Arrays.toString(primeFactorsArray).replace(" ", ""));

        return primeFactorsList;
    }

    @Override
    public List<String> getLog() {
        return new LinkedList<>(log);
    }

    @Override
    public void addEntry(String e) {
        log.add(e);
    }

}
