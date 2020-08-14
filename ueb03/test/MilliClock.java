/**
 * Eine "Stoppuhr", die in Millisekunden die Zeit seit dem letzten Tick misst.
 * 
 * @author kar, mhe
 * 
 */
public class MilliClock {

    /**
     * Zeit des letzten Aufrufs.
     */
    private long lastCall;

    /**
     * Letzte gemessene Differenz zwischen zwei Aufrufen.
     */
    private long lastDiff;

    /**
     * Konstruktor. Aktuelle Zeit wird gespeichert.
     */
    public MilliClock() {
        lastCall = System.currentTimeMillis();
        lastDiff = 0;
    }

    /**
     * Tick. Muss am Anfang jedes Tests aufgerufen werden um die aktuelle Zeit zu speichern.
     * Berechnet die Differenz zwischen dem aktuellen und vorhergehenden Aufruf.
     * 
     */
    public void tick() {
        final long current = System.currentTimeMillis();
        lastDiff = current - lastCall;
        lastCall = current;
    }

    /**
     * Liefert die letzte ermittelte Zeitdifferenz.
     * 
     * @return letzte ermittelte Zeitdifferenz
     */
    public long getDiffSinceLastCallMilli() {
        return lastDiff;
    }

}
