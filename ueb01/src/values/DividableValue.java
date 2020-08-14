package values;

import expressions.exceptions.DivByZeroException;

/**
 * Schnittstelle für teilbare Werte.
 * 
 * @author kar, mhe, Marcello
 * @param <T> Der Typ des teilbaren Wertes
 */
public interface DividableValue<T> {
    /**
     * Gibt einen neuen Wert zurück, der durch die Division des aktuellen Wertes durch den
     * übergebenen Wert entsteht.
     * 
     * @param other Der Wert, durch den der aktuelle Wert dividiert werden soll
     * @return Das Ergebnis der Division ,
     * @throws DivByZeroException wenn durch null geteilt werden soll
     */
    T div(T other) throws DivByZeroException;
}
