package pinacolada.resources.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.localization.*;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLDynamicCard;
import pinacolada.cards.base.TemplateCardData;
import pinacolada.dungeon.PCLDungeon;
import pinacolada.resources.PCLPlayerData;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;

public class PCLCoreResources extends PCLResources<PCLPlayerData<?, ?, ?>, PCLCoreImages, PCLCoreTooltips, PCLCoreStrings> {

    public PCLCoreResources() {
        super(PGR.BASE_PREFIX, AbstractCard.CardColor.COLORLESS, PCLEnum.PlayerClass.CORE, new PCLCoreImages(PGR.BASE_PREFIX));
    }

    // Core resources are pulled when a non-PCL character is used, so it should accept all non-filtered colorless cards
    public boolean containsColorless(AbstractCard card) {
        return !PCLDungeon.isColorlessCardExclusive(card);
    }

    @Override
    public boolean filterColorless(AbstractCard card) {
        return card instanceof PCLCard && !(card instanceof PCLDynamicCard) && ((PCLCard) card).cardData.resources == this;
    }

    @Override
    public PCLPlayerData<?, ?, ?> getData() {
        return null;
    }

    @Override
    public String getReplacement(String cardID) {
        // Prevent example templates from showing up for regular characters
        PCLCardData data = PCLCardData.getStaticData(cardID);
        return data instanceof TemplateCardData ? ((TemplateCardData) data).originalID : null;
    }

    @Override
    public PCLCoreStrings getStrings() {
        return new PCLCoreStrings(this);
    }

    @Override
    public PCLCoreTooltips getTooltips() {
        return new PCLCoreTooltips();
    }

    @Override
    protected void postInitialize() {
        tooltips.initializeIcons();
        // No data
    }

    @Override
    public void receiveEditCharacters() {
        // No-op
    }

    @Override
    public void receiveEditStrings() {
        //loadCustomStrings(OrbStrings.class);
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
}