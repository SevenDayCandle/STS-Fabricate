package pinacolada.skills.skills.special.moves;

import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

public class PMove_Shuffle extends PMove<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PMove_Shuffle.class, PField_Empty.class);

    public PMove_Shuffle() {
        super(DATA);
    }

    public PMove_Shuffle(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.subjects_shuffleYourDeck;
    }

    @Override
    public String getSubText() {
        return TEXT.subjects_shuffleYourDeck;
    }

    @Override
    public void use(PCLUseInfo info) {
        getActions().reshuffleDiscardPile(false);
        super.use(info);
    }
}
