package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

public class PMod_PerEnergy extends PMod<PField_Empty>
{
    public static final PSkillData<PField_Empty> DATA = register(PMod_PerEnergy.class, PField_Empty.class).selfTarget();

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
        return TEXT.conditions.per(TEXT.subjects.x, PGR.core.tooltips.energy.title);
    }

    @Override
    public String getSubText()
    {
        return PGR.core.tooltips.energy.getTitleOrIcon();
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (this.childEffect != null)
        {
            updateChildAmount(info);
            this.childEffect.use(info);
        }
    }

    @Override
    public int getModifiedAmount(PSkill<?> be, PCLUseInfo info)
    {
        return be.baseAmount * EnergyPanel.getCurrentEnergy() / Math.max(1, this.amount);
    }
}
