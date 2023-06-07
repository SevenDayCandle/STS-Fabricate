package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.card.ChooseCardsToObtainEffect;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMove_ObtainCard extends PMove_GenerateCard implements OutOfCombatMove {
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_ObtainCard.class, PField_CardCategory.class)
            .setExtra(1, DEFAULT_MAX)
            .setGroups(PCLCardGroupHelper.MasterDeck)
            .selfTarget();

    public PMove_ObtainCard() {
        this(1);
    }

    public PMove_ObtainCard(int copies, String... cardData) {
        super(DATA, PCLCardTarget.None, copies, cardData);
    }

    public PMove_ObtainCard(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.obtain;
    }

    @Override
    public void performAction(PCLUseInfo info, AbstractCard c) {
        getActions().showAndObtain(c);
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }

    @Override
    public void useOutsideOfBattle() {
        super.useOutsideOfBattle();
        if (GameUtilities.inBattle()) {
            use(makeInfo(getOwnerCreature()), __ -> {}, PCLActions.top);
        }
        else {
            PCLEffects.Queue.add(new ChooseCardsToObtainEffect(amount, getBaseCards(null), null));
        }
    }
}
