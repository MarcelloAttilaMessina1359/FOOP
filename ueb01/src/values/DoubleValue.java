package values;

import expressions.exceptions.DivByZeroException;

/**
 * Ein Wert für Gleitkommazahlen (double).
 * 
 * @author kar, mhe, Marcello
 */
public class DoubleValue extends Value<DoubleValue> implements DividableValue<DoubleValue> {
    /**
     * Wert der Gleitkommazahl.
     */
    private final double value;

    /**
     * Konstruktor.
     * 
     * @param value Der Wert der Gleitkommazahl
     */
    public DoubleValue(double value) {
        this.value = value;
    }

    /**
     * Gibt den Wert der Gleitkommazahl zurück.
     * 
     * @return Der Wert der Gleitkommazahl
     */
    public double getValue() {
        return value;
    }

    @Override
    public DoubleValue add(DoubleValue other) {
        return new DoubleValue(this.value + other.value);
    }

    @Override
    public DoubleValue mul(DoubleValue other) {
        return new DoubleValue(this.value * other.value);
    }

    @Override
    public DoubleValue sub(DoubleValue other) {
        return new DoubleValue(this.value - other.value);
    }

    @Override
    public DoubleValue div(DoubleValue other) throws DivByZeroException {

        if (other.value == .0) {
            throw new DivByZeroException();
        } else {
            return new DoubleValue(this.value / other.value);
        }
    }

    @Override
    public StringBuilder toString(StringBuilder builder) {
        return builder.append(this.value);
    }


}
