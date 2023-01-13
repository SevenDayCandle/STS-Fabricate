package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

public class PCond_PayEnergy extends PCond<PField_Empty>
{
    public static final PSkillData<PField_Empty> DATA = register(PCond_PayEnergy.class, PField_Empty.class)
            .selfTarget();

    public PCond_PayEnergy(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_PayEnergy()
    {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_PayEnergy(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        if (EnergyPanel.getCurrentEnergy() < amount)
        {
            return false;
        }
        if (isUsing)
        {
            getActions().spendEnergy(amount, false);
        }
        return true;
    }

    @Override
    public String getSampleText()
    {
        return capital(TEXT.actions.pay("X", PGR.core.tooltips.energy.title), true);
    }

    @Override
    public String getSubText()
    {
        return capital(TEXT.actions.pay(getAmountRawString(), PGR.core.tooltips.energy), true);
    }
}
