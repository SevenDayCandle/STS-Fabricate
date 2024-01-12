package pinacolada.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Denote a card that can be viewed in the card compendium and the card editor. Replaces Basemod.addCard
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface VisibleCard {
    boolean add() default true;
}
