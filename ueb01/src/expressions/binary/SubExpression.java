package expressions.binary;

import context.Context;
import expressions.Expression;
import expressions.exceptions.ContextIncompleteException;
import expressions.exceptions.DivByZeroException;
import values.Value;

/**
 * Ein Ausdruck für Subtraktion.
 * 
 * @author kar, mhe, Marcello
 * @param <T> Der Typ der Subtraktion
 */
public class SubExpression<T extends Value<T>> extends BinaryExpression<T> {

    /**
     * Konstruktor.
     * 
     * @param left linker Teilausdruck
     * @param right rechter Teilausdruck
     */
    public SubExpression(Expression<T> left, Expression<T> right) {
        super(left, right);
    }

    @Override
    public String getOp() {
        return "-";
    }

    @Override
    public T evaluate(Context<T> c) throws ContextIncompleteException,
            DivByZeroException {
        return left.evaluate(c).sub(right.evaluate(c));
    }
}
