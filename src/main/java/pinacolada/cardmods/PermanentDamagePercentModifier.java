package pinacolada.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class PermanentDamagePercentModifier extends AbstractCardModifier {
    public int change;

    public PermanentDamagePercentModifier(int change) {
        this.change = change;
    }

    public static PermanentDamagePercentModifier apply(AbstractCard c, int change) {
        PermanentDamagePercentModifier mod = get(c);
        if (mod == null) {
            mod = new PermanentDamagePercentModifier(change);
            CardModifierManager.addModifier(c, mod);
        }
        else {
            mod.change += change;
        }
        mod.onInitialApplication(c);
        return mod;
    }

    public static PermanentDamagePercentModifier get(AbstractCard c) {
        for (AbstractCardModifier mod : CardModifierManager.modifiers(c)) {
            if (mod instanceof PermanentDamagePercentModifier) {
                return (PermanentDamagePercentModifier) mod;
            }
        }
        return null;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new PermanentDamagePercentModifier(change);
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage + damage * change / 100f;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.damage = (int) (card.baseDamage + card.baseDamage * change / 100f);
        card.isDamageModified = card.baseDamage != card.damage;
    }
}
