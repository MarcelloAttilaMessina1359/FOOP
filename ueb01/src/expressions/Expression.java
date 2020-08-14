package expressions;

import context.Context;
import expressions.exceptions.ContextIncompleteException;
import expressions.exceptions.DivByZeroException;
import values.Value;

import java.util.Set;


/**
 * Ein mathematischer Ausdruck.
 * 
 * @author kar, mhe, Marcello
 * @param <T> Der Typ des Ausdruks
 */
public interface Expression<T extends Value<T>> {

    /**
     * Wertet den Ausdruck mit den Variablen aus dem übergebenen Kontext aus.
     * Kann eine Auswertung nicht vollständig erfolgen, so wird eine Exception
     * ausgelöst.
     * 
     * @param c Kontext. Falls der Kontext nicht benötigt wird, darf c leer oder
     *            null sein.
     * @return Ergebnis des Ausdrucks (ein mathematischer Wert).
     * @throws ContextIncompleteException Falls zur Auswertung benötigte
     *             Variablen nicht im Kontext enthalten sind
     * @throws DivByZeroException Falls im Rahmen der Auswertung eine Division
     *             durch Null erfolgen sollte
     */
    T evaluate(Context<T> c) throws ContextIncompleteException,
            DivByZeroException;

    /**
     * Testet ob der Ausdruck konstant ist, also ohne Variablenbelegung
     * ausgewertet werden kann.
     * 
     * 
     * @return true, wenn diese Expression ohne Variablenbelegung ausgewertet
     *         werden kann, sonst false.
     */
    boolean isConst();

    /**
     * Gibt zurück, ob der Ausdruck einen Zyklus enthält.
     * 
     * @return true, wenn der Ausdruck einen Zyklus enthält
     */
    boolean hasCycles();


    /**
     * Gibt zurück, ob der Ausdruck einen Zyklus enthält.
     *
     * @param visitedNodes Die bereits besichtigten Knoten des Baumes
     * @return true, wenn der Ausdruck einen Zyklus enthält
     */
    boolean hasCycles1(Set<Expression<T>> visitedNodes);


    /**
     * Erzeugt eine Stringdarstellung des Ausdrucks.
     * 
     * Operationen werden grundsätzlich geklammert dargestellt.
     * 
     * Bei binären Operationen ist zwischen den Operanden und dem
     * Operationssymbol jeweils ein einzelnes Leerzeichen.
     * 
     * Operationsymbole für die Operationen:
     * <ul>
     * <li>Addieren: +</li>
     * <li>Subtrahieren: -</li>
     * <li>Multiplizieren: *</li>
     * <li>Dividieren: /</li>
     * </ul>
     * 
     * Beispiele:
     * <ul>
     * <li><code>((5 + 3) - 1)</code></li>
     * <li><code>((foo + 4) * bar)</code></li>
     * <li><code>((20.0 - 30.2)) + (foo / bar))</code></li>
     * </ul>
     * 
     * @pre builder ist nicht null.
     * @param builder Builder an den er aktuelle Ausdruck angehängt werden soll.
     * @return Der übergebene Builder
     */
    StringBuilder toString(StringBuilder builder);

    /**
     * Gibt die Repräsentation des Ausdrucks als String zurück. Hier wird aus
     * Effizienzgründen die eigene toString-Methode mit dem StringBuilder
     * verwendet.
     * 
     * @return Stringrepräsentation
     */
    @Override
    String toString();

}
