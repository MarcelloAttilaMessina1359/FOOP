package wson.annotations;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Typ für Annotationen, die zu nutzende Werte für Felder angeben, denen Strings zugewiesen werden
 * können
 * 
 * @author kar, mhe, Marcello
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StoreAs {
    /**
     * Der überschreibende Wert
     *
     * @return den Wert der mit der die Annotation versetzt wurde
     */
    String value();
}
