package values;


import expressions.exceptions.DivByZeroException;

/**
 * Ein Wert für Ganzzahlen (int).
 *
 * @author kar, mhe, Marcello
 */
public class IntValue extends Value<IntValue> implements DividableValue<IntValue> {
    /**
     * Wert der Ganzzahl.
     */
    private final int value;

    /**
     * Konstruktor.
     * 
     * @param value Der Wert der Ganzzahlen
     */
    public IntValue(int value) {
        this.value = value;
    }

    /**
     * Gibt den Wert der Ganzzahl zurück.
     * 
     * @return Der Wert der Ganzzahl
     */
    public int getValue() {
        return value;
    }


    @Override
    public IntValue add(IntValue other) {
        return new IntValue(this.value + other.value);
    }

    @Override
    public IntValue mul(IntValue other) {
        return new IntValue(this.value * other.value);
    }

    @Override
    public IntValue sub(IntValue other) {
        return new IntValue(this.value - other.value);
    }

    @Override
    public StringBuilder toString(StringBuilder builder) {
        return builder.append(this.value);
    }

    @Override
    public IntValue div(IntValue other) throws DivByZeroException {

        if (other.value == 0) {
            throw new DivByZeroException();
        }
        return new IntValue(this.value / other.value);
    }
}
