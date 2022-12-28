package pinacolada.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.shop.Merchant;
import pinacolada.interfaces.listeners.OnAddingToCardRewardListener;

import java.util.HashMap;

public class MerchantPatches
{
    @SpirePatch(clz = Merchant.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {float.class, float.class, int.class})
    public static class MerchantPatches_initCards
    {
        private static final HashMap<Merchant, MerchantData> map = new HashMap<>();

        public static class MerchantData
        {
            public CardGroup colorless;
            public CardGroup common;
            public CardGroup uncommon;
            public CardGroup rare;

            protected CardGroup getReplacement(CardGroup group)
            {
                final CardGroup replacement = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
                for (AbstractCard c : group.group)
                {
                    if (c instanceof OnAddingToCardRewardListener && ((OnAddingToCardRewardListener)c).shouldCancel())
                    {
                        continue;
                    }

                    replacement.group.add(c);
                }

                return replacement;
            }
        }

        @SpirePrefixPatch
        public static void prefix(Merchant __instance, float x, float y, int newShopScreen)
        {
            final MerchantData data = new MerchantData();
            map.put(__instance, data);

            data.colorless = AbstractDungeon.colorlessCardPool;
            AbstractDungeon.colorlessCardPool = data.getReplacement(data.colorless);

            data.common = AbstractDungeon.commonCardPool;
            AbstractDungeon.commonCardPool = data.getReplacement(data.common);

            data.uncommon = AbstractDungeon.uncommonCardPool;
            AbstractDungeon.uncommonCardPool = data.getReplacement(data.uncommon);

            data.rare = AbstractDungeon.rareCardPool;
            AbstractDungeon.rareCardPool = data.getReplacement(data.rare);
        }

        @SpirePostfixPatch
        public static void postfix(Merchant __instance, float x, float y, int newShopScreen)
        {
            final MerchantData data = map.remove(__instance);

            AbstractDungeon.colorlessCardPool = data.colorless;
            AbstractDungeon.commonCardPool = data.common;
            AbstractDungeon.uncommonCardPool = data.uncommon;
            AbstractDungeon.rareCardPool = data.rare;
        }
    }
}
