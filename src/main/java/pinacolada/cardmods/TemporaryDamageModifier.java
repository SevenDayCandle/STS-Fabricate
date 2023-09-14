package pinacolada.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

// Copied and modified from STS-AnimatorMod
@AbstractCardModifier.SaveIgnore
public class TemporaryDamageModifier extends AbstractCardModifier {
    protected transient boolean temporary;
    protected transient boolean untilPlayed;
    public transient int change;

    public TemporaryDamageModifier(int change) {
        this(change, false, false);
    }

    public TemporaryDamageModifier(int change, boolean temporary, boolean untilPlayed) {
        this.change = change;
        this.temporary = temporary;
        this.untilPlayed = untilPlayed;
    }

    public static TemporaryDamageModifier apply(AbstractCard c, int change, boolean temporary, boolean untilPlayed) {
        TemporaryDamageModifier mod = get(c);
        if (mod == null) {
            mod = new TemporaryDamageModifier(change, temporary, untilPlayed);
            CardModifierManager.addModifier(c, mod);
        }
        else {
            mod.change += change;
            mod.temporary = mod.temporary & temporary;
            mod.untilPlayed = mod.untilPlayed & untilPlayed;
        }
        return mod;
    }

    public static TemporaryDamageModifier get(AbstractCard c) {
        for (AbstractCardModifier mod : CardModifierManager.modifiers(c)) {
            if (mod instanceof TemporaryDamageModifier) {
                return (TemporaryDamageModifier) mod;
            }
        }
        return null;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new TemporaryDamageModifier(change, temporary, untilPlayed);
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage + change;
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
