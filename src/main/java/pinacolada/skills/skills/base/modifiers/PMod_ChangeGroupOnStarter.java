package pinacolada.skills.skills.base.modifiers;

import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardGeneric;

@VisibleSkill
public class PMod_ChangeGroupOnStarter extends PMod_ChangeGroup {

    public static final PSkillData<PField_CardGeneric> DATA = register(PMod_ChangeGroupOnStarter.class, PField_CardGeneric.class).selfTarget();

    public PMod_ChangeGroupOnStarter(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_ChangeGroupOnStarter() {
        super(DATA);
    }

    public PMod_ChangeGroupOnStarter(PCLCardGroupHelper... groups) {
        super(DATA, groups);
    }

    @Override
    public String getConditionSampleText() {
        return PGR.core.tooltips.starter.title;
    }

    @Override
    public boolean meetsCondition(PCLUseInfo info) {
        return info.isStarter;
    }
}
