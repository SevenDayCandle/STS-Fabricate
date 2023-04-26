package pinacolada.patches.eui;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIGameUtils;
import extendedui.ui.TextureCache;
import extendedui.ui.cardFilter.filters.CardTypePanelFilterItem;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class EUIPatches {
    protected static final CardTypePanelFilterItem SUMMON = new CardTypePanelFilterItem(PCLEnum.CardType.SUMMON);

    @SpirePatch(clz = CardTypePanelFilterItem.class, method = "get")
    public static class CardTypePanelFilterItem_Get {
        @SpirePrefixPatch
        public static SpireReturn<CardTypePanelFilterItem> prefix(AbstractCard.CardType type) {
            if (type == PCLEnum.CardType.SUMMON) {
                return SpireReturn.Return(SUMMON);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = EUIGameUtils.class, method = "canSeeAnyColorCard")
    public static class ExtendedUIPatches_CanSeeAnyColorCard {
        @SpirePostfixPatch
        public static boolean postfix(boolean retVal, AbstractCard c) {
            return retVal
                    && (c.color != AbstractCard.CardColor.COLORLESS || PGR.getResources(GameUtilities.getActingColor()).containsColorless(c))
                    && !PGR.dungeon.bannedCards.contains(c.cardID);
        }
    }

    @SpirePatch(clz = EUIGameUtils.class, method = "getEveryColorCard")
    public static class ExtendedUIPatches_GetEveryColorCard {
        @SpirePostfixPatch
        public static ArrayList<AbstractCard> postfix(ArrayList<AbstractCard> retVal) {
            AbstractCard.CardColor color = GameUtilities.getActingColor();
            if (color != AbstractCard.CardColor.COLORLESS) {
                for (PCLCustomCardSlot c : PCLCustomCardSlot.getCards(color)) {
                    retVal.add(c.makeFirstCard(true));
                }
            }
            for (PCLCustomCardSlot c : PCLCustomCardSlot.getCards(AbstractCard.CardColor.COLORLESS)) {
                retVal.add(c.makeFirstCard(true));
            }
            return retVal;
        }
    }

    @SpirePatch(clz = EUIGameUtils.class, method = "iconForType")
    public static class ExtendedUIPatches_IconForType {
        @SpirePrefixPatch
        public static SpireReturn<TextureCache> prefix(AbstractCard.CardType type) {
            if (type == PCLEnum.CardType.SUMMON) {
                return SpireReturn.Return(PCLCoreImages.Types.summon);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = EUIGameUtils.class, method = "textForRarity")
    public static class ExtendedUIPatches_TextForRarity {
        @SpirePrefixPatch
        public static SpireReturn<String> prefix(AbstractCard.CardRarity type) {
            if (type == PCLEnum.CardRarity.SECRET) {
                return SpireReturn.Return(PGR.core.strings.ctype_secretRare);
            }
            else if (type == PCLEnum.CardRarity.LEGENDARY) {
                return SpireReturn.Return(PGR.core.strings.ctype_legendary);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = EUIGameUtils.class, method = "textForType")
    public static class ExtendedUIPatches_TextForType {
        @SpirePrefixPatch
        public static SpireReturn<String> prefix(AbstractCard.CardType type) {
            if (type == PCLEnum.CardType.SUMMON) {
                return SpireReturn.Return(PGR.core.tooltips.summon.title);
            }
            return SpireReturn.Continue();
        }
    }
}
