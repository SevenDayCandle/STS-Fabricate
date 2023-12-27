package pinacolada.skills.skills.base.conditions;

import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnStartOfTurnPostDrawSubscriber;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PDelegateCond;

@VisibleSkill
public class PCond_AtTurnStart extends PDelegateCond<PField_Empty> implements OnStartOfTurnPostDrawSubscriber {

    public static final PSkillData<PField_Empty> DATA = register(PCond_AtTurnStart.class, PField_Empty.class, 1, 1)
            .noTarget();

    public PCond_AtTurnStart() {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_AtTurnStart(PSkillSaveData content) {
        super(DATA, content);
    }

    // This should not activate the child effect when played normally

    @Override
    public boolean canPlay(PCLUseInfo info, PSkill<?> triggerSource) {
        return true;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return !isUnderWhen(callingSkill, parentSkill) && source != null ? TEXT.cond_inXAtTurnStart(TEXT.cpile_hand) : TEXT.cond_atStartOfTurn();
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return !isWhenClause() && source != null ? TEXT.cond_inXAtTurnStart(TEXT.cpile_hand) : TEXT.cond_atStartOfTurn();
    }

    @Override
    public void onStartOfTurnPostDraw() {
        useFromTrigger(generateInfo(null));
    }

    @Override
    public boolean shouldUseWhenText() {
        return false;
    }
}
