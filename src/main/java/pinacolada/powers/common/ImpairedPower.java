package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import pinacolada.annotations.VisiblePower;
import pinacolada.dungeon.CombatManager;
import pinacolada.interfaces.subscribers.OnOrbApplyFocusSubscriber;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

@VisiblePower
public class ImpairedPower extends PCLPower implements OnOrbApplyFocusSubscriber {
    public static final PCLPowerData DATA = register(ImpairedPower.class)
            .setType(PowerType.DEBUFF)
            .setEndTurnBehavior(PCLPowerData.Behavior.TurnBased)
            .setTooltip(PGR.core.tooltips.impaired);
    public static final int MULTIPLIER = 50;

    public ImpairedPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(DATA, owner, source, amount);
    };

    public ImpairedPower(AbstractCreature owner, AbstractCreature source, int amount, boolean isSourceMonster) {
        super(DATA, owner, source, amount);
        justApplied = isSourceMonster;
    };

    public static float getOrbMultiplier() {
        return (MULTIPLIER + CombatManager.getPlayerEffectBonus(DATA.ID));
    }

    @Override
    public String getUpdatedDescription() {
        return formatDescription(0, PCLRenderHelpers.decimalFormat(getOrbMultiplier()), amount, amount == 1 ? powerStrings.DESCRIPTIONS[1] : powerStrings.DESCRIPTIONS[2]);
    }

    @Override
    public float modifyOrbOutgoing(float initial) {
        return initial * Math.max(0, getOrbMultiplier() / 100f);
    }

    @Override
    public void onApplyFocus(AbstractOrb orb) {
        if (GameUtilities.canOrbApplyFocus(orb)) {
            orb.passiveAmount = (int) modifyOrbOutgoing(orb.passiveAmount);
            if (GameUtilities.canOrbApplyFocusToEvoke(orb)) {
                orb.evokeAmount = (int) modifyOrbOutgoing(orb.evokeAmount);
            }
        }
    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();

        CombatManager.subscribe(this);
    }

    @Override
    public void onRemove() {
        super.onRemove();

        CombatManager.unsubscribe(this);
    }
}