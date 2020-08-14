package examples;

public class ENumber {
    public byte _byte;
    public Byte _Byte;
    public short _short;
    public Short _Short;
    public int _int;
    public Integer _Integer;
    public long _long;
    public Long _Long;
    public float _float;
    public Float _Float;
    public double _double;
    public Double _Double;

    public ENumber init() {
        this._byte = 42;
        this._Byte = _byte;
        this._short = -1337;
        this._Short = _short;
        this._int = 123456;
        this._Integer = _int;
        this._long = -9876543210L;
        this._Long = _long;
        this._float = 1.0F;
        this._Float = _float;
        this._double = -0.4242424242D;
        this._Double = _double;
        
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_Byte == null) ? 0 : _Byte.hashCode());
        result = prime * result + ((_Double == null) ? 0 : _Double.hashCode());
        result = prime * result + ((_Float == null) ? 0 : _Float.hashCode());
        result = prime * result + ((_Integer == null) ? 0 : _Integer.hashCode());
        result = prime * result + ((_Long == null) ? 0 : _Long.hashCode());
        result = prime * result + ((_Short == null) ? 0 : _Short.hashCode());
        result = prime * result + _byte;
        long temp;
        temp = Double.doubleToLongBits(_double);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + Float.floatToIntBits(_float);
        result = prime * result + _int;
        result = prime * result + (int) (_long ^ (_long >>> 32));
        result = prime * result + _short;
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
        ENumber other = (ENumber) obj;
        if (_Byte == null) {
            if (other._Byte != null)
                return false;
        } else if (!_Byte.equals(other._Byte))
            return false;
        if (_Double == null) {
            if (other._Double != null)
                return false;
        } else if (!_Double.equals(other._Double))
            return false;
        if (_Float == null) {
            if (other._Float != null)
                return false;
        } else if (!_Float.equals(other._Float))
            return false;
        if (_Integer == null) {
            if (other._Integer != null)
                return false;
        } else if (!_Integer.equals(other._Integer))
            return false;
        if (_Long == null) {
            if (other._Long != null)
                return false;
        } else if (!_Long.equals(other._Long))
            return false;
        if (_Short == null) {
            if (other._Short != null)
                return false;
        } else if (!_Short.equals(other._Short))
            return false;
        if (_byte != other._byte)
            return false;
        if (Double.doubleToLongBits(_double) != Double.doubleToLongBits(other._double))
            return false;
        if (Float.floatToIntBits(_float) != Float.floatToIntBits(other._float))
            return false;
        if (_int != other._int)
            return false;
        if (_long != other._long)
            return false;
        if (_short != other._short)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ENumber [_byte=" + _byte + ", _Byte=" + _Byte + ", _short=" + _short + ", _Short="
                + _Short + ", _int=" + _int + ", _Integer=" + _Integer + ", _long=" + _long
                + ", _Long=" + _Long + ", _float=" + _float + ", _Float=" + _Float + ", _double="
                + _double + ", _Double=" + _Double + "]";
    }
}
