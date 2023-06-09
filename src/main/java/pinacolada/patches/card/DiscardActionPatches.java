package pinacolada.patches.card;


import basemod.devcommands.hand.HandDiscard;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DiscardSpecificCardAction;
import com.megacrit.cardcrawl.actions.defect.ScrapeFollowUpAction;
import com.megacrit.cardcrawl.actions.unique.GamblingChipAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import pinacolada.dungeon.CombatManager;

public class DiscardActionPatches {

    @SpirePatch(clz = HandDiscard.class, method = "execute")
    public static class HandDiscard_Execute {
        @SpireInsertPatch(localvars = {"c"}, locator = Locator.class)
        public static void insertPre(HandDiscard __instance, AbstractCard c) {
            CombatManager.onCardDiscarded(c);
        }
    }

    @SpirePatch(clz = DiscardAction.class, method = "update")
    public static class DiscardAction_Update {
        @SpireInsertPatch(localvars = {"c"}, locator = Locator.class)
        public static void insertPre(DiscardAction __instance, AbstractCard c) {
            CombatManager.onCardDiscarded(c);
        }
    }

    @SpirePatch(clz = DiscardSpecificCardAction.class, method = "update")
    public static class DiscardSpecificCardAction_Update {
        @SpireInsertPatch(localvars = {"targetCard"}, locator = Locator.class)
        public static void insertPre(DiscardSpecificCardAction __instance, AbstractCard targetCard) {
            CombatManager.onCardDiscarded(targetCard);
        }
    }

    @SpirePatch(clz = GamblingChipAction.class, method = "update")
    public static class GamblingChipAction_Update {
        @SpireInsertPatch(localvars = {"c"}, locator = Locator.class)
        public static void insertPre(GamblingChipAction __instance, AbstractCard c) {
            CombatManager.onCardDiscarded(c);
        }
    }

    @SpirePatch(clz = ScrapeFollowUpAction.class, method = "update")
    public static class ScrapeFollowUpAction_Update {
        @SpireInsertPatch(localvars = {"c"}, locator = Locator.class)
        public static void insertPre(ScrapeFollowUpAction __instance, AbstractCard c) {
            CombatManager.onCardDiscarded(c);
        }
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "triggerOnManualDiscard");
            return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
