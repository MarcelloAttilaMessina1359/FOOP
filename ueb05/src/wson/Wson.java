package wson;

import java.io.StringReader;
import java.io.PushbackReader;
import java.io.IOException;

/**
 * Eine Klasse zur Serialisierung und Deserialisierung von Java-Werten mittels JSON.
 * 
 * @author kar, mhe, TODO Namen ergänzen
 * 
 */
public class Wson {
    /**
     * Konstruktor
     */
    public Wson() {
    }

    /**
     * Deserialisiert einen JSON-String zu einem Java-Wert.
     * 
     * @param json Zu deserialisierendes JSON. Muss gültig sein. Darf an erlaubten Stellen
     *            Whitespace enthalten. Werte für nicht in den Java-Klassen enthaltene Felder werden
     *            ignoriert.
     * @param classOfT Klasse des deserialisierten (Wurzel-)Wertes
     * @param <T> Typ des deserialisierten (Wurzel-)Wertes
     * @pre json != null
     * @pre classOfT != null
     * @return Der deserialisierte (Wurzel-)Wert
     * @throws JSONSyntaxException Syntax-Fehler bei der JSON-Verarbeitung
     */

    public <T> T fromJson(String json, Class<T> classOfT) throws JSONSyntaxException {
        assert json != null;
        assert classOfT != null;

        try {
            JSONReader r = new JSONReader();
            PushbackReader pbr = new PushbackReader(new StringReader(json));
            Object o = JSONParser.readElement(pbr);

            return r.fromObject(o, PrimitiveWrapper.wrap(classOfT), null);   
        } catch (IOException e) {
            throw new RuntimeException("not supposed to happen", e);
        }
    }


    /**
     * Serialisiert einen Java-Wert zu einem JSON-String.
     * 
     * @param src Zu serialisierender Wert
     * @return JSON-String (ohne unnötige Whitespaces)
     */
    public String toJson(Object src) {

        JSONWriter w = new JSONWriter();
        return w.serialize(src);
    }

}
