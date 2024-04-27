package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import pinacolada.annotations.VisiblePower;
import pinacolada.dungeon.CombatManager;
import pinacolada.interfaces.subscribers.OnOrbChannelSubscriber;
import pinacolada.powers.PCLPowerData;
import pinacolada.powers.PCLSubscribingPower;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

@VisiblePower
public class SorceryPower extends PCLSubscribingPower implements OnOrbChannelSubscriber {
    public static final PCLPowerData DATA = register(SorceryPower.class)
            .setType(PowerType.BUFF)
            .setEndTurnBehavior(PCLPowerData.Behavior.Permanent)
            .setTooltip(PGR.core.tooltips.sorcery);

    public SorceryPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(DATA, owner, source, amount);
    }

    public float modifyOrbOutgoing(float initial) {
        return initial + amount;
    }

    @Override
    public void onChannelOrb(AbstractOrb orb) {
        GameUtilities.modifyOrbBaseFocus(orb, amount, true, false);
        CombatManager.onSpecificPowerActivated(this, owner, true);
        removePower();
    }
}
