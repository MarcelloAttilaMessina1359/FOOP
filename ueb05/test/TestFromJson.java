import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import examples.EAccessibility;
import examples.EAnnotation;
import examples.EArray;
import examples.EBooleanNull;
import examples.ECharString;
import examples.ECycle;
import examples.EList;
import examples.EMapObject;
import examples.ENumber;
import examples.EInheritance.EChild;
import wson.JSONSyntaxException;
import wson.Wson;

/**
 * Testet die Methode fromJson
 * 
 * @author kar, mhe
 *
 */
public class TestFromJson {
    private final Wson wson = new Wson();

    private final EArray eArray = new EArray().init();
    private final EBooleanNull eBooleanNull = new EBooleanNull().init();
    private final ECharString eCharString = new ECharString().init();
    private final EChild eChild = new EChild().init();
    private final EList eList = new EList().init();
    private final ENumber eNumber = new ENumber().init();

    @Test
    public void eBooleanNull() throws JSONSyntaxException, IOException {
        Assert.assertEquals("fromJson: einzelner boolean-Wert", true,
                wson.fromJson("true", boolean.class));
        Assert.assertEquals("fromJson: einzelner boolean-Wert", false,
                wson.fromJson("false", boolean.class));
        Assert.assertEquals("fromJson: EBooleanNull", eBooleanNull,
                wson.fromJson(FileReader.readExampleJson("EBooleanNull"), EBooleanNull.class));
    }

    @Test
    public void eNumber() throws JSONSyntaxException, IOException {
        Assert.assertEquals("fromJson: einzelner double-Wert", 36.2,
                wson.fromJson("36.2", double.class), 0.1);
        Assert.assertEquals("fromJson: ENumber", eNumber,
                wson.fromJson(FileReader.readExampleJson("ENumber"), ENumber.class));
    }

    @Test
    public void eCharString() throws JSONSyntaxException, IOException {
        Assert.assertEquals("fromJson: einzelner String-Wert", "FHW",
                wson.fromJson("\"FHW\"", String.class));
        Assert.assertEquals("fromJson: ECharString", eCharString,
                wson.fromJson(FileReader.readExampleJson("ECharString"), ECharString.class));
    }

    @Test
    public void eArray() throws JSONSyntaxException, IOException {
        int[] a = { 4, 2 };

        Assert.assertArrayEquals("fromJson: einzelnes Array", a,
                wson.fromJson("[4,2]", int[].class));
        Assert.assertEquals("fromJson: EArray", eArray,
                wson.fromJson(FileReader.readExampleJson("EArray"), EArray.class));
    }
    
    @Test
    public void eList() throws JSONSyntaxException, IOException {
        Assert.assertEquals("fromJson: EList", eList,
                wson.fromJson(FileReader.readExampleJson("EList"), EList.class));
    }
    
    private void testExampleMapObject(EMapObject e) {
        Assert.assertEquals("_obj (EBooleanNull) in EMapObject", eBooleanNull, e._obj);

        Map<String, EBooleanNull> _map = new HashMap<>();
        _map.put("o", new EBooleanNull().init());
        Assert.assertEquals("_map in EMapObject", _map, e._map);

        Map<Object, Object> objAsMap = new HashMap<>();
        objAsMap.put("_boolean_true", false);
        objAsMap.put("_boolean_false", false);
        // Boolean-Wrapper (null-Werte) werden nicht serialisiert
        Assert.assertEquals("_raw_map/key (EBooleanNull, Objekt wird zu Map) in EMapObject",
                objAsMap, e._raw_map.get("key"));

        // Schlüssel werden zu Strings
        Map<String, Object> _raw_map = new HashMap<>();
        _raw_map.put("4.2", 42.0d); // Zahlen werden zu double-Werten
        _raw_map.put("true", new HashMap<>());
        _raw_map.put("\" \"", "value");

        e._raw_map.remove("key"); // bereits oben geprüft
        Assert.assertEquals(_raw_map, e._raw_map);
    }

    @Test
    public void eMapObject() throws JSONSyntaxException, IOException {
        testExampleMapObject(
                wson.fromJson(FileReader.readExampleJson("EMapObject"), EMapObject.class));
    }
    
    @Test
    public void eMapObject_whitespace_unordered() throws JSONSyntaxException, IOException {
        // Whitespaces in JSON müssen beim Einlesen übersprungen werden
        // Einträge von JSON-Maps sind unsortiert
        testExampleMapObject(wson.fromJson(
                FileReader.readExampleJson("EMapObject_whitespace_unordered"), EMapObject.class));
    }

    @Test
    public void eAccessibility() throws JSONSyntaxException, IOException {
        EAccessibility e =
                wson.fromJson(FileReader.readExampleJson("EAccessibility"), EAccessibility.class);
        Assert.assertEquals("fromJson: EAccessibility (_private)", 42, e.getValue());
    }

    @Test
    public void eCycle() throws JSONSyntaxException, IOException {
        ECycle e = wson.fromJson(FileReader.readExampleJson("ECycle"), ECycle.class);

        Assert.assertEquals("fromJson: ECycle (b1)", new EBooleanNull(), e.b1);
        Assert.assertEquals("fromJson: ECycle (b2)", new EBooleanNull(), e.b2);
        Assert.assertNull("fromJson: ECycle (other1)", e.other1);

        // other2 ("{}") wird als neues ECycle-Objekt deserialisiert:
        Assert.assertNull("fromJson: ECycle (other2.b1)", e.other2.b1);
        Assert.assertNull("fromJson: ECycle (other2.b2)", e.other2.b2);
        Assert.assertNull("fromJson: ECycle (other2.other1)", e.other2.other1);
        Assert.assertNull("fromJson: ECycle (other2.other2)", e.other2.other2);
    }

    @Test
    public void eInheritance() throws JSONSyntaxException, IOException {
        Assert.assertEquals("fromJson: EInheritance", eChild,
                wson.fromJson(FileReader.readExampleJson("EInheritance"), EChild.class));
    }


}
