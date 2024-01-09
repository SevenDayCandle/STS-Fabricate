package pinacolada.skills.skills;

import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;

// Mods where the actual calculated amount is calculated in the use function
public abstract class PActiveMod<T extends PField> extends PMod<T> {
    public PActiveMod(PSkillData<T> data, PSkillSaveData content) {
        super(data, content);
    }

    public PActiveMod(PSkillData<T> data) {
        super(data);
    }

    public PActiveMod(PSkillData<T> data, PCLCardTarget target, int amount) {
        super(data, target, amount);
    }

    public PActiveMod(PSkillData<T> data, PCLCardTarget target, int amount, int extra) {
        super(data, target, amount, extra);
    }
}
