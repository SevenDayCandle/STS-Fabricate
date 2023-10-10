package pinacolada.patches.creature;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.characters.PCLCharacterAnimation;
import pinacolada.dungeon.CombatManager;

public class AbstractCreaturePatches {
    @SpirePatch(clz = AbstractCreature.class, method = "loadAnimation")
    public static class AbstractMonster_LoadAnimation {
        @SpirePrefixPatch
        public static void prefix(AbstractCreature __instance, String atlasUrl, String skeletonUrl, float scale) {
            PCLCharacterAnimation.registerCreatureAnimation(__instance, atlasUrl, skeletonUrl, scale);
        }
    }

    @SpirePatch(clz = AbstractCreature.class, method = "healthBarUpdatedEvent")
    public static class AbstractCreaturePatches_HealthBarUpdatedEvent {
        @SpirePostfixPatch
        public static void method(AbstractCreature __instance) {
            CombatManager.onHealthBarUpdated(__instance);
        }
    }

    @SpirePatch(clz = AbstractCreature.class, method = "addBlock", paramtypez = {int.class})
    public static class AbstractCreaturePatches_AddBlock {
        @SpirePrefixPatch
        public static void method(AbstractCreature __instance, @ByRef int[] block) {
            block[0] = CombatManager.onBlockGained(__instance, block[0]);
        }
    }

    @SpirePatch(clz = AbstractCreature.class, method = "brokeBlock")
    public static class AbstractCreaturePatches_BrokeBlock {
        @SpirePostfixPatch
        public static void method(AbstractCreature __instance) {
            CombatManager.onBlockBroken(__instance);
        }
    }

    @SpirePatch(clz = AbstractCreature.class, method = "heal", paramtypez = {int.class})
    public static class AbstractCreaturePatches_Heal {
        @SpirePrefixPatch
        public static void method(AbstractCreature __instance, @ByRef int[] healAmount) {
            healAmount[0] = CombatManager.onCreatureHeal(__instance, healAmount[0]);
        }
    }
}
