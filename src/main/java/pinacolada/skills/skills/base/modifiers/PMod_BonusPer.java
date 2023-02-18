package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.core.Settings;
import extendedui.utilities.ColoredString;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;
import pinacolada.skills.skills.PPassiveMod;

public abstract class PMod_BonusPer<T extends PField> extends PPassiveMod<T>
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

    public String getConditionText()
    {
        return getSubText();
    }

    @Override
    public String getSampleText()
    {
        return TEXT.subjects_xBonus(TEXT.cond_per(TEXT.subjects_x, getSubText()));
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return TEXT.cond_genericConditional(childEffect != null ? capital(childEffect.getText(false), addPeriod) : "", TEXT.cond_per(getAmountRawString(), getConditionText())) + PCLCoreStrings.period(addPeriod);
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
        return be.baseAmount + (getMultiplier(info) * amount);
    }

    public abstract int getMultiplier(PCLUseInfo info);
}
