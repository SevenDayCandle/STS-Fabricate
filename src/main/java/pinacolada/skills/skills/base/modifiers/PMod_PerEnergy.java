package pinacolada.skills.skills.base.modifiers;

import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

import java.util.List;

import static pinacolada.skills.PSkill.PCLEffectType.General;

public class PMod_PerEnergy extends PMod
{

    public static final PSkillData DATA = register(PMod_PerEnergy.class, General).selfTarget();

    public PMod_PerEnergy(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_PerEnergy()
    {
        super(DATA);
    }

    public PMod_PerEnergy(int amount, PCLAffinity... affinities)
    {
        super(DATA, PCLCardTarget.None, amount, affinities);
    }

    public PMod_PerEnergy(int amount, List<PCLAffinity> affinities)
    {
        super(DATA, PCLCardTarget.None, amount, affinities.toArray(new PCLAffinity[]{}));
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
        String payString = (!(alt || sourceCard != null && sourceCard.energyOnUse == -1)) ? (TEXT.actions.pay("X", PGR.core.tooltips.energy) + ": ") : "";
        return payString + super.getText(addPeriod);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (this.childEffect != null)
        {
            updateChildAmount(info);
            if (alt || (sourceCard != null && sourceCard.energyOnUse == -1))
            {
                GameUtilities.useXCostEnergy(sourceCard);
            }
            this.childEffect.use(info);
        }
    }

    @Override
    public int getModifiedAmount(PSkill be, PCLUseInfo info)
    {
        return be.baseAmount * GameUtilities.getXCostEnergy(sourceCard, alt) / Math.max(1, this.amount);
    }
}
