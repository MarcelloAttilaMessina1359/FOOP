package examples;

public class ECharString {
    public char _char;
    public Character _Character;
    public Object _String_simple;
    public String _String_quoted;
    
    public ECharString init() {
        this._char = 'c';
        this._Character = 'c';
        this._String_simple = "FHW";
        this._String_quoted = "\"\\/\b\f\n\r\t";
        
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_Character == null) ? 0 : _Character.hashCode());
        result = prime * result + ((_String_quoted == null) ? 0 : _String_quoted.hashCode());
        result = prime * result + ((_String_simple == null) ? 0 : _String_simple.hashCode());
        result = prime * result + _char;
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
        ECharString other = (ECharString) obj;
        if (_Character == null) {
            if (other._Character != null)
                return false;
        } else if (!_Character.equals(other._Character))
            return false;
        if (_String_quoted == null) {
            if (other._String_quoted != null)
                return false;
        } else if (!_String_quoted.equals(other._String_quoted))
            return false;
        if (_String_simple == null) {
            if (other._String_simple != null)
                return false;
        } else if (!_String_simple.equals(other._String_simple))
            return false;
        if (_char != other._char)
            return false;
        return true;
    }
}
