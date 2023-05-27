package pinacolada.patches.basemod;

import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLCard;

public class CardModifierManagerPatches {
    @SpirePatch(clz = CardModifierManager.class, method = "onCardModified")
    public static class CardModifierManagerPatches_AddModifier {
        @SpirePrefixPatch
        public static void insertPre(AbstractCard card) {
            if (card instanceof PCLCard) {
                ((PCLCard) card).initializeName();
            }
        }
    }
}
