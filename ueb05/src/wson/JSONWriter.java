package wson;


import wson.annotations.StoreAs;


import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Enthält Hilfsmethoden für {@link Wson#toJson}.
 * 
 * @author kar, mhe, Marcello
 *
 */
class JSONWriter {

    /**
     * Ein Set in dem bereits besichtigte Objekte gespeichert werden
     * dient zur Erkennung von Zirkelschlüssen
     */
    private Set<ReferenceWrapper> visited = new HashSet<>();

    /**
     * Maskiert Zeichen im übergebenen String gemäß der Vorgaben von JSON.
     * 
     * @param s Quellstring
     * @return String mit maskierten Zeichen
     */
    private String escapeString(String s) {
        s = s.replace("\\", "\\\\");
        s = s.replace("\"", "\\\"");
        // s = s.replace("/", "\\/"); // nur bei Rückübersetzung relevant, hier nicht nötig
        s = s.replace("\b", "\\b");
        s = s.replace("\f", "\\f");
        s = s.replace("\n", "\\n");
        s = s.replace("\r", "\\r");
        s = s.replace("\t", "\\t");
        // nicht-druckbare Zeichen werden ignoriert

        return s;
    }

    /**
     * Methode die zu einer Klasse alle Felder einer Klasse und ihrer
     * geerbten auf eine Map abildet
     *
     * @param startClass die Klasse ab der die Felder der Klasse durchsucht werden sollen
     * @return eine Map die alle Felde der Klasse @startClass per name auf das Feld abbildet
     */
    private Field[] getAllFields(Class<?> startClass) {
        List<Field> fieldsList = new ArrayList<>();

        while (startClass.getSuperclass() != null) {
            Field[] fields = startClass.getDeclaredFields();
            for (Field f: fields) {
                fieldsList.add(f);
            }
            startClass = startClass.getSuperclass();
        }

        return fieldsList.toArray(new Field[fieldsList.size()]);
    }

    /**
     * Methode um ein Boolean zu serialisieren
     *
     * @param src das Boolean Objekt
     * @param sb Stringbuilder an dem der serialisierte Boolean angehängt werden soll
     */
    private void serializeBoolean(Object src, StringBuilder sb) {
        Boolean b = Boolean.class.cast(src);

        if (b) {
            sb.append("true");
        } else {
            sb.append("false");
        }
    }

    /**
     * Methode um eine Nummer zu serialisiern
     *
     * @param src das Number Objekt
     * @param sb Stringbuilder an dem die serialisierte Number angehängt werden soll
     */
    private void serializeNumber(Object src, StringBuilder sb) {
        Number n = Number.class.cast(src);

        sb.append(n.toString());
    }

    /**
     * Methode um ein Char zu serialisieren
     *
     * @param src das Char Objekt
     * @param sb StringBuilder an dem der zu serialiserende Char angehängt werden soll
     */
    private void serializeCharacter(Object src, StringBuilder sb) {
        Character c = Character.class.cast(src);

        sb.append("\"");
        sb.append(c);
        sb.append("\"");
    }

    /**
     * Methode um ein String zu serialisieren
     *
     * @param src das String Objekt
     * @param sb StringBuilder an dem der zu serialiserende String angehängt werden soll
     */
    private void serializeString(Object src, StringBuilder sb) {
        String s = String.class.cast(src);

        sb.append("\"");
        sb.append(escapeString(s));
        sb.append("\"");
    }

    /**
     * Methode um ein Array zu serialisieren
     *
     * @param src das Array Objekt
     * @param sb StringBuilder an dem das zuserialisierende Array angehägt wird
     */
    private void serializeArray(Object src, StringBuilder sb) {

        sb.append("[");
        for (int o = 0; o < Array.getLength(src); o++) {
            sb.append(serialize(Array.get(src, o))).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
    }

    /**
     * Methode um ein iterable zu serialisieren
     *
     * @param src das iterable objekt
     * @param sb StringBuilder an dem der zuserialisierende Iterable angehägt wird
     */
    private void serializeIterable(Object src, StringBuilder sb) {

        sb.append("[");

        for (Object o : Iterable.class.cast(src)) {
            sb.append(serialize(o)).append(",");
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
    }

    private void serializeMap(Object src, StringBuilder sb) {
        sb.append("{");

        Map<?, ?> map = Map.class.cast(src);

        if (!map.isEmpty()) {
            map.forEach((key, value) -> {
                String sKey = serialize(key);
                if (sKey.charAt(0) == '\"') {
                    sb.append(sKey);
                } else {
                    sb.append("\"");
                    sb.append(sKey);
                    sb.append("\"");
                }
                sb.append(":");
                sb.append(serialize(value));
                sb.append(",");
            });
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("}");
    }

    /**

     * Methode zum Serialisieren eines Objekts
     *
     * @param src das zu serialisierende Objekt
     * @return das Objekt als JSON-String
     */
    protected String serialize(Object src) {

        //Das serialiserte Objekt
        StringBuilder serialized = new StringBuilder();

        //Klassen Object holen um es dann bestimmen zu können
        Class<?> srcCls = src.getClass();

        //Ist es ein Boolean
        if (Boolean.class.isAssignableFrom(srcCls)) {
            serializeBoolean(src, serialized);
            //Ist es eine Zahl
        } else if (Number.class.isAssignableFrom(srcCls)) {
            serializeNumber(src, serialized);
            //Ist es ein String oder eine Char
        } else if (String.class.isAssignableFrom(srcCls)) {
            serializeString(src, serialized);
            //Ist es ein Char
        } else if (Character.class.isAssignableFrom(srcCls)) {
            serializeCharacter(src, serialized);
            //Ist es ein Array
        } else if (srcCls.isArray()) {
            serializeArray(src, serialized);
            //Ist es ein Iterable
        } else if (Iterable.class.isAssignableFrom(srcCls)) {
            serializeIterable(src, serialized);
            //Ist es eine Map
        } else if (Map.class.isAssignableFrom(srcCls)) {
            serializeMap(src, serialized);
            //Zirkelschluss auschließen
        } else {

            if (visited.contains(new ReferenceWrapper(src))) {
                return serialized.append("{}").toString();
            }
            visited.add(new ReferenceWrapper(src));


            Field[] fields = getAllFields(srcCls);
            //Sortieren
            Arrays.sort(fields, new Comparator<Field>() {
                @Override
                public int compare(Field field, Field field1) {
                    return field.getName().compareTo(field1.getName());
                }
            });

            for (Field field: fields) {
                field.setAccessible(true);
                Object srcVl = null;
                StoreAs annotation = field.getAnnotation(StoreAs.class);
                if (annotation != null
                        && field.getType().isAssignableFrom(annotation.value().getClass())) {
                    srcVl = annotation.value();
                } else {
                    try {
                        srcVl = field.get(src);
                    } catch (IllegalAccessException e) {
                        new RuntimeException("Sollte nicht passieren", e);
                    }
                }


                if (srcVl != null && !srcVl.getClass().isAnonymousClass()
                        && !Modifier.isStatic(field.getModifiers())) {
                    serialized.append("\"");
                    serialized.append(field.getName());
                    serialized.append("\":");
                    String s = serialize(srcVl);
                    serialized.append(s).append(",");
                }
            }

            serialized.deleteCharAt(serialized.length() - 1);
            serialized.append("}");
        }


        return serialized.toString();
    }
}
