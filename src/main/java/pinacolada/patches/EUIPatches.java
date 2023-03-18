package pinacolada.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIGameUtils;
import extendedui.ui.TextureCache;
import extendedui.ui.cardFilter.filters.CardTypePanelFilterItem;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;

public class EUIPatches
{
    protected static final CardTypePanelFilterItem SUMMON = new CardTypePanelFilterItem(PCLEnum.CardType.SUMMON);

    @SpirePatch(clz = CardTypePanelFilterItem.class, method = "get")
    public static class CardTypePanelFilterItem_Get
    {
        @SpirePrefixPatch
        public static SpireReturn<CardTypePanelFilterItem> prefix(AbstractCard.CardType type)
        {
            if (type == PCLEnum.CardType.SUMMON)
            {
                return SpireReturn.Return(SUMMON);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = EUIGameUtils.class, method = "iconForType")
    public static class ExtendedUIPatches_IconForType
    {
        @SpirePrefixPatch
        public static SpireReturn<TextureCache> prefix(AbstractCard.CardType type)
        {
            if (type == PCLEnum.CardType.SUMMON)
            {
                return SpireReturn.Return(PCLCoreImages.Types.summon);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = EUIGameUtils.class, method = "textForRarity")
    public static class ExtendedUIPatches_TextForRarity
    {
        @SpirePrefixPatch
        public static SpireReturn<String> prefix(AbstractCard.CardRarity type)
        {
            if (type == PCLEnum.CardRarity.SECRET)
            {
                return SpireReturn.Return(PGR.core.strings.ctype_secretRare);
            }
            else if (type == PCLEnum.CardRarity.LEGENDARY)
            {
                return SpireReturn.Return(PGR.core.strings.ctype_legendary);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = EUIGameUtils.class, method = "textForType")
    public static class ExtendedUIPatches_TextForType
    {
        @SpirePrefixPatch
        public static SpireReturn<String> prefix(AbstractCard.CardType type)
        {
            if (type == PCLEnum.CardType.SUMMON)
            {
                return SpireReturn.Return(PGR.core.tooltips.summon.title);
            }
            return SpireReturn.Continue();
        }
    }
}
