package pinacolada.skills.skills.base.conditions;

import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnEndOfTurnFirstSubscriber;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PDelegateCond;

@VisibleSkill
public class PCond_AtTurnEnd extends PDelegateCond<PField_Empty> implements OnEndOfTurnFirstSubscriber {

    public static final PSkillData<PField_Empty> DATA = register(PCond_AtTurnEnd.class, PField_Empty.class, 1, 1)
            .selfTarget();

    public PCond_AtTurnEnd() {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_AtTurnEnd(PSkillSaveData content) {
        super(DATA, content);
    }

    // This should not activate the child effect when played normally

    @Override
    public boolean canPlay(PCLUseInfo info) {
        return true;
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return isUnderWhen(callingSkill) ? TEXT.cond_atEndOfTurn() : TEXT.cond_inXAtTurnEnd(TEXT.cpile_hand);
    }

    @Override
    public String getSubText() {
        return isWhenClause() ? TEXT.cond_atEndOfTurn() : TEXT.cond_inXAtTurnEnd(TEXT.cpile_hand);
    }

    @Override
    public void onEndOfTurnFirst(boolean isPlayer) {
        useFromTrigger(makeInfo(null));
    }
}
