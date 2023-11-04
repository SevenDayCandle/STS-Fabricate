package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PMove_TriggerAlly extends PMove<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PMove_TriggerAlly.class, PField_Empty.class)
            .setTargets(PCLCardTarget.AllAlly, PCLCardTarget.RandomAlly, PCLCardTarget.SingleAlly)
            .pclOnly();

    public PMove_TriggerAlly() {
        this(1);
    }

    public PMove_TriggerAlly(int amount) {
        super(DATA, PCLCardTarget.SingleAlly, amount);
    }

    public PMove_TriggerAlly(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_TriggerAlly(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_trigger(PGR.core.tooltips.summon.title);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return amount == 1 ? TEXT.act_trigger(getTargetStringPerspective(perspective)) : TEXT.act_triggerXTimes(getTargetStringPerspective(perspective), getAmountRawString());
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        for (AbstractCreature t : getTargetList(info)) {
            if (t instanceof PCLCardAlly) {
                order.triggerAlly((PCLCardAlly) t, amount);
            }
        }
        super.use(info, order);
    }
}
