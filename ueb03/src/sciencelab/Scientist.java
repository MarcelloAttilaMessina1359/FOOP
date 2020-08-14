package sciencelab;


import java.util.Timer;
import java.util.TimerTask;

/**
 * Ein potentiell leicht mental instabiler Wisschenschaftler, der in einem
 * Wissenschaftler-Gemeinschaftlabor arbeitet.
 * 
 * @author kar, mhe, Marcello
 * 
 */
public class Scientist {

    /**
     * Die Multitools.
     */
    private final MultiTool left, right;

    /**
     * So lange bastet er.
     */
    private final long tinkeringTimeMS;

    /**
     * So lange probiert er aus.
     */
    private final long tryoutTimeMS;

    /**
     * Name: Nur fuer debugging benötigt.
     */
    private final String id;

    /**
     * Die Anzahl an Durchläufen
     */
    private int n = 0;
    /**
     * Maximale Anzahl an Durchläufen.
     */
    private final int k;

    /**
     * Zeit, nach der er ohne Ausprobieren wahninnig wird.
     */
    private final long saneTimeMS;

    /**
     * Startzeit (nur für Logging).
     */
    private long startTime;

    /**
     * Ist der Wissenschaftler verrückt geworden
     */
    private boolean isInsane = false;

    /**
     * Timer um die Zeit bis zum verrückt werden eines Wissenschaftlers zu timen
     */
    private Timer goBananasTimer = null;

    /**
     * Arbeitet der Wissenschaftler
     */
    private volatile boolean working = false;



    /**
     * Konstruktor.
     * 
     * @param l linkes Multitool
     * @param r rechtes Multitool
     * @param tryoutTime Dauer des Ausprobierens in ms
     * @param tinkeringTime Dauer des Bastelns in ms
     * @param saneTime Zeit bis zum Wahnsinnigwerden (nur für Aufgabenteil C interessant)
     * @param idStr ID (nur für Debugging)
     * @param numSteps Anzahl der Basteln-Ausprobieren-Durchläufe
     */
    public Scientist(MultiTool l, MultiTool r, long tryoutTime, long tinkeringTime,
            long saneTime, String idStr, int numSteps) {
        this.left = l;
        this.right = r;
        this.tryoutTimeMS = tryoutTime;
        this.tinkeringTimeMS = tinkeringTime;
        this.saneTimeMS = saneTime;
        this.id = idStr;
        this.k = numSteps;
    }

    /**
     * 
     * @return Ist der Wissenschaftler verrückt geworden?
     */
    public boolean isInsane() {
        return isInsane;
    }

    /**
     * Logging. Schreibt id + Nachricht + vergangene Zeit auf stdout und flusht den Ausgabestream.
     * 
     * Für besser lesbare Zeitangaben sollte this.startTime zu Beginn der aufrufenden Methode
     * (startAlgoA|B|C) mit System.currentTimeMillis() initialisiert werden.
     * 
     * @param s Nachricht, die ausgegeben werden soll
     */
    private void printLog(String s) {
        System.out.println(id + ": " + s + " " + (System.currentTimeMillis() - this.startTime));
        System.out.flush();
    }

    /**
     * Implementierung Algorithmus A.
     */
    void startAlgoA() {
        startTime = System.currentTimeMillis();
        working = true;
        try {
            while (working) {

                left.tryAcquiringMultitool(this);
                printLog("Linkes Multifunktionswerkzeug nehmen");

                right.tryAcquiringMultitool(this);
                printLog("Rechtes Multifunktionswerkzeug nehmen");

                Thread.sleep(tinkeringTimeMS);
                printLog("Basteln");

                left.returnMultitool(this);
                right.returnMultitool(this);
                printLog("Beide Multifunktionswerkzeug zurücklegen");

                Thread.sleep(tryoutTimeMS);
                printLog("Ausprobieren");
                n++;
                if (n == k) {
                    working = false;
                }
            }
            printLog("Beendet");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Sollte nicht auftreten, Thread wurde"
                    + "von außerhalb unterbrochen");
        }

    }

    /**
     * Implementierung Algorithmus B.
     */
    void startAlgoB() {
        startTime = System.currentTimeMillis();
        working = true;
        try {
            while (working) {


                left.tryAcquiringMultitool(this);
                printLog("Linkes Multifunktionswerkzeug nehmen");
                if (!right.isInUse()) {


                    right.tryAcquiringMultitool(this);
                    printLog("Rechtes Multifunktionswerkzeug nehmen");

                    Thread.sleep(tinkeringTimeMS);
                    printLog("Basteln");

                    left.returnMultitool(this);
                    right.returnMultitool(this);
                    printLog("Beide Multifunktionswerkzeug zurücklegen");

                    Thread.sleep(tryoutTimeMS);
                    printLog("Ausprobieren");

                    n++;
                    if (n == k) {
                        working = false;
                    }
                } else {
                    left.returnMultitool(this);
                    printLog("Linkes Multifunktionswerkzeug zurücklegen");

                    synchronized (right) {
                        while (right.isInUse() && !Thread.currentThread().isInterrupted()) {
                            right.wait();
                        }
                    }
                    printLog("Warten, bis rechtes "
                            + "Multifunktionswerkzeug frei wird"
                            + " (aber nicht direkt nehmen!)");
                }
            }
            printLog("Beendet");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Sollte nicht auftreten, Thread wurde"
                    + "von außerhalb unterbrochen");
        }
    }

    /**
     * Methode startet einen Timer, bei Ablauf
     * ist der Wissenschaftler verrückt
     * @param scientistThread der Thread des Wissenschaftlers
     */
    private void startGoBananasTimer(Thread scientistThread) {
        goBananasTimer = new Timer();
        goBananasTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                scientistThread.interrupt();
            }
        }, saneTimeMS);
    }

    /**
     * Implementierung Algorithmus C.
     */
    void startAlgoC() {
        startTime = System.currentTimeMillis();
        startGoBananasTimer(Thread.currentThread());
        working = true;
        try {
            while (working) {

                left.tryAcquiringMultitool(this);
                printLog("Linkes Multifunktionswerkzeug nehmen");
                if (!right.isInUse()) {

                    right.tryAcquiringMultitool(this);
                    printLog("Rechtes Multifunktionswerkzeug nehmen");

                    Thread.sleep(tinkeringTimeMS);
                    printLog("Basteln");

                    left.returnMultitool(this);
                    right.returnMultitool(this);
                    printLog("Beide Multifunktionswerkzeug zurücklegen");

                    goBananasTimer.cancel();
                    Thread.sleep(tryoutTimeMS);
                    printLog("Ausprobieren (kein wahnsinnig werden möglich)");

                    startGoBananasTimer(Thread.currentThread());
                    printLog("Zeit bis zum Verhungern ab hier neu messen");
                    n++;
                    if (n == k) {
                        working = false;
                        goBananasTimer.cancel();
                    }
                } else {

                    left.returnMultitool(this);
                    printLog("Linkes Multifunktionswerkzeug zurücklegen");

                    synchronized (right) {
                        while (right.isInUse()
                                && !Thread.currentThread().isInterrupted()) {
                            right.wait();
                        }
                    }
                    printLog("Warten, bis rechtes "
                            + "Multifunktionswerkzeug frei wird"
                            + " (aber nicht direkt nehmen!)");
                }

            }
            printLog("Beendet");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            isInsane = true;
            working = false;
            printLog("Muahahahahahahaaaaaaa!");
            right.returnMultitool(this);
            left.returnMultitool(this);
        }
        goBananasTimer.cancel();
    }
}
