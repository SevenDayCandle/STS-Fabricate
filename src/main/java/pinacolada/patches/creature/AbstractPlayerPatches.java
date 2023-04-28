package pinacolada.patches.creature;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUITooltip;
import javassist.CtBehavior;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.modifiers.OverrideSkillModifier;
import pinacolada.dungeon.CombatManager;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;

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
        @SpireInsertPatch(rloc = 7)
        public static void insert(AbstractPlayer __instance, @ByRef AbstractOrb[] orbToSet) {
            AbstractOrb orb = orbToSet[0];

            // Replace the orb to be channeled according to any effects
            orbToSet[0] = CombatManager.onTryChannelOrb(orb);
        }

        @SpireInsertPatch(locator = Locator.class)
        public static void insert2(AbstractPlayer __instance, @ByRef AbstractOrb[] orbToSet) {
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
        @SpireInsertPatch(rloc = 7)
        public static SpireReturn<Void> insertPre(AbstractPlayer __instance, AbstractCard c, AbstractMonster m, int energyOnUse) {
            // TODO make this into an instrument once you have a patch for EYBCard's useCard patch and you can ensure conflicts don't occur
            // OverrideSkillModifier affects PCLCard skills directly so no need to invoke them below
            final PCLCard pCard = EUIUtils.safeCast(c, PCLCard.class);
            if (pCard != null) {
                CombatManager.onUsingCard(pCard, __instance, m);
                return SpireReturn.Return();
            }

            ArrayList<OverrideSkillModifier> wrappers = OverrideSkillModifier.getAll(c);
            if (!wrappers.isEmpty()) {
                CombatManager.onUsingCardPostActions(c, __instance, m);
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher matcher = new Matcher.MethodCallMatcher(AbstractPlayer_UseCard.class, "Use");
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

    @SpirePatch(clz = AbstractPlayer.class, method = "renderPowerTips", paramtypez = {SpriteBatch.class})
    public static class AbstractPlayer_RenderPowerTips {
        @SpirePrefixPatch
        public static SpireReturn prefix(AbstractPlayer __instance, SpriteBatch sb) {
            if (!PGR.config.vanillaPowerRender.get()) {
                if (EUITooltip.canRenderTooltips()) {
                    EUITooltip.queueTooltips(__instance);
                }
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "damage", paramtypez = {DamageInfo.class})
    public static class AbstractPlayer_Damage {
        @SpireInsertPatch(localvars = {"damageAmount"}, locator = Locator.class)
        public static void insertPre(AbstractPlayer __instance, DamageInfo info, @ByRef int[] damageAmount) {
            damageAmount[0] = Math.max(0, CombatManager.onModifyDamageFirst(__instance, info, damageAmount[0]));
        }

        @SpireInsertPatch(localvars = {"damageAmount"}, locator = Locator2.class)
        public static void insertPre2(AbstractPlayer __instance, DamageInfo info, @ByRef int[] damageAmount) {
            damageAmount[0] = Math.max(0, CombatManager.onModifyDamageLast(__instance, info, damageAmount[0]));
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
}
