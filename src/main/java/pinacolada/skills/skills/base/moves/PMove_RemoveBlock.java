package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PMove_RemoveBlock extends PMove<PField_Empty>
{
    public static final PSkillData<PField_Empty> DATA = register(PMove_RemoveBlock.class, PField_Empty.class);

    public PMove_RemoveBlock(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_RemoveBlock()
    {
        super(DATA, PCLCardTarget.Single, 1);
    }

    public PMove_RemoveBlock(PCLCardTarget target)
    {
        super(DATA, target, 1);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.act_remove(PGR.core.tooltips.block.title);
    }

    @Override
    public boolean isDetrimental()
    {
        return target == PCLCardTarget.Self;
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().add(new RemoveAllBlockAction(info.target, info.source));
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        return TEXT.act_removeFrom(PGR.core.tooltips.block, getTargetString());
    }
}
