package pinacolada.patches.creature;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.ui.EUIBase;
import extendedui.ui.tooltips.EUITooltip;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.characters.CreatureAnimationInfo;
import pinacolada.dungeon.CombatManager;
import pinacolada.monsters.PCLIntentInfo;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

public class AbstractMonsterPatches {
    public static AbstractCreature getTarget(AbstractMonster mo) {
        AbstractCreature c = CombatManager.summons.getTarget(mo);
        return c != null ? c : AbstractDungeon.player;
    }

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

    @SpirePatch(clz = AbstractMonster.class, method = "applyPowers")
    public static class AbstractMonster_ApplyPowers {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractCard.class.getName()) && m.getFieldName().equals("target")) {
                        m.replace("{ $_ = $proceed($1, pinacolada.patches.creature.AbstractMonsterPatches.getTarget($0)); }");
                    }
                }
            };
        }

    }

    @SpirePatch(clz = AbstractMonster.class, method = "renderIntent")
    public static class AbstractMonster_RenderIntent {
        @SpirePostfixPatch
        public static void postfix(AbstractMonster __instance, SpriteBatch sb) {
            if (GameUtilities.isAttacking(__instance.intent)) {
                AbstractCreature c = CombatManager.summons.getTarget(__instance);
                // null means aoe
                if (c == null) {
                    FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, PCLCardTarget.AllEnemy.getShortString(), __instance.intentHb.cX - 64.0F, __instance.intentHb.cY - 90.0F + GameUtilities.getBobEffect(__instance).y, Settings.CREAM_COLOR);
                }
                else if (c != AbstractDungeon.player && (__instance.hb.hovered || __instance.intentHb.hovered)) {
                    PCLRenderHelpers.drawCurve(sb, ImageMaster.TARGET_UI_ARROW, Color.SCARLET.cpy(), __instance.hb, c.hb, EUIBase.scale(100), 0.25f, 0.02f, 20);
                }
            }
        }
    }
}
