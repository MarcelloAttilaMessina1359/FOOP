package examples;

public class EAccessibility {
    // wird serialisiert:
    public final int _final = 5;
    private int _private;
    int _friendly;

    // wird nicht serialisiert:
    public static final int CONST = 3;
    public Object _anonymous = new Object() {
        @SuppressWarnings("unused")
        public int _not_serialized = -1;
    };

    public int getValue() {
        return _private;
    }

    public EAccessibility init() {
        // Synthetische Variablen, die durch diese Assertion erstellt würden, werden
        // nicht serialisiert:
        assert _private != 123;

        this._private = 42;
        this._friendly = 143;
        
        return this;
    }
    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_anonymous == null) ? 0 : _anonymous.hashCode());
        result = prime * result + _final;
        result = prime * result + _private;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EAccessibility other = (EAccessibility) obj;
        if (_anonymous == null) {
            if (other._anonymous != null)
                return false;
        } else if (!_anonymous.equals(other._anonymous))
            return false;
        if (_final != other._final)
            return false;
        if (_private != other._private)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "EAccessibility [_final=" + _final + ", _private=" + _private + ", _friendly="
                + _friendly + ", _anonymous=" + _anonymous + "]";
    }

}
