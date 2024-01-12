package pinacolada.patches.dungeon;

import basemod.ReflectionHacks;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.VictoryRoom;
import com.megacrit.cardcrawl.screens.GameOverScreen;
import com.megacrit.cardcrawl.screens.GameOverStat;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import extendedui.EUIUtils;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class VictoryPatches {
    private static int glyphBonus = 0;

    private static int getAscensionGlyphScoreBonus(int baseScore) {
        return MathUtils.round((float) baseScore * 0.02F * EUIUtils.sum(PGR.dungeon.ascensionGlyphCounters, Integer::floatValue));
    }

    private static GameOverStat getAscensionGlyphStats() {
        return new GameOverStat(PGR.core.strings.csel_ascensionGlyph, null, String.valueOf(glyphBonus));
    }

    @SpirePatch(clz = VictoryRoom.class, method = "onPlayerEntry")
    public static class VictoryRoomPatches_onEnterRoom {
        @SpirePrefixPatch
        public static void method(VictoryRoom __instance) {
            if (Settings.isStandardRun() && __instance.eType == VictoryRoom.EventType.HEART // this is the room you enter after defeating act 3 Boss
                    && GameUtilities.isPCLPlayerClass(AbstractDungeon.player.chosenClass)) {
                PGR.getPlayerData(AbstractDungeon.player.chosenClass).recordVictory(GameUtilities.getAscensionLevel());
            }
        }
    }

    @SpirePatch(clz = VictoryScreen.class, method = "createGameOverStats")
    public static class VictoryScreenPatches_createGameOverStats {
        @SpirePostfixPatch
        public static void method(VictoryScreen __instance) {
            ArrayList<GameOverStat> stats = ReflectionHacks.getPrivate(__instance, GameOverScreen.class, "stats");
            if (glyphBonus > 0) {
                stats.add(Math.max(0, stats.size() - 2), getAscensionGlyphStats());
            }

            if (Settings.isStandardRun() && GameUtilities.isPCLPlayerClass()) {
                PGR.getPlayerData(AbstractDungeon.player.chosenClass)
                        .recordTrueVictory(GameUtilities.getAscensionLevel(),
                        ReflectionHacks.getPrivate(__instance, GameOverScreen.class, "score"),
                        AbstractDungeon.actNum > 3);
            }
        }
    }

    @SpirePatch(clz = GameOverScreen.class, method = "calcScore", paramtypez = {boolean.class})
    public static class GameOverScreenPatches_calcScore {
        @SpirePostfixPatch
        public static int method(int __result, boolean isVictory) {
            glyphBonus = getAscensionGlyphScoreBonus(__result);
            return __result + glyphBonus;
        }
    }
}
