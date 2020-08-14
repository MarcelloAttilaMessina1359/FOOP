package expressions.binary;

import expressions.AbstractExpression;
import expressions.Expression;
import expressions.ExpressionWrapper;
import values.Value;

import java.util.Set;

/**
 * Abstrakte Klasse für mathematische Ausdrücke mit zwei Teilausdrücken.
 * 
 * @author kar, mhe, Marcello
 * @param <T> der Typ des Ausdrucks
 */
public abstract class BinaryExpression<T extends Value<T>> extends AbstractExpression<T> {

    /**
     * Linker Teilausdruck.
     */
    protected Expression<T> left;

    /**
     * Rechter Teilausdruck.
     */
    protected Expression<T> right;

    /**
     * Konstruktor.
     * 
     * @param left linker Teilausdruck
     * @param right rechter Teilausdruck
     */
    public BinaryExpression(Expression<T> left, Expression<T> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean isConst() {

        return left.isConst() & right.isConst();
    }

    @Override
    public StringBuilder toString(StringBuilder builder) {
        return builder.append("(").append(left.toString(new StringBuilder())).append(" ")
                .append(getOp()).append(" ").append(right.toString(new StringBuilder()))
                .append(")");
    }

    @Override
    public boolean hasCycles1(Set<Expression<T>> visitedNodes) {

        visitedNodes.add(new ExpressionWrapper<>(this));

        //Zeigt ein Knoten auf sich selber
        // oder über andere auf sich selber (kam er schonmal vor)
        // so hat er ein Zirkelschluss
        if (visitedNodes.contains(new ExpressionWrapper<>(left))
                | visitedNodes
                .contains(new ExpressionWrapper<>(right))) {
            return true;
        }
        return left.hasCycles1(visitedNodes)
                | right.hasCycles1(visitedNodes);

    }
    /**
     * Methode die den Operator einer BinärenOperation wiedergibt
     *
     * @return  der Operator der Binärenoperation als String
     */
    public abstract String getOp();

    /**
     * Gibt den linken Teilausdruck zurück.
     * 
     * @return left Der linke Teilausdruck
     */
    public Expression<T> getLeftExpression() {
        return this.left;
    }

    /**
     * Gibt den rechten Teilausdruck zurück.
     * 
     * @return left Der rechte Teilausdruck
     */
    public Expression<T> getRightExpression() {
        return this.right;
    }

    /**
     * Setzt den linken Teilausdruck.
     * 
     * @param left Der zu setzene linke Teilausdruck.
     */
    public void setLeftExpression(Expression<T> left) {
        this.left = left;
    }

    /**
     * Setzt den rechten Teilausdruck.
     * 
     * @param right Der zu setzene rechte Teilausdruck.
     */
    public void setRightExpression(Expression<T> right) {
        this.right = right;
    }


}
