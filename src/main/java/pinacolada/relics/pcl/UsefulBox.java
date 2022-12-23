package pinacolada.relics.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.misc.CombatManager;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

public class UsefulBox extends AbstractBox
{
    public static final String ID = createFullID(UsefulBox.class);

    public UsefulBox()
    {
        super(ID, RelicTier.SPECIAL, LandingSound.SOLID);
    }

    @Override
    public void atBattleStart()
    {
        if (PGR.core.dungeon.startingSeries != null && CombatManager.summons.summons.size() > 0)
        {
            PCLCardData data = GameUtilities.getRandomElement(EUIUtils.filter(PGR.core.dungeon.startingSeries.cardData, cd -> cd.cardRarity == AbstractCard.CardRarity.COMMON));
            if (data != null)
            {
                PCLActions.bottom.summonAlly((PCLCard) data.makeCopy(false), CombatManager.summons.summons.get(0));
            }
        }
    }
}