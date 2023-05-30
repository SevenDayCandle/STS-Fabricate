package pinacolada.patches.eui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import extendedui.EUIGameUtils;
import extendedui.ui.screens.RelicPoolScreen;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.relics.PCLPointerRelic;
import pinacolada.resources.PGR;
import pinacolada.ui.editor.relic.PCLCustomRelicEditRelicScreen;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class RelicPoolScreenPatches {
    protected static PCLEffectWithCallback<?> currentEffect;
    public static RelicPoolScreen.DebugOption editRelic = new RelicPoolScreen.DebugOption(PGR.core.strings.misc_edit, RelicPoolScreenPatches::editRelic);

    public static void editRelic(RelicPoolScreen pool, AbstractRelic c) {
        PCLCustomRelicSlot relicSlot = PCLCustomRelicSlot.get(c.relicId);
        if (relicSlot != null) {
            if (EUIGameUtils.inGame()) {
                AbstractDungeon.overlayMenu.cancelButton.hide();
            }
            GameUtilities.setTopPanelVisible(false);
            currentEffect = new PCLCustomRelicEditRelicScreen(relicSlot, true)
                    .setOnSave(() -> {
                        relicSlot.commitBuilder();
                        for (AbstractRelic r : AbstractDungeon.player.relics) {
                            if (c.relicId.equals(r.relicId) && r instanceof PCLPointerRelic) {
                                ((PCLPointerRelic) r).reset();
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

    @SpirePatch(clz = RelicPoolScreen.class, method = "getOptions")
    public static class RelicPoolScreenPatches_GetOptions {
        @SpirePostfixPatch
        public static ArrayList<RelicPoolScreen.DebugOption> postfix(ArrayList<RelicPoolScreen.DebugOption> retVal, AbstractRelic c) {
            if (PCLCustomRelicSlot.get(c.relicId) != null) {
                retVal.add(editRelic);
            }
            return retVal;
        }
    }

    @SpirePatch(clz = RelicPoolScreen.class, method = "removeRelicFromPool")
    public static class RelicPoolScreenPatches_RemoveRelicFromPool {
        @SpirePostfixPatch
        public static void postfix(RelicPoolScreen __instance, AbstractRelic c) {
            PGR.dungeon.banRelic(c.relicId);
        }
    }

    @SpirePatch(clz = RelicPoolScreen.class, method = "update")
    public static class RelicPoolScreenPatches_Update {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(RelicPoolScreen __instance) {
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

    @SpirePatch(clz = RelicPoolScreen.class, method = "render")
    public static class RelicPoolScreenPatches_Render {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(RelicPoolScreen __instance, SpriteBatch sb) {
            if (currentEffect != null) {
                PGR.blackScreen.render(sb);
                currentEffect.render(sb);
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
}
