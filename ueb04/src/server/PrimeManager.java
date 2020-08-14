package server;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.RecursiveTask;

import helper.Logger;

/**
 * 
 * Generiert dauerhaft Primzahlen in einem eigenen Thread und bietet auf Basis der generierten
 * Primzahlen die Ermittlung der jeweils nächstgrößeren Primzahl zu einer übergebenen Zahl und die
 * Zerlegung einer übergebenen Zahl in Primfaktoren an.
 * 
 * Sofern die benötigte(n) Primzahl(en) zum Anfragezeitpunkt schon berechnet wurde(n), werden
 * Anfragen sofort beantwortet. Falls dies nicht der Fall ist, warten die Anfragen bis die
 * entsprechende(n) Primzahl(en) berechnet wurde(n).
 * 
 * Der PrimeManager muss jederzeit eine beliebige Anzahl von Anfragen gleichzeitig bearbeiten
 * können.
 * 
 * @author kar, mhe, Marcello
 * 
 */
public class PrimeManager implements Logger {

    /**
     * Die erste zu überprüfende Primzahl
     */
    private final long firstPrimeCandidate = 3L;

    /**
     * Anzahl der Primzahlen (Faktorkandidaten), die von einem Task maximal getestet werden
     */
    private int partitionSize = 0;

    /**
     * Flag, die bestimmt ob der Arbeiter, wenn gestartet, arebeiten soll
     */
    private volatile boolean work = false;

    /**
     * Der workerThread zum berechnen der Primzahlen
     */
    private Thread workerThread = null;

    /**
     * Liste mit den bereits berechneten Primzahlen
     */
    private ConcurrentSkipListSet<Long> primes = new ConcurrentSkipListSet<>();

    /**
     * WaitSet aller Anfragen für dessen Ergebniss noch
     * keine Primezahl zur berechnung vorliegt
     */
    private ConcurrentHashMap<Long, FutureResult> waitMap = new ConcurrentHashMap<>();

    /**
     * Die aktuelle auf Primzahl zu überprüfende Zahl
     */
    private long primeCandidate = firstPrimeCandidate;

    /**
     * Der Log des PrimeManagers
     */
    private List<String> log = new LinkedList<>();

    /**
     * Konstruktor.
     * 
     * Mittels partitionSize wird die Größe der Partionen für die Primfaktorenzerlegung angegeben
     * (Anzahl der Primzahlen (Faktorkandidaten), die von einem Task maximal getestet werden).
     * 
     * @pre partitionSize ist größer gleich 1
     * 
     * @param partitionSize Größe der Partition für ForkJoin-Primfaktorenzerlegung
     */
    public PrimeManager(int partitionSize) {
        assert partitionSize >= 1 : "Es können nur Intervalle (>= 1) gebildet werden.";
        this.partitionSize = partitionSize;
        //Es werden im WorkerThread nur Zahlen > 2 mittels dem Sieb des
        //Eratosthenes geprueft
        primes.add(2L);
        addEntry("found prime: 2");
    }

    /**
     * Liefert zu der übergebenen Zahl die nächstgrößere Primzahl. Ist die übergebene Zahl selbst
     * bereits prim, so wird sie als Ergebnis zurückgegeben.
     * 
     * Wenn die Aussage zum Zeitpunkt der Anfrage noch nicht getroffen werden kann, wird so lange
     * gewartet bis dies möglich ist.
     * 
     * @pre Die übergebene Zahl muss eine positive Ganzzahl (inkl. 0) sein
     * @param q Die Zahl für die, die nächstgrößere Primzahl ermittelt werden soll
     * @return die nächstgrößere Primzahl oder die Zahl selbst (falls sie selbst prim ist)
     */
    public long nextPrime(long q) {
        assert (q >= 0) : "nextPrime muss mit einer positiven Ganzzahl aufgerufen werden.";

        //Loggen
        addEntry("requested: nextprime," + q);
        //Die nächst >= Pimzahl holen
        Long nextPrime = getNextPrime(q);

        addEntry("response: nextprime," + q + "," + nextPrime);

        return nextPrime;
    }

    /**
     * Liefert eine aufsteigend sortierte Liste aller Primfakoren der übergebenen Zahl q.
     * 
     * Wenn die Berechnung zum Zeitpunkt der Anfrage noch nicht stattfinden kann, wird so lange
     * gewartet bis dies möglich ist.
     * 
     * @pre Es dürfen nur positive Ganzzahlen geprüft werden, die größer gleich 2 sind (siehe
     *      Definition Primzahlen)
     * @param q Die zu zerlegende Zahl
     * @return Liste mit denm aufsteigend sortierten Primfaktoren von q
     */
    public List<Long> primeFactors(long q) {
        assert (q >= 2) : "PrimeFactors muss mit einer positiven Ganzzahl >=2 aufgerufen werden.";

        //Loggen
        addEntry("requested: primefactors," + q);

        //Die hälfte von q bestimmen
        Long halfQ = q / 2;

        //Die nächst >= Pimzahl holen
        Long halfPrime = getNextPrime(halfQ);

        //Die Resultat Liste
        List<Long> primeFactors = new LinkedList<>();
        //Liste alle primzahlen bis q / 2 hinzufügen
        List<Long> primes = new LinkedList<>();
        Iterator<Long> primesIterator = this.primes.iterator();
        Long currentPrime = null;

        while (primesIterator.hasNext()
                && (currentPrime = primesIterator.next()) <= halfPrime) {
            primes.add(currentPrime);
        }
        PrimeFactorizationTask primeFactorizationTask = new PrimeFactorizationTask(primes, q);
        primeFactors = primeFactorizationTask.compute();

        //Wenn es keine Primfaktor Zerlegung gibt ist die Zahl selbst Prim
        if (primeFactors.isEmpty()) {
            primeFactors.add(q);
        }

        addEntry("response: primefactors," + q + "," + primeFactors.toString().replace(" ", ""));

        return primeFactors;
    }




