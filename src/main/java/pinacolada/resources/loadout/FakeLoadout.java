package pinacolada.resources.loadout;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import extendedui.ui.screens.CustomCardLibraryScreen;
import pinacolada.cards.base.PCLCardData;
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
                if (c.rarity == AbstractCard.CardRarity.COMMON || (c.rarity == AbstractCard.CardRarity.BASIC && !(c.hasTag(AbstractCard.CardTags.STARTER_STRIKE) || c.hasTag(AbstractCard.CardTags.STARTER_DEFEND)))) {
                    slot.addItem(c.cardID, 5);
                }
            }
        }
    }
}
