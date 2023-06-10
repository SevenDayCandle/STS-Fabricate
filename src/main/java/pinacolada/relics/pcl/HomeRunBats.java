package pinacolada.relics.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleRelic;
import pinacolada.powers.PCLPowerHelper;
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
                PBranchCond.branch(PCond.checkPowerSingle(2, PCLPowerHelper.LockOn),
                        PMove.gain(1, PCLPowerHelper.Critical),
                        PMove.applyToSingle(1, PCLPowerHelper.LockOn))));
    }
}