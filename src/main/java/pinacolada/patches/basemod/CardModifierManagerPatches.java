package pinacolada.patches.basemod;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.dungeon.CombatManager;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

public class CardModifierManagerPatches {
    @SpirePatch(clz = CardModifierManager.class, method = "addModifier")
    public static class CardModifierManagerPatches_AddModifier {
        @SpirePrefixPatch
        public static void insertPre(AbstractCard card, @ByRef AbstractCardModifier[] mod) {
            if (PGR.isLoaded() && GameUtilities.inGame()) {
                mod[0] = CombatManager.onTryAddModifier(card, mod[0]);
            }
        }
    }

    @SpirePatch(clz = CardModifierManager.class, method = "onCardModified")
    public static class CardModifierManagerPatches_OnCardModified {
        @SpirePrefixPatch
        public static void insertPre(AbstractCard card) {
            if (card instanceof PCLCard) {
                ((PCLCard) card).initializeName();
            }
        }
    }
}
