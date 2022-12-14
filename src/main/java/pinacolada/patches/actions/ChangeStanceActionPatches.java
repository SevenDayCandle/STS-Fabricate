package pinacolada.patches.actions;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.stances.AbstractStance;
import extendedui.utilities.EUIClassUtils;
import javassist.CtBehavior;
import pinacolada.misc.CombatStats;

@SpirePatch(clz = ChangeStanceAction.class, method = "update")
public class ChangeStanceActionPatches
{
    @SpireInsertPatch(localvars = {"oldStance"}, locator = Locator.class)
    public static void insertPre(ChangeStanceAction __instance, AbstractStance oldStance)
    {
        CombatStats.onChangeStance(oldStance, EUIClassUtils.getField(__instance, "newStance"));
    }

    private static class Locator extends SpireInsertLocator
    {
        public int[] Locate(CtBehavior ctBehavior) throws Exception
        {
            final Matcher matcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
            return LineFinder.findInOrder(ctBehavior, matcher);
        }
    }
}

