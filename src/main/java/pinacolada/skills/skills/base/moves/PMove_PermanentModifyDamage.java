package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.actions.cards.ModifyCard;
import pinacolada.actions.cards.ModifyDamage;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cardmods.PermanentDamageModifier;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardModify;

@VisibleSkill
public class PMove_PermanentModifyDamage extends PMove_PermanentModify implements OutOfCombatMove {
    public static final PSkillData<PField_CardModify> DATA = PMove_Modify.register(PMove_PermanentModifyDamage.class, PField_CardModify.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .setExtra(0, DEFAULT_MAX)
            .setGroups(PCLCardGroupHelper.getAll())
            .noTarget();

    public PMove_PermanentModifyDamage() {
        this(1, 1);
    }

    public PMove_PermanentModifyDamage(int amount, int extra, PCLCardGroupHelper... groups) {
        super(DATA, amount, extra, groups);
    }

    public PMove_PermanentModifyDamage(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    protected void applyModifierOutsideOfCombat(AbstractCard c, int amount) {
        PermanentDamageModifier.apply(c, amount);
    }

    @Override
    protected ModifyCard modifyCard(AbstractCard c, int amount, boolean forced, boolean relative, boolean untilPlayed) {
        return new ModifyDamage(c, amount, forced, relative, untilPlayed);
    }

    @Override
    public String getObjectText(Object requestor) {
        return TEXT.subjects_damage;
    }
}
