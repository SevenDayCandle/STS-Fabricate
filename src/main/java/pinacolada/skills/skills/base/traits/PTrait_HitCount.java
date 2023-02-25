package pinacolada.skills.skills.base.traits;

import pinacolada.annotations.VisibleSkill;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PTrait_HitCount extends PTrait<PField_Empty>
{

    public static final PSkillData<PField_Empty> DATA = register(PTrait_HitCount.class, PField_Empty.class);

    public PTrait_HitCount()
    {
        this(1);
    }

    public PTrait_HitCount(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PTrait_HitCount(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public String getSubDescText()
    {
        return TEXT.subjects_hits;
    }

    @Override
    public String getSubSampleText()
    {
        return TEXT.subjects_hits;
    }

    @Override
    public float modifyHitCount(PCLUseInfo info, float amount)
    {
        return amount + this.amount;
    }

    @Override
    public boolean isDetrimental()
    {
        return amount < 0;
    }
}
