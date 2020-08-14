package examples;

import java.util.ArrayList;
import java.util.List;

public class EList {
    public List<Integer> _list;

    public EList init() {
        this._list = new ArrayList<>();
        this._list.add(4);
        this._list.add(2);
        
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_list == null) ? 0 : _list.hashCode());
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
        EList other = (EList) obj;
        if (_list == null) {
            if (other._list != null)
                return false;
        } else if (!_list.equals(other._list))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "EList [_list=" + _list + "]";
    }
}
