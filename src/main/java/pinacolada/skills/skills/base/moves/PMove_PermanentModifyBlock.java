package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cardmods.PermanentBlockModifier;
import pinacolada.cardmods.TemporaryBlockModifier;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.card.ChooseCardsForModifierEffect;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardModify;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMove_PermanentModifyBlock extends PMove_Modify<PField_CardModify> implements OutOfCombatMove {
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
    public ActionT1<AbstractCard> getAction(PCLActions order) {
        return (c) -> {
            order.modifyBlock(c, amount, fields.forced, !fields.not, fields.or);
            AbstractCard deckCopy = GameUtilities.getMasterDeckInstance(c.uuid);
            if (deckCopy != null && deckCopy != c) {
                order.modifyBlock(deckCopy, amount, fields.forced, !fields.not, fields.or);
            }
        };
    }

    @Override
    public String getObjectText() {
        return PGR.core.tooltips.block.title;
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
        return amount < 0;
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }

    @Override
    public void useOutsideOfBattle() {
        super.useOutsideOfBattle();
        PCLEffects.Queue.add(new ChooseCardsForModifierEffect(this, c -> {
            PermanentBlockModifier.apply(c, !fields.not ? c.baseBlock + amount : amount);
        }));
    }
}
