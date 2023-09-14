package pinacolada.relics;

import basemod.ReflectionHacks;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.FloatyEffect;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.PCLActions;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.misc.PCLCollectibleSaveData;
import pinacolada.patches.dungeon.RewardItemPatches;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.megacrit.cardcrawl.rewards.RewardItem.TEXT;
import static pinacolada.utilities.GameUtilities.CHAR_OFFSET;

public abstract class PCLRelic extends AbstractRelic implements KeywordProvider, CustomSavable<PCLCollectibleSaveData> {
    private static EUITooltip hiddenTooltip;
    private static EUITooltip sapphireTooltip;

    public static AbstractPlayer player;
    public static Random rng;
    public final PCLRelicData relicData;
    public ArrayList<EUIKeywordTooltip> euiTips;
    public EUIKeywordTooltip mainTooltip;
    public PCLCollectibleSaveData auxiliaryData = new PCLCollectibleSaveData();

    public PCLRelic(PCLRelicData data) {
        super(data.ID, "", data.tier, data.sfx);
        this.relicData = data;
        preSetup(data);
        initializePCLTips();
        setupImages(data.imagePath);
    }

    public static String createFullID(Class<? extends PCLRelic> type) {
        return createFullID(PGR.core, type);
    }

    public static String createFullID(PCLResources<?, ?, ?, ?> resources, Class<? extends PCLRelic> type) {
        return resources.createID(type.getSimpleName());
    }

    public static EUITooltip getHiddenTooltip() {
        if (hiddenTooltip == null) {
            hiddenTooltip = new EUITooltip(LABEL[1], MSG[1]);
        }
        return hiddenTooltip;
    }

