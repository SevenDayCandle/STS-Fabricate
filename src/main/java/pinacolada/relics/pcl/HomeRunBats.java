package pinacolada.relics.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleRelic;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.powers.PCLPowerData;
import pinacolada.powers.common.CriticalPower;
import pinacolada.relics.PCLPointerRelic;
import pinacolada.relics.PCLRelicData;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;
import pinacolada.skills.skills.PBranchCond;
import pinacolada.skills.skills.PTrigger;

@VisibleRelic
public class HomeRunBats extends PCLPointerRelic {
    public static final PCLRelicData DATA = registerTemplate(HomeRunBats.class)
            .setTier(RelicTier.SPECIAL);

    public HomeRunBats() {
        super(DATA);
    }

    public void setup() {
        addUseMove(PTrigger.when(1, PCond.onOtherCardPlayed(AbstractCard.CardType.ATTACK),
                PBranchCond.branch(PCond.payPower(PCLCardTarget.Single, 8, PCLPowerData.Mark),
                        PMove.gain(1, CriticalPower.DATA),
                        PMove.applyToSingle(8, PCLPowerData.Mark))));
    }
}