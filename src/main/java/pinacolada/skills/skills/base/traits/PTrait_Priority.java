package pinacolada.skills.skills.base.traits;

import pinacolada.annotations.VisibleSkill;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PTrait_Priority extends PTrait<PField_Empty>
{

    public static final PSkillData<PField_Empty> DATA = register(PTrait_Priority.class, PField_Empty.class);

    public PTrait_Priority()
    {
        this(1);
    }

    public PTrait_Priority(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PTrait_Priority(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public String getSubDescText()
    {
        return PGR.core.tooltips.priority.title;
    }

    @Override
    public String getSubSampleText()
    {
        return PGR.core.tooltips.priority.title;
    }

    @Override
    public float modifyMagicNumber(PCLUseInfo info, float amount)
    {
        return amount + this.amount;
    }
}