    public static EUITooltip getSapphireTooltip() {
        if (sapphireTooltip == null) {
            sapphireTooltip = new EUITooltip(TEXT[7], TEXT[8] + FontHelper.colorString(TEXT[6] + TEXT[9], "y"));
        }
        return sapphireTooltip;
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

    protected static PCLRelicData registerTemplate(Class<? extends PCLRelic> type) {
        return registerTemplate(type, PGR.core);
    }

    protected static PCLRelicData registerTemplate(Class<? extends PCLRelic> type, PCLResources<?, ?, ?, ?> resources) {
        return PCLRelicData.registerTemplate(new PCLRelicData(type, resources));
    }

    public static void renderUnseenTip() {
        EUITooltip.queueTooltip(getHiddenTooltip());
    }


    protected void activateBattleEffect() {

    }

    public int addCounter(int amount) {
        setCounter(counter + amount);

        return counter;
    }

    public float atBlockLastModify(PCLUseInfo info, float block) {
        return atBlockLastModify(block, info.card);
    }

    public float atBlockLastModify(float block, AbstractCard c) {
        return block;
    }

    public float atBlockModify(PCLUseInfo info, float block) {
        return atBlockModify(block, info.card);
    }

    public float atBlockModify(float block, AbstractCard c) {
        return block;
    }

    public int atCostModify(PCLUseInfo info, int block) {
        return atCostModify(block, info.card);
    }

    public int atCostModify(int cost, AbstractCard c) {
        return cost;
    }

    public float atDamageLastModify(PCLUseInfo info, float block) {
        return atDamageLastModify(block, info.card);
    }

    public float atDamageLastModify(float block, AbstractCard c) {
        return block;
    }

    public float atDamageModify(PCLUseInfo info, float block) {
        return atDamageModify(block, info.card);
    }

    public float atHealModify(PCLUseInfo info, float block) {
        return block;
    }

    public float atHitCountModify(PCLUseInfo info, float block) {
        return block;
    }

    @Override
    public void atPreBattle() {
        super.atPreBattle();

        activateBattleEffect();
    }

    public float atRightCountModify(PCLUseInfo info, float block) {
        return block;
    }

    public float atSkillBonusModify(PCLUseInfo info, float block) {
        return block;
    }

    public boolean canSpawn() {
        return relicData.cardColor == AbstractCard.CardColor.COLORLESS || relicData.cardColor.equals(GameUtilities.getActingColor());
    }

    public boolean canUpgrade() {
        return auxiliaryData.timesUpgraded < relicData.maxUpgradeLevel || relicData.maxUpgradeLevel < 0;
    }

    protected void deactivateBattleEffect() {

    }

    protected void displayAboveCreature(AbstractCreature creature) {
        PCLActions.top.add(new RelicAboveCreatureAction(creature, this));
    }

    protected void forceObtain(AbstractPlayer p, int slot, boolean callOnEquip) {
        this.isDone = true;
        this.isObtained = true;

        if (slot >= p.relics.size()) {
            p.relics.add(this);
        }
        else {
            p.relics.set(slot, this);
        }

        this.currentX = 64.0F * Settings.scale + (float) slot * PAD_X;
        this.currentY = (float) Settings.HEIGHT - 102.0F * Settings.scale; // We will never be on mobile lol
        this.targetX = this.currentX;
        this.targetY = this.currentY;
        this.hb.move(this.currentX, this.currentY);
        if (callOnEquip) {
            this.onEquip();
            this.relicTip();
        }

        UnlockTracker.markRelicAsSeen(this.relicId);
        updateDescription(null);
        if (AbstractDungeon.topPanel != null) {
            AbstractDungeon.topPanel.adjustRelicHbs();
        }
    }

    protected String formatDescription(int index, Object... args) {
        return EUIUtils.format(getDescriptions()[index], args);
    }

    protected String getCounterString() {
        return String.valueOf(counter);
    }

    // Deliberately avoid getUpdatedDescrption because this gets called in the constructor before we can set up skills or data
    public String getDescriptionImpl() {
        return formatDescription(0, getValue());
    }

    public String[] getDescriptions() {
        return DESCRIPTIONS;
    }

    protected FloatyEffect getFEffect() {
        return ReflectionHacks.getPrivate(this, AbstractRelic.class, "f_effect");
    }

    public String getNameFromData() {
        String name = relicData.strings.NAME;
        if (auxiliaryData.timesUpgraded > 0) {
            return GameUtilities.getMultiformName(name, auxiliaryData.form, auxiliaryData.timesUpgraded, relicData.maxForms, relicData.maxUpgradeLevel, relicData.branchFactor);
        }
        return name;
    }

    protected float getOffsetX() {
        return ReflectionHacks.getPrivate(this, AbstractRelic.class, "offsetX");
    }

    public TextureAtlas.AtlasRegion getPowerIcon() {
        final Texture texture = img;
        final int h = texture.getHeight();
        final int w = texture.getWidth();
        final int section = h / 2;
        return new TextureAtlas.AtlasRegion(texture, (w / 2) - (section / 2), (h / 2) - (section / 2), section, section);
    }

    // Relics that are replaced with this one when obtained
    public PCLRelicData[] getReplacementIDs() {
        return null;
    }

    protected float getRotation() {
        return ReflectionHacks.getPrivate(this, AbstractRelic.class, "rotation");
    }

    @Override
    public List<EUIKeywordTooltip> getTips() {
        return euiTips;
    }

    @Override
    public List<EUIKeywordTooltip> getTipsForFilters() {
        return euiTips.subList(1, euiTips.size());
    }

    // Don't use this, use getDescriptionImpl instead which is run after the constructor
    @Deprecated
    @Override
    public final String getUpdatedDescription() {
        return super.getUpdatedDescription();
    }

    public int getValue() {
        return counter;
    }

    // Initialize later to ensure relicData is set
    public void initializePCLTips() {
        if (euiTips == null) {
            euiTips = new ArrayList<>();
        }
        else {
            euiTips.clear();
        }
        updateDescription(null);
        ModInfo info = EUIGameUtils.getModInfo(this);
        mainTooltip = info != null ? new EUIKeywordTooltip(getNameFromData(), description, info.ID) : new EUIKeywordTooltip(getNameFromData(), description);
        euiTips.add(mainTooltip);
        EUITooltip.scanForTips(description, euiTips);
    }

    @Override
    protected void initializeTips() {
        // Unused, use euitips instead
        tips.clear();
    }

    @Override
    public void instantObtain(AbstractPlayer p, int slot, boolean callOnEquip) {
        if (tryRetain(callOnEquip)) {
            super.instantObtain(p, slot, callOnEquip);
        }
    }

    @Override
    public void instantObtain() {
        if (tryRetain(true)) {
            super.instantObtain();
        }
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

    @Override
    public void obtain() {
        if (tryRetain(true)) {
            super.obtain();
        }
    }

    @Override
    public void onEquip() {
        super.onEquip();

        if (GameUtilities.inBattle(true)) {
            activateBattleEffect();
        }

        updateDescription(null);
    }

    @Override
    public void onLoad(PCLCollectibleSaveData data) {
        if (data != null) {
            this.auxiliaryData = new PCLCollectibleSaveData(data);
        }
        updateDescription(null);
    }

    @Override
    public PCLCollectibleSaveData onSave() {
        return auxiliaryData;
    }

    protected void onStack(AbstractRelic other) {
        upgrade();
    }

    @Override
    public void onUnequip() {
        super.onUnequip();

        if (GameUtilities.inBattle(true)) {
            deactivateBattleEffect();
        }
    }

    @Override
    public void onVictory() {
        super.onVictory();

        deactivateBattleEffect();
    }

    protected void preSetup(PCLRelicData data) {

    }

    @Override
    public void render(SpriteBatch sb) {
        if (!Settings.hideRelics) {
            float xOffset = -64;
            float yOffset = -64;

            if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.BOSS_REWARD && !isObtained) {
                FloatyEffect f_effect = getFEffect();
                xOffset += f_effect.x;
                yOffset += f_effect.y;
            }

            renderRelicImage(sb, Color.WHITE, xOffset, yOffset, 0.5f);
            renderCounter(sb, false);
            if (this.isDone) {
                renderFlash(sb, false);
            }
            if (this.hb.hovered && !this.isObtained && (!AbstractDungeon.isScreenUp || AbstractDungeon.screen == AbstractDungeon.CurrentScreen.BOSS_REWARD || AbstractDungeon.screen == AbstractDungeon.CurrentScreen.SHOP)) {
                this.renderBossTip(sb);
            }

            this.hb.render(sb);
        }
    }

    @Override
    public void render(SpriteBatch sb, boolean renderAmount, Color outlineColor) {
        float scaleMult = AbstractDungeon.screen == AbstractDungeon.CurrentScreen.NEOW_UNLOCK ? MathUtils.cosDeg((float) (System.currentTimeMillis() / 5L % 360L)) : 0.5f;
        if (this.isSeen) {
            renderRelicImage(sb,
                    Color.WHITE,
                    -64f,
                    -64f,
                    scaleMult);
        }
        else {
            EUIRenderHelpers.drawSilhouette(sb, Color.LIGHT_GRAY, s -> {
                renderRelicImage(s,
                         this.hb.hovered ? Settings.HALF_TRANSPARENT_BLACK_COLOR : Color.BLACK,
                        -64f,
                        -64f,
                        scaleMult);
            });
        }

        if (this.hb.hovered && !CardCrawlGame.relicPopup.isOpen) {
            if (!this.isSeen) {
                renderUnseenTip();
            }
            else {
                this.renderTip(sb);
            }
        }
        this.hb.render(sb);
    }

    @Override
    public void renderBossTip(SpriteBatch sb) {
        EUITooltip.queueTooltips(euiTips, Settings.WIDTH * 0.63F, Settings.HEIGHT * 0.63F);
    }

    @Override
    public void renderInTopPanel(SpriteBatch sb) {
        if (!Settings.hideRelics) {
            PCLRenderHelpers.drawGrayscaleIf(sb, s -> renderRelicImage(s, Color.WHITE, getOffsetX() - 64f, -64f, 0.5f), grayscale);
            this.renderCounter(sb, true);
            this.renderFlash(sb, true);
            this.hb.render(sb);
        }
    }

    public void renderRelicImage(SpriteBatch sb, Color color, float xOffset, float yOffset, float scaleMult) {
        sb.setColor(color);
        sb.draw(this.img, this.currentX + xOffset, this.currentY + yOffset, 64.0F, 64.0F, 128.0F, 128.0F, this.scale * scaleMult, this.scale * scaleMult, getRotation(), 0, 0, 128, 128, false, false);
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        if (RewardItemPatches.isRenderingForSapphire) {
            EUITooltip.queueTooltips(mainTooltip, getSapphireTooltip());
            RewardItemPatches.isRenderingForSapphire = false;
            return;
        }
        EUITooltip.queueTooltips(this);
    }

    @Override
    public void renderWithoutAmount(SpriteBatch sb, Color c) {
        renderRelicImage(sb, Color.WHITE, -64f, -64f, 0.5f);
        if (this.hb.hovered) {
            this.renderTip(sb);
        }
        this.hb.render(sb);
    }

    @Override
    public Type savedType() {
        return new TypeToken<PCLCollectibleSaveData>() {
        }.getType();
    }

    // Imitating lizard tail so that used up status can be saved
    @Override
    public void setCounter(int setCounter) {
        if (setCounter == -2) {
            this.usedUp();
        }
        else {
            super.setCounter(setCounter);
        }
    }

    public boolean setEnabled(boolean value) {
        super.grayscale = !value;
        return value;
    }

    public PCLRelic setForm(int form) {
        this.auxiliaryData.form = form;
        initializePCLTips();
        return this;
    }

    public void setTimesUpgraded(int times) {
        auxiliaryData.timesUpgraded = times;
        updateName();
        updateDescription(null);
    }

    public void setupImages(String imagePath) {
        Texture t = EUIRM.getTexture(imagePath, true, false);
        if (t == null) {
            imagePath = PCLCoreImages.CardAffinity.unknown.path();
            t = EUIRM.getTexture(imagePath, true, false);
        }
        this.img = t;
        this.outlineImg = t;
    }

    protected boolean tryRetain(boolean callOnEquip) {
        PCLRelicData[] replacements = getReplacementIDs();
        if (replacements != null) {
            Set<String> ids = new HashSet<>(EUIUtils.map(replacements, r -> r.ID));
            ArrayList<AbstractRelic> relics = player.relics;
            for (int i = 0; i < relics.size(); i++) {
                AbstractRelic old = relics.get(i);
                if (ids.contains(old.relicId)) {
                    forceObtain(player, i, callOnEquip);
                    setCounter(old.counter);
                    return false;
                }
                else if (old instanceof PCLRelic && relicId.equals(old.relicId) && ((PCLRelic) old).relicData.unique) {
                    ((PCLRelic) old).onStack(this);
                    this.discarded = true;
                    this.isDone = true;
                    this.isObtained = true;
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public final void updateDescription(AbstractPlayer.PlayerClass c) {
        this.description = usedUp ? MSG[2] : getDescriptionImpl();
        PCLRelicData[] replacements = getReplacementIDs();
        if (replacements != null) {
            String joinedNames = PCLCoreStrings.joinWithAnd(r -> r.strings.NAME, replacements);
            String replaceString = PCLCoreStrings.colorString("i", EUIUtils.format(PGR.core.strings.misc_replaces, joinedNames));
            this.description = EUIUtils.joinStrings(EUIUtils.SPLIT_LINE, replaceString, this.description);
        }
        if (this.mainTooltip != null) {
            this.mainTooltip.setTitle(name).setDescription(description);
        }
    }

    protected void updateFlash() {
        if (this.flashTimer != 0.0F) {
            this.flashTimer -= Gdx.graphics.getDeltaTime();
            if (this.flashTimer < 0.0F) {
                if (this.pulse) {
                    this.flashTimer = 1.0F;
                }
                else {
                    this.flashTimer = 0.0F;
                }
            }
        }
    }

    public void updateName() {
        ReflectionHacks.setPrivateFinal(this, AbstractRelic.class, "name", getNameFromData());
    }

    public PCLRelic upgrade() {
        if (this.canUpgrade()) {
            auxiliaryData.timesUpgraded += 1;
            updateName();
            updateDescription(null);
        }
        return this;
    }

    @Override
    public void usedUp() {
        this.counter = -2; // Lizard's tail
        this.grayscale = true;
        this.usedUp = true;
        this.description = MSG[2];
        this.initializePCLTips();
    }
}
