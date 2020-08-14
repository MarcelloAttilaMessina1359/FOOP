import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import helper.MessageType;
import org.junit.Assert;

import org.junit.Test;

import server.PrimeServer;
import client.PrimeClient;

public class BeispieltestClientServer {

    private static final int SLEEP_TIME = 200;

    private static final int PORT = 4242;
    
    private static final int PARTITION_SIZE = 1000;

    private static final int DELAY = 10;


    @Test
    public void justConnection() throws IOException, InterruptedException {
        
        final PrimeServer server = new PrimeServer(PORT, PARTITION_SIZE);
        server.startServer(DELAY);

        final PrimeClient client = new PrimeClient("localhost", PORT);
        client.connect();

        // warten, sodass der Server die Nachricht sicher schon erhalten hat
        Thread.sleep(SLEEP_TIME);

        List<String> slog = server.getLog();
        List<String> clog = client.getLog();

        Assert.assertEquals(mkList("client connected,1"), slog);
        Assert.assertEquals(mkList("connecting", "connected,1"), clog);

        client.disconnect();
        server.stopServer();

        slog = server.getLog();
        clog = client.getLog();

        Assert.assertEquals(
                mkList("client connected,1", "client disconnected,1"), slog);
        Assert.assertEquals(

                mkList("connecting", "connected,1", "disconnecting"), clog);
    }

    @Test
    public void simpleNextPrime() throws IOException, InterruptedException {
                
        final PrimeServer server = new PrimeServer(PORT, PARTITION_SIZE);
        server.startServer(DELAY);

        final PrimeClient client = new PrimeClient("localhost", PORT);
        client.connect();

        long nextPrime = client.nextPrime(5);
        Assert.assertEquals(5L, nextPrime);

        // warten, so dass der Server die Nachricht schon erhalten hat und die
        // Berechnung sicher abgeschlossen ist
        Thread.sleep(SLEEP_TIME);

        client.disconnect();

        // warten, sodass der Server die Nachricht schon erhalten hat
        Thread.sleep(SLEEP_TIME);

        server.stopServer();
        List<String> slog = server.getLog();
        List<String> clog = client.getLog();

        Assert.assertEquals(mkList("client connected,1",
                "requested: 1,nextprime,5,5", "client disconnected,1"), slog);
        Assert.assertEquals(
                mkList("connecting", "connected,1", "requesting: nextprime,5",
                        "response: nextprime,5", "disconnecting"), clog);
    }







    @Test
    public void simplePrimeFactors() throws IOException, InterruptedException {
        
        final PrimeServer server = new PrimeServer(PORT, PARTITION_SIZE);
        server.startServer(DELAY);

        final PrimeClient client = new PrimeClient("localhost", PORT);
        client.connect();

        List<Long> factors = client.primeFactors(6);
        Assert.assertEquals((Long)2L, factors.get(0));
        Assert.assertEquals((Long)3L, factors.get(1));
        

        // warten, so dass der Server die Nachricht schon erhalten hat und die
        // Berechnung sicher abgeschlossen ist
        Thread.sleep(SLEEP_TIME);

        client.disconnect();

        // warten, sodass der Server die Nachricht schon erhalten hat
        Thread.sleep(SLEEP_TIME);

        server.stopServer();

        List<String> slog = server.getLog();
        List<String> clog = client.getLog();
        Assert.assertEquals(mkList("client connected,1",
                "requested: 1,primefactors,6,[2,3]", "client disconnected,1"), slog);
        Assert.assertEquals(
                mkList("connecting", "connected,1", "requesting: primefactors,6",
                        "response: primefactors,[2,3]", "disconnecting"),
                clog);
    }

    @Test
    public void testTwoClientsInterlocked()
            throws IOException, InterruptedException {

        try {
            Thread.sleep(SLEEP_TIME);
            // 2 Clients parallel, je Client eine Anfrage

            final PrimeServer server = new PrimeServer(PORT, PARTITION_SIZE);
            server.startServer(DELAY);

            final PrimeClient client1 = new PrimeClient("localhost", PORT);
            client1.connect();

            final PrimeClient client2 = new PrimeClient("localhost", PORT);
            client2.connect();

            final Thread c1 = new Thread(new Runnable() {

                public void run() {
                    try {
                        client1.nextPrime(11); // Was in einem Thread passiert, bleibt in einem Thread (Assertions und Exceptions kommen da nicht raus)
                    } catch (IOException e) {
                        System.err.println("Unerwartete Exception in c1: " + e);
                    }
                }
            });
            c1.start();

            final Thread c2 = new Thread(new Runnable() {

                public void run() {
                    try {
                        client2.nextPrime(1); // Was in einem Thread passiert, bleibt in einem Thread (Assertions und Exceptions kommen da nicht raus)
                    } catch (IOException e) {
                        System.err.println("Unerwartete Exception in c2: " + e);
                    }
                }
            });
            c2.start();

            // Warten bis beide zurckgekehrt sind
            c1.join();
            c2.join();

            // Abmelden (sicherstellen, dass wirklich nacheinander)
            Thread.sleep(SLEEP_TIME);
            client1.disconnect();
            Thread.sleep(SLEEP_TIME);
            client2.disconnect();

            // Server stoppen
            server.stopServer();

            // Logs besorgen
            List<String> slog = server.getLog();
            List<String> clog1 = client1.getLog();
            List<String> clog2 = client2.getLog();

            // Erwartete Logeinträge generieren
            Assert.assertEquals(mkList("client connected,1", "client connected,2"), slog.subList(0, 2));
            Assert.assertTrue(slog.contains("requested: 1,nextprime,11,11"));
            Assert.assertTrue(slog.contains("requested: 2,nextprime,1,2"));
            Assert.assertEquals(mkList("client disconnected,1", "client disconnected,2"), slog.subList(slog.size() - 2, slog.size()));

            Assert.assertEquals(
                    mkList("connecting", "connected,1",
                            "requesting: nextprime,11", "response: nextprime,11",
                            "disconnecting"), clog1);

            Assert.assertEquals(
                    mkList("connecting", "connected,2",
                            "requesting: nextprime,1", "response: nextprime,2",
                            "disconnecting"), clog2);
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
            e.getLocalizedMessage();
        }
    }


