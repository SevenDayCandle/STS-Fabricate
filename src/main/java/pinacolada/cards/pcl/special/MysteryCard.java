package pinacolada.cards.pcl.special;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.interfaces.markers.Hidden;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.skills.PCustomCond;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.WeightedList;

import java.util.ArrayList;

// TODO transform when added to deck
public class MysteryCard extends PCLCard implements Hidden
{
    public static final PCLCardData DATA = register(MysteryCard.class)
            .setImagePath(QuestionMark.DATA.imagePath)
            .setSkill(0, CardRarity.SPECIAL, PCLCardTarget.AllEnemy)
            .setColorless();
    private MysteryCond move;

    public MysteryCard()
    {
        this(false, CardRarity.COMMON);
    }

    public MysteryCard(boolean isDummy)
    {
        this(isDummy, CardRarity.COMMON);
    }

    public MysteryCard(boolean isDummy, CardRarity... rarities)
    {
        super(DATA, new MysteryCond(DATA, isDummy ? 2 : 0, DATA.getCost(0)).edit(f -> f.setRarity(rarities)));
    }

    public void setup(Object input)
    {
        move = (MysteryCond) input;
        addUseMove(move);
    }

    public AbstractCard createObscuredCard() {
        return move.createObscuredCard();
    }

    public static class MysteryCond extends PCustomCond
    {
        public MysteryCond(PCLCardData cardData, int index, int amount)
        {
            super(cardData, index, amount, 0);
        }

        @Override
        public String getSubText()
        {
            return EUIUtils.format(cardData.strings.EXTENDED_DESCRIPTION[descIndex], amount, PCLCoreStrings.joinWithOr(EUIUtils.map(fields.rarities, EUIGameUtils::textForRarity)));
        }

        private boolean checkCondition(AbstractCard c)
        {
            return c.cost == amount && GameUtilities.isObtainableInCombat(c);
        }

        public final AbstractCard createObscuredCard()
        {
            ArrayList<AbstractCard> pool = getPool(fields.rarities);
            WeightedList<AbstractCard> possiblePicks = new WeightedList<>();
            for (AbstractCard c : pool)
            {
                possiblePicks.add(c, getWeight(c));
            }
            AbstractCard card = possiblePicks.retrieve(rng).makeCopy();
            for (int i = 0; i < extra; i++)
            {
                card.upgrade();
            }
            card.cost = card.costForTurn = amount;
            return card;
        }

        private ArrayList<AbstractCard> getPool(ArrayList<CardRarity> rarities)
        {
            ArrayList<AbstractCard> pool = new ArrayList<>();
            for (CardRarity rarity : rarities)
            {
                switch (rarity)
                {
                    case COMMON:
                        pool.addAll(AbstractDungeon.commonCardPool.group);
                        break;
                    case UNCOMMON:
                        pool.addAll(AbstractDungeon.uncommonCardPool.group);
                        break;
                    case RARE:
                        pool.addAll(AbstractDungeon.rareCardPool.group);
                        break;
                    case CURSE:
                        pool.addAll(AbstractDungeon.curseCardPool.group);
                        break;
                }
            }
            return EUIUtils.filter(pool, this::checkCondition);
        }

        private int getWeight(AbstractCard c)
        {
            return 10 - Math.max(2, 2 * c.cost);
        }
    }
}