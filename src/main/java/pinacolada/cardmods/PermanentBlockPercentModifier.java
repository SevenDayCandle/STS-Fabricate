package pinacolada.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class PermanentBlockPercentModifier extends AbstractCardModifier {
    public int change;

    public PermanentBlockPercentModifier(int change) {
        this.change = change;
    }

    public static PermanentBlockPercentModifier apply(AbstractCard c, int change) {
        PermanentBlockPercentModifier mod = get(c);
        if (mod == null) {
            mod = new PermanentBlockPercentModifier(change);
            CardModifierManager.addModifier(c, mod);
        }
        else {
            mod.change += change;
            mod.onInitialApplication(c);
        }
        return mod;
    }

    public static PermanentBlockPercentModifier get(AbstractCard c) {
        for (AbstractCardModifier mod : CardModifierManager.modifiers(c)) {
            if (mod instanceof PermanentBlockPercentModifier) {
                return (PermanentBlockPercentModifier) mod;
            }
        }
        return null;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new PermanentBlockPercentModifier(change);
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block + block * change / 100f;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.block = (int) (card.baseBlock + card.baseBlock * change / 100f);
        card.isBlockModified = card.baseBlock != card.block;
    }
}
