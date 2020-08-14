package context;







import values.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * Enthält eine Menge von Variablenbelegungen und Zugriffsmethoden darauf.
 * 
 * Eine Variablenbelegung setzt sich zusammen aus einem Namen und einem mathematischen Wert.
 * 
 * @author kar, mhe, Marcello
 * @param <T> Der Typ des Kontext muss ein mathematisher Wert sein
 */
public class Context<T extends Value<T>> {

    /**
     * Die Menge von Variablenbelegung
     */
    private Map<String, T> contextSet = new HashMap<>();

    /**
     * Setzt ein neues Name-Wert-Paar im Kontext. Ein Name-Wert Paar, dessen Name bereits vorhanden
     * ist, wird überschrieben. Der Kontext selbst wird zudem als Rückgabewert gesetzt.
     * 
     * @param name Name
     * @param value mathematischer Wert
     * @return this
     */
    public Context<T> setValue(String name, T value) {
        contextSet.put(name, value);
        return this;
    }

    /**
     * Liefert den mathematischen Wert der Variablen mit dem übergebenen Namen. Sollte es kein
     * Element mit dem Namen geben, so wird eine ElementNotFoundException ausgelöst.
     * 
     * @param name Name der gesuchten Variable
     * @return mathematischer Wert der gesuchten Variable
     * @throws ElementNotFoundException Der Name ist nicht im Kontext vorhanden
     */
    public T getValue(String name) throws ElementNotFoundException {

        T value = contextSet.get(name);

        if (value == null) {
            throw new ElementNotFoundException("Der Name ist nicht im Kontext vorhanden");
        } else {
            return value;
        }
    }

    /**
     * Prüft ob der Name im Kontext vorhanden ist.
     * 
     * @param name zu prüfender Name
     * @return true, wenn der Name vorhanden ist, sonst false
     */
    public boolean has(String name) {
        return contextSet.get(name) != null;
    }

}
