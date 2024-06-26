package pinacolada.powers.common;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.VisiblePower;
import pinacolada.dungeon.CombatManager;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

@VisiblePower
public class WardingPower extends PCLPower {
    public static final PCLPowerData DATA = register(WardingPower.class)
            .setType(PowerType.BUFF)
            .setEndTurnBehavior(PCLPowerData.Behavior.Permanent)
            .setIsCommon(true)
            .setTooltip(PGR.core.tooltips.warding);

    public WardingPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(DATA, owner, source, amount);
    }

    @Override
    public float modifyBlock(float block, AbstractCard card) {
        return super.modifyBlock(block, card) + amount;
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        super.onUseCard(card, action);
        if (card.baseBlock > 0 || (card instanceof EditorCard && ((EditorCard) card).getCardBlock() != null)) {
            CombatManager.onSpecificPowerActivated(this, GameUtilities.getCardOwner(card), true);
            removePower();
        }
    }
}
