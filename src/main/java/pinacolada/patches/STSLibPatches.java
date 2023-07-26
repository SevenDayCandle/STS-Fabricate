package pinacolada.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.patches.CustomTargeting;
import com.evacipated.cardcrawl.mod.stslib.patches.FlavorText;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUI;
import extendedui.patches.CardCrawlGamePatches;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import pinacolada.dungeon.CombatManager;
import pinacolada.interfaces.markers.FabricateItem;

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
}
