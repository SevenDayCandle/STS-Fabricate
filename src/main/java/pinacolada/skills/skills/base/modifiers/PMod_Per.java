package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIRM;
import extendedui.utilities.ColoredString;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveMod;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

public abstract class PMod_Per<T extends PField_Not> extends PPassiveMod<T> {
    public PMod_Per(PSkillData<T> data, PSkillSaveData content) {
        super(data, content);
    }

    public PMod_Per(PSkillData<T> data) {
        super(data);
    }

    public PMod_Per(PSkillData<T> data, int amount) {
        super(data, PCLCardTarget.None, amount);
    }


    public PMod_Per(PSkillData<T> data, int amount, int extra) {
        super(data, PCLCardTarget.None, amount, extra);
    }

    public PMod_Per(PSkillData<T> data, PCLCardTarget target, int amount) {
        super(data, target, amount);
    }

    public PMod_Per(PSkillData<T> data, PCLCardTarget target, int amount, int extra) {
        super(data, target, amount, extra);
    }

    @Override
    public ColoredString getColoredValueString() {
        String amString = fields.not && amount >= 0 ? "+" + amount : String.valueOf(amount);
        if (baseAmount != amount) {
            return new ColoredString(amString, amount >= baseAmount ? Settings.GREEN_TEXT_COLOR : Settings.RED_TEXT_COLOR);
        }

        return new ColoredString(amString, Settings.CREAM_COLOR);
    }

    @Override
    public String getText(boolean addPeriod) {
        String appendix = extra > 0 ? " (" + TEXT.subjects_max(extra) + ")" + getXRawString() : getXRawString();
        String childText = childEffect != null ? capital(childEffect.getText(false), addPeriod) : "";
        return getConditionText(childText) + appendix + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public int getModifiedAmount(PSkill<?> be, PCLUseInfo info, boolean isUsing) {
        return fields.not ? (be.baseAmount + (getMultiplier(info, isUsing) * amount)) : be.baseAmount * getMultiplier(info, isUsing) / Math.max(1, this.amount);
    }

    public String getConditionText(String childText) {
        if (fields.not) {
            return TEXT.cond_xConditional(childText, TEXT.cond_xPerY(getAmountRawString(), getSubText()));
        }
        return TEXT.cond_xPerY(childText,
                this.amount <= 1 ? getSubText() : EUIRM.strings.numNoun(getAmountRawString(), getSubText()));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.cond_xPerY(TEXT.subjects_x, getSubSampleText());
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerNotBoolean(editor, StringUtils.capitalize(TEXT.subjects_bonus), TEXT.cetut_bonus);
    }

    @Override
    public String wrapAmount(int input) {
        return input >= 0 && fields.not ? "+" + input : String.valueOf(input);
    }

    public String getSubSampleText() {
        return getSubText();
    }

    public abstract int getMultiplier(PCLUseInfo info, boolean isUsing);
}
