package pinacolada.skills.skills.base.modifiers;

import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;

@VisibleSkill
public class PMod_PerDamage extends PMod_Per<PField_Not> {

    public static final PSkillData<PField_Not> DATA = register(PMod_PerDamage.class, PField_Not.class).selfTarget();

    public PMod_PerDamage() {
        this(1);
    }

    public PMod_PerDamage(int amount) {
        super(DATA, amount);
    }

    public PMod_PerDamage(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public int getMultiplier(PCLUseInfo info) {
        return (sourceCard != null ? sourceCard.damage : 0);
    }

    @Override
    public String getSubText() {
        return TEXT.subjects_damage;
    }
}
