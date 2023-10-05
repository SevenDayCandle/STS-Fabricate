package pinacolada.resources.loadout;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import extendedui.EUIUtils;
import extendedui.ui.screens.CustomCardLibraryScreen;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.PCLDynamicCardData;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.relics.PCLDynamicRelicData;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class FakeLoadout extends PCLLoadout {
    public FakeLoadout() {
        super(AbstractCard.CardColor.COLORLESS, PGR.BASE_PREFIX, 0, -1, 1, 9999);
    }

    public boolean allowCustoms() {
        return true;
    }

    @Override
    public ArrayList<String> getAvailableCardIDs() {
        ArrayList<String> base = new ArrayList<>();
        if (!GameUtilities.isColorlessCardColor(color)) {
            for (AbstractCard c : CustomCardLibraryScreen.getCards(color)) {
                switch (c.rarity) {
                    case BASIC:
                    case COMMON:
                    case UNCOMMON:
                    case RARE:
                        base.add(c.cardID);
                }
            }
            for (PCLCustomCardSlot custom : PCLCustomCardSlot.getCards(color)) {
                PCLDynamicCardData data = custom.getBuilder(0);
                switch (data.cardRarity) {
                    case BASIC:
                    case COMMON:
                    case UNCOMMON:
                    case RARE:
                        base.add(data.ID);
                }
            }
        }
        return base;
    }

    @Override
    public ArrayList<String> getAvailableRelicIDs() {
        ArrayList<String> base = super.getAvailableRelicIDs();
        if (!GameUtilities.isColorlessCardColor(color)) {
            for (AbstractRelic c : GameUtilities.getRelics(color).values()) {
                if (GameUtilities.isRelicTierSpawnable(c.tier)) {
                    base.add(c.relicId);
                }
            }
            for (PCLCustomRelicSlot custom : PCLCustomRelicSlot.getRelics(color)) {
                PCLDynamicRelicData data = custom.getBuilder(0);
                if (GameUtilities.isRelicTierSpawnable(data.tier) || data.tier == AbstractRelic.RelicTier.STARTER) {
                    base.add(data.ID);
                }
            }
        }
        for (AbstractRelic c : GameUtilities.getRelics(AbstractCard.CardColor.COLORLESS).values()) {
            if (GameUtilities.isRelicTierSpawnable(c.tier)) {
                base.add(c.relicId);
            }
        }
        for (PCLCustomRelicSlot custom : PCLCustomRelicSlot.getRelics(AbstractCard.CardColor.COLORLESS)) {
            PCLDynamicRelicData data = custom.getBuilder(0);
            if (GameUtilities.isRelicTierSpawnable(data.tier) || data.tier == AbstractRelic.RelicTier.STARTER) {
                base.add(data.ID);
            }
        }
        return base;
    }

    @Override
    public ArrayList<String> getBaseStartingRelics() {
        return EUIUtils.arrayList();
    }

    @Override
    public PCLCardData getSymbolicCard() {
        return QuestionMark.DATA;
    }

    @Override
    public PCLTrophies getTrophies() {
        return null;
    }

    public void onSelect(CharacterOption option) {
        AbstractCard.CardColor newColor = option.c.getCardColor();
        if (newColor != color) {
            color = newColor;
            clearPresets();
        }
    }

    @Override
    public void onVictory(int ascensionLevel, int trophyLevel, int score) {
        // No-op
    }

    @Override
    protected void setDefaultRelicsForData(PCLLoadoutData data) {
        // No-op
    }
}
