package pinacolada.cards.base.tags;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SpirePatch(
        cls = "com.megacrit.cardcrawl.cards.AbstractCard",
        method = "<class>"
)
public class InnateField {
    public static SpireField<Integer> value = new SpireField<>(() -> 0);

    public InnateField() {
    }
}