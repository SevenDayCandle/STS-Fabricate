package pinacolada.patches.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import extendedui.EUI;
import extendedui.ui.AbstractScreen;
import pinacolada.resources.PGR;

public class CharacterOptionPatches {
    @SpirePatch(clz = CharacterOption.class, method = "render")
    public static class CharacterOption_Render {
        @SpirePostfixPatch
        public static void postfix(CharacterOption __instance, SpriteBatch sb) {
            if (__instance.selected && __instance.c != null) {
                PGR.charSelectProvider.renderOption(__instance, sb);
            }
        }
    }

    @SpirePatch(clz = CharacterOption.class, method = "renderRelics")
    public static class CharacterOption_RenderRelics {
        @SpirePrefixPatch
        public static SpireReturn prefix(CharacterOption __instance) {
            return (EUI.currentScreen != null && AbstractDungeon.screen == AbstractScreen.EUI_SCREEN) ? SpireReturn.Return() : SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = CharacterOption.class, method = "update")
    public static class CharacterOption_Update {
        @SpirePostfixPatch
        public static void postfix(CharacterOption __instance) {
            if (__instance.selected && __instance.c != null) {
                PGR.charSelectProvider.updateOption(__instance);
            }
        }
    }

    @SpirePatch(clz = CharacterOption.class, method = "decrementAscensionLevel")
    public static class CharacterOptionPatches_DecrementAscensionLevel {
        @SpirePostfixPatch
        public static void postfix(CharacterOption __instance) {
            PGR.charSelectProvider.updateForAscensionChange(CardCrawlGame.mainMenuScreen.charSelectScreen);
        }
    }

    @SpirePatch(clz = CharacterOption.class, method = "incrementAscensionLevel")
    public static class CharacterOptionPatches_IncrementAscensionLevel {
        @SpirePostfixPatch
        public static void postfix(CharacterOption __instance) {
            PGR.charSelectProvider.updateForAscensionChange(CardCrawlGame.mainMenuScreen.charSelectScreen);
        }
    }
}
