package pinacolada.patches.creature;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIUtils;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import pinacolada.cards.base.PCLCard;
import pinacolada.dungeon.CombatManager;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PCLEnum;

import java.util.ArrayList;

public class AbstractPlayerPatches {
    protected static ArrayList<AbstractMonster> targetsCache;

    // TODO support for Friendly Minions
    protected static void replaceTargets(AbstractPlayer player) {
        final PCLCard card = EUIUtils.safeCast(player.hoveredCard, PCLCard.class);
        if (card != null && (card.pclTarget.targetsAllies() || card.type == PCLEnum.CardType.SUMMON)) {
            final MonsterGroup group = AbstractDungeon.getCurrRoom().monsters;
            final ArrayList<AbstractMonster> summons = CombatManager.summons.getSummons(card.type != PCLEnum.CardType.SUMMON);
            if (card.type != PCLEnum.CardType.SUMMON && card.pclTarget.targetsEnemies()) {
                summons.addAll(group.monsters);
            }
            if (summons.size() > 0) {
                targetsCache = group.monsters;
                group.monsters = summons;

                // Summons should always target an available slot, regardless of whether it is occupied or not
                if (card.type == PCLEnum.CardType.SUMMON) {
                    card.target = AbstractCard.CardTarget.ENEMY;
                    PCLCardAlly.emptyAnimation.highlight();
                }
                // For cards that can also target yourself, assume you are targeting yourself if nothing else is hovered
                else {
                    PCLCardAlly.emptyAnimation.unhighlight();
                    if (card.pclTarget.targetsSelf() && !EUIUtils.any(group.monsters, g -> g.hb.hovered)) {
                        card.target = AbstractCard.CardTarget.SELF;
                    }
                    else {
                        card.target = card.pclTarget.cardTarget;
                    }
                }
                return;
            }
        }
        PCLCardAlly.emptyAnimation.unhighlight();
        targetsCache = null;
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

    @SpirePatch(clz = AbstractPlayer.class, method = "useCard")
    public static class AbstractPlayer_UseCard {

        public static void energy(AbstractCard c, AbstractPlayer p, int amount) {
            p.energy.use(CombatManager.onTrySpendEnergy(c, p, amount));
        }

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
                        m.replace("{ pinacolada.patches.creature.AbstractPlayerPatches.AbstractPlayer_UseCard.energy(c, this, $1); }");
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

    @SpirePatch(clz = AbstractPlayer.class, method = "applyPreCombatLogic")
    public static class AbstractPlayer_ApplyPreCombatLogic {
        @SpirePrefixPatch
        public static void method(AbstractPlayer __instance) {
            CombatManager.onStartup();
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

    @SpirePatch(clz = AbstractPlayer.class, method = "clickAndDragCards")
    public static class AbstractPlayer_ClickAndDragCards {
        @SpirePostfixPatch
        public static void Postfix(AbstractPlayer __instance) {
            if (targetsCache != null) {
                AbstractDungeon.getCurrRoom().monsters.monsters = targetsCache;
                targetsCache = null;
            }
        }

        @SpirePrefixPatch
        public static void Prefix(AbstractPlayer __instance) {
            replaceTargets(__instance);
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "updateSingleTargetInput")
    public static class AbstractPlayer_UpdateSingleTargetInput {
        @SpirePostfixPatch
        public static void Postfix(AbstractPlayer __instance) {
            if (targetsCache != null) {
                AbstractDungeon.getCurrRoom().monsters.monsters = targetsCache;
                targetsCache = null;
            }
        }

        @SpirePrefixPatch
        public static void Prefix(AbstractPlayer __instance) {
            replaceTargets(__instance);
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

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = SpirePatch.CLASS
    )
    public static class AbstractPlayerFields {
        public static SpireField<ArrayList<String>> overrideCards = new SpireField<>(() -> null);
    }
}
