package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.actions.PCLActions;
import pinacolada.actions.powers.StabilizePowerAction;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.powers.PCLPowerData;
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

    public PMove_StabilizePower(PCLCardTarget target, int amount, PCLPowerData... powers) {
        super(DATA, target, amount);
        fields.setPower(powers);
    }

    public PMove_StabilizePower(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_StabilizePower(PCLCardTarget target, PCLPowerData... powers) {
        this(target, 1, powers);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return PGR.core.tooltips.stabilize.title;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String powerString = fields.getPowerSubjectString();
        String mainString = TEXT.act_zOn(PGR.core.tooltips.stabilize.title, powerString, getTargetStringPerspective(perspective));
        if (amount != 1) {
            mainString = (TEXT.cond_forTurns(getAmountRawString(requestor)) + ", " + mainString);
        }
        return fields.random ? TEXT.subjects_randomly(mainString) : mainString;
    }

    protected void stabilizePowerAsNew(PCLUseInfo info, PCLPowerData power, PCLActions order) {
        if (power != null) {
            List<? extends AbstractCreature> targets = getTargetListAsNew(info);
            for (AbstractCreature t : targets) {
                AbstractPower sample = power.create(t, t, 1); // Only needed for the StabilizePowerAction reference
                power.doFor(po -> order.add(new StabilizePowerAction(info.source, t, po, refreshAmount(info)).setPower(sample)));
            }
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (fields.powers.isEmpty()) {
            List<? extends AbstractCreature> targets = getTargetListAsNew(info);
            for (PCLPowerData power : PCLPowerData.getAllData(false, c -> c.isCommon && c.isDebuff())) {
                for (AbstractCreature t : targets) {
                    power.doFor(po -> order.add(new StabilizePowerAction(info.source, t, po, refreshAmount(info))));
                }
            }
        }
        else if (fields.random) {
            String powerID = GameUtilities.getRandomElement(fields.powers);
            PCLPowerData power = PCLPowerData.getStaticDataOrCustom(powerID);
            stabilizePowerAsNew(info, power, order);
        }
        else {
            for (String powerID : fields.powers) {
                PCLPowerData power = PCLPowerData.getStaticDataOrCustom(powerID);
                stabilizePowerAsNew(info, power, order);
            }
        }
        super.use(info, order);
    }
}
