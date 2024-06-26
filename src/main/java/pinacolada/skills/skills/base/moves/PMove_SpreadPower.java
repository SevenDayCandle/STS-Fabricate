package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.actions.PCLActions;
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
public class PMove_SpreadPower extends PMove<PField_Power> {
    public static final PSkillData<PField_Power> DATA = register(PMove_SpreadPower.class, PField_Power.class);

    public PMove_SpreadPower() {
        this(PCLCardTarget.Self, 1);
    }

    public PMove_SpreadPower(PCLCardTarget target, int amount, PCLPowerData... powers) {
        super(DATA, target, amount);
        fields.setPower(powers);
    }

    public PMove_SpreadPower(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_SpreadPower(PCLCardTarget target, PCLPowerData... powers) {
        this(target, 0, powers);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return PGR.core.tooltips.spread.title;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String powerString = fields.getPowerSubjectString();
        String mainString = shouldActAsAll() ? TEXT.act_spread(powerString, getTargetStringPerspective(perspective)) : TEXT.act_spreadAmount(getAmountRawString(requestor), powerString, getTargetStringPerspective(perspective));
        return fields.random ? TEXT.subjects_randomly(mainString) : mainString;
    }

    protected void spreadPower(PCLUseInfo info, PCLPowerData power, PCLActions order) {
        List<? extends AbstractCreature> targets = getTargetList(info);
        // Spread amount 0 will spread the entire power
        if (power != null && (amount > 0 || baseAmount <= 0)) {
            for (AbstractCreature t : targets) {
                int actualAmount = refreshAmount(info);
                power.doFor(id -> order.spreadPower(info.source, t, id, actualAmount));
            }
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (fields.powers.isEmpty()) {
            for (PCLPowerData power : PCLPowerData.getAllData(false, p -> p.isCommon)) {
                spreadPower(info, power, order);
            }
        }
        else if (fields.random) {
            String powerID = GameUtilities.getRandomElement(fields.powers);
            PCLPowerData power = PCLPowerData.getStaticDataOrCustom(powerID);
            spreadPower(info, power, order);
        }
        else {
            for (String powerID : fields.powers) {
                PCLPowerData power = PCLPowerData.getStaticDataOrCustom(powerID);
                spreadPower(info, power, order);
            }
        }
        super.use(info, order);
    }
}
