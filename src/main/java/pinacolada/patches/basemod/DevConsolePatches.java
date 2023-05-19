package pinacolada.patches.basemod;

import basemod.devcommands.ConsoleCommand;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import extendedui.EUIUtils;
import pinacolada.relics.PCLCustomRelicSlot;

import java.util.ArrayList;

public class DevConsolePatches {
    @SpirePatch(clz = ConsoleCommand.class, method = "getRelicOptions", optional = true)
    public static class DevConsolePatches_Relic {

        @SpirePostfixPatch
        public static ArrayList<String> postfix(ArrayList<String> ret) {
            ret.addAll(EUIUtils.map(PCLCustomRelicSlot.getRelics(null), slot -> slot.ID));
            return ret;
        }
    }
}
