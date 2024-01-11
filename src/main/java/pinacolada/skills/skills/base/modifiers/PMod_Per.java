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

    public String getConditionText(PCLCardTarget perspective, Object requestor, String childText) {
        if (fields.not) {
            return TEXT.cond_xConditional(childText, TEXT.cond_xPerY(getAmountRawString(), getSubText(perspective, requestor)));
        }
        return TEXT.cond_xPerY(childText,
                this.amount <= 1 ? getSubText(perspective, requestor) : EUIRM.strings.numNoun(getAmountRawString(), getSubText(perspective, requestor)));
    }

    @Override
    public int getModifiedAmount(PCLUseInfo info, int baseAmount, boolean isUsing) {
        return fields.not ? (baseAmount + (getMultiplier(info, isUsing) * amount)) : baseAmount * getMultiplier(info, isUsing) / Math.max(1, this.amount);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.cond_xPerY(TEXT.subjects_x, getSubSampleText());
    }

    public String getSubSampleText() {
        return getSubText(PCLCardTarget.Self, null);
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        String appendix = extra > 0 ? " (" + TEXT.subjects_max(extra) + ")" + getXRawString() : getXRawString();
        String childText = childEffect != null ? capital(childEffect.getText(perspective, requestor, false), addPeriod) : "";
        return getConditionText(perspective, requestor, childText) + appendix + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerNotBoolean(editor, StringUtils.capitalize(TEXT.subjects_bonus), TEXT.cetut_bonus);
    }

    @Override
    public String wrapTextAmountSelf(int input) {
        return input >= 0 && fields.not ? "+" + input : String.valueOf(input);
    }

    public abstract int getMultiplier(PCLUseInfo info, boolean isUsing);
}
