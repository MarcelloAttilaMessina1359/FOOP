package expressions;

import context.Context;
import context.ElementNotFoundException;
import expressions.exceptions.ContextIncompleteException;
import expressions.exceptions.DivByZeroException;
import values.Value;

import java.util.Set;


/**
 * Ein variabler Ausdruck.
 * 
 * @author kar, mhe, Marcello
 * @param <T> Der Typ des Variablen Ausdrucks
 */
public class VarExpression<T extends Value<T>> extends AbstractExpression<T> {
    /**
     * Name der Variablen.
     */
    private final String varName;

    /**
     * Konstruktor.
     * 
     * @param varName Name der Variablen
     */
    public VarExpression(String varName) {
        this.varName = varName;
    }

    @Override
    public T evaluate(Context<T> c) throws ContextIncompleteException, DivByZeroException {

        try {
            return c.getValue(varName);
        } catch (ElementNotFoundException e) {
            throw new ContextIncompleteException();
        }
    }

    @Override
    public boolean isConst() {
        return false;
    }

    @Override
    public StringBuilder toString(StringBuilder builder) {
        return builder.append(this.varName);
    }

    @Override
    public boolean hasCycles1(Set<Expression<T>> visitedNodes) {
        return false;
    }
}
