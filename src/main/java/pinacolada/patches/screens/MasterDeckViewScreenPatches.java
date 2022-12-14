package pinacolada.patches.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import pinacolada.cards.base.CardAffinityComparator;
import pinacolada.resources.PGR;

public class MasterDeckViewScreenPatches
{
    @SpirePatch(clz = MasterDeckViewScreen.class, method = "update")
    public static class MasterDeckViewScreen_Update
    {
        @SpirePrefixPatch
        public static void prefix(MasterDeckViewScreen __instance)
        {
            PGR.core.cardAffinities.tryUpdate(true);
        }
    }

    @SpirePatch(clz = MasterDeckViewScreen.class, method = "render")
    public static class MasterDeckViewScreen_Render
    {
//        @SpirePrefixPatch
//        public static void prefix(MasterDeckViewScreen __instance, SpriteBatch sb)
//        {
//            screen.PreRender(sb);
//        }

        @SpirePrefixPatch
        public static void prefix(MasterDeckViewScreen __instance, SpriteBatch sb)
        {
            PGR.core.cardAffinities.tryRender(sb);
        }
    }

    @SpirePatch(clz = MasterDeckViewScreen.class, method = "open")
    public static class MasterDeckViewScreen_Open
    {
        private static MasterDeckViewScreen screen;

        @SpirePrefixPatch
        public static void prefix(MasterDeckViewScreen __instance)
        {
            screen = __instance;
            PGR.core.cardAffinities.open(AbstractDungeon.player.masterDeck.group, false, c -> screen.setSortOrder(new CardAffinityComparator(c.type)), false);
        }
    }

//    @SpirePatch(clz= MasterDeckViewScreen.class, method="onClose")
//    public static class MasterDeckViewScreen_OnClose
//    {
//        @SpirePostfixPatch
//        public static void postfix(MasterDeckViewScreen __instance)
//        {
//            screen.Close();
//        }
//    }
}
