package pinacolada.patches;

import com.evacipated.cardcrawl.mod.stslib.patches.FlavorText;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.interfaces.markers.DynamicCard;

public class STSLibPatches
{
    @SpirePatch(clz = FlavorText.FlavorIntoCardStrings.class, method = "postfix")
    public static class FlavorIntoCardStrings_Postfix
    {
        // Custom cards do not have existing flavor text so this call will cause the card to fail to load altogether
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(AbstractCard c)
        {
            if (c instanceof DynamicCard)
            {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
}
