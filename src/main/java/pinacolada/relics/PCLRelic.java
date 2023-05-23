package pinacolada.relics;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import com.megacrit.cardcrawl.vfx.FloatyEffect;
import com.megacrit.cardcrawl.vfx.GlowRelicParticle;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.PCLActions;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.List;

public abstract class PCLRelic extends AbstractRelic implements KeywordProvider {
    public static AbstractPlayer player;
    public static Random rng;
    public final PCLRelicData relicData;
    public ArrayList<EUIKeywordTooltip> tips;
    public EUIKeywordTooltip mainTooltip;

    public PCLRelic(PCLRelicData data) {
        super(data.ID, "", data.tier, data.sfx);
        this.relicData = data;
        initializePCLTips();
        setupImages(data.imagePath);
    }

    public static String createFullID(Class<? extends PCLRelic> type) {
        return createFullID(PGR.core, type);
    }

    public static String createFullID(PCLResources<?, ?, ?, ?> resources, Class<? extends PCLRelic> type) {
        return resources.createID(type.getSimpleName());
    }

    protected static PCLRelicData register(Class<? extends PCLRelic> type) {
        return register(type, PGR.core);
    }

    protected static PCLRelicData register(Class<? extends PCLRelic> type, PCLResources<?, ?, ?, ?> resources) {
        return registerRelicData(new PCLRelicData(type, resources));
    }

    protected static <T extends PCLRelicData> T registerRelicData(T cardData) {
        return PCLRelicData.registerData(cardData);
    }

    protected void activateBattleEffect() {

    }

    public int addCounter(int amount) {
        setCounter(counter + amount);

        return counter;
    }

    public float atBlockModify(PCLUseInfo info, float block, AbstractCard c) {
        return atBlockModify(block, c);
    }

    public float atBlockModify(float block, AbstractCard c) {
        return block;
    }

    public float atDamageModify(PCLUseInfo info, float block, AbstractCard c) {
        return atDamageModify(block, c);
    }

    public float atHealModify(PCLUseInfo info, float block, AbstractCard c) {
        return block;
    }

    public float atHitCountModify(PCLUseInfo info, float block, AbstractCard c) {
        return block;
    }

    public float atMagicNumberModify(PCLUseInfo info, float block, AbstractCard c) {
        return block;
    }

    public float atRightCountModify(PCLUseInfo info, float block, AbstractCard c) {
        return block;
    }

    protected void deactivateBattleEffect() {

    }

    protected void displayAboveCreature(AbstractCreature creature) {
        PCLActions.top.add(new RelicAboveCreatureAction(creature, this));
    }

    protected String formatDescription(int index, Object... args) {
        return EUIUtils.format(getDescriptions()[index], args);
    }

    protected String getCounterString() {
        return String.valueOf(counter);
    }

    public String[] getDescriptions() {
        return DESCRIPTIONS;
    }

    public TextureAtlas.AtlasRegion getPowerIcon() {
        final Texture texture = img;
        final int h = texture.getHeight();
        final int w = texture.getWidth();
        final int section = h / 2;
        return new TextureAtlas.AtlasRegion(texture, (w / 2) - (section / 2), (h / 2) - (section / 2), section, section);
    }

    @Override
    public List<EUIKeywordTooltip> getTipsForFilters() {
        return tips.subList(1, tips.size());
    }

    @Override
    public List<EUIKeywordTooltip> getTips() {
        return tips;
    }

    public String getName() {
        return relicData.strings.NAME;
    }

    public int getValue() {
        return counter;
    }

    public void loadImage(String path) {
        loadImage(path, false);
    }

    public void loadImage(String path, boolean refresh) {
        Texture t = EUIRM.getTexture(path, true, refresh, true);
        if (t == null) {
            t = EUIRM.getLocalTexture(path, true, refresh, true);
            if (t == null) {
                path = PCLCoreImages.CardAffinity.unknown.path();
                t = EUIRM.getTexture(path, true, false, true);
            }
        }
        this.img = t;
        this.outlineImg = t;
    }

