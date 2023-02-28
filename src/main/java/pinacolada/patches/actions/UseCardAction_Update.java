package pinacolada.patches.actions;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.utility.ShowCardAndPoofAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIUtils;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.resources.PCLEnum;

@SpirePatch(clz = UseCardAction.class, method = "update", optional = true)
public class UseCardAction_Update
{
    @SpireInstrumentPatch
    public static ExprEditor instrument()
    {
        return new ExprEditor()
        {
            public void edit(javassist.expr.FieldAccess m) throws CannotCompileException
            {
                if (m.getClassName().equals(AbstractCard.class.getName()) && m.getFieldName().equals("purgeOnUse"))
                {
                    m.replace("{ $_ = pinacolada.patches.actions.UseCardAction_Update.patch($0); }");
                }
            }
        };
    }

    public static boolean patch(AbstractCard card)
    {
        return card.purgeOnUse || PCLCardTag.Purge.has(card);
    }

    @SpireInsertPatch(locator = Locator.class)
    public static SpireReturn<Void> insert(UseCardAction __instance)
    {
        PCLCard c = EUIUtils.safeCast(ReflectionHacks.getPrivate(__instance, UseCardAction.class, "targetCard"), PCLCard.class);
        if (c != null && c.type == PCLEnum.CardType.SUMMON)
        {
            PCLActions.top.add(new ShowCardAndPoofAction(c));
            AbstractDungeon.player.cardInUse = null;
            __instance.isDone = true;
            return SpireReturn.Return();
        }
        return SpireReturn.Continue();
    }

    private static class Locator extends SpireInsertLocator
    {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
        {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.CardType.class, "POWER");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}

