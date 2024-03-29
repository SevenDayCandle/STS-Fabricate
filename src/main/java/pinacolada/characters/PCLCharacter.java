package pinacolada.characters;

import basemod.BaseMod;
import basemod.abstracts.CustomPlayer;
import basemod.animations.AbstractAnimation;
import basemod.animations.SpineAnimation;
import basemod.animations.SpriterAnimation;
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
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.screens.stats.CharStat;
import com.megacrit.cardcrawl.ui.panels.energyorb.EnergyOrbInterface;
import extendedui.EUIUtils;
import extendedui.ui.TextureCache;
import org.apache.commons.lang3.StringUtils;
import pinacolada.effects.PCLSFX;
import pinacolada.resources.PCLPlayerData;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.ui.PCLEnergyOrb;
import pinacolada.utilities.BlendableSkeletonMeshRenderer;

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
    protected AbstractAnimation originalAnimation;
    protected TextureCache charTexture;
    protected boolean actualFlip;
    protected String creatureID;
    public final PCLPlayerData<?,?,?> playerData;
    public String description;

    public PCLCharacter(PCLPlayerData<?,?,?> playerData, PCLCharacterSpineAnimation animation) {
        this(playerData.getCharacterStrings(), playerData, animation);
    }

    public PCLCharacter(PCLPlayerData<?,?,?> playerData, PCLCharacterSpriterAnimation animation) {
        this(playerData.getCharacterStrings(), playerData, animation);
    }

    public PCLCharacter(CharacterStrings charStrings, PCLPlayerData<?,?,?> playerData, PCLCharacterSpineAnimation animation) {
        this(charStrings.NAMES[0], playerData, playerData.resources.images.createEnergyOrb(), animation, charStrings.TEXT[0]);
    }

    public PCLCharacter(CharacterStrings charStrings, PCLPlayerData<?,?,?> playerData, PCLCharacterSpriterAnimation animation) {
        this(charStrings.NAMES[0], playerData, playerData.resources.images.createEnergyOrb(), animation, charStrings.TEXT[0]);
    }

    public PCLCharacter(String name, PCLPlayerData<?,?,?> playerData, EnergyOrbInterface orb, AbstractAnimation animation, String shoulderImage1, String shoulderImage2, String corpseImage, String description) {
        super(name, playerData.resources.playerClass, orb, animation);
        this.playerData = playerData;
        this.originalAnimation = animation;

        // SpriteAnimation is already registered via patch in super constructor
        if (!(this.animation instanceof SpriterAnimation)) {
            PCLCharacterAnimation.registerCreatureAnimation(this, this.animation);
            reloadDefaultAnimation();
        }
        initializeLoadout(name, description, shoulderImage1, shoulderImage2, corpseImage);
    }

    public PCLCharacter(String name, PCLPlayerData<?,?,?> playerData, EnergyOrbInterface orb, PCLCharacterSpineAnimation animation, String description) {
        super(name, playerData.resources.playerClass, orb, animation);
        this.playerData = playerData;
        this.originalAnimation = animation;
        initializeLoadout(name, description, animation.shoulderImage1, animation.shoulderImage2, animation.corpseImage);
        PCLCharacterAnimation.registerCreatureAnimation(this, this.animation);
        reloadDefaultAnimation();
    }

    public PCLCharacter(String name, PCLPlayerData<?,?,?> playerData, EnergyOrbInterface orb, PCLCharacterSpriterAnimation animation, String description) {
        super(name, playerData.resources.playerClass, orb, animation);
        this.playerData = playerData;
        this.originalAnimation = animation;
        initializeLoadout(name, description, animation.shoulderImage1, animation.shoulderImage2, animation.corpseImage);
        PCLCharacterAnimation.registerCreatureAnimation(this, this.animation);
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
                EUIUtils.logError(this, "Failed to load damage animation");
            }
        }

        super.damage(info);
    }

    @Override
    public void doCharSelectScreenSelectEffect() {
        CardCrawlGame.sound.playA(getCustomModeCharacterButtonSoundKey(), MathUtils.random(-0.1f, 0.2f));
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
    public int getAscensionMaxHPLoss() {
        return PCLPlayerData.DEFAULT_HP / 10;
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
    public String getCustomModeCharacterButtonSoundKey() {
        return PCLSFX.TINGSHA;
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
    public BitmapFont getEnergyNumFont() {
        return FontHelper.energyNumFontBlue;
    }

    @Override
    public CharSelectInfo getLoadout() {
        return prepareLoadout().getLoadout(name, description, this);
    }

    @Override
    public String getLocalizedCharacterName() {
        return name;
    }

    @Override
    public String getPortraitImageName() {
        return null;
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
    public String getSpireHeartText() {
        return com.megacrit.cardcrawl.events.beyond.SpireHeart.DESCRIPTIONS[10];
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
    public String getTitle(PlayerClass playerClass) // Top panel title
    {
        return name;
    }

    @Override
    public String getVampireText() {
        return com.megacrit.cardcrawl.events.city.Vampires.DESCRIPTIONS[5];
    }

    protected void initializeLoadout(String name, String description, String shoulderImage1, String shoulderImage2, String corpseImage) {
        PCLLoadout loadout = prepareLoadout();
        initializeClass(null, shoulderImage2, shoulderImage1, corpseImage,
                loadout.getLoadout(name, description, this), 0f, -5f, 240f, 244f, new EnergyManager(loadout.getEnergy()));
        this.description = description;
    }

    // Intentionally avoid calling loadAnimation to avoid registering animations
    protected void loadSpineAnimation(String atlasUrl, String skeletonUrl, float scale, String idleStr, String hitStr) {
        try {
            this.atlas = new TextureAtlas(Gdx.files.internal(atlasUrl));
            SkeletonJson json = new SkeletonJson(this.atlas);
            json.setScale(Settings.renderScale * scale);
            SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal(skeletonUrl));
            this.skeleton = new Skeleton(skeletonData);
            this.skeleton.setColor(Color.WHITE);
            this.stateData = new AnimationStateData(skeletonData);
            this.state = new AnimationState(this.stateData);
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

    @Override
    public final AbstractPlayer newInstance() {
        return playerData.createCharacter();
    }

    protected PCLLoadout prepareLoadout() {
        PCLPlayerData<?,?,?> pData = playerData != null ? playerData : PGR.getPlayerData(chosenClass); // May get called before we can set playerData
        return pData.prepareLoadout();
    }

    public void reloadAnimation(AbstractAnimation animation) {
        reloadAnimation(animation, DEFAULT_IDLE, DEFAULT_HIT);
    }

    public void reloadAnimation(AbstractAnimation animation, String idleStr, String hitStr) {
        if (this.img != null) {
            this.img.dispose();
            this.img = null;
        }
        if (this.atlas != null) {
            this.atlas.dispose();
            this.atlas = null;
        }

        this.animation = animation;
        if (animation instanceof SpineAnimation) {
            loadSpineAnimation(((SpineAnimation) animation).atlasUrl, ((SpineAnimation) animation).skeletonUrl, ((SpineAnimation) animation).scale, idleStr, hitStr);
        }
        else if (animation instanceof PCLCharacterAnimation) {
            loadSpineAnimation(((PCLCharacterAnimation) animation).atlasUrl, ((PCLCharacterAnimation) animation).skeletonUrl, ((PCLCharacterAnimation) animation).scale, idleStr, hitStr);
        }
        // Because apparently the player image logic doesn't get hit if the atlas is null, even if it is never actually used
        else {
            this.atlas = new TextureAtlas();
        }
    }

    public void reloadDefaultAnimation() {
        reloadAnimation(originalAnimation);
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

    protected void renderPlayerImageImpl(SpriteBatch sb) {
        boolean shouldFlip = this.flipHorizontal ^ this.actualFlip;
        switch (this.animation.type()) {
            case NONE:
                if (this.atlas != null) {
                    this.state.update(Gdx.graphics.getDeltaTime());
                    this.state.apply(this.skeleton);
                    this.skeleton.updateWorldTransform();
                    this.skeleton.setPosition(this.drawX + this.animX, this.drawY + this.animY);
                    this.skeleton.setFlip(shouldFlip, this.flipVertical);
                    sb.end();
                    CardCrawlGame.psb.begin();
                    renderPlayerSkeleton();
                    CardCrawlGame.psb.end();
                    sb.begin();
                }
                else if (this.img != null) {
                    renderPlayerSingle(sb);
                }
                break;
            case MODEL:
                BaseMod.publishAnimationRender(sb);
                break;
            case SPRITE:
                this.animation.setFlip(shouldFlip, this.flipVertical);
                renderPlayerSprite(sb);
        }
    }

    protected void renderPlayerSingle(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        sb.draw(this.img, this.drawX - (float) this.img.getWidth() * Settings.scale / 2.0F + this.animX, this.drawY, (float) this.img.getWidth() * Settings.scale, (float) this.img.getHeight() * Settings.scale, 0, 0, this.img.getWidth(), this.img.getHeight(), this.flipHorizontal, this.flipVertical);
    }

    protected void renderPlayerSkeleton() {
        sr.draw(CardCrawlGame.psb, this.skeleton);
    }

    protected void renderPlayerSprite(SpriteBatch sb) {
        this.animation.renderSprite(sb, this.drawX + this.animX, this.drawY + this.animY + AbstractDungeon.sceneOffsetY);
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
        setCreature(PCLCharacterAnimation.getIdentifierString(creature), DEFAULT_IDLE, DEFAULT_HIT);
    }

    public void setCreature(String id, String idleStr, String hitStr) {
        creatureID = id;
        PCLCharacterAnimation.tryLoadAnimations(id);
        AbstractAnimation animation = PCLCharacterAnimation.getAnimationForID(creatureID);
        if (animation != null) {
            reloadAnimation(animation, idleStr, hitStr);
        }
        else {
            String imgUrl = PCLCharacterAnimation.getImageForID(creatureID);
            if (imgUrl != null) {
                if (this.img != null) {
                    this.img.dispose();
                }
                this.img = ImageMaster.loadImage(imgUrl); // Do not cache monster images
                if (this.atlas != null) {
                    this.atlas.dispose();
                }
                this.atlas = null;
            }
            else {
                EUIUtils.logError(this, "Both animation and image was missing for id " + id);
                if (this.img != null) {
                    this.img.dispose();
                }
                this.img = null;
                resetCreature();
            }
        }

        actualFlip = !PCLCharacterAnimation.isPlayer(creatureID);
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