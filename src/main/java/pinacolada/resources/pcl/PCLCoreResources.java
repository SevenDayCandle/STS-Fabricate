package pinacolada.resources.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.localization.*;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLDynamicCard;
import pinacolada.misc.PCLDungeon;
import pinacolada.resources.PCLAbstractPlayerData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;

public class PCLCoreResources extends PCLResources<PCLAbstractPlayerData, PCLCoreImages, PCLCoreTooltips, PCLCoreStrings>
{
    public static final String ID = PGR.BASE_PREFIX;

    public PCLCoreResources()
    {
        super(ID, AbstractCard.CardColor.COLORLESS, AbstractPlayer.PlayerClass.IRONCLAD, new PCLCoreImages(ID));
    }

    @Override
    public void receiveEditStrings()
    {
        loadCustomStrings(OrbStrings.class);
        loadCustomCardStrings();
        loadCustomStrings(RelicStrings.class);
        loadCustomStrings(PowerStrings.class);
        loadCustomStrings(UIStrings.class);
        loadCustomStrings(EventStrings.class);
        loadCustomStrings(PotionStrings.class);
        loadCustomStrings(MonsterStrings.class);
        loadCustomStrings(BlightStrings.class);
        loadCustomStrings(RunModStrings.class);
        loadCustomStrings(StanceStrings.class);
        loadAugmentStrings();
    }

    @Override
    public PCLAbstractPlayerData getData()
    {
        return null;
    }

    @Override
    public PCLCoreTooltips getTooltips()
    {
        return new PCLCoreTooltips();
    }

    @Override
    public PCLCoreStrings getStrings()
    {
        return new PCLCoreStrings(this);
    }

    protected void postInitialize()
    {
        tooltips.initializeIcons();
    }

    // Core resources are pulled when a non-PCL character is used, so it should accept all non-filtered colorless cards
    public boolean containsColorless(AbstractCard card)
    {
        return !PCLDungeon.isColorlessCardExclusive(card);
    }

    @Override
    public boolean filterColorless(AbstractCard card)
    {
        return card instanceof PCLCard && !(card instanceof PCLDynamicCard) && ((PCLCard) card).cardData.resources == this;
    }
}