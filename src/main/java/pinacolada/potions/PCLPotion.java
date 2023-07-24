package pinacolada.potions;

import basemod.ReflectionHacks;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.evacipated.cardcrawl.modthespire.lib.SpireSuper;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.vfx.FlashPotionEffect;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUIPreview;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.RotatingList;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.PCLActions;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.misc.PCLCollectibleSaveData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.Skills;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class PCLPotion extends AbstractPotion implements KeywordProvider, PointerProvider, CustomSavable<PCLCollectibleSaveData> {
    public final ArrayList<EUIKeywordTooltip> tips = new ArrayList<>();
    public final PCLPotionData potionData;
    public Skills skills;
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

    public void fillPreviews(RotatingList<EUIPreview> list) {
        PointerProvider.fillPreviewsForKeywordProvider(this, list);
    }

    @Override
    public List<EUIKeywordTooltip> getTipsForFilters() {
        return tips.subList(1, tips.size());
    }

    @Override
    public List<EUIKeywordTooltip> getTips() {
        return tips;
    }

    public String getUpdatedDescription() {
        return StringUtils.capitalize(getEffectPowerTextStrings());
    }

    public void initialize() {
        skills = new Skills();
        setup();
        this.isThrown = EUIUtils.any(getEffects(), e -> e.target.targetsSingle());
        this.targetRequired = isThrown;
        initializeTips();
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
    public PCLCollectibleSaveData onSave() {
        return auxiliaryData;
    }

    @Override
    public void onLoad(PCLCollectibleSaveData data) {
        if (data != null) {
            this.auxiliaryData = new PCLCollectibleSaveData(data);
        }
    }

    @Override
    public Type savedType() {
        return new TypeToken<PCLCollectibleSaveData>() {
        }.getType();
    }

    protected void renderImpl(SpriteBatch sb, boolean useOutlineColor) {
        float angle = ReflectionHacks.getPrivate(this, AbstractPotion.class, "angle");
        Texture containerImg = ReflectionHacks.getPrivate(this, AbstractPotion.class, "containerImg");
        Texture liquidImg = ReflectionHacks.getPrivate(this, AbstractPotion.class, "liquidImg");
        Texture hybridImg = ReflectionHacks.getPrivate(this, AbstractPotion.class, "hybridImg");
        Texture spotsImg = ReflectionHacks.getPrivate(this, AbstractPotion.class, "spotsImg");
        Texture outlineImg = ReflectionHacks.getPrivate(this, AbstractPotion.class, "outlineImg");
        ArrayList<FlashPotionEffect> effect = ReflectionHacks.getPrivate(this, AbstractPotion.class, "effect");

        updateFlash();
        updateEffect();
        if (this.hb.hovered) {
            EUITooltip.queueTooltips(this);
            this.scale = 1.5F * Settings.scale;
        }
        else {
            this.scale = MathHelper.scaleLerpSnap(this.scale, 1.2F * Settings.scale);
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

        for (FlashPotionEffect e : effect) {
            e.render(sb, this.posX, this.posY);
        }

        if (this.hb != null) {
            this.hb.render(sb);
        }
    }

    public PCLPotion setForm(int form) {
        this.auxiliaryData.form = form;
        initializeTips();
        return this;
    }

    public void setup() {
    }

    @Override
    public int timesUpgraded() {
        return this.getPotency() - 1;
    }

    @Override
    public int xValue() {
        return this.potency;
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Skills getSkills() {
        return skills;
    }

    @SpireOverride
    protected void updateEffect() {
        SpireSuper.call();
    }

    @SpireOverride
    protected void updateFlash() {
        SpireSuper.call();
    }

    public PCLPotion upgrade() {
        if (this.canUpgrade()) {
            auxiliaryData.timesUpgraded += 1;
            initializeTips();
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

    @Override
    public boolean canUse() {
        return EUIUtils.all(skills.onUseEffects, sk -> sk.canPlay(null, null)) && super.canUse();
    }

    public void update() {
        super.update();
        if (this.hb.justHovered) {
            for (PSkill<?> ef : getEffects()) {
                ef.setAmountFromCard();
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        renderImpl(sb, false);
    }

    @Override
    public void shopRender(SpriteBatch sb) {
        this.generateSparkles(0.0F, 0.0F, false);
        renderImpl(sb, false);
    }

    @Override
    public void labRender(SpriteBatch sb) {
        renderImpl(sb, true);
    }

    // May be null before potion data is initialized
    // Base will be multiplied by 2, then decreased by 1 (so 0 upgrades will give 0 upgrade bonuses without Sacred Bark and 1 with it)
    @Override
    public int getPotency(int i) {
        return auxiliaryData != null ? auxiliaryData.timesUpgraded + 1 : 1;
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
}
