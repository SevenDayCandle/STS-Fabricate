package pinacolada.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIGameUtils;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;

public class ExtendedUIPatches
{
    @SpirePatch(clz = EUIGameUtils.class, method = "textForRarity")
    public static class ExtendedUIPatches_TextForRarity
    {
        @SpirePrefixPatch
        public static SpireReturn<String> prefix(AbstractCard.CardRarity type)
        {
            if (type == PCLEnum.CardRarity.SECRET)
            {
                return SpireReturn.Return(PGR.core.strings.cardType.secretRare);
            }
            else if (type == PCLEnum.CardRarity.LEGENDARY)
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
            if (type == PCLEnum.CardType.SUMMON)
            {
                return SpireReturn.Return(PGR.core.tooltips.summon.title);
            }
            return SpireReturn.Continue();
        }
    }
}
