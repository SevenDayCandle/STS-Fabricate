package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleSkill;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PMod_PerCardBlock extends PMod_PerCardProperty {

    public static final PSkillData<PField_CardCategory> DATA = register(PMod_PerCardBlock.class, PField_CardCategory.class).noTarget();

    public PMod_PerCardBlock() {
        this(1);
    }

    public PMod_PerCardBlock(int amount) {
        super(DATA, amount);
    }

    public PMod_PerCardBlock(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    protected int getCardProperty(AbstractCard c) {
        return c.block;
    }

    @Override
    protected String getCardPropertyString() {
        return PGR.core.tooltips.block.title;
    }
}
