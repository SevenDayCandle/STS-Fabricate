package pinacolada.cards.base.fields;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SpirePatch(
        cls = "com.megacrit.cardcrawl.cards.AbstractCard",
        method = "<class>"
)
public class HasteField
{
    public static SpireField<Integer> value = new SpireField<>(() -> {
        return 0;
    });

    public HasteField()
    {
    }
}