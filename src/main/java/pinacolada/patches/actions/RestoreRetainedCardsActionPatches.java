package pinacolada.patches.actions;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.unique.RestoreRetainedCardsAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import pinacolada.cards.base.fields.PCLCardTag;

@SpirePatch(clz = RestoreRetainedCardsAction.class, method = "update", optional = true)
public class RestoreRetainedCardsActionPatches
{
    @SpireInstrumentPatch
    public static ExprEditor instrument()
    {
        return new ExprEditor()
        {
            public void edit(javassist.expr.FieldAccess m) throws CannotCompileException
            {
                if (m.getClassName().equals(AbstractCard.class.getName()) && m.getFieldName().equals("retain") && m.isWriter())
                {
                    m.replace("{ pinacolada.patches.actions.RestoreRetainedCardsActionPatches.patch($0); }");
                }
            }
        };
    }

    public static void patch(AbstractCard card)
    {
        if (PCLCardTag.Retain.tryProgress(card))
        {
            card.retain = false;
        }
    }
}

