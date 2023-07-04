package pinacolada.patches.screens;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.SingleRelicViewPopup;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.patches.game.OverlayMenuPatches;
import extendedui.text.EUISmartText;
import extendedui.utilities.EUIClassUtils;
import extendedui.utilities.EUIFontHelper;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import pinacolada.relics.PCLRelic;

// TODO custom single relic view popup screen
public class SingleRelicViewPopupPatches {
    protected static final float IMAGE_Y = (float)Settings.HEIGHT / 2.0F - 64.0F + 76.0F * Settings.scale;

    @SpirePatch(clz = SingleRelicViewPopup.class, method = "renderDescription", paramtypez = {SpriteBatch.class})
    public static class SingleRelicViewPopup_RenderDescription {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(SingleRelicViewPopup __instance, SpriteBatch sb) {
            AbstractRelic temp = EUIClassUtils.getField(__instance, "relic");
            if (temp instanceof PCLRelic && temp.isSeen) {
                float width = ReflectionHacks.getPrivateStatic(SingleRelicViewPopup.class, "DESC_LINE_WIDTH");
                float spacing = ReflectionHacks.getPrivateStatic(SingleRelicViewPopup.class, "DESC_LINE_SPACING");
                float height = EUISmartText.getSmartHeight(EUIFontHelper.cardDescriptionFontNormal, temp.description, width, spacing) / 2.0F;
                EUISmartText.write(sb, EUIFontHelper.cardDescriptionFontNormal, temp.description, (float) Settings.WIDTH / 2.0F - 200.0F * Settings.scale, (float) Settings.HEIGHT / 2.0F - 140.0F * Settings.scale - height, width, spacing, Settings.CREAM_COLOR);
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = SingleRelicViewPopup.class, method = "renderName", paramtypez = {SpriteBatch.class})
    public static class SingleRelicViewPopup_RenderName {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(SingleRelicViewPopup __instance, SpriteBatch sb) {
            AbstractRelic temp = EUIClassUtils.getField(__instance, "relic");
            if (temp instanceof PCLRelic && temp.isSeen) {
                FontHelper.renderWrappedText(sb, FontHelper.SCP_cardDescFont, ((PCLRelic) temp).getName(), (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F + 280.0F * Settings.scale, 9999.0F, Settings.CREAM_COLOR, 0.9F);
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = SingleRelicViewPopup.class, method = "renderRelicImage", paramtypez = {SpriteBatch.class})
    public static class SingleRelicViewPopup_RenderRelicImage {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(SingleRelicViewPopup __instance, SpriteBatch sb) {
            AbstractRelic temp = EUIClassUtils.getField(__instance, "relic");
            if (temp instanceof PCLRelic) {
                if (UnlockTracker.isRelicLocked(temp.relicId)) {
                    sb.setColor(new Color(0.0F, 0.0F, 0.0F, 0.5F));
                    sb.draw(ImageMaster.RELIC_LOCK_OUTLINE, (float)Settings.WIDTH / 2.0F - 64.0F, IMAGE_Y, 64.0F, 64.0F, 128.0F, 128.0F, Settings.scale * 2.0F, Settings.scale * 2.0F, 0.0F, 0, 0, 128, 128, false, false);
                    sb.setColor(Color.WHITE);
                    sb.draw(ImageMaster.RELIC_LOCK, (float)Settings.WIDTH / 2.0F - 64.0F, IMAGE_Y, 64.0F, 64.0F, 128.0F, 128.0F, Settings.scale * 2.0F, Settings.scale * 2.0F, 0.0F, 0, 0, 128, 128, false, false);
                } else {
                    if (!temp.isSeen) {
                        sb.setColor(new Color(1.0F, 1.0F, 1.0F, 0.75F));
                    } else {
                        sb.setColor(new Color(0.0F, 0.0F, 0.0F, 0.5F));
                    }

                    sb.draw(temp.outlineImg, (float)Settings.WIDTH / 2.0F - 64.0F, IMAGE_Y, 64.0F, 64.0F, 128.0F, 128.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 128, 128, false, false);
                    if (!temp.isSeen) {
                        sb.setColor(Color.BLACK);
                    } else {
                        sb.setColor(Color.WHITE);
                    }

                    sb.draw(temp.img, (float)Settings.WIDTH / 2.0F - 64.0F, IMAGE_Y, 64.0F, 64.0F, 128.0F, 128.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 128, 128, false, false);
                }

                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }
}
