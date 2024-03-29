package pinacolada.skills;

import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.skills.fields.PField;
import pinacolada.ui.editor.PCLCustomEffectPage;
import pinacolada.ui.editor.nodes.PCLCustomEffectNode;

public abstract class PPrimary<T extends PField> extends PSkill<T> {
    public PPrimary(PSkillData<T> data, PSkillSaveData content) {
        super(data, content);
    }

    public PPrimary(PSkillData<T> data) {
        super(data);
    }

    public PPrimary(PSkillData<T> data, PCLCardTarget target, int amount) {
        super(data, target, amount);
    }

    public PPrimary(PSkillData<T> data, PCLCardTarget target, int amount, int extra) {
        super(data, target, amount, extra);
    }

    public boolean isSkillAllowed(PSkill<?> skill, PCLCustomEffectPage editor, PCLCustomEffectNode node) {
        return true;
    }

    public boolean shouldUseWhenText() {
        return false;
    }
}
