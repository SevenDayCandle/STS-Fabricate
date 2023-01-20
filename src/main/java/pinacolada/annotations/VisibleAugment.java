package pinacolada.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Denote an Augment to be registered. Such Augments MUST have an PCLAugmentData field called DATA. Augments not registered in this fashion cannot be spawned in the console or chosen in the loadout editor.
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface VisibleAugment
{
}
