package examples;

public class EBooleanNull {
    public boolean _boolean_true, _boolean_false;
    public Boolean _Boolean_true;
    public Object _Boolean_false;
    public Boolean _Boolean_null; // null-Werte werden nicht serialisiert

    public EBooleanNull init() {
        this._boolean_true = true;
        this._boolean_false = false;
        this._Boolean_true = true;
        this._Boolean_false = false;
        this._Boolean_null = null;
        
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_Boolean_false == null) ? 0 : _Boolean_false.hashCode());
        result = prime * result + ((_Boolean_null == null) ? 0 : _Boolean_null.hashCode());
        result = prime * result + ((_Boolean_true == null) ? 0 : _Boolean_true.hashCode());
        result = prime * result + (_boolean_false ? 1231 : 1237);
        result = prime * result + (_boolean_true ? 1231 : 1237);
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
        EBooleanNull other = (EBooleanNull) obj;
        if (_Boolean_false == null) {
            if (other._Boolean_false != null)
                return false;
        } else if (!_Boolean_false.equals(other._Boolean_false))
            return false;
        if (_Boolean_null == null) {
            if (other._Boolean_null != null)
                return false;
        } else if (!_Boolean_null.equals(other._Boolean_null))
            return false;
        if (_Boolean_true == null) {
            if (other._Boolean_true != null)
                return false;
        } else if (!_Boolean_true.equals(other._Boolean_true))
            return false;
        if (_boolean_false != other._boolean_false)
            return false;
        if (_boolean_true != other._boolean_true)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "EBooleanNull [_boolean_true=" + _boolean_true + ", _boolean_false=" + _boolean_false
                + ", _Boolean_true=" + _Boolean_true + ", _Boolean_false=" + _Boolean_false
                + ", _Boolean_null=" + _Boolean_null + "]";
    }
}
