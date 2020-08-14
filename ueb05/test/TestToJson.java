import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import examples.EInheritance.EChild;
import examples.EList;
import examples.EMapObject;
import examples.ENumber;
import wson.JSONSyntaxException;
import wson.Wson;

/**
 * Testet die Methode toJson
 * 
 * @author kar, mhe
 *
 */
public class TestToJson {
    private final Wson wson = new Wson();

    private final EAccessibility eAccessibility = new EAccessibility().init();
    private final EAnnotation eAnnotation = new EAnnotation().init();
    private final EArray eArray = new EArray().init();
    private final EBooleanNull eBooleanNull = new EBooleanNull().init();
    private final ECharString eCharString = new ECharString().init();
    private final EChild eChild = new EChild().init();
    private final ECycle eCycle = new ECycle().init();
    private final EList eList = new EList().init();
    private final EMapObject eMapObject = new EMapObject().init();
    private final ENumber eNumber = new ENumber().init();

    @Test
    public void eBooleanNull() throws JSONSyntaxException, IOException {
        Assert.assertEquals("toJson: einzelner boolean-Wert", "true", wson.toJson(true));
        wson.toJson(eBooleanNull);
        Assert.assertEquals("toJson: EBooleanNull", FileReader.readExampleJson("EBooleanNull"),
                wson.toJson(eBooleanNull));
    }

    @Test
    public void eNumber() throws JSONSyntaxException, IOException {
        Assert.assertEquals("toJson: einzelner double-Wert", "36.2", wson.toJson(36.2));
        Assert.assertEquals("toJson: ENumber", FileReader.readExampleJson("ENumber"),
                wson.toJson(eNumber));
    }

    @Test
    public void eCharString() throws JSONSyntaxException, IOException {
        Assert.assertEquals("toJson: einzelner String-Wert", "\"FHW\"", wson.toJson("FHW"));
        Assert.assertEquals("toJson: ECharString", FileReader.readExampleJson("ECharString"),
                wson.toJson(eCharString));
    }

    @Test
    public void eArray() throws JSONSyntaxException, IOException {
        int[] a = { 4, 2 };
        Assert.assertEquals("toJson: einzelnes Array", "[4,2]", wson.toJson(a));
        wson.toJson(eArray);
        Assert.assertEquals("toJson: EArray", FileReader.readExampleJson("EArray"),
                wson.toJson(eArray));
    }

    @Test
    public void eList() throws JSONSyntaxException, IOException {
        List<Integer> l = new ArrayList<>();
        l.add(4);
        l.add(2);
        Assert.assertEquals("toJson: einzelne List", "[4,2]", wson.toJson(l));
        Assert.assertEquals("toJson: EList", FileReader.readExampleJson("EList"),
                wson.toJson(eList));
    }

    @Test
    public void eMapObject() throws JSONSyntaxException, IOException {
        Assert.assertEquals("toJson: EMapObject", FileReader.readExampleJson("EMapObject"),
                wson.toJson(eMapObject));
    }

    @Test
    public void eAccessibility() throws JSONSyntaxException, IOException {
        wson.toJson(eAccessibility);
        Assert.assertEquals("toJson: EAccessibility", FileReader.readExampleJson("EAccessibility"),
                wson.toJson(eAccessibility));
    }

    @Test
    public void eAnnotation() throws JSONSyntaxException, IOException {
        Assert.assertEquals("toJson: EAnnotation", FileReader.readExampleJson("EAnnotation"),
                wson.toJson(eAnnotation));
    }

    @Test
    public void eCycle() throws JSONSyntaxException, IOException {
        wson.toJson(eCycle);
        Assert.assertEquals("toJson: ECycle", FileReader.readExampleJson("ECycle"),
                wson.toJson(eCycle));
    }

    @Test
    public void eInheritance() throws JSONSyntaxException, IOException {
        Assert.assertEquals("toJson: EInheritance", FileReader.readExampleJson("EInheritance"),
                wson.toJson(eChild));
    }
    
}
