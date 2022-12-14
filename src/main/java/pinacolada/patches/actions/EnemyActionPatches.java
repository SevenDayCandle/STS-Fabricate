package pinacolada.patches.actions;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.unique.VampireDamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.misc.CombatManager;

// Make the enemy hit itself (Also VampireDamageAction because it obviously does not inherit from DamageAction)
public class EnemyActionPatches
{
    @SpirePatch(clz = DamageAction.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCreature.class, DamageInfo.class, AbstractGameAction.AttackEffect.class})
    public static class DamageAction_ctor
    {
        @SpirePostfixPatch
        public static void method(DamageAction action, AbstractCreature target, DamageInfo info, AbstractGameAction.AttackEffect effect)
        {
            if (action != null && action.source != null)
            {
                CombatManager.onDamageAction(action, target, info, effect);
            }
        }
    }

    @SpirePatch(clz = VampireDamageAction.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCreature.class, DamageInfo.class, AbstractGameAction.AttackEffect.class})
    public static class VampireDamageAction_ctor
    {
        @SpirePostfixPatch
        public static void method(VampireDamageAction action, AbstractCreature target, DamageInfo info, AbstractGameAction.AttackEffect effect)
        {
            if (action != null && action.source != null)
            {
                CombatManager.onDamageAction(action, target, info, effect);
            }
        }
    }
}