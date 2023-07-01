package pinacolada.relics.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import pinacolada.annotations.VisibleRelic;
import pinacolada.interfaces.providers.CardRewardActionProvider;
import pinacolada.relics.PCLRelic;
import pinacolada.relics.PCLRelicData;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

@VisibleRelic
public class GenericDice extends PCLRelic implements CardRewardActionProvider {
    public static final PCLRelicData DATA = register(GenericDice.class)
            .setProps(RelicTier.STARTER, LandingSound.SOLID);
    public static final int BONUS_PER_CARDS = 60;

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

    protected AbstractCard.CardRarity getRarity(AbstractCard card) {
        int roll = rng.random(100);
        if (roll < 10) {
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
    public String getUpdatedDescription() {
        return formatDescription(0, BONUS_PER_CARDS);
    }

    @Override
    public void onEquip() {
        super.onEquip();

        setCounter(0);
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        super.onEnterRoom(room);

        if (room instanceof MonsterRoom) {
            setCounter(counter + getBonus());
            flash();
        }
    }
}