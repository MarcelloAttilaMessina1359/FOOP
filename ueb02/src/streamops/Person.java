package streamops;

import java.util.Objects;

/**
 * Repräsentiert eine Person mit Namen, Geschlecht, Wohnort und ggfs. Einkommen.
 * 
 * Diese Klasse darf nicht verändert werden.
 * 
 * @author kar, mhe
 *
 */
public class Person {

    /**
     * Aufzählungstyp für das Geschlecht.
     */
    public enum Gender {
        DIVERSE, FEMALE, MALE
    };

    private final Gender g;
    private final String name, surname;
    private final int zipcode;
    private final int income;

    /**
     * Konstruktor.
     * 
     * @param g Geschlecht
     * @param name Vorname
     * @param surname Zuname
     * @param zipcode Postleitzahl
     * @param income Einkommen
     * @pre g != null
     * @pre name != null
     * @pre surname != null
     */
    public Person(Gender g, String name, String surname, int zipcode, int income) {
        super();
        assert g != null;
        assert name != null;
        assert surname != null;

        this.g = g;
        this.name = name;
        this.surname = surname;
        this.zipcode = zipcode;
        this.income = income;
    }

    /**
     * Gibt das Geschlecht zurück.
     * 
     * @return Das Geschlecht
     */
    public Gender getGender() {
        return g;
    }

    /**
     * Gibt den Vornamen zurück.
     * 
     * @return Der Vorname
     */
    public String getName() {
        return name;
    }

    /**
     * Gibt den Zuname zurück.
     * 
     * @return Der Zuname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Gibt die Postleitzahl zurück.
     * 
     * @return Die Postleitzahl
     */
    public int getZipcode() {
        return zipcode;
    }

    /**
     * Gibt das Einkommen zurück.
     * 
     * @return Das Einkommen
     */
    public int getIncome() {
        return income;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Person)) {
            return false;
        }
        Person other = (Person) obj;
        return this.g == other.g && this.income == other.income && this.name.equals(other.name)
                && this.surname.equals(other.surname) && this.zipcode == other.zipcode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(g, income, name, surname, zipcode);
    }

    @Override
    public String toString() {
        return "Person [g=" + g + ", name=" + name + ", surname=" + surname + ", zipcode=" + zipcode
                + ", income=" + income + "]";
    }

}
