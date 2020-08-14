import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import server.PrimeManager;

public class BeispieltestPrimeManager {

    @Test
    public void testBasicNextPrime() throws InterruptedException {

        final PrimeManager g = new PrimeManager(100);

        // Berechnung starten 
        g.startWorker(10);
        Thread.sleep(100); // Wir wollen nur bereits sicher schon berechnete Zahlen testen

        // Hinweis: sequentiell
        Assert.assertEquals(2, g.nextPrime(0));
        Thread.sleep(10); // Berechnungen sollen weitergehen
        Assert.assertEquals(2, g.nextPrime(2));
        Thread.sleep(10);
        Assert.assertEquals(2, g.nextPrime(1));
        Thread.sleep(10);

        g.stopWorker();

        List<String> glog = g.getLog();

        List<String> entries_in_order = new LinkedList<>(
                Arrays.asList("requested: nextprime,0", "response: nextprime,0,2"
                , "requested: nextprime,2","response: nextprime,2,2"
                , "requested: nextprime,1","response: nextprime,1,2"
                ));


        // mindestens die 2 sollte er schon gefunden haben
        Assert.assertTrue(glog.contains("found prime: 2"));

        // nur die interessanten Logeinträge herausfiltern
        List<String> glog_stripped = new LinkedList<>();        
        for (String s : glog) {
            if ( s.contains("requested") || s.contains("response") ) {
                glog_stripped.add(s);
            }
        }
        // Listen auf gleiche Einträge prüfen
        Assert.assertEquals(glog_stripped, entries_in_order);
    }

    @Test
    public void testNoNextPrimeYet() throws InterruptedException {

        final PrimeManager g = new PrimeManager(100);

        //Vor dem Starten bereits nachfragen
        final Thread c1 = new Thread(new Runnable() {
            public void run() {
                g.nextPrime(272); // Eine Assertion käme da nie raus, Ergebnis in dem Fall wird über das Log getestet
            }
        });
        c1.start();

        // Berechnung starten
        g.startWorker(10);

        //Aufergebnis warten
        c1.join();

        // Berechnung kann sicher gestoppt werden
        g.stopWorker();

        //Der log
        List<String> glog = g.getLog();

        // Interessante Einträge stichprobenhaft überprüfen
        Assert.assertTrue(glog.contains("response: nextprime,272,277"));

    }

    @Test
    public void testNoNextPrimeYet2WaitingForSame() throws InterruptedException {

        final PrimeManager g = new PrimeManager(100);

        //Vor dem Starten bereits nachfragen
        final Thread c1 = new Thread(new Runnable() {
            public void run() {
                g.nextPrime(272); // Eine Assertion käme da nie raus, Ergebnis in dem Fall wird über das Log getestet
            }
        });
        c1.start();

        final Thread c2 = new Thread(new Runnable() {
            public void run() {
                g.nextPrime(272);
            }
        });
        c2.start();

        // Berechnung starten
        g.startWorker(10);

        //Aufergebnis warten
        c1.join();
        c2.join();

        // Berechnung kann sicher gestoppt werden
        g.stopWorker();

        //Der log
        List<String> glog = g.getLog();

        // Interessante Einträge stichprobenhaft überprüfen
        Assert.assertTrue(glog.contains("response: nextprime,272,277"));
        //System.out.println("der Log" + glog);
    }
    private class PrimeFactorRunnable implements Runnable {

        private long q = 0;
        private PrimeManager g = null;
        private List<Long> primeFactors = null;

        PrimeFactorRunnable(long q, PrimeManager g) {
            this.q = q;
            this.g = g;
        }


        @Override
        public void run() {
            primeFactors = g.primeFactors(q);
        }

        List<Long> getPrimeFactors() {
            return primeFactors;
        }
    }

    @Test
    public void testPrimeFactorization() throws InterruptedException {

        final PrimeManager g = new PrimeManager(1);

        // Berechnung starten
        g.startWorker(10);
        PrimeFactorRunnable first = new PrimeFactorRunnable(14, g);
        PrimeFactorRunnable second = new PrimeFactorRunnable(35, g);

        final Thread c1 = new Thread(first);
        c1.start();

        final Thread c2 = new Thread(second);
        c2.start();

        // Warten bis beide Threads zurückgekehrt sind
        c1.join();
        c2.join();

        // Berechnung kann sicher gestoppt werden
        g.stopWorker();

        List<String> glog = g.getLog();
        //Die Ergebnisse
        Assert.assertTrue(glog.contains("requested: primefactors,14"));
        Assert.assertTrue(glog.contains("response: primefactors,14,[2,7]"));
        Assert.assertTrue(glog.contains("requested: primefactors,35"));
        Assert.assertTrue(glog.contains("response: primefactors,35,[5,7]"));
    }

