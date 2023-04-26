package pinacolada.relics.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import extendedui.EUIUtils;
import pinacolada.interfaces.providers.CardRewardActionProvider;
import pinacolada.relics.PCLRelic;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public abstract class AbstractCubes extends PCLRelic implements CardRewardActionProvider {
    public static final String ID = createFullID(AbstractCubes.class);
    public final int normalUses;
    public final int maxUses;

    public AbstractCubes(String id, RelicTier tier, LandingSound sfx, int normalUses, int maxUses) {
        super(id, tier, sfx);
        this.normalUses = normalUses;
        this.maxUses = maxUses;
        this.updateDescription(null);
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
        return formatDescription(0, normalUses, maxUses);
    }

    @Override
    public void onEquip() {
        super.onEquip();

        setCounter(1);
    }

    @Override
    public void obtain() {
        ArrayList<AbstractRelic> relics = player.relics;
        for (int i = 0; i < relics.size(); i++) {
            AbstractCubes relic = EUIUtils.safeCast(relics.get(i), AbstractCubes.class);
            if (relic != null) {
                instantObtain(player, i, true);
                setCounter(relic.counter);
                return;
            }
        }

        super.obtain();
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        super.onEnterRoom(room);

        if (room instanceof MonsterRoom) {
            setCounter(Math.min(maxUses, counter + (GameUtilities.getTotalCardsInPlay() / normalUses)));
            flash();
        }
    }
}