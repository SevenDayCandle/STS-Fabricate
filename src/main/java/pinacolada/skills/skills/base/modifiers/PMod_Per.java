package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIRM;
import extendedui.utilities.ColoredString;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;
import pinacolada.skills.skills.PPassiveMod;

public abstract class PMod_Per<T extends PField> extends PPassiveMod<T>
{
    public PMod_Per(PSkillData<T> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PMod_Per(PSkillData<T> data)
    {
        super(data);
    }

    public PMod_Per(PSkillData<T> data, int amount)
    {
        super(data, PCLCardTarget.None, amount);
    }


    public PMod_Per(PSkillData<T> data, int amount, int extra)
    {
        super(data, PCLCardTarget.None, amount, extra);
    }

    public PMod_Per(PSkillData<T> data, PCLCardTarget target, int amount)
    {
        super(data, target, amount);
    }

    public PMod_Per(PSkillData<T> data, PCLCardTarget target, int amount, int extra)
    {
        super(data, target, amount, extra);
    }

    public String getConditionText()
    {
        return this.amount <= 1 ? getSubText() : EUIRM.strings.numNoun(getAmountRawString(), getSubText());
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.per(TEXT.subjects.x, getSubText());
    }

    @Override
    public String getText(boolean addPeriod)
    {
        String subText = extra > 0 ? getConditionText() + " (" + TEXT.subjects.max(extra) + ")" : getConditionText();
        return TEXT.conditions.per(childEffect != null ? capital(childEffect.getText(false), addPeriod) : "",
                subText + getXRawString()) + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public ColoredString getColoredValueString()
    {
        if (baseAmount != amount)
        {
            return new ColoredString(amount, amount >= baseAmount ? Settings.GREEN_TEXT_COLOR : Settings.RED_TEXT_COLOR);
        }

        return new ColoredString(amount, Settings.CREAM_COLOR);
    }

    @Override
    public int getModifiedAmount(PSkill<?> be, PCLUseInfo info)
    {
        return be.baseAmount * getMultiplier(info) / Math.max(1, this.amount);
    }

    public abstract int getMultiplier(PCLUseInfo info);
}