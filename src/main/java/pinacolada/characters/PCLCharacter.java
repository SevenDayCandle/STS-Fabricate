package pinacolada.characters;

import basemod.BaseMod;
import basemod.abstracts.CustomPlayer;
import basemod.animations.AbstractAnimation;
import basemod.animations.G3DJAnimation;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.screens.stats.CharStat;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.ui.TextureCache;
import org.apache.commons.lang3.StringUtils;
import pinacolada.effects.PCLSFX;
import pinacolada.resources.PCLAbstractPlayerData;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.ui.PCLEnergyOrb;
import pinacolada.utilities.BlendableSkeletonMeshRenderer;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.ArrayList;

public abstract class PCLCharacter extends CustomPlayer {
    protected static final String DEFAULT_HIT = "hit";
    protected static final String DEFAULT_IDLE = "idle";

    public static BlendableSkeletonMeshRenderer sr = new BlendableSkeletonMeshRenderer();

    static {
        sr.setPremultipliedAlpha(true);
    }

    private String hitAnim = null;
    private String idleAnim = null;
    protected TextureCache charTexture;
    protected boolean actualFlip;
    protected String atlasUrl;
    protected String creatureID;
    protected String corpseImage;
    protected String skeletonUrl;
    protected String shoulderImage1;
    protected String shoulderImage2;
    public String description;

    protected PCLCharacter(String name, PlayerClass playerClass) {
        super(name, playerClass, new PCLEnergyOrb(), new G3DJAnimation(null, null));
    }

    public PCLCharacter(String name, PlayerClass playerClass, PCLEnergyOrb orb, String atlasUrl, String skeletonUrl, String shoulderImage1, String shoulderImage2, String corpseImage, String description) {
        super(name, playerClass, orb, new G3DJAnimation(null, null));
        this.atlasUrl = atlasUrl;
        this.skeletonUrl = skeletonUrl;
        this.shoulderImage1 = skeletonUrl;
        this.shoulderImage2 = skeletonUrl;
        this.corpseImage = skeletonUrl;
        this.description = description;

        PCLLoadout loadout = prepareLoadout();
        initializeClass(null, shoulderImage2, shoulderImage1, corpseImage,
                prepareLoadout().getLoadout(name, description, this), 0f, -5f, 240f, 244f, new EnergyManager(loadout.getEnergy()));

        reloadDefaultAnimation();
    }

    protected Animation getAnimation(String key) {
        return this.state.getData().getSkeletonData().findAnimation(key);
    }

    protected Animation getAnimation(int index) {
        if (this.stateData != null) {
            Array<Animation> animations = this.stateData.getSkeletonData().getAnimations();
            return index < animations.size ? animations.get(index) : null;
        }
        return null;
    }

    @Override
    public ArrayList<String> getStartingDeck() {
        return prepareLoadout().getStartingDeck();
    }

    @Override
    public ArrayList<String> getStartingRelics() {
        return prepareLoadout().getStartingRelics();
    }

    @Override
    public CharSelectInfo getLoadout() {
        return prepareLoadout().getLoadout(name, description, this);
    }

    @Override
    public String getTitle(PlayerClass playerClass) // Top panel title
    {
        return name;
    }

    @Override
    public int getAscensionMaxHPLoss() {
        return PCLAbstractPlayerData.DEFAULT_HP / 10;
    }

    @Override
    public BitmapFont getEnergyNumFont() {
        return FontHelper.energyNumFontBlue;
    }

