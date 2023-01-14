package pinacolada.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Denote a PSkill that can be selected in the card editor. This class MUST define a field called DATA that holds this skill's PSkillData
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface VisibleSkill
{
}
