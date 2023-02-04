package pinacolada.skills.skills.base.moves;

import pinacolada.annotations.VisibleSkill;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PMove_GainOrbSlots extends PMove_Gain
{
    public static final PSkillData<PField_Empty> DATA = register(PMove_GainOrbSlots.class, PField_Empty.class)
            .selfTarget();

    public PMove_GainOrbSlots()
    {
        this(1);
    }

    public PMove_GainOrbSlots(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_GainOrbSlots(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public String gainText()
    {
        return PGR.core.tooltips.orbSlot.getTitleOrIcon();
    }

    @Override
    public String getSampleText()
    {
        return TEXT.act_gainAmount(TEXT.subjects_x, PGR.core.tooltips.orbSlot.title);
    }

    @Override
    public boolean isDetrimental()
    {
        return amount < 0;
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().gainOrbSlots(amount);
        super.use(info);
    }
}
