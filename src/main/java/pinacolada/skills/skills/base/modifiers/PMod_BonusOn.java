package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.core.Settings;
import extendedui.utilities.ColoredString;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.stances.PCLStanceHelper;

public abstract class PMod_BonusOn extends PMod
{

    public PMod_BonusOn(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_BonusOn(PSkillData data)
    {
        super(data);
    }

    public PMod_BonusOn(PSkillData data, int amount)
    {
        super(data, PCLCardTarget.None, amount);
    }

    public PMod_BonusOn(PSkillData data, int amount, int extra)
    {
        super(data, PCLCardTarget.None, amount, extra);
    }

    public PMod_BonusOn(PSkillData data, int amount, PCLStanceHelper stance)
    {
        super(data, amount, stance);
    }

    public PMod_BonusOn(PSkillData data, int amount, PCLAffinity... affinities)
    {
        super(data, PCLCardTarget.None, amount, affinities);
    }

    public abstract String getConditionSampleText();

    public String getConditionText()
    {
        return getConditionSampleText();
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.numIf("X", getConditionSampleText());
    }

    @Override
    public String getSubText()
    {
        return TEXT.conditions.numIf(getAmountRawString(), getConditionText());
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
        return be.baseAmount + (meetsCondition(info) ? amount : 0);
    }

    public abstract boolean meetsCondition(PCLUseInfo info);
}
