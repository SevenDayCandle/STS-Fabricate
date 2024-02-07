package pinacolada.characters;

import basemod.ReflectionHacks;
import basemod.abstracts.CustomMonster;
import basemod.animations.AbstractAnimation;
import basemod.animations.SpriterAnimation;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIUtils;
import pinacolada.misc.PCLCustomLoadable;
import pinacolada.utilities.GameUtilities;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static pinacolada.utilities.GameUtilities.JSON_FILTER;

public class PCLCharacterAnimation extends AbstractAnimation {
    private static final String ANIMATION_PATH = PCLCustomLoadable.FOLDER + "/animations";
    private static final HashMap<String, AbstractAnimation> creatureAnimations = new HashMap<>();
    private static final HashMap<String, String> creatureImages = new HashMap<>();
    private static final ArrayList<String> playerIDs = new ArrayList<>();

    public final String atlasUrl;
    public final String skeletonUrl;
    public final float scale;

    public PCLCharacterAnimation(String atlasUrl, String skeletonUrl, float scale) {
        this.atlasUrl = atlasUrl;
        this.skeletonUrl = skeletonUrl;
        this.scale = scale;
    }

    public static Collection<String> getAll() {
        return creatureAnimations.keySet();
    }

    public static AbstractAnimation getAnimation(AbstractCreature c) {
        return getAnimationForID(getIdentifierString(c));
    }

    public static AbstractAnimation getAnimationForID(String id) {
        return creatureAnimations.get(id);
    }

    public static AbstractAnimation getAnimationForIDWithLoading(String id) {
        AbstractAnimation info = getAnimationForID(id);
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

    public static String getIdentifierString(Object c) {
        return c != null ? c.getClass().getName() : null;
    }

    public static String getImageForID(String id) {
        return creatureImages.get(id);
    }

    public static String getRandomKey() {
        return GameUtilities.getRandomElement(playerIDs);
    }

    public static boolean isPlayer(String id) {
        return playerIDs.contains(id);
    }

    // TODO load all enemies
    public static void postInitialize() {
        FileHandle folder = Gdx.files.local(ANIMATION_PATH);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        for (FileHandle f : folder.list()) {
            String name = f.nameWithoutExtension();
            String base = ANIMATION_PATH + "/" + name;
            switch (f.extension()) {
                case "json":
                    String atlasUrl = base + ".atlas";
                    if (Gdx.files.local(atlasUrl).exists()) {
                        registerCustomAnimation(f.name(), atlasUrl, f.path(), 1f, true);
                    }
                    break;
                case "scml":
                    registerCustomSpriter(name, f.path(), true);
                    break;
            }
        }
    }

    public static void registerCreatureAnimation(AbstractCreature creature, AbstractAnimation animation) {
        String id = getIdentifierString(creature);
        if (id != null && !creatureAnimations.containsKey(id)) {
            if (creature instanceof AbstractPlayer) {
                playerIDs.add(id);
            }
            creatureAnimations.put(id, animation);
        }
    }

    public static void registerCreatureAnimation(AbstractCreature creature, String atlasUrl, String skeletonUrl, float scale) {
        String id = getIdentifierString(creature);
        if (id != null && !creatureAnimations.containsKey(id)) {
            if (creature instanceof AbstractPlayer) {
                playerIDs.add(id);
            }
            creatureAnimations.put(id, new PCLCharacterAnimation(atlasUrl, skeletonUrl, scale));
        }
    }

    public static void registerCreatureImage(AbstractCreature creature, String imageUrl) {
        creatureImages.putIfAbsent(getIdentifierString(creature), imageUrl);
    }

    public static void registerCreatureSpriter(CustomMonster creature) {
        AbstractAnimation animation = ReflectionHacks.getPrivate(creature, CustomMonster.class, "animation");
        if (animation != null) {
            creatureAnimations.putIfAbsent(getIdentifierString(creature), animation);
        }
    }

    public static void registerCustomAnimation(String id, String atlasUrl, String skeletonUrl, float scale, boolean isPlayer) {
        if (id != null && !creatureAnimations.containsKey(id)) {
            if (isPlayer) {
                playerIDs.add(id);
            }
            creatureAnimations.put(id, new PCLCharacterAnimation(atlasUrl, skeletonUrl, scale));
        }
    }

    public static void registerCustomSpriter(String id, String scml, boolean isPlayer) {
        if (id != null && !creatureAnimations.containsKey(id)) {
            if (isPlayer) {
                playerIDs.add(id);
            }
            creatureAnimations.put(id, new SpriterAnimation(scml));
        }
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

    public static void tryLoadAnimations(String id) {
        AbstractAnimation info = getAnimationForID(id);
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

    @Override
    public Type type() {
        return Type.NONE;
    }
}
