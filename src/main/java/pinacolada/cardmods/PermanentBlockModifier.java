package pinacolada.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;

// Copied and modified from STS-AnimatorMod
public class PermanentBlockModifier extends AbstractCardModifier {
    public int change;

    public PermanentBlockModifier(int change) {
        this.change = change;
    }

    public static PermanentBlockModifier apply(AbstractCard c, int change) {
        PermanentBlockModifier mod = get(c);
        if (mod == null) {
            mod = new PermanentBlockModifier(change);
            CardModifierManager.addModifier(c, mod);
        }
        else {
            mod.change += change;
            mod.onInitialApplication(c);
        }
        return mod;
    }

    public static PermanentBlockModifier get(AbstractCard c) {
        for (AbstractCardModifier mod : CardModifierManager.modifiers(c)) {
            if (mod instanceof PermanentBlockModifier) {
                return (PermanentBlockModifier) mod;
            }
        }
        return null;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new PermanentBlockModifier(change);
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block + change;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.block = card.baseBlock + change;
        card.isBlockModified = card.baseBlock != card.block;
    }
}
