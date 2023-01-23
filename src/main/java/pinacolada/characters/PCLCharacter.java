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
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.daily.mods.BlueCards;
import com.megacrit.cardcrawl.daily.mods.GreenCards;
import com.megacrit.cardcrawl.daily.mods.PurpleCards;
import com.megacrit.cardcrawl.daily.mods.RedCards;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.screens.stats.CharStat;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.ui.TextureCache;
import org.apache.commons.lang3.StringUtils;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.SFX;
import pinacolada.effects.vfx.SmokeEffect;
import pinacolada.patches.library.RelicLibraryPatches;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.ui.PCLEnergyOrb;
import pinacolada.ui.characterSelection.PCLBaseStatEditor;
import pinacolada.utilities.BlendableSkeletonMeshRenderer;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.ArrayList;

public abstract class PCLCharacter extends CustomPlayer
{
    protected static final String DEFAULT_HIT = "hit";
    protected static final String DEFAULT_IDLE = "idle";

    public static BlendableSkeletonMeshRenderer sr = new BlendableSkeletonMeshRenderer();
    public String description;
    protected TextureCache charTexture;
    protected boolean actualFlip;
    protected String atlasUrl;
    protected String creatureID;
    protected String corpseImage;
    protected String skeletonUrl;
    protected String shoulderImage1;
    protected String shoulderImage2;
    private String hitAnim = null;
    private String idleAnim = null;

    static
    {
        sr.setPremultipliedAlpha(true);
    }

    protected PCLCharacter(String name, PlayerClass playerClass)
    {
        super(name, playerClass, new PCLEnergyOrb(), new G3DJAnimation(null, null));
    }

    public PCLCharacter(String name, PlayerClass playerClass, PCLEnergyOrb orb, String atlasUrl, String skeletonUrl, String shoulderImage1, String shoulderImage2, String corpseImage, String description)
    {
        super(name, playerClass, orb, new G3DJAnimation(null, null));
        this.atlasUrl = atlasUrl;
        this.skeletonUrl = skeletonUrl;
        this.shoulderImage1 = skeletonUrl;
        this.shoulderImage2 = skeletonUrl;
        this.corpseImage = skeletonUrl;
        this.description = description;


        initializeClass(null, shoulderImage2, shoulderImage1, corpseImage,
                getLoadout(), 0f, -5f, 240f, 244f, new EnergyManager(3));

        reloadAnimation(atlasUrl, skeletonUrl, 1f);
    }

    protected PCLLoadout prepareLoadout()
    {
        return PGR.getPlayerData(chosenClass).prepareLoadout();
    }

    @Override
    public ArrayList<AbstractCard> getCardPool(ArrayList<AbstractCard> arrayList)
    {
        arrayList = super.getCardPool(arrayList);

        if (ModHelper.isModEnabled(RedCards.ID))
        {
            CardLibrary.addRedCards(arrayList);
        }
        if (ModHelper.isModEnabled(GreenCards.ID))
        {
            CardLibrary.addGreenCards(arrayList);
        }
        if (ModHelper.isModEnabled(BlueCards.ID))
        {
            CardLibrary.addBlueCards(arrayList);
        }
        if (ModHelper.isModEnabled(PurpleCards.ID))
        {
            CardLibrary.addPurpleCards(arrayList);
        }

        return arrayList;
    }

    @Override
    public Texture getEnergyImage()
    {
        if (this.energyOrb instanceof PCLEnergyOrb)
        {
            return ((PCLEnergyOrb) this.energyOrb).getEnergyImage();
        }
        else
        {
            return super.getEnergyImage();
        }
    }

    @Override
    public CharStat getCharStat()
    {
        // yes
        return super.getCharStat();
    }

    @Override
    public Texture getCustomModeCharacterButtonImage()
    {
        if (charTexture == null)
        {
            charTexture = new TextureCache(BaseMod.getPlayerButton(this.chosenClass));
        }
        return charTexture.texture();
    }

    @Override
    public String getPortraitImageName()
    {
        return null; // Updated in AnimatorCharacterSelectScreen
    }

