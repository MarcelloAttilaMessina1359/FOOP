package context;

/**
 * Diese Exception zeigt an, wenn ein Wert in einem {@link context.Context} nicht gefunden wurde.
 * 
 * @author kar, mhe
 */
public class ElementNotFoundException extends Exception {
    /**
     * Für einen glücklichen Compiler.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Standardfehlermeldung.
     */
    private static final String DEFAULT_MESSAGE = "Exception caused by missing value in context";

    /**
     * Exception mit Standardfehlermeldung.
     */
    public ElementNotFoundException() {
        super();
    }

    /**
     * Exception mit erweiterter Fehlermeldung.
     * 
     * @param message ist eine genauere Fehlerbeschreibung.
     */
    public ElementNotFoundException(String message) {
        super(DEFAULT_MESSAGE + ": " + message);
    }

}