    @Test
    public void testPrimeFactorsRequestWaiting() throws InterruptedException {
        final PrimeManager g = new PrimeManager(1);

        PrimeFactorRunnable first = new PrimeFactorRunnable(14, g);
        PrimeFactorRunnable second = new PrimeFactorRunnable(35, g);

        final Thread c1 = new Thread(first);
        c1.start();

        final Thread c2 = new Thread(second);
        c2.start();

        // Berechnung starten
        g.startWorker(10);

        // Warten bis beide Threads zurückgekehrt sind
        c1.join();
        c2.join();

        // Berechnung kann sicher gestoppt werden
        g.stopWorker();

        List<String> glog = g.getLog();
        //Die Ergebnisse
        Assert.assertTrue(glog.contains("requested: primefactors,14"));
        Assert.assertTrue(glog.contains("response: primefactors,14,[2,7]"));
        Assert.assertTrue(glog.contains("requested: primefactors,35"));
        Assert.assertTrue(glog.contains("response: primefactors,35,[5,7]"));
    }

    @Test
    public void testPrimeFactorsRequestWaiting1() throws InterruptedException {
        final PrimeManager g = new PrimeManager(100);

        PrimeFactorRunnable first = new PrimeFactorRunnable(2, g);
        PrimeFactorRunnable second = new PrimeFactorRunnable(11, g);
        PrimeFactorRunnable third = new PrimeFactorRunnable(146, g);

        final Thread c1 = new Thread(first);
        c1.start();

        final Thread c2 = new Thread(second);
        c2.start();

        final Thread c3 = new Thread(third);
        c3.start();

        // Berechnung starten
        g.startWorker(10);

        // Warten bis beide Threads zurückgekehrt sind
        c1.join();
        c2.join();
        c3.join();

        // Berechnung kann sicher gestoppt werden
        g.stopWorker();

        List<String> glog = g.getLog();
        //Die Ergebnisse
        Assert.assertTrue(glog.contains("requested: primefactors,2"));
        Assert.assertTrue(glog.contains("response: primefactors,2,[2]"));
        Assert.assertTrue(glog.contains("requested: primefactors,11"));
        Assert.assertTrue(glog.contains("response: primefactors,11,[11]"));
        Assert.assertTrue(glog.contains("requested: primefactors,146"));
        Assert.assertTrue(glog.contains("response: primefactors,146,[2,73]"));
    }

    @Test
    public void testPrimeFactorsRequestWaitingUnevenPrimeFactors() throws InterruptedException {
        final PrimeManager g = new PrimeManager(2);

        PrimeFactorRunnable first = new PrimeFactorRunnable(68, g);
        PrimeFactorRunnable second = new PrimeFactorRunnable(35, g);

        final Thread c1 = new Thread(first);
        c1.start();

        final Thread c2 = new Thread(second);
        c2.start();

        // Berechnung starten
        g.startWorker(10);

        // Warten bis beide Threads zurückgekehrt sind
        c1.join();
        c2.join();

        // Berechnung kann sicher gestoppt werden
        g.stopWorker();

        List<String> glog = g.getLog();
        //System.out.println(glog);
        //Die Ergebnisse
        Assert.assertTrue(glog.contains("requested: primefactors,68"));
        Assert.assertTrue(glog.contains("response: primefactors,68,[2,2,17]"));
        Assert.assertTrue(glog.contains("requested: primefactors,35"));
        Assert.assertTrue(glog.contains("response: primefactors,35,[5,7]"));
    }

    @Test
    public void testPrimeFactorsRequestWaitingUnevenPrimeFactors2() throws InterruptedException {
        final PrimeManager g = new PrimeManager(3);

        PrimeFactorRunnable first = new PrimeFactorRunnable(40, g);
        PrimeFactorRunnable second = new PrimeFactorRunnable(35, g);

        final Thread c1 = new Thread(first);
        c1.start();

        final Thread c2 = new Thread(second);
        c2.start();

        // Berechnung starten
        g.startWorker(10);

        // Warten bis beide Threads zurückgekehrt sind
        c1.join();
        c2.join();

        // Berechnung kann sicher gestoppt werden
        g.stopWorker();

        List<String> glog = g.getLog();
        //System.out.println(glog);
        //Die Ergebnisse
        Assert.assertTrue(glog.contains("requested: primefactors,40"));
        Assert.assertTrue(glog.contains("response: primefactors,40,[2,2,2,5]"));
        Assert.assertTrue(glog.contains("requested: primefactors,35"));
        Assert.assertTrue(glog.contains("response: primefactors,35,[5,7]"));
    }

