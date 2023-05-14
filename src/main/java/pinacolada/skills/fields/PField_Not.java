package pinacolada.skills.fields;

import extendedui.EUIRM;
import pinacolada.resources.PGR;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

public class PField_Not extends PField {
    public boolean not;

    public boolean doesValueMatchThreshold(int input) {
        return doesValueMatchThreshold(input, skill.amount);
    }

    public boolean doesValueMatchThreshold(int input, int threshold) {
        return not ? input <= threshold : input >= threshold;
    }

    @Override
    public boolean equals(PField other) {
        return other instanceof PField_Not && not == ((PField_Not) other).not;
    }

    @Override
    public PField_Not makeCopy() {
        return new PField_Not().setNot(not);
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
    }

    public String getThresholdString(String subject) {
        return getThresholdString(skill.getAmountRawString(), subject, skill.baseAmount);
    }

    public String getThresholdString(String valueStr, String subject, int amount) {
        return amount == 1 && !not ? subject : EUIRM.strings.numNoun(getThresholdValString(valueStr, amount), subject);
    }

    public String getThresholdValString() {
        return getThresholdValString(skill.getAmountRawString(), skill.baseAmount);
    }

    public String getThresholdValString(String valueStr, int amount) {
        if (not && amount == 0) {
            return valueStr;
        }
        return valueStr + (not ? "-" : "+");
    }

    public void registerNotBoolean(PCLCustomEffectEditingPane editor) {
        editor.registerBoolean(PGR.core.strings.cedit_not, v -> not = v, not);
    }

    public void registerNotBoolean(PCLCustomEffectEditingPane editor, String name, String desc) {
        editor.registerBoolean(name, desc, v -> not = v, not);
    }

    public PField_Not setNot(boolean value) {
        this.not = value;
        return this;
    }
}
