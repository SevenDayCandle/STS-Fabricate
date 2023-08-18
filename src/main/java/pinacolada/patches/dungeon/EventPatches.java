package pinacolada.patches.dungeon;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.colorless.Apparition;
import com.megacrit.cardcrawl.cards.colorless.Bite;
import com.megacrit.cardcrawl.cards.colorless.JAX;
import com.megacrit.cardcrawl.cards.colorless.RitualDagger;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.OverlayMenu;
import com.megacrit.cardcrawl.events.city.DrugDealer;
import com.megacrit.cardcrawl.events.city.Ghosts;
import com.megacrit.cardcrawl.events.city.Nest;
import com.megacrit.cardcrawl.events.city.Vampires;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

public class EventPatches {

    @SpirePatch(clz = DrugDealer.class, method = "buttonEffect")
    public static class DrugDealer_ButtonEffect {

        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.NewExpr m) throws CannotCompileException {
                    if (m.getClassName().equals(JAX.class.getName())) {
                        m.replace("{ $_ = pinacolada.patches.dungeon.EventPatches.getEventReplacement(com.megacrit.cardcrawl.cards.colorless.JAX.ID); }");
                    }
                }
            };
        }
    }

    @SpirePatch(clz = DrugDealer.class, method = SpirePatch.CONSTRUCTOR)
    public static class DrugDealer_Ctor {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(CardLibrary.class.getName()) && m.getMethodName().equals("getCopy")) {
                        m.replace("{ $_ = pinacolada.patches.dungeon.EventPatches.getEventReplacement($1); }");
                    }
                }
            };
        }
    }

    @SpirePatch(clz = Ghosts.class, method = "becomeGhost")
    @SpirePatch(clz = Ghosts.class, method = SpirePatch.CONSTRUCTOR)
    public static class EventPatches_Ghosts {

        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.NewExpr m) throws CannotCompileException {
                    if (m.getClassName().equals(Apparition.class.getName())) {
                        m.replace("{ $_ = pinacolada.patches.dungeon.EventPatches.getEventReplacement(com.megacrit.cardcrawl.cards.colorless.Apparition.ID); }");
                    }
                }
            };
        }
    }

    @SpirePatch(clz = Nest.class, method = "buttonEffect")
    public static class EventPatches_Nest {

        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.NewExpr m) throws CannotCompileException {
                    if (m.getClassName().equals(RitualDagger.class.getName())) {
                        m.replace("{ $_ = pinacolada.patches.dungeon.EventPatches.getEventReplacement(com.megacrit.cardcrawl.cards.colorless.RitualDagger.ID); }");
                    }
                }
            };
        }
    }

    @SpirePatch(clz = Vampires.class, method = "replaceAttacks")
    @SpirePatch(clz = Vampires.class, method = SpirePatch.CONSTRUCTOR)
    public static class EventPatches_Vampires {

        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.NewExpr m) throws CannotCompileException {
                    if (m.getClassName().equals(Bite.class.getName())) {
                        m.replace("{ $_ = pinacolada.patches.dungeon.EventPatches.getEventReplacement(com.megacrit.cardcrawl.cards.colorless.Bite.ID); }");
                    }
                }
            };
        }
    }

    public static AbstractCard getEventReplacement(String id) {
        String replacementID = getStandardReplacementID(id);
        if (replacementID != null) {
            return CardLibrary.getCopy(replacementID);
        }
        return CardLibrary.getCopy(id);
    }

    public static String getStandardReplacementID(String id) {
        return PGR.getResources(GameUtilities.getPlayerClass()).getEventReplacement(id);
    }
}
