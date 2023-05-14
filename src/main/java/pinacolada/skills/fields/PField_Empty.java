package pinacolada.skills.fields;

import pinacolada.ui.editor.PCLCustomEffectEditingPane;

public class PField_Empty extends PField {
    @Override
    public boolean equals(PField other) {
        return other instanceof PField_Empty;
    }

    @Override
    public PField_Empty makeCopy() {
        return new PField_Empty();
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
    }
}
