package pinacolada.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLCardAffinities;

// Modifier for displaying affinities if they are applied to a non PCL card
@AbstractCardModifier.SaveIgnore
public class AffinityDisplayModifier extends AbstractCardModifier {
    public PCLCardAffinities affinities = new PCLCardAffinities(null);

    public AffinityDisplayModifier() {
    }

    public AffinityDisplayModifier(PCLCardAffinities affinities) {
        this.affinities = new PCLCardAffinities(affinities.card, affinities);
    }

    public static AffinityDisplayModifier get(AbstractCard c) {
        for (AbstractCardModifier mod : CardModifierManager.modifiers(c)) {
            if (mod instanceof AffinityDisplayModifier) {
                return (AffinityDisplayModifier) mod;
            }
        }
        return null;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new AffinityDisplayModifier(affinities);
    }

    public void onInitialApplication(AbstractCard card) {
        affinities = new PCLCardAffinities(card);
    }

    @Override
    public void onRender(AbstractCard card, SpriteBatch sb) {
        affinities.renderOnCard(sb, card, false);
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        return !(card instanceof PCLCard);
    }

}
