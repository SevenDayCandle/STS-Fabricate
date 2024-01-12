package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.actions.cards.ModifyCard;
import pinacolada.actions.cards.ModifyCost;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cardmods.PermanentCostModifier;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardModify;

@VisibleSkill
public class PMove_PermanentModifyCost extends PMove_PermanentModify implements OutOfCombatMove {
    public static final PSkillData<PField_CardModify> DATA = PMove_Modify.register(PMove_PermanentModifyCost.class, PField_CardModify.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .setExtra(0, DEFAULT_MAX)
            .setGroups(PCLCardGroupHelper.getAll())
            .noTarget();

    public PMove_PermanentModifyCost() {
        this(1, 1);
    }

    public PMove_PermanentModifyCost(int amount, int extra, PCLCardGroupHelper... groups) {
        super(DATA, amount, extra, groups);
    }

    public PMove_PermanentModifyCost(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public boolean canCardPass(AbstractCard c) {
        return fields.getFullCardFilter().invoke(c) && ModifyCost.canCardPass(c, amount);
    }

    @Override
    public String getObjectText() {
        return TEXT.subjects_cost;
    }


    @Override
    public boolean isDetrimental() {
        return !fields.not && extra > 0;
    }

    @Override
    protected void applyModifierOutsideOfCombat(AbstractCard c, int amount) {
        PermanentCostModifier.apply(c, amount);
    }

    @Override
    protected ModifyCard modifyCard(AbstractCard c, int amount, boolean forced, boolean relative, boolean untilPlayed) {
        return new ModifyCost(c, amount, forced, relative, untilPlayed);
    }
}
