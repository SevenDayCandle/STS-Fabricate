package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PPassiveCond;

@VisibleSkill
public class PCond_PayEnergy extends PPassiveCond<PField_Empty>
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
        return capital(TEXT.act_pay(TEXT.subjects_x, PGR.core.tooltips.energy.title), true);
    }

    @Override
    public String getSubText()
    {
        return capital(TEXT.act_pay(getAmountRawString(), PGR.core.tooltips.energy), true);
    }
}
