package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.actions.cards.ModifyBlock;
import pinacolada.actions.cards.ModifyCard;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cardmods.PermanentBlockModifier;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardModify;

@VisibleSkill
public class PMove_PermanentModifyBlock extends PMove_PermanentModify {
    public static final PSkillData<PField_CardModify> DATA = PMove_Modify.register(PMove_PermanentModifyBlock.class, PField_CardModify.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .setExtra(0, DEFAULT_MAX)
            .setGroups(PCLCardGroupHelper.getAll())
            .noTarget();

    public PMove_PermanentModifyBlock() {
        this(1, 1);
    }

    public PMove_PermanentModifyBlock(int amount, int extra, PCLCardGroupHelper... groups) {
        super(DATA, amount, extra, groups);
    }

    public PMove_PermanentModifyBlock(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    protected void applyModifierOutsideOfCombat(AbstractCard c, int amount) {
        PermanentBlockModifier.apply(c, amount);
    }

    @Override
    protected ModifyCard modifyCard(AbstractCard c, int amount, boolean forced, boolean relative, boolean untilPlayed) {
        return new ModifyBlock(c, amount, forced, relative, untilPlayed);
    }

    @Override
    public String getObjectText() {
        return PGR.core.tooltips.block.title;
    }
}
