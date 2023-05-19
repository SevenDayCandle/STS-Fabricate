package pinacolada.patches.basemod;

import basemod.BaseMod;
import basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer.OnEvokeOrb;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import javassist.CtBehavior;
import pinacolada.dungeon.CombatManager;
import pinacolada.resources.PGR;
import pinacolada.ui.customRun.PCLCustomRunScreen;

import java.util.List;

public class BaseModPatches {
    @SpirePatch(cls = "basemod.BaseModImGuiUI", method = "cardSearchTab", optional = true)
    public static class BaseModPatches_CardSearchTab {

        @SpirePrefixPatch
        public static SpireReturn<Void> prefix() {
            if (PGR.debugCards != null) {
                PGR.debugCards.render();
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(cls = "basemod.BaseModImGuiUI", method = "potionSearchTab", optional = true)
    public static class BaseModPatches_PotionSearchTab {

        @SpirePostfixPatch
        public static void postfix() {
            if (PGR.debugAugments != null) {
                PGR.debugAugments.render();
            }
        }
    }

    @SpirePatch(clz = OnEvokeOrb.Nested.class, method = "onEvoke")
    public static class BaseModPatches_OnEvoke {

        @SpirePostfixPatch
        public static void prefix(AbstractOrb orb) {
            CombatManager.onEvokeOrb(orb);
        }
    }

    @SpirePatch(clz = BaseMod.class, method = "publishAddCustomModeMods")
    public static class BaseModPatches_PublishAddCustomModeMods {

        @SpireInsertPatch(localvars = {"character", "mod"}, locator = Locator.class)
        public static void insertPre(List<CustomMod> modList, AbstractPlayer character, CustomMod mod) {
            PCLCustomRunScreen.COLOR_MOD_MAPPING.put(mod.ID, character.getCardColor());
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                final Matcher matcher = new Matcher.MethodCallMatcher(BaseMod.class, "insertCustomMod");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }
}
