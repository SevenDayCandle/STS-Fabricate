package pinacolada.patches;

import basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer.OnEvokeOrb;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import pinacolada.misc.CombatStats;
import pinacolada.resources.PGR;

public class BaseModPatches
{
    @SpirePatch(cls = "basemod.BaseModImGuiUI", method = "cardSearchTab", optional = true)
    public static class BaseModPatches_CardSearchTab
    {

        @SpirePrefixPatch
        public static SpireReturn<Void> prefix()
        {
            PGR.core.debugCards.render();
            return SpireReturn.Return();
        }
    }

    @SpirePatch(cls = "basemod.BaseModImGuiUI", method = "potionSearchTab", optional = true)
    public static class BaseModPatches_PotionSearchTab
    {

        @SpirePostfixPatch
        public static void postfix()
        {
            PGR.core.debugAugments.render();
        }
    }

    @SpirePatch(clz = OnEvokeOrb.Nested.class, method = "onEvoke")
    public static class BaseModPatches_OnEvoke
    {

        @SpirePostfixPatch
        public static void prefix(AbstractOrb orb)
        {
            CombatStats.onEvokeOrb(orb);
        }
    }
}
