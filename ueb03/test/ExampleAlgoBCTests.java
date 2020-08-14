import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import sciencelab.AlgorithmType;
import sciencelab.Scientist;

/**
 * Beispieltests für Aufgabenteile B (und C).
 * 
 * @author kar, mhe
 * 
 */
public class ExampleAlgoBCTests extends TestToolkit {

    /**
     * B. 2 Wissenschaftler, 1 Durchlauf
     */
    @Test
    public void testB1() throws InterruptedException {

        System.out.println();
        System.out.println("Test B1");
        System.out.println();

        // Zeitmessung starten
        c.tick();

        // 2 Wissenschaftlerthreads erzeugen und starten
        final Thread[] startAll = m.startAll(2, 1, 500, 500, 0, AlgorithmType.B, null);

        Assert.assertEquals("Es wurden nicht alle Threads erzeugt", 2, startAll.length);

        // Warten bis sich alle 2 Wissenschaftlerthreads beenden
        for (Thread t : startAll) {
            t.join();
            System.out.println("Joined: " + t.getName());
        }

        // Zeit nochmals messen
        c.tick();

        // Timing sicherstellen (mit Toleranz durch assertTiming-Methode)
        assertTiming(1500);
    }

    /**
     * B. 2 Wissenschaftler, 1 Durchlauf, Ausprobierenzeit länger als Bastelzeit.
     */
    @Test
    public void testB2a() throws InterruptedException {

        System.out.println();
        System.out.println("Test B2a");
        System.out.println();

        c.tick();

        final Thread[] startAll = m.startAll(2, 1, 250, 500, 0, AlgorithmType.B, null);

        Assert.assertEquals("Es wurden nicht alle Threads erzeugt", 2, startAll.length);

        for (Thread t : startAll) {
            t.join();
            System.out.println("Joined: " + t.getName());
        }

        c.tick();
        assertTiming(1000);
    }

    /**
     * B. 2 Wissenschaftler, 1 Durchlauf. Ausprobierenzeit kürzer als Bastelzeit.
     */
    @Test
    public void test2b() throws InterruptedException {

        System.out.println();
        System.out.println("Test B2b");
        System.out.println();

        c.tick();

        final Thread[] startAll = m.startAll(2, 1, 200, 100, 0, AlgorithmType.B, null);

        Assert.assertEquals("Es wurden nicht alle Threads erzeugt", 2, startAll.length);

        for (Thread t : startAll) {
            t.join();
            System.out.println("Joined: " + t.getName());
        }

        c.tick();
        assertTiming(500);
    }

    /**
     * B. 2 Wissenschaftler. 2 Durchlaeufe. Ausprobierenzeit kürzer als Bastelzeit.
     */
    @Test
    public void test2c() throws InterruptedException {

        System.out.println();
        System.out.println("Test B2c");
        System.out.println();

        c.tick();

        final Thread[] startAll = m.startAll(2, 2, 200, 100, 0, AlgorithmType.B, null);

        Assert.assertEquals("Es wurden nicht alle Threads erzeugt", 2, startAll.length);

        for (Thread t : startAll) {
            t.join();
            System.out.println("Joined: " + t.getName());
        }

        c.tick();
        assertTiming(900);
    }

    /**
     * B. 3 Wissenschaftler. 3 Durchläufe.
     */
    @Test
    public void test3() throws InterruptedException {

        System.out.println();
        System.out.println("Test B3");
        System.out.println();

        c.tick();

        final Thread[] startAll = m.startAll(3, 3, 100, 100, 0, AlgorithmType.B, null);

        Assert.assertEquals("Es wurden nicht alle Threads erzeugt", 3, startAll.length);

        for (Thread t : startAll) {
            t.join();
            System.out.println("Joined: " + t.getName());
        }

        c.tick();
        assertTiming(1000, 1200); // je nach Scheduling
    }

