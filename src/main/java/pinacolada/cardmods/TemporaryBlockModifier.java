package pinacolada.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;

// Copied and modified from STS-AnimatorMod
public class TemporaryBlockModifier extends AbstractCardModifier {
    protected transient boolean temporary;
    protected transient boolean untilPlayed;
    public int change;

    public TemporaryBlockModifier(int change) {
        this(change, false, false);
    }

    public TemporaryBlockModifier(int change, boolean temporary, boolean untilPlayed) {
        this.change = change;
        this.temporary = temporary;
        this.untilPlayed = untilPlayed;
    }

    public static TemporaryBlockModifier apply(AbstractCard c, int change, boolean temporary, boolean untilPlayed) {
        TemporaryBlockModifier mod = get(c);
        if (mod == null) {
            mod = new TemporaryBlockModifier(change, temporary, untilPlayed);
            CardModifierManager.addModifier(c, mod);
        }
        else {
            mod.change += change;
            mod.temporary = mod.temporary & temporary;
            mod.untilPlayed = mod.untilPlayed & untilPlayed;
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
    public AbstractCardModifier makeCopy() {
        return new TemporaryBlockModifier(change, temporary, untilPlayed);
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block + change;
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