    @Override
    public ArrayList<String> getStartingDeck()
    {
        return prepareLoadout().getStartingDeck();
    }

    @Override
    public ArrayList<String> getStartingRelics()
    {
        return prepareLoadout().getStartingRelics();
    }

    @Override
    public CharSelectInfo getLoadout()
    {
        return prepareLoadout().getLoadout(name, description, this);
    }

    @Override
    public String getTitle(PlayerClass playerClass) // Top panel title
    {
        return name;
    }

    @Override
    public int getAscensionMaxHPLoss()
    {
        return PCLBaseStatEditor.StatType.HP.baseAmount / 10;
    }

    @Override
    public BitmapFont getEnergyNumFont()
    {
        return FontHelper.energyNumFontBlue;
    }

    @Override
    public void doCharSelectScreenSelectEffect()
    {
        CardCrawlGame.sound.playA(getCustomModeCharacterButtonSoundKey(), MathUtils.random(-0.1f, 0.2f));
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, false);
    }

    @Override
    public String getCustomModeCharacterButtonSoundKey()
    {
        return SFX.TINGSHA;
    }

    @Override
    public String getLocalizedCharacterName()
    {
        return name;
    }

    @Override
    public String getSpireHeartText()
    {
        return com.megacrit.cardcrawl.events.beyond.SpireHeart.DESCRIPTIONS[10];
    }

    @Override
    public Color getSlashAttackColor()
    {
        return Color.SKY;
    }

    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect()
    {
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
    public String getVampireText()
    {
        return com.megacrit.cardcrawl.events.city.Vampires.DESCRIPTIONS[5];
    }

    @Override
    public ArrayList<String> getRelicNames()
    {
        final ArrayList<String> list = new ArrayList<>();
        for (AbstractRelic r : relics)
        {
            RelicLibraryPatches.addRelic(list, r);
        }

        return list;
    }

    @Override
    public void damage(DamageInfo info)
    {
        if (atlas != null && hitAnim != null && info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output - this.currentBlock > 0)
        {
            try
            {
                AnimationState.TrackEntry e = this.state.setAnimation(0, hitAnim, false);
                this.state.addAnimation(0, idleAnim, true, 0f);
                e.setTimeScale(0.9f);
            }
            catch (Exception e)
            {
                EUIUtils.logError(this, "Failed to load damage animation with atlas " + atlasUrl + " and skeleton " + skeletonUrl);
            }
        }

        super.damage(info);
    }

    @Override
    public void renderPlayerImage(SpriteBatch sb) {
        if (creatureID == null)
        {
            super.renderPlayerImage(sb);
        }
        else
        {
            renderPlayerImageImpl(sb);
        }
    }

    protected void renderPlayerImageImpl(SpriteBatch sb)
    {
        boolean shouldFlip = this.flipHorizontal ^ this.actualFlip;
        switch(this.animation.type()) {
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
                } else {
                    sb.setColor(Color.WHITE);
                    sb.draw(this.img, this.drawX - (float)this.img.getWidth() * Settings.scale / 2.0F + this.animX, this.drawY, (float)this.img.getWidth() * Settings.scale, (float)this.img.getHeight() * Settings.scale, 0, 0, this.img.getWidth(), this.img.getHeight(), this.flipHorizontal, this.flipVertical);
                }
                break;
            case MODEL:
                BaseMod.publishAnimationRender(sb);
                break;
            case SPRITE:
                this.animation.setFlip(shouldFlip, this.flipVertical);
                PCLRenderHelpers.drawBlended(sb, EUIRenderHelpers.BlendingMode.Glowing, (s) -> {
                    PCLRenderHelpers.drawColorized(s, this.getTransparentColor(), s2 -> {
                        this.animation.renderSprite(s, this.drawX + this.animX, this.drawY + this.animY + AbstractDungeon.sceneOffsetY);
                    });
                });
        }
    }

    @Override
    public void playDeathAnimation() {
        PCLEffects.Manual.add(new SmokeEffect(hb.cX, hb.cY, getCardRenderColor()));
        super.playDeathAnimation();
    }

    public Color getTransparentColor()
    {
        Color c = getCardRenderColor();
        c.a = 0.7f;
        return c;
    }

    public void setCreature(AbstractCreature creature)
    {
        setCreature(CreatureAnimationInfo.getIdentifierString(creature));
    }

    public void setCreature(String id)
    {
        creatureID = id;
        CreatureAnimationInfo.tryLoadAnimations(id);
        CreatureAnimationInfo animation = CreatureAnimationInfo.getAnimationForID(creatureID);
        if (animation != null)
        {
            reloadAnimation(animation.atlas, animation.skeleton, animation.scale);
        }
        else
        {
            String imgUrl = CreatureAnimationInfo.getImageForID(creatureID);
            if (imgUrl != null)
            {
                this.img = ImageMaster.loadImage(imgUrl);
                this.atlas = null;
            }
            else
            {
                AbstractAnimation spriter = CreatureAnimationInfo.getSpriterForID(creatureID);
                if (spriter != null)
                {
                    this.animation = spriter;
                }
            }
        }

        actualFlip = !CreatureAnimationInfo.isPlayer(creatureID);
    }

    public void resetCreature()
    {
        creatureID = null;
        reloadAnimation(atlasUrl, skeletonUrl, 1f);
        actualFlip = false;
    }

    public void reloadAnimation(String atlasUrl, String skeletonUrl, float scale)
    {
        try
        {
            this.loadAnimationPCL(atlasUrl, skeletonUrl, scale);
            tryFindAnimations();
            AnimationState.TrackEntry e = this.state.setAnimation(0, idleAnim, true);
            if (hitAnim != null)
            {
                this.stateData.setMix(hitAnim, idleAnim, 0.1f);
            }
            e.setTimeScale(0.9f);
        }
        catch (Exception e)
        {
            EUIUtils.logError(this, "Failed to reload animation with atlas " + atlasUrl + " and skeleton " + skeletonUrl);
        }
    }

    // Intentionally avoid calling loadAnimation to avoid registering animations
    protected void loadAnimationPCL(String atlasUrl, String skeletonUrl, float scale)
    {
        this.atlas = new TextureAtlas(Gdx.files.internal(atlasUrl));
        SkeletonJson json = new SkeletonJson(this.atlas);
        if (CardCrawlGame.dungeon != null && AbstractDungeon.player != null) {
            if (AbstractDungeon.player.hasRelic("PreservedInsect") && !this.isPlayer && AbstractDungeon.getCurrRoom().eliteTrigger) {
                scale += 0.3F;
            }

            if (ModHelper.isModEnabled("MonsterHunter") && !this.isPlayer) {
                scale -= 0.3F;
            }
        }

        json.setScale(Settings.renderScale / scale);
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal(skeletonUrl));
        this.skeleton = new Skeleton(skeletonData);
        this.skeleton.setColor(Color.WHITE);
        this.stateData = new AnimationStateData(skeletonData);
        this.state = new AnimationState(this.stateData);
    }

    protected Animation getAnimation(int index)
    {
        if (this.stateData != null)
        {
            Array<Animation> animations = this.stateData.getSkeletonData().getAnimations();
            return index < animations.size ? animations.get(index) : null;
        }
        return null;
    }

    protected Animation getAnimation(String key)
    {
        return this.state.getData().getSkeletonData().findAnimation(key);
    }

    protected void tryFindAnimations()
    {
        Animation idle = getAnimation(DEFAULT_IDLE);
        if (idle == null)
        {
            idle = getAnimation(StringUtils.capitalize(DEFAULT_IDLE));
        }
        if (idle == null)
        {
            idle = getAnimation(0);
        }

        if (idle != null)
        {
            idleAnim = idle.getName();
        }
        else
        {
            idleAnim = null;
        }

        Animation hit = getAnimation(DEFAULT_HIT);
        if (idle == null)
        {
            idle = getAnimation(StringUtils.capitalize(DEFAULT_HIT));
        }
        if (hit == null)
        {
            hit = getAnimation(1);
        }

        if (hit != null)
        {
            hitAnim = hit.getName();
        }
        else
        {
            hitAnim = null;
        }
    }
}