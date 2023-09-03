package pinacolada.powers.common;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.annotations.VisiblePower;
import pinacolada.dungeon.CombatManager;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PGR;
import pinacolada.utilities.PCLRenderHelpers;

@VisiblePower
public class CriticalPower extends PCLPower {
    public static final PCLPowerData DATA = register(CriticalPower.class)
            .setType(PowerType.BUFF)
            .setEndTurnBehavior(PCLPowerData.Behavior.Permanent)
            .setTooltip(PGR.core.tooltips.critical);
    public static final int MULTIPLIER = 100;

    public CriticalPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(DATA, owner, source, amount);
    }

    public static float calculateDamage(float damage, float multiplier) {
        return Math.max(0, damage + Math.max(0, damage + (multiplier / 100f)));
    }

    public static float getMultiplier(int stacks) {
        return (stacks + 1) * (MULTIPLIER + CombatManager.getPlayerEffectBonus(DATA.ID));
    }

    @Override
    public float atDamageFinalGive(float damage, DamageInfo.DamageType type) {
        return type == DamageInfo.DamageType.NORMAL ? calculateDamage(damage, getMultiplier(amount)) : damage;
    }

    @Override
    public String getUpdatedDescription() {
        return formatDescription(0, PCLRenderHelpers.decimalFormat(getMultiplier(amount)));
    }

    @Override
    public void onRemoveDamagePowers() {
        removePower();
    }

    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (owner == AbstractDungeon.player && card.type == AbstractCard.CardType.ATTACK) {
            removePower();
        }
    }
}
