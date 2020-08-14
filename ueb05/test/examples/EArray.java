package examples;

import java.util.Arrays;

public class EArray {

    public int[] _int_array;
    public int[][] _table;

    public EArray init() {
        this._int_array = new int[2];        
        this._int_array[0] = 4;
        this._int_array[1] = 2;
        
        this._table = new int[2][3]; 
        this._table[0][0] = 1;
        this._table[0][1] = 2;
        this._table[0][2] = 3;
        this._table[1][0] = -1;
        this._table[1][1] = -2;
        this._table[1][2] = -3;
        
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(_int_array);
        result = prime * result + Arrays.deepHashCode(_table);
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
        EArray other = (EArray) obj;
        if (!Arrays.equals(_int_array, other._int_array))
            return false;
        if (!Arrays.deepEquals(_table, other._table))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "EArray [_int_array=" + Arrays.toString(_int_array) + ", _table="
                + Arrays.toString(_table) + "]";
    }
}
