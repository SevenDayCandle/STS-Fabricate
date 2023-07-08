package pinacolada.resources;

import extendedui.EUIUtils;
import extendedui.ui.TextureCache;

public abstract class AbstractImages {
    public static final String CHARACTER_PNG = "images/{0}/characters/idle/char.png";
    public static final String SKELETON_ATLAS = "images/{0}/characters/idle/char.atlas";
    public static final String SKELETON_JSON = "images/{0}/characters/idle/char.json";
    public static final String SHOULDER1_PNG = "images/{0}/characters/shoulder.png";
    public static final String SHOULDER2_PNG = "images/{0}/characters/shoulder2.png";
    public static final String CORPSE_PNG = "images/{0}/characters/corpse.png";
    public static final String CHAR_BUTTON_PNG = "images/{0}/ui/charselect/button.png";
    public static final String CHAR_BACKGROUND = "images/{0}/ui/charselect/background.png";

    public static final String ATTACK_PNG = "images/{0}/cardui/512/bg_attack_canvas.png";
    public static final String SKILL_PNG = "images/{0}/cardui/512/bg_skill_canvas.png";
    public static final String POWER_PNG = "images/{0}/cardui/512/bg_power_canvas.png";
    public static final String SUMMON_PNG = "images/{0}/cardui/512/bg_summon_canvas.png";
    public static final String ATTACK_PNG_L = "images/{0}/cardui/1024/bg_attack_canvas.png";
    public static final String SKILL_PNG_L = "images/{0}/cardui/1024/bg_skill_canvas.png";
    public static final String POWER_PNG_L = "images/{0}/cardui/1024/bg_power_canvas.png";
    public static final String SUMMON_PNG_L = "images/{0}/cardui/1024/bg_summon_canvas.png";
    public static final String ORB_A_PNG = "images/{0}/cardui/512/energy_orb_default_a.png";
    public static final String ORB_B_PNG = "images/{0}/cardui/512/energy_orb_default_b.png";
    public static final String ORB_C_PNG = "images/{0}/cardui/512/energy_orb_default_c.png";
    public static final String ORB_BASE_LAYER = "images/{0}/ui/energy/BaseLayer.png";
    public static final String ORB_FLASH = "images/{0}/ui/energy/OrbFlash.png";
    public static final String ORB_TOP_LAYER1 = "images/{0}/ui/energy/TopLayer1.png";
    public static final String ORB_TOP_LAYER2 = "images/{0}/ui/energy/TopLayer2.png";
    public static final String ORB_TOP_LAYER3 = "images/{0}/ui/energy/TopLayer3.png";
    public static final String ORB_TOP_LAYER4 = "images/{0}/ui/energy/TopLayer4.png";
    public static final String AFFINITY_RED = "images/{0}/cardui/affinities/Red.png";
    public static final String AFFINITY_GREEN = "images/{0}/cardui/affinities/Green.png";
    public static final String AFFINITY_BLUE = "images/{0}/cardui/affinities/Blue.png";
    public static final String AFFINITY_ORANGE = "images/{0}/cardui/affinities/Orange.png";
    public static final String AFFINITY_LIGHT = "images/{0}/cardui/affinities/Light.png";
    public static final String AFFINITY_DARK = "images/{0}/cardui/affinities/Dark.png";
    public static final String AFFINITY_SILVER = "images/{0}/cardui/affinities/Silver.png";

    public String character;
    public String skeletonAtlas;
    public String skeletonJson;
    public String shoulder1;
    public String shoulder2;
    public String corpse;
    public String charButton;
    public String charBackground;

    public String attack;
    public String skill;
    public String power;
    public String summon;
    public String attackL;
    public String skillL;
    public String powerL;
    public String summonL;
    public String orbA;
    public String orbB;
    public String orbC;

    public TextureCache orbBaseLayer;
    public TextureCache orbFlash;
    public TextureCache orbTopLayer1;
    public TextureCache orbTopLayer2;
    public TextureCache orbTopLayer3;
    public TextureCache orbTopLayer4;

    public TextureCache cardEnergyOrb;
    public TextureCache cardEnergyOrbL;
    public TextureCache cardBackgroundAttack;
    public TextureCache cardBackgroundPower;
    public TextureCache cardBackgroundSkill;
    public TextureCache cardBackgroundAttackL;
    public TextureCache cardBackgroundPowerL;
    public TextureCache cardBackgroundSkillL;
    public TextureCache cardBackgroundSummon;
    public TextureCache cardBackgroundSummonL;

