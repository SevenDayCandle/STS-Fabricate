package pinacolada.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIGameUtils;
import pinacolada.resources.PGR;

public class ExtendedUIPatches
{
    @SpirePatch(clz = EUIGameUtils.class, method = "textForRarity")
    public static class ExtendedUIPatches_TextForRarity
    {
        @SpirePrefixPatch
        public static SpireReturn<String> prefix(AbstractCard.CardRarity type)
        {
            if (type == PGR.Enums.CardRarity.SECRET)
            {
                return SpireReturn.Return(PGR.core.strings.cardType.secretRare);
            }
            else if (type == PGR.Enums.CardRarity.LEGENDARY)
            {
                return SpireReturn.Return(PGR.core.strings.cardType.legendary);
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
            if (type == PGR.Enums.CardType.SUMMON)
            {
                return SpireReturn.Return(PGR.core.tooltips.summon.title);
            }
            return SpireReturn.Continue();
        }
    }
}
