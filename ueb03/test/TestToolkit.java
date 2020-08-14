import java.util.Map;

import org.junit.Assert;

import sciencelab.Main;

/**
 * 
 * Klasse, die Methoden zur Zeitmessung und zum Starten der Threads benötigten Objekte (Main)
 * bereitstellt.
 * 
 * @author kar, mhe
 * 
 */
public class TestToolkit {

    /**
     * Tolerierte Abweichung nach oben in ms.
     */
    public static final long EPS = 50;

    /**
     * Tolerierte Abweichung nach unten in ms. Kompensiert Ungenauigkeiten in der Zeitmessung.
     */
    public static final long IMPRECISION = 20;

    /**
     * Merken, wie viele Threads schon vorher laufen (Eclipse verhaelt sich anders als CmdLine!), um
     * spaeter testen zu koennen, dass sich alle beendet haben.
     */
    protected static final int NUM_NON_DEAMON = getNonDaemonThreads();

    /**
     * Zeitmessung.
     */
    protected final MilliClock c = new MilliClock();

    /**
     * Main-Klasse zur Verwaltung der Threads.
     */
    protected final Main m = new Main();

    /**
     * Stellt für einen Wert sicher, dass der Algorithmus im vorgegeben Zeitfenster gelaufen ist.
     * 
     * @param estimatedTime kuerzeste moegliche Dauer, die der Algorithmus braucht
     */
    public void assertTiming(long estimatedTime) {
        assertTiming(estimatedTime, estimatedTime);
    }

    /**
     * Stellt für einen minimalen und maximalen Wert sicher, dass der Algorithmus im vorgegeben
     * Zeitfenster gelaufen ist.
     * 
     * @param smallestEstimatedTime kuerzest moegliche Dauer, die der Algorithmus braucht
     * @param biggestEstimatedTime laengst moegliche Dauer, die der Algorithmus braucht
     */
    public void assertTiming(long smallestEstimatedTime, long biggestEstimatedTime) {

        final long diff = c.getDiffSinceLastCallMilli();

        Assert.assertTrue("Algorithmus ist zu schnell! Erlaubt: " + smallestEstimatedTime
                + " Gedauert: " + diff, diff >= smallestEstimatedTime - IMPRECISION);

        Assert.assertTrue("Algorithmus ist zu langsam! Erlaubt: " + biggestEstimatedTime
                + " Gedauert: " + diff, diff < biggestEstimatedTime + EPS);

    }

    /**
     * Gibt die Anzahl der existierenden "Vordergrund"-Threads zurück.
     * 
     * @return Anzahl der existierenden "Vordergrund"-Threads
     */
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
