package pinacolada.patches.abstractCreature;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.characters.CreatureAnimationInfo;
import pinacolada.misc.CombatStats;

public class AbstractCreaturePatches
{
    @SpirePatch(clz = AbstractCreature.class, method = "loadAnimation")
    public static class AbstractMonster_LoadAnimation
    {
        @SpirePrefixPatch
        public static void prefix(AbstractCreature __instance, String atlasUrl, String skeletonUrl, float scale)
        {
            CreatureAnimationInfo.registerCreatureAnimation(__instance, atlasUrl, skeletonUrl, scale);
        }
    }

    @SpirePatch(clz = AbstractCreature.class, method = "healthBarUpdatedEvent")
    public static class AbstractCreaturePatches_HealthBarUpdatedEvent
    {
        @SpirePostfixPatch
        public static void method(AbstractCreature __instance)
        {
            CombatStats.onHealthBarUpdated(__instance);
        }
    }

    @SpirePatch(clz = AbstractCreature.class, method = "addBlock", paramtypez = {int.class})
    public static class AbstractCreaturePatches_AddBlock
    {
        @SpirePostfixPatch
        public static void method(AbstractCreature __instance, int block)
        {
            CombatStats.onBlockGained(__instance, block);
        }
    }

    @SpirePatch(clz = AbstractCreature.class, method = "brokeBlock")
    public static class AbstractCreaturePatches_BrokeBlock
    {
        @SpirePostfixPatch
        public static void method(AbstractCreature __instance)
        {
            CombatStats.onBlockBroken(__instance);
        }
    }

    @SpirePatch(clz= AbstractCreature.class, method = "loseBlock", paramtypez = {int.class, boolean.class})
    public static class AbstractCreaturePatches_LoseBlock
    {
        @SpirePrefixPatch
        public static void method(AbstractCreature __instance, int amount, boolean noAnimation)
        {
            CombatStats.onBeforeLoseBlock(__instance, amount, noAnimation);
        }
    }
}
