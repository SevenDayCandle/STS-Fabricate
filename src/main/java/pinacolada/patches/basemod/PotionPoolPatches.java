package pinacolada.patches.basemod;

import basemod.patches.com.megacrit.cardcrawl.helpers.PotionLibrary.PotionHelperGetPotion;
import basemod.patches.com.megacrit.cardcrawl.helpers.PotionLibrary.PotionHelperGetPotions;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import pinacolada.potions.PCLCustomPotionSlot;
import pinacolada.resources.PGR;

import java.util.ArrayList;

public class PotionPoolPatches {
    private static boolean FLAG;

    public static void initialize() {
        FLAG = true;
    }

    // Unset flag to prevent PotionHelper from pulling up custom potions
    public static AbstractPotion getDirectPotion(String potionID) {
        if (FLAG) {
            FLAG = false;
            AbstractPotion directPotion = PotionHelper.getPotion(potionID);
            FLAG = true;
            return directPotion;
        }
        return null;
    }

    @SpirePatch2(clz = PotionHelperGetPotion.class, method = "patch")
    public static class PotionHelperGetPotion_Patch {
        @SpirePrefixPatch
        public static SpireReturn<?> postfix(String name) {
            if (FLAG) {
                PCLCustomPotionSlot slot = PCLCustomPotionSlot.get(name);
                if (slot != null) {
                    return SpireReturn.Return(SpireReturn.Return(slot.make()));
                }
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = PotionHelperGetPotions.class, method = "Postfix")
    public static class PotionHelperGetPotions_Postfix {
        @SpirePostfixPatch
        public static void postfix(ArrayList<String> __result, AbstractPlayer.PlayerClass c, boolean getAll) {
            PGR.dungeon.loadCustomPotions(__result, c, getAll);
        }
    }
}

