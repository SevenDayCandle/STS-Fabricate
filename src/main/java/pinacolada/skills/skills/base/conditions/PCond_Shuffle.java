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
            .selfTarget();

    public PCond_Shuffle() {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_Shuffle(PSkillSaveData content) {
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
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return isUnderWhen(callingSkill, parentSkill) ? TEXT.cond_whenSingle(TEXT.subjects_shuffleYourDeck) : TEXT.subjects_shuffleYourDeck;
    }

    @Override
    public String getSubText() {
        return TEXT.cond_wheneverYou(TEXT.subjects_shuffleYourDeck);
    }

    @Override
    public void onShuffle(boolean triggerRelics) {
        useFromTrigger(generateInfo(null));
    }
}