    @Test
    public void testInterlockedNextPrime() throws InterruptedException {
        try {
            final PrimeManager g = new PrimeManager(100);

            // Berechnung starten
            g.startWorker(10);
            Thread.sleep(200); // Wir wollen nur bereits sicher schon berechnete Zahlen testen 

            final Thread c1 = new Thread(new Runnable() {
                public void run() {
                    g.nextPrime(11); // Eine Assertion käme da nie raus, Ergebnis in dem Fall wird über das Log getestet
                }
            });
            c1.start();

            final Thread c2 = new Thread(new Runnable() {
                public void run() {
                    g.nextPrime(4);
                }
            });
            c2.start();

            // Warten bis beide Threads zurückgekehrt sind
            c1.join();
            c2.join();

            // Berechnung kann sicher gestoppt werden
            g.stopWorker();

            List<String> glog = g.getLog();
            // nur die ersten fünf interessanten Logeinträge herausfiltern 
            List<String> glog_stripped = new LinkedList<>();
            int i = 0;
            for (String s : glog) {
                if ( s.contains("found prime") ) {
                    glog_stripped.add(s);
                    i++;
                }
                if (i == 5) {
                    break;
                }
            }

            // Listen auf gleiche Einträge prüfen, mindestens die notwendigen Primzahlen gefunden?
            List<String> primes = new LinkedList<>(Arrays.asList(
                    "found prime: 2", "found prime: 3", "found prime: 5", "found prime: 7", "found prime: 11"
            ));


            Assert.assertEquals(glog_stripped, primes);

            // Interessante Einträge stichprobenhaft überprüfen
            Assert.assertTrue(glog.contains("response: nextprime,4,5"));
            Assert.assertTrue(glog.contains("response: nextprime,11,11"));
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
            e.getLocalizedMessage();
        }
        
      }
    @Test
    public void testStartWorkerAfterRequests() throws InterruptedException {
        final PrimeManager g = new PrimeManager(100);

        //Vor dem Starten bereits nachfragen
        final Thread c1 = new Thread(new Runnable() {
            public void run() {
                g.nextPrime(272); // Eine Assertion käme da nie raus, Ergebnis in dem Fall wird über das Log getestet
            }
        });
        c1.start();

        final Thread c2 = new Thread(new Runnable() {
            public void run() {
                g.nextPrime(272);
            }
        });
        c2.start();

        final Thread c3 = new Thread(new Runnable() {
            public void run() {
                g.nextPrime(8);
            }
        });
        c3.start();

        final Thread c4 = new Thread(new Runnable() {
            public void run() {
                g.nextPrime(12);
            }
        });
        c4.start();

        final Thread c5 = new Thread(new Runnable() {
            public void run() {
                g.nextPrime(25);
            }
        });
        c5.start();

        final Thread c6 = new Thread(new Runnable() {
            public void run() {
                g.nextPrime(198);
            }
        });
        c6.start();

        // Berechnung starten
        g.startWorker(10);

        //Aufergebnis warten
        c1.join();
        c2.join();
        c3.join();
        c4.join();
        c5.join();
        c6.join();

        // Berechnung kann sicher gestoppt werden
        g.stopWorker();

        //Der log
        List<String> glog = g.getLog();

        // Interessante Einträge stichprobenhaft überprüfen
        Assert.assertTrue(glog.contains("response: nextprime,272,277"));
        Assert.assertTrue(glog.contains("response: nextprime,8,11"));
        Assert.assertTrue(glog.contains("response: nextprime,12,13"));
        Assert.assertTrue(glog.contains("response: nextprime,25,29"));
        Assert.assertTrue(glog.contains("response: nextprime,198,199"));
    }

