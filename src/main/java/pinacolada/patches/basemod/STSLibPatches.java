package pinacolada.patches.basemod;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.patches.CommonKeywordIconsPatches;
import com.evacipated.cardcrawl.mod.stslib.patches.FlavorText;
import com.evacipated.cardcrawl.mod.stslib.patches.relicInterfaces.BetterOnUsePotionPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import pinacolada.dungeon.CombatManager;
import pinacolada.interfaces.markers.FabricateItem;
import pinacolada.resources.PGR;

public class STSLibPatches {
    @SpirePatch(clz = FlavorText.FlavorIntoCardStrings.class, method = "postfix")
    public static class FlavorIntoCardStrings_Postfix {
        // Custom cards do not have existing flavor text so this call will cause the card to fail to load altogether
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(AbstractCard c) {
            if (c instanceof FabricateItem) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = BetterOnUsePotionPatch.class, method = "Do")
    public static class BetterOnUsePotionPatch_Do {
        @SpirePrefixPatch
        public static void prefix(AbstractPotion c) {
            CombatManager.onUsePotion(c);
        }
    }

    @SpirePatch(clz = CommonKeywordIconsPatches.class, method = "RenderBadges")
    public static class CommonKeywordIconsPatches_RenderBadges {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(SpriteBatch sb, AbstractCard card) {
            if (PGR.config.displayCardTagDescription.get()) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = CommonKeywordIconsPatches.SingleCardViewRenderIconOnCard.class, method = "patch")
    public static class CommonKeywordIconsPatches_SingleCardViewRenderIconOnCard {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix() {
            if (PGR.config.displayCardTagDescription.get()) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
}
