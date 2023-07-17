package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.card.ChooseCardsToObtainEffect;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
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

    public PMove_ObtainCard(int copies, int choices, String... cardData) {
        super(DATA, PCLCardTarget.None, copies, choices, cardData);
    }

    public PMove_ObtainCard(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_obtain(TEXT.subjects_card);
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.obtain;
    }

    @Override
    public void performAction(PCLUseInfo info, PCLActions order, AbstractCard c) {
        order.showAndObtain(c);
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }

    @Override
    public void useOutsideOfBattle() {
        super.useOutsideOfBattle();
        AbstractRoom curRoom = GameUtilities.getCurrentRoom();
        // If the action is not an "choose x of y" option, add the cards directly into the player's deck
        if (isOutOf()) {
            if (curRoom instanceof MonsterRoom && curRoom.rewardAllowed) {
                RewardItem r = new RewardItem();
                String name = getName();
                if (!StringUtils.isEmpty(name)) {
                    r.text = name;
                }
                r.cards = getBaseCards(null);
                curRoom.addCardReward(r);
            }
            else {
                PCLEffects.Queue.add(new ChooseCardsToObtainEffect(amount, getBaseCards(null), null));
            }
        }
        else {
            for (AbstractCard c : getBaseCards(null)) {
                PCLEffects.Queue.showAndObtain(c);
            }
        }
    }
}
