package pinacolada.resources;

import com.badlogic.gdx.graphics.Texture;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.TextureCache;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.characters.PCLCharacterSpineAnimation;
import pinacolada.characters.PCLCharacterSpriterAnimation;
import pinacolada.ui.PCLEnergyOrb;

public abstract class AbstractImages {
    protected static final String AFFINITY_FORMAT = "images/{0}/cardui/affinities/{1}.png";
    protected static final String ATTACK_PNG = "images/{0}/cardui/512/bg_attack_canvas.png";
    protected static final String ATTACK_PNG_L = "images/{0}/cardui/1024/bg_attack_canvas.png";
    protected static final String DEFAULT_CHARACTER_PNG = "images/{0}/characters/char.png";
    protected static final String DEFAULT_CHAR_BACKGROUND = "images/{0}/ui/charselect/background.png";
    protected static final String DEFAULT_CHAR_BUTTON_PNG = "images/{0}/ui/charselect/button.png";
    protected static final String DEFAULT_CORPSE_PNG = "images/{0}/characters/corpse.png";
    protected static final String DEFAULT_ORB_BASE_LAYER = "images/{0}/ui/energy/baseLayer.png";
    protected static final String DEFAULT_ORB_BORDER = "images/{0}/ui/energy/border.png";
    protected static final String DEFAULT_ORB_FLASH = "images/{0}/ui/energy/flash.png";
    protected static final String DEFAULT_ORB_TOP_LAYER1 = "images/{0}/ui/energy/topLayer1.png";
    protected static final String DEFAULT_ORB_TOP_LAYER2 = "images/{0}/ui/energy/topLayer2.png";
    protected static final String DEFAULT_ORB_TOP_LAYER3 = "images/{0}/ui/energy/topLayer3.png";
    protected static final String DEFAULT_ORB_TOP_LAYER4 = "images/{0}/ui/energy/topLayer4.png";
    protected static final String DEFAULT_SHOULDER1_PNG = "images/{0}/characters/shoulder.png";
    protected static final String DEFAULT_SHOULDER2_PNG = "images/{0}/characters/shoulder2.png";
    protected static final String DEFAULT_SKELETON_ATLAS = "images/{0}/characters/char.atlas";
    protected static final String DEFAULT_SKELETON_JSON = "images/{0}/characters/char.json";
    protected static final String DEFAULT_SPRITER_SCML = "images/{0}/characters/anim.scml";
    protected static final String ORB_A_PNG = "images/{0}/cardui/512/energy_orb_default_a.png";
    protected static final String ORB_B_PNG = "images/{0}/cardui/512/energy_orb_default_b.png";
    protected static final String ORB_C_PNG = "images/{0}/cardui/512/energy_orb_default_c.png";
    protected static final String POWER_PNG = "images/{0}/cardui/512/bg_power_canvas.png";
    protected static final String POWER_PNG_L = "images/{0}/cardui/1024/bg_power_canvas.png";
    protected static final String SKILL_PNG = "images/{0}/cardui/512/bg_skill_canvas.png";
    protected static final String SKILL_PNG_L = "images/{0}/cardui/1024/bg_skill_canvas.png";
    protected static final String SUMMON_PNG = "images/{0}/cardui/512/bg_summon_canvas.png";
    protected static final String SUMMON_PNG_L = "images/{0}/cardui/1024/bg_summon_canvas.png";

    public final String ID;
    public String attack;
    public String attackL;
    public String orbA;
    public String orbB;
    public String orbC;
    public String power;
    public String powerL;
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

    public AbstractImages(String ID) {
        this.ID = ID;
        initializeCardImages();
        initializeAffinityTextures();
    }

    public PCLEnergyOrb createEnergyOrb() {
        return createEnergyOrb(
                DEFAULT_ORB_BASE_LAYER,
                DEFAULT_ORB_TOP_LAYER1,
                DEFAULT_ORB_TOP_LAYER2,
                DEFAULT_ORB_TOP_LAYER3,
                DEFAULT_ORB_TOP_LAYER4,
                DEFAULT_ORB_FLASH,
                DEFAULT_ORB_BORDER
        );
    }

