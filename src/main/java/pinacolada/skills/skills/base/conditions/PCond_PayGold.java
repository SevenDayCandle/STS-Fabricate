package pinacolada.skills.skills.base.conditions;

import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

public class PCond_PayGold extends PCond<PField_Empty>
{
    public static final PSkillData<PField_Empty> DATA = register(PCond_PayGold.class, PField_Empty.class)
            .selfTarget();

    public PCond_PayGold(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_PayGold()
    {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_PayGold(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        if (info.source.gold < amount)
        {
            return false;
        }
        if (isUsing)
        {
            getActions().gainGold(-amount);
        }
        return true;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.pay(TEXT.subjects.x, PGR.core.tooltips.gold.title);
    }

    @Override
    public String getSubText()
    {
        return capital(TEXT.actions.pay(amount, PGR.core.tooltips.gold), true);
    }
}
