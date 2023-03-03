package pinacolada.patches.card;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.tags.EphemeralField;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.misc.CombatManager;

public class AbstractCardPatches
{
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
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "triggerOnManualDiscard")
    public static class AbstractCard_TriggerOnManualDiscard
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> method(AbstractCard __instance)
        {
            CombatManager.onCardDiscarded(__instance);
            return SpireReturn.Continue();
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
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "triggerOnEndOfPlayerTurn")
    public static class CardGroupPatches_TriggerOnEndOfPlayerTurn
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(AbstractCard __instance)
        {
            // Call the field directly instead of querying card tag, because card tag also checks for purgeOnUse
            if (EphemeralField.value.get(__instance))
            {
                PCLActions.last.purge(__instance);
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
}