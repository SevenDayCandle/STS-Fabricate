package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PMove_RemoveBlock extends PMove<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PMove_RemoveBlock.class, PField_Empty.class);

    public PMove_RemoveBlock(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_RemoveBlock() {
        super(DATA, PCLCardTarget.Single, 1);
    }

    public PMove_RemoveBlock(PCLCardTarget target) {
        super(DATA, target, 1);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_remove(PGR.core.tooltips.block.title);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return TEXT.act_removeFrom(PGR.core.tooltips.block, getTargetStringPerspective(perspective));
    }

    @Override
    public boolean isDetrimental() {
        return target == PCLCardTarget.Self;
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        for (AbstractCreature c : getTargetList(info)) {
            order.add(new RemoveAllBlockAction(c, info.source));
        }
        super.use(info, order);
    }
}
