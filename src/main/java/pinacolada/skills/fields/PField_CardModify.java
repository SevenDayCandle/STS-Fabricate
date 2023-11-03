package pinacolada.skills.fields;

import pinacolada.resources.PGR;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

public class PField_CardModify extends PField_CardCategory {
    public boolean or;

    public PField_CardModify() {
        super();
    }

    public PField_CardModify(PField_CardModify other) {
        super(other);
        setOr(other.or);
    }

    @Override
    public boolean equals(PField other) {
        return super.equals(other) && ((PField_CardModify) other).or == this.or;
    }

    @Override
    public PField_CardModify makeCopy() {
        return new PField_CardModify(this);
    }

    public void registerOrBoolean(PCLCustomEffectEditingPane editor) {
        registerOrBoolean(editor, PGR.core.strings.cedit_or, null);
    }

    public void registerOrBoolean(PCLCustomEffectEditingPane editor, String name, String desc) {
        editor.registerBoolean(name, desc, v -> or = v, or);
    }

    public PField_CardModify setOr(boolean value) {
        this.or = value;
        return this;
    }
}
