package pinacolada.patches.screens;

import basemod.CustomCharacterSelectScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import extendedui.EUI;
import extendedui.ui.AbstractMenuScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import pinacolada.resources.PGR;

public class CharacterSelectScreenPatches {


    @SpirePatch(clz = CharacterSelectScreen.class, method = "initialize")
    public static class CharacterSelectScreen_Initialize {
        @SpirePostfixPatch
        public static void initialize(CharacterSelectScreen __instance) {
            PGR.charSelectProvider.initialize(__instance);
        }
    }

    @SpirePatch(clz = CharacterSelectScreen.class, method = "open")
    public static class CharacterSelectScreen_Open {
        @SpirePostfixPatch
        public static void initialize(CharacterSelectScreen __instance) {
            PGR.charSelectProvider.playEffect = null;
        }
    }

    @SpirePatch(clz = CharacterSelectScreen.class, method = "update")
    public static class CharacterSelectScreen_Update {
        @SpirePostfixPatch
        public static void postfix(CharacterSelectScreen __instance) {
            PGR.charSelectProvider.updateSelectedCharacter(__instance);
        }

        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(CharacterSelectScreen __instance) {
            return (EUI.currentScreen != null && (CardCrawlGame.mainMenuScreen != null && CardCrawlGame.mainMenuScreen.screen == AbstractMenuScreen.EUI_MENU)) ? SpireReturn.Return() : SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = CharacterSelectScreen.class, method = "render")
    public static class CharacterSelectScreen_Render {
        @SpireInsertPatch(locator = Locator.class)
        public static void insertPre(CharacterSelectScreen __instance, SpriteBatch sb) {
            if (PGR.charSelectProvider.playEffect != null) {
                PGR.charSelectProvider.playEffect.render(sb);
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(CharacterSelectScreen.class, "cancelButton");
                return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(clz = CustomCharacterSelectScreen.class, method = "render")
    public static class CustomCharacterSelectScreen_Render {
        @SpireInsertPatch(locator = IndexLocator.class)
        public static SpireReturn<Void> insertPre(CharacterSelectScreen __instance, SpriteBatch sb) {
            if (PGR.charSelectProvider.hasDialog()) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }


    }

    @SpirePatch(clz = CustomCharacterSelectScreen.class, method = "update")
    public static class CustomCharacterSelectScreen_Update {
        @SpireInsertPatch(locator = IndexLocator.class)
        public static SpireReturn<Void> insertPre(CharacterSelectScreen __instance) {
            if (PGR.charSelectProvider.hasDialog()) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    private static class IndexLocator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(CustomCharacterSelectScreen.class, "maxSelectIndex");
            return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
