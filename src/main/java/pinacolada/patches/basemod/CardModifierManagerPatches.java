package pinacolada.patches.basemod;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
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

    // Called by both PCLCard and AbstractCard, so we don't need to patch two places :)
    @SpirePatch(clz = CardModifierManager.class, method = "onModifyBlock")
    public static class CardModifierManagerPatches_OnModifyBlock {
        @SpirePostfixPatch
        public static float postfix(float retVal, float damage, AbstractCard card) {
            return CombatManager.onModifyBlockFirst(retVal, card);
        }
    }

    @SpirePatch(clz = CardModifierManager.class, method = "onModifyBlockFinal")
    public static class CardModifierManagerPatches_OnModifyBlockLast {
        @SpirePostfixPatch
        public static float postfix(float retVal, float damage, AbstractCard card) {
            return CombatManager.onModifyBlockFirst(retVal, card);
        }
    }

    @SpirePatch(clz = CardModifierManager.class, method = "onModifyDamage")
    public static class CardModifierManagerPatches_OnModifyDamageFirst {
        @SpirePostfixPatch
        public static float postfix(float retVal, float damage, AbstractCard card, AbstractMonster mo) {
            return CombatManager.onModifyDamageGiveFirst(retVal, card.damageTypeForTurn, GameUtilities.getCardOwner(card), mo, card);
        }
    }

    @SpirePatch(clz = CardModifierManager.class, method = "onModifyDamageFinal")
    public static class CardModifierManagerPatches_OnModifyDamageLast {
        @SpirePostfixPatch
        public static float postfix(float retVal, float damage, AbstractCard card, AbstractMonster mo) {
            return CombatManager.onModifyDamageGiveLast(retVal, card.damageTypeForTurn, GameUtilities.getCardOwner(card), mo, card);
        }
    }
}
