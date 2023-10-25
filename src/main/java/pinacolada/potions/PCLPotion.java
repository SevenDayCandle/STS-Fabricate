package pinacolada.potions;

import basemod.ReflectionHacks;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.evacipated.cardcrawl.modthespire.lib.SpireSuper;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUIPreview;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIColors;
import extendedui.utilities.RotatingList;
import pinacolada.actions.PCLActions;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.misc.PCLCollectibleSaveData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillPowerContainer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class PCLPotion extends AbstractPotion implements KeywordProvider, PointerProvider, CustomSavable<PCLCollectibleSaveData> {
    public final ArrayList<EUIKeywordTooltip> tips = new ArrayList<>();
    public final PCLPotionData potionData;
    private float flashTime;
    public PSkillPowerContainer skills;
    public EUIKeywordTooltip mainTooltip;
    public PCLCollectibleSaveData auxiliaryData = new PCLCollectibleSaveData();

    // We deliberately avoid using initializeData because we need to load the PotionStrings after the super call
    public PCLPotion(PCLPotionData data) {
        super("", data.ID, data.rarity, data.size, data.effect, data.liquidColor.cpy(), data.hybridColor.cpy(), data.spotsColor.cpy());
        this.potionData = data;
        name = data.strings.NAME;
        initialize();
    }

    public static String createFullID(Class<? extends PCLPotion> type) {
        return createFullID(PGR.core, type);
    }

    public static String createFullID(PCLResources<?, ?, ?, ?> resources, Class<? extends PCLPotion> type) {
        return resources.createID(type.getSimpleName());
    }

    protected static PCLPotionData register(Class<? extends PCLPotion> type) {
        return register(type, PGR.core);
    }

    protected static PCLPotionData register(Class<? extends PCLPotion> type, PCLResources<?, ?, ?, ?> resources) {
        return registerPotionData(new PCLPotionData(type, resources));
    }

    protected static <T extends PCLPotionData> T registerPotionData(T cardData) {
        return PCLPotionData.registerData(cardData);
    }

    protected static PCLPotionData registerTemplate(Class<? extends PCLPotion> type) {
        return registerTemplate(type, PGR.core);
    }

    protected static PCLPotionData registerTemplate(Class<? extends PCLPotion> type, PCLResources<?, ?, ?, ?> resources) {
        return PCLPotionData.registerTemplate(new PCLPotionData(type, resources));
    }

    public boolean canUpgrade() {
        return auxiliaryData.timesUpgraded < potionData.maxUpgradeLevel || potionData.maxUpgradeLevel < 0;
    }

    @Override
    public boolean canUse() {
        return EUIUtils.all(skills.onUseEffects, sk -> sk.canPlay(null, null)) && super.canUse();
    }

    public void fillPreviews(RotatingList<EUIPreview> list) {
        PointerProvider.fillPreviewsForKeywordProvider(this, list);
    }

    @Override
    public void flash() {
        flashTime = 1;
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String getName() {
        return name;
    }

    // May be null before potion data is initialized
    @Override
    public int getPotency(int i) {
        return auxiliaryData != null ? auxiliaryData.timesUpgraded + 1 : 1;
    }

    @Override
    public PSkillPowerContainer getSkills() {
        return skills;
    }

    @Override
    public List<EUIKeywordTooltip> getTips() {
        return tips;
    }

    @Override
    public List<EUIKeywordTooltip> getTipsForFilters() {
        return tips.subList(1, tips.size());
    }

    public String getUpdatedDescription() {
        return getEffectPowerTextStrings();
    }

    @Override
    public int getXValue() {
        return this.potency;
    }

    public void initialize() {
        skills = new PSkillPowerContainer();
        setup();
        initializeTargetRequired();
        initializeTips();
    }

    protected void initializeTargetRequired() {
        for (PSkill<?> skill : getEffects()) {
            skill.recurse(e -> {
                if (e.target.targetsSingle()) {
                    this.targetRequired = this.isThrown = true;
                }
            });
        }
    }

    protected void initializeTips() {
        this.description = getUpdatedDescription();
        tips.clear();
        ModInfo info = EUIGameUtils.getModInfo(this);
        mainTooltip = info != null ? new EUIKeywordTooltip(name, description, info.ID) : new EUIKeywordTooltip(name, description);
        tips.add(mainTooltip);
        EUITooltip.scanForTips(description, tips);
    }

    @Override
    public void labRender(SpriteBatch sb) {
        renderImpl(sb, true);
    }

    @Override
    public AbstractPotion makeCopy() {
        try {
            return potionData.create();
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onLoad(PCLCollectibleSaveData data) {
        if (data != null) {
            this.auxiliaryData = new PCLCollectibleSaveData(data);
            onUpgrade();
        }
    }

    @Override
    public PCLCollectibleSaveData onSave() {
        return auxiliaryData;
    }

    protected void onUpgrade() {
        for (PSkill<?> ef : getEffects()) {
            ef.setAmountFromCard().onUpgrade();
        }
        for (PSkill<?> ef : getPowerEffects()) {
            ef.setAmountFromCard().onUpgrade();
        }
        initializeTips();
    }

    @Override
    public void render(SpriteBatch sb) {
        renderImpl(sb, false);
    }

    protected void renderFlash(SpriteBatch sb, float x, float y, float scale, Texture containerImg, Texture liquidImg, Texture hybridImg, Texture spotsImg, Texture outlineImg) {
        Color renderColor = EUIColors.white(liquidColor.a);
        sb.setColor(renderColor);
        sb.draw(containerImg, x - 32.0F, y - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, scale, scale, 0.0F, 0, 0, 64, 64, false, false);
        sb.setColor(this.liquidColor);
        sb.draw(liquidImg, x - 32.0F, y - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, scale, scale, 0.0F, 0, 0, 64, 64, false, false);
        sb.setBlendFunction(770, 1);
        sb.setColor(renderColor);
        sb.draw(containerImg, x - 32.0F, y - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, scale, scale, 0.0F, 0, 0, 64, 64, false, false);
        sb.setColor(this.liquidColor);
        sb.draw(liquidImg, x - 32.0F, y - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, scale, scale, 0.0F, 0, 0, 64, 64, false, false);
        if (hybridImg != null) {
            sb.setColor(this.hybridColor);
            sb.draw(hybridImg, x - 32.0F, y - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, scale, scale, 0.0F, 0, 0, 64, 64, false, false);
        }
        if (spotsImg != null) {
            sb.setColor(this.spotsColor);
            sb.draw(spotsImg, x - 32.0F, y - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, scale, scale, 0.0F, 0, 0, 64, 64, false, false);
        }
        sb.setBlendFunction(770, 771);
    }

    protected void renderImpl(SpriteBatch sb, boolean useOutlineColor) {
        float angle = ReflectionHacks.getPrivate(this, AbstractPotion.class, "angle");
        Texture containerImg = ReflectionHacks.getPrivate(this, AbstractPotion.class, "containerImg");
        Texture liquidImg = ReflectionHacks.getPrivate(this, AbstractPotion.class, "liquidImg");
        Texture hybridImg = ReflectionHacks.getPrivate(this, AbstractPotion.class, "hybridImg");
        Texture spotsImg = ReflectionHacks.getPrivate(this, AbstractPotion.class, "spotsImg");
        Texture outlineImg = ReflectionHacks.getPrivate(this, AbstractPotion.class, "outlineImg");

        updateEffect();
        if (this.hb.hovered) {
            renderTip();
            this.scale = 1.25F * Settings.scale;
        }
        else {
            this.scale = MathHelper.scaleLerpSnap(this.scale, Settings.scale);
        }

        this.renderOutline(sb, useOutlineColor ? this.labOutlineColor : Settings.HALF_TRANSPARENT_BLACK_COLOR);
        sb.setColor(this.liquidColor);
        sb.draw(liquidImg, this.posX - 32.0F, this.posY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, this.scale, this.scale, angle, 0, 0, 64, 64, false, false);
        if (this.hybridColor != null && hybridImg != null) {
            sb.setColor(this.hybridColor);
            sb.draw(hybridImg, this.posX - 32.0F, this.posY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, this.scale, this.scale, angle, 0, 0, 64, 64, false, false);
        }

        if (this.spotsColor != null && spotsImg != null) {
            sb.setColor(this.spotsColor);
            sb.draw(spotsImg, this.posX - 32.0F, this.posY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, this.scale, this.scale, angle, 0, 0, 64, 64, false, false);
        }

        sb.setColor(Color.WHITE);
        sb.draw(containerImg, this.posX - 32.0F, this.posY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, this.scale, this.scale, angle, 0, 0, 64, 64, false, false);

        if (flashTime > 0) {
            flashTime -= EUI.delta();
            renderFlash(sb, posX, posY, Interpolation.exp10In.apply(Settings.scale, Settings.scale * 12f, flashTime), containerImg, liquidImg, hybridImg, spotsImg, outlineImg);
        }

        if (this.hb != null) {
            this.hb.render(sb);
        }
    }

    public void renderTip() {
        EUITooltip.queueTooltips(this);
    }

    @Override
    public final Type savedType() {
        return PCLCollectibleSaveData.TOKEN.getType();
    }

    public PCLPotion setForm(int form) {
        this.auxiliaryData.form = form;
        initializeTips();
        return this;
    }

    public void setTimesUpgraded(int i) {
        auxiliaryData.timesUpgraded = i;
        for (PSkill<?> ef : getEffects()) {
            ef.setAmountFromCard().onUpgrade();
        }
        for (PSkill<?> ef : getPowerEffects()) {
            ef.setAmountFromCard().onUpgrade();
        }
        initializeTips();
    }

    public void setup() {
    }

    @Override
    public void shopRender(SpriteBatch sb) {
        this.generateSparkles(0.0F, 0.0F, false);
        renderImpl(sb, false);
    }

    @Override
    public int timesUpgraded() {
        return this.getPotency() - 1;
    }

    public void update() {
        super.update();
        if (this.hb.justHovered) {
            for (PSkill<?> ef : getEffects()) {
                ef.setAmountFromCard();
            }
        }
    }

    @SpireOverride
    protected void updateEffect() {
        SpireSuper.call();
    }

    @SpireOverride
    protected void updateFlash() {
        // No-op to avoid creating flash effects that can crash the game
    }

    public PCLPotion upgrade() {
        if (this.canUpgrade()) {
            auxiliaryData.timesUpgraded += 1;
            onUpgrade();
        }
        return this;
    }

    @Override
    public void use(AbstractCreature target) {
        final PCLUseInfo info = CombatManager.playerSystem.generateInfo(null, AbstractDungeon.player, target);
        for (PSkill<?> ef : getEffects()) {
            ef.use(info, PCLActions.bottom);
        }
    }
}
