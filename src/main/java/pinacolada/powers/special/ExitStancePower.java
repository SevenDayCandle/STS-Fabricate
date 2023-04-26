package pinacolada.powers.special;

import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.stances.NeutralStance;
import pinacolada.actions.PCLActions;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.powers.PCLPower;
import pinacolada.resources.PGR;
import pinacolada.skills.skills.PSpecialSkill;
import pinacolada.utilities.GameUtilities;

public class ExitStancePower extends PCLPower {
    public static final String POWER_ID = PGR.core.createID(ExitStancePower.class.getSimpleName());

    public ExitStancePower(AbstractCreature owner, int amount) {
        super(owner, POWER_ID);

        createTrigger(this::onUse)
                .setCheckCondition((__) -> !GameUtilities.inStance(NeutralStance.STANCE_ID))
                .setOneUsePerPower(true);
        this.hideAmount = true;

        initialize(amount, NeutralPowertypePatch.NEUTRAL, false);
    }

    public void onUse(PSpecialSkill move, PCLUseInfo info) {
        PCLActions.bottom.changeStance(NeutralStance.STANCE_ID);
    }
}