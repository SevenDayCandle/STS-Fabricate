package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.core.Settings;
import extendedui.utilities.ColoredString;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;

public abstract class PMod_BonusPer<T extends PField> extends PMod<T>
{

    public PMod_BonusPer(PSkillData<T> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PMod_BonusPer(PSkillData<T> data)
    {
        super(data);
    }

    public PMod_BonusPer(PSkillData<T> data, int amount)
    {
        super(data, PCLCardTarget.None, amount);
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
    public int getModifiedAmount(PSkill<?> be, PCLUseInfo info)
    {
        return be.baseAmount + (multiplier(info) * amount);
    }

    public abstract int multiplier(PCLUseInfo info);
}