    @Override
    public void doCharSelectScreenSelectEffect() {
        CardCrawlGame.sound.playA(getCustomModeCharacterButtonSoundKey(), MathUtils.random(-0.1f, 0.2f));
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, false);
    }

    @Override
    public String getCustomModeCharacterButtonSoundKey() {
        return PCLSFX.TINGSHA;
    }

    @Override
    public String getLocalizedCharacterName() {
        return name;
    }

    @Override
    public String getSpireHeartText() {
        return com.megacrit.cardcrawl.events.beyond.SpireHeart.DESCRIPTIONS[10];
    }

    @Override
    public Color getSlashAttackColor() {
        return Color.SKY;
    }

    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[]
                {
                        AbstractGameAction.AttackEffect.SLASH_HEAVY,
                        AbstractGameAction.AttackEffect.FIRE,
                        AbstractGameAction.AttackEffect.SLASH_DIAGONAL,
                        AbstractGameAction.AttackEffect.SLASH_HEAVY,
                        AbstractGameAction.AttackEffect.FIRE,
                        AbstractGameAction.AttackEffect.SLASH_DIAGONAL
                };
    }

    @Override
    public String getVampireText() {
        return com.megacrit.cardcrawl.events.city.Vampires.DESCRIPTIONS[5];
    }

    @Override
    public void damage(DamageInfo info) {
        if (atlas != null && hitAnim != null && info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output - this.currentBlock > 0) {
            try {
                AnimationState.TrackEntry e = this.state.setAnimation(0, hitAnim, false);
                this.state.addAnimation(0, idleAnim, true, 0f);
                e.setTimeScale(0.9f);
            }
            catch (Exception e) {
                EUIUtils.logError(this, "Failed to load damage animation with atlas " + atlasUrl + " and skeleton " + skeletonUrl);
            }
        }

        super.damage(info);
    }

    public Color getTransparentColor() {
        Color c = getCardRenderColor();
        c.a = 0.7f;
        return c;
    }

    // Intentionally avoid calling loadAnimation to avoid registering animations
    protected void loadAnimationPCL(String atlasUrl, String skeletonUrl, float scale) {
        this.atlas = new TextureAtlas(Gdx.files.internal(atlasUrl));
        SkeletonJson json = new SkeletonJson(this.atlas);
        json.setScale(Settings.renderScale * scale);
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal(skeletonUrl));
        this.skeleton = new Skeleton(skeletonData);
        this.skeleton.setColor(Color.WHITE);
        this.stateData = new AnimationStateData(skeletonData);
        this.state = new AnimationState(this.stateData);
    }

    protected PCLLoadout prepareLoadout() {
        return PGR.getPlayerData(chosenClass).prepareLoadout();
    }

    public void reloadAnimation(float scale) {
        reloadAnimation(atlasUrl, skeletonUrl, DEFAULT_IDLE, DEFAULT_HIT, scale);
    }

    public void reloadAnimation(String atlasUrl, String skeletonUrl, String idleStr, String hitStr, float scale) {
        try {
            this.loadAnimationPCL(atlasUrl, skeletonUrl, scale);
            tryFindAnimations(idleStr, hitStr);
            AnimationState.TrackEntry e = this.state.setAnimation(0, idleAnim, true);
            if (hitAnim != null) {
                this.stateData.setMix(hitAnim, idleAnim, 0.1f);
            }
            e.setTimeScale(0.9f);
        }
        catch (Exception e) {
            EUIUtils.logError(this, "Failed to reload animation with atlas " + atlasUrl + " and skeleton " + skeletonUrl);
        }
    }

    public void reloadDefaultAnimation() {
        reloadAnimation(1f);
    }

    @Override
    public void renderPlayerImage(SpriteBatch sb) {
        if (creatureID == null) {
            super.renderPlayerImage(sb);
        }
        else {
            renderPlayerImageImpl(sb);
        }
    }

    @Override
    public Texture getEnergyImage() {
        if (this.energyOrb instanceof PCLEnergyOrb) {
            return ((PCLEnergyOrb) this.energyOrb).getEnergyImage();
        }
        else {
            return super.getEnergyImage();
        }
    }

    @Override
    public CharStat getCharStat() {
        // yes
        return super.getCharStat();
    }

    @Override
    public Texture getCustomModeCharacterButtonImage() {
        if (charTexture == null) {
            charTexture = new TextureCache(BaseMod.getPlayerButton(this.chosenClass));
        }
        return charTexture.texture();
    }

    @Override
    public String getPortraitImageName() {
        return null;
    }

    protected void renderPlayerImageImpl(SpriteBatch sb) {
        boolean shouldFlip = this.flipHorizontal ^ this.actualFlip;
        switch (this.animation.type()) {
            case NONE:
                if (this.atlas != null) {
                    this.state.update(Gdx.graphics.getDeltaTime());
                    this.state.apply(this.skeleton);
                    this.skeleton.updateWorldTransform();
                    this.skeleton.setPosition(this.drawX + this.animX, this.drawY + this.animY);
                    this.skeleton.setColor(this.getTransparentColor());
                    this.skeleton.setFlip(shouldFlip, this.flipVertical);
                    sb.end();
                    CardCrawlGame.psb.begin();
                    PCLRenderHelpers.drawWithShader(CardCrawlGame.psb, EUIRenderHelpers.getColorizeShader(), s -> sr.draw(CardCrawlGame.psb, this.skeleton, EUIRenderHelpers.BlendingMode.Glowing.srcFunc, EUIRenderHelpers.BlendingMode.Glowing.dstFunc));
                    CardCrawlGame.psb.end();
                    sb.begin();
                }
                else {
                    sb.setColor(Color.WHITE);
                    sb.draw(this.img, this.drawX - (float) this.img.getWidth() * Settings.scale / 2.0F + this.animX, this.drawY, (float) this.img.getWidth() * Settings.scale, (float) this.img.getHeight() * Settings.scale, 0, 0, this.img.getWidth(), this.img.getHeight(), this.flipHorizontal, this.flipVertical);
                }
                break;
            case MODEL:
                BaseMod.publishAnimationRender(sb);
                break;
            case SPRITE:
                this.animation.setFlip(shouldFlip, this.flipVertical);
                PCLRenderHelpers.drawBlended(sb, EUIRenderHelpers.BlendingMode.Glowing, (s) ->
                        PCLRenderHelpers.drawColorized(s, this.getTransparentColor(),
                                s2 -> this.animation.renderSprite(s, this.drawX + this.animX, this.drawY + this.animY + AbstractDungeon.sceneOffsetY)));
        }
    }

    public void resetCreature() {
        creatureID = null;
        reloadDefaultAnimation();
        actualFlip = false;
    }

    public void setCreature(String id) {
        setCreature(id, DEFAULT_IDLE, DEFAULT_HIT);
    }

    public void setCreature(AbstractCreature creature) {
        setCreature(CreatureAnimationInfo.getIdentifierString(creature), DEFAULT_IDLE, DEFAULT_HIT);
    }

    public void setCreature(String id, String idleStr, String hitStr) {
        creatureID = id;
        CreatureAnimationInfo.tryLoadAnimations(id);
        CreatureAnimationInfo animation = CreatureAnimationInfo.getAnimationForID(creatureID);
        if (animation != null) {
            reloadAnimation(animation.atlas, animation.skeleton, idleStr, hitStr, animation.scale);
        }
        else {
            String imgUrl = CreatureAnimationInfo.getImageForID(creatureID);
            if (imgUrl != null) {
                this.img = ImageMaster.loadImage(imgUrl);
                this.atlas = null;
            }
            else {
                AbstractAnimation spriter = CreatureAnimationInfo.getSpriterForID(creatureID);
                if (spriter != null) {
                    this.animation = spriter;
                }
            }
        }

        actualFlip = !CreatureAnimationInfo.isPlayer(creatureID);
    }

    protected void tryFindAnimations(String idleStr, String hitStr) {
        Animation idle = getAnimation(idleStr);
        if (idle == null) {
            idle = getAnimation(StringUtils.capitalize(idleStr));
        }
        if (idle == null) {
            idle = getAnimation(0);
        }

        if (idle != null) {
            idleAnim = idle.getName();
        }
        else {
            idleAnim = null;
        }

        Animation hit = getAnimation(hitStr);
        if (idle == null) {
            idle = getAnimation(StringUtils.capitalize(hitStr));
        }
        if (hit == null) {
            hit = getAnimation(1);
        }

        if (hit != null) {
            hitAnim = hit.getName();
        }
        else {
            hitAnim = null;
        }
    }
}