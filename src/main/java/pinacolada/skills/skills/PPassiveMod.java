package pinacolada.skills.skills;

import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;

public abstract class PPassiveMod<T extends PField> extends PMod<T> {
    public PPassiveMod(PSkillData<T> data, PSkillSaveData content) {
        super(data, content);
    }

    public PPassiveMod(PSkillData<T> data) {
        super(data);
    }

    public PPassiveMod(PSkillData<T> data, PCLCardTarget target, int amount) {
        super(data, target, amount);
    }

    public PPassiveMod(PSkillData<T> data, PCLCardTarget target, int amount, int extra) {
        super(data, target, amount, extra);
    }

    // Do not stack passive amounts
    @Override
    public PPassiveMod<T> stack(PSkill<?> other) {
        if (this.childEffect != null && other.getChild()  != null) {
            this.childEffect.stack(other.getChild());
        }

        return this;
    }
}
