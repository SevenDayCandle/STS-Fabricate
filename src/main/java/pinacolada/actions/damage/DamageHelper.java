package pinacolada.actions.damage;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.*;
import pinacolada.effects.AttackEffects;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class DamageHelper
{
    public static void addIgnoredPower(ArrayList<AbstractPower> powers, AbstractCreature target, String powerID)
    {
        AbstractPower power = target.getPower(powerID);
        if (power != null)
        {
            powers.add(power);
            target.powers.remove(power);
        }
    }

    public static void applyTint(AbstractCreature target, Color overrideColor, AbstractGameAction.AttackEffect attackEffect)
    {
        final Color tint = overrideColor != null ? overrideColor : AttackEffects.getDamageTint(attackEffect);
        if (tint != null)
        {
            target.tint.color.set(tint.cpy());
            target.tint.changeColor(Color.WHITE.cpy());
        }
    }

    public static void dealDamage(AbstractCreature target, DamageInfo info, boolean bypassBlock, boolean bypassThorns)
    {
        int previousBlock = 0;
        if (bypassBlock)
        {
            previousBlock = target.currentBlock;
            target.currentBlock = 0;
        }

        ArrayList<AbstractPower> ignoredPowers = null;
        if (bypassThorns)
        {
            ignoredPowers = removePowers(target);
        }

        target.damage(info);

        if (!GameUtilities.isDeadOrEscaped(target))
        {
            if (ignoredPowers != null)
            {
                reapplyPowers(ignoredPowers, target);
            }

            if (previousBlock > 0)
            {
                target.currentBlock = previousBlock;
            }
        }
    }

    public static void reapplyPowers(ArrayList<AbstractPower> powers, AbstractCreature target)
    {
        for (AbstractPower p : powers)
        {
            AbstractPower current = target.getPower(p.ID);
            if (current != null)
            {
                current.amount += p.amount;
            }
            else
            {
                target.powers.add(p);
            }
        }
    }

    public static ArrayList<AbstractPower> removePowers(AbstractCreature target)
    {
        ArrayList<AbstractPower> toReAdd = new ArrayList<>();

        addIgnoredPower(toReAdd, target, ThornsPower.POWER_ID);
        addIgnoredPower(toReAdd, target, MalleablePower.POWER_ID);
        addIgnoredPower(toReAdd, target, FlameBarrierPower.POWER_ID);
        addIgnoredPower(toReAdd, target, CurlUpPower.POWER_ID);
        addIgnoredPower(toReAdd, target, PlatedArmorPower.POWER_ID);
        addIgnoredPower(toReAdd, target, ReactivePower.POWER_ID);

        for (int i = target.powers.size() - 1; i >= 0; i--)
        {
            if (target.powers.get(i).ID.toLowerCase().contains("thorns"))
            {
                toReAdd.add(target.powers.get(i));
                target.powers.remove(i);
            }
        }

        return toReAdd;
    }
}
