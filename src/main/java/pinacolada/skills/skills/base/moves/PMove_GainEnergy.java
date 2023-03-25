package pinacolada.skills.skills.base.moves;

import pinacolada.annotations.VisibleSkill;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PMove_GainEnergy extends PMove_Gain
{
    public static final PSkillData<PField_Empty> DATA = register(PMove_GainEnergy.class, PField_Empty.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .selfTarget();

    public PMove_GainEnergy()
    {
        this(1);
    }

    public PMove_GainEnergy(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_GainEnergy(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public String gainText()
    {
        return PGR.core.tooltips.energy.getTitleOrIcon();
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().gainEnergy(amount);
        super.use(info);
    }
}
