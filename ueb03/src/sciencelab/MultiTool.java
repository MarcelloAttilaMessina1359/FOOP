package sciencelab;

/**
 * Ein Multitool, mit dem Wissenschaftler an ihrer Erfindung arbeiten.
 * 
 * @author kar, mhe, Marcello
 * 
 */
public class MultiTool {

    /**
     * Der Aktuelle häter des Multitools
     */
    private Scientist holder = null;

    /**
     * Wird das Multitool gerade verwendet
     */
    private boolean isInUse = false;


    /**
     * Methode, die prüft ob das MultiTool frei ist, und falls nein,
     * so lange wartet, bis es frei ist und es dann reserviert (vergibt).
     * @param scientist der Wissenschaftler der das Multitool benutzen möchte
     */
    public synchronized void tryAcquiringMultitool(Scientist scientist) {
        //Falls in Benutzung, warten bis es Frei ist
        while (isInUse && !Thread.currentThread().isInterrupted()) {
            try {
                wait();
            } catch (InterruptedException e) {
                //Das Interrupted Flag wieder auf True setzen
                Thread.currentThread().interrupt();
            }
        }

        isInUse = true;
        holder = scientist;
    }

    /**
     * Methode, die prüft, ob das Multitool gerade frei ist
     * @return true falls es gerade benutzt wird, sonst false
     */
    public boolean isInUse() {
        return isInUse;
    }

    /**
     * Methode, die das Multitool freigibt (darf nur funktionieren, wenn das Multitool
     * gerade dem freigebenden Wissenschaftler gehört).
     * @param scientist der Wissenschaftler mit dem vorhaben das Multitool zurückzulegen
     */
    public synchronized void returnMultitool(Scientist scientist) {
        if (scientist == holder) {
            isInUse = false;
            holder = null;
            //Alle auf den Monitor warten Threads informieren
            notifyAll();
        }
    }
}
