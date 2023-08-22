package pinacolada.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Denote a Blight to be registered. Such Blights MUST have either static String or PCLBlightData field whose name matches the value listed in data().
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface VisibleBlight {
    String DATA = "DATA";

    String data() default DATA;
}
