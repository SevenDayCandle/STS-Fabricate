package pinacolada.patches.basemod;

import com.evacipated.cardcrawl.mod.stslib.patches.FlavorText;
import com.evacipated.cardcrawl.mod.stslib.patches.relicInterfaces.BetterOnUsePotionPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import extendedui.EUIUtils;
import pinacolada.dungeon.CombatManager;
import pinacolada.interfaces.markers.FabricateItem;

public class STSLibPatches {
    public static String getProperName(String derp) {
        AbstractCard proper = CardLibrary.getCard(derp);
        return proper != null ? proper.name : EUIUtils.EMPTY_STRING;
    }

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
}
