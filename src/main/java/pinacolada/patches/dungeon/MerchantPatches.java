package pinacolada.patches.dungeon;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.shop.Merchant;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.resources.PGR;

// Copied and modified from STS-AnimatorMod
public class MerchantPatches {
    @SpirePatch(clz = Merchant.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {float.class, float.class, int.class})
    public static class MerchantPatches_initCards {
        private static CardGroup colorless;
        private static CardGroup common;
        private static CardGroup uncommon;
        private static CardGroup rare;

        private static CardGroup getReplacement(CardGroup group, int minAttackSkill, int minPower) {
            final CardGroup replacement = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
            for (AbstractCard c : group.group) {
                if (!PGR.dungeon.tryCancelCardReward(c) && PGR.dungeon.canObtainCopy(c)) {
                    replacement.group.add(c);
                }
            }
            // The pool must have at least two attack/skill and one power because LOOP DEE LOOP DEE LOOP
            int attackCount = 0;
            int skillCount = 0;
            int powerCount = 0;
            for (AbstractCard c : replacement.group) {
                if (c.type == AbstractCard.CardType.ATTACK) {
                    attackCount += 1;
                }
                else if (c.type == AbstractCard.CardType.SKILL) {
                    skillCount += 1;
                }
                else if (c.type == AbstractCard.CardType.POWER) {
                    powerCount += 1;
                }
            }
            while (attackCount < minAttackSkill) {
                replacement.group.add(makeTempCard(AbstractCard.CardType.ATTACK));
                attackCount += 1;
            }
            while (skillCount < minAttackSkill) {
                replacement.group.add(makeTempCard(AbstractCard.CardType.SKILL));
                skillCount += 1;
            }
            while (powerCount < minPower) {
                replacement.group.add(makeTempCard(AbstractCard.CardType.POWER));
                powerCount += 1;
            }

            if (replacement.group.isEmpty()) {
                replacement.group.add(makeTempCard(AbstractCard.CardType.SKILL));
            }

            return replacement;
        }

        // Must change the card color to curse because colorless will apparently cause LOOP DEE LOOP
        private static AbstractCard makeTempCard(AbstractCard.CardType type) {
            AbstractCard c = AbstractDungeonPatches.makeTempCard(type);
            c.color = AbstractCard.CardColor.CURSE;
            return c;
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
            AbstractDungeon.colorlessCardPool = getReplacement(colorless, 0, 0);

            common = AbstractDungeon.commonCardPool;
            AbstractDungeon.commonCardPool = getReplacement(common, 2, 0);

            uncommon = AbstractDungeon.uncommonCardPool;
            AbstractDungeon.uncommonCardPool = getReplacement(uncommon, 2, 1);

            rare = AbstractDungeon.rareCardPool;
            AbstractDungeon.rareCardPool = getReplacement(rare, 2, 1);
        }
    }
}
