package pinacolada.actions.creature;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.*;
import extendedui.EUIUtils;
import pinacolada.effects.PCLAttackVFX;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class DamageHelper {
    public static void applyTint(AbstractCreature target, Color overrideColor, PCLAttackVFX attackEffect) {
        final Color tint = overrideColor != null ? overrideColor : attackEffect != null ? attackEffect.damageTint : null;
        if (tint != null) {
            target.tint.color.set(tint.cpy());
            target.tint.changeColor(Color.WHITE.cpy());
        }
    }

    public static void dealDamage(AbstractCreature target, DamageInfo info, boolean bypassBlock, boolean bypassThorns) {
        int previousBlock = 0;
        if (bypassBlock) {
            previousBlock = target.currentBlock;
            target.currentBlock = 0;
        }

        ArrayList<AbstractPower> prevPowers = null;
        if (bypassThorns) {
            prevPowers = target.powers;
            target.powers = getNonContactPowers(target);
        }

        target.damage(info);

        if (!GameUtilities.isDeadOrEscaped(target)) {
            if (prevPowers != null) {
                target.powers = prevPowers;
            }

            if (previousBlock > 0) {
                target.currentBlock = previousBlock;
            }
        }
    }

    public static void dealDirectHPLoss(AbstractCreature source, AbstractCreature target, int amount, boolean bypassTempHP, boolean bypassPowers) {
        int tempHP = 0;
        if (bypassTempHP) {
            tempHP = TempHPField.tempHp.get(target);
            TempHPField.tempHp.set(target, 0);
        }

        ArrayList<AbstractPower> prevPowers = null;
        if (bypassPowers) {
            prevPowers = target.powers;
            target.powers = new ArrayList<>();
        }

        target.damage(new DamageInfo(source, amount, DamageInfo.DamageType.HP_LOSS));

        if (GameUtilities.areMonstersBasicallyDead()) {
            GameUtilities.clearPostCombatActions();
        }
        else {
            if (tempHP > 0) {
                TempHPField.tempHp.set(target, tempHP);
            }
            if (!GameUtilities.isDeadOrEscaped(target) && prevPowers != null) {
                target.powers = prevPowers;
            }
        }
    }

    public static ArrayList<AbstractPower> getNonContactPowers(AbstractCreature target) {
        return EUIUtils.filter(target.powers, power -> {
            switch (power.ID) {
                case ThornsPower.POWER_ID:
                case MalleablePower.POWER_ID:
                case FlameBarrierPower.POWER_ID:
                case CurlUpPower.POWER_ID:
                case PlatedArmorPower.POWER_ID:
                case ReactivePower.POWER_ID:
                case AngryPower.POWER_ID:
                    return false;
            }
            return !(power instanceof PCLPower && ((PCLPower) power).data.endTurnBehavior == PCLPowerData.Behavior.Plated);
        });
    }
}
