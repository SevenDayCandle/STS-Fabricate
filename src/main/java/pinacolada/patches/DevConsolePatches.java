package pinacolada.patches;

import basemod.DevConsole;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

public class DevConsolePatches
{
    @SpirePatch(clz = DevConsole.class, method = "execute")
    public static class AutocompletePatches_Execute
    {
        @SpirePrefixPatch
        public static void prefix()
        {
            if (GameUtilities.inGame() && GameUtilities.isPCLPlayerClass())
            {
                PGR.core.dungeon.setCheating();
            }
        }
    }
}
