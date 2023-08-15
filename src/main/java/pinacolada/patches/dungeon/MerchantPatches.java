package pinacolada.patches.dungeon;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.shop.Merchant;
import pinacolada.resources.PGR;

// Copied and modified from STS-AnimatorMod
public class MerchantPatches {
    @SpirePatch(clz = Merchant.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {float.class, float.class, int.class})
    public static class MerchantPatches_initCards {
        private static CardGroup colorless;
        private static CardGroup common;
        private static CardGroup uncommon;
        private static CardGroup rare;

        protected static CardGroup getReplacement(CardGroup group) {
            final CardGroup replacement = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
            for (AbstractCard c : group.group) {
                if (!PGR.dungeon.tryCancelCardReward(c)) {
                    replacement.group.add(c);
                }
            }

            return replacement;
        }

        @SpirePostfixPatch
        public static void postfix(Merchant __instance, float x, float y, int newShopScreen) {
            AbstractDungeon.colorlessCardPool = colorless;
            AbstractDungeon.commonCardPool = common;
            AbstractDungeon.uncommonCardPool = uncommon;
            AbstractDungeon.rareCardPool = rare;
        }

        @SpirePrefixPatch
        public static void prefix(Merchant __instance, float x, float y, int newShopScreen) {
            colorless = AbstractDungeon.colorlessCardPool;
            AbstractDungeon.colorlessCardPool = getReplacement(colorless);

            common = AbstractDungeon.commonCardPool;
            AbstractDungeon.commonCardPool = getReplacement(common);

            uncommon = AbstractDungeon.uncommonCardPool;
            AbstractDungeon.uncommonCardPool = getReplacement(uncommon);

            rare = AbstractDungeon.rareCardPool;
            AbstractDungeon.rareCardPool = getReplacement(rare);
        }
    }
}
