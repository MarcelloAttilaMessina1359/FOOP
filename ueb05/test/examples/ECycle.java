package examples;

public class ECycle {
    public ECycle other1, other2;

    public EBooleanNull b1, b2;

    public ECycle init() {
        this.other1 = this; // other1 wird nicht mehr serialisiert, da ein Zirkelschluss entsteht.
        // Die Serialisierung darf natürlich nicht zu einer Endlosschleife führen.
        
        this.other2 = new ECycle(); // wird als "{}" serialisiert, da Zirkelschluss über 
        this.other2.other2 = this; // other2.other2 und this (und sonst nur null-Werte)

        this.b1 = new EBooleanNull();
        this.b2 = b1; // kein Zirkelschluss
        // b1 und b2 werden beide als jeweils eigene Objekte serialisiert
        
        return this;
    }
}
