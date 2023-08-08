package pinacolada.patches;

import com.evacipated.cardcrawl.mod.stslib.patches.FlavorText;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import extendedui.EUIUtils;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import pinacolada.interfaces.markers.FabricateItem;

public class CompatibilityPatches {

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

    @SpirePatch(cls = "infinitespire.quests.PickUpCardQuest", method = "getTitle", optional = true)
    public static class InfiniteSpire_PickUpCardQuest {
        // Because this explodes if the card doesn't exist in the base game library
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractCard.class.getName()) && m.getFieldName().equals("name")) {
                        m.replace("{ $_ = pinacolada.patches.CompatibilityPatches.getProperName(this.cardID); }");
                    }
                }
            };
        }
    }
}
