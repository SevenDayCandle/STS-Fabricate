package pinacolada.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.stances.AbstractStance;
import pinacolada.stances.PCLStanceHelper;

@SpirePatch(clz = AbstractStance.class, method = "getStanceFromName", paramtypez = {String.class})
public class AbstractStancePatches
{
    @SpirePrefixPatch
    public static SpireReturn<AbstractStance> prefix(String name)
    {
        PCLStanceHelper stance = PCLStanceHelper.get(name);
        if (stance != null)
        {
            return SpireReturn.Return(stance.create());
        }

        return SpireReturn.Continue();
    }
}
