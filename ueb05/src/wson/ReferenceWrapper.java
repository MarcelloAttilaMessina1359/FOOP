package wson;

import java.util.Set;

/**
 * Kapselt eine Referenz. Implementiert equals als Vergleich der in zwei ReferenceWrapper-Instanzen
 * gekapselten Referenzen.
 * 
 * Diese Klasse ist vollständig vorgegeben und darf nicht verändert werden.
 * 
 * @author kar, mhe
 *
 */
public class ReferenceWrapper {
    private final Object ref;

    /**
     * Konstruktor
     * 
     * @param ref Zu kapselnde Referenz
     */
    public ReferenceWrapper(Object ref) {
        this.ref = ref;
    }

    @Override
    public String toString() {
        if (ref == null) {
            return "ref: null";
        }
        return "ref: " + this.ref.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ReferenceWrapper) {
            return this.ref == ((ReferenceWrapper) other).ref;
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (ref == null) {
            return 0;
        }
        return this.ref.hashCode();
    }

    /**
     * Gibt zurück, ob die übergebene Referenz gleich einer der im übergebenen Set gekapselten
     * Referenzen ist.
     * 
     * @param ref Die zu prüfende Referenz
     * @param set Das zu durchsuchende Set
     * @pre set != null
     * @return true, wenn die übergebene Referenz gleich einer der im übergebenen Set gekapselten
     *         Referenzen ist, ansonsten false
     */
    public static boolean isRefInSet(Object ref, Set<ReferenceWrapper> set) {
        assert set != null;

        return set.contains(new ReferenceWrapper(ref));
    }

    /**
     * Fügt die übergebene Referenz gekapselt in das übergebene Set ein.
     * 
     * @param ref Die einzufügende Referenz
     * @param set Das Set, in das eingefügt werden soll
     * @pre set != null
     */
    public static void addRefToSet(Object ref, Set<ReferenceWrapper> set) {
        assert set != null;

        set.add(new ReferenceWrapper(ref));
    }
}