    /**
     * C. 2 Wissenschaftler. 1 Durchlauf. Test ohne wahnsinnig werdende Wissenschaftler.
     */
    @Test
    public void testC1() throws InterruptedException {

        System.out.println();
        System.out.println("Test C1");
        System.out.println();

        c.tick();

        // Diesmal Liste mit Wissenschaftlern besorgen
        final List<Scientist> scientists = new LinkedList<>();

        final Thread[] startAll = m.startAll(2, 1, 100, 100, 1000, AlgorithmType.C, scientists);

        Assert.assertEquals("Es wurden nicht alle Threads erzeugt", 2, startAll.length);
        Assert.assertEquals("Es wurden nicht alle Wissenschaftler erzeugt", 2, scientists.size());

        for (Thread t : startAll) {
            t.join();
            System.out.println("joined " + t.getName());
        }

        c.tick();
        assertTiming(300);

        Thread.sleep(EPS); // warten, bis sich auch die Timer beendet haben!
        Assert.assertEquals(NUM_NON_DEAMON, getNonDaemonThreads());

        // keiner darf wahnsinnig sein
        for (Scientist s : scientists) {
            Assert.assertEquals(false, s.isInsane());
        }

    }

    /**
     * C. 2 Studenten. 1 Durchlauf. Test ohne wahnsinnig werdenen Wissenschaftlern
     */
    @Test
    public void testC2() throws InterruptedException {

        System.out.println();
        System.out.println("Test C2");
        System.out.println();

        c.tick();

        // Diesmal Liste mit Wissenscahtlern besorgen
        final List<Scientist> Scientists = new LinkedList<>();

        final Thread[] startAll = m.startAll(2, 1, 100, 100, 300, AlgorithmType.C, Scientists);

        Assert.assertEquals("Es wurden nicht alle Threads erzeugt", 2, startAll.length);
        Assert.assertEquals("Es wurden nicht alle Studenten erzeugt", 2, Scientists.size());

        for (Thread t : startAll) {
            t.join();
            System.out.println("joined " + t.getName());
        }

        c.tick();

        assertTiming(300);
        Thread.sleep(EPS); // warten, bis sich auch die Timer beendet haben!
        Assert.assertEquals("Daemonische Threads", NUM_NON_DEAMON, getNonDaemonThreads());

        // keiner darf verhungert sein
        for (Scientist s : Scientists) {
            Assert.assertEquals(false, s.isInsane());
        }

    }

    /**
     * C. 2 Studenten. 1 Durchlauf. Test mit wahnsinnigen Wissenschaftlern.
     */
    @Test
    public void testC3() throws InterruptedException {

        System.out.println();
        System.out.println("Test C3");
        System.out.println();

        c.tick();

        // Diesmal Liste mit Studenten besorgen
        final List<Scientist> Scientists = new LinkedList<>();

        final Thread[] startAll = m.startAll(2, 1, 100, 100, 90, AlgorithmType.C, Scientists);

        Assert.assertEquals("Es wurden nicht alle Threads erzeugt", 2, startAll.length);
        Assert.assertEquals("Es wurden nicht alle Studenten erzeugt", 2, Scientists.size());

        for (Thread t : startAll) {
            t.join();
            System.out.println("joined " + t.getName());
        }

        c.tick();
        assertTiming(90);

        Thread.sleep(EPS); // warten, bis sich auch die Timer beendet haben!
        Assert.assertEquals(NUM_NON_DEAMON, getNonDaemonThreads());

        // keiner darf verhungert sein
        for (Scientist s : Scientists) {
            Assert.assertEquals(true, s.isInsane());
        }

    }

    /**
     * allen werden Wahnsinnig
     *
     * @throws InterruptedException
     */
    @Test
    public void testC4() throws InterruptedException {

        System.out.println();
        System.out.println("Test C4");
        System.out.println();

        c.tick();

        // Diesmal Liste mit Wissenschaftlern besorgen
        final List<Scientist> Scientists = new LinkedList<>();

        final Thread[] startAll =
                m.startAll(2, 1, 500, 500, 300, AlgorithmType.C, Scientists);

        Assert.assertEquals("Es wurden nicht alle Threads erzeugt", 2,
                startAll.length);
        Assert.assertEquals("Es wurden nicht alle Archäologen erzeugt", 2,
                Scientists.size());

        for (Thread t : startAll) {
            t.join();
            System.out.println("joined " + t.getName());
        }

        c.tick();
        assertTiming(300);

        Thread.sleep(EPS); // warten, bis sich auch die Timer beendet haben!
        Assert.assertEquals(NUM_NON_DEAMON, getNonDaemonThreads());

        // keinem darf langweilig sein
        for (Scientist a : Scientists) {
            Assert.assertEquals(true, a.isInsane());
        }
    }

