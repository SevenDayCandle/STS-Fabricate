package pinacolada.resources.loadout;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import extendedui.ui.screens.CustomCardLibraryScreen;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.PCLDynamicCardData;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.relics.PCLDynamicRelicData;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

// Copied and modified from STS-AnimatorMod
public class FakeLoadout extends PCLLoadout {
    public FakeLoadout() {
        super(AbstractCard.CardColor.COLORLESS, PGR.BASE_PREFIX, 0, -1, 1, 9999);
    }

    @Override
    public void addBasicDefends(LoadoutCardSlot slot) {
        addLoadoutCardsImpl(slot);
    }

    @Override
    public void addBasicStrikes(LoadoutCardSlot slot) {
        addLoadoutCardsImpl(slot);
    }

    @Override
    public void addLoadoutCards(LoadoutCardSlot slot) {
        addLoadoutCardsImpl(slot);
    }

    protected void addLoadoutCardsImpl(LoadoutCardSlot slot) {
        if (!GameUtilities.isColorlessCardColor(color)) {
            for (AbstractCard c : CustomCardLibraryScreen.CardLists.get(color).group) {
                switch (c.rarity) {
                    case BASIC:
                    case COMMON:
                    case UNCOMMON:
                    case RARE:
                        slot.addItem(c.cardID, getValueForRarity(c.rarity));
                }
            }
            for (PCLCustomCardSlot custom : PCLCustomCardSlot.getCards(color)) {
                PCLDynamicCardData data = custom.getBuilder(0);
                switch (data.cardRarity) {
                    case BASIC:
                    case COMMON:
                    case UNCOMMON:
                    case RARE:
                        slot.addItem(data.ID, getValueForRarity(data.cardRarity));
                }
            }
        }
    }

    @Override
    public void addLoadoutRelics(LoadoutRelicSlot slot) {
        super.addLoadoutRelics(slot);
        if (!GameUtilities.isColorlessCardColor(color)) {
            for (AbstractRelic c : GameUtilities.getRelics(color).values()) {
                if (GameUtilities.isRelicTierSpawnable(c.tier)) {
                    slot.addItem(c, getValueForRarity(c.tier));
                }
            }
            for (PCLCustomRelicSlot custom : PCLCustomRelicSlot.getRelics(color)) {
                PCLDynamicRelicData data = custom.getBuilder(0);
                if (GameUtilities.isRelicTierSpawnable(data.tier) || data.tier == AbstractRelic.RelicTier.STARTER) {
                    slot.addItem(data.create(), getValueForRarity(data.tier));
                }
            }
        }
        for (AbstractRelic c : GameUtilities.getRelics(AbstractCard.CardColor.COLORLESS).values()) {
            if (GameUtilities.isRelicTierSpawnable(c.tier)) {
                slot.addItem(c, getValueForRarity(c.tier));
            }
        }
        for (PCLCustomRelicSlot custom : PCLCustomRelicSlot.getRelics(AbstractCard.CardColor.COLORLESS)) {
            PCLDynamicRelicData data = custom.getBuilder(0);
            if (GameUtilities.isRelicTierSpawnable(data.tier) || data.tier == AbstractRelic.RelicTier.STARTER) {
                slot.addItem(data.create(), getValueForRarity(data.tier));
            }
        }
    }

    public boolean allowCustoms() {
        return true;
    }

    @Override
    public PCLCardData getSymbolicCard() {
        return QuestionMark.DATA;
    }

    @Override
    public PCLTrophies getTrophies() {
        return null;
    }

    public int getValueForRarity(AbstractCard.CardRarity rarity) {
        switch (rarity) {
            case COMMON:
            case BASIC:
                return COMMON_LOADOUT_VALUE;
            case UNCOMMON:
                return 2 + COMMON_LOADOUT_VALUE * 2;
        }
        return COMMON_LOADOUT_VALUE * 4;
    }

    public int getValueForRarity(AbstractRelic.RelicTier rarity) {
        switch (rarity) {
            case COMMON:
            case STARTER:
            case SHOP:
                return COMMON_LOADOUT_VALUE * 2;
            case UNCOMMON:
                return COMMON_LOADOUT_VALUE * 3;
            case RARE:
                return COMMON_LOADOUT_VALUE * 4;
        }
        return COMMON_LOADOUT_VALUE * 5;
    }

    @Override
    public void onOpen(CharacterOption option) {
        AbstractCard.CardColor newColor = option.c.getCardColor();
        if (newColor != color) {
            color = newColor;
            clearPresets();
        }
    }

    @Override
    public void onVictory(int ascensionLevel, int trophyLevel, int score) {
        //
    }

    protected void setDefaultCardsForData(PCLLoadoutData data) {
        data.getCardSlot(0).select(0, 4).markAllSeen();
        data.getCardSlot(1).select(1, 4).markAllSeen();
        data.getCardSlot(2).select(2, 1).markCurrentSeen();
        data.getCardSlot(3).select(3, 1).markCurrentSeen();
        data.getCardSlot(4).select(4, 1).markCurrentSeen();
        data.getCardSlot(5).clear();
    }
}
