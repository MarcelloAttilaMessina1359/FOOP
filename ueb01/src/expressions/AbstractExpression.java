package expressions;
import values.Value;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstrakte Oberklasse zur Vermeidung von Codeverdoppelung.
 * 
 * @author kar, mhe, Marcello
 * @param <T> Der Typ des abstrakten Ausdrucks
 */
public abstract class AbstractExpression<T extends Value<T>> implements Expression<T> {

    @Override
    public String toString() {
        return toString(new StringBuilder()).toString();
    }


    @Override
    public boolean hasCycles() {

        Set<Expression<T>> visitedNodes = new HashSet<>();

        return hasCycles1(visitedNodes);
    }

    @Override
    public abstract boolean hasCycles1(Set<Expression<T>> visitedNodes);


    // Folgende Methoden nutzen toString(), sind daher ineffizient und dürfen somit nur in Tests
    // verwendet werden:
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AbstractExpression)) {
            return false;
        }
        AbstractExpression<?> other = (AbstractExpression<?>) obj;
        return this.toString().equals(other.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

}
