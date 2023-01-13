package pinacolada.skills.skills.base.modifiers;

import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.utilities.GameUtilities;

// TODO better name for field
public class PMod_PerEnergy extends PMod<PField_Not>
{

    public static final PSkillData DATA = register(PMod_PerEnergy.class, PField_Not.class).selfTarget();

    public PMod_PerEnergy(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_PerEnergy()
    {
        super(DATA);
    }

    public PMod_PerEnergy(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.pay("X", PGR.core.tooltips.energy.title);
    }

    @Override
    public String getSubText()
    {
        return PGR.core.tooltips.energy.getTitleOrIcon();
    }

    @Override
    public String getText(boolean addPeriod)
    {
        String payString = (!(fields.not || sourceCard != null && sourceCard.energyOnUse == -1)) ? (TEXT.actions.pay("X", PGR.core.tooltips.energy) + ": ") : "";
        return payString + super.getText(addPeriod);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (this.childEffect != null)
        {
            updateChildAmount(info);
            if (fields.not || (sourceCard != null && sourceCard.energyOnUse == -1))
            {
                GameUtilities.useXCostEnergy(sourceCard);
            }
            this.childEffect.use(info);
        }
    }

    @Override
    public int getModifiedAmount(PSkill<?> be, PCLUseInfo info)
    {
        return be.baseAmount * GameUtilities.getXCostEnergy(sourceCard, fields.not) / Math.max(1, this.amount);
    }
}