    /**
     * Methode die zu der übergebenen Zahl q die nächst größere oder
     * q selber, wenn es sich bei q um eine Primzahl handlet, Primzahl
     * zurück gibt und wartet wenn diese noch nicht vorliegt
     *
     * @param q von der Zahl aus nächst größer/gleiche Primzahl zu ermitteln
     * @return q wenn q selber eine primzahl ist, sonst die Nächst größere primzahl,
     *         existiert die Zahl noch dann null
     */
    private Long getNextPrime(long q) {

        //Die nächst >= Pimzahl holen
        Long nextPrime = primes.ceiling(q);

        //Wenn nicht vorhanden
        if (nextPrime == null) {
            FutureResult futureResult = null;

            synchronized (waitMap) {
                //Neuen waitlong der map hinzufügen, wenn nicht schon vorhanden
                futureResult = waitMap.putIfAbsent(q, new FutureResult());

                //Falls es der erste Eintrag ist ist waitlong null
                if (futureResult == null) {
                    //Den Wert zum key q nochmals fetchen
                    futureResult = waitMap.get(q);
                }
            }

            //Auf das Ergbnis warten
            futureResult.waitForResult();

            //Sobald das Ergebnis vorliegt erneut auslesen
            nextPrime = futureResult.result;

        }
        return nextPrime;
    }

    /**
     * Methode die zu der übergebenen Zahl q die nächst kleinere
     * Primzahl zurück gibt
     * @param q die Zahl von der aus die nächst kleinere Primzahl ermittelt wird
     * @return die Nächst kleiner Prfimzahl von q aus, null falls es keine gibt
     */
    private Long getPreviousPrime(long q) {
        return primes.floor(q - 1);
    }

    /**
     * Liefert eine Kopie aller bis zum aktuellen Zeitpunkt gefundenen Primzahlen.
     * 
     * Diese Methode ist nur zu Test- und Debugzwecken vorgesehen und darf auch nur dafür verwendet
     * werden.
     * 
     * @return Eine Kopie aller bis jetzt gefundenen Primzahlen.
     */
    public Collection<Long> knownPrimes() {
        return new ConcurrentSkipListSet<>(primes);
    }

