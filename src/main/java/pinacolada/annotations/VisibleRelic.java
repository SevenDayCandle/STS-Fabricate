package pinacolada.annotations;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Denote a relic that can be viewed in the relic compendium. Replaces Basemod.addRelic
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface VisibleRelic {
    AbstractCard.CardColor color() default AbstractCard.CardColor.COLORLESS;
}
