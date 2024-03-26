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
import pinacolada.cards.base.fields.PCLCardSelection;
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
import pinacolada.skills.fields.PField_CardModify;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

@VisibleSkill
public class PMove_ObtainCard extends PMove_GenerateCard implements OutOfCombatMove {
    public static final PSkillData<PField_CardModify> DATA = register(PMove_ObtainCard.class, PField_CardModify.class)
            .setExtra(1, DEFAULT_MAX)
            .setExtra2(0, DEFAULT_MAX)
            .setOrigins(PCLCardSelection.Manual)
            .setGroups(PCLCardGroupHelper.MasterDeck)
            .noTarget();

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
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.obtain;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_obtain(TEXT.subjects_card);
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }

    @Override
    public void performAction(PCLUseInfo info, PCLActions order, AbstractCard c) {
        order.showAndObtain(c);
    }

    @Override
    public void useOutsideOfBattle(PCLUseInfo info) {
        ArrayList<AbstractCard> cards = getBaseCards(info);
        AbstractRoom curRoom = GameUtilities.getCurrentRoom();
        // If the action is not an "choose x of y" option, add the cards directly into the player's deck
        if (isOutOf()) {
            if (curRoom instanceof MonsterRoom && curRoom.rewardAllowed && curRoom.isBattleOver) {
                RewardItem r = new RewardItem();
                String name = getName();
                if (!StringUtils.isEmpty(name)) {
                    r.text = name;
                }
                r.cards = cards;
                curRoom.addCardReward(r);
                info.setData(cards);
                super.useOutsideOfBattle(info);
            }
            else {
                PCLEffects.Queue.add(new ChooseCardsToObtainEffect(refreshAmount(info), cards, null))
                        .addCallback(effect -> {
                            info.setData(effect.cards);
                            super.useOutsideOfBattle(info);
                        });
            }
        }
        else {
            for (AbstractCard c : cards) {
                PCLEffects.Queue.showAndObtain(c);
            }
            info.setData(cards);
            super.useOutsideOfBattle(info);
        }
    }
}