    /**
     * Zwei werden Wahnsinnig
     * @throws InterruptedException
     */
    @Test
    public void testC5() throws InterruptedException {

        System.out.println();
        System.out.println("Test C5");
        System.out.println();

        c.tick();

        // Diesmal Liste mit Archäologen besorgen
        final List<Scientist> Scientist = new LinkedList<>();

        final Thread[] startAll =
                m.startAll(4, 1, 300, 500, 500, AlgorithmType.C, Scientist);

        Assert.assertEquals("Es wurden nicht alle Threads erzeugt", 4,
                startAll.length);
        Assert.assertEquals("Es wurden nicht alle Archäologen erzeugt", 4,
                Scientist.size());

        for (Thread t : startAll) {
            t.join();
            System.out.println("joined " + t.getName());
        }

        c.tick();
        assertTiming(800);

        Thread.sleep(EPS); // warten, bis sich auch die Timer beendet haben!
        Assert.assertEquals(NUM_NON_DEAMON, getNonDaemonThreads());

        int insaneScientists = 0;
        // keiner darf verhungert sein
        for (Scientist a : Scientist) {
            if (a.isInsane()) {
                insaneScientists++;
            }
        }

        Assert.assertEquals(insaneScientists, 2);

    }

    /**
     * Zwei werden Wahnsinnig 4 durchläfe
     * @throws InterruptedException
     */
    @Test
    public void testC6() throws InterruptedException {

        System.out.println();
        System.out.println("Test C6");
        System.out.println();

        c.tick();

        // Diesmal Liste mit Archäologen besorgen
        final List<Scientist> Scientist = new LinkedList<>();

        final Thread[] startAll =
                m.startAll(4, 4, 300, 500, 500, AlgorithmType.C, Scientist);

        Assert.assertEquals("Es wurden nicht alle Threads erzeugt", 4,
                startAll.length);
        Assert.assertEquals("Es wurden nicht alle Archäologen erzeugt", 4,
                Scientist.size());

        for (Thread t : startAll) {
            t.join();
            System.out.println("joined " + t.getName());
        }

        c.tick();
        // 4 * 800 ( 300 + 500), zwei verhungern wegen 300 + 300 > 500
        assertTiming(3200);

        Thread.sleep(EPS); // warten, bis sich auch die Timer beendet haben!
        Assert.assertEquals(NUM_NON_DEAMON, getNonDaemonThreads());

        int insaneScientists = 0;
        // keiner darf verhungert sein
        for (Scientist a : Scientist) {
            if (a.isInsane()) {
                insaneScientists++;
            }
        }

        Assert.assertEquals(insaneScientists, 2);

    }

    /**
     * 3 werden Wahnsinnig 5 durchläfe
     * @throws InterruptedException
     */
    @Test
    public void testC7() throws InterruptedException {

        System.out.println();
        System.out.println("Test C7");
        System.out.println();

        c.tick();

        // Diesmal Liste mit Archäologen besorgen
        final List<Scientist> Scientist = new LinkedList<>();

        final Thread[] startAll =
                m.startAll(5, 4, 300, 500, 500, AlgorithmType.C, Scientist);

        Assert.assertEquals("Es wurden nicht alle Threads erzeugt", 5,
                startAll.length);
        Assert.assertEquals("Es wurden nicht alle Wissenschaftler erzeugt", 5,
                Scientist.size());

        for (Thread t : startAll) {
            t.join();
            System.out.println("joined " + t.getName());
        }

        c.tick();
        // 4 * 800 ( 300 + 500), drei verhungern wegen 300 + 300 > 500
        assertTiming(3200);

        Thread.sleep(EPS); // warten, bis sich auch die Timer beendet haben!
        Assert.assertEquals(NUM_NON_DEAMON, getNonDaemonThreads());

        int insaneScientists = 0;
        // keiner darf verhungert sein
        for (Scientist a : Scientist) {
            if (a.isInsane()) {
                insaneScientists++;
            }
        }

        Assert.assertEquals(insaneScientists, 3);

    }

