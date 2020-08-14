package expressions.exceptions;

/**
 * Eine Exception, die beim Auswerten einer {@link expressions.Expression} auftreten kann, wenn
 * durch Null geteilt werden soll.
 * 
 * @author kar, mhe
 */
public class DivByZeroException extends Exception {
    /**
     * Für einen glücklichen Compiler.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Standardfehlermeldung.
     */
    private static final String DEFAULT_MESSAGE = "Exception caused by division by zero";

    /**
     * Exception mit Standardfehlermeldung.
     */
    public DivByZeroException() {
        super(DEFAULT_MESSAGE + ".");
    }

    /**
     * Exeption mit erweiterter Fehlermeldung.
     * 
     * @param message ist eine genauere Fehlerbeschreibung.
     */
    public DivByZeroException(String message) {
        super(DEFAULT_MESSAGE + ": " + message);
    }

}
