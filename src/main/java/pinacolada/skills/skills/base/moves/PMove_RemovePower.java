package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.IntangiblePower;
import com.megacrit.cardcrawl.powers.LockOnPower;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.powers.PCLPowerData;
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

    public PMove_RemovePower(PCLCardTarget target, PCLPowerData... powers) {
        super(DATA, target, 1);
        fields.setPower(powers);
    }

    public PMove_RemovePower(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_remove(TEXT.cedit_powers);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        String powerString = fields.getPowerSubjectString();
        powerString = target == PCLCardTarget.Self && perspective == PCLCardTarget.Self ? TEXT.act_remove(TEXT.subjects_onYou(powerString)) : TEXT.act_removeFrom(powerString, getTargetStringPerspective(perspective));
        return fields.random ? TEXT.subjects_randomly(powerString) : powerString;
    }

    protected void removePower(List<? extends AbstractCreature> targets, PCLPowerData power, PCLActions order) {
        if (power != null) {
            for (AbstractCreature t : targets) {
                power.doFor(id -> order.removePower(t, t, id));
                order.removePower(t, t, power.ID);
            }
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        List<? extends AbstractCreature> targets = getTargetList(info);
        if (fields.powers.isEmpty()) {
            for (PCLPowerData power : PCLPowerData.getAllData(false, p -> !p.isDebuff() ^ fields.debuff)) {
                for (AbstractCreature t : targets) {
                    order.removePower(t, t, power.ID);
                }
            }
        }
        else if (fields.random) {
            String powerID = GameUtilities.getRandomElement(fields.powers);
            PCLPowerData power = PCLPowerData.getStaticDataOrCustom(powerID);
            removePower(targets, power, order);
        }
        else {
            for (String powerID : fields.powers) {
                PCLPowerData power = PCLPowerData.getStaticDataOrCustom(powerID);
                removePower(targets, power, order);
            }
        }
        super.use(info, order);
    }
}
