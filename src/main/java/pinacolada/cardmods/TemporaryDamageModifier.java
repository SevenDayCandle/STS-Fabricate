package pinacolada.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

// Copied and modified from STS-AnimatorMod
public class TemporaryDamageModifier extends AbstractCardModifier {
    protected boolean removeOnPlay;
    protected boolean temporary;
    public int change;

    public TemporaryDamageModifier(int change) {
        this(change, false, false);
    }

    public TemporaryDamageModifier(int change, boolean removeOnPlay, boolean temporary) {
        this.change = change;
        this.removeOnPlay = removeOnPlay;
        this.temporary = temporary;
    }

    public static TemporaryDamageModifier apply(AbstractCard c, int change, boolean removeOnPlay, boolean temporary) {
        TemporaryDamageModifier mod = get(c);
        if (mod == null) {
            mod = new TemporaryDamageModifier(change, removeOnPlay, temporary);
            CardModifierManager.addModifier(c, mod);
        }
        else {
            mod.change += change;
            mod.removeOnPlay = mod.removeOnPlay & removeOnPlay;
            mod.temporary = mod.temporary & temporary;
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
        return new TemporaryDamageModifier(change, removeOnPlay, temporary);
    }

    @Override
    public boolean removeOnCardPlayed(AbstractCard card) {
        return removeOnPlay;
    }

    public boolean removeAtEndOfTurn(AbstractCard card) {
        return temporary;
    }

    public float modifyDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage + change;
    }
}
