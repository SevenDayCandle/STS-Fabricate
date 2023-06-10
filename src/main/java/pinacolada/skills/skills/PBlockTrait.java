package pinacolada.skills.skills;

import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;

public abstract class PBlockTrait<T extends PField> extends PTrait<T> {
    public PBlockTrait(PSkillData<T> data, PSkillSaveData content) {
        super(data, content);
    }

    public PBlockTrait(PSkillData<T> data) {
        super(data);
    }

    public PBlockTrait(PSkillData<T> data, int amount) {
        super(data, amount);
    }
}
