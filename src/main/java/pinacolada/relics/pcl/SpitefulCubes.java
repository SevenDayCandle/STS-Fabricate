package pinacolada.relics.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class SpitefulCubes extends AbstractCubes
{
    public static final String ID = createFullID(SpitefulCubes.class);
    public static final int MAX_STORED_USES = 3;
    public static final int USES_PER_ELITE = 3;
    public static final int USES_PER_NORMAL = 0;
    private static final CardGroup tempGroup1 = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
    private static final CardGroup tempGroup2 = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
    private static final CardGroup tempGroup3 = new CardGroup(CardGroup.CardGroupType.CARD_POOL);

    public SpitefulCubes()
    {
        super(ID, RelicTier.STARTER, LandingSound.SOLID, USES_PER_NORMAL, USES_PER_ELITE, MAX_STORED_USES);
    }

    public AbstractCard getReward(AbstractCard card, RewardItem rewardItem)
    {
        final PCLCard c = PCLCard.cast(card);
        if (c != null && c.affinities != null)
        {
            final ArrayList<PCLAffinity> affinities = c.affinities.getAffinities();
            final CardGroup g1 = AbstractDungeon.srcCommonCardPool;
            final CardGroup g2 = AbstractDungeon.srcUncommonCardPool;
            final CardGroup g3 = AbstractDungeon.srcRareCardPool;

            tempGroup1.clear();
            tempGroup2.clear();
            tempGroup3.clear();

            for (AbstractCard sc : g1.group)
            {
                if (GameUtilities.hasAnyAffinity(sc, affinities))
                {
                    tempGroup1.addToTop(sc);
                }
            }
            for (AbstractCard sc : g2.group)
            {
                if (GameUtilities.hasAnyAffinity(sc, affinities))
                {
                    tempGroup2.addToTop(sc);
                }
            }
            for (AbstractCard sc : g3.group)
            {
                if (GameUtilities.hasAnyAffinity(sc, affinities))
                {
                    tempGroup3.addToTop(sc);
                }
            }

            AbstractDungeon.srcCommonCardPool = tempGroup1;
            AbstractDungeon.srcUncommonCardPool = tempGroup2;
            AbstractDungeon.srcRareCardPool = tempGroup3;

            final AbstractCard reward = PGR.core.dungeon.getRandomRewardCard(rewardItem.cards, true, true);

            AbstractDungeon.srcCommonCardPool = g1;
            AbstractDungeon.srcUncommonCardPool = g2;
            AbstractDungeon.srcRareCardPool = g3;

            return reward;
        }
        return super.getReward(card, rewardItem);
    }
}