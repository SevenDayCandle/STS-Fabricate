package pinacolada.powers.common;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.dungeon.CombatManager;
import pinacolada.interfaces.markers.MultiplicativePower;
import pinacolada.powers.PCLPower;
import pinacolada.utilities.PCLRenderHelpers;

public class CriticalPower extends PCLPower implements MultiplicativePower {
    public static final String POWER_ID = createFullID(CriticalPower.class);
    public static final int MULTIPLIER = 100;

    public CriticalPower(AbstractCreature owner, int amount) {
        super(owner, POWER_ID);

        initialize(amount, PowerType.BUFF, true);
    }

    public static float calculateDamage(float damage, float multiplier) {
        return Math.max(0, damage + Math.max(0, damage + (multiplier / 100f)));
    }

    public static float getMultiplier(int stacks) {
        return (stacks + 1) * (MULTIPLIER + CombatManager.getPlayerEffectBonus(POWER_ID));
    }

    @Override
    public float atDamageFinalGive(float damage, DamageInfo.DamageType type) {
        return type == DamageInfo.DamageType.NORMAL ? calculateDamage(damage, getMultiplier(amount)) : damage;
    }

    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (owner == AbstractDungeon.player && card.type == AbstractCard.CardType.ATTACK) {
            removePower();
        }
    }

    @Override
    public String getUpdatedDescription() {
        return formatDescription(0, PCLRenderHelpers.decimalFormat(getMultiplier(amount)));
    }

    @Override
    public void onRemoveDamagePowers() {
        removePower();
    }
}
