package pinacolada.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Denote a power to be registered with Basemod. Replaces Basemod.addPower
// If this power has a POWER_ID field, it will be used as the ID to be used in Basemod. Otherwise, a new ID will be generated from its name
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface VisiblePower
{
}
