package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PMod_PerCardDamage extends PMod_PerCardProperty<PField_CardCategory> {

    public static final PSkillData<PField_CardCategory> DATA = register(PMod_PerCardDamage.class, PField_CardCategory.class).noTarget();

    public PMod_PerCardDamage() {
        this(1);
    }

    public PMod_PerCardDamage(int amount) {
        super(DATA, amount);
    }

    public PMod_PerCardDamage(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    protected int getCardProperty(AbstractCard c) {
        return c.damage;
    }

    @Override
    protected String getCardPropertyString() {
        return TEXT.subjects_damage;
    }
}
