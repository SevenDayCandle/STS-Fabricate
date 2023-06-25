package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.IntangiblePower;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Power;
import pinacolada.utilities.GameUtilities;

import java.util.List;

@VisibleSkill
public class PMove_RemovePower extends PMove<PField_Power> {
    public static final PSkillData<PField_Power> DATA = register(PMove_RemovePower.class, PField_Power.class);

    public PMove_RemovePower() {
        this(PCLCardTarget.Self);
    }

    public PMove_RemovePower(PCLCardTarget target, PCLPowerHelper... powers) {
        super(DATA, target, 1);
        fields.setPower(powers);
    }

    public PMove_RemovePower(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.act_remove(TEXT.cedit_powers);
    }

    @Override
    public String getSubText() {
        String powerString = fields.getPowerSubjectString();
        powerString = target == PCLCardTarget.Self ? TEXT.act_remove(TEXT.subjects_onYou(powerString)) : TEXT.act_removeFrom(powerString, getTargetString());
        return fields.random ? TEXT.subjects_randomly(powerString) : powerString;
    }

    protected void removePower(List<? extends AbstractCreature> targets, PCLPowerHelper power, PCLActions order) {
        for (AbstractCreature t : targets) {
            order.removePower(t, t, power.ID);
        }
        // Handle powers that are equivalent in terms of what the player sees but that have different IDs
        if (power == PCLPowerHelper.Intangible) {
            for (AbstractCreature t : targets) {
                order.removePower(t, t, IntangiblePower.POWER_ID);
            }
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        List<? extends AbstractCreature> targets = getTargetList(info);
        if (fields.powers.isEmpty()) {
            for (PCLPowerHelper power : PCLPowerHelper.commonDebuffs()) {
                for (AbstractCreature t : targets) {
                    order.removePower(t, t, power.ID);
                }
            }
        }
        else if (fields.random) {
            PCLPowerHelper power = GameUtilities.getRandomElement(fields.powers);
            if (power != null) {
                removePower(targets, power, order);
            }
        }
        else {
            for (PCLPowerHelper power : fields.powers) {
                removePower(targets, power, order);
            }
        }
        super.use(info, order);
    }
}
