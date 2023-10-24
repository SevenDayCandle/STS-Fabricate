package pinacolada.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class TemporaryBlockPercentModifier extends AbstractCardModifier {
    protected transient boolean temporary;
    protected transient boolean untilPlayed;
    public int change;

    public TemporaryBlockPercentModifier(int change) {
        this(change, false, false);
    }

    public TemporaryBlockPercentModifier(int change, boolean temporary, boolean untilPlayed) {
        this.change = change;
        this.temporary = temporary;
        this.untilPlayed = untilPlayed;
    }

    public static TemporaryBlockPercentModifier apply(AbstractCard c, int change, boolean temporary, boolean untilPlayed) {
        TemporaryBlockPercentModifier mod = get(c);
        if (mod == null) {
            mod = new TemporaryBlockPercentModifier(change, temporary, untilPlayed);
            CardModifierManager.addModifier(c, mod);
        }
        else {
            mod.change += change;
            mod.temporary = mod.temporary & temporary;
            mod.untilPlayed = mod.untilPlayed & untilPlayed;
            mod.onInitialApplication(c);
        }
        return mod;
    }

    public static TemporaryBlockPercentModifier get(AbstractCard c) {
        for (AbstractCardModifier mod : CardModifierManager.modifiers(c)) {
            if (mod instanceof TemporaryBlockPercentModifier) {
                return (TemporaryBlockPercentModifier) mod;
            }
        }
        return null;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new TemporaryBlockPercentModifier(change, temporary, untilPlayed);
    }

    @Override
    public float modifyBlock(float block, AbstractCard card) {
        return block + block * change / 100f;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.block = (int) (card.baseBlock + card.baseBlock * change / 100f);
        card.isBlockModified = card.baseBlock != card.block;
    }

    @Override
    public boolean removeAtEndOfTurn(AbstractCard card) {
        return temporary;
    }

    @Override
    public boolean removeOnCardPlayed(AbstractCard card) {
        return untilPlayed;
    }
}
