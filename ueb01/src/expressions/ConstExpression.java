package expressions;

import context.Context;
import values.Value;

import java.util.Set;


/**
 * Ein konstanter Ausdruck (ein Literal).
 * 
 * @author kar, mhe, Marcello
 * @param <T> Der Typ des Konstanten Ausdruck
 */
public class ConstExpression<T extends Value<T>> extends AbstractExpression<T> {

    /**
     * Wert der Konstanten.
     */
    private final T value;

    /**
     * Konstruktor.
     * 
     * @param value Wert des Ausdrucks
     */
    public ConstExpression(T value) {
        this.value = value;
    }


    @Override
    public T evaluate(Context<T> c) {
        return value;
    }

    @Override
    public boolean isConst() {
        return true;
    }

    @Override
    public StringBuilder toString(StringBuilder builder) {
        return builder.append(this.value);
    }

    @Override
    public boolean hasCycles1(Set<Expression<T>> visitedNodes) {
        return false;
    }
}
