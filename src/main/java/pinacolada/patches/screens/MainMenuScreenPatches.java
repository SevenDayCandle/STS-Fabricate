package pinacolada.patches.screens;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.helpers.controller.CInputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import pinacolada.resources.PCLEnum;
import pinacolada.ui.menu.CustomMenuButton;

public class MainMenuScreenPatches {
    @SpirePatch(clz = MainMenuScreen.class, method = "setMainMenuButtons")
    public static class MainMenuScreenPatches_SetMainMenuButtons {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"index"}
        )
        public static void Insert(Object __obj_instance, @ByRef int[] index) {
            MainMenuScreen __instance = (MainMenuScreen) __obj_instance;
            __instance.buttons.add(new CustomMenuButton(PCLEnum.Buttons.CUSTOM, index[0]++));
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(MenuButton.ClickResult.class, "INFO");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    // Fix rare transient crash that can occur if a controller is plugged in and custom menu screen is clicked
    @SpirePatch(clz = MainMenuScreen.class, method = "updateMenuPanelController")
    public static class MainMenuScreenPatches_UpdateMenuPanelController {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn<Void> Insert(MainMenuScreen __instance) {
            if (__instance.panelScreen.panels.size() <= 0) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CInputHelper.class, "setCursor");
                return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher)[2]};
            }
        }
    }
}
