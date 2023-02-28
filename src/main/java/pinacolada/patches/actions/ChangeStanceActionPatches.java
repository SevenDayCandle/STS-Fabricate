package pinacolada.patches.actions;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.stances.AbstractStance;
import javassist.CtBehavior;
import pinacolada.misc.CombatManager;

@SpirePatch(clz = ChangeStanceAction.class, method = "update")
public class ChangeStanceActionPatches
{
    @SpireInsertPatch(localvars = {"oldStance"}, locator = Locator.class)
    public static void insertPre(ChangeStanceAction __instance, AbstractStance oldStance)
    {
        CombatManager.onChangeStance(oldStance, ReflectionHacks.getPrivate(__instance, ChangeStanceAction.class,"newStance"));
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

