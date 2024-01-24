package pinacolada.patches.screens;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.controller.CInputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;

public class MainMenuScreenPatches {
    @SpirePatch(clz = MainMenuScreen.class, method = "setMainMenuButtons")
    public static class MainMenuScreenPatches_SetMainMenuButtons {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"index"}
        )
        public static void Insert(Object __obj_instance, @ByRef int[] index) {
            MainMenuScreen __instance = (MainMenuScreen) __obj_instance;
            __instance.buttons.add(new MenuButton(PCLEnum.Buttons.CUSTOM, index[0]++));
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(MenuButton.ClickResult.class, "INFO");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = MenuButton.class, method = "setLabel")
    public static class MainMenuScreenPatches_SetLabel {
        @SpirePostfixPatch
        public static void setLabel(MenuButton __instance, @ByRef String[] ___label) {
            if (__instance.result == PCLEnum.Buttons.CUSTOM) {
                ___label[0] = PGR.core.strings.cedit_customCards;
            }
        }
    }

    @SpirePatch2(clz = MenuButton.class, method = "buttonEffect")
    public static class MainMenuScreenPatches_ButtonEffect {
        @SpirePostfixPatch
        public static void openScreen(MenuButton __instance) {
            if (__instance.result == PCLEnum.Buttons.CUSTOM) {
                CardCrawlGame.mainMenuScreen.panelScreen.open(PCLEnum.Menus.CUSTOM);
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
            if (__instance.panelScreen.panels.isEmpty()) {
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
