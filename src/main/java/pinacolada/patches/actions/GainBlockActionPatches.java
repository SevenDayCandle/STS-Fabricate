package pinacolada.patches.actions;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class GainBlockActionPatches
{

    private static void modifyBlock(GainBlockAction action, int amount)
    {
        if (action != null && action.source instanceof AbstractMonster)
        {
            float tmp = action.amount;
            for (AbstractPower power : action.source.powers)
            {
                tmp = power.modifyBlock(tmp);
            }
            action.amount = (int) tmp;
        }
    }

    // This allows block-modifying powers like Frail and Dexterity to affect enemies
    @SpirePatch(clz = GainBlockAction.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCreature.class, int.class})
    public static class GainBlockAction_ctor
    {
        @SpirePostfixPatch
        public static void method(GainBlockAction action, AbstractCreature target, int amount)
        {
            modifyBlock(action, amount);
        }
    }

    @SpirePatch(clz = GainBlockAction.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCreature.class, AbstractCreature.class, int.class})
    public static class GainBlockAction_ctor2
    {
        @SpirePostfixPatch
        public static void method(GainBlockAction action, AbstractCreature target, AbstractCreature source, int amount)
        {
            modifyBlock(action, amount);
        }
    }
}