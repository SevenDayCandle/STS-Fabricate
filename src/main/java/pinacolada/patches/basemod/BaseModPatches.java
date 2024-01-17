package pinacolada.patches.basemod;

import basemod.BaseMod;
import basemod.abstracts.CustomSavableRaw;
import basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer.OnEvokeOrb;
import basemod.patches.com.megacrit.cardcrawl.core.CardCrawlGame.LoadPlayerSaves;
import basemod.patches.com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue.Save;
import basemod.patches.com.megacrit.cardcrawl.saveAndContinue.SaveFile.ConstructSaveFilePatch;
import basemod.patches.com.megacrit.cardcrawl.saveAndContinue.SaveFile.ModSaves;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.google.gson.annotations.SerializedName;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import javassist.CtBehavior;
import pinacolada.blights.PCLBlight;
import pinacolada.dungeon.CombatManager;
import pinacolada.orbs.PCLOrb;
import pinacolada.relics.PCLRelic;
import pinacolada.resources.PGR;
import pinacolada.ui.customRun.PCLCustomRunScreen;

import java.util.HashMap;
import java.util.List;

public class BaseModPatches {
    private static final String BLIGHT_PATH = "pinacolada:mod_blight_saves";

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

    @SpirePatch(clz = BaseMod.class, method = "calculateCardDamage", paramtypez = {AbstractPlayer.class, AbstractMonster.class, AbstractCard.class, float.class})
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

                for (AbstractOrb r : AbstractDungeon.player.orbs) {
                    if (r instanceof PCLOrb) {
                        retVal = ((PCLOrb) r).atDamageModify(retVal, c);
                    }
                }
                for (AbstractOrb r : AbstractDungeon.player.orbs) {
                    if (r instanceof PCLOrb) {
                        retVal = ((PCLOrb) r).atDamageLastModify(retVal, c);
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

    @SpirePatch(clz = LoadPlayerSaves.class, method = "Postfix", optional = true)
    public static class BaseModPatches_LoadPlayerSaves {

        // Lazy approach to have CustomSavable apply to all PCLBlights without having to register new save fields for each
        @SpirePostfixPatch
        public static void postfix(CardCrawlGame __instance, AbstractPlayer p) {
            ModSaves.ArrayListOfJsonElement modBlightSaves = FabricateSave.modBlightSaves.get(CardCrawlGame.saveFile);
            int i = 0;
            for (AbstractBlight blight : AbstractDungeon.player.blights) {
                if (blight instanceof PCLBlight) {
                    ((CustomSavableRaw)blight).onLoadRaw(modBlightSaves == null || i >= modBlightSaves.size() ? null : modBlightSaves.get(i));
                }
                i++;
            }
        }
    }

    @SpirePatch(clz = Save.class, method = "Insert", optional = true)
    public static class BaseModPatches_Save {

        // Lazy approach to have CustomSavable apply to all PCLBlights without having to register new save fields for each
        @SpirePostfixPatch
        public static void postfix(SaveFile save, HashMap<String, Object> params) {
            params.put(BLIGHT_PATH, FabricateSave.modBlightSaves.get(save));
        }
    }

    @SpirePatch(clz = ConstructSaveFilePatch.class, method = "Prefix", optional = true)
    public static class BaseModPatches_ConstructSaveFilePatch {

        // Lazy approach to have CustomSavable apply to all PCLBlights without having to register new save fields for each
        @SpirePostfixPatch
        public static void postfix(SaveFile __instance, SaveFile.SaveType saveType) {
            ModSaves.ArrayListOfJsonElement modBlightSaves = new ModSaves.ArrayListOfJsonElement();
            for (AbstractBlight blight : AbstractDungeon.player.blights) {
                if (blight instanceof PCLBlight) {
                    modBlightSaves.add(((CustomSavableRaw)blight).onSaveRaw());
                } else {
                    modBlightSaves.add(null);
                }
            }
            FabricateSave.modBlightSaves.set(__instance, modBlightSaves);
        }
    }

    @SpirePatch(
            clz = SaveFile.class,
            method = "<class>"
    )
    public static class FabricateSave {
        @SerializedName(BLIGHT_PATH)
        public static SpireField<ModSaves.ArrayListOfJsonElement> modBlightSaves = new SpireField<>(() -> {
            return null;
        });
    }
}
