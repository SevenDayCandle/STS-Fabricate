package pinacolada.patches.screens;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.SingleRelicViewPopup;
import extendedui.text.EUISmartText;
import extendedui.utilities.EUIClassUtils;
import pinacolada.relics.PCLRelic;

public class SingleRelicViewPopupPatches {
    @SpirePatch(clz = SingleRelicViewPopup.class, method = "renderDescription", paramtypez = {SpriteBatch.class})
    public static class SingleRelicViewPopup_Open {
        @SpireInsertPatch(rloc = 0)
        public static SpireReturn<Void> insert(SingleRelicViewPopup __instance, SpriteBatch sb) {
            AbstractRelic temp = EUIClassUtils.getField(__instance, "relic");
            if (temp instanceof PCLRelic && temp.isSeen) {
                float width = ReflectionHacks.getPrivateStatic(SingleRelicViewPopup.class, "DESC_LINE_WIDTH");
                float spacing = ReflectionHacks.getPrivateStatic(SingleRelicViewPopup.class, "DESC_LINE_SPACING");
                float height = FontHelper.getSmartHeight(FontHelper.cardDescFont_N, temp.description, width, spacing) / 2.0F;
                EUISmartText.write(sb, FontHelper.cardDescFont_N, temp.description, (float) Settings.WIDTH / 2.0F - 200.0F * Settings.scale, (float) Settings.HEIGHT / 2.0F - 140.0F * Settings.scale - height, width, spacing, Settings.CREAM_COLOR);
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }
}
