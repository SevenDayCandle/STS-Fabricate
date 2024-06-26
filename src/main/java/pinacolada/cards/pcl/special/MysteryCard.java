package pinacolada.cards.pcl.special;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnCardCreatedSubscriber;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PCustomCond;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

public class MysteryCard extends PCLCard {
    public static final PCLCardData DATA = register(MysteryCard.class)
            .setImagePath(QuestionMark.DATA.imagePath)
            .setSkill(-2, CardRarity.SPECIAL, PCLCardTarget.AllEnemy)
            .setColorless();
    protected MysteryCond move;

    public MysteryCard() {
        this(false);
    }

    public MysteryCard(boolean isDummy) {
        super(DATA, new MysteryCond(DATA, isDummy ? 2 : 0));
    }

    public MysteryCard(boolean isDummy, PField_CardCategory fields) {
        super(DATA, new MysteryCond(DATA, fields, isDummy ? 2 : 0));
    }

    public AbstractCard createObscuredCard() {
        return move.createObscuredCard();
    }

    public void setup(Object input) {
        move = (MysteryCond) input;
        move.setUpgradeExtra(1);
        addUseMove(move);
    }

    public static class MysteryCond extends PCustomCond implements OnCardCreatedSubscriber {

        public MysteryCond(PCLCardData cardData, int index) {
            super(cardData, index);
        }

        public MysteryCond(PCLCardData cardData, PField_CardCategory fields, int index) {
            super(cardData, fields, index);
        }

        private boolean checkCondition(AbstractCard c) {
            return GameUtilities.isObtainableInCombat(c) && fields.getFullCardFilter().invoke(c);
        }

        public final AbstractCard createObscuredCard() {
            RandomizedList<AbstractCard> possiblePicks = GameUtilities.getCardsFromFullCombatPools(this::checkCondition);
            AbstractCard card = possiblePicks.retrieve(AbstractDungeon.cardRandomRng);
            if (card != null) {
                card = card.makeCopy();
                for (int i = 0; i < extra; i++) {
                    card.upgrade();
                }
            }
            return card;
        }

        @Override
        public String getSubText(PCLCardTarget perspective, Object requestor) {
            return EUIUtils.format(cardData.strings.EXTENDED_DESCRIPTION[descIndex], fields.getFullCardString(requestor));
        }

        @Override
        public void onCardCreated(AbstractCard card, boolean startOfBattle) {
            useFromTrigger(generateInfo(null));
        }

        protected void useImpl(PCLUseInfo info, PCLActions order) {
            if (source instanceof AbstractCard) {
                order.replaceCard(((AbstractCard) source).uuid, createObscuredCard());
            }
        }
    }
}