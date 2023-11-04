package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.actions.GameActionManager;
import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveCond;

@VisibleSkill
public class PCond_TurnCount extends PPassiveCond<PField_Not> {

    public static final PSkillData<PField_Not> DATA = register(PCond_TurnCount.class, PField_Not.class)
            .noTarget();

    public PCond_TurnCount() {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_TurnCount(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return fields.doesValueMatchThreshold(GameActionManager.turn);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return EUIRM.strings.numNoun(TEXT.subjects_x, PGR.core.strings.subjects_turnCount);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return fields.getThresholdRawString(PGR.core.strings.subjects_turnCount);
    }
}
