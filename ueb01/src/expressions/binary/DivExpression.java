package expressions.binary;

import context.Context;
import expressions.Expression;
import expressions.exceptions.ContextIncompleteException;
import expressions.exceptions.DivByZeroException;
import values.DividableValue;
import values.Value;

/**
 * Ein Ausdruck für Division.
 * 
 * @author kar, mhe, Marcello
 * @param <T> der Typ der Division
 */
public class DivExpression<T extends Value<T> & DividableValue<T>> extends BinaryExpression<T> {

    /**
     * Konstruktor.
     * 
     * @param left linker Teilausdruck
     * @param right rechter Teilausdruck
     */
    public DivExpression(Expression<T> left, Expression<T> right) {
        super(left, right);
    }

    @Override
    public String getOp() {
        return "/";
    }

    @Override
    public T evaluate(Context<T> c) throws ContextIncompleteException,
            DivByZeroException {
        return left.evaluate(c).div(right.evaluate(c));
    }
}
