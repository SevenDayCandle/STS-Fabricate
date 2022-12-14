package pinacolada.skills.skills.base.conditions;

import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PCond_PayGold extends PCond
{
    public static final PSkillData DATA = register(PCond_PayGold.class, PCLEffectType.General)
            .selfTarget();

    public PCond_PayGold(PSkillSaveData content)
    {
        super(content);
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
        return TEXT.actions.pay("X", PGR.core.tooltips.gold.title);
    }

    @Override
    public String getSubText()
    {
        return capital(TEXT.actions.pay(amount, PGR.core.tooltips.gold), true);
    }
}
