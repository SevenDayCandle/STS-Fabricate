package pinacolada.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Denote a Blight to be registered. Such Blights MUST have a static String field whose name matches the value listed in id(). Augments not registered in this fashion cannot be spawned in the console or chosen in the loadout editor.
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface VisibleBlight {
    String ID = "ID";

    String id() default ID;
}
