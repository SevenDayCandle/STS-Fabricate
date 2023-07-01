package pinacolada.resources.loadout;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.ui.screens.CustomCardLibraryScreen;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.PCLDynamicCardData;
import pinacolada.cards.base.tags.CardFlag;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
// TODO use this for custom run loadouts
public class FakeLoadout extends PCLLoadout {
    public FakeLoadout() {
        super(AbstractCard.CardColor.COLORLESS, PGR.BASE_PREFIX, 0, -1, 1);
    }

    @Override
    public PCLCardData getSymbolicCard() {
        return QuestionMark.DATA;
    }

    @Override
    public PCLTrophies getTrophies() {
        return null;
    }

    @Override
    public void onVictory(int ascensionLevel, int trophyLevel, int score) {
        //
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
    public void addBasicDefends(LoadoutCardSlot slot) {
        if (!GameUtilities.isColorlessCardColor(color)) {
            for (AbstractCard c : CustomCardLibraryScreen.CardLists.get(color).group) {
                if (c.hasTag(AbstractCard.CardTags.STARTER_STRIKE)) {
                    slot.addItem(c.cardID, 0);
                }
            }
        }
    }

    @Override
    public void addBasicStrikes(LoadoutCardSlot slot) {
        if (!GameUtilities.isColorlessCardColor(color)) {
            for (AbstractCard c : CustomCardLibraryScreen.CardLists.get(color).group) {
                if (c.hasTag(AbstractCard.CardTags.STARTER_DEFEND)) {
                    slot.addItem(c.cardID, 0);
                }
            }
        }
    }

    @Override
    public void addLoadoutCards(LoadoutCardSlot slot) {
        if (!GameUtilities.isColorlessCardColor(color)) {
            for (AbstractCard c : CustomCardLibraryScreen.CardLists.get(color).group) {
                switch (c.rarity) {
                    case BASIC:
                        if ((c.hasTag(AbstractCard.CardTags.STARTER_STRIKE) || c.hasTag(AbstractCard.CardTags.STARTER_DEFEND))) {
                            break;
                    }
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

    protected void setDefaultCardsForData(PCLLoadoutData data) {
        data.getCardSlot(0).select(0, 4).markAllSeen();
        data.getCardSlot(1).select(0, 4).markAllSeen();
        data.getCardSlot(2).select(0, 1).markCurrentSeen();
        data.getCardSlot(3).select(1, 1).markCurrentSeen();
        data.getCardSlot(4).select(2, 1).markCurrentSeen();
        data.getCardSlot(5).select(null);
    }

    public int getValueForRarity(AbstractCard.CardRarity rarity) {
        switch (rarity) {
            case COMMON:
            case BASIC:
                return COMMON_LOADOUT_VALUE;
            case UNCOMMON:
                return 2 + COMMON_LOADOUT_VALUE * 2;
        }
        return 5 + COMMON_LOADOUT_VALUE * 3;
    }

    public boolean allowCustoms() {
        return true;
    }
}
