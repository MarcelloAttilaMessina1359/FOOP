import org.junit.Test;

import sciencelab.AlgorithmType;

/**
 * Dieser Test ruft lediglich Algorithmus A auf, damit man auf der Konsole
 * beobachten kann, ob und wie der Deadlock auftritt - er sollte in den meisten
 * Fällen nicht zum Ende kommen und darf daher auch nicht in die Testsuite
 * eingebunden werden!
 * 
 * @author kar, mhe
 * 
 */
public class DummyAlgoATest extends TestToolkit
{

  /**
   * A. 2 Wissenschaftler, 100 Durchläufe
   */
  @Test
  public void test1() throws InterruptedException
  {
    // keine Clock und kein assertTiming hier, da der Test einen Deadlock
    // verursachen soll

    final Thread[] startAll =
        m.startAll(2, 100, 50, 0, 0, AlgorithmType.A, null);
    for (Thread t : startAll) {
      t.join();
      System.out.println("Joined: " + t.getName());
    }

  }

}