    @Test
    public void testMultipleClientsInterlocked()
            throws IOException, InterruptedException {

        try {
            Thread.sleep(SLEEP_TIME);
            // 2 Clients parallel, je Client eine Anfrage

            final PrimeServer server = new PrimeServer(PORT, 1);
            server.startServer(DELAY);

            final PrimeClient client1 = new PrimeClient("localhost", PORT);
            client1.connect();

            final PrimeClient client2 = new PrimeClient("localhost", PORT);
            client2.connect();

            final PrimeClient client3 = new PrimeClient("localhost", PORT);
            client3.connect();

            final PrimeClient client4 = new PrimeClient("localhost", PORT);
            client4.connect();

            final Thread c1 = new Thread(new Runnable() {

                public void run() {
                    try {
                        client1.nextPrime(11); // Was in einem Thread passiert, bleibt in einem Thread (Assertions und Exceptions kommen da nicht raus)
                    } catch (IOException e) {
                        System.err.println("Unerwartete Exception in c1: " + e);
                    }
                }
            });
            c1.start();

            final Thread c2 = new Thread(new Runnable() {

                public void run() {
                    try {
                        client2.nextPrime(1); // Was in einem Thread passiert, bleibt in einem Thread (Assertions und Exceptions kommen da nicht raus)
                    } catch (IOException e) {
                        System.err.println("Unerwartete Exception in c2: " + e);
                    }
                }
            });
            c2.start();



            final Thread c3 = new Thread(new Runnable() {

                public void run() {
                    try {
                        client3.primeFactors(2); // Was in einem Thread passiert, bleibt in einem Thread (Assertions und Exceptions kommen da nicht raus)
                    } catch (IOException e) {
                        System.err.println("Unerwartete Exception in c3: " + e);
                    }
                }
            });
            c3.start();

            final Thread c4 = new Thread(new Runnable() {

                public void run() {
                    try {
                        client4.primeFactors(300); // Was in einem Thread passiert, bleibt in einem Thread (Assertions und Exceptions kommen da nicht raus)
                    } catch (IOException e) {
                        System.err.println("Unerwartete Exception in c4: " + e);
                    }
                }
            });
            c4.start();




             //Warten bis beide zurckgekehrt sind
            c1.join();
            c2.join();
            c3.join();
            c4.join();
            // Abmelden (sicherstellen, dass wirklich nacheinander)
            Thread.sleep(SLEEP_TIME);
            client1.disconnect();
            Thread.sleep(SLEEP_TIME);
            client2.disconnect();
            Thread.sleep(SLEEP_TIME);
            client3.disconnect();
            Thread.sleep(SLEEP_TIME);
            client4.disconnect();

            // Server stoppen
            server.stopServer();

            // Logs besorgen
            List<String> slog = server.getLog();
            List<String> clog1 = client1.getLog();
            List<String> clog2 = client2.getLog();
            List<String> clog3 = client3.getLog();
            List<String> clog4 = client4.getLog();

            System.err.println(slog);
            // Erwartete Logeinträge generieren



            Assert.assertEquals(mkList("client connected,1", "client connected,2"), slog.subList(0, 2));
            Assert.assertTrue(slog.contains("requested: 1,nextprime,11,11"));
            Assert.assertTrue(slog.contains("requested: 2,nextprime,1,2"));
            Assert.assertEquals(mkList("client disconnected,3", "client disconnected,4"), slog.subList(slog.size() - 2, slog.size()));
            Assert.assertTrue(slog.contains("requested: 3,primefactors,2,[2]"));
            Assert.assertTrue(slog.contains("requested: 4,primefactors,300,[2,2,3,5,5]"));



            Assert.assertEquals(
                    mkList("connecting", "connected,1",
                            "requesting: nextprime,11", "response: nextprime,11",
                            "disconnecting"), clog1);

            Assert.assertEquals(
                    mkList("connecting", "connected,2",
                            "requesting: nextprime,1", "response: nextprime,2",
                            "disconnecting"), clog2);



            Assert.assertEquals(
                    mkList("connecting", "connected,3",
                            "requesting: primefactors,2", "response: primefactors,[2]",
                            "disconnecting"), clog3);


            Assert.assertEquals(
                    mkList("connecting", "connected,4",
                            "requesting: primefactors,300", "response: primefactors,[2,2,3,5,5]",
                            "disconnecting"), clog4);


        } catch (NullPointerException e) {
            e.printStackTrace();
            e.getLocalizedMessage();
        }
    }




    /**
     * Generieren einer Liste aus beliebig vielen Strings.
     *
     * @param x Array von String
     * @return eine Liste mit allen Elementen aus x in der gleichen Reihenfolge
     *         wie angegeben
     */
    public static List<String> mkList(String... x) {
        final List<String> res = new LinkedList<>();
        for (int i = 0; i < x.length; i++) {
            res.add(x[i]);
        }
        return res;
    }
    public static int getNonDaemonThreads() {

        final Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        final StringBuilder out = new StringBuilder();
        int i = 0;

        for (Thread t : allStackTraces.keySet()) {
            if (!t.isDaemon() && !t.getClass().toString().contains("junit")) {
                out.append(" ").append(t.getName());
                i++;
            }
        }
        System.out.println("Running threads: (" + i + "):" + out.toString());
        return i;
    }
}
