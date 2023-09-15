package pinacolada.resources;

import com.badlogic.gdx.graphics.Texture;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.TextureCache;
import pinacolada.cards.base.fields.PCLAffinity;

public abstract class AbstractImages {
    protected static final String AFFINITY_FORMAT = "images/{0}/cardui/affinities/{1}.png";
    protected static final String ATTACK_PNG = "images/{0}/cardui/512/bg_attack_canvas.png";
    protected static final String ATTACK_PNG_L = "images/{0}/cardui/1024/bg_attack_canvas.png";
    protected static final String CHARACTER_PNG = "images/{0}/characters/idle/char.png";
    protected static final String CHAR_BACKGROUND = "images/{0}/ui/charselect/background.png";
    protected static final String CHAR_BUTTON_PNG = "images/{0}/ui/charselect/button.png";
    protected static final String CORPSE_PNG = "images/{0}/characters/corpse.png";
    protected static final String ORB_A_PNG = "images/{0}/cardui/512/energy_orb_default_a.png";
    protected static final String ORB_BASE_LAYER = "images/{0}/ui/energy/BaseLayer.png";
    protected static final String ORB_B_PNG = "images/{0}/cardui/512/energy_orb_default_b.png";
    protected static final String ORB_C_PNG = "images/{0}/cardui/512/energy_orb_default_c.png";
    protected static final String ORB_FLASH = "images/{0}/ui/energy/OrbFlash.png";
    protected static final String ORB_TOP_LAYER1 = "images/{0}/ui/energy/TopLayer1.png";
    protected static final String ORB_TOP_LAYER2 = "images/{0}/ui/energy/TopLayer2.png";
    protected static final String ORB_TOP_LAYER3 = "images/{0}/ui/energy/TopLayer3.png";
    protected static final String ORB_TOP_LAYER4 = "images/{0}/ui/energy/TopLayer4.png";
    protected static final String POWER_PNG = "images/{0}/cardui/512/bg_power_canvas.png";
    protected static final String POWER_PNG_L = "images/{0}/cardui/1024/bg_power_canvas.png";
    protected static final String SHOULDER1_PNG = "images/{0}/characters/shoulder.png";
    protected static final String SHOULDER2_PNG = "images/{0}/characters/shoulder2.png";
    protected static final String SKELETON_ATLAS = "images/{0}/characters/idle/char.atlas";
    protected static final String SKELETON_JSON = "images/{0}/characters/idle/char.json";
    protected static final String SKILL_PNG = "images/{0}/cardui/512/bg_skill_canvas.png";
    protected static final String SKILL_PNG_L = "images/{0}/cardui/1024/bg_skill_canvas.png";
    protected static final String SUMMON_PNG = "images/{0}/cardui/512/bg_summon_canvas.png";
    protected static final String SUMMON_PNG_L = "images/{0}/cardui/1024/bg_summon_canvas.png";

    public final String ID;
    public String attack;
    public String attackL;
    public String charBackground;
    public String charButton;
    public String character;
    public String corpse;
    public String orbA;
    public String orbB;
    public String orbC;
    public String power;
    public String powerL;
    public String shoulder1;
    public String shoulder2;
    public String skeletonAtlas;
    public String skeletonJson;
    public String skill;
    public String skillL;
    public String summon;
    public String summonL;
    public TextureCache cardBackgroundAttack;
    public TextureCache cardBackgroundAttackL;
    public TextureCache cardBackgroundPower;
    public TextureCache cardBackgroundPowerL;
    public TextureCache cardBackgroundSkill;
    public TextureCache cardBackgroundSkillL;
    public TextureCache cardBackgroundSummon;
    public TextureCache cardBackgroundSummonL;
    public TextureCache cardEnergyOrb;
    public TextureCache cardEnergyOrbL;
    public TextureCache orbBaseLayer;
    public TextureCache orbFlash;
    public TextureCache orbTopLayer1;
    public TextureCache orbTopLayer2;
    public TextureCache orbTopLayer3;
    public TextureCache orbTopLayer4;

    public AbstractImages(String ID) {
        this.ID = ID;
        initializeCardImages();
        initializeCharacterImages();
        initializeAffinityTextures();
        initializeOrbTextures();
    }

    protected String getAffinityPath(PCLAffinity af) {
        return EUIUtils.format(AFFINITY_FORMAT, ID, af.getAffinitySymbol());
    }

    public Texture getAffinityTexture(PCLAffinity af) {
        return EUIRM.getTexture(getAffinityPath(af));
    }

    public TextureCache[] getOrbTextures() {
        return new TextureCache[]{
                orbBaseLayer, orbTopLayer1, orbTopLayer2, orbTopLayer3, orbTopLayer4
        };
    }

    protected void initializeAffinityTextures() {
    }

    protected void initializeCardImages() {
        attack = EUIUtils.format(ATTACK_PNG, ID);
        skill = EUIUtils.format(SKILL_PNG, ID);
        power = EUIUtils.format(POWER_PNG, ID);
        summon = EUIUtils.format(SUMMON_PNG, ID);
        attackL = EUIUtils.format(ATTACK_PNG_L, ID);
        skillL = EUIUtils.format(SKILL_PNG_L, ID);
        powerL = EUIUtils.format(POWER_PNG_L, ID);
        summonL = EUIUtils.format(SUMMON_PNG_L, ID);
        orbA = EUIUtils.format(ORB_A_PNG, ID);
        orbB = EUIUtils.format(ORB_B_PNG, ID);
        orbC = EUIUtils.format(ORB_C_PNG, ID);
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

    protected void initializeCharacterImages() {
        character = EUIUtils.format(CHARACTER_PNG, ID);
        skeletonAtlas = EUIUtils.format(SKELETON_ATLAS, ID);
        skeletonJson = EUIUtils.format(SKELETON_JSON, ID);
        shoulder1 = EUIUtils.format(SHOULDER1_PNG, ID);
        shoulder2 = EUIUtils.format(SHOULDER2_PNG, ID);
        corpse = EUIUtils.format(CORPSE_PNG, ID);
        charButton = EUIUtils.format(CHAR_BUTTON_PNG, ID);
        charBackground = EUIUtils.format(CHAR_BACKGROUND, ID);
    }

    protected void initializeOrbTextures() {
        orbBaseLayer = new TextureCache(EUIUtils.format(ORB_BASE_LAYER, ID));
        orbFlash = new TextureCache(EUIUtils.format(ORB_FLASH, ID));
        orbTopLayer1 = new TextureCache(EUIUtils.format(ORB_TOP_LAYER1, ID));
        orbTopLayer2 = new TextureCache(EUIUtils.format(ORB_TOP_LAYER2, ID));
        orbTopLayer3 = new TextureCache(EUIUtils.format(ORB_TOP_LAYER3, ID));
        orbTopLayer4 = new TextureCache(EUIUtils.format(ORB_TOP_LAYER4, ID));
    }
}
