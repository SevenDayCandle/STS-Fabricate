package pinacolada.patches.creature;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CtBehavior;
import pinacolada.characters.PCLCharacterAnimation;
import pinacolada.dungeon.CombatManager;
import pinacolada.monsters.PCLIntentInfo;

public class AbstractMonsterPatches {

    @SpirePatch(clz = AbstractMonster.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {String.class, String.class, int.class, float.class, float.class, float.class, float.class, String.class, float.class, float.class, boolean.class})
    public static class AbstractMonster_Ctor {
        @SpirePostfixPatch
        public static void postfix(AbstractMonster __instance, String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights) {
            if (imgUrl != null) {
                PCLCharacterAnimation.registerCreatureImage(__instance, imgUrl);
            }
        }
    }

    @SpirePatch(clz = AbstractMonster.class, method = "<class>")
    public static class AbstractMonster_Fields {
        public static final SpireField<PCLIntentInfo> enemyIntent = new SpireField<>(() -> null);
    }

    @SpirePatch(clz = AbstractMonster.class, method = "damage", paramtypez = {DamageInfo.class})
    public static class AbstractMonster_Damage1 {
        @SpireInsertPatch(localvars = {"damageAmount"}, locator = Locator.class)
        public static void insertPre(AbstractMonster __instance, DamageInfo info, @ByRef int[] damageAmount) {
            damageAmount[0] = Math.max(0, CombatManager.onIncomingDamageFirst(__instance, info, damageAmount[0]));
        }

        @SpireInsertPatch(localvars = {"damageAmount"}, locator = Locator2.class)
        public static void insertPre2(AbstractMonster __instance, DamageInfo info, @ByRef int[] damageAmount) {
            damageAmount[0] = Math.max(0, CombatManager.onIncomingDamageLast(__instance, info, damageAmount[0]));
        }

        @SpireInsertPatch(localvars = {"damageAmount"}, locator = Locator3.class)
        public static void insertPre3(AbstractMonster __instance, DamageInfo info, int damageAmount) {
            CombatManager.onAttack(info, damageAmount, __instance);
        }

        @SpireInsertPatch(localvars = {"damageAmount"}, locator = Locator4.class)
        public static void insertPre4(AbstractMonster __instance, DamageInfo info, @ByRef int[] damageAmount) {
            damageAmount[0] = Math.max(0, CombatManager.onCreatureLoseHP(__instance, info, damageAmount[0]));
        }


        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher matcher = new Matcher.MethodCallMatcher(AbstractMonster.class, "decrementBlock");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }

        private static class Locator2 extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                final Matcher matcher = new Matcher.MethodCallMatcher(Math.class, "min");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }

        private static class Locator3 extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                final Matcher matcher = new Matcher.FieldAccessMatcher(AbstractMonster.class, "powers");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }

        private static class Locator4 extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                final Matcher matcher = new Matcher.FieldAccessMatcher(AbstractMonster.class, "lastDamageTaken");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }

    @SpirePatch(clz = AbstractMonster.class, method = "die", paramtypez = {boolean.class})
    public static class AbstractMonster_Die {
        @SpirePrefixPatch
        public static SpireReturn<Void> method(AbstractMonster __instance, boolean triggerRelics) {
            if (!__instance.isDying) // to avoid triggering this more than once
            {
                if (!CombatManager.onCreatureDeath(__instance, triggerRelics)) {
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = AbstractMonster.class, method = "calculateDamage", paramtypez = {int.class})
    public static class AbstractMonster_CalculateDamage {
        @SpireInsertPatch(locator = MonsterDamageFinalGiveLocator.class, localvars = {"tmp"})
        public static void finalGive(AbstractMonster __instance, @ByRef float[] tmp) {
            tmp[0] = CombatManager.onModifyDamageGiveLast(tmp[0], DamageInfo.DamageType.NORMAL, __instance, AbstractDungeon.player, null);
        }

        @SpireInsertPatch(locator = PlayerDamageFinalReceiveLocator.class, localvars = {"tmp"})
        public static void finalReceive(AbstractMonster __instance, @ByRef float[] tmp) {
            tmp[0] = CombatManager.onModifyDamageReceiveLast(tmp[0], DamageInfo.DamageType.NORMAL, __instance, AbstractDungeon.player, null);
        }

        @SpireInsertPatch(locator = MonsterDamageGiveLocator.class, localvars = {"tmp"})
        public static void give(AbstractMonster __instance, @ByRef float[] tmp) {
            tmp[0] = CombatManager.onModifyDamageGiveFirst(tmp[0], DamageInfo.DamageType.NORMAL, __instance, AbstractDungeon.player, null);
        }

        @SpirePostfixPatch
        public static void postfix(AbstractMonster __instance, int dmg) {
            PCLIntentInfo.currentEnemy = null;
        }

        @SpirePrefixPatch
        public static void prefix(AbstractMonster __instance, int dmg) {
            PCLIntentInfo.currentEnemy = __instance;
        }

        @SpireInsertPatch(locator = PlayerDamageReceiveLocator.class, localvars = {"tmp"})
        public static void receive(AbstractMonster __instance, @ByRef float[] tmp) {
            tmp[0] = CombatManager.onModifyDamageReceiveFirst(tmp[0], DamageInfo.DamageType.NORMAL, __instance, AbstractDungeon.player, null);
        }

        private static class MonsterDamageGiveLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractMonster.class, "powers");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{tmp[0]};
            }
        }

        private static class MonsterDamageFinalGiveLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractMonster.class, "powers");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{tmp[1]};
            }
        }

        private static class PlayerDamageFinalReceiveLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{tmp[1]};
            }
        }

        private static class PlayerDamageReceiveLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{tmp[0]};
            }
        }
    }
}
