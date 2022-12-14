package pinacolada.patches;


import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.screens.options.InputSettingsScreen;
import com.megacrit.cardcrawl.screens.options.RemapInputElement;
import javassist.CtBehavior;
import pinacolada.misc.PCLHotkeys;
import pinacolada.resources.PGR;

import java.util.ArrayList;


public class HotkeyPatches
{

    @SpirePatch(
            clz = InputSettingsScreen.class,
            method = "refreshData"
    )
    public static class RefreshData
    {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"elements"}
        )
        public static void insert(InputSettingsScreen __instance, ArrayList<RemapInputElement> elements)
        {
            if (!Settings.isControllerMode)
            {
                elements.add(new RemapInputElement(__instance, PGR.core.strings.hotkeys.controlPileChange, PCLHotkeys.controlPileChange));
                elements.add(new RemapInputElement(__instance, PGR.core.strings.hotkeys.controlPileSelect, PCLHotkeys.controlPileSelect));
                elements.add(new RemapInputElement(__instance, PGR.core.strings.hotkeys.rerollCurrent, PCLHotkeys.rerollCurrent));
                elements.add(new RemapInputElement(__instance, PGR.core.strings.hotkeys.toggleFormulaDisplay, PCLHotkeys.toggleFormulaDisplay));
                elements.add(new RemapInputElement(__instance, PGR.core.strings.hotkeys.viewAugments, PCLHotkeys.viewAugmentScreen));
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(InputSettingsScreen.class, "maxScrollAmount");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = InputActionSet.class,
            method = "load"
    )
    public static class Load
    {
        @SpirePrefixPatch
        public static void prefix()
        {
            PCLHotkeys.load();
        }
    }

    @SpirePatch(
            clz = InputActionSet.class,
            method = "save"
    )
    public static class Save
    {
        @SpirePrefixPatch
        public static void prefix()
        {
            PCLHotkeys.save();
        }
    }

    @SpirePatch(
            clz = InputActionSet.class,
            method = "resetToDefaults"
    )
    public static class Reset
    {
        @SpirePrefixPatch
        public static void prefix()
        {
            PCLHotkeys.resetToDefaults();
        }
    }
}