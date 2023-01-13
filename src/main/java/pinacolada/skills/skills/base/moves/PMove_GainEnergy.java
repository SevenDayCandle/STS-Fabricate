package pinacolada.skills.skills.base.moves;

import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

public class PMove_GainEnergy extends PMove_Gain
{
    public static final PSkillData<PField_Empty> DATA = register(PMove_GainEnergy.class, PField_Empty.class)
            .selfTarget();

    public PMove_GainEnergy()
    {
        this(1);
    }

    public PMove_GainEnergy(PSkillSaveData content)
    {
        super(content);
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
