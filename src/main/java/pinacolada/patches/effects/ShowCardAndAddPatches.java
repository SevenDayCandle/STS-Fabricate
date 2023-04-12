package pinacolada.patches.effects;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDrawPileEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import extendedui.utilities.EUIClassUtils;
import pinacolada.dungeon.CombatManager;
import pinacolada.patches.library.CardLibraryPatches;

// Copied and modified from STS-AnimatorMod
public class ShowCardAndAddPatches
{
    @SpirePatch(clz = ShowCardAndAddToDrawPileEffect.class, method = "update")
    public static class ShowCardAndAddToDrawPileEffect_Update
    {
        @SpirePostfixPatch
        public static void postfix(ShowCardAndAddToDrawPileEffect __instance)
        {
            if (__instance.isDone)
            {
                CombatManager.onCardCreated(EUIClassUtils.getField(__instance, "card"), false);
            }
        }
    }

    @SpirePatch(clz = ShowCardAndAddToDiscardEffect.class, method = "update")
    public static class ShowCardAndAddToDiscardPileEffect_Update
    {
        @SpirePostfixPatch
        public static void postfix(ShowCardAndAddToDiscardEffect __instance)
        {
            if (__instance.isDone)
            {
                CombatManager.onCardCreated(EUIClassUtils.getField(__instance, "card"), false);
            }
        }
    }

    @SpirePatch(clz = ShowCardAndAddToHandEffect.class, method = "update")
    public static class ShowCardAndAddToHandEffect_Update
    {
        @SpirePostfixPatch
        public static void postfix(ShowCardAndAddToHandEffect __instance)
        {
            if (__instance.isDone)
            {
                CombatManager.onCardCreated(EUIClassUtils.getField(__instance, "card"), false);
            }
        }
    }

    // Constructors:

    @SpirePatch(clz = ShowCardAndObtainEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class, float.class, float.class, boolean.class})
    public static class ShowCardAndObtainEffect_Ctor
    {
        @SpirePrefixPatch
        public static void prefix(ShowCardAndObtainEffect __instance, @ByRef AbstractCard[] srcCard, float x, float y, boolean converge)
        {
            CardLibraryPatches.tryReplace(srcCard);
        }
    }

    @SpirePatch(clz = ShowCardAndAddToDrawPileEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class, boolean.class, boolean.class})
    public static class ShowCardAndAddToDrawPileEffect_Ctor1
    {
        @SpirePrefixPatch
        public static void prefix(ShowCardAndAddToDrawPileEffect __instance, @ByRef AbstractCard[] srcCard, boolean randomSpot, boolean toBottom)
        {
            CardLibraryPatches.tryReplace(srcCard);
        }
    }

    @SpirePatch(clz = ShowCardAndAddToDrawPileEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class, float.class, float.class, boolean.class, boolean.class, boolean.class})
    public static class ShowCardAndAddToDrawPileEffect_Ctor2
    {
        @SpirePrefixPatch
        public static void prefix(ShowCardAndAddToDrawPileEffect __instance, @ByRef AbstractCard[] srcCard, float x, float y, boolean randomSpot, boolean cardOffset, boolean toBottom)
        {
            CardLibraryPatches.tryReplace(srcCard);
        }
    }

    @SpirePatch(clz = ShowCardAndAddToDiscardEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class})
    public static class ShowCardAndAddToDiscardEffect_Ctor1
    {
        @SpirePrefixPatch
        public static void prefix(ShowCardAndAddToDiscardEffect __instance, @ByRef AbstractCard[] card)
        {
            CardLibraryPatches.tryReplace(card);
        }
    }

    @SpirePatch(clz = ShowCardAndAddToDiscardEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class, float.class, float.class})
    public static class ShowCardAndAddToDiscardEffect_Ctor2
    {
        @SpirePrefixPatch
        public static void prefix(ShowCardAndAddToDiscardEffect __instance, @ByRef AbstractCard[] srcCard, float x, float y)
        {
            CardLibraryPatches.tryReplace(srcCard);
        }
    }

    @SpirePatch(clz = ShowCardAndAddToHandEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class})
    public static class ShowCardAndAddToHandEffect_Ctor1
    {
        @SpirePrefixPatch
        public static void prefix(ShowCardAndAddToHandEffect __instance, @ByRef AbstractCard[] card)
        {
            CardLibraryPatches.tryReplace(card);
        }
    }

    @SpirePatch(clz = ShowCardAndAddToHandEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class, float.class, float.class})
    public static class ShowCardAndAddToHandEffect_Ctor2
    {
        @SpirePrefixPatch
        public static void prefix(ShowCardAndAddToHandEffect __instance, @ByRef AbstractCard[] card, float offsetX, float offsetY)
        {
            CardLibraryPatches.tryReplace(card);
        }
    }
}
