package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActions;
import pinacolada.actions.cards.ModifyCost;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cardmods.PermanentCostModifier;
import pinacolada.cardmods.TemporaryCostModifier;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.card.ChooseCardsForModifierEffect;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardModify;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMove_PermanentModifyCost extends PMove_Modify<PField_CardModify> implements OutOfCombatMove {
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
    public ActionT1<AbstractCard> getAction(PCLActions order) {
        return (c) -> {
            order.modifyCost(c, amount, fields.forced, !fields.not, fields.or);
            AbstractCard deckCopy = GameUtilities.getMasterDeckInstance(c.uuid);
            if (deckCopy != null && deckCopy != c) {
                order.modifyCost(deckCopy, amount, fields.forced, !fields.not, fields.or);
            }
        };
    }

    @Override
    public String getObjectText() {
        return TEXT.subjects_cost;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.subjects_permanentlyX(super.getSampleText(callingSkill, parentSkill));
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return TEXT.subjects_permanentlyX(super.getSubText(perspective, requestor));
    }

    @Override
    public boolean isDetrimental() {
        return !fields.not && extra > 0;
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }

    @Override
    public void useOutsideOfBattle() {
        super.useOutsideOfBattle();
        PCLEffects.Queue.add(new ChooseCardsForModifierEffect(this, c -> {
            PermanentCostModifier.apply(c, !fields.not ? amount : amount - c.costForTurn);
        }));
    }
}