    @Test
    public void testStartWorkerAfterRequestsOnlyPrimes() throws InterruptedException {
        try {
            final PrimeManager g = new PrimeManager(100);

            //Vor dem Starten bereits nachfragen
            final Thread c1 = new Thread(new Runnable() {
                public void run() {
                    g.nextPrime(2); // Eine Assertion käme da nie raus, Ergebnis in dem Fall wird über das Log getestet
                }
            });
            c1.start();

            final Thread c2 = new Thread(new Runnable() {
                public void run() {
                    g.nextPrime(2);
                }
            });
            c2.start();

            final Thread c3 = new Thread(new Runnable() {
                public void run() {
                    g.nextPrime(2);
                }
            });
            c3.start();

            final Thread c4 = new Thread(new Runnable() {
                public void run() {
                    g.nextPrime(2);
                }
            });
            c4.start();

            final Thread c5 = new Thread(new Runnable() {
                public void run() {
                    g.nextPrime(2);
                }
            });
            c5.start();

            final Thread c6 = new Thread(new Runnable() {
                public void run() {
                    g.nextPrime(2);
                }
            });
            c6.start();

            // Berechnung starten
            g.startWorker(10);

            //Aufergebnis warten
            c1.join();
            c2.join();
            c3.join();
            c4.join();
            c5.join();
            c6.join();

            // Berechnung kann sicher gestoppt werden
            g.stopWorker();


            //Der log
            List<String> glog = g.getLog();
            System.err.println("der log " + glog);
            int count = 0;
            for (String s: glog) {
                if (s.contains("response: nextprime,2,2")) {
                    count++;
                }
            }
            Assert.assertEquals(6, count);
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
            e.getLocalizedMessage();
        }

    }

    @Test
    public void testStartWorkerAfterRequestsOnlyPrimes2() throws InterruptedException {

        final PrimeManager g = new PrimeManager(100);

        //Vor dem Starten bereits nachfragen
        final Thread c1 = new Thread(new Runnable() {
            public void run() {
                g.nextPrime(101); // Eine Assertion käme da nie raus, Ergebnis in dem Fall wird über das Log getestet
            }
        });
        c1.start();

        final Thread c2 = new Thread(new Runnable() {
            public void run() {
                g.nextPrime(431);
            }
        });
        c2.start();

        final Thread c3 = new Thread(new Runnable() {
            public void run() {
                g.nextPrime(331);
            }
        });
        c3.start();

        final Thread c4 = new Thread(new Runnable() {
            public void run() {
                g.nextPrime(541);
            }
        });
        c4.start();

        final Thread c5 = new Thread(new Runnable() {
            public void run() {
                g.nextPrime(283);
            }
        });
        c5.start();

        final Thread c6 = new Thread(new Runnable() {
            public void run() {
                g.nextPrime(229);
            }
        });
        c6.start();

        // Berechnung starten
        g.startWorker(10);

        //Aufergebnis warten
        c1.join();
        c2.join();
        c3.join();
        c4.join();
        c5.join();
        c6.join();

        // Berechnung kann sicher gestoppt werden
        g.stopWorker();

        //Der log
        List<String> glog = g.getLog();

        // Interessante Einträge stichprobenhaft überprüfen
        Assert.assertTrue(glog.contains("response: nextprime,229,229"));
        Assert.assertTrue(glog.contains("response: nextprime,431,431"));
        Assert.assertTrue(glog.contains("response: nextprime,283,283"));
        Assert.assertTrue(glog.contains("response: nextprime,541,541"));
        Assert.assertTrue(glog.contains("response: nextprime,331,331"));
        Assert.assertTrue(glog.contains("response: nextprime,101,101"));
    }
    @Test
    public void testStartWorkerAfterRequestsPrimesAndFactors() throws InterruptedException {

        final PrimeManager g = new PrimeManager(100);

        //Vor dem Starten bereits nachfragen
        final Thread c1 = new Thread(new Runnable() {
            public void run() {
                g.nextPrime(340); // Eine Assertion käme da nie raus, Ergebnis in dem Fall wird über das Log getestet
            }
        });
        c1.start();

        final Thread c2 = new Thread(new Runnable() {
            public void run() {
                g.primeFactors(120);
            }
        });
        c2.start();

        final Thread c3 = new Thread(new Runnable() {
            public void run() {
                g.nextPrime(813);
            }
        });
        c3.start();

        final Thread c4 = new Thread(new Runnable() {
            public void run() {
                g.primeFactors(180);
            }
        });
        c4.start();

        final Thread c5 = new Thread(new Runnable() {
            public void run() {
                g.nextPrime(450);
            }
        });
        c5.start();

        final Thread c6 = new Thread(new Runnable() {
            public void run() {
                g.primeFactors(200);
            }
        });
        c6.start();

        // Berechnung starten
        g.startWorker(10);

        //Aufergebnis warten
        c1.join();
        c2.join();
        c3.join();
        c4.join();
        c5.join();
        c6.join();

        // Berechnung kann sicher gestoppt werden
        g.stopWorker();

        //Der log
        List<String> glog = g.getLog();

        // Interessante Einträge stichprobenhaft überprüfen
        Assert.assertTrue(glog.contains("response: nextprime,340,347"));
        Assert.assertTrue(glog.contains("response: primefactors,120,[2,2,2,3,5]"));
        Assert.assertTrue(glog.contains("response: nextprime,813,821"));
        Assert.assertTrue(glog.contains("response: primefactors,180,[2,2,3,3,5]"));
        Assert.assertTrue(glog.contains("response: nextprime,450,457"));
        Assert.assertTrue(glog.contains("response: primefactors,200,[2,2,2,5,5]"));
    }

