package pinacolada.patches.relic;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import pinacolada.relics.PCLDynamicRelic;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

// Copied and modified from STS-AnimatorMod
public class AbstractRelicPatches {

    @SpirePatch(clz = AbstractRelic.class, method = SpirePatch.CONSTRUCTOR)
    public static class AbstractRelic_Ctor {
        @SpireInsertPatch(locator = Locator.class)
        public static void insert(AbstractRelic __instance) {
            if (__instance instanceof PCLDynamicRelic) {
                RelicStrings whyDoYouDoThisToMe = ReflectionHacks.getPrivate(__instance, AbstractRelic.class, "relicStrings");
                if (whyDoYouDoThisToMe == null) {
                    ReflectionHacks.setPrivate(__instance, AbstractRelic.class, "relicStrings", PGR.getRelicStrings(Circlet.ID));
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(RelicStrings.class, "DESCRIPTIONS");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    // TODO Allow custom prices
    @SpirePatch(clz = AbstractRelic.class, method = "getPrice")
    public static class AbstractRelicPatches_GetPrice {
        @SpirePrefixPatch
        public static SpireReturn<Integer> insert(AbstractRelic __instance) {
            if (GameUtilities.isPCLPlayerClass() && __instance.tier == AbstractRelic.RelicTier.BOSS) {
                return SpireReturn.Return(333);
            }
            else {
                return SpireReturn.Continue();
            }
        }
    }
}