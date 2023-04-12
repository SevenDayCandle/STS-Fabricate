package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.core.Settings;
import extendedui.utilities.ColoredString;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;
import pinacolada.skills.skills.PPassiveMod;

public abstract class PMod_BonusOn<T extends PField> extends PPassiveMod<T>
{

    public PMod_BonusOn(PSkillData<T> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PMod_BonusOn(PSkillData<T> data)
    {
        super(data);
    }

    public PMod_BonusOn(PSkillData<T> data, int amount)
    {
        super(data, PCLCardTarget.None, amount);
    }

    public PMod_BonusOn(PSkillData<T> data, int amount, int extra)
    {
        super(data, PCLCardTarget.None, amount, extra);
    }

    public String getConditionText()
    {
        return getSubText();
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill)
    {
        return TEXT.cond_numIf(TEXT.subjects_x, getSubText());
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return TEXT.cond_genericConditional(childEffect != null ? capital(childEffect.getText(false), addPeriod) : "", TEXT.cond_numIf(getAmountRawString(), getConditionText())) + PCLCoreStrings.period(addPeriod);
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
        return be.baseAmount + (meetsCondition(info) ? amount : 0);
    }

    public abstract boolean meetsCondition(PCLUseInfo info);
}