    @Test
    public void testStartWorkerBeforeRequestsPrimesAndFactors() throws InterruptedException {

        final PrimeManager g = new PrimeManager(100);

        // Berechnung starten
        g.startWorker(10);

        //Vor dem Starten bereits nachfragen
        final Thread c1 = new Thread(new Runnable() {
            public void run() {
                g.nextPrime(340); // Eine Assertion käme da nie raus, Ergebnis in dem Fall wird über das Log getestet
            }
        });
        c1.start();

        final Thread c2 = new Thread(new Runnable() {
            public void run() {
                g.primeFactors(120);
            }
        });
        c2.start();

        final Thread c3 = new Thread(new Runnable() {
            public void run() {
                g.nextPrime(813);
            }
        });
        c3.start();

        final Thread c4 = new Thread(new Runnable() {
            public void run() {
                g.primeFactors(180);
            }
        });
        c4.start();

        final Thread c5 = new Thread(new Runnable() {
            public void run() {
                g.nextPrime(450);
            }
        });
        c5.start();

        final Thread c6 = new Thread(new Runnable() {
            public void run() {
                g.primeFactors(200);
            }
        });
        c6.start();



        //Aufergebnis warten
        c1.join();
        c2.join();
        c3.join();
        c4.join();
        c5.join();
        c6.join();

        // Berechnung kann sicher gestoppt werden
        g.stopWorker();

        //Der log
        List<String> glog = g.getLog();

        // Interessante Einträge stichprobenhaft überprüfen
        Assert.assertTrue(glog.contains("response: nextprime,340,347"));
        Assert.assertTrue(glog.contains("response: primefactors,120,[2,2,2,3,5]"));
        Assert.assertTrue(glog.contains("response: nextprime,813,821"));
        Assert.assertTrue(glog.contains("response: primefactors,180,[2,2,3,3,5]"));
        Assert.assertTrue(glog.contains("response: nextprime,450,457"));
        Assert.assertTrue(glog.contains("response: primefactors,200,[2,2,2,5,5]"));
    }

    @Test
    public void testStartWorkerMidwayRequestsPrimesAndFactors() throws InterruptedException {

        final PrimeManager g = new PrimeManager(100);



        //Vor dem Starten bereits nachfragen
        final Thread c1 = new Thread(new Runnable() {
            public void run() {
                g.nextPrime(340); // Eine Assertion käme da nie raus, Ergebnis in dem Fall wird über das Log getestet
            }
        });
        c1.start();

        final Thread c2 = new Thread(new Runnable() {
            public void run() {
                g.primeFactors(120);
            }
        });
        c2.start();

        final Thread c3 = new Thread(new Runnable() {
            public void run() {
                g.nextPrime(813);
            }
        });
        c3.start();

        // Berechnung starten
        g.startWorker(10);


        final Thread c4 = new Thread(new Runnable() {
            public void run() {
                g.primeFactors(180);
            }
        });
        c4.start();

        final Thread c5 = new Thread(new Runnable() {
            public void run() {
                g.nextPrime(450);
            }
        });
        c5.start();

        final Thread c6 = new Thread(new Runnable() {
            public void run() {
                g.primeFactors(200);
            }
        });
        c6.start();


        //Aufergebnis warten
        c1.join();
        c2.join();
        c3.join();
        c4.join();
        c5.join();
        c6.join();

        // Berechnung kann sicher gestoppt werden
        g.stopWorker();

        //Der log
        List<String> glog = g.getLog();

        // Interessante Einträge stichprobenhaft überprüfen
        Assert.assertTrue(glog.contains("response: nextprime,340,347"));
        Assert.assertTrue(glog.contains("response: primefactors,120,[2,2,2,3,5]"));
        Assert.assertTrue(glog.contains("response: nextprime,813,821"));
        Assert.assertTrue(glog.contains("response: primefactors,180,[2,2,3,3,5]"));
        Assert.assertTrue(glog.contains("response: nextprime,450,457"));
        Assert.assertTrue(glog.contains("response: primefactors,200,[2,2,2,5,5]"));
    }

}
