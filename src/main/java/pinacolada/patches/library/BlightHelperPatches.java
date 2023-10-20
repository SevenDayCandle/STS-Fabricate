package pinacolada.patches.library;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.blights.Durian;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.BlightHelper;
import com.megacrit.cardcrawl.random.Random;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.utilities.BlightTier;
import pinacolada.annotations.VisibleBlight;
import pinacolada.blights.PCLBlightData;
import pinacolada.blights.PCLCustomBlightSlot;
import pinacolada.resources.PCLPlayerData;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class BlightHelperPatches {
    private static boolean TEMP_DISABLE_PATCH;
    private static final HashMap<String, Class<? extends AbstractBlight>> additionalBlights = new HashMap<>();

    public static Collection<String> getAdditionalBlightIDs() {
        return additionalBlights.keySet();
    }

    public static ArrayList<AbstractBlight> getAdditionalBlights() {
        return EUIUtils.map(getAdditionalBlightIDs(), EUIGameUtils::getSeenBlight);
    }

    public static RandomizedList<String> getBlightIDs(Collection<BlightTier> tiers, Collection<AbstractCard.CardColor> colors, boolean allowCustom) {
        RandomizedList<String> blights = new RandomizedList<>();
        for (BlightTier tier : tiers) {
            switch (tier) {
                case BOSS:
                    blights.addAll(BlightHelper.chestBlights);
                    break;
                case BASIC:
                    blights.addAll(BlightHelper.blights);
                    blights.removeIf(b -> BlightHelper.chestBlights.contains(b));
                    for (String a : additionalBlights.keySet()) {
                        if (PCLBlightData.getStaticData(a) == null) {
                            blights.add(a);
                        }
                    }
                case SPECIAL:
                    for (String a : additionalBlights.keySet()) {
                        PCLBlightData data = PCLBlightData.getStaticData(a);
                        if (data != null && data.tier == BlightTier.SPECIAL) {
                            blights.add(a);
                        }
                    }
            }
        }
        for (PCLBlightData data : PCLBlightData.getAllData(false, false, b -> tiers.contains(b.tier) && colors.contains(b.cardColor))) {
            blights.add(data.ID);
        }

        // TODO make this better
        PCLPlayerData<?,?,?> data = PGR.dungeon.getPlayerData();
        if (data != null) {
            String[] additional = data.getAdditionalBlightIDs(allowCustom);
            if (additional != null) {
                for (String s : additional) {
                    PCLBlightData bd = PCLBlightData.getStaticData(s);
                    if (bd == null) {
                        PCLCustomBlightSlot slot = PCLCustomBlightSlot.get(s);
                        if (slot != null) {
                            bd = slot.getBuilder(0);
                        }
                    }
                    if (bd != null && tiers.contains(bd.tier) && colors.contains(bd.cardColor)) {
                        blights.add(s);
                    }
                }
            }
        }

        if (allowCustom) {
            for (AbstractCard.CardColor color : colors) {
                for (PCLCustomBlightSlot slot : PCLCustomBlightSlot.getBlights(color)) {
                    if (slot.tier != null) {
                        BlightTier t = BlightTier.valueOf(slot.tier);
                        if (tiers.contains(t)) {
                            blights.add(slot.ID);
                        }
                    }
                }
            }
        }

        return blights;
    }

    public static AbstractBlight getDirectBlight(String id) {
        AbstractBlight blight = getDirectBlightInternal(id);
        if (blight != null) {
            return blight;
        }
        // Hardcoded stuff -_-
        TEMP_DISABLE_PATCH = true;
        blight = BlightHelper.getBlight(id);
        TEMP_DISABLE_PATCH = false;
        return blight;
    }

    private static AbstractBlight getDirectBlightInternal(String id) {
        PCLBlightData data = PCLBlightData.getStaticData(id);
        if (data != null) {
            return data.create();
        }
        final Class<? extends AbstractBlight> blight = additionalBlights.get(id);
        if (blight != null) {
            try {
                return blight.getConstructor().newInstance();
            }
            catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static AbstractBlight getRandomBlightForTier(BlightTier tier) {
        return getRandomBlightForTier(tier, AbstractDungeon.relicRng, AbstractDungeon.player != null ? AbstractDungeon.player.getCardColor() : AbstractCard.CardColor.COLORLESS, Collections.emptyList(), PGR.dungeon.allowCustomBlights);
    }

    public static AbstractBlight getRandomBlightForTier(BlightTier tier, Random rng, AbstractCard.CardColor actingColor, Collection<String> exclusions, boolean allowCustom) {
        RandomizedList<String> blights = getBlightIDs(tier == null ? EUIUtils.set(BlightTier.BASIC, BlightTier.BOSS) : Collections.singleton(tier),
                GameUtilities.isColorlessCardColor(actingColor) ? Collections.singleton(AbstractCard.CardColor.COLORLESS) : EUIUtils.set(actingColor, AbstractCard.CardColor.COLORLESS),
                allowCustom);
        blights.removeAll(exclusions);
        String res = blights.retrieve(rng, false);
        return res != null ? BlightHelper.getBlight(res) : null;
    }

    public static void loadCustomBlights() {
        for (Class<?> ct : GameUtilities.getClassesWithAnnotation(VisibleBlight.class)) {
            try {
                VisibleBlight a = ct.getAnnotation(VisibleBlight.class);
                Object data = ReflectionHacks.getPrivateStatic(ct, a.data());
                String id = data instanceof PCLBlightData ? ((PCLBlightData) data).ID : String.valueOf(data);
                additionalBlights.put(id, (Class<? extends AbstractBlight>) ct);
                EUIUtils.logInfoIfDebug(BlightHelper.class, "Adding blight " + id);
            }
            catch (Exception e) {
                EUIUtils.logError(PGR.class, "Failed to load blight " + ct.getName() + ": " + e.getLocalizedMessage());
            }
        }
    }

    @SpirePatch(clz = BlightHelper.class, method = "getBlight", paramtypez = {String.class})
    public static class BlightHelperPatches_GetBlight {
        @SpirePrefixPatch
        public static SpireReturn<AbstractBlight> method(String id) {
            if (!TEMP_DISABLE_PATCH) {
                AbstractBlight internal = getDirectBlightInternal(id);
                if (internal != null) {
                    return SpireReturn.Return(internal);
                }
                PCLCustomBlightSlot slot = PCLCustomBlightSlot.get(id);
                if (slot != null) {
                    return SpireReturn.Return(slot.make());
                }
            }
            return SpireReturn.Continue();
        }
    }

    // Despite the name, this only pulls chest blights :)
    @SpirePatch(clz = BlightHelper.class, method = "getRandomBlight", paramtypez = {})
    public static class BlightHelperPatches_GetRandomBlight {
        @SpirePrefixPatch
        public static SpireReturn<AbstractBlight> method() {
            AbstractBlight res = null;
            if (AbstractDungeon.player == null) {
                return SpireReturn.Return(getRandomBlightForTier(BlightTier.BOSS));
            }
            // Hardcoded durian check
            return SpireReturn.Return(getRandomBlightForTier(BlightTier.BOSS, AbstractDungeon.relicRng, AbstractDungeon.player.getCardColor(),
                    AbstractDungeon.player.maxHealth <= 20 ? Collections.singleton(Durian.ID) : Collections.emptyList(), PGR.dungeon.allowCustomBlights));
        }
    }

    @SpirePatch(clz = BlightHelper.class, method = "getRandomBlight", paramtypez = {Random.class})
    public static class BlightHelperPatches_GetRandomBlight2 {
        @SpirePrefixPatch
        public static SpireReturn<AbstractBlight> method(Random rng) {
            return SpireReturn.Return(getRandomBlightForTier(null, rng, AbstractDungeon.player != null ? AbstractDungeon.player.getCardColor() : AbstractCard.CardColor.COLORLESS, Collections.emptyList(), PGR.dungeon.allowCustomBlights));
        }
    }

    @SpirePatch(clz = BlightHelper.class, method = "getRandomChestBlight")
    public static class BlightHelperPatches_GetRandomChestBlight {
        @SpirePrefixPatch
        public static SpireReturn<AbstractBlight> method(ArrayList<String> exclusions) {
            return SpireReturn.Return(getRandomBlightForTier(BlightTier.BOSS, AbstractDungeon.relicRng, AbstractDungeon.player != null ? AbstractDungeon.player.getCardColor() : AbstractCard.CardColor.COLORLESS, exclusions, PGR.dungeon.allowCustomBlights));
        }
    }
}