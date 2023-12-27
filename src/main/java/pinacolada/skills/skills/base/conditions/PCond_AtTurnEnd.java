package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
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
            .noTarget();

    public PCond_AtTurnEnd() {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_AtTurnEnd(PSkillSaveData content) {
        super(DATA, content);
    }

    // This should not activate the child effect when played normally

    @Override
    public boolean canPlay(PCLUseInfo info, PSkill<?> triggerSource) {
        return true;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return !isUnderWhen(callingSkill, parentSkill) && source instanceof AbstractCard ? TEXT.cond_inXAtTurnEnd(TEXT.cpile_hand) : TEXT.cond_atEndOfTurn();
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return !isWhenClause() && source instanceof AbstractCard ? TEXT.cond_inXAtTurnEnd(TEXT.cpile_hand) : TEXT.cond_atEndOfTurn();
    }

    @Override
    public void onEndOfTurnFirst(boolean isPlayer) {
        useFromTrigger(generateInfo(null));
    }

    @Override
    public boolean shouldUseWhenText() {
        return false;
    }
}
