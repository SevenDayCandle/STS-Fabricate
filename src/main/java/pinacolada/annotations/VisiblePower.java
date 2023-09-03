package pinacolada.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Denote a Power to be registered with Basemod. Such Powers should have a static PCLPowerData field whose name matches the value listed in data(). Powers without such a field will have an ID automatically created for them
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface VisiblePower {
    String DEFAULT = "DATA";

    String data() default DEFAULT;
}
