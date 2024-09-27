package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleSkill;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PMod_PerCardCost extends PMod_PerCardProperty<PField_CardCategory> {

    public static final PSkillData<PField_CardCategory> DATA = register(PMod_PerCardCost.class, PField_CardCategory.class).noTarget();

    public PMod_PerCardCost() {
        this(1);
    }

    public PMod_PerCardCost(int amount) {
        super(DATA, amount);
    }

    public PMod_PerCardCost(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    protected int getCardProperty(AbstractCard c) {
        return Math.max(0, c.costForTurn);
    }

    @Override
    protected String getCardPropertyString() {
        return TEXT.subjects_cost;
    }
}
