package pinacolada.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Denote a potion that can be viewed in the potion compendium. Replaces Basemod.addPotion
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface VisiblePotion {
}
