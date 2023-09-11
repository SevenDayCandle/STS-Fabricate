package pinacolada.relics.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleRelic;
import pinacolada.interfaces.providers.CardRewardActionProvider;
import pinacolada.relics.PCLRelic;
import pinacolada.relics.PCLRelicData;
import pinacolada.resources.PGR;
import pinacolada.ui.cardReward.PCLCardRewardScreen;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.HashSet;

@VisibleRelic
public class GenericDice extends PCLRelic implements CardRewardActionProvider {
    public static final PCLRelicData DATA = register(GenericDice.class)
            .setProps(RelicTier.STARTER, LandingSound.SOLID)
            .setLoadoutValue(6)
            .setUnique(true);

    public GenericDice() {
        super(DATA);
        setCounter(1);
    }

    public boolean canAct() {
        return counter > 0;
    }

    public boolean doAction(AbstractCard card, RewardItem rewardItem, int cardIndex) {
        setCounter(counter - 1);
        rerollCard(card, getReward(card, rewardItem), rewardItem, cardIndex);
        return false;
    }

    public static int getChance() {
        return GameUtilities.getTotalCardsInRewardPool();
    }


    @Override
    public String getDescriptionImpl() {
        if (GameUtilities.inGame()) {
            return EUIUtils.joinStrings(EUIUtils.SPLIT_LINE, formatDescription(0), formatDescription(1, getChance()));
        }
        return formatDescription(0);
    }

    public AbstractCard getReward(AbstractCard card, RewardItem rewardItem) {
        for (AbstractCard c : rewardItem.cards) {
            PCLCardRewardScreen.seenCards.add(c.cardID);
        }
        AbstractCard replacement = PGR.dungeon.getRandomRewardReplacementCard(card.rarity, c -> !(PCLCardRewardScreen.seenCards.contains(c.cardID)), AbstractDungeon.cardRng, true);
        if (replacement != null) {
            PCLCardRewardScreen.seenCards.add(replacement.cardID);
        }
        return replacement;
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        super.onEnterRoom(room);

        float chance = getChance();
        while (chance > 0) {
            if (GameUtilities.chance(chance)) {
                setCounter(counter + 1);
                flash();
            }
            chance -= 100;
        }

        updateDescription(null);
    }

    @Override
    protected void onStack(AbstractRelic other) {
        super.onStack(other);
        setCounter(counter + other.counter);
    }
}