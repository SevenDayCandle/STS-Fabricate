package pinacolada.patches.relic;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import pinacolada.utilities.GameUtilities;

import static extendedui.ui.AbstractScreen.EUI_SCREEN;

// Copied and modified from STS-AnimatorMod
// TODO Change values as needed
public class AbstractRelicPatches
{
    @SpirePatch(clz = AbstractRelic.class, method = "getPrice")
    public static class AbstractRelicPatches_GetPrice
    {
        @SpirePrefixPatch
        public static SpireReturn insert(AbstractRelic __instance)
        {
            if (GameUtilities.isPCLPlayerClass() && __instance.tier == AbstractRelic.RelicTier.BOSS)
            {
                return SpireReturn.Return(333);
            }
            else
            {
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch(clz = AbstractRelic.class, method = "update")
    public static class AbstractRelicPatches_Update
    {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn insert(AbstractRelic __instance)
        {
            if (AbstractDungeon.screen == EUI_SCREEN)
            {
                __instance.scale = MathHelper.scaleLerpSnap(__instance.scale, Settings.scale);
                __instance.hb.unhover();

                return SpireReturn.Return(null);
            }
            else
            {
                return SpireReturn.Continue();
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "player");

                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}