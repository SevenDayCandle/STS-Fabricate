package pinacolada.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.cutscenes.Cutscene;
import com.megacrit.cardcrawl.cutscenes.CutscenePanel;
import extendedui.utilities.EUIClassUtils;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
// TODO Remove, no longer necessary
public class CutscenePatches
{
    @SpirePatch(clz = Cutscene.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractPlayer.PlayerClass.class})
    public static class CutscenePatches_ctor
    {
        @SpirePostfixPatch
        public static void postfix(Cutscene __instance, AbstractPlayer.PlayerClass playerClass)
        {
            if (GameUtilities.isPCLPlayerClass(playerClass))
            {
                EUIClassUtils.setField(__instance, "isDone", true);
                ArrayList<CutscenePanel> panels = EUIClassUtils.getField(__instance, "panels");
                panels.clear();
            }
        }
    }
}
