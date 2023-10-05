package pinacolada.patches.eui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import extendedui.EUIGameUtils;
import extendedui.ui.screens.PotionPoolScreen;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.potions.PCLCustomPotionSlot;
import pinacolada.potions.PCLPotion;
import pinacolada.resources.PGR;
import pinacolada.ui.editor.potion.PCLCustomPotionEditScreen;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class PotionPoolScreenPatches {
    protected static PCLEffectWithCallback<?> currentEffect;
    public static PotionPoolScreen.DebugOption editPotion = new PotionPoolScreen.DebugOption(PGR.core.strings.misc_edit, PotionPoolScreenPatches::edit);

    public static void edit(PotionPoolScreen pool, AbstractPotion c) {
        PCLCustomPotionSlot potionSlot = PCLCustomPotionSlot.get(c.ID);
        if (potionSlot != null) {
            if (EUIGameUtils.inGame()) {
                AbstractDungeon.overlayMenu.cancelButton.hide();
            }
            GameUtilities.setTopPanelVisible(false);
            currentEffect = new PCLCustomPotionEditScreen(potionSlot, true)
                    .setOnSave(() -> {
                        PCLCustomPotionSlot.editSlot(potionSlot, potionSlot.ID); // Card slot ID should never change
                        for (AbstractPotion r : AbstractDungeon.player.potions) {
                            if (r instanceof PCLPotion && c.ID.equals(r.ID)) {
                                ((PCLPotion) r).initialize();
                            }
                        }
                    })
                    .addCallback(() -> {
                        GameUtilities.setTopPanelVisible(true);
                        if (EUIGameUtils.inGame()) {
                            AbstractDungeon.overlayMenu.cancelButton.show(MasterDeckViewScreen.TEXT[1]);
                        }
                    });
        }

    }

    @SpirePatch(clz = PotionPoolScreen.class, method = "getOptions")
    public static class PotionPoolScreenPatches_GetOptions {
        @SpirePostfixPatch
        public static ArrayList<PotionPoolScreen.DebugOption> postfix(ArrayList<PotionPoolScreen.DebugOption> retVal, AbstractPotion c) {
            if (PCLCustomPotionSlot.get(c.ID) != null) {
                retVal.add(editPotion);
            }
            return retVal;
        }
    }

    @SpirePatch(clz = PotionPoolScreen.class, method = "update")
    public static class PotionPoolScreenPatches_Update {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(PotionPoolScreen __instance) {
            if (currentEffect != null) {
                PGR.blackScreen.update();
                currentEffect.update();

                if (currentEffect.isDone) {
                    currentEffect = null;
                }
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = PotionPoolScreen.class, method = "render")
    public static class PotionPoolScreenPatches_Render {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(PotionPoolScreen __instance, SpriteBatch sb) {
            if (currentEffect != null) {
                PGR.blackScreen.render(sb);
                currentEffect.render(sb);
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
}
