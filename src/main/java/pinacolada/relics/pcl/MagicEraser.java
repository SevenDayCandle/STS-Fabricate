package pinacolada.relics.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.rewards.RewardItem;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleRelic;
import pinacolada.interfaces.markers.CardRewardActionProvider;

@VisibleRelic
public class MagicEraser extends AbstractCubes implements CardRewardActionProvider
{
    public static final String ID = createFullID(MagicEraser.class);
    public static final int MAX_USES = 5;
    public static final int USES_PER_ELITE = 1;
    public static final int USES_PER_NORMAL = 0;

    public MagicEraser()
    {
        super(ID, RelicTier.STARTER, LandingSound.SOLID, USES_PER_NORMAL, USES_PER_ELITE, MAX_USES);
    }

    @Override
    public AbstractCard doAction(AbstractCard card, RewardItem rewardItem, int cardIndex)
    {
        setCounter(counter - 1);
        return card;
    }

    @Override
    public String getUpdatedDescription()
    {
        return EUIUtils.format(DESCRIPTIONS[0], MAX_USES);
    }
}