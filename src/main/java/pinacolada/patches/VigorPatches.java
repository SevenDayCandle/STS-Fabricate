package pinacolada.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.watcher.VigorPower;

public class VigorPatches
{
    // Prevent Vigor from being removed from non-player characters when you play a card
    @SpirePatch(clz = VigorPower.class, method = "onUseCard")
    public static class VigorPatches_OnUseCard
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(VigorPower __instance, AbstractCard card, UseCardAction action)
        {
            if (__instance.owner != AbstractDungeon.player)
            {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
}
