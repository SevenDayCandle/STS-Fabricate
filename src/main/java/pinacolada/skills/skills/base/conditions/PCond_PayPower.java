package pinacolada.skills.skills.base.conditions;

import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Power;
import pinacolada.utilities.GameUtilities;

public class PCond_PayPower extends PCond<PField_Power>
{
    public static final PSkillData<PField_Power> DATA = register(PCond_PayPower.class, PField_Power.class)
            .selfTarget();

    public PCond_PayPower(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_PayPower()
    {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_PayPower(int amount, PCLPowerHelper... powers)
    {
        super(DATA, PCLCardTarget.None, amount);
        fields.setPower(powers);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        for (PCLPowerHelper power : fields.powers)
        {
            if (GameUtilities.getPowerAmount(power.ID) < amount)
            {
                return false;
            }
        }
        if (isUsing)
        {
            for (PCLPowerHelper power : fields.powers)
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
        return capital(TEXT.actions.pay(amount, fields.powers.isEmpty()
                ? plural(PGR.core.tooltips.debuff) :
                fields.getPowerAndString()), true);
    }
}
