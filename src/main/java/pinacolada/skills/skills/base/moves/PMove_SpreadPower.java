package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.IntangiblePower;
import com.megacrit.cardcrawl.powers.LockOnPower;
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
    public String getSubText(PCLCardTarget perspective) {
        String powerString = fields.getPowerSubjectString();
        String mainString = shouldActAsAll() ? TEXT.act_spread(powerString, getTargetStringPerspective(perspective)) : TEXT.act_spreadAmount(getAmountRawString(), powerString, getTargetStringPerspective(perspective));
        return fields.random ? TEXT.subjects_randomly(mainString) : mainString;
    }

    protected void spreadPower(AbstractCreature p, List<? extends AbstractCreature> targets, PCLPowerData power, PCLActions order) {
        // Spread amount 0 will spread the entire power
        if (power != null && (amount > 0 || baseAmount <= 0)) {
            for (AbstractCreature t : targets) {
                order.spreadPower(p, t, power.ID, amount);
            }
            // Handle powers that are equivalent in terms of what the player sees but that have different IDs
            if (power == PCLPowerData.Intangible) {
                for (AbstractCreature t : targets) {
                    order.spreadPower(p, t, IntangiblePower.POWER_ID, amount);
                }
            }
            else if (power == PCLPowerData.LockOn) {
                for (AbstractCreature t : targets) {
                    order.spreadPower(p, t, LockOnPower.POWER_ID, amount);
                }
            }
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        List<? extends AbstractCreature> targets = getTargetList(info);
        if (fields.powers.isEmpty()) {
            for (PCLPowerData power : PCLPowerData.getAllData(false, p -> p.isCommon)) {
                spreadPower(info.source, targets, power, order);
            }
        }
        else if (fields.random) {
            String powerID = GameUtilities.getRandomElement(fields.powers);
            PCLPowerData power = PCLPowerData.getStaticDataOrCustom(powerID);
            spreadPower(info.source, targets, power, order);
        }
        else {
            for (String powerID : fields.powers) {
                PCLPowerData power = PCLPowerData.getStaticDataOrCustom(powerID);
                spreadPower(info.source, targets, power, order);
            }
        }
        super.use(info, order);
    }
}
