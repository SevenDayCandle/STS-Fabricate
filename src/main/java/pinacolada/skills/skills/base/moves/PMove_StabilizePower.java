package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.IntangiblePower;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Power;
import pinacolada.utilities.GameUtilities;

import java.util.List;

@VisibleSkill
public class PMove_StabilizePower extends PMove<PField_Power> {
    public static final PSkillData<PField_Power> DATA = register(PMove_StabilizePower.class, PField_Power.class);

    public PMove_StabilizePower() {
        this(PCLCardTarget.Self, 1);
    }

    public PMove_StabilizePower(PCLCardTarget target, int amount, PCLPowerHelper... powers) {
        super(DATA, target, amount);
        fields.setPower(powers);
    }

    public PMove_StabilizePower(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_StabilizePower(PCLCardTarget target, PCLPowerHelper... powers) {
        this(target, 1, powers);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return PGR.core.tooltips.stabilize.title;
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        String powerString = fields.getPowerSubjectString();
        String mainString = TEXT.act_stabilize(powerString, getTargetStringPerspective(perspective));
        if (amount != 1) {
            mainString = (TEXT.cond_forTurns(getAmountRawString()) + ", " + mainString);
        }
        return fields.random ? TEXT.subjects_randomly(mainString) : mainString;
    }

    protected void stabilizePower(AbstractCreature p, List<? extends AbstractCreature> targets, PCLPowerHelper power, PCLActions order) {
        for (AbstractCreature t : targets) {
            order.stabilizePower(p, t, power.ID, amount);
        }
        // Handle powers that are equivalent in terms of what the player sees but that have different IDs
        if (power == PCLPowerHelper.Intangible) {
            for (AbstractCreature t : targets) {
                order.stabilizePower(p, t, IntangiblePower.POWER_ID, amount);
            }
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        List<? extends AbstractCreature> targets = getTargetList(info);
        if (fields.powers.isEmpty()) {
            for (PCLPowerHelper power : PCLPowerHelper.commonDebuffs()) {
                stabilizePower(info.source, targets, power, order);
            }
        }
        else if (fields.random) {
            PCLPowerHelper power = GameUtilities.getRandomElement(fields.powers);
            if (power != null) {
                stabilizePower(info.source, targets, power, order);
            }
        }
        else {
            for (PCLPowerHelper power : fields.powers) {
                stabilizePower(info.source, targets, power, order);
            }
        }
        super.use(info, order);
    }
}
