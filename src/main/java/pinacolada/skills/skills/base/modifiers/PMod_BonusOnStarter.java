package pinacolada.skills.skills.base.modifiers;

import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PMod_BonusOnStarter extends PMod_BonusOn<PField_Empty> {

    public static final PSkillData<PField_Empty> DATA = register(PMod_BonusOnStarter.class, PField_Empty.class).noTarget();

    public PMod_BonusOnStarter(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_BonusOnStarter() {
        this(0);
    }

    public PMod_BonusOnStarter(int amount) {
        super(DATA, amount);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return PGR.core.tooltips.starter.title;
    }

    @Override
    public boolean meetsCondition(PCLUseInfo info, boolean isUsing) {
        return info.isStarter;
    }
}
