package pinacolada.skills.skills.base.conditions;

import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

import java.util.List;

public class PCond_PayPower extends PCond
{
    public static final PSkillData DATA = register(PCond_PayPower.class, PCLEffectType.Power)
            .selfTarget();

    public PCond_PayPower(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_PayPower()
    {
        super(DATA, PCLCardTarget.None, 1, new PCLAffinity[]{});
    }

    public PCond_PayPower(int amount, PCLPowerHelper... powers)
    {
        super(DATA, PCLCardTarget.None, amount, powers);
    }

    public PCond_PayPower(int amount, List<PCLPowerHelper> powers)
    {
        super(DATA, PCLCardTarget.None, amount, powers.toArray(new PCLPowerHelper[]{}));
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        for (PCLPowerHelper power : powers)
        {
            if (GameUtilities.getPowerAmount(power.ID) < amount)
            {
                return false;
            }
        }
        if (isUsing)
        {
            for (PCLPowerHelper power : powers)
            {
                getActions().applyPower(PCLCardTarget.Self, power, -amount);
            }
        }
        return true;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.pay("X", TEXT.cardEditor.powers);
    }

    @Override
    public String getSubText()
    {
        return capital(TEXT.actions.pay(amount, powers.isEmpty()
                ? plural(PGR.core.tooltips.debuff) :
                getPowerAndString()), true);
    }
}
