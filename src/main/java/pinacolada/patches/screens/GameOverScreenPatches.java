package pinacolada.patches.screens;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.GameOverScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.utilities.EUIClassUtils;
import pinacolada.resources.PCLAbstractPlayerData;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

public class GameOverScreenPatches
{
    public static final int MAX_LEVEL = PCLAbstractPlayerData.MAX_UNLOCK_LEVEL;
    public static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("DeathScreen");
    protected static final String SCORE = "score";
    protected static final String UNLOCK_LEVEL = "unlockLevel";
    protected static final String UNLOCK_COST = "unlockCost";
    protected static final String NEXT_UNLOCK_COST = "nextUnlockCost";

    protected static Color getColor(GameOverScreen __instance, String name)
    {
        return ReflectionHacks.getPrivate(__instance, GameOverScreen.class, name);
    }

    protected static boolean getBool(GameOverScreen __instance, String name)
    {
        return ReflectionHacks.getPrivate(__instance, GameOverScreen.class, name);
    }

    protected static float getFloat(GameOverScreen __instance, String name)
    {
        return ReflectionHacks.getPrivate(__instance, GameOverScreen.class, name);
    }

    protected static int getInt(GameOverScreen __instance, String name)
    {
        return ReflectionHacks.getPrivate(__instance, GameOverScreen.class, name);
    }

    @SpirePatch(clz = GameOverScreen.class, method = "calculateUnlockProgress")
    public static class GameOverScreen_calculateUnlockProgress
    {
        @SpirePrefixPatch
        public static SpireReturn prefix(GameOverScreen __instance)
        {
            if (!GameUtilities.isPCLPlayerClass())
            {
                return SpireReturn.Continue();
            }

            EUIClassUtils.setField(__instance, "score", GameOverScreen.calcScore(GameOverScreen.isVictory));
            EUIClassUtils.setField(__instance, "unlockLevel", UnlockTracker.getUnlockLevel(AbstractDungeon.player.chosenClass));

            if (getInt(__instance, "unlockLevel") >= MAX_LEVEL)
            {
                EUIClassUtils.setField(__instance, "maxLevel", true);
                return SpireReturn.Return(null);
            }

            if (getInt(__instance, "score") == 0)
            {
                EUIClassUtils.setField(__instance, "playedWhir", true);
            }

            EUIClassUtils.setField(__instance, "unlockProgress", (float) UnlockTracker.getCurrentProgress(AbstractDungeon.player.chosenClass));
            EUIClassUtils.setField(__instance, "unlockTargetStart", getFloat(__instance, "unlockProgress"));
            EUIClassUtils.setField(__instance, "unlockCost", UnlockTracker.getCurrentScoreCost(AbstractDungeon.player.chosenClass));
            EUIClassUtils.setField(__instance, "unlockTargetProgress", getFloat(__instance, "unlockProgress") + getInt(__instance, "score"));
            EUIClassUtils.setField(__instance, "nextUnlockCost", PGR.getResources(AbstractDungeon.player.chosenClass).getUnlockCost(1, true));

            if (getFloat(__instance,"unlockTargetProgress") >= getInt(__instance,"unlockCost"))
            {
                EUIClassUtils.setField(__instance, "unlockBundle", UnlockTracker.getUnlockBundle(AbstractDungeon.player.chosenClass, getInt(__instance, "unlockLevel")));
                if (getInt(__instance, "unlockLevel") == (MAX_LEVEL - 1))
                {
                    EUIClassUtils.setField(__instance, "unlockTargetProgress", (float) getInt(__instance, "unlockCost"));
                }
                else if (getFloat(__instance, "unlockTargetProgress") > (getInt(__instance, "unlockCost") - getFloat(__instance, "unlockProgress") + getInt(__instance, "nextUnlockCost") - 1.0F))
                {
                    EUIClassUtils.setField(__instance, "unlockTargetProgress", (getInt(__instance, "unlockCost") - getFloat(__instance, "unlockProgress") + getInt(__instance, "nextUnlockCost") - 1.0F));
                }
            }

            UnlockTracker.addScore(AbstractDungeon.player.chosenClass, getInt(__instance, "score"));

            EUIClassUtils.setField(__instance, "progressPercent", getFloat(__instance, "unlockTargetStart") / getInt(__instance, "unlockCost"));

            return SpireReturn.Return(null);
        }
    }

    @SpirePatch(clz = GameOverScreen.class, method = "renderProgressBar")
    public static class GameOverScreen_renderProgressBar
    {
        @SpirePrefixPatch
        public static SpireReturn insert(GameOverScreen __instance, SpriteBatch sb)
        {
            if (!GameUtilities.isPCLPlayerClass())
            {
                return SpireReturn.Continue();
            }
            if (getBool(__instance, "maxLevel"))
            {
                return SpireReturn.Return(null);
            }

            getColor(__instance, "whiteUiColor").a = getFloat(__instance, "progressBarAlpha") * 0.3f;
            sb.setColor(getColor(__instance, "whiteUiColor"));

            sb.draw(ImageMaster.WHITE_SQUARE_IMG, getFloat(__instance, "progressBarX"),
                    Settings.HEIGHT * 0.2F, getFloat(__instance, "progressBarWidth"), 14.0F * Settings.scale);
            sb.setColor(new Color(1.0F, 0.8F, 0.3F, getFloat(__instance, "progressBarAlpha") * 0.9F));
            sb.draw(ImageMaster.WHITE_SQUARE_IMG, getFloat(__instance, "progressBarX"), Settings.HEIGHT * 0.2F,
                    getFloat(__instance, "progressBarWidth") * getFloat(__instance, "progressPercent"), 14.0F * Settings.scale);
            sb.setColor(new Color(0.0F, 0.0F, 0.0F, getFloat(__instance, "progressBarAlpha") * 0.25F));
            sb.draw(ImageMaster.WHITE_SQUARE_IMG, getFloat(__instance, "progressBarX"), Settings.HEIGHT * 0.2F,
                    getFloat(__instance, "progressBarWidth") * getFloat(__instance, "progressPercent"), 4.0F * Settings.scale);
            String text = "[" + (int) getFloat(__instance, "unlockProgress") + "/" + getInt(__instance, "unlockCost") + "]";
            getColor(__instance, "creamUiColor").a = getFloat(__instance, "progressBarAlpha") * 0.9F;
            FontHelper.renderFontLeftTopAligned(sb, FontHelper.topPanelInfoFont, text,
                    576.0F * Settings.xScale, Settings.HEIGHT * 0.2F - 12.0F * Settings.scale, getColor(__instance, "creamUiColor"));

            if (getInt(__instance, "unlockLevel") == (MAX_LEVEL - 1))
            {
                text = uiStrings.TEXT[42] + (MAX_LEVEL - getInt(__instance, "unlockLevel"));
            }
            else
            {
                text = uiStrings.TEXT[41] + (MAX_LEVEL - getInt(__instance, "unlockLevel"));
            }

            FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelInfoFont, text,
                    1344.0F * Settings.xScale, Settings.HEIGHT * 0.2F - 12.0F * Settings.scale, getColor(__instance, "creamUiColor"));

            return SpireReturn.Return(null);
        }
    }
}