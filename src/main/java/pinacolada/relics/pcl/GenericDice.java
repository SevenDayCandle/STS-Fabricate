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
    public static final int MAX_STORED_USES = 5;
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

    public AbstractCard getReward(AbstractCard card, RewardItem rewardItem) {
        return PGR.dungeon.getRandomRewardReplacementCard(card.rarity, rewardItem.cards, AbstractDungeon.cardRng, true);
    }

    @Override
    public String getUpdatedDescription() {
        return formatDescription(0, BONUS_PER_CARDS, MAX_STORED_USES);
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        super.onEnterRoom(room);

        if (room instanceof MonsterRoom) {
            setCounter(Math.min(MAX_STORED_USES, counter + (GameUtilities.getTotalCardsInPlay() / BONUS_PER_CARDS)));
            flash();
        }
    }

    @Override
    public void onEquip() {
        super.onEquip();

        setCounter(1);
    }
}