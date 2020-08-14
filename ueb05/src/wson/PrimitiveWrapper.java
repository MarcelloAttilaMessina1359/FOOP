package wson;

import java.util.HashMap;
import java.util.Map;

/**
 * Stellt eine Hilfsmethode {@link #wrap} zur Verfügung, die zu Class-Objekten primitiver Typen die
 * Objekte der entsprechenden Wrapper-Klassen liefert.
 * 
 * Diese Klasse ist vollständig vorgegeben und darf nicht verändert werden.
 * 
 * @author kar, mhe
 */
public class PrimitiveWrapper {
    private static final Map<Class<?>, Class<?>> PRIMTOWRAP = new HashMap<>();

    static {
        PRIMTOWRAP.put(boolean.class, Boolean.class);
        PRIMTOWRAP.put(byte.class, Byte.class);
        PRIMTOWRAP.put(char.class, Character.class);
        PRIMTOWRAP.put(double.class, Double.class);
        PRIMTOWRAP.put(float.class, Float.class);
        PRIMTOWRAP.put(int.class, Integer.class);
        PRIMTOWRAP.put(long.class, Long.class);
        PRIMTOWRAP.put(short.class, Short.class);
        PRIMTOWRAP.put(void.class, Void.class);
    }

    /**
     * Gibt zu einem übergebenen Class-Objekt das entsprechende Objekt der Wrapper-Klasse zurück,
     * wenn es ein primitiver Typ ist. Ansonsten wird es unverändert zurückgegeben.
     * 
     * @param type beliebiges Class-Objekt
     * @param <T> Typ der Werte von type
     * @pre type != null
     * @return type selbst, wenn type keine Klasse für primitive Werte ist, ansonsten die
     *         entsprechende Wrapper-Klasse
     */
    public static <T> Class<T> wrap(Class<T> type) {
        assert type != null;

        return (Class<T>) PRIMTOWRAP.getOrDefault(type, type); // sicherer Cast
    }
}
