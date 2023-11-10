package pinacolada.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class PermanentDamageModifier extends AbstractCardModifier {
    public int change;

    public PermanentDamageModifier(int change) {
        this.change = change;
    }

    public static PermanentDamageModifier apply(AbstractCard c, int change) {
        PermanentDamageModifier mod = get(c);
        if (mod == null) {
            mod = new PermanentDamageModifier(change);
            CardModifierManager.addModifier(c, mod);
        }
        else {
            mod.change += change;
        }
        mod.onInitialApplication(c);
        return mod;
    }

    public static PermanentDamageModifier get(AbstractCard c) {
        for (AbstractCardModifier mod : CardModifierManager.modifiers(c)) {
            if (mod instanceof PermanentDamageModifier) {
                return (PermanentDamageModifier) mod;
            }
        }
        return null;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new PermanentDamageModifier(change);
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage + change;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.damage = card.baseDamage + change;
        card.isDamageModified = card.baseDamage != card.damage;
    }
}
