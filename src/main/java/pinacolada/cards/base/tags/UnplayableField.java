package pinacolada.cards.base.tags;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SpirePatch(
        cls = "com.megacrit.cardcrawl.cards.AbstractCard",
        method = "<class>"
)
public class UnplayableField
{
    public static SpireField<Boolean> value = new SpireField<>(() -> {
        return false;
    });

    public UnplayableField()
    {
    }
}