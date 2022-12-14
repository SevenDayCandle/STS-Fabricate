package pinacolada.patches.screens;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.ui.cardView.PCLSingleCardPopup;

public class SingleCardViewPopupPatches
{
    private static final PCLCoreImages Images = PGR.core.images;
    private static final PCLSingleCardPopup betterPopup = new PCLSingleCardPopup();

    @SpirePatch(clz = SingleCardViewPopup.class, method = "open", paramtypez = {AbstractCard.class})
    public static class SingleCardViewPopup_Open
    {
        @SpireInsertPatch(rloc = 0)
        public static SpireReturn<Void> insert(SingleCardViewPopup __instance, AbstractCard card)
        {
            PCLCard c = EUIUtils.safeCast(card, PCLCard.class);
            if (c != null && !c.isFlipped)
            {
                PGR.core.cardPopup.open(c, null);

                return SpireReturn.Return(null);
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "open", paramtypez = {AbstractCard.class, CardGroup.class})
    public static class SingleCardViewPopup_Open2
    {
        @SpireInsertPatch(rloc = 0)
        public static SpireReturn<Void> insert(SingleCardViewPopup __instance, AbstractCard card, CardGroup group)
        {
            PCLCard c = EUIUtils.safeCast(card, PCLCard.class);
            if (c != null && !c.isFlipped)
            {
                PGR.core.cardPopup.open(c, group);

                return SpireReturn.Return(null);
            }

            return SpireReturn.Continue();
        }
    }
}
