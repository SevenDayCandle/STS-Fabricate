package pinacolada.patches.basemod;

import basemod.BaseMod;
import basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer.OnEvokeOrb;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import javassist.CtBehavior;
import pinacolada.blights.PCLBlight;
import pinacolada.dungeon.CombatManager;
import pinacolada.relics.PCLRelic;
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

    @SpirePatch(clz = BaseMod.class, method = "calculateCardDamage")
    public static class BaseModPatches_CalculateCardDamage {

        // Called only by the vanilla AbstractCard card calculations, in both the single and multi-damage scenarios
        @SpirePostfixPatch
        public static float postfix(float retVal, AbstractPlayer player, AbstractMonster mo, AbstractCard c, float tmp) {
            if (player != null) {
                // Vanilla relics already have atDamageModify
                for (AbstractRelic relic : player.relics) {
                    if (relic instanceof PCLRelic) {
                        retVal = ((PCLRelic) relic).atDamageLastModify(retVal, c);
                    }
                }

                for (AbstractBlight blight : player.blights) {
                    if (blight instanceof PCLBlight) {
                        retVal = ((PCLBlight) blight).atDamageModify(retVal, c);
                    }
                }
                for (AbstractBlight blight : player.blights) {
                    if (blight instanceof PCLBlight) {
                        retVal = ((PCLBlight) blight).atDamageLastModify(retVal, c);
                    }
                }
            }
            return retVal;
        }
    }

    @SpirePatch(cls = "basemod.BaseModImGuiUI", method = "potionSearchTab", optional = true)
    public static class BaseModPatches_PotionSearchTab {

        @SpirePostfixPatch
        public static void postfix() {
            if (PGR.debugBlights != null) {
                PGR.debugBlights.render();
            }
            if (PGR.debugAugments != null) {
                PGR.debugAugments.render();
            }
        }

        @SpirePrefixPatch
        public static SpireReturn<Void> prefix() {
            if (PGR.debugPotions != null) {
                PGR.debugPotions.render();
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(cls = "basemod.BaseModImGuiUI", method = "relicSearchTab", optional = true)
    public static class BaseModPatches_RelicSearchTab {

        @SpirePrefixPatch
        public static SpireReturn<Void> prefix() {
            if (PGR.debugRelics != null) {
                PGR.debugRelics.render();
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
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
