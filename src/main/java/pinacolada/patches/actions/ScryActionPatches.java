package pinacolada.patches.actions;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import javassist.CtBehavior;
import pinacolada.dungeon.CombatManager;

@SpirePatch(clz = ScryAction.class, method = "update")
public class ScryActionPatches {
    @SpireInsertPatch(localvars = {"c"}, locator = Locator.class)
    public static void insertPre(ScryAction __instance, AbstractCard c) {
        CombatManager.onCardScry(c);
    }

    @SpireInsertPatch(locator = Locator2.class)
    public static void insertPre2(ScryAction __instance) {
        CombatManager.onScryAction(__instance);
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            final Matcher matcher = new Matcher.MethodCallMatcher(CardGroup.class, "moveToDiscardPile");
            return LineFinder.findInOrder(ctBehavior, matcher);
        }
    }

    private static class Locator2 extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            Matcher matcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
            return LineFinder.findInOrder(ctBehavior, matcher);
        }
    }
}