    // Initialize later to ensure relicData is set
    public void initializePCLTips() {
        if (tips == null) {
            tips = new ArrayList<>();
        }
        else {
            tips.clear();
        }

        AbstractPlayer.PlayerClass playerClass = EUIGameUtils.getPlayerClassForCardColor(relicData.cardColor);
        mainTooltip = playerClass != null ? new EUIKeywordTooltip(getName(), playerClass, description) : new EUIKeywordTooltip(getName(), description);
        tips.add(mainTooltip);
        EUIGameUtils.scanForTips(description, tips);
    }

    public boolean isEnabled() {
        return !super.grayscale;
    }

    @Override
    public PCLRelic makeCopy() {
        try {
            return relicData.create();
        }
        catch (Exception e) {
            return null;
        }
    }

    public boolean setEnabled(boolean value) {
        super.grayscale = !value;
        return value;
    }

    public void setupImages(String imagePath) {
        loadImage(imagePath);
    }

    @Override
    public final void updateDescription(AbstractPlayer.PlayerClass c) {
        this.description = getUpdatedDescription();
        if (this.mainTooltip != null)
        {
            this.mainTooltip.setDescription(description);
        }
    }

    @Override
    public String getUpdatedDescription() {
        return formatDescription(0, getValue());
    }

    @Override
    public void onEquip() {
        super.onEquip();

        if (GameUtilities.inBattle(true)) {
            activateBattleEffect();
        }
    }

    @Override
    public void onUnequip() {
        super.onUnequip();

        if (GameUtilities.inBattle(true)) {
            deactivateBattleEffect();
        }
    }

    @Override
    public void atPreBattle() {
        super.atPreBattle();

        activateBattleEffect();
    }

    @Override
    public void onVictory() {
        super.onVictory();

        deactivateBattleEffect();
    }

    @Override
    public void renderBossTip(SpriteBatch sb) {
        EUITooltip.queueTooltips(tips, Settings.WIDTH * 0.63F, Settings.HEIGHT * 0.63F);
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        EUITooltip.queueTooltips(this);
    }

    @Override
    protected void initializeTips() {
        // No-op, use initializePCLTips() instead
    }

    @Override
    public void update() {
        this.updateFlash();
        if (!this.isDone) {
            float rotation = getRotation();
            if (this.isAnimating) {
                float newGlow = getGlowTimer() - Gdx.graphics.getDeltaTime();
                FloatyEffect fEffect = getFEffect();

                if (newGlow < 0.0F) {
                    newGlow = 0.5F;
                    AbstractDungeon.effectList.add(new GlowRelicParticle(this.img, this.currentX + fEffect.x, this.currentY + fEffect.y, rotation));
                }
                setGlowTimer(newGlow);

                fEffect.update();
                if (this.hb.hovered) {
                    this.scale = Settings.scale * 0.75F;
                } else {
                    this.scale = MathHelper.scaleLerpSnap(this.scale, Settings.scale * 0.55F);
                }
            } else if (this.hb.hovered) {
                this.scale = Settings.scale * 0.625F;
            } else {
                this.scale = MathHelper.scaleLerpSnap(this.scale, Settings.scale * 0.5F);
            }

            if (this.isObtained) {
                if (rotation != 0.0F) {
                    setRotation(MathUtils.lerp(rotation, 0.0F, Gdx.graphics.getDeltaTime() * 6.0F * 2.0F));
                }

                if (this.currentX != this.targetX) {
                    this.currentX = MathUtils.lerp(this.currentX, this.targetX, Gdx.graphics.getDeltaTime() * 6.0F);
                    if (Math.abs(this.currentX - this.targetX) < 0.5F) {
                        this.currentX = this.targetX;
                    }
                }

                if (this.currentY != this.targetY) {
                    this.currentY = MathUtils.lerp(this.currentY, this.targetY, Gdx.graphics.getDeltaTime() * 6.0F);
                    if (Math.abs(this.currentY - this.targetY) < 0.5F) {
                        this.currentY = this.targetY;
                    }
                }

                if (this.currentY == this.targetY && this.currentX == this.targetX) {
                    this.isDone = true;
                    if (AbstractDungeon.topPanel != null) {
                        AbstractDungeon.topPanel.adjustRelicHbs();
                    }

                    this.hb.move(this.currentX, this.currentY);
                    if (this.tier == AbstractRelic.RelicTier.BOSS && AbstractDungeon.getCurrRoom() instanceof TreasureRoomBoss) {
                        AbstractDungeon.overlayMenu.proceedButton.show();
                    }

                    this.onEquip();
                }

                this.scale = Settings.scale * 0.5f;
            }

            if (this.hb != null) {
                this.hb.update();
                if (this.hb.hovered && (!AbstractDungeon.isScreenUp || AbstractDungeon.screen == AbstractDungeon.CurrentScreen.BOSS_REWARD) && AbstractDungeon.screen != AbstractDungeon.CurrentScreen.NEOW_UNLOCK) {
                    if (InputHelper.justClickedLeft && !this.isObtained) {
                        InputHelper.justClickedLeft = false;
                        this.hb.clickStarted = true;
                    }

                    if ((this.hb.clicked || CInputActionSet.select.isJustPressed()) && !this.isObtained) {
                        CInputActionSet.select.unpress();
                        this.hb.clicked = false;
                        if (!Settings.isTouchScreen) {
                            this.bossObtainLogic();
                        } else {
                            AbstractDungeon.bossRelicScreen.confirmButton.show();
                            AbstractDungeon.bossRelicScreen.confirmButton.isDisabled = false;
                            AbstractDungeon.bossRelicScreen.touchRelic = this;
                        }
                    }
                }
            }

            if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.BOSS_REWARD) {
                this.updateAnimation();
            }
        } else {
            if (AbstractDungeon.player != null && AbstractDungeon.player.relics.indexOf(this) / MAX_RELICS_PER_PAGE == relicPage) {
                this.hb.update();
            } else {
                this.hb.hovered = false;
            }

            if (this.hb.hovered && AbstractDungeon.topPanel.potionUi.isHidden) {
                this.scale = Settings.scale * 0.625F;
                CardCrawlGame.cursor.changeType(GameCursor.CursorType.INSPECT);
            } else {
                this.scale = MathHelper.scaleLerpSnap(this.scale, Settings.scale * 0.5f);
            }

            this.updateRelicPopupClick();
        }

    }

    protected void updateFlash() {
        if (this.flashTimer != 0.0F) {
            this.flashTimer -= Gdx.graphics.getDeltaTime();
            if (this.flashTimer < 0.0F) {
                if (this.pulse) {
                    this.flashTimer = 1.0F;
                } else {
                    this.flashTimer = 0.0F;
                }
            }
        }
    }

    protected void updateRelicPopupClick() {
        if (this.hb.hovered && InputHelper.justClickedLeft) {
            this.hb.clickStarted = true;
        }

        if (this.hb.clicked || this.hb.hovered && CInputActionSet.select.isJustPressed()) {
            CardCrawlGame.relicPopup.open(this, AbstractDungeon.player.relics);
            CInputActionSet.select.unpress();
            this.hb.clicked = false;
            this.hb.clickStarted = false;
        }
    }

    protected FloatyEffect getFEffect() {
        return ReflectionHacks.getPrivate(this, AbstractRelic.class, "f_effect");
    }

    protected float getGlowTimer() {
        return ReflectionHacks.getPrivate(this, AbstractRelic.class, "glowTimer");
    }

    protected float getRotation() {
        return ReflectionHacks.getPrivate(this, AbstractRelic.class, "rotation");
    }

    protected void setGlowTimer(float value) {
        ReflectionHacks.setPrivate(this, AbstractRelic.class, "glowTimer", value);
    }

    protected void setRotation(float value) {
        ReflectionHacks.setPrivate(this, AbstractRelic.class, "rotation", value);
    }

    public boolean canSpawn() {
        return relicData.cardColor.equals(GameUtilities.getActingColor());
    }
}
