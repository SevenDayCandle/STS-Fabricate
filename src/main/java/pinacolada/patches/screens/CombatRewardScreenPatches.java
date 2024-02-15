package pinacolada.patches.screens;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import extendedui.utilities.EUIClassUtils;
import javassist.CtBehavior;
import pinacolada.effects.PCLEffects;
import pinacolada.interfaces.listeners.OnReceiveRewardsListener;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.utilities.GameUtilities;

// Copied and modified from STS-AnimatorMod
public class CombatRewardScreenPatches {
    @SpirePatch(clz = CombatRewardScreen.class, method = "update")
    public static class CombatRewardScreenPatches_Update {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(CombatRewardScreen __instance) {
            if (PCLEffects.isEmpty()) {
                return SpireReturn.Continue();
            }
            EUIClassUtils.invoke(__instance, "updateEffects");
            return SpireReturn.Return();
        }
    }

    @SpirePatch(clz = CombatRewardScreen.class, method = "setupItemReward")
    public static class CombatRewardScreenPatches_SetupItemReward {

        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(CombatRewardScreen __instance) {
            AbstractRoom currentRoom = GameUtilities.getCurrentRoom();

            final AbstractPlayer p = AbstractDungeon.player;
            for (AbstractRelic r : p.relics) {
                if (r instanceof OnReceiveRewardsListener) {
                    ((OnReceiveRewardsListener) r).onReceiveRewards(__instance.rewards, currentRoom);
                }
                if (r instanceof PointerProvider) {
                    for (PSkill<?> move : ((PointerProvider) r).getFullEffects()) {
                        move.invokeCastChildren(OnReceiveRewardsListener.class, target -> target.onReceiveRewards(__instance.rewards, currentRoom));
                    }
                }
            }
            for (AbstractBlight r : p.blights) {
                if (r instanceof OnReceiveRewardsListener) {
                    ((OnReceiveRewardsListener) r).onReceiveRewards(__instance.rewards, currentRoom);
                }
                if (r instanceof PointerProvider) {
                    for (PSkill<?> move : ((PointerProvider) r).getFullEffects()) {
                        move.invokeCastChildren(OnReceiveRewardsListener.class, target -> target.onReceiveRewards(__instance.rewards, currentRoom));
                    }
                }
            }

            if (p instanceof OnReceiveRewardsListener) {
                ((OnReceiveRewardsListener) p).onReceiveRewards(__instance.rewards, currentRoom);
            }

            PGR.dungeon.tryCreateAugmentReward(__instance.rewards);
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CombatRewardScreen.class, "positionRewards");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}