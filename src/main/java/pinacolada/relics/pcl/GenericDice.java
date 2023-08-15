package pinacolada.relics.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;
import pinacolada.annotations.VisibleRelic;
import pinacolada.interfaces.providers.CardRewardActionProvider;
import pinacolada.relics.PCLRelic;
import pinacolada.relics.PCLRelicData;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

@VisibleRelic
public class GenericDice extends PCLRelic implements CardRewardActionProvider {
    public static final PCLRelicData DATA = register(GenericDice.class)
            .setProps(RelicTier.STARTER, LandingSound.SOLID)
            .setUnique(true);
    public static final int BONUS_PER_CARDS = 25;

    public GenericDice() {
        super(DATA);
    }

    public boolean canAct() {
        return counter > 0;
    }

    public AbstractCard doAction(AbstractCard card, RewardItem rewardItem, int cardIndex) {
        setCounter(counter - 1);
        return getReward(card, rewardItem);
    }

    protected int getBonus() {
        return GameUtilities.getTotalCardsInRewardPool() / BONUS_PER_CARDS;
    }

    @Override
    public String getDescriptionImpl() {
        return formatDescription(0, BONUS_PER_CARDS);
    }

    protected AbstractCard.CardRarity getRarity(AbstractCard card) {
        int roll = rng.random(100);
        if (roll < 2) {
            return AbstractCard.CardRarity.RARE;
        }
        if (roll < 11) {
            return card.rarity == AbstractCard.CardRarity.RARE ? AbstractCard.CardRarity.RARE : AbstractCard.CardRarity.UNCOMMON;
        }
        if (roll < 25) {
            return AbstractCard.CardRarity.UNCOMMON;
        }
        return AbstractCard.CardRarity.COMMON;
    }

    public AbstractCard getReward(AbstractCard card, RewardItem rewardItem) {
        return PGR.dungeon.getRandomRewardReplacementCard(getRarity(card), rewardItem.cards, AbstractDungeon.cardRng, true);
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        super.onEnterRoom(room);

        if (room instanceof RestRoom) {
            setCounter(counter + getBonus());
            flash();
        }
    }

    @Override
    public void onEquip() {
        super.onEquip();

        setCounter(Math.max(0, counter) + getBonus());
    }

    @Override
    protected void onStack(AbstractRelic other) {
        setCounter(counter + getBonus());
    }
}