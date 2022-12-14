package pinacolada.patches.cardCrawlGame;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import pinacolada.misc.CombatManager;

public class CardCrawlGamePatches
{
    @SpirePatch(clz = CardCrawlGame.class, method = "startOver")
    public static class CardCrawlGame_StartOver
    {
        @SpirePrefixPatch
        public static void prefix()
        {
            CombatManager.onStartOver();
        }
    }
}
