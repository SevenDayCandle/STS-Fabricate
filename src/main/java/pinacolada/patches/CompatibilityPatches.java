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