    public AffinityIcons affinities;

    public AbstractImages(String id) {
        initializeCardImages(id);
        initializeCharacterImages(id);
        initializeAffinityTextures(id);
        initializeOrbTextures(id);
    }

    public TextureCache[] getOrbTextures() {
        return new TextureCache[]{
                orbBaseLayer, orbTopLayer1, orbTopLayer2, orbTopLayer3, orbTopLayer4
        };
    }

    protected void initializeAffinityTextures(String id) {
        affinities = new AffinityIcons(id);
    }

    protected void initializeCardImages(String id) {
        attack = EUIUtils.format(ATTACK_PNG, id);
        skill = EUIUtils.format(SKILL_PNG, id);
        power = EUIUtils.format(POWER_PNG, id);
        summon = EUIUtils.format(SUMMON_PNG, id);
        attackL = EUIUtils.format(ATTACK_PNG_L, id);
        skillL = EUIUtils.format(SKILL_PNG_L, id);
        powerL = EUIUtils.format(POWER_PNG_L, id);
        summonL = EUIUtils.format(SUMMON_PNG_L, id);
        orbA = EUIUtils.format(ORB_A_PNG, id);
        orbB = EUIUtils.format(ORB_B_PNG, id);
        orbC = EUIUtils.format(ORB_C_PNG, id);
        cardEnergyOrb = new TextureCache(orbA);
        cardEnergyOrbL = new TextureCache(orbB);
        cardBackgroundAttack = new TextureCache(attack);
        cardBackgroundPower = new TextureCache(power);
        cardBackgroundSkill = new TextureCache(skill);
        cardBackgroundAttackL = new TextureCache(attackL);
        cardBackgroundPowerL = new TextureCache(powerL);
        cardBackgroundSkillL = new TextureCache(skillL);
        cardBackgroundSummon = new TextureCache(summon);
        cardBackgroundSummonL = new TextureCache(summonL);
    }

    protected void initializeCharacterImages(String id) {
        character = EUIUtils.format(CHARACTER_PNG, id);
        skeletonAtlas = EUIUtils.format(SKELETON_ATLAS, id);
        skeletonJson = EUIUtils.format(SKELETON_JSON, id);
        shoulder1 = EUIUtils.format(SHOULDER1_PNG, id);
        shoulder2 = EUIUtils.format(SHOULDER2_PNG, id);
        corpse = EUIUtils.format(CORPSE_PNG, id);
        charButton = EUIUtils.format(CHAR_BUTTON_PNG, id);
        charBackground = EUIUtils.format(CHAR_BACKGROUND, id);
    }

    protected void initializeOrbTextures(String id) {
        orbBaseLayer = new TextureCache(EUIUtils.format(ORB_BASE_LAYER, id));
        orbFlash = new TextureCache(EUIUtils.format(ORB_FLASH, id));
        orbTopLayer1 = new TextureCache(EUIUtils.format(ORB_TOP_LAYER1, id));
        orbTopLayer2 = new TextureCache(EUIUtils.format(ORB_TOP_LAYER2, id));
        orbTopLayer3 = new TextureCache(EUIUtils.format(ORB_TOP_LAYER3, id));
        orbTopLayer4 = new TextureCache(EUIUtils.format(ORB_TOP_LAYER4, id));
    }

    public static class AffinityIcons {
        public TextureCache red;
        public TextureCache green;
        public TextureCache blue;
        public TextureCache orange;
        public TextureCache light;
        public TextureCache dark;
        public TextureCache silver;

        public AffinityIcons(String id) {
            red = new TextureCache(EUIUtils.format(AFFINITY_RED, id), true);
            green = new TextureCache(EUIUtils.format(AFFINITY_GREEN, id), true);
            blue = new TextureCache(EUIUtils.format(AFFINITY_BLUE, id), true);
            orange = new TextureCache(EUIUtils.format(AFFINITY_ORANGE, id), true);
            light = new TextureCache(EUIUtils.format(AFFINITY_LIGHT, id), true);
            dark = new TextureCache(EUIUtils.format(AFFINITY_DARK, id), true);
            silver = new TextureCache(EUIUtils.format(AFFINITY_SILVER, id), true);
        }
    }
}
