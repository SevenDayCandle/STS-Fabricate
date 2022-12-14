package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.core.Settings;
import extendedui.utilities.ColoredString;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.stances.PCLStanceHelper;

public abstract class PMod_BonusPer extends PMod
{

    public PMod_BonusPer(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_BonusPer(PSkillData data)
    {
        super(data);
    }

    public PMod_BonusPer(PSkillData data, int amount)
    {
        super(data, PCLCardTarget.None, amount);
    }

    public PMod_BonusPer(PSkillData data, int amount, PCLStanceHelper stance)
    {
        super(data, amount, stance);
    }

    public PMod_BonusPer(PSkillData data, int amount, PCLAffinity... affinities)
    {
        super(data, PCLCardTarget.None, amount, affinities);
    }

    public PMod_BonusPer(PSkillData data, int amount, PCLOrbHelper... orbs)
    {
        super(data, PCLCardTarget.None, amount, orbs);
    }

    public abstract String getConditionSampleText();

    public String getConditionText()
    {
        return getConditionSampleText();
    }

    @Override
    public String getSampleText()
    {
        return TEXT.subjects.xBonus(TEXT.conditions.per("X", getConditionSampleText()));
    }

    @Override
    public String getSubText()
    {
        return TEXT.conditions.per(getAmountRawString(), getConditionText());
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return TEXT.conditions.genericConditional(childEffect != null ? capital(childEffect.getText(false), addPeriod) : "", getSubText()) + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public final ColoredString getColoredValueString()
    {
        if (baseAmount != amount)
        {
            return new ColoredString(amount >= 0 ? "+" + amount : amount, amount >= baseAmount ? Settings.GREEN_TEXT_COLOR : Settings.RED_TEXT_COLOR);
        }

        return new ColoredString(amount >= 0 ? "+" + amount : amount, Settings.CREAM_COLOR);
    }

    @Override
    public int getModifiedAmount(PSkill be, PCLUseInfo info)
    {
        return be.baseAmount + (multiplier(info) * amount);
    }

    public abstract int multiplier(PCLUseInfo info);
}
