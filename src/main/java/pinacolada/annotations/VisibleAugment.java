package pinacolada.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Denote an Augment to be registered. Such Augments MUST have a static PCLAugmentData field whose name matches the value listed in data(). Augments not registered in this fashion cannot be spawned in the console or chosen in the loadout editor.
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface VisibleAugment {
    String DEFAULT = "DATA";

    String data() default DEFAULT;
}