    /**
     * Startet den PrimeWorker-Thread und somit die Berechnung der Primzahlen ab der Zahl 2. Das
     * übergebene delay wird verwendet um die Berechnungen jeweils um den übergebenen Wert in ms zu
     * verzögern. Dabei wird nach jeder geprüften / berechneten Zahl das delay durchgeführt.
     * 
     * Sollte die Berechnung unterbrochen worden sein und wieder gestartet werden, so wird sie an
     * der Stelle fortgesetzt, an der sie unterbrochen wurde.
     * 
     * @pre delay ist größer gleich 0
     * @param delay gewünschte Verzögerung zwischen zwei Berechnungen (s.o.)
     */
    public void startWorker(long delay) {
        assert delay >= 0 : "Delay muss >= 0 sein!";

        //worker soll Arbeiten
        work = !work;


        workerThread = new Thread(() -> {
            try {
            //Solange er Arbeiten soll
                while (work) {
                    //Ist die nächste Zahl Prim
                    if (isPrime(primeCandidate)) {
                        primes.add(primeCandidate);
                        addEntry("found prime: " + primeCandidate);
                        //Alle wartenden aufwecken
                        //Dazu alle möglichen wartenden aufwecken
                        Long from = getPreviousPrime(primeCandidate);
                        //Falls zwei returned floor null
                        if (from != null) {
                            //Von der letzten Primzahl aus alle möglichen auf diese wartende
                            //(primeCandidate) aufwecken.
                            for (long toWake = 0; toWake <= primeCandidate; toWake++) {

                                //Wenn es wartenden gibt
                                if (waitMap.get(toWake) != null) {

                                    waitMap.get(toWake).setResult(primeCandidate);
                                }
                                waitMap.remove(toWake);
                            }
                        }


                    }
                    //Zahl inkrementieren
                    primeCandidate++;

                    //Thread um den Delay pausieren
                    Thread.sleep(delay);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        workerThread.start();
    }

    /**
     * Methode zum überprüfen ob eine Zahl eine Primzahl ist
     * anhand des Sieb des Eratosthenes
     *
     * @param q die zu überprüfende Zahl
     * @return true, wenn es eine Primzahl ist, sonst false
     */
    private boolean isPrime(long q) {

        //Die Zahl bis zu der das
        //Sieb angewendet wird
        Long sqrt = (long) Math.sqrt(q);

        //Iterator für die zu Prüfenden Primzahlen
        Iterator<Long> iPrimes = primes.iterator();

        //Der aktuelle Teiler
        Long primeDivident = iPrimes.next();

        //Jede Zahl n > 2 wird durch alle Primzahlne <= sqrt von q geteilt
        while (iPrimes.hasNext() && primeDivident <= sqrt) {

            //Überprüfen ob q durch diese Teilbar ist
            //wenn ja, dann keine Primzahl
            if (q % primeDivident == 0) {
                return false;
            }

            //Nächste Primzahl holen
            primeDivident = iPrimes.next();
        }
        return true;
    }

    /**
     * Ein Klasse das ein zukünftiges Ergebnis darstellt
     * ist das ergebnis nicht vorhanden wird solange gewartet
     * bis es vorliegt
     */
    private class FutureResult {

        /**
         * Das Ergebnis
         */
        private volatile Long result = null;

        /**
         * Methode die den eintretenden Thread solange warten lässt
         * bis das Ergebnis vorliegt
         */
        synchronized void waitForResult() {
            while (result == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.err.println("Es ist irgendwas beim warten"
                            + "schief gelaufen");
                    e.printStackTrace();
                }
            }
        }

        /**
         * Setter für das Ergebnis, liegt das Ergebnis vor
         * werden zu dem alle auf diese wartende aufgeweckt
         *
         * @param result das berechnete Ergebnis
         */
        synchronized void setResult(Long result) {
            this.result = result;
            //Alle wartenden aufwecken
            notifyAll();
        }

        /**
         * Getter für das Ergebnis
         *
         * @return das Ergebnis wenn es vorliegt, sonst null
         */
        Long getResult() {
            return result;
        }
    }

    private class PrimeFactorizationTask extends RecursiveTask<List<Long>> {

        /**
         * Die Primzahlen mit der die Zerlegung vorgenommen werden soll
         */
        private List<Long> primes = null;

        /**
         * Die von diesm Task genutzten Primzahlen zur zerlegung
         */
        private List<Long> primeFactors;

        /**
         * Die Zahl dessn Primfaktor-Zerlegung vorgenommen werden soll
         */
        private long q;

        /**
         * Konstruktor
         *
         * @param primes die Primzahlen die zur Primzahlen-Zerlegung benötigt werden
         * @param q die zu Zerlegende Zahl
         */
        PrimeFactorizationTask(List<Long> primes, long q) {
            this.primes = primes;
            this.q = q;
        }

        /**
         * Methode die die Primfaktor-Zerlegung einer Zahl vornimmt,
         * dabei wird die Berechnung parallelisiert indem sie
         * in verschiedene unabhängige Tasks zerlegt wird
         *
         * @return die Primzahlzerlegung der zu Zerlegenden Zahl
         *          in aufsteigender Reinfolge
         */
        @Override
        protected List<Long> compute() {

            //Wenn die size <= partitionSize ist dann
            //die Primzahlen zurück geben
            if (primes.size() <= partitionSize) {
                primeFactors = primes;
                return primeFactors();
            } else {
                this.primeFactors = primes.subList(0, partitionSize);
            }

            //Neuen Task erstellen
            List<Long> primes = this.primes.subList(partitionSize, this.primes.size());
            PrimeFactorizationTask primeFactorizationTask = new PrimeFactorizationTask(primes, q);

            //Ausführen
            primeFactorizationTask.fork();
            List<Long> primeFactors = primeFactors();
            primeFactors.addAll(primeFactorizationTask.join());

            return primeFactors;
        }

        List<Long> primeFactors() {
            //Die primeFactors
            List<Long> primeFactors = new LinkedList<>();
            //Iterator der Primzahlen
            Iterator<Long> primesIterator = this.primeFactors.iterator();
            //Der aktuelle Teiler
            Long primeFactor = null;
            //Auf teilbarkeit prüfen und
            //der Liste hinzufügen
            for (int pF = 0; pF < this.primeFactors.size();) {

                primeFactor = this.primeFactors.get(pF);

                if (q % primeFactor == 0) {
                    primeFactors.add(primeFactor);
                    q /= primeFactor;
                } else {
                    pF++;
                }
            }
            return primeFactors;
        }
    }

    /**
     * Beendet die Berechnung der Primzahlen. Die bereits berechneten Primzahlen werden dabei nicht
     * verworfen.
     */
    public void stopWorker() {
        //Den Worker Stoppen
        work = !work;
    }

    @Override
    public List<String> getLog() {
        return new LinkedList<>(log);
    }

    @Override
    public void addEntry(String e) {
        synchronized (log) {
            log.add(e);
        }
    }

}
