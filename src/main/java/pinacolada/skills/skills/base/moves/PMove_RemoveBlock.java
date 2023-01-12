package pinacolada.skills.skills.base.moves;

import pinacolada.actions.basic.RemoveBlock;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

public class PMove_RemoveBlock extends PMove<PField_Empty>
{
    public static final PSkillData<PField_Empty> DATA = register(PMove_RemoveBlock.class, PField_Empty.class);

    public PMove_RemoveBlock(PSkillSaveData content)
    {
        super(content);
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
        return TEXT.actions.remove(PGR.core.tooltips.block.title);
    }

    @Override
    public boolean isDetrimental()
    {
        return target == PCLCardTarget.Self;
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().add(new RemoveBlock(info.target, info.source)).setVFX(true, true);
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        return TEXT.actions.removeFrom(PGR.core.tooltips.block, getTargetString());
    }
}
