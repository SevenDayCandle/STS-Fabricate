package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActions;
import pinacolada.actions.cards.ModifyCard;
import pinacolada.actions.cards.ModifyDamage;
import pinacolada.actions.cards.ModifyDamagePercent;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cardmods.PermanentDamageModifier;
import pinacolada.cardmods.PermanentDamagePercentModifier;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.card.ChooseCardsForModifierEffect;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardModify;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMove_PermanentModifyDamagePercent extends PMove_PermanentModify implements OutOfCombatMove {
    public static final PSkillData<PField_CardModify> DATA = PMove_Modify.register(PMove_PermanentModifyDamagePercent.class, PField_CardModify.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .setExtra(0, DEFAULT_MAX)
            .setGroups(PCLCardGroupHelper.getAll())
            .noTarget();

    public PMove_PermanentModifyDamagePercent() {
        this(1, 1);
    }

    public PMove_PermanentModifyDamagePercent(int amount, int extra, PCLCardGroupHelper... groups) {
        super(DATA, amount, extra, groups);
    }

    public PMove_PermanentModifyDamagePercent(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getNumericalObjectText() {
        return EUIRM.strings.numNoun(getAmountRawString() + "%", getObjectText());
    }

    @Override
    public String getObjectSampleText() {
        return getObjectText() + "%";
    }

    @Override
    public String getObjectText() {
        return TEXT.subjects_damage;
    }

    @Override
    protected void applyModifierOutsideOfCombat(AbstractCard c, int amount) {
        PermanentDamagePercentModifier.apply(c, amount);
    }

    @Override
    protected ModifyCard modifyCard(AbstractCard c, int amount, boolean forced, boolean relative, boolean untilPlayed) {
        return new ModifyDamagePercent(c, amount, forced, relative, untilPlayed);
    }
}