    /**
     * 3 werden Wahnsinnig 5 durchläfe
     * @throws InterruptedException
     */
    @Test
    public void testC8() throws InterruptedException {

        System.out.println();
        System.out.println("Test C8");
        System.out.println();

        c.tick();

        // Diesmal Liste mit Archäologen besorgen
        final List<Scientist> Scientist = new LinkedList<>();

        final Thread[] startAll =
                m.startAll(6, 5, 300, 500, 500, AlgorithmType.C, Scientist);

        Assert.assertEquals("Es wurden nicht alle Threads erzeugt", 6,
                startAll.length);
        Assert.assertEquals("Es wurden nicht alle Wissenschaftler erzeugt", 6,
                Scientist.size());

        for (Thread t : startAll) {
            t.join();
            System.out.println("joined " + t.getName());
        }

        c.tick();
        // 5 * 800 ( 300 + 500), drei verhungern wegen 300 + 500 > 500
        assertTiming(4000);

        Thread.sleep(EPS); // warten, bis sich auch die Timer beendet haben!
        Assert.assertEquals(NUM_NON_DEAMON, getNonDaemonThreads());

        int insaneScientists = 0;
        // keiner darf verhungert sein
        for (Scientist a : Scientist) {
            if (a.isInsane()) {
                insaneScientists++;
            }
        }

        Assert.assertEquals(insaneScientists, 3);

    }

    /**
     * keiner wird Wahnsinnig 5 durchläfe
     * @throws InterruptedException
     */
    @Test
    public void testC9() throws InterruptedException {

        System.out.println();
        System.out.println("Test C9");
        System.out.println();

        c.tick();

        // Diesmal Liste mit Archäologen besorgen
        final List<Scientist> Scientist = new LinkedList<>();

        final Thread[] startAll =
                m.startAll(6, 5, 300, 500, 800, AlgorithmType.C, Scientist);

        Assert.assertEquals("Es wurden nicht alle Threads erzeugt", 6,
                startAll.length);
        Assert.assertEquals("Es wurden nicht alle Wissenschaftler erzeugt", 6,
                Scientist.size());

        for (Thread t : startAll) {
            t.join();
            System.out.println("joined " + t.getName());
        }

        c.tick();
        // 5 * 800 (4000, da sich 3 immer Abwechseln) + 300 (da die ersten 3 den anderen 3 300 voraus sind)
        // (bei 6 können nur 3 gleichzeitig), keiner verhungert wegen 300 + 300 < 800
        assertTiming(4300);

        Thread.sleep(EPS); // warten, bis sich auch die Timer beendet haben!
        Assert.assertEquals(NUM_NON_DEAMON, getNonDaemonThreads());

        int insaneScientists = 0;
        // keiner darf verhungert sein
        for (Scientist a : Scientist) {
            if (a.isInsane()) {
                insaneScientists++;
            }
        }

        Assert.assertEquals(0, insaneScientists);

    }


    /**
     * 5 werden Wahnsinnig 9 durchläfe
     * @throws InterruptedException
     */
    @Test
    public void testC10() throws InterruptedException {

        System.out.println();
        System.out.println("Test C10");
        System.out.println();

        c.tick();

        // Diesmal Liste mit Archäologen besorgen
        final List<Scientist> Scientist = new LinkedList<>();

        final Thread[] startAll =
                m.startAll(9, 9, 200, 200, 450, AlgorithmType.C, Scientist);

        Assert.assertEquals("Es wurden nicht alle Threads erzeugt", 9,
                startAll.length);
        Assert.assertEquals("Es wurden nicht alle Wissenschaftler erzeugt", 9,
                Scientist.size());

        for (Thread t : startAll) {
            t.join();
            System.out.println("joined " + t.getName());
        }

        c.tick();
        // 9 * 400
        // 5 verhungern wegen 200 + 200 < 450
        assertTiming(3600);

        Thread.sleep(EPS); // warten, bis sich auch die Timer beendet haben!
        Assert.assertEquals(NUM_NON_DEAMON, getNonDaemonThreads());

        int insaneScientists = 0;
        // keiner darf verhungert sein
        for (Scientist a : Scientist) {
            if (a.isInsane()) {
                insaneScientists++;
            }
        }

        Assert.assertEquals(5, insaneScientists);

    }
}