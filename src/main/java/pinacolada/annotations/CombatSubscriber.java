package pinacolada.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Denote an interface class to be registered with CombatManager
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CombatSubscriber
{
}
