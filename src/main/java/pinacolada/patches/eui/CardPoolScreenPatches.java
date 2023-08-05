package pinacolada.patches.eui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.ui.panelitems.CardPoolPanelItem;
import extendedui.ui.screens.CardPoolScreen;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.resources.PGR;
import pinacolada.ui.editor.card.PCLCustomCardEditCardScreen;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class CardPoolScreenPatches {
    protected static PCLEffectWithCallback<?> currentEffect;
    public static CardPoolScreen.DebugOption editCard = new CardPoolScreen.DebugOption(PGR.core.strings.misc_edit, CardPoolScreenPatches::editCard);

    public static void editCard(CardPoolScreen pool, AbstractCard c) {
        if (EUIGameUtils.inGame()) {
            PCLCustomCardSlot cardSlot = PCLCustomCardSlot.get(c.cardID);
            if (cardSlot != null) {
                AbstractDungeon.overlayMenu.cancelButton.hide();
                GameUtilities.setTopPanelVisible(false);
                c.unhover();
                currentEffect = new PCLCustomCardEditCardScreen(cardSlot, true)
                        .setOnSave(() -> {
                            cardSlot.commitBuilder();
                            if (c instanceof EditorCard) {
                                ((EditorCard) c).fullReset();
                                ((EditorCard) pool.cardGrid.getUpgrade(c)).fullReset();
                            }
                            for (AbstractCard ca : GameUtilities.getAllCopies(c.cardID)) {
                                if (ca instanceof EditorCard) {
                                    ((EditorCard) ca).fullReset();
                                }
                            }
                        })
                        .addCallback(() -> {
                            GameUtilities.setTopPanelVisible(true);
                            AbstractDungeon.overlayMenu.cancelButton.show(MasterDeckViewScreen.TEXT[1]);
                            Settings.hideRelics = true;
                        });
            }
        }
    }

    public static void editFromExternal(PCLCustomCardSlot cardSlot) {
        if (EUIGameUtils.inGame()) {
            if (cardSlot != null) {
                EUI.cardsScreen.openScreen(AbstractDungeon.player, CardPoolPanelItem.getAllCards());
                AbstractDungeon.overlayMenu.cancelButton.hide();
                GameUtilities.setTopPanelVisible(false);
                currentEffect = new PCLCustomCardEditCardScreen(cardSlot, true)
                        .setOnSave(() -> {
                            cardSlot.commitBuilder();
                            for (AbstractCard ca : GameUtilities.getAllCopies(cardSlot.ID)) {
                                if (ca instanceof EditorCard) {
                                    ((EditorCard) ca).fullReset();
                                }
                            }
                        })
                        .addCallback(() -> {
                            GameUtilities.setTopPanelVisible(true);
                            AbstractDungeon.overlayMenu.cancelButton.show(MasterDeckViewScreen.TEXT[1]);
                            AbstractDungeon.closeCurrentScreen();
                        });
            }
        }
    }

    @SpirePatch(clz = CardPoolScreen.class, method = "getOptions")
    public static class CardPoolScreenPatches_GetOptions {
        @SpirePostfixPatch
        public static ArrayList<CardPoolScreen.DebugOption> postfix(ArrayList<CardPoolScreen.DebugOption> retVal, AbstractCard c) {
            if (PCLCustomCardSlot.get(c.cardID) != null) {
                retVal.add(editCard);
            }
            return retVal;
        }
    }

    @SpirePatch(clz = CardPoolScreen.class, method = "removeCardFromPool")
    public static class CardPoolScreenPatches_RemoveCardFromPool {
        @SpirePostfixPatch
        public static void postfix(CardPoolScreen __instance, AbstractCard c) {
            PGR.dungeon.ban(c.cardID);
        }
    }

    @SpirePatch(clz = CardPoolScreen.class, method = "update")
    public static class CardPoolScreenPatches_Update {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(CardPoolScreen __instance) {
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

    @SpirePatch(clz = CardPoolScreen.class, method = "render")
    public static class CardPoolScreenPatches_Render {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(CardPoolScreen __instance, SpriteBatch sb) {
            if (currentEffect != null) {
                PGR.blackScreen.render(sb);
                currentEffect.render(sb);
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
}
