package pinacolada.patches.actions;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.dungeon.CombatManager;

public class ReducePowerActionPatches
{

    @SpirePatch(clz = ReducePowerAction.class, method = SpirePatch.CONSTRUCTOR, paramtypez =
            {AbstractCreature.class, AbstractCreature.class, String.class, int.class})
    public static class ReducePowerAction_Ctor
    {
        @SpirePostfixPatch
        public static void postfix(ReducePowerAction __instance, AbstractCreature target, AbstractCreature source,
                                   String power, int amount)
        {
            if (!CombatManager.canReducePower(source, target, power, __instance))
            {
                __instance.isDone = true;
            }
        }
    }

    @SpirePatch(clz = ReducePowerAction.class, method = SpirePatch.CONSTRUCTOR, paramtypez =
            {AbstractCreature.class, AbstractCreature.class, AbstractPower.class, int.class})
    public static class ReducePowerAction_Ctor2
    {
        @SpirePostfixPatch
        public static void postfix(ReducePowerAction __instance, AbstractCreature target, AbstractCreature source,
                                   AbstractPower powerInstance, int amount)
        {
            if (!CombatManager.canReducePower(source, target, powerInstance, __instance))
            {
                __instance.isDone = true;
            }
        }
    }
}


