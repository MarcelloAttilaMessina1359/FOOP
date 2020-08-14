package examples;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
public class EMapObject {
    public EBooleanNull _obj = new EBooleanNull();
    public Map<String, EBooleanNull> _map = new HashMap<>();
    public HashMap _raw_map = new HashMap();
    
    public EMapObject init() {
        _obj.init();
        
        // korrektes EBooleanNull bei Deserialierung
        this._map.put("o", new EBooleanNull().init());
        
        // Schlüssel wird jeweils zu String:
        this._raw_map.put(4.2, 42);  // 42 wird als 42.0 deserialisiert
        this._raw_map.put(true, new HashMap());
        this._raw_map.put("\" \"", "value");
        
        // Objekt wird bei Deserialierung zu Map, wenn Typ unbestimmbar:
        this._raw_map.put("key", new EBooleanNull()); 
        
        return this;
    }
}
