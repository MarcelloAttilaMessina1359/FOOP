package wson;


import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Enthält Hilfsmethoden für {@link Wson#fromJson} zur Konvertierung.
 * 
 * @author kar, mhe, Marcello
 *
 */
class JSONReader {

    /**
     * Methode die zu einer Klasse alle Felder einer Klasse und ihrer
     * geerbten auf eine Map abildet
     *
     * @param startClass die Klasse ab der die Felder der Klasse durchsucht werden sollen
     * @return eine Map die alle Felde der Klasse @startClass per name auf das Feld abbildet
     */
    private Map<String, Field> getAllFields(Class<?> startClass) {
        Map<String, Field> stringFieldMap = new HashMap<>();

        while (startClass.getSuperclass() != null) {
            Field[] fields = startClass.getDeclaredFields();
            for (Field f: fields) {
                stringFieldMap.put(f.getName(), f);
            }
            startClass = startClass.getSuperclass();
        }

        return stringFieldMap;
    }

    /**
     * Methode die eine Übergeben Zahl(src) zu Ihren Typ castet
     *
     * @param src die zu castende Zahl
     * @param classOfT die Klasse der zu castenden Zahl
     * @param <T> der Type der Zahl
     * @return die in Ihren Type gecastete Zahl
     */
    private <T> T toNumber(Double src, Class<T> classOfT) {

        if (Byte.class.isAssignableFrom(classOfT)) {
            return classOfT.cast(src.byteValue());
        }

        if (Short.class.isAssignableFrom(classOfT)) {
            return classOfT.cast(src.shortValue());
        }

        if (Integer.class.isAssignableFrom(classOfT)) {
            return classOfT.cast(src.intValue());
        }

        if (Long.class.isAssignableFrom(classOfT)) {
            return classOfT.cast(src.longValue());
        }

        if (Float.class.isAssignableFrom(classOfT)) {
            return classOfT.cast(src.floatValue());
        }

        return classOfT.cast(src);
    }

    /**
     * Methode um aus dem übergebenen src Objekt ein Array des Typs T zu erzeugen
     *
     * @param src das src Array
     * @param classOfT  die Klasse des Arrays
     * @param <T> der Typ der Klasse
     * @return das aus src mit typ T erzeugte Array
     */
    private <T> T toArray(Object src, Class<T> classOfT) {

        List<?> srcList = List.class.cast(src);

        Object array = Array.newInstance(classOfT.getComponentType(), srcList.size());

        for (int obj = 0; obj < srcList.size(); obj++) {
            Object o = srcList.get(obj);
            Array.set(array, obj,
                    fromObject(o, PrimitiveWrapper.wrap(classOfT.getComponentType()), null));
        }

        return classOfT.cast(array);
    }

    /**
     * Methode um eine Liste aus der als Objekt übergenen Liste zu erstellen
     *
     * @param src die src Liste
     * @param classOfT die Klasse des Typs der Liste
     * @param types die Typen der Generics
     * @param <T> der Typ der Liste
     * @return die aus src vom Typ T Type erzeugte liste
     */
    private <T> T toList(Object src, Class<T> classOfT, Type[] types) {

        List<Object> resultList = new ArrayList<>();

        for (Object o: Iterable.class.cast(src)) {
            //Die klasse des Generic
            Class<?> elementClass = null;
            try {
                elementClass = Class.forName(types[0].getTypeName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Die Generische Klassen "
                        + "der Liste konnten nicht gefunden werden ", e);
            }
            resultList.add(fromObject(o, elementClass, null));
        }
        return classOfT.cast(resultList);
    }

    /**
     * Methode um aus der in src enthaltentn Map eine Map des Typs T mit
     * den Typparametern types zu erzeugen
     *
     * @param src die src Map als Objekt
     * @param classOfT die Klasse der Map
     * @param types die generischen Typen der Map
     * @param <T> der Typ der Map
     * @return die aus src vom Typ T erzeugte Map Type, Type
     */
    private <T> T toMap(Object src, Class<T> classOfT, Type[] types) {
        Map<Object, Object> resultMap = new HashMap<>();
        //Map des Objektes casten
        Map<?, ?> map = Map.class.cast(src);
        for (Map.Entry<?, ?> entry: map.entrySet()) {
            //Der Key und Value
            Object key = entry.getKey(),
                    value = entry.getValue();
            //Die Klassen der Generics
            Class<?> keyClass = null,
                    valueClass = null;
            try {
                //Raw map oder nicht ?
                if (types.length > 1) {

                    keyClass = Class.forName(types[0].getTypeName());
                    valueClass = Class.forName(types[1].getTypeName());
                    resultMap.put(fromObject(key, keyClass, null)
                            , fromObject(value, valueClass, null));
                } else {

                    keyClass = Class.forName(types[0].getTypeName());
                    valueClass = value.getClass();

                    resultMap.put(fromObject(key, keyClass, null)
                            , fromObject(value, valueClass, null));

                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Die generischen Klassen "
                        + "der Map konnten nicht gefunden werden ", e);
            }
        }
        return classOfT.cast(resultMap);
    }


    /**
     * Methode die ein Objekt Ausliest und eine Instanz erzeugt
     *
     * @param src das Quell Obekjet
     * @param classOfT die höchste Klasse des Objekts
     * @param types die Typen von Generischen Typen ( die Typen oder null)
     * @param <T> der Typ des Objeckts
     * @return das Objekt was src darstellt
     */
    protected <T> T fromObject(Object src, Class<T> classOfT, Type[] types) {

        if (Boolean.class.isAssignableFrom(classOfT)) {
            return PrimitiveWrapper.wrap(classOfT).cast(src);
        }
        if (Number.class.isAssignableFrom(classOfT)) {
            return toNumber(Double.class.cast(src), PrimitiveWrapper.wrap(classOfT));
        }
        if (Character.class.isAssignableFrom(classOfT)) {
            Character c = String.valueOf(src).charAt(0);
            return PrimitiveWrapper.wrap(classOfT).cast(c);
        }
        if (String.class.isAssignableFrom(classOfT)) {
            return classOfT.cast(src);
        }
        if (classOfT.isArray()) {
            return  toArray(src, classOfT);
        }
        if (Iterable.class.isAssignableFrom(classOfT)) {

            return toList(src, classOfT, types);
        }

        if (Map.class.isAssignableFrom(classOfT)) {
            //Wenn keine Types dann ist es Objekt
            //dessen Class nicht bestimmt werden
            //konnte
            if (types == null) {
                types = new Type[] {String.class};
            }
            return toMap(src, classOfT, types);
        } else {
            //Neues Objekt erzeugen
            T obj = null;

            try {
                obj = classOfT.getDeclaredConstructor().newInstance();
            } catch (InstantiationException
                    | IllegalAccessException
                    | InvocationTargetException
                    | NoSuchMethodException e) {
                throw new RuntimeException("Objekt der Klasse " + classOfT.getName()
                + " konnte nicht initsialisiert werden ", e);
            }
            //Alle Felder holen
            Map<String, Field> fields = getAllFields(obj.getClass());
            //Map des Objektes casten
            Map<?, ?> objMap = Map.class.cast(src);
            //Über die objMap loop und das Objekt befüllen
            for (Map.Entry<?, ?> entry: objMap.entrySet()) {
                //Felder müssen Strings sein, sosnt ist es Fehlerhaft
                String key = (String) entry.getKey();
                Object value = entry.getValue();
                //Das zu füllende Feld
                try {
                    //Das Feld
                    Field f = fields.get(key);
                    f.setAccessible(true);

                    //Das Objekt
                    Object o = null;
                    //Der Typ des Feldes
                    Class<?> fieldType = f.getType();

                    //Den spezielleren Typ nehmen
                    if (fieldType.isAssignableFrom(entry.getValue().getClass())
                            && entry.getValue().getClass() != fieldType) {
                        fieldType = entry.getValue().getClass();
                    }

                    //Falls es eine Liste die Generics bestimmen
                    if (List.class.isAssignableFrom(fieldType)) {

                        ParameterizedType p = (ParameterizedType) f.getGenericType();
                        Type[] listTypes = p.getActualTypeArguments();
                        o = toList(value, fieldType, listTypes);
                    } else if (Map.class.isAssignableFrom(fieldType)) {

                        Type t = f.getGenericType();
                        ParameterizedType p = null;
                        Type[] mapTypes = null;
                        //Raw map oder nicht
                        if (t instanceof ParameterizedType) {
                            p = (ParameterizedType) f.getGenericType();
                            mapTypes = p.getActualTypeArguments();
                        } else {
                            mapTypes = new Type[] {String.class};
                        }

                        o = toMap(value, fieldType, mapTypes);
                    } else {

                        o = fromObject(value, PrimitiveWrapper.wrap(fieldType), null);
                    }
                    //Und setzen
                    f.set(obj, o);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Feld konnte nicht gefunden werden ", e);
                }
            }
            return obj;
        }
    }
}
