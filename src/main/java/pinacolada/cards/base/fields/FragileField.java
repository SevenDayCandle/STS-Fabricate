package pinacolada.cards.base.fields;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SpirePatch(
        cls = "com.megacrit.cardcrawl.cards.AbstractCard",
        method = "<class>"
)
public class FragileField
{
    public static SpireField<Boolean> value = new SpireField<>(() -> {
        return false;
    });

    public FragileField()
    {
    }
}