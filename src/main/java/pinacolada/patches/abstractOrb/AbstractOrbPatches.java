package pinacolada.patches.abstractOrb;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.OrbStrings;
import com.megacrit.cardcrawl.orbs.*;
import com.megacrit.cardcrawl.powers.ElectroPower;
import com.megacrit.cardcrawl.powers.LockOnPower;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.orbs.DarkOrbEvokeAction;
import pinacolada.actions.orbs.LightningOrbAction;
import pinacolada.misc.CombatStats;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameActions;
import pinacolada.utilities.GameUtilities;

import java.util.HashMap;

public class AbstractOrbPatches
{
    protected static final HashMap<String, OrbStrings> orbStrings = new HashMap<>();
    protected static final HashMap<String, EUITooltip> tooltips = new HashMap<>();

    protected static OrbStrings getOrbStrings(String orbID)
    {
        return orbStrings.getOrDefault(orbID, PGR.getOrbStrings(PGR.core.createID(orbID)));
    }

    protected static EUITooltip getTooltip(AbstractOrb orb)
    {
        OrbStrings orbStrings = getOrbStrings(orb.ID);
        return tooltips.getOrDefault(orb.ID, new EUITooltip(orbStrings.NAME, EUIUtils.format(orbStrings.DESCRIPTION[0], orb.passiveAmount, orb.evokeAmount)));
    }

    @SpirePatch(clz = Dark.class, method = "onEndOfTurn")
    @SpirePatch(clz = Frost.class, method = "onEndOfTurn")
    @SpirePatch(clz = Lightning.class, method = "onEndOfTurn")
    public static class AbstractOrb_onEndOfTurn
    {
        @SpirePostfixPatch
        public static void postfix(AbstractOrb __instance)
        {
            if (!(__instance instanceof EmptyOrbSlot))
            {
                CombatStats.onOrbPassiveEffect(__instance);
            }
        }
    }

    @SpirePatch(clz = Plasma.class, method = "onStartOfTurn")
    public static class AbstractOrb_onStartOfTurn
    {
        @SpirePostfixPatch
        public static void postfix(AbstractOrb __instance)
        {
            if (!(__instance instanceof EmptyOrbSlot))
            {
                CombatStats.onOrbPassiveEffect(__instance);
            }
        }
    }

    @SpirePatch(clz = AbstractOrb.class, method = "applyFocus")
    public static class AbstractOrbPatches_ApplyFocus
    {
        @SpirePostfixPatch
        public static void postfix(AbstractOrb __instance)
        {
            // Certain mods may instantiate Orb instances in static methods before CombatStats is initialized, which will cause this method call to crash without this in-game check
            if (GameUtilities.inGame())
            {
                CombatStats.onOrbApplyFocus(__instance);
            }

        }
    }

    @SpirePatch(clz = AbstractOrb.class, method = "applyLockOn", paramtypez = {AbstractCreature.class, int.class})
    public static class AbstractOrbPatches_ApplyLockOn
    {
        @SpirePostfixPatch
        public static int postfix(int retVal, AbstractCreature target, int dmg)
        {
            // Use float for more accurate values
            float inputDmg = retVal;
            if (GameUtilities.getPowerAmount(target, LockOnPower.POWER_ID) >= 1)
            {
                float modifier = CombatStats.getEffectBonus(LockOnPower.POWER_ID);
                inputDmg = modifier > 0 ? (dmg * ((retVal / (float) dmg) + modifier)) : retVal;
            }
            return (int) CombatStats.onOrbApplyLockOn(target, inputDmg);
        }
    }

    @SpirePatch(clz = AbstractOrb.class, method = "update")
    public static class AbstractOrbPatches_update
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(AbstractOrb __instance)
        {
            if (GameUtilities.isPCLPlayerClass() && GameUtilities.isValidOrb(__instance))
            {
                __instance.hb.update();
                if (__instance.hb.hovered)
                {
                    EUITooltip.queueTooltip(getTooltip(__instance), InputHelper.mX + __instance.hb.width, InputHelper.mY + (__instance.hb.height * 0.5f));
                }

                ReflectionHacks.setPrivate(__instance, AbstractOrb.class, "fontScale", MathHelper.scaleLerpSnap(ReflectionHacks.getPrivate(__instance, AbstractOrb.class, "fontScale"), 0.7F));
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = Dark.class, method = "applyFocus")
    public static class DarkPatches_ApplyFocus
    {
        @SpirePostfixPatch
        public static void postfix(Dark __instance)
        {
            if (GameUtilities.inGame())
            {
                CombatStats.onOrbApplyFocus(__instance);
            }
        }
    }

    @SpirePatch(clz = Dark.class, method = "onEvoke")
    public static class DarkPatches_OnEvoke
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(Dark __instance)
        {
            if (GameUtilities.isPCLPlayerClass())
            {
                GameActions.top.add(new DarkOrbEvokeAction(__instance.evokeAmount));
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = Dark.class, method = "updateDescription")
    public static class DarkPatches_UpdateDescription
    {
        @SpirePostfixPatch
        public static void postfix(Dark __instance)
        {
            if (GameUtilities.isPCLPlayerClass())
            {
                __instance.description = EUIUtils.format(getOrbStrings(__instance.ID).DESCRIPTION[0], __instance.passiveAmount, __instance.evokeAmount);
            }
        }
    }

    @SpirePatch(clz = Frost.class, method = "updateDescription")
    public static class FrostPatches_UpdateDescription
    {
        @SpirePostfixPatch
        public static void postfix(Frost __instance)
        {
            if (GameUtilities.isPCLPlayerClass())
            {
                __instance.description = EUIUtils.format(getOrbStrings(__instance.ID).DESCRIPTION[0], __instance.passiveAmount, __instance.evokeAmount);
            }
        }
    }

    @SpirePatch(clz = Lightning.class, method = "onEvoke")
    public static class LightningPatches_OnEvoke
    {
        @SpirePrefixPatch
        public static SpireReturn prefix(Lightning __instance)
        {
            if (GameUtilities.isPCLPlayerClass())
            {
                GameActions.top.add(new LightningOrbAction(null, __instance.evokeAmount, AbstractDungeon.player.hasPower(ElectroPower.POWER_ID)));
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = Lightning.class, method = "onEndOfTurn")
    public static class LightningPatches_OnEndOfTurn
    {
        @SpirePrefixPatch
        public static SpireReturn prefix(Lightning __instance)
        {
            if (GameUtilities.isPCLPlayerClass())
            {
                GameActions.top.add(new LightningOrbAction(__instance, __instance.passiveAmount, AbstractDungeon.player.hasPower(ElectroPower.POWER_ID)));
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = Lightning.class, method = "triggerPassiveEffect")
    public static class LightningPatches_TriggerPassiveEffect
    {
        @SpirePrefixPatch
        public static SpireReturn prefix(Lightning __instance, DamageInfo info, boolean hitAll)
        {
            if (GameUtilities.isPCLPlayerClass())
            {
                GameActions.top.add(new LightningOrbAction(__instance, info.output, hitAll));
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = Lightning.class, method = "updateDescription")
    public static class LightningPatches_UpdateDescription
    {
        @SpirePostfixPatch
        public static void postfix(Lightning __instance)
        {
            if (GameUtilities.isPCLPlayerClass())
            {
                __instance.description = EUIUtils.format(getOrbStrings(__instance.ID).DESCRIPTION[0], __instance.passiveAmount, __instance.evokeAmount);
            }
        }
    }

    @SpirePatch(clz = Plasma.class, method = "updateDescription")
    public static class PlasmaPatches_UpdateDescription
    {
        @SpirePostfixPatch
        public static void postfix(Plasma __instance)
        {
            if (GameUtilities.isPCLPlayerClass())
            {
                __instance.description = EUIUtils.format(getOrbStrings(__instance.ID).DESCRIPTION[0], __instance.passiveAmount, __instance.evokeAmount);
            }
        }
    }
}