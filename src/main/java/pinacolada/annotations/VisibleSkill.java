package pinacolada.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* Denote a PSkill that can be selected in the card editor. This class MUST have the following
    - A static field whose name matches the value listed in data() that holds this skill's PSkillData
    - An empty constructor that takes no arguments
*/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface VisibleSkill {
    String DEFAULT = "DATA";

    String data() default DEFAULT;
}
