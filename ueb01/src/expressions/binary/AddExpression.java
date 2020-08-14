package expressions.binary;

import context.Context;
import expressions.Expression;
import expressions.exceptions.ContextIncompleteException;
import expressions.exceptions.DivByZeroException;
import values.Value;


/**
 * Ein Ausdruck für Addition.
 * 
 * @author kar, mhe, Marcello
 * @param <T> Der Typ der Addition
 */
public class AddExpression<T extends Value<T>> extends BinaryExpression<T> {

    /**
     * Konstruktor.
     * 
     * @param left linker Teilausdruck
     * @param right rechter Teilausdruck
     */
    public AddExpression(Expression<T> left, Expression<T> right) {
        super(left, right);
    }

    @Override
    public String getOp() {
        return "+";
    }

    @Override
    public T evaluate(Context<T> c) throws ContextIncompleteException,
            DivByZeroException {
        return left.evaluate(c).add(right.evaluate(c));
    }


}
