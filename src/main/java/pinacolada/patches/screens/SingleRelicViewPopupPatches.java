package pinacolada.patches.screens;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.SingleRelicViewPopup;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUIUtils;
import extendedui.patches.game.OverlayMenuPatches;
import extendedui.text.EUISmartText;
import extendedui.utilities.EUIClassUtils;
import extendedui.utilities.EUIFontHelper;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import pinacolada.cards.base.PCLCard;
import pinacolada.relics.PCLRelic;
import pinacolada.resources.PGR;

import java.util.ArrayList;

public class SingleRelicViewPopupPatches {
    @SpirePatch(clz = SingleRelicViewPopup.class, method = "open", paramtypez = {AbstractRelic.class})
    public static class SingleRelicViewPopup_Open {
        @SpirePrefixPatch
        public static SpireReturn<Void> insert(SingleRelicViewPopup __instance, AbstractRelic card) {
            PCLRelic c = EUIUtils.safeCast(card, PCLRelic.class);
            if (c != null) {
                PGR.relicPopup.open(c, null);

                return SpireReturn.Return(null);
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = SingleRelicViewPopup.class, method = "open", paramtypez = {AbstractRelic.class, ArrayList.class})
    public static class SingleRelicViewPopup_Open2 {
        @SpirePrefixPatch
        public static SpireReturn<Void> insert(SingleRelicViewPopup __instance, AbstractRelic card, ArrayList<AbstractRelic> group) {
            PCLRelic c = EUIUtils.safeCast(card, PCLRelic.class);
            if (c != null) {
                PGR.relicPopup.open(c, group);

                return SpireReturn.Return(null);
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = SingleRelicViewPopup.class, method = "openNext")
    public static class SingleRelicViewPopup_OpenNext {
        @SpirePostfixPatch
        public static void insert(SingleRelicViewPopup __instance) {
            PGR.relicPopup.forceUnfade();
        }
    }

    @SpirePatch(clz = SingleRelicViewPopup.class, method = "openPrev")
    public static class SingleRelicViewPopup_OpenPrev {
        @SpirePostfixPatch
        public static void insert(SingleRelicViewPopup __instance) {
            PGR.relicPopup.forceUnfade();
        }
    }

}
