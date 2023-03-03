package pinacolada.cards.base.tags;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SpirePatch(
        cls = "com.megacrit.cardcrawl.cards.AbstractCard",
        method = "<class>"
)
public class PurgeField
{
    // This version of purge activates after a certain number of uses, and moves the card to the PURGED_CARDS pile
    public static SpireField<Integer> value = new SpireField<>(() -> {
        return 0;
    });

    public PurgeField()
    {
    }
}