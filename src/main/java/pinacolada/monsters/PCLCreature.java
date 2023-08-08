package pinacolada.monsters;

import basemod.ReflectionHacks;
import basemod.abstracts.CustomMonster;
import basemod.animations.AbstractAnimation;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.*;
import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.BobEffect;
import extendedui.EUIGameUtils;
import extendedui.interfaces.markers.IntentProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.utilities.EUIColors;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PCLCreature extends CustomMonster implements IntentProvider {
    private static final Map<String, PCLCreatureData> staticData = new HashMap<>();
    public static final Color TAKEN_TURN_COLOR = new Color(0.64f, 0.64f, 0.64f, 1f);
    public static final Color TAKEN_TURN_NUMBER_COLOR = EUIColors.lerpNew(Color.GRAY, Settings.CREAM_COLOR, 0.75f);
    public final PCLCreatureData creatureData;
    public PCLAffinity affinity = PCLAffinity.General;
    public boolean stunned;
    public boolean hasTakenTurn;

    public PCLCreature(PCLCreatureData data) {
        super(data.strings.NAME, data.ID, data.hp, data.hbX, data.hbY, data.hbW, data.hbH, data.imgUrl);
        this.creatureData = data;
    }

    public PCLCreature(PCLCreatureData data, float offsetX, float offsetY) {
        super(data.strings.NAME, data.ID, data.hp, data.hbX, data.hbY, data.hbW, data.hbH, data.imgUrl, offsetX, offsetY);
        this.creatureData = data;
        setupHitbox(offsetX, offsetY);
    }

    public PCLCreature(PCLCreatureData data, float offsetX, float offsetY, boolean ignoreBlights) {
        super(data.strings.NAME, data.ID, data.hp, data.hbX, data.hbY, data.hbW, data.hbH, data.imgUrl, offsetX, offsetY, ignoreBlights);
        this.creatureData = data;
        setupHitbox(offsetX, offsetY);
    }

    public static PCLCreatureData getStaticData(String id) {
        return staticData.get(id);
    }

    protected static PCLCreatureData register(Class<? extends PCLCreature> type) {
        return PCLCreature.register(type, PGR.core);
    }

    protected static PCLCreatureData register(Class<? extends PCLCreature> type, PCLResources<?, ?, ?, ?> resources) {
        return registerData(new PCLCreatureData(type, resources));
    }

    protected static PCLCreatureData registerData(PCLCreatureData creatureData) {
        staticData.put(creatureData.ID, creatureData);
        return creatureData;
    }

    public void atEndOfRound() {
        hasTakenTurn = false;
    }

    public void doActionAnimation(boolean setTakenTurn) {
        useFastAttackAnimation();
        if (setTakenTurn) {
            hasTakenTurn = true;
        }
    }

    public BobEffect getBobEffect() {
        return GameUtilities.getBobEffect(this);
    }

    public int getEffectiveHPForTurn() {
        return currentHealth + currentBlock + GameUtilities.getEndOfTurnBlock(this) + TempHPField.tempHp.get(this);
    }

    public Color getIntentColor() {
        return ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentColor");
    }

    @Override
    public EUIKeywordTooltip getIntentTip() {
        return null;
    }

    @Override
    public List<EUIKeywordTooltip> getTips() {
        return new ArrayList<>();
    }

    @Override
    public void loadAnimation(String atlasUrl, String skeletonUrl, float scale) {
        super.loadAnimation(atlasUrl, skeletonUrl, scale);
    }

    @Override
    public void addPower(AbstractPower powerToApply) {
        super.addPower(powerToApply);
        if (powerToApply instanceof StunMonsterPower) {
            stunned = true;
        }
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

    public void render(SpriteBatch sb) {
        if (!this.isDead && !this.escaped) {
            this.renderAnimation(sb);

            if (this == AbstractDungeon.getCurrRoom().monsters.hoveredMonster && this.atlas == null && this.animation == null) {
                sb.setBlendFunction(770, 1);
                sb.setColor(new Color(1.0F, 1.0F, 1.0F, 0.1F));
                sb.draw(this.img, this.drawX - (float) this.img.getWidth() * Settings.scale / 2.0F + this.animX, this.drawY + this.animY + AbstractDungeon.sceneOffsetY, (float) this.img.getWidth() * Settings.scale, (float) this.img.getHeight() * Settings.scale, 0, 0, this.img.getWidth(), this.img.getHeight(), this.flipHorizontal, this.flipVertical);
                sb.setBlendFunction(770, 771);
            }

            if (shouldShowIntents()) {
                this.renderIntentVfxBehind(sb);
                this.renderIntent(sb);
                this.renderIntentVfxAfter(sb);
                this.renderDamageRange(sb);
            }

            this.hb.render(sb);
            this.intentHb.render(sb);
            this.healthHb.render(sb);
        }

        if (!AbstractDungeon.player.isDead) {
            this.renderHealth(sb);
            this.renderName(sb);
        }
    }

    public void renderAnimation(SpriteBatch sb) {
        renderAnimation(sb, tint.color);
    }

    public void renderAnimation(SpriteBatch sb, Color color) {
        if (this.animation != null && this.animation.type() == AbstractAnimation.Type.SPRITE) {
            this.animation.renderSprite(sb, this.drawX + this.animX, this.drawY + this.animY + AbstractDungeon.sceneOffsetY);
        }
        else if (this.atlas == null) {
            sb.setColor(color);
            sb.draw(this.img, this.drawX - (float) this.img.getWidth() * Settings.scale / 2.0F + this.animX, this.drawY + this.animY + AbstractDungeon.sceneOffsetY, (float) this.img.getWidth() * Settings.scale, (float) this.img.getHeight() * Settings.scale, 0, 0, this.img.getWidth(), this.img.getHeight(), this.flipHorizontal, this.flipVertical);
        }
        else {
            this.state.update(Gdx.graphics.getDeltaTime());
            this.state.apply(this.skeleton);
            this.skeleton.updateWorldTransform();
            this.skeleton.setPosition(this.drawX + this.animX, this.drawY + this.animY + AbstractDungeon.sceneOffsetY);
            this.skeleton.setColor(color);
            this.skeleton.setFlip(this.flipHorizontal, this.flipVertical);
            sb.end();
            CardCrawlGame.psb.begin();
            AbstractMonster.sr.draw(CardCrawlGame.psb, this.skeleton);
            CardCrawlGame.psb.end();
            sb.begin();
            sb.setBlendFunction(770, 771);
        }
    }

    public void setAnimation(AbstractAnimation animation) {
        this.animation = animation;
    }

    // Offset positions should be given with Settings.scaling already applied
    protected void setupHitbox(float offsetX, float offsetY) {
        this.drawX = offsetX;
        this.drawY = offsetY;
        updateHitbox(creatureData.hbX, creatureData.hbY, creatureData.hbW, creatureData.hbH);
        refreshHitboxLocation();
        refreshIntentHbLocation();
    }

    protected boolean shouldShowIntents() {
        return !this.isDying && !this.isEscaping && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.player.isDead && EUIGameUtils.canViewEnemyIntents(this) && !Settings.hideCombatElements;
    }

    public void takeTurn(boolean manual) {
        if (stunned) {
            stunned = false;
        }
        else {
            performActions(manual);
        }
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void takeTurn() {
        takeTurn(false);
    }

    public abstract void performActions(boolean manual);
}
