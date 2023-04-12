package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveCond;

@VisibleSkill
public class PCond_CheckEnergy extends PPassiveCond<PField_Not>
{
    public static final PSkillData<PField_Not> DATA = register(PCond_CheckEnergy.class, PField_Not.class);

    public PCond_CheckEnergy(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_CheckEnergy()
    {
        super(DATA, PCLCardTarget.Self, 1);
    }

    public PCond_CheckEnergy(int amount)
    {
        super(DATA, PCLCardTarget.Self, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource)
    {
        // Use the cache when played because this check will occur after you spent energy
        return isUsing ? conditionMetCache : EnergyPanel.getCurrentEnergy() >= amount;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill)
    {
        return EUIRM.strings.numNoun(TEXT.subjects_x, PGR.core.tooltips.energy.title);
    }

    @Override
    public String getSubText()
    {
        return getTargetHasString(amount > 1 ? EUIRM.strings.numNoun(amount + "+", PGR.core.tooltips.energy) : PGR.core.tooltips.energy.toString());
    }
}
