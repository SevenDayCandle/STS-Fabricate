package pinacolada.patches.screens;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.mainMenu.SaveSlotScreen;
import com.megacrit.cardcrawl.ui.panels.RenamePopup;
import extendedui.utilities.EUIClassUtils;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLLoadoutDataInfo;

public class SaveSlotScreenPatches {
    @SpirePatch(clz = RenamePopup.class, method = "confirm")
    public static class RenamePopup_Confirm {
        @SpireInsertPatch(locator = Locator.class)
        public static void postfix(RenamePopup __instance) {
            reload(EUIClassUtils.getField(__instance, "slot"));
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(CardCrawlGame.class, "playerName");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(clz = SaveSlotScreen.class, method = "confirm", paramtypez = {int.class})
    public static class SaveSlotScreenPatches_Confirm {
        @SpirePostfixPatch
        public static void postfix(SaveSlotScreen __instance, int slot) {
            reload(slot);
        }
    }

    private static void reload(int slot) {
        PGR.config.load(slot);
        for (PCLResources<?, ?, ?, ?> resources : PGR.getRegisteredResources()) {
            resources.reload();
        }
        PCLLoadoutDataInfo.reloadLoadouts();
    }
}
