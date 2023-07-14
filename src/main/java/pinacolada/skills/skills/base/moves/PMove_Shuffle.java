package pinacolada.skills.skills.base.moves;

import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PMove_Shuffle extends PMove<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PMove_Shuffle.class, PField_Empty.class, 1, 1).selfTarget();

    public PMove_Shuffle() {
        super(DATA);
    }

    public PMove_Shuffle(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.subjects_shuffleYourDeck;
    }

    @Override
    public String getSubText() {
        return TEXT.subjects_shuffleYourDeck;
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        order.reshuffleDiscardPile(false);
        super.use(info, order);
    }
}
