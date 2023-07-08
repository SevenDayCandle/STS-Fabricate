package pinacolada.patches.creature;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.ui.tooltips.EUITooltip;
import javassist.CtBehavior;
import pinacolada.characters.CreatureAnimationInfo;
import pinacolada.dungeon.CombatManager;
import pinacolada.monsters.PCLIntentInfo;
import pinacolada.resources.PGR;

public class AbstractMonsterPatches {
    @SpirePatch(clz = AbstractMonster.class, method = "renderTip", paramtypez = {SpriteBatch.class})
    public static class AbstractMonster_RenderTip {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(AbstractMonster __instance, SpriteBatch sb) {
            if (!PGR.config.vanillaPowerRender.get()) {
                if (__instance.reticleAlpha == 0) {
                    EUITooltip.queueTooltips(__instance);
                }
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractMonster.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {String.class, String.class, int.class, float.class, float.class, float.class, float.class, String.class, float.class, float.class, boolean.class})
    public static class AbstractMonster_Ctor {
        @SpirePostfixPatch
        public static void postfix(AbstractMonster __instance, String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights) {
            if (imgUrl != null) {
                CreatureAnimationInfo.registerCreatureImage(__instance, imgUrl);
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
        public static void insertPre3(AbstractMonster __instance, DamageInfo info, @ByRef int[] damageAmount) {
            CombatManager.onAttack(info, damageAmount[0], __instance);
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
    }

    @SpirePatch(clz = AbstractMonster.class, method = "die", paramtypez = {boolean.class})
    public static class AbstractMonster_Die {
        @SpirePrefixPatch
        public static void method(AbstractMonster __instance, boolean triggerRelics) {
            if (!__instance.isDying) // to avoid triggering this more than once
            {
                CombatManager.onMonsterDeath(__instance, triggerRelics);
            }
        }
    }

    @SpirePatch(clz = AbstractMonster.class, method = "calculateDamage", paramtypez = {int.class})
    public static class AbstractMonster_CalculateDamage {
        @SpirePostfixPatch
        public static void postfix(AbstractMonster __instance, int dmg) {
            PCLIntentInfo.currentEnemy = null;
        }

        @SpirePrefixPatch
        public static void prefix(AbstractMonster __instance, int dmg) {
            PCLIntentInfo.currentEnemy = __instance;
        }
    }
}
