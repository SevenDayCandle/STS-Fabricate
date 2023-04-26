package pinacolada.characters;

import basemod.ReflectionHacks;
import basemod.abstracts.CustomMonster;
import basemod.animations.AbstractAnimation;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIUtils;
import pinacolada.utilities.GameUtilities;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

public class CreatureAnimationInfo {
    private static final HashMap<String, CreatureAnimationInfo> creatureAnimations = new HashMap<>();
    private static final HashMap<String, String> creatureImages = new HashMap<>();
    private static final HashMap<String, AbstractAnimation> creatureSpriters = new HashMap<>();
    private static final ArrayList<String> playerIDs = new ArrayList<>();

    public final String atlas;
    public final String skeleton;
    public final float scale;

    public CreatureAnimationInfo(String atlas, String skeleton, float scale) {
        this.atlas = atlas;
        this.skeleton = skeleton;
        this.scale = scale;
    }

    public static CreatureAnimationInfo getAnimation(AbstractCreature c) {
        return getAnimationForID(getIdentifierString(c));
    }

    public static CreatureAnimationInfo getAnimationForID(String id) {
        return creatureAnimations.get(id);
    }

    public static String getIdentifierString(Object c) {
        return c != null ? c.getClass().getName() : null;
    }

    public static CreatureAnimationInfo getAnimationForIDWithLoading(String id) {
        CreatureAnimationInfo info = getAnimationForID(id);
        if (info == null) {
            AbstractCreature creature = tryCreate(id);
            if (creature != null) {
                info = getAnimationForID(id);
                if (info == null) {
                    if (creature instanceof CustomMonster) {
                        registerCreatureSpriter((CustomMonster) creature);
                    }
                }
                else {
                    return info;
                }
            }
        }
        return info;
    }

    public static AbstractCreature tryCreate(String id) {
        try {
            Constructor<? extends AbstractCreature> constructor = (Constructor<? extends AbstractCreature>) EUIUtils.tryGetConstructor(Class.forName(id), float.class, float.class);
            assert constructor != null;
            return constructor.newInstance(-9999, -9999);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void registerCreatureSpriter(CustomMonster creature) {
        AbstractAnimation animation = ReflectionHacks.getPrivate(creature, CustomMonster.class, "animation");
        if (animation != null) {
            creatureSpriters.putIfAbsent(getIdentifierString(creature), animation);
        }
    }

    public static String getRandomKey() {
        return GameUtilities.getRandomElement(playerIDs);
    }

    public static AbstractAnimation getSpriterForID(String id) {
        return creatureSpriters.get(id);
    }

    public static boolean isPlayer(String id) {
        return playerIDs.contains(id);
    }

    public static void registerCreatureAnimation(AbstractCreature creature, String atlasUrl, String skeletonUrl, float scale) {
        String id = getIdentifierString(creature);
        if (id != null && !creatureAnimations.containsKey(id)) {
            if (creature instanceof AbstractPlayer) {
                playerIDs.add(id);
            }
            creatureAnimations.put(id, new CreatureAnimationInfo(atlasUrl, skeletonUrl, scale));
        }
    }

    public static void registerCreatureImage(AbstractCreature creature, String imageUrl) {
        creatureImages.putIfAbsent(getIdentifierString(creature), imageUrl);
    }

    public static void tryLoadAnimations(String id) {
        CreatureAnimationInfo info = getAnimationForID(id);
        if (info == null) {
            AbstractCreature creature = tryCreate(id);
            if (creature != null) {
                info = getAnimationForID(id);
                if (info == null) {
                    String image = getImageForID(id);
                    if (image == null && creature instanceof CustomMonster) {
                        registerCreatureSpriter((CustomMonster) creature);
                    }
                }
            }
        }
    }

    public static String getImageForID(String id) {
        return creatureImages.get(id);
    }
}
