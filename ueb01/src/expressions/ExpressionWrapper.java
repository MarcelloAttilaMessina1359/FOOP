package expressions;

import context.Context;
import expressions.exceptions.ContextIncompleteException;
import expressions.exceptions.DivByZeroException;
import values.Value;

import java.util.Set;

/**
 * Wrapper für Ausdrücke, dessen equals auf Referenzgleichheit testet. Wird genutzt, um einen
 * Ausdrucksbaum auf Zyklenfreiheit zu überprüfen.
 * 
 * @author kar, mhe, Marcello
 * @param <T> Der Typ des Ausdrucks
 */
public class ExpressionWrapper<T extends Value<T>> extends AbstractExpression<T> {

    /**
     * Der eingewickelte Ausdruck.
     */
    protected final Expression<T> subExpression;

    /**
     * Konstruktor.
     * 
     * @param subExpression Der einzuwickelnde Ausdruck
     */
    public ExpressionWrapper(Expression<T> subExpression) {
        this.subExpression = subExpression;
    }

    @Override
    public T evaluate(Context<T> c) throws ContextIncompleteException,
            DivByZeroException {
        return subExpression.evaluate(c);
    }

    @Override
    public boolean isConst() {
        return subExpression.isConst();
    }

    @Override
    public StringBuilder toString(StringBuilder builder) {
        return this.subExpression.toString(builder);
    }


    @Override
    public boolean hasCycles1(Set<Expression<T>> visitedNodes) {
        return subExpression.hasCycles1(visitedNodes);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ExpressionWrapper) {
            return this.subExpression == ((ExpressionWrapper<?>) other).subExpression;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this.subExpression);
    }

}
