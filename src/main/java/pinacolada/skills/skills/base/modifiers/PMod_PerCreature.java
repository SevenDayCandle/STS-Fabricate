package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Creature;
import pinacolada.skills.fields.PField_Not;

@VisibleSkill
public class PMod_PerCreature extends PMod_Per<PField_Creature> {

    public static final PSkillData<PField_Creature> DATA = register(PMod_PerCreature.class, PField_Creature.class);

    public PMod_PerCreature(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerCreature() {
        super(DATA);
    }

    public PMod_PerCreature(int amount) {
        super(DATA, PCLCardTarget.AllEnemy, amount);
    }

    public PMod_PerCreature(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return fields.creatures.isEmpty() ? getTargetList(info).size() : EUIUtils.count(getTargetList(info), fields::filter);
    }

    public String getSubSampleText() {
        return TEXT.subjects_character;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return fields.creatures.isEmpty() ? getTargetStringPluralSuffix() : fields.getString();
    }
}