    public PCLEnergyOrb createEnergyOrb(String baseLayer, String topLayer1, String topLayer2, String topLayer3, String topLayer4, String flash, String border) {
        return new PCLEnergyOrb(
                new TextureCache[]{
                        new TextureCache(EUIUtils.format(baseLayer, ID)),
                        new TextureCache(EUIUtils.format(topLayer1, ID)),
                        new TextureCache(EUIUtils.format(topLayer2, ID)),
                        new TextureCache(EUIUtils.format(topLayer3, ID)),
                        new TextureCache(EUIUtils.format(topLayer4, ID))
                },
                new TextureCache(EUIUtils.format(flash, ID)),
                new TextureCache(EUIUtils.format(border, ID))
        );
    }

    public PCLCharacterSpineAnimation createSpineAnimation() {
        return createSpineAnimation(1f);
    }

    public PCLCharacterSpineAnimation createSpineAnimation(float scale) {
        return createSpineAnimation(
                DEFAULT_SKELETON_ATLAS,
                DEFAULT_SKELETON_JSON,
                DEFAULT_SHOULDER1_PNG,
                DEFAULT_SHOULDER2_PNG,
                DEFAULT_CORPSE_PNG,
                scale
        );
    }

    public PCLCharacterSpineAnimation createSpineAnimation(String atlas, String skeleton, float scale) {
        return createSpineAnimation(
                atlas,
                skeleton,
                DEFAULT_SHOULDER1_PNG,
                DEFAULT_SHOULDER2_PNG,
                DEFAULT_CORPSE_PNG,
                scale
        );
    }


    public PCLCharacterSpineAnimation createSpineAnimation(String atlas, String skeleton, String shoulder1, String shoulder2, String corpse, float scale) {
        return new PCLCharacterSpineAnimation(
                EUIUtils.format(atlas, ID),
                EUIUtils.format(skeleton, ID),
                EUIUtils.format(shoulder1, ID),
                EUIUtils.format(shoulder2, ID),
                EUIUtils.format(corpse, ID),
                scale
        );
    }

    public PCLCharacterSpriterAnimation createSpriterAnimation() {
        return createSpriterAnimation(DEFAULT_SPRITER_SCML, DEFAULT_SHOULDER1_PNG, DEFAULT_SHOULDER2_PNG, DEFAULT_CORPSE_PNG);
    }

    public PCLCharacterSpriterAnimation createSpriterAnimation(String spriter) {
        return createSpriterAnimation(spriter, DEFAULT_SHOULDER1_PNG, DEFAULT_SHOULDER2_PNG, DEFAULT_CORPSE_PNG);
    }

    public PCLCharacterSpriterAnimation createSpriterAnimation(String spriter, String shoulder1, String shoulder2, String corpse) {
        return new PCLCharacterSpriterAnimation(
                EUIUtils.format(spriter, ID),
                EUIUtils.format(shoulder1, ID),
                EUIUtils.format(shoulder2, ID),
                EUIUtils.format(corpse, ID)
        );
    }

    protected String getAffinityPath(PCLAffinity af) {
        return EUIUtils.format(AFFINITY_FORMAT, ID, af.getAffinitySymbol());
    }

    public Texture getAffinityTexture(PCLAffinity af) {
        return EUIRM.getTexture(getAffinityPath(af));
    }

    public String getCharacterPath() {
        return EUIUtils.format(DEFAULT_CHARACTER_PNG, ID);
    }

    public String getCharBackgroundPath() {
        return EUIUtils.format(DEFAULT_CHAR_BACKGROUND, ID);
    }

    public String getCharButtonPath() {
        return EUIUtils.format(DEFAULT_CHAR_BUTTON_PNG, ID);
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
}
