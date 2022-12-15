package pinacolada.patches.abstractCard;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.cards.base.modifiers.OverrideSkillModifier;
import pinacolada.misc.CombatManager;

public class AbstractCardPatches
{

    @SpirePatch(clz = AbstractCard.class, method = "atTurnStart")
    public static class AbstractCard_AtTurnStart
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> method(AbstractCard __instance)
        {
            return quitIfOverride(__instance);
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "atTurnStartPreDraw")
    public static class AbstractCard_AtTurnStartPreDraw
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> method(AbstractCard __instance)
        {
            return quitIfOverride(__instance);
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "cardPlayable")
    public static class AbstractCard_CardPlayable
    {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> method(AbstractCard __instance, AbstractMonster m)
        {
            if (PCLCardTag.Unplayable.has(__instance))
            {
                return SpireReturn.Return(false);
            }
            if (!OverrideSkillModifier.getAll(__instance).isEmpty())
            {
                return SpireReturn.Return(true);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "onRetained")
    public static class AbstractCard_OnRetained
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> method(AbstractCard __instance)
        {
            return quitIfOverride(__instance);
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "triggerAtStartOfTurn")
    public static class AbstractCard_TriggerAtStartOfTurn
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> method(AbstractCard __instance)
        {
            return quitIfOverride(__instance);
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "triggerWhenDrawn")
    public static class AbstractCard_TriggerOnDrawn
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> method(AbstractCard __instance)
        {
            return quitIfOverride(__instance);
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "triggerOnEndOfTurnForPlayingCard")
    public static class AbstractCard_TriggerOnEndOfTurnForPlayingCard
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> method(AbstractCard __instance)
        {
            return quitIfOverride(__instance);
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "triggerOnExhaust")
    public static class AbstractCard_TriggerOnExhaust
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> method(AbstractCard __instance)
        {
            return quitIfOverride(__instance);
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "triggerOnManualDiscard")
    public static class AbstractCard_TriggerOnManualDiscard
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> method(AbstractCard __instance)
        {
            CombatManager.onCardDiscarded(__instance);
            return quitIfOverride(__instance);
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "triggerOnScry")
    public static class AbstractCard_TriggerOnScry
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> method(AbstractCard __instance)
        {
            return quitIfOverride(__instance);
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "triggerOnOtherCardPlayed", paramtypez = {AbstractCard.class})
    public static class CardGroupPatches_TriggerOnOtherCardPlayed
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(AbstractCard __instance, AbstractCard c)
        {
            if (PCLCardTag.Fragile.has(__instance))
            {
                PCLActions.last.exhaust(__instance);
            }
            if (!OverrideSkillModifier.getAll(__instance).isEmpty())
            {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    private static SpireReturn<Void> quitIfOverride(AbstractCard __instance)
    {
        if (!OverrideSkillModifier.getAll(__instance).isEmpty())
        {
            return SpireReturn.Return();
        }
        return SpireReturn.Continue();
    }
}