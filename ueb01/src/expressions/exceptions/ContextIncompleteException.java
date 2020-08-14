package expressions.exceptions;

/**
 * Diese Exception zeigt an, wenn ein {@link context.Context} unvollständig ist.
 * 
 * @author kar, mhe
 */
public class ContextIncompleteException extends Exception {
    /**
     * Für einen glücklichen Compiler.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Standardfehlermeldung.
     */
    private static final String DEFAULT_MESSAGE = "Exception caused by an imcomplete context";

    /**
     * Exception mit Standardfehlermeldung.
     */
    public ContextIncompleteException() {
        super(DEFAULT_MESSAGE + ".");
    }

    /**
     * Exeption mit erweiterter Fehlermeldung.
     * 
     * @param message ist eine genauere Fehlerbeschreibung.
     */
    public ContextIncompleteException(String message) {
        super(DEFAULT_MESSAGE + ": " + message);
    }

}
