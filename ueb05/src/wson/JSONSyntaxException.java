package wson;

/**
 * Syntax-Fehler beim Parsen der JSON-Daten
 * 
 * Bei dieser Fehlermeldung sollte die Defaultnachricht beim Auslösen der Exception um eine
 * sinnvolle Fehlerbeschreibung erweitert werden. (Dies ist zudem sehr hilfreich beim Debuggen).
 * 
 * @author kar, mhe
 * 
 */
public class JSONSyntaxException extends Exception {
    
    private static final long serialVersionUID = 1L;

    private static final String DEFAULTMESSAGE = "Exception caused by JSON-Syntax-Error";

    /**
     * Exception mit Standardfehlermeldung.
     */
    public JSONSyntaxException() {
        this(DEFAULTMESSAGE);
    }

    /**
     * Exeption mit individueller Fehlermeldung.
     * 
     * @param message Message
     */
    public JSONSyntaxException(String message) {
        super(DEFAULTMESSAGE + ": " + message);
    }

}
