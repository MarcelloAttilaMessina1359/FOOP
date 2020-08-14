package examples;

import wson.annotations.StoreAs;

public class EAnnotation {

    @StoreAs("new") // Wert, den _string im JSON-Format erhalten soll
    public String _string = "old";

    @StoreAs("new") // Wert, den _object im JSON-Format erhalten soll
    public Object _object;

    // Annotation an Feldern, denen String nicht zugewiesen werden kann, wird ignoriert:
    @StoreAs("invalid")
    public int _int = -1;

    public EAnnotation init() {
        // Initialisierung geschieht bereits im impliziten Konstruktor
        
        return this;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + _int;
        result = prime * result + ((_object == null) ? 0 : _object.hashCode());
        result = prime * result + ((_string == null) ? 0 : _string.hashCode());
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
        EAnnotation other = (EAnnotation) obj;
        if (_int != other._int)
            return false;
        if (_object == null) {
            if (other._object != null)
                return false;
        } else if (!_object.equals(other._object))
            return false;
        if (_string == null) {
            if (other._string != null)
                return false;
        } else if (!_string.equals(other._string))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "EAnnotation [_string=" + _string + ", _object=" + _object + ", _int=" + _int + "]";
    }

}
