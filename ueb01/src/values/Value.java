package values;

import java.util.List;

/**
 * Abstrakte Klasse für mathematische Werte. Stellt Methoden zur Addition, Subtraktion und
 * Multiplikation zur Verfügung.
 * 
 * @author kar, mhe, Marcello
 * @param <T> Der Typ des mathematischen Wertes
 */
public abstract class Value<T> {

    /**
     * Gibt einen neuen Wert zurück, der durch die Addition des übergebenen Wertes zu dem aktuellen
     * Wert entsteht.
     * 
     * @param other Der Wert, der zu dem aktuellen Wert addiert werden soll
     * @return Das Ergebnis der Addition
     */
    public abstract T add(T other);

    /**
     * Gibt einen neuen Wert zurück, der durch die Multiplikation des aktuellen Wertes mit dem
     * übergebenen Wert entsteht.
     * 
     * @param other Der Wert, mit dem der aktuelle Wert multipliziert werden soll
     * @return Das Ergebnis der Multiplikation
     */
    public abstract T mul(T other);

    /**
     * Gibt einen neuen Wert zurück, der durch die Subtraktion des übergebenen Wertes vom aktuellen
     * Wert entsteht.
     * 
     * @param other Der Wert, der von dem aktuellen Wert subtrahiert werden soll
     * @return Das Ergebnis der Subtraktion
     */
    public abstract T sub(T other);

    /**
     * Hängt diesen Wert hinten an die übergebene Liste an.
     * 
     * Hinweis: Der Typ von l darf nicht weiter eingeschränkt werden als nötig.
     * 
     * @param l Die zu nutzende Liste
     */
    public void  appendToList(List<? super Value<T>> l) {
        l.add(this);
        /* ... */
    }

    /**
     * Erzeugt eine Stringdarstellung des Ausdrucks.
     * 
     * @pre builder ist nicht null
     * @param builder Builder, an den er aktuelle Wert angehängt werden soll
     * @return Der übergebene Builder
     */
    public abstract StringBuilder toString(StringBuilder builder);

    @Override
    public String toString() {
        return toString(new StringBuilder()).toString();
    }

}
