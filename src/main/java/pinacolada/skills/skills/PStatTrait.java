package pinacolada.skills.skills;

import pinacolada.skills.PMod;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_Not;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

public abstract class PStatTrait<T extends PField_Not> extends PTrait<T> {
    public PStatTrait(PSkillData<T> data, PSkillSaveData content) {
        super(data, content);
    }

    public PStatTrait(PSkillData<T> data) {
        super(data);
    }

    public PStatTrait(PSkillData<T> data, int amount) {
        super(data, amount);
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerNotBoolean(editor, TEXT.cedit_exact, null);
    }

    @Override
    public String wrapTextAmountSelf(int input) {
        return input >= 0 && !fields.not ? "+" + input : String.valueOf(input);
    }
}
