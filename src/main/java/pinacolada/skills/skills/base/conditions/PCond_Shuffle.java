package pinacolada.skills.skills.base.conditions;

import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnShuffleSubscriber;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PDelegateCond;

@VisibleSkill
public class PCond_Shuffle extends PDelegateCond<PField_Empty> implements OnShuffleSubscriber {
    public static final PSkillData<PField_Empty> DATA = register(PCond_Shuffle.class, PField_Empty.class, 1, 1)
            .noTarget();

    public PCond_Shuffle() {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_Shuffle(PSkillSaveData content) {
        super(DATA, content);
    }

    // This should not activate the child effect when played normally

    @Override
    public boolean canPlay(PCLUseInfo info, PSkill<?> triggerSource) {
        return true;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return isUnderWhen(callingSkill, parentSkill) ? TEXT.cond_when(TEXT.subjects_shuffleYourDeck) : TEXT.cond_onGeneric(TEXT.subjects_shuffle);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        if (isBranch()) {
            return getWheneverYouString(TEXT.subjects_shuffle);
        }
        if (isWhenClause()) {
            return getWheneverYouString(TEXT.subjects_shuffleYourDeck);
        }
        return TEXT.cond_onGeneric(TEXT.subjects_shuffle);
    }

    @Override
    public void onShuffle(boolean triggerRelics) {
        if (triggerRelics) {
            useFromTrigger(generateInfo(null));
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
    }
}
