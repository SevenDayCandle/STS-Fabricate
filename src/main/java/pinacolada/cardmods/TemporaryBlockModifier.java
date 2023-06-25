package pinacolada.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;

// Copied and modified from STS-AnimatorMod
public class TemporaryBlockModifier extends AbstractCardModifier {
    protected boolean removeOnPlay;
    protected boolean temporary;
    public int change;

    public TemporaryBlockModifier(int change) {
        this(change, false, false);
    }

    public TemporaryBlockModifier(int change, boolean removeOnPlay, boolean temporary) {
        this.change = change;
        this.removeOnPlay = removeOnPlay;
        this.temporary = temporary;
    }

    public static TemporaryBlockModifier apply(AbstractCard c, int change, boolean removeOnPlay, boolean temporary) {
        TemporaryBlockModifier mod = get(c);
        if (mod == null) {
            mod = new TemporaryBlockModifier(change, removeOnPlay, temporary);
            CardModifierManager.addModifier(c, mod);
        }
        else {
            mod.change += change;
            mod.removeOnPlay = mod.removeOnPlay & removeOnPlay;
            mod.temporary = mod.temporary & temporary;
        }
        return mod;
    }

    public static TemporaryBlockModifier get(AbstractCard c) {
        for (AbstractCardModifier mod : CardModifierManager.modifiers(c)) {
            if (mod instanceof TemporaryBlockModifier) {
                return (TemporaryBlockModifier) mod;
            }
        }
        return null;
    }

    @Override
    public boolean removeOnCardPlayed(AbstractCard card) {
        return removeOnPlay;
    }

    public boolean removeAtEndOfTurn(AbstractCard card) {
        return temporary;
    }

    public float modifyBlock(float block, AbstractCard card) {
        return block + change;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new TemporaryBlockModifier(change, removeOnPlay, temporary);
    }
}
