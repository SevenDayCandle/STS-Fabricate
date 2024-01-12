package pinacolada.patches.creature;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import pinacolada.cards.base.PCLCard;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.GridCardSelectScreenHelper;

import java.util.ArrayList;

public class AbstractPlayerPatches {
    @SpirePatch(clz = AbstractPlayer.class, method = "preBattlePrep")
    public static class AbstractPlayer_ApplyPreCombatLogic {
        @SpirePrefixPatch
        public static void method(AbstractPlayer __instance) {
            CombatManager.onStartup();
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "channelOrb")
    public static class AbstractPlayer_ChannelOrb {

        @SpireInsertPatch(locator = Locator.class)
        public static void insert(AbstractPlayer __instance, @ByRef AbstractOrb[] orbToSet) {
            CombatManager.onChannel(orbToSet[0]);
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                final Matcher matcher = new Matcher.MethodCallMatcher(AbstractOrb.class, "playChannelSFX");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "renderHand")
    public static class AbstractPlayer_RenderHand {
        @SpirePrefixPatch
        public static SpireReturn<Void> method(AbstractPlayer __instance, SpriteBatch sb) {
            if (GridCardSelectScreenHelper.isActive()) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "useCard")
    public static class AbstractPlayer_UseCard {

        @SpireInsertPatch(locator = Locator.class, localvars = {"c", "monster"})
        public static void insert(AbstractPlayer __instance, AbstractCard c, AbstractMonster monster) {
            CombatManager.onPlayCardPostActions(c, monster);
        }

        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractCard.class.getName()) && m.getMethodName().equals("use")) {
                        m.replace("{ if (!pinacolada.patches.creature.AbstractPlayerPatches.AbstractPlayer_UseCard.use($0, $1, $2)) $proceed($$); }");
                    }
                    else if (m.getClassName().equals(EnergyManager.class.getName()) && m.getMethodName().equals("use")) {
                        m.replace("{ $_ = $proceed(pinacolada.dungeon.CombatManager.onTrySpendEnergy(c, this, $1)); }");
                    }
                }
            };
        }

        public static boolean use(AbstractCard c, AbstractPlayer p, AbstractMonster m) {
            return CombatManager.onUsingCard(c, p, m);
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                final Matcher matcher = new Matcher.MethodCallMatcher(GameActionManager.class, "addToBottom");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "updateInput")
    public static class AbstractPlayer_UpdateInput {
        @SpirePostfixPatch
        public static void method(AbstractPlayer __instance) {
            if ((__instance.isDraggingCard || __instance.isHoveringDropZone) && __instance.hoveredCard instanceof PCLCard) {
                ((PCLCard) __instance.hoveredCard).onDrag(ReflectionHacks.getPrivate(__instance, AbstractPlayer.class, "hoveredMonster"));
            }
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "damage", paramtypez = {DamageInfo.class})
    public static class AbstractPlayer_Damage {
        @SpireInsertPatch(localvars = {"damageAmount"}, locator = Locator.class)
        public static void insertPre(AbstractPlayer __instance, DamageInfo info, @ByRef int[] damageAmount) {
            damageAmount[0] = Math.max(0, CombatManager.onIncomingDamageFirst(__instance, info, damageAmount[0]));
        }

        @SpireInsertPatch(localvars = {"damageAmount"}, locator = Locator2.class)
        public static void insertPre2(AbstractPlayer __instance, DamageInfo info, @ByRef int[] damageAmount) {
            damageAmount[0] = Math.max(0, CombatManager.onIncomingDamageLast(__instance, info, damageAmount[0]));
        }

        @SpireInsertPatch(localvars = {"damageAmount"}, locator = Locator3.class)
        public static void insertPre3(AbstractPlayer __instance, DamageInfo info, @ByRef int[] damageAmount) {
            CombatManager.onAttack(info, damageAmount[0], __instance);
        }

        @SpireInsertPatch(localvars = {"damageAmount"}, locator = Locator4.class)
        public static void insertPre4(AbstractPlayer __instance, DamageInfo info, @ByRef int[] damageAmount) {
            damageAmount[0] = Math.max(0, CombatManager.onPlayerLoseHP(__instance, info, damageAmount[0]));
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher matcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "decrementBlock");
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
                final Matcher matcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }

        private static class Locator4 extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher matcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "lastDamageTaken");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "initializeStarterDeck")
    public static class AbstractPlayer_InitializeStarterDeck {

        public static void energy(AbstractCard c, AbstractPlayer p, int amount) {
            p.energy.use(CombatManager.onTrySpendEnergy(c, p, amount));
        }

        public static ArrayList<String> getCardList(AbstractPlayer p) {
            ArrayList<String> getCards = AbstractPlayerFields.overrideCards.get(p);
            return getCards != null ? getCards : p.getStartingDeck();
        }

        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractPlayer.class.getName()) && m.getMethodName().equals("getStartingDeck")) {
                        m.replace("{ $_ = pinacolada.patches.creature.AbstractPlayerPatches.AbstractPlayer_InitializeStarterDeck.getCardList($0); }");
                    }
                }
            };
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "renderTargetingUi")
    public static class AbstractPlayer_RenderTargetingUi {
        @SpirePrefixPatch
        public static SpireReturn<Void> method(AbstractPlayer __instance, SpriteBatch s) {
            if (CombatManager.targeting.shouldHideArrows()) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = SpirePatch.CLASS
    )
    public static class AbstractPlayerFields {
        public static SpireField<ArrayList<String>> overrideCards = new SpireField<>(() -> null);
    }
}
