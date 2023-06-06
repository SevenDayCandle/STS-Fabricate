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

    public String getThresholdRawString(String subject) {
        return getThresholdRawString(skill.getAmountRawString(), subject, skill.baseAmount);
    }

    public String getThresholdRawString(String valueStr, String subject, int amount) {
        return amount == 1 && !not ? subject : EUIRM.strings.numNoun(valueStr, subject);
    }

    public String getThresholdValString(int amount) {
        if (not && amount == 0) {
            return String.valueOf(amount);
        }
        return amount + (not ? "-" : "+");
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
