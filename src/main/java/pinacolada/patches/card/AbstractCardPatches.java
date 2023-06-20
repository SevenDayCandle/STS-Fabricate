package pinacolada.patches.card;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CtBehavior;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.tags.EphemeralField;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.dungeon.CombatManager;
import pinacolada.relics.PCLRelic;

public class AbstractCardPatches {
    @SpirePatch(clz = AbstractCard.class, method = "applyPowersToBlock")
    public static class AbstractCard_ApplyPowersToBlock {
        @SpireInsertPatch(localvars = {"tmp"}, locator = Locator.class)
        public static void insertPre(AbstractCard __instance, @ByRef float[] tmp) {
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (r instanceof PCLRelic) {
                    tmp[0] = ((PCLRelic) r).atBlockModify(tmp[0], __instance);
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher matcher = new Matcher.MethodCallMatcher(MathUtils.class, "floor");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "canUse")
    public static class AbstractCard_CardPlayable {
        @SpirePostfixPatch
        public static boolean method(boolean retVal, AbstractCard __instance, AbstractPlayer p, AbstractMonster m) {
            return CombatManager.canPlayCard(__instance, p, m, retVal);
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "resetAttributes")
    public static class AbstractCard_ResetAttributes
    {
        @SpirePostfixPatch
        public static void method(AbstractCard __instance) {
            CombatManager.onCardReset(__instance);
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "triggerOnOtherCardPlayed", paramtypez = {AbstractCard.class})
    public static class CardGroupPatches_TriggerOnOtherCardPlayed {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(AbstractCard __instance, AbstractCard c) {
            if (PCLCardTag.Fragile.has(__instance)) {
                PCLActions.last.exhaust(__instance);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "triggerOnEndOfPlayerTurn")
    public static class CardGroupPatches_TriggerOnEndOfPlayerTurn {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(AbstractCard __instance) {
            // Call the field directly instead of querying card tag, because card tag also checks for purgeOnUse
            if (EphemeralField.value.get(__instance)) {
                PCLActions.last.purge(__instance);
            }
            return SpireReturn.Continue();
        }
    }
}