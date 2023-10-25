package pinacolada.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class TemporaryDamagePercentModifier extends AbstractCardModifier {
    protected transient boolean temporary;
    protected transient boolean untilPlayed;
    public int change;

    public TemporaryDamagePercentModifier(int change) {
        this(change, false, false);
    }

    public TemporaryDamagePercentModifier(int change, boolean temporary, boolean untilPlayed) {
        this.change = change;
        this.temporary = temporary;
        this.untilPlayed = untilPlayed;
    }

    public static TemporaryDamagePercentModifier apply(AbstractCard c, int change, boolean temporary, boolean untilPlayed) {
        TemporaryDamagePercentModifier mod = get(c);
        if (mod == null) {
            mod = new TemporaryDamagePercentModifier(change, temporary, untilPlayed);
            CardModifierManager.addModifier(c, mod);
        }
        else {
            mod.change += change;
            mod.temporary = mod.temporary & temporary;
            mod.untilPlayed = mod.untilPlayed & untilPlayed;
        }
        mod.onInitialApplication(c);
        return mod;
    }

    public static TemporaryDamagePercentModifier get(AbstractCard c) {
        for (AbstractCardModifier mod : CardModifierManager.modifiers(c)) {
            if (mod instanceof TemporaryDamagePercentModifier) {
                return (TemporaryDamagePercentModifier) mod;
            }
        }
        return null;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new TemporaryDamagePercentModifier(change, temporary, untilPlayed);
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

    @Override
    public boolean removeAtEndOfTurn(AbstractCard card) {
        return temporary;
    }

    @Override
    public boolean removeOnCardPlayed(AbstractCard card) {
        return untilPlayed;
    }
}
