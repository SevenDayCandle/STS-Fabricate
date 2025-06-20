package pinacolada.cards.base;

import basemod.ReflectionHacks;
import basemod.abstracts.AbstractCardModifier;
import basemod.abstracts.CustomSavable;
import basemod.helpers.CardModifierManager;
import basemod.helpers.TooltipInfo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.blockmods.AbstractBlockModifier;
import com.evacipated.cardcrawl.mod.stslib.blockmods.BlockModifierManager;
import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.SoulboundField;
import com.evacipated.cardcrawl.mod.stslib.patches.CustomTargeting;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.evacipated.cardcrawl.modthespire.lib.SpireSuper;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.green.Tactician;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FlightPower;
import com.megacrit.cardcrawl.powers.FocusPower;
import com.megacrit.cardcrawl.powers.LockOnPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.*;
import extendedui.interfaces.delegates.ActionT3;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT3;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.TextureCache;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUIPreview;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.*;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.blights.PCLBlight;
import pinacolada.cardmods.TemporaryCostModifier;
import pinacolada.cards.CardTriggerConnection;
import pinacolada.cards.base.cardText.PCLCardText;
import pinacolada.cards.base.fields.*;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLCardTargetingManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.EffekseerEFK;
import pinacolada.effects.PCLAttackVFX;
import pinacolada.interfaces.listeners.OnAddToDeckListener;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.monsters.PCLCardCreature;
import pinacolada.orbs.PCLOrb;
import pinacolada.patches.screens.GridCardSelectScreenPatches;
import pinacolada.powers.PCLPower;
import pinacolada.powers.replacement.PCLLockOnPower;
import pinacolada.relics.PCLRelic;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillContainer;
import pinacolada.skills.delay.DelayTiming;
import pinacolada.skills.skills.PSpecialCond;
import pinacolada.skills.skills.PSpecialPowerSkill;
import pinacolada.skills.skills.PSpecialSkill;
import pinacolada.skills.skills.PTrigger;
import pinacolada.skills.skills.base.moves.PMove_StackCustomPower;
import pinacolada.skills.skills.special.primary.PCardPrimary_DealDamage;
import pinacolada.skills.skills.special.primary.PCardPrimary_GainBlock;
import pinacolada.ui.combat.PowerFormulaDisplay;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class PCLCard extends AbstractCard implements KeywordProvider, EditorCard, OnAddToDeckListener, CustomSavable<PCLCardSaveData> {
    private final static float AUGMENT_OFFSET_X = -AbstractCard.RAW_W * 0.4695f;
    private final static float AUGMENT_OFFSET_Y = AbstractCard.RAW_H * 0.08f;
    private final static float BANNER_OFFSET_X = -AbstractCard.RAW_W * 0.349f;
    private final static float BANNER_OFFSET_X2 = AbstractCard.RAW_W * 0.345f;
    private final static float BANNER_OFFSET_Y = -AbstractCard.RAW_H * 0.04f;
    private final static float BANNER_OFFSET_Y2 = -AbstractCard.RAW_H * 0.06f;
    private final static float FOOTER_SIZE = 52f;
    private final static float HEADER_WIDTH = AbstractCard.IMG_WIDTH * 0.73f;
    private static final Color COLORLESS_ORB_COLOR = new Color(0.7f, 0.7f, 0.7f, 1);
    private static final Color CURSE_COLOR = new Color(0.22f, 0.22f, 0.22f, 1);
    private static final Color COLOR_SECRET = new Color(0.6f, 0.45f, 0.1f, 1f);
    private static final Color COLOR_ULTRA_RARE = new Color(1f, 0.44f, 0.3f, 1f);
    private static final Color HOVER_IMG_COLOR = new Color(1f, 0.815f, 0.314f, 0.8f);
    private static final Color SELECTED_CARD_COLOR = new Color(0.5f, 0.9f, 0.9f, 1f);
    private static final float SHADOW_OFFSET_X = 18f * Settings.scale;
    private static final float SHADOW_OFFSET_Y = 14f * Settings.scale;
    public static final Color CARD_TYPE_COLOR = new Color(0.35F, 0.35F, 0.35F, 1.0F);
    public static final Color REGULAR_GLOW_COLOR = new Color(0.2F, 0.9F, 1.0F, 0.25F);
    public static final Color SHADOW_COLOR = new Color(0, 0, 0, 0.25f);
    public static final Color SYNERGY_GLOW_COLOR = new Color(1, 0.843f, 0, 0.25f);
    public static final String UNPLAYABLE_MESSAGE = CardCrawlGame.languagePack.getCardStrings(Tactician.ID).EXTENDED_DESCRIPTION[0];
    private final transient Color energyColor = new Color();
    private final transient float[] fakeGlowList = new float[4];
    public final PSkillContainer skills = new PSkillContainer();
    public final ArrayList<EUIKeywordTooltip> tooltips = new ArrayList<>();
    public final ArrayList<PCLAugment> augments = new ArrayList<>();
    public final PCLCardAffinities affinities;
    public final PCLCardData cardData;
    public final PCLCardText cardText;
    private ColoredTexture portraitImgBackup;
    private float badgeAlphaTargetOffset = 1f;
    private float badgeAlphaOffset = -0.2f;
    private transient int glowIndex = 0;
    protected transient ArrayList<PCLCardAffinity> previousAffinities;
    protected ColoredTexture portraitForeground;
    protected ColoredTexture portraitImg;
    protected String bottomText;
    public DelayTiming timing = DelayTiming.StartOfTurnLast;
    public PCLAttackType attackType = PCLAttackType.Normal;
    public PCLCardSaveData auxiliaryData = new PCLCardSaveData();
    public PCLCardTarget pclTarget = PCLCardTarget.Single;
    public PCardPrimary_DealDamage onAttackEffect;
    public PCardPrimary_GainBlock onBlockEffect;
    public boolean hovered;
    public boolean isHealModified = false;
    public boolean isHitCountModified = false;
    public boolean isPopup = false;
    public boolean isPreview = false;
    public boolean isRightCountModified = false;
    public boolean upgradedHeal = false;
    public boolean upgradedHitCount = false;
    public boolean upgradedRightCount = false;
    public float hoverDuration;
    public int baseHitCount;
    public int baseRightCount;
    public int currentHealth = 1; // Used for storing the card's current HP in battle
    public int hitCount;
    public int upgradeLevelIncrease;
    public int rightCount;
    public transient AbstractCreature owner;
    public transient ArrayList<AbstractCreature> multiDamageCreatures;
    public transient CardTriggerConnection controller;
    public transient PCLCard parent;
    public transient PowerFormulaDisplay formulaDisplay;

    protected PCLCard(PCLCardData cardData) {
        this(cardData, cardData.ID, cardData.imagePath, cardData.getCost(0), cardData.cardType, cardData.cardColor, cardData.cardRarity, PCLCardTargetingManager.PCL, 0, 0, null);
    }

    protected PCLCard(PCLCardData cardData, String id, String imagePath, int cost, CardType type, CardColor color, CardRarity rarity, CardTarget target, int form, int timesUpgraded, Object input) {
        super(id, cardData.strings.NAME, "status/beta", "status/beta", cost, VisibleSkill.DEFAULT, type, color, rarity, target);
        this.cardData = cardData;
        this.cardText = new PCLCardText(this);
        this.affinities = new PCLCardAffinities(this);

        for (int i = 0; i < cardData.slots; i++) {
            augments.add(null);
        }

        setupFlags();
        setupProperties(cardData, form, timesUpgraded);
        setup(input);
        setForm(form, timesUpgraded);
        setupImages(imagePath);
    }

    protected PCLCard(PCLCardData cardData, Object input) {
        this(cardData, cardData.ID, cardData.imagePath, cardData.getCost(0), cardData.cardType, cardData.cardColor, cardData.cardRarity, PCLCardTargetingManager.PCL, 0, 0, input);
    }

    protected PCLCard(PCLCardData cardData, int form, int timesUpgraded) {
        this(cardData, cardData.ID, cardData.imagePath, cardData.getCost(0), cardData.cardType, cardData.cardColor, cardData.cardRarity, PCLCardTargetingManager.PCL, form, timesUpgraded, null);
    }

    protected PCLCard(PCLCardData cardData, int form, int timesUpgraded, Object input) {
        this(cardData, cardData.ID, cardData.imagePath, cardData.getCost(0), cardData.cardType, cardData.cardColor, cardData.cardRarity, PCLCardTargetingManager.PCL, form, timesUpgraded, input);
    }

    protected PCLCard(PCLCardData cardData, String id, String imagePath, int cost, CardType type, CardColor color, CardRarity rarity, CardTarget target) {
        this(cardData, id, imagePath, cost, type, color, rarity, target, 0, 0, null);
    }

    @SafeVarargs
    public static <T> T[] array(T... items) {
        return EUIUtils.array(items);
    }

    public static PCLCard cast(AbstractCard card) {
        return EUIUtils.safeCast(card, PCLCard.class);
    }

    protected static int[] nums(int damage, int block) {
        return nums(damage, block, 0, 0);
    }

    protected static int[] nums(int damage, int block, int magicNumber, int secondaryValue) {
        return nums(damage, block, magicNumber, secondaryValue, 1);
    }

    protected static int[] nums(int damage, int block, int magicNumber, int secondaryValue, int hitCount) {
        return nums(damage, block, magicNumber, secondaryValue, hitCount, 1);
    }

    protected static int[] nums(int damage, int block, int magicNumber, int secondaryValue, int hitCount, int rightCount) {
        return new int[]{damage, block, magicNumber, secondaryValue, hitCount, rightCount};
    }

    protected static int[] nums(int damage, int block, int magicNumber) {
        return nums(damage, block, magicNumber, 0);
    }

    protected static PCLCardData register(Class<? extends PCLCard> type) {
        return register(type, PGR.core);
    }

    protected static PCLCardData register(Class<? extends PCLCard> type, PCLResources<?, ?, ?, ?> resources) {
        return registerCardData(new PCLCardData(type, resources));
    }

    protected static <T extends PCLCardData> T registerCardData(T cardData) {
        return PCLCardData.registerData(cardData);
    }

    protected static TemplateCardData registerTemplate(Class<? extends PCLCard> type, String sourceID) {
        return registerTemplate(type, PGR.core, sourceID);
    }

    protected static TemplateCardData registerTemplate(Class<? extends PCLCard> type, PCLResources<?, ?, ?, ?> resources, String sourceID) {
        return PCLCardData.registerData(new TemplateCardData(type, resources, sourceID));
    }

    protected static int[] ups(int damage, int block) {
        return ups(damage, block, 0, 0);
    }

    protected static int[] ups(int damage, int block, int magicNumber, int secondaryValue) {
        return ups(damage, block, magicNumber, secondaryValue, 0);
    }

    protected static int[] ups(int damage, int block, int magicNumber, int secondaryValue, int hitCount) {
        return ups(damage, block, magicNumber, secondaryValue, hitCount, 0);
    }

    protected static int[] ups(int damage, int block, int magicNumber, int secondaryValue, int hitCount, int rightCount) {
        return new int[]{damage, block, magicNumber, secondaryValue, hitCount, rightCount};
    }

    protected static int[] ups(int damage, int block, int magicNumber) {
        return ups(damage, block, magicNumber, 0);
    }

    protected PMove_StackCustomPower addApplyPower(PCLCardTarget target, int amount, PTrigger... effects) {
        PMove_StackCustomPower added = getApplyPower(target, amount, effects);
        getEffects().add(added);
        return added;
    }

    public void addAttackDisplay(AbstractPower p, float oldDamage, float tempDamage) {
        if (formulaDisplay != null && (baseDamage > 0 || onAttackEffect != null)) {
            formulaDisplay.addAttackPower(p, oldDamage, tempDamage);
        }
    }

    public void addAttackDisplay(PCLAffinity p, float oldDamage, float tempDamage) {
        if (formulaDisplay != null && (baseDamage > 0 || onAttackEffect != null)) {
            formulaDisplay.addAttackAffinity(p, oldDamage, tempDamage);
        }
    }

    public void addAttackDisplay(Texture tex, float oldDamage, float tempDamage) {
        if (formulaDisplay != null && (baseDamage > 0 || onAttackEffect != null)) {
            formulaDisplay.addAttackGeneric(tex, oldDamage, tempDamage);
        }
    }

    public void addAttackDisplay(float oldDamage, float tempDamage) {
        if (formulaDisplay != null && (baseDamage > 0 || onAttackEffect != null)) {
            formulaDisplay.addAttackGeneric(oldDamage, tempDamage);
        }
    }

    public void addAttackResult(float oldDamage, float tempDamage) {
        if (formulaDisplay != null && (baseDamage > 0 || onAttackEffect != null)) {
            formulaDisplay.setAttackResult(oldDamage, tempDamage);
        }
    }

    public void addAugment(PCLAugment augment) {
        addAugment(augment, true);
    }

    public void addAugment(PCLAugment augment, boolean save) {
        int slot = getFreeAugmentSlot();
        if (slot >= 0) {
            augments.set(slot, augment);
        }
        else {
            augments.add(augment);
        }
        if (save) {
            auxiliaryData.addAugment(augment.save, slot);
        }
        augment.onAddToCard(this);
        refresh(null);
    }

    public void addAugment(PCLAugment augment, int slot, boolean save) {
        if (slot >= 0) {
            augments.set(slot, augment);
            if (save) {
                auxiliaryData.addAugment(augment.save, slot);
            }
        }
        augment.onAddToCard(this);
        refresh(null);
    }

    protected PCardPrimary_GainBlock addBlockMove() {
        return addBlockMove(new PCardPrimary_GainBlock(this));
    }

    protected PCardPrimary_GainBlock addBlockMove(PCLCardTarget target) {
        return addBlockMove((PCardPrimary_GainBlock) new PCardPrimary_GainBlock(this).setTarget(target));
    }

    protected PCardPrimary_GainBlock addBlockMove(PCardPrimary_GainBlock skill) {
        onBlockEffect = skill;
        addUseMove(skill);
        return onBlockEffect;
    }

    protected PCardPrimary_DealDamage addDamageMove() {
        return addDamageMove(new PCardPrimary_DealDamage(this));
    }

    protected PCardPrimary_DealDamage addDamageMove(PCLAttackVFX attackEffect) {
        return addDamageMove(new PCardPrimary_DealDamage(this, attackEffect.key));
    }

    protected PCardPrimary_DealDamage addDamageMove(AbstractGameAction.AttackEffect attackEffect) {
        return addDamageMove(new PCardPrimary_DealDamage(this, attackEffect));
    }

    protected PCardPrimary_DealDamage addDamageMove(EffekseerEFK efx) {
        return addDamageMove(new PCardPrimary_DealDamage(this).setDamageEffect(efx));
    }

    protected PCardPrimary_DealDamage addDamageMove(EffekseerEFK efx, Color color) {
        return addDamageMove(new PCardPrimary_DealDamage(this).setDamageEffect(efx, color));
    }

    protected PCardPrimary_DealDamage addDamageMove(AbstractGameAction.AttackEffect attackEffect, EffekseerEFK efx) {
        return addDamageMove(new PCardPrimary_DealDamage(this, attackEffect).setDamageEffect(efx));
    }

    protected PCardPrimary_DealDamage addDamageMove(AbstractGameAction.AttackEffect attackEffect, EffekseerEFK efx, Color color) {
        return addDamageMove(new PCardPrimary_DealDamage(this, attackEffect).setDamageEffect(efx, color));
    }

    protected PCardPrimary_DealDamage addDamageMove(PCardPrimary_DealDamage skill) {
        onAttackEffect = skill;
        addUseMove(skill);
        return onAttackEffect;
    }

    public void addDefendDisplay(AbstractPower p, float oldDamage, float tempDamage) {
        if (formulaDisplay != null && (baseBlock > 0 || onBlockEffect != null)) {
            formulaDisplay.addDefendPower(p, oldDamage, tempDamage);
        }
    }

    public void addDefendDisplay(PCLAffinity p, float oldDamage, float tempDamage) {
        if (formulaDisplay != null && (baseBlock > 0 || onBlockEffect != null)) {
            formulaDisplay.addDefendAffinity(p, oldDamage, tempDamage);
        }
    }

    public void addDefendDisplay(Texture p, float oldDamage, float tempDamage) {
        if (formulaDisplay != null && (baseBlock > 0 || onBlockEffect != null)) {
            formulaDisplay.addDefendGeneric(p, oldDamage, tempDamage);
        }
    }

    public void addDefendDisplay(float oldDamage, float tempDamage) {
        if (formulaDisplay != null && (baseBlock > 0 || onBlockEffect != null)) {
            formulaDisplay.addDefendGeneric(oldDamage, tempDamage);
        }
    }

    public void addDefendResult(float oldDamage, float tempDamage) {
        if (formulaDisplay != null && (baseBlock > 0 || onBlockEffect != null)) {
            formulaDisplay.setDefendResult(oldDamage, tempDamage);
        }
    }

    public PMove_StackCustomPower addGainPower(PTrigger... effect) {
        return addApplyPower(PCLCardTarget.Self, -1, effect);
    }

    public PMove_StackCustomPower addGainPower(int amount, PTrigger... effect) {
        return addApplyPower(PCLCardTarget.Self, amount, effect);
    }

    public PSkill<?> addPowerMove(PSkill<?> effect) {
        PSkill<?> added = effect.setSource(this).onAddToCard(this);
        getPowerEffects().add(added);
        return added;
    }

    public PSpecialCond addSpecialCond(int descIndex, FuncT3<Boolean, PSpecialCond, PCLUseInfo, Boolean> onUse) {
        return addSpecialCond(descIndex, onUse, 1, 0);
    }

    public PSpecialCond addSpecialCond(int descIndex, FuncT3<Boolean, PSpecialCond, PCLUseInfo, Boolean> onUse, int amount, int extra) {
        PSpecialCond move = (PSpecialCond) getSpecialCond(descIndex, onUse, amount, extra).setSource(this).onAddToCard(this);
        getEffects().add(move);
        return move;
    }

    public PSpecialCond addSpecialCond(int descIndex, FuncT3<Boolean, PSpecialCond, PCLUseInfo, Boolean> onUse, int amount) {
        return addSpecialCond(descIndex, onUse, amount, 0);
    }

    public PSpecialSkill addSpecialMove(int descIndex, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse) {
        return addSpecialMove(cardData.strings.EXTENDED_DESCRIPTION[descIndex], onUse, 1, 0);
    }

    public PSpecialSkill addSpecialMove(String description, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, int amount, int extra) {
        PSpecialSkill move = (PSpecialSkill) getSpecialMove(description, onUse, amount, extra)
                .setSource(this)
                .setTarget(this.pclTarget)
                .onAddToCard(this);
        getEffects().add(move);
        return move;
    }

    public PSpecialSkill addSpecialMove(int descIndex, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, int amount) {
        return addSpecialMove(cardData.strings.EXTENDED_DESCRIPTION[descIndex], onUse, amount, 0);
    }

    public PSpecialSkill addSpecialMove(int descIndex, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, int amount, int extra) {
        return addSpecialMove(cardData.strings.EXTENDED_DESCRIPTION[descIndex], onUse, amount, extra);
    }

    public PSpecialSkill addSpecialMove(String description, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, int amount) {
        return addSpecialMove(description, onUse, amount, 0);
    }

    public PSpecialPowerSkill addSpecialPower(int descIndex, FuncT3<? extends AbstractPower, AbstractCreature, AbstractCreature, PSpecialPowerSkill> onUse) {
        return addSpecialPower(cardData.strings.EXTENDED_DESCRIPTION[descIndex], onUse, 1, 0);
    }

    public PSpecialPowerSkill addSpecialPower(String description, FuncT3<? extends AbstractPower, AbstractCreature, AbstractCreature, PSpecialPowerSkill> onUse, int amount, int extra) {
        PSpecialPowerSkill move = (PSpecialPowerSkill) getSpecialPower(description, onUse, amount, extra).setSource(this).onAddToCard(this);
        getEffects().add(move);
        return move;
    }

    public PSpecialPowerSkill addSpecialPower(int descIndex, FuncT3<? extends AbstractPower, AbstractCreature, AbstractCreature, PSpecialPowerSkill> onUse, int amount) {
        return addSpecialPower(cardData.strings.EXTENDED_DESCRIPTION[descIndex], onUse, amount, 0);
    }

    public PSpecialPowerSkill addSpecialPower(int descIndex, FuncT3<? extends AbstractPower, AbstractCreature, AbstractCreature, PSpecialPowerSkill> onUse, int amount, int extra) {
        return addSpecialPower(cardData.strings.EXTENDED_DESCRIPTION[descIndex], onUse, amount, extra);
    }

    public PSpecialPowerSkill addSpecialPower(String description, FuncT3<? extends AbstractPower, AbstractCreature, AbstractCreature, PSpecialPowerSkill> onUse, int amount) {
        return addSpecialPower(description, onUse, amount, 0);
    }

    public PSkill<?> addUseMove(PSkill<?> effect) {
        PSkill<?> added = effect.setSource(this).onAddToCard(this);
        getEffects().add(added);
        return added;
    }

    public PSkill<?> addUseMove(PSkill<?> primary, PSkill<?>... effects) {
        PSkill<?> added = PSkill.chain(primary, effects).setSource(this).onAddToCard(this);
        getEffects().add(added);
        return added;
    }

    @Override
    public void applyPowers() {
        calculateCardDamage(null);
    }

    @Override
    protected final void applyPowersToBlock() { /* Useless */ }

    @Override
    public PCLAttackType attackType() {
        return attackType;
    }

    @Override
    public int branchFactor() {
        return cardData.branchFactor;
    }

    // TODO only update when necessary (i.e. when phase or target changes)
    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        calculateCardDamage(mo, false);
    }

    public void calculateCardDamage(AbstractCreature mo, boolean isUsing) {
        if (isMultiDamage) {
            multiDamageCreatures = pclTarget.getTargets(getSourceCreature(), mo);
            multiDamage = new int[multiDamageCreatures.size()];

            int best = -PSkill.DEFAULT_MAX;
            for (int i = 0; i < multiDamage.length; i++) {
                refresh(multiDamageCreatures.get(i));
                multiDamage[i] = damage;

                if (damage > best) {
                    best = damage;
                }
            }

            if (best > 0) {
                damage = Math.max(0, MathUtils.floor(best));
                updateDamageVars();
            }
        }
        else {
            refresh(mo, isUsing);
        }
    }

    @Override
    public void calculateDamageDisplay(AbstractMonster mo) {
        calculateCardDamage(mo);
    }

    public boolean canRenderGlow() {
        return transparency >= 0.7f && owner == null;
    }

    public boolean canRenderTip() {
        return ReflectionHacks.getPrivate(this, AbstractCard.class, "renderTip");
    }

    @Override
    public boolean canUpgrade() {
        int max = maxUpgrades();
        return timesUpgraded < max || max < 0;
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        boolean init = cardPlayable(m) && this.hasEnoughEnergy();
        return CombatManager.canPlayCard(this, p, m, init);
    }

    @Override
    public boolean cardPlayable(AbstractMonster m) {
        cantUseMessage = PCLCard.UNPLAYABLE_MESSAGE;
        return isEffectPlayable(m) && super.cardPlayable(m);
    }

    public int changeForm(Integer form, int timesUpgraded) {
        return changeForm(form, this.timesUpgraded, timesUpgraded);
    }

    public int changeForm(Integer form, int prevUpgrade, int timesUpgraded) {

        // Preserve any changes made to the base numbers while in the original form
        int baseDamageDiff = baseDamage - (cardData.getDamage(this.auxiliaryData.form) + cardData.getDamageUpgrade(this.auxiliaryData.form) * prevUpgrade);
        int baseBlockDiff = baseBlock - (cardData.getBlock(this.auxiliaryData.form) + cardData.getBlockUpgrade(this.auxiliaryData.form) * prevUpgrade);
        int baseMagicNumberDiff = baseMagicNumber - (cardData.getMagicNumber(this.auxiliaryData.form) + cardData.getMagicNumberUpgrade(this.auxiliaryData.form) * prevUpgrade);
        int baseSecondaryValueDiff = baseHeal - (cardData.getHp(this.auxiliaryData.form) + cardData.getHpUpgrade(this.auxiliaryData.form) * prevUpgrade);
        int baseHitCountDiff = baseHitCount - (cardData.getHitCount(this.auxiliaryData.form) + cardData.getHitCountUpgrade(this.auxiliaryData.form) * prevUpgrade);
        int baseRightCountDiff = baseRightCount - (cardData.getRightCount(this.auxiliaryData.form) + cardData.getRightCountUpgrade(this.auxiliaryData.form) * prevUpgrade);

        // Preserve any changes made to the numbers for this turn while in the original form
        int tempDamageDiff = damage - baseDamage;
        int tempBlockDiff = block - baseBlock;
        int tempMagicNumberDiff = magicNumber - baseMagicNumber;
        int tempSecondaryValueDiff = heal - baseHeal;
        int tempHitCountDiff = hitCount - baseHitCount;
        int tempRightCountDiff = rightCount - baseRightCount;

        int baseCost = cardData.getCost(this.auxiliaryData.form);
        int costDiff = cost - ((baseCost + cardData.getCostUpgrade(this.auxiliaryData.form) * prevUpgrade));


        form = MathUtils.clamp(form, 0, this.maxForms() - 1);

        onFormChange(form, timesUpgraded);
        setNumbers(baseDamageDiff + cardData.getDamage(form) + cardData.getDamageUpgrade(form) * timesUpgraded,
                baseBlockDiff + cardData.getBlock(form) + cardData.getBlockUpgrade(form) * timesUpgraded,
                baseMagicNumberDiff + cardData.getMagicNumber(form) + cardData.getMagicNumberUpgrade(form) * timesUpgraded,
                baseSecondaryValueDiff + cardData.getHp(form) + cardData.getHpUpgrade(form) * timesUpgraded,
                baseHitCountDiff + cardData.getHitCount(form) + cardData.getHitCountUpgrade(form) * timesUpgraded,
                baseRightCountDiff + cardData.getRightCount(form) + cardData.getRightCountUpgrade(form) * timesUpgraded);

        damage += tempDamageDiff;
        block += tempBlockDiff;
        magicNumber += tempMagicNumberDiff;
        heal += tempSecondaryValueDiff;
        hitCount += tempHitCountDiff;
        rightCount += tempRightCountDiff;

        // Ensure HP is set to the base health
        currentHealth = heal;

        upgradedDamage = baseDamage > cardData.getDamage(form);
        upgradedBlock = baseBlock > cardData.getBlock(form);
        upgradedMagicNumber = baseMagicNumber > cardData.getMagicNumber(form);
        upgradedHeal = baseHeal > cardData.getHp(form);
        upgradedHitCount = baseHitCount > cardData.getHitCount(form);
        upgradedRightCount = baseRightCount > cardData.getRightCount(form);

        int newCost = cardData.getCost(form) + cardData.getCostUpgrade(form) * timesUpgraded;
        setCost(newCost + costDiff);
        upgradedCost = cost < cardData.getCost(form);
        affinities.applyUpgrades(cardData.affinities, auxiliaryData.form, prevUpgrade, timesUpgraded);

        return setForm(form, timesUpgraded);
    }

    protected Texture createPopupTexture() {
        return EUIRM.createTexture(Gdx.files.internal(assetUrl), true, false);
    }

    @Override
    public void displayUpgrades() {
        isCostModified = upgradedCost;
        isMagicNumberModified = upgradedMagicNumber;
        isHealModified = upgradedHeal;
        isDamageModified = upgradedDamage;
        isBlockModified = upgradedBlock;
        isRightCountModified = upgradedRightCount;

        if (isDamageModified) {
            damage = baseDamage;
        }
        if (isBlockModified) {
            block = baseBlock;
        }
        if (isMagicNumberModified) {
            magicNumber = baseMagicNumber;
        }
        if (isHealModified) {
            heal = baseHeal;
        }

        displayUpgradesForSkills(true);

        // Force refresh the descriptions, affinities, and augments
        initializeDescription();
        affinities.displayUpgrades(previousAffinities);
    }

    public void displayUpgradesForSkills(boolean value) {
        for (PSkill<?> ef : getEffects()) {
            ef.displayUpgrades(value);
        }
        for (PSkill<?> ef : getPowerEffects()) {
            ef.displayUpgrades(value);
        }
    }

    public void fillPreviews(RotatingList<EUIPreview> list) {
        PointerProvider.fillPreviewsForKeywordProvider(this, list);
    }

    public void fullReset() {
        this.clearSkills();
        for (PCLCardTag tag : PCLCardTag.values()) {
            tag.set(this, 0);
        }
        this.onAttackEffect = null;
        this.onBlockEffect = null;

        setupProperties(cardData, this.auxiliaryData.form, timesUpgraded);
        setup(null);
        setForm(this.auxiliaryData.form, timesUpgraded);
        refresh(null);
    }

    protected PMove_StackCustomPower getApplyPower(PCLCardTarget target, int amount, PTrigger... effects) {
        Integer[] powerIndexes = EUIUtils.range(getPowerEffects().size(), getPowerEffects().size() + effects.length - 1);
        for (PTrigger effect : effects) {
            addPowerMove(effect);
        }
        return (PMove_StackCustomPower) new PMove_StackCustomPower(target, amount, powerIndexes).setSource(this).onAddToCard(this);
    }

    public PCLAugment getAugment(int index) {
        return augments.size() > index ? augments.get(index) : null;
    }

    public ArrayList<PSkill<?>> getAugmentSkills() {
        return EUIUtils.flatMap(augments, aug -> aug != null ? aug.skills.onUseEffects : Collections.emptyList());
    }

    public ArrayList<PCLAugment> getAugments() {
        return EUIUtils.filter(augments, Objects::nonNull);
    }

    public TextureAtlas.AtlasRegion getBorderTexture() {
        return ImageMaster.CARD_ATTACK_BG_SILHOUETTE;
    }

    protected String getBottomText() {
        String loadoutName = cardData.getLoadoutName();
        return (loadoutName == null || loadoutName.isEmpty()) ? null : loadoutName;
    }

    @Override
    public PCLCard getCachedUpgrade() {
        PCLCard upgrade = cardData.tempCard;

        if (upgrade == null || upgrade.uuid != this.uuid || (upgrade.timesUpgraded != (timesUpgraded + 1))) {
            upgrade = cardData.tempCard = makeUpgradePreview(auxiliaryData.form);
        }

        return upgrade;
    }

    protected Texture getCardAttributeBanner() {
        if (shouldUsePCLFrame()) {
            if (rarity == PCLEnum.CardRarity.LEGENDARY || rarity == PCLEnum.CardRarity.SECRET) {
                return (isPopup ? PCLCoreImages.CardUI.cardBannerAttribute2L : PCLCoreImages.CardUI.cardBannerAttribute2).texture();
            }
            return (isPopup ? PCLCoreImages.CardUI.cardBannerAttributeL : PCLCoreImages.CardUI.cardBannerAttribute).texture();
        }
        return (isPopup ? PCLCoreImages.CardUI.cardBannerAttributeVanillaL : PCLCoreImages.CardUI.cardBannerAttributeVanilla).texture();
    }

    protected Texture getCardBackground() {
        PCLResources<?, ?, ?, ?> resources = PGR.getResources(color);
        if (resources == null || resources.images == null) {
            resources = PGR.core;
        }

        if (isSummon()) {
            return isPopup ? resources.images.cardBackgroundSummonL.texture() : resources.images.cardBackgroundSummon.texture();
        }
        switch (type) {
            case ATTACK:
                return isPopup ? resources.images.cardBackgroundAttackL.texture() : resources.images.cardBackgroundAttack.texture();
            case POWER:
                return isPopup ? resources.images.cardBackgroundPowerL.texture() : resources.images.cardBackgroundPower.texture();
            default:
                return isPopup ? resources.images.cardBackgroundSkillL.texture() : resources.images.cardBackgroundSkill.texture();
        }
    }

    protected Texture getCardBanner() {
        if (shouldUsePCLFrame()) {
            if (rarity == PCLEnum.CardRarity.LEGENDARY || rarity == PCLEnum.CardRarity.SECRET) {
                return (isPopup ? PCLCoreImages.CardUI.cardBanner2L : PCLCoreImages.CardUI.cardBanner2).texture();
            }
            return (isPopup ? PCLCoreImages.CardUI.cardBannerL : PCLCoreImages.CardUI.cardBanner).texture();
        }
        return null;
    }

    protected TextureAtlas.AtlasRegion getCardBannerVanillaRegion() {
        if (isPopup) {
            switch (rarity) {
                case RARE:
                    return ImageMaster.CARD_BANNER_RARE_L;
                case UNCOMMON:
                    return ImageMaster.CARD_BANNER_UNCOMMON_L;
                default:
                    return ImageMaster.CARD_BANNER_COMMON_L;
            }
        }
        else {
            switch (rarity) {
                case RARE:
                    return ImageMaster.CARD_BANNER_RARE;
                case UNCOMMON:
                    return ImageMaster.CARD_BANNER_UNCOMMON;
                default:
                    return ImageMaster.CARD_BANNER_COMMON;
            }
        }
    }

    // For PCL cards, always use the skill silhouette because its cards are all rectangular
    @Override
    public TextureAtlas.AtlasRegion getCardBgAtlas() {
        if (shouldUsePCLFrame() || type == PCLEnum.CardType.BLESSING ) {
            return ImageMaster.CARD_SKILL_BG_SILHOUETTE;
        }
        TextureAtlas.AtlasRegion reg = super.getCardBgAtlas();
        return reg != null ? reg : ImageMaster.CARD_POWER_BG_SILHOUETTE;
    }

    public PCardPrimary_GainBlock getCardBlock() {
        return onBlockEffect;
    }

    public PCardPrimary_DealDamage getCardDamage() {
        return onAttackEffect;
    }

    protected boolean getDarken() {
        return ReflectionHacks.getPrivate(this, AbstractCard.class, "darken");
    }

    public String getDescriptionForSort() {
        return CardModifierManager.onCreateDescription(this, makeExportString(getEffectStrings()));
    }

    @Override
    public String getEffectPowerTextStrings() {
        return EUIUtils.joinStringsMapNonnull(PGR.config.removeLineBreaks.get() ? " " : EUIUtils.DOUBLE_SPLIT_LINE,
                ef -> ef != null && !(ef.isPassiveOnly() && ef.source != this) ? StringUtils.capitalize(ef.getPowerTextForDisplay(null)) : null,
                getFullEffects());
    }

    @Override
    public String getEffectStrings() {
        return EUIUtils.joinStringsMapNonnull(PGR.config.removeLineBreaks.get() ? " " : EUIUtils.DOUBLE_SPLIT_LINE,
                ef -> ef != null && !(ef.isPassiveOnly() && ef.source != this) ? StringUtils.capitalize(ef.getTextForDisplay()) : null,
                getFullEffects());
    }

    protected Texture getEnergyOrb() {
        // For non-custom cards, use the original resource card color so that colorless/curses have their resource's energy orb
        PCLResources<?, ?, ?, ?> resources = PGR.getResources(this instanceof PCLDynamicCard ? color : cardData.resources.cardColor);
        if (resources == null || resources.images == null) {
            resources = PGR.core;
        }
        return (isPopup ? resources.images.cardEnergyOrbL : resources.images.cardEnergyOrb).texture();
    }

    @Override
    public int getForm() {
        return auxiliaryData.form;
    }

    public int getFreeAugmentSlot() {
        for (int i = 0; i < augments.size(); i++) {
            if (augments.get(i) == null) {
                return i;
            }
        }
        return -1;
    }

    public ArrayList<PSkill<?>> getFullEffects() {
        ArrayList<PSkill<?>> result = new ArrayList<>();
        result.addAll(getEffects());
        result.addAll(getAugmentSkills());
        return result;
    }

    protected TextureCache getHPIcon() {
        return isPopup ? PCLCoreImages.CardIcons.hpL : PCLCoreImages.CardIcons.hp;
    }

    public String getHPString() {
        return String.valueOf(currentHealth) + "/" + heal;
    }

    protected Color getHPStringColor() {
        return currentHealth < heal ? Settings.RED_TEXT_COLOR : isHealModified || heal > baseHeal ? Settings.GREEN_TEXT_COLOR : Settings.CREAM_COLOR;
    }

    public String getID() {
        return cardID;
    }

    public String getName() {
        return name;
    }

    public String getNameForSort() {
        return name;
    }

    protected Texture getPortraitFrame() {
        if (shouldUsePCLFrame()) {
            if (isSummon()) {
                return isPopup ? PCLCoreImages.CardUI.cardFrameSummonL.texture() : PCLCoreImages.CardUI.cardFrameSummon.texture();
            }
            switch (type) {
                case ATTACK:
                    return isPopup ? PCLCoreImages.CardUI.cardFrameAttackL.texture() : PCLCoreImages.CardUI.cardFrameAttack.texture();
                case POWER:
                    return isPopup ? PCLCoreImages.CardUI.cardFramePowerL.texture() : PCLCoreImages.CardUI.cardFramePower.texture();
                default:
                    return isPopup ? PCLCoreImages.CardUI.cardFrameSkillL.texture() : PCLCoreImages.CardUI.cardFrameSkill.texture();
            }
        }
        return null;
    }

    protected TextureAtlas.AtlasRegion getPortraitFrameVanillaRegion() {
        if (isSummon()) {
            if (isPopup) {
                switch (rarity) {
                    case RARE:
                        return ImageMaster.CARD_FRAME_POWER_RARE_L;
                    case UNCOMMON:
                        return ImageMaster.CARD_FRAME_POWER_UNCOMMON_L;
                    default:
                        return ImageMaster.CARD_FRAME_POWER_COMMON_L;
                }
            }
            else {
                switch (rarity) {
                    case RARE:
                        return ImageMaster.CARD_FRAME_POWER_RARE;
                    case UNCOMMON:
                        return ImageMaster.CARD_FRAME_POWER_UNCOMMON;
                    default:
                        return ImageMaster.CARD_FRAME_POWER_COMMON;
                }
            }
        }
        else if (isPopup) {
            switch (type) {
                case ATTACK:
                    switch (rarity) {
                        case RARE:
                            return ImageMaster.CARD_FRAME_ATTACK_RARE_L;
                        case UNCOMMON:
                            return ImageMaster.CARD_FRAME_ATTACK_UNCOMMON_L;
                        default:
                            return ImageMaster.CARD_FRAME_ATTACK_COMMON_L;
                    }
                case POWER:
                    switch (rarity) {
                        case RARE:
                            return ImageMaster.CARD_FRAME_POWER_RARE_L;
                        case UNCOMMON:
                            return ImageMaster.CARD_FRAME_POWER_UNCOMMON_L;
                        default:
                            return ImageMaster.CARD_FRAME_POWER_COMMON_L;
                    }
                default:
                    switch (rarity) {
                        case RARE:
                            return ImageMaster.CARD_FRAME_SKILL_RARE_L;
                        case UNCOMMON:
                            return ImageMaster.CARD_FRAME_SKILL_UNCOMMON_L;
                        default:
                            return ImageMaster.CARD_FRAME_SKILL_COMMON_L;
                    }
            }
        }
        else {
            switch (type) {
                case ATTACK:
                    switch (rarity) {
                        case RARE:
                            return ImageMaster.CARD_FRAME_ATTACK_RARE;
                        case UNCOMMON:
                            return ImageMaster.CARD_FRAME_ATTACK_UNCOMMON;
                        default:
                            return ImageMaster.CARD_FRAME_ATTACK_COMMON;
                    }
                case POWER:
                    switch (rarity) {
                        case RARE:
                            return ImageMaster.CARD_FRAME_POWER_RARE;
                        case UNCOMMON:
                            return ImageMaster.CARD_FRAME_POWER_UNCOMMON;
                        default:
                            return ImageMaster.CARD_FRAME_POWER_COMMON;
                    }
                default:
                    switch (rarity) {
                        case RARE:
                            return ImageMaster.CARD_FRAME_SKILL_RARE;
                        case UNCOMMON:
                            return ImageMaster.CARD_FRAME_SKILL_UNCOMMON;
                        default:
                            return ImageMaster.CARD_FRAME_SKILL_COMMON;
                    }
            }
        }
    }

    public Texture getPortraitImageTexture() {
        return portraitImg.texture;
    }

    protected TextureCache getPriorityIcon() {
        return isPopup ?
                (timing.movesBeforePlayer() ? PCLCoreImages.CardIcons.priorityPlusL : PCLCoreImages.CardIcons.priorityMinusL)
                : (timing.movesBeforePlayer() ? PCLCoreImages.CardIcons.priorityPlus : PCLCoreImages.CardIcons.priorityMinus);
    }

    public Color getRarityColor() {
        if (rarity == PCLEnum.CardRarity.SECRET) {
            return COLOR_SECRET;
        }
        else if (rarity == PCLEnum.CardRarity.LEGENDARY) {
            return COLOR_ULTRA_RARE;
        }

        return EUIGameUtils.colorForRarity(rarity);
    }

    public Color getRarityTextColor() {
        switch (rarity) {
            case BASIC:
            case CURSE:
                return Color.WHITE;
        }

        return CARD_TYPE_COLOR;
    }

    public Color getRarityVanillaColor() {
        switch (rarity) {
            case COMMON:
            case UNCOMMON:
            case RARE:
                return EUIColors.white(transparency);
        }
        return getRarityColor();
    }

    protected Color getRenderColor() {
        return ReflectionHacks.getPrivate(this, AbstractCard.class, "renderColor");
    }

    public PSkillContainer getSkills() {
        return skills;
    }

    public AbstractCreature getSourceCreature() {
        return owner != null ? owner : AbstractDungeon.player;
    }

    protected PSpecialCond getSpecialCond(int descIndex, FuncT3<Boolean, PSpecialCond, PCLUseInfo, Boolean> onUse, int amount, int extra) {
        return new PSpecialCond(cardData, descIndex, onUse, amount, extra);
    }

    protected PSpecialCond getSpecialCond(int descIndex, FuncT3<Boolean, PSpecialCond, PCLUseInfo, Boolean> onUse, int amount) {
        return getSpecialCond(descIndex, onUse, amount, 0);
    }

    protected PSpecialSkill getSpecialMove(String description, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, int amount, int extra) {
        return new PSpecialSkill(this.cardID + this.getEffects().size(), description, onUse, amount, extra);
    }

    protected PSpecialSkill getSpecialMove(int descIndex, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse) {
        return getSpecialMove(cardData.strings.EXTENDED_DESCRIPTION[descIndex], onUse, 1, 0);
    }

    protected PSpecialSkill getSpecialMove(int descIndex, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, int amount) {
        return getSpecialMove(cardData.strings.EXTENDED_DESCRIPTION[descIndex], onUse, amount, 0);
    }

    protected PSpecialSkill getSpecialMove(int descIndex, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, int amount, int extra) {
        return getSpecialMove(cardData.strings.EXTENDED_DESCRIPTION[descIndex], onUse, amount, extra);
    }

    protected PSpecialSkill getSpecialMove(FuncT1<String, PSpecialSkill> strFunc, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, int amount) {
        return getSpecialMove(strFunc, onUse, amount, 0);
    }

    protected PSpecialSkill getSpecialMove(FuncT1<String, PSpecialSkill> strFunc, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, int amount, int extra) {
        return new PSpecialSkill(this.cardID + this.getEffects().size(), strFunc, onUse, amount, extra);
    }

    protected PSpecialPowerSkill getSpecialPower(String description, FuncT3<? extends AbstractPower, AbstractCreature, AbstractCreature, PSpecialPowerSkill> onUse, int amount, int extra) {
        return getSpecialPower(description, PCLCardTarget.Self, onUse, amount, extra);
    }

    protected PSpecialPowerSkill getSpecialPower(String description, PCLCardTarget target, FuncT3<? extends AbstractPower, AbstractCreature, AbstractCreature, PSpecialPowerSkill> onUse, int amount, int extra) {
        return new PSpecialPowerSkill(this.cardID + this.getEffects().size(), description, target, onUse, amount, extra);
    }

    protected PSpecialPowerSkill getSpecialPower(int descIndex, FuncT3<? extends AbstractPower, AbstractCreature, AbstractCreature, PSpecialPowerSkill> onUse) {
        return getSpecialPower(cardData.strings.EXTENDED_DESCRIPTION[descIndex], onUse, 1, 0);
    }

    protected PSpecialPowerSkill getSpecialPower(int descIndex, FuncT3<? extends AbstractPower, AbstractCreature, AbstractCreature, PSpecialPowerSkill> onUse, int amount) {
        return getSpecialPower(cardData.strings.EXTENDED_DESCRIPTION[descIndex], onUse, amount, 0);
    }

    protected PSpecialPowerSkill getSpecialPower(int descIndex, FuncT3<? extends AbstractPower, AbstractCreature, AbstractCreature, PSpecialPowerSkill> onUse, int amount, int extra) {
        return getSpecialPower(cardData.strings.EXTENDED_DESCRIPTION[descIndex], onUse, amount, extra);
    }

    @Override
    public TargetFilter getTargetFilter() {
        return pclTarget.getTargetFilter();
    }

    @Override
    public List<EUIKeywordTooltip> getTips() {
        return tooltips;
    }

    @Override
    public List<EUIKeywordTooltip> getTipsForRender() {
        ArrayList<EUIKeywordTooltip> dynamicTooltips = new ArrayList<>();

        if (controller != null) {
            EUIKeywordTooltip tip = controller.getTooltip();
            if (tip != null) {
                dynamicTooltips.add(tip);
            }
        }

        // Only show these tooltips outside of combat
        if (!CombatManager.inBattle() || isPopup || (AbstractDungeon.player != null && AbstractDungeon.player.masterDeck.contains(this))) {
            if (isSoulbound()) {
                dynamicTooltips.add(PGR.core.tooltips.soulbound);
            }
            if (cardData.canToggleFromPopup) {
                dynamicTooltips.add(PGR.core.tooltips.multiform);
            }
            if (isUnique()) {
                dynamicTooltips.add(PGR.core.tooltips.unique);
            }
        }

        // Add appropriate summon timing tooltip
        if (isSummon()) {
            if (timing.movesBeforePlayer()) {
                dynamicTooltips.add(PGR.core.tooltips.turnStart);
            }
            else {
                dynamicTooltips.add(PGR.core.tooltips.turnEnd);
            }
        }

        // Add tips from tags
        for (PCLCardTag tag : PCLCardTag.getAll()) {
            if (tag.has(this)) {
                dynamicTooltips.add(tag.getTooltip());
            }
        }

        for (EUIKeywordTooltip tip : tooltips) {
            if (!dynamicTooltips.contains(tip)) {
                dynamicTooltips.add(tip);
            }
        }

        // Add tips from modifiers
        for (AbstractCardModifier modifier : CardModifierManager.modifiers(this)) {
            List<TooltipInfo> tooltips = modifier.additionalTooltips(this);
            if (tooltips != null) {
                for (TooltipInfo info : tooltips) {
                    dynamicTooltips.add(new EUIKeywordTooltip(info.title, GameUtilities.sanitizePowerDescription(info.description)));
                }
            }
        }

        return dynamicTooltips;
    }

    protected Color getTypeColor() {
        return ReflectionHacks.getPrivate(this, AbstractCard.class, "typeColor");
    }

    public Texture getTypeIcon() {
        return EUIGameUtils.iconForType(type).texture();
    }

    protected String getTypeText() {
        return EUIGameUtils.textForType(this.type);
    }

    // Upgrade name is determined by number of upgrades and the current form (if multiple exist)
    protected String getUpgradeName() {
        // In case cardData is somehow null, return a generic name
        String name = cardData != null && cardData.strings != null ? cardData.strings.NAME : this.name != null ? this.name : EUIUtils.EMPTY_STRING;
        if (upgraded) {
            if (cardData != null) {
                name = GameUtilities.getMultiformName(name, auxiliaryData.form, timesUpgraded, maxForms(), maxUpgrades(), cardData.branchFactor);
            }
            else {
                name = GameUtilities.getMultiformName(name, auxiliaryData.form, timesUpgraded, 1, maxUpgrades(), 0);
            }
        }

        return name;
    }

    @Override
    public int getXValue() {
        return misc;
    }

    public int hitCount() {
        return hitCount;
    }

    public int hitCountBase() {
        return baseHitCount;
    }

    @Override
    public void hover() {
        super.hover();
        if (!this.hovered) {
            this.drawScale = 1.0F;
            this.targetDrawScale = 1.0F;
        }

        this.hovered = true;

        if (AbstractDungeon.player != null && AbstractDungeon.player.hand.contains(this)) {
            if (hb.justHovered) {
                triggerOnGlowCheck();
            }
            if (controller != null && hb.hovered && EUIInputManager.rightClick.isJustPressed()) {
                controller.targetToUse(1);
            }
        }
    }

    @Override
    public void initializeDescription() {
        if (cardText != null) {
            this.cardText.forceReinitialize();
        }
    }

    // Don't use this, use initializeDescription instead
    @Deprecated
    @Override
    public final void initializeDescriptionCN() {
        initializeDescription();
    }

    // Just use originalName to cache the card modded title
    // Reset name back to original after initializeTitle to get the correct width
    public void initializeName() {
        String temp = getUpgradeName();
        name = originalName = CardModifierManager.onRenderTitle(this, temp);
        initializeTitle();
        name = temp;
    }

    public boolean isAoE() {
        return isMultiDamage;
    }

    public boolean isBranchingUpgrade() {
        return maxForms() > 1 && cardData.canToggleOnUpgrade;
    }

    protected boolean isEffectPlayable(AbstractMonster m) {
        PCLUseInfo info = CombatManager.playerSystem.getInfo(this, getSourceCreature(), m);
        for (PSkill<?> be : getFullEffects()) {
            if (!be.canPlay(info, null, true)) {
                return false;
            }
        }
        return true;
    }

    public boolean isMultiUpgrade() {
        int max = maxUpgrades();
        return max < 0 || max > 1;
    }

    public boolean isOnScreen() {
        return current_y >= -200f * Settings.scale && current_y <= Settings.HEIGHT + 200f * Settings.scale;
    }

    @Override
    public boolean isPopup() {
        return isPopup;
    }

    public boolean isSoulbound() {
        return SoulboundField.soulbound.get(this);
    }

    public boolean isStarter() {
        return GameUtilities.isStarter(this);
    }

    public boolean isSummon() {
        return type == PCLEnum.CardType.SUMMON;
    }

    public boolean isUnique() {
        return cardData.unique;
    }

    public void loadImage(String path) {
        Texture t = EUIRM.getTexture(path, true, PGR.config.lowVRAM.get());
        if (t == null) {
            path = QuestionMark.DATA.imagePath;
            t = EUIRM.getTexture(path, true, PGR.config.lowVRAM.get());
        }
        assetUrl = path;
        portraitImg = new ColoredTexture(t, null);
    }

    public final void loadSingleCardView() {
        if (PGR.config.lowVRAM.get() || portraitImg.getWidth() < 400) { // Half-size portraits are 250, but they won't be reloaded until game reset. Also, replacement cards will have smaller portraits
            portraitImgBackup = portraitImg;
            portraitImg = new ColoredTexture(createPopupTexture());
        }
    }

    @Override
    public PCLCard makeCopy() {
        return cardData.create(0, 0);
    }

    protected PCLCard makeCopyProperties(PCLCard copy) {
        // Modifiers and augments are copied first to avoid incorrect numbers in previews
        for (PCLAugment augment : getAugments()) {
            copy.addAugment(augment.makeCopy());
        }

        // Only copy modifiers if they exist, to avoid unnecessary text re-initialization
        if (!CardModifierManager.modifiers(this).isEmpty()) {
            CardModifierManager.copyModifiers(this, copy, false, true, false);
        }

        for (AbstractBlockModifier mod : BlockModifierManager.modifiers(this)) {
            if (!mod.isInherent()) {
                BlockModifierManager.addModifier(copy, mod);
            }
        }
        for (AbstractDamageModifier mod : DamageModifierManager.modifiers(this)) {
            if (!mod.isInherent()) {
                DamageModifierManager.addModifier(copy, mod);
            }
        }

        copy.auxiliaryData = new PCLCardSaveData(auxiliaryData);
        copy.target = this.target;
        copy.upgradeLevelIncrease = this.upgradeLevelIncrease;
        copy.upgraded = this.upgraded;
        copy.timesUpgraded = this.timesUpgraded;
        copy.baseDamage = this.baseDamage;
        copy.baseBlock = this.baseBlock;
        copy.baseMagicNumber = this.baseMagicNumber;
        copy.cost = this.cost;
        copy.costForTurn = this.costForTurn;
        copy.isCostModified = this.isCostModified;
        copy.isCostModifiedForTurn = this.isCostModifiedForTurn;
        copy.inBottleLightning = this.inBottleLightning;
        copy.inBottleFlame = this.inBottleFlame;
        copy.inBottleTornado = this.inBottleTornado;
        copy.isSeen = this.isSeen;
        copy.isLocked = this.isLocked;
        copy.misc = this.misc;
        copy.freeToPlayOnce = this.freeToPlayOnce;
        copy.exhaustOnUseOnce = exhaustOnUseOnce;
        for (PCLCardTag tag : PCLCardTag.getAll()) {
            tag.set(copy, tag.getInt(this));
        }

        copy.affinities.initialize(affinities);
        copy.magicNumber = magicNumber;
        copy.isMagicNumberModified = isMagicNumberModified;

        copy.currentHealth = currentHealth;
        copy.heal = heal;
        copy.baseHeal = baseHeal;
        copy.isHealModified = isHealModified;

        copy.hitCount = hitCount;
        copy.baseHitCount = baseHitCount;
        copy.isHitCountModified = isHitCountModified;

        copy.rightCount = rightCount;
        copy.baseRightCount = baseRightCount;
        copy.isRightCountModified = isRightCountModified;

        copy.tags.clear();
        copy.tags.addAll(tags);
        copy.originalName = this.originalName;
        copy.name = this.name;

        copy.initializeDescription();
        return copy;
    }

    public PCLCard makePopupCopy() {
        PCLCard copy = makeStatEquivalentCopy();
        copy.current_x = (float) Settings.WIDTH / 2f;
        copy.current_y = (float) Settings.HEIGHT / 2f;
        copy.drawScale = copy.targetDrawScale = 2f;
        copy.isPopup = true;
        return copy;
    }

    public String makePowerString() {
        return makePowerString(getEffectStrings());
    }

    public PCLCard makeSetAugmentPreview(PCLAugment augment) {
        PCLCard upgrade = (PCLCard) this.makeSameInstanceOf();
        upgrade.addAugment(augment.makeCopy(), false);
        upgrade.isPreview = true;
        return upgrade;
    }

    // Custom implementation to avoid unnecessary text re-initializations
    @Override
    public PCLCard makeStatEquivalentCopy() {
        PCLCard copy = cardData.create(auxiliaryData.form, timesUpgraded);
        return makeCopyProperties(copy);
    }

    public PCLCard makeUpgradePreview(int form) {
        PCLCard upgrade = (PCLCard) this.makeSameInstanceOf();
        upgrade.previousAffinities = new ArrayList<>(upgrade.affinities.sorted);
        upgrade.changeForm(form, timesUpgraded + 1);
        upgrade.isPreview = true;
        upgrade.displayUpgrades();
        return upgrade;
    }

    @Override
    public int maxForms() {
        return cardData != null ? cardData.maxForms : 1;
    }

    @Override
    public int maxUpgrades() {
        return (cardData != null ? cardData.maxUpgradeLevel : 1) + upgradeLevelIncrease;
    }

    protected float modifyBlock(PCLUseInfo info, float amount) {
        for (PSkill<?> be : getFullEffects()) {
            amount = be.modifyBlockFirst(info, amount);
        }
        return amount;
    }

    protected float modifyBlockLast(PCLUseInfo info, float amount) {
        for (PSkill<?> be : getFullEffects()) {
            amount = be.modifyBlockLast(info, amount);
        }
        return amount;
    }

    protected int modifyCost(PCLUseInfo info, int amount) {
        for (PSkill<?> be : getFullEffects()) {
            amount = be.modifyCost(info, amount);
        }
        return amount;
    }

    protected float modifyDamage(PCLUseInfo info, float amount) {
        for (PSkill<?> be : getFullEffects()) {
            amount = be.modifyDamageGiveFirst(info, amount);
        }
        return amount;
    }

    protected float modifyDamageLast(PCLUseInfo info, float amount) {
        for (PSkill<?> be : getFullEffects()) {
            amount = be.modifyDamageGiveLast(info, amount);
        }
        return amount;
    }

    protected float modifyHitCount(PCLUseInfo info, float amount) {
        for (PSkill<?> be : getFullEffects()) {
            amount = be.modifyHitCount(info, amount);
        }
        return amount;
    }

    protected float modifyMagicHeal(PCLUseInfo info, float amount) {
        for (PSkill<?> be : getFullEffects()) {
            amount = be.modifyHeal(info, amount);
        }
        return amount;
    }

    protected float modifyRightCount(PCLUseInfo info, float amount) {
        for (PSkill<?> be : getFullEffects()) {
            amount = be.modifyRightCount(info, amount);
        }
        return amount;
    }

    @Override
    public boolean onAddToDeck() {
        doEffects(PSkill::triggerOnObtain);
        return true;
    }

    public void onDrag(AbstractMonster m) {
        doEffects(be -> be.onDrag(m));
    }

    protected void onFormChange(Integer form, int timesUpgraded) {

    }

    @Override
    public void onLoad(PCLCardSaveData data) {
        if (data != null) {
            changeForm(data.form, timesUpgraded);
            this.auxiliaryData = new PCLCardSaveData(data);
            if (data.augments != null) {
                for (PCLAugment.SaveData dat : data.augments) {
                    if (dat != null) {
                        PCLAugmentData augment = PCLAugmentData.getStaticDataOrCustom(dat.ID);
                        if (augment != null) {
                            addAugment(augment.create(dat.form, dat.timesUpgraded), false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onRemoveFromMasterDeck() {
        super.onRemoveFromMasterDeck();
        doEffects(PSkill::triggerOnRemoveFromInventory);
        for (PCLAugment augment : getAugments()) {
            if (augment.canRemove()) {
                PGR.dungeon.addAugment(augment.save);
            }
        }
    }

    @Override
    public void onRetained() {
        super.onRetained();
        doEffects(be -> be.triggerOnRetain(this));
    }

    @Override
    public PCLCardSaveData onSave() {
        return auxiliaryData;
    }

    protected void onUpgrade() {
    }

    public void onUse(PCLUseInfo info) {
        if (!dontTriggerOnUseCard) {
            doEffects(be -> be.use(info, PCLActions.bottom));
        }
    }

    @Override
    public PCLCardTarget pclTarget() {
        return pclTarget;
    }

    public void refresh(AbstractCreature enemy) {
        refresh(enemy, false);
    }

    public void refresh(AbstractCreature enemy, boolean isUsing) {
        AbstractCreature owner = getSourceCreature();
        PCLUseInfo info = CombatManager.playerSystem.getInfo(this, owner, enemy);
        refreshImpl(info, isUsing);
    }

    // Update damage, block, and magic number from the powers on a given target
    // Every step of the calculation is recorded for display in the damage formula widget
    public void refreshImpl(PCLUseInfo info, boolean isUsing) {
        AbstractCreature owner = info.source;
        AbstractCreature enemy = info.target;
        doEffects(be -> be.refresh(info, true, isUsing));
        AbstractMonster asEnemy = GameUtilities.asMonster(enemy);

        boolean applyEnemyPowers = (enemy != null && !GameUtilities.isDeadOrEscaped(enemy));
        float tempBlock = CardModifierManager.onModifyBaseBlock(baseBlock, this);
        float tempDamage = CardModifierManager.onModifyBaseDamage(baseDamage, this, asEnemy);
        float tempHitCount = CombatManager.onModifyHitCount(baseHitCount, this);
        float tempRightCount = CombatManager.onModifyRightCount(baseRightCount, this);
        tempDamage = modifyDamage(info, tempDamage);
        tempBlock = modifyBlock(info, tempBlock);
        float oldBlock = tempBlock;
        float oldDamage = tempDamage;

        // Do not update damage display for summons in your hand
        if (owner != null && (type != PCLEnum.CardType.SUMMON || owner != AbstractDungeon.player)) {
            int applyCount = attackType == PCLAttackType.Brutal ? 2 : 1;

            if (owner instanceof AbstractPlayer) {
                for (AbstractRelic r : ((AbstractPlayer) owner).relics) {
                    oldBlock = tempBlock;
                    oldDamage = tempDamage;
                    if (r instanceof PCLRelic) {
                        tempDamage = ((PCLRelic) r).atDamageModify(info, tempDamage);
                        tempBlock = ((PCLRelic) r).atBlockModify(info, tempBlock);
                    }
                    else {
                        tempDamage = r.atDamageModify(tempDamage, this);
                    }
                    addAttackDisplay(r.img, oldDamage, tempDamage);
                    addDefendDisplay(r.img, oldBlock, tempBlock);
                }

                for (AbstractRelic r : ((AbstractPlayer) owner).relics) {
                    if (r instanceof PCLRelic) {
                        tempHitCount = ((PCLRelic) r).atHitCountModify(info, tempHitCount);
                        tempRightCount = ((PCLRelic) r).atRightCountModify(info, tempRightCount);
                    }
                }

                // Vanilla blights and orbs don't affect calculations
                for (AbstractBlight r : ((AbstractPlayer) owner).blights) {
                    if (r instanceof PCLBlight) {
                        oldBlock = tempBlock;
                        oldDamage = tempDamage;
                        tempDamage = ((PCLBlight) r).atDamageModify(info, tempDamage);
                        tempBlock = ((PCLBlight) r).atBlockModify(info, tempBlock);
                        addAttackDisplay(r.img, oldDamage, tempDamage);
                        addDefendDisplay(r.img, oldBlock, tempBlock);
                    }
                }
                for (AbstractOrb r : ((AbstractPlayer) owner).orbs) {
                    if (r instanceof PCLOrb) {
                        oldBlock = tempBlock;
                        oldDamage = tempDamage;
                        tempDamage = ((PCLOrb) r).atDamageModify(info, tempDamage);
                        tempBlock = ((PCLOrb) r).atBlockModify(info, tempBlock);
                        addAttackDisplay(oldDamage, tempDamage);
                        addDefendDisplay(oldBlock, tempBlock);
                    }
                }
            }

            tempBlock = CardModifierManager.onModifyBlock(tempBlock, this);
            tempDamage = CardModifierManager.onModifyDamage(tempDamage, this, asEnemy);

            for (AbstractPower p : owner.powers) {
                if (p instanceof PCLPower) {
                    tempHitCount = ((PCLPower) p).modifyHitCount(info, tempHitCount, this);
                    tempRightCount = ((PCLPower) p).modifyRightCount(info, tempRightCount, this);
                }
            }

            if (attackType.useFocus) {
                for (AbstractPower p : owner.powers) {
                    oldBlock = tempBlock;
                    oldDamage = tempDamage;
                    tempBlock = p.modifyBlock(tempBlock, this);
                    if (FocusPower.POWER_ID.equals(p.ID)) {
                        tempDamage += p.amount;
                    }
                    else if (p instanceof PCLPower) {
                        tempDamage = ((PCLPower) p).modifyOrbOutgoing(tempDamage);
                    }
                    addAttackDisplay(p, oldDamage, tempDamage);
                    addDefendDisplay(p, oldBlock, tempBlock);
                }
            }
            else {
                for (AbstractPower p : owner.powers) {
                    oldBlock = tempBlock;
                    oldDamage = tempDamage;

                    if (p instanceof PCLPower) {
                        tempBlock = ((PCLPower) p).modifyBlock(info, tempBlock, this);
                        for (int i = 0; i < applyCount; i++) {
                            tempDamage = ((PCLPower) p).atDamageGive(info, tempDamage, damageTypeForTurn, this);
                        }
                    }
                    else {
                        tempBlock = p.modifyBlock(tempBlock, this);
                        for (int i = 0; i < applyCount; i++) {
                            tempDamage = p.atDamageGive(tempDamage, damageTypeForTurn, this);
                        }
                    }

                    addAttackDisplay(p, oldDamage, tempDamage);
                    addDefendDisplay(p, oldBlock, tempBlock);
                }
            }

            tempBlock = CombatManager.playerSystem.modifyBlock(tempBlock, info, parent != null ? parent : this, this);
            tempDamage = CombatManager.playerSystem.modifyDamage(tempDamage, info, parent != null ? parent : this, this);

            oldBlock = tempBlock;
            oldDamage = tempDamage;
            tempDamage = modifyDamageLast(info, tempDamage);
            tempBlock = modifyBlockLast(info, tempBlock);
            addAttackDisplay(oldDamage, tempDamage);
            addDefendDisplay(oldBlock, tempBlock);

            if (owner instanceof AbstractPlayer) {
                for (AbstractRelic r : ((AbstractPlayer) owner).relics) {
                    if (r instanceof PCLRelic) {
                        oldBlock = tempBlock;
                        oldDamage = tempDamage;
                        tempDamage = ((PCLRelic) r).atDamageLastModify(info, tempDamage);
                        tempBlock = ((PCLRelic) r).atBlockLastModify(info, tempBlock);
                        addAttackDisplay(r.img, oldDamage, tempDamage);
                        addDefendDisplay(r.img, oldBlock, tempBlock);
                    }
                }

                // Vanilla blights and orbs don't affect calculations
                for (AbstractBlight r : ((AbstractPlayer) owner).blights) {
                    if (r instanceof PCLBlight) {
                        oldBlock = tempBlock;
                        oldDamage = tempDamage;
                        tempDamage = ((PCLBlight) r).atDamageLastModify(info, tempDamage);
                        tempBlock = ((PCLBlight) r).atBlockLastModify(info, tempBlock);
                        addAttackDisplay(r.img, oldDamage, tempDamage);
                        addDefendDisplay(r.img, oldBlock, tempBlock);
                    }
                }
                for (AbstractOrb r : ((AbstractPlayer) owner).orbs) {
                    if (r instanceof PCLOrb) {
                        oldBlock = tempBlock;
                        oldDamage = tempDamage;
                        tempDamage = ((PCLOrb) r).atDamageLastModify(info, tempDamage);
                        tempBlock = ((PCLOrb) r).atBlockLastModify(info, tempBlock);
                        addAttackDisplay(oldDamage, tempDamage);
                        addDefendDisplay(oldBlock, tempBlock);
                    }
                }
            }
            else if (owner instanceof PCLCardCreature) {
                oldBlock = tempBlock;
                oldDamage = tempDamage;
                tempDamage = ((PCLCardCreature) owner).atDamageLastModify(info, tempDamage);
                tempBlock = ((PCLCardCreature) owner).atBlockLastModify(info, tempBlock);
                addAttackDisplay(oldDamage, tempDamage);
                addDefendDisplay(oldBlock, tempBlock);
            }

            oldBlock = tempBlock;
            oldDamage = tempDamage;
            tempBlock = CardModifierManager.onModifyBlockFinal(tempBlock, this);
            tempDamage = CardModifierManager.onModifyDamageFinal(tempDamage, this, asEnemy);
            addAttackDisplay(oldDamage, tempDamage);
            addDefendDisplay(oldBlock, tempBlock);

            for (AbstractPower p : owner.powers) {
                tempBlock = p.modifyBlockLast(tempBlock);
            }

            if (applyEnemyPowers) {
                if (attackType.bypassFlight && EUIUtils.any(enemy.powers, po -> FlightPower.POWER_ID.equals(po.ID))) {
                    tempDamage *= 2f * applyCount;
                }

                oldDamage = tempDamage;
                tempDamage = CombatManager.onModifyDamageReceiveFirst(tempDamage, damageTypeForTurn, getSourceCreature(), enemy, this);
                addAttackDisplay(oldDamage, tempDamage);

                if (attackType.useFocus) {
                    for (AbstractPower p : enemy.powers) {
                        oldDamage = tempDamage;
                        // Lock-on calculations are hardcoded in AbstractOrb so we are falling back on PCLLockOn's multiplier for now
                        if (LockOnPower.POWER_ID.equals(p.ID)) {
                            tempDamage *= PCLLockOnPower.getOrbMultiplier(enemy.isPlayer);
                        }
                        else if (p instanceof PCLPower) {
                            for (int i = 0; i < applyCount; i++) {
                                tempDamage = ((PCLPower) p).modifyOrbIncoming(tempDamage);
                            }
                        }
                        addAttackDisplay(p, oldDamage, tempDamage);
                    }
                }
                else {
                    for (AbstractPower p : enemy.powers) {
                        oldDamage = tempDamage;
                        for (int i = 0; i < applyCount; i++) {
                            tempDamage = p.atDamageReceive(tempDamage, damageTypeForTurn, this);
                        }
                        addAttackDisplay(p, oldDamage, tempDamage);
                    }
                }

            }

            if (owner instanceof AbstractPlayer) {
                tempDamage = ((AbstractPlayer) owner).stance.atDamageGive(tempDamage, damageTypeForTurn, this);
            }

            for (AbstractPower p : owner.powers) {
                oldDamage = tempDamage;
                for (int i = 0; i < applyCount; i++) {
                    tempDamage = p.atDamageFinalGive(tempDamage, damageTypeForTurn, this);
                }
                addAttackDisplay(p, oldDamage, tempDamage);
            }

            if (applyEnemyPowers) {

                oldDamage = tempDamage;
                tempDamage = CombatManager.onModifyDamageReceiveLast(tempDamage, damageTypeForTurn, getSourceCreature(), enemy, this);
                addAttackDisplay(oldDamage, tempDamage);

                for (AbstractPower p : enemy.powers) {
                    oldDamage = tempDamage;
                    for (int i = 0; i < applyCount; i++) {
                        tempDamage = p.atDamageFinalReceive(tempDamage, damageTypeForTurn, this);
                    }
                    addAttackDisplay(p, oldDamage, tempDamage);
                }
                oldDamage = tempDamage;
                tempDamage = CombatManager.onDamageOverride(enemy, damageTypeForTurn, tempDamage, this);
                addAttackDisplay(oldDamage, tempDamage);
            }
        }

        block = Math.max(0, MathUtils.floor(tempBlock));
        damage = Math.max(0, MathUtils.floor(tempDamage));
        hitCount = Math.max(0, MathUtils.floor(modifyHitCount(info, tempHitCount)));
        rightCount = Math.max(0, MathUtils.floor(modifyRightCount(info, tempRightCount)));

        boolean prevHitCountModified = isHitCountModified;
        boolean prevRightCountModified = isHitCountModified;
        this.isBlockModified = (baseBlock != block);
        this.isDamageModified = (baseDamage != damage);
        this.isHitCountModified = (baseHitCount != hitCount);
        this.isRightCountModified = (baseRightCount != rightCount);

        // Do not use the regular update methods because those will refresh amounts from onAttack with the standard setAmount
        if (onAttackEffect != null) {
            onAttackEffect.setAmountFromCardForUpdateOnly();
            if (prevHitCountModified != isHitCountModified) {
                initializeDescription();
            }
        }
        if (onBlockEffect != null) {
            onBlockEffect.setAmountFromCardForUpdateOnly();
            if (prevHitCountModified != isHitCountModified) {
                initializeDescription();
            }
        }

        addAttackResult(baseDamage, tempDamage);
        addDefendResult(baseBlock, tempBlock);

        // Invoke cost updates
        int baseCostChange = modifyCost(info, cost) - cost;
        TemporaryCostModifier.tryRefresh(this, owner, costForTurn, baseCostChange);

        // Refresh card text
        cardText.refresh(info);

        // Release damage display for rendering
        formulaDisplay = null;
    }

    public PCLAugment removeAugment(int index) {
        return removeAugment(index, true);
    }

    public PCLAugment removeAugment(int index, boolean save) {
        if (index >= 0 && index < augments.size()) {
            PCLAugment augment = augments.get(index);
            if (augment != null && augment.canRemove()) {
                augments.set(index, null);
                if (save) {
                    auxiliaryData.removeAugmentAt(index);
                }
                augment.onRemoveFromCard(this);
                refresh(null);
                return augment;
            }
        }
        return null;
    }

    public PCLAugment removeAugment(PCLAugment augment) {
        return removeAugment(augment, true);
    }

    public PCLAugment removeAugment(PCLAugment augment, boolean save) {
        if (augment != null && augment.canRemove()) {
            int index = augments.indexOf(augment);
            if (index >= 0 && index < augments.size()) {
                augments.set(index, null);
                if (save) {
                    auxiliaryData.removeAugmentAt(index);
                }
                augment.onRemoveFromCard(this);
                refresh(null);
                return augment;
            }
        }
        return null;
    }

    @Override
    public final void render(SpriteBatch sb) {
        render(sb, hovered, false, false);
    }

    @Override
    public final void render(SpriteBatch sb, boolean selected) {
        render(sb, hovered, selected, false);
    }

    public void render(SpriteBatch sb, boolean hovered, boolean selected, boolean library) {
        if (Settings.hideCards || !isOnScreen()) {
            return;
        }

        if (flashVfx != null) {
            flashVfx.render(sb);
        }

        if (isFlipped) {
            renderBack(sb, hovered, selected);
            return;
        }

        if (EUIGameUtils.canShowUpgrades(library) && !isPreview && !isPopup && canUpgrade()) {
            updateGlow();
            renderGlow(sb);
            renderUpgradePreview(sb);
            return;
        }

        updateGlow();
        renderGlow(sb);
        renderImage(sb, hovered, selected);
        renderType(sb);
        renderDescription(sb);
        renderTint(sb);
        renderEnergy(sb);
        renderTitle(sb);
        hb.render(sb);

        CardModifierManager.onRender(this, sb);
    }

    protected void renderAffinities(SpriteBatch sb) {
        if (isSeen && (PGR.config.showIrrelevantProperties.get() || GameUtilities.isPCLActingCardColor(this) || (GameUtilities.inGame() && !CombatManager.showAffinities().isEmpty()))) {
            affinities.renderOnCard(sb, this, AbstractDungeon.player != null && AbstractDungeon.player.hand.contains(this));
        }
    }

    protected void renderAtlas(SpriteBatch sb, Color color, TextureAtlas.AtlasRegion img, float drawX, float drawY) {
        sb.setColor(color);
        sb.draw(img, drawX + img.offsetX - (float) img.originalWidth / 2f, drawY + img.offsetY - (float) img.originalHeight / 2f, (float) img.originalWidth / 2f - img.offsetX, (float) img.originalHeight / 2f - img.offsetY, (float) img.packedWidth, (float) img.packedHeight, this.drawScale * Settings.scale, this.drawScale * Settings.scale, this.angle);
    }

    protected void renderAtlas(SpriteBatch sb, Color color, TextureAtlas.AtlasRegion img, float drawX, float drawY, float scale) {
        sb.setColor(color);
        sb.draw(img, drawX + img.offsetX - (float) img.originalWidth / 2f, drawY + img.offsetY - (float) img.originalHeight / 2f, (float) img.originalWidth / 2f - img.offsetX, (float) img.originalHeight / 2f - img.offsetY, (float) img.packedWidth, (float) img.packedHeight, this.drawScale * Settings.scale * scale, this.drawScale * Settings.scale * scale, this.angle);
    }

    protected void renderAttributeBanner(SpriteBatch sb, TextureCache icon, float sign, float offsetIconX) {
        final Texture panel = getCardAttributeBanner();
        final Color rarityColor = getRarityColor();

        if (panel != null) {
            PCLRenderHelpers.drawOnCardAuto(sb, this, panel, sign * AbstractCard.RAW_W * 0.33f, BANNER_OFFSET_Y, 120f, 54f, rarityColor, rarityColor.a * transparency, 1, 0, sign < 0, false);
            if (icon != null) {
                final float icon_x = offsetIconX + (sign * (AbstractCard.RAW_W * 0.43f));
                PCLRenderHelpers.drawOnCardAuto(sb, this, icon.texture(), icon_x, BANNER_OFFSET_Y, 48, 48);
            }
        }
    }

    protected void renderAttributeBannerWithText(SpriteBatch sb, TextureCache icon, String text, Color textColor, float offsetX, float offsetY, float offsMult, float scaleMult, float sign, float offsetIconX) {
        renderAttributeBanner(sb, icon, sign, offsetIconX);

        BitmapFont largeFont = PCLRenderHelpers.getEnergyFont(this, scaleMult);
        float text_width = offsMult * (isPopup ? 0.25f : 0.52f) * EUITextHelper.getTextWidth(largeFont, text) / Settings.scale;
        float suffix_width = 0;
        float text_x = sign * AbstractCard.RAW_W * (0.32f - sign * offsetX);
        EUITextHelper.writeOnCard(sb, this, largeFont, text, offsetX + (text_width), offsetY, textColor, true);

        EUITextHelper.resetFont(largeFont);
    }

    protected void renderAttributes(SpriteBatch sb) {
        if (this.type == PCLEnum.CardType.SUMMON) {
            renderAttributeBannerWithText(sb, getHPIcon(), this.getHPString(), this.getHPStringColor(), BANNER_OFFSET_X, BANNER_OFFSET_Y,0.85f,0.85f, -1, 0);
            renderAttributeBannerWithText(sb, getPriorityIcon(), this.pclTarget.getShortStringForTag(), Settings.CREAM_COLOR, BANNER_OFFSET_X2, BANNER_OFFSET_Y2,0,0.45f, 1, -25f);
        }
        else if (PGR.config.showCardTarget.get()) {
            renderAttributeBannerWithText(sb, null, this.pclTarget.getShortStringForTag(), Settings.CREAM_COLOR, BANNER_OFFSET_X2, BANNER_OFFSET_Y,0,0.45f, 1, 0);
        }
    }

    private float renderAugment(SpriteBatch sb, PCLAugment augment, float y) {
        PCLRenderHelpers.drawOnCardAuto(sb, this, PCLCoreImages.CardUI.augmentSlot.texture(), AUGMENT_OFFSET_X, AUGMENT_OFFSET_Y + y, 28, 28, Color.WHITE, this.transparency, 1);
        if (augment != null) {
            PCLRenderHelpers.drawOnCardAuto(sb, this, augment.getTextureBase(), AUGMENT_OFFSET_X, AUGMENT_OFFSET_Y + y, 28, 28, Color.WHITE, this.transparency, 1);
            Texture tex = augment.getTexture();
            if (tex != null) {
                PCLRenderHelpers.drawOnCardAuto(sb, this, tex, AUGMENT_OFFSET_X, AUGMENT_OFFSET_Y + y, 28, 28, Color.WHITE, this.transparency, 1);
            }
        }
        else {
            PCLRenderHelpers.drawOnCardAuto(sb, this, PCLCoreImages.CardUI.augmentSlot.texture(), AUGMENT_OFFSET_X, AUGMENT_OFFSET_Y + y, 28, 28, Color.WHITE, this.transparency, 1);
        }

        return 30; // y offset
    }

    @SpireOverride
    protected void renderBack(SpriteBatch sb, boolean hovered, boolean selected) {
        SpireSuper.call(sb, hovered, selected);
    }

    @SpireOverride
    protected void renderBannerImage(SpriteBatch sb, float drawX, float drawY) {
        renderAffinities(sb);
        float sc = isPopup ? 0.5f : 1f;
        if (!tryRenderCentered(sb, getCardBanner(), getRarityColor(), sc)) {
            // Copying base game behavior
            if (isPopup) {
                renderAtlas(sb, getRarityVanillaColor(), getCardBannerVanillaRegion(), (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F, sc);
            }
            else {
                renderAtlas(sb, getRarityVanillaColor(), getCardBannerVanillaRegion(), current_x, current_y, sc);
            }
        }
    }

    @SpireOverride
    private void renderCard(SpriteBatch sb, boolean hovered, boolean selected) {
        render(sb, hovered, selected, false);
    }

    @SpireOverride
    protected void renderCardBg(SpriteBatch sb, float x, float y) {
        Texture card = getCardBackground();
        float popUpMultiplier = isPopup ? 0.5f : 1f;
        if (GameUtilities.isColorlessCardColor(this.color)) {
            PCLRenderHelpers.drawGrayscale(sb, (s) ->
                    PCLRenderHelpers.drawOnCardAuto(s, this, card,
                            0, 0, card.getWidth(), card.getHeight(),
                            this.color == CardColor.CURSE ? PCLCard.CURSE_COLOR : PCLCard.COLORLESS_ORB_COLOR, transparency, popUpMultiplier));
        }
        else {
            PCLRenderHelpers.drawOnCardAuto(sb, this, card, 0, 0, card.getWidth(), card.getHeight(), getRenderColor(), transparency, popUpMultiplier);
        }
    }

    @Deprecated
    @Override
    public final void renderCardPreview(SpriteBatch sb) { /* Useless */ }

    @Deprecated
    @Override
    public final void renderCardPreviewInSingleView(SpriteBatch sb) { /* Useless */ }

    @Override
    public void renderCardTip(SpriteBatch sb) {
        if (!Settings.hideCards && !isFlipped && !isLocked && isSeen && (isPopup || canRenderTip()) && (AbstractDungeon.player == null || !AbstractDungeon.player.isDraggingCard || Settings.isTouchScreen)) {
            EUITooltip.queueTooltips(this);
        }
    }

    @SpireOverride
    public void renderDescription(SpriteBatch sb) {
        if (!Settings.hideCards && !isFlipped) {
            if (isLocked || !isSeen) {
                FontHelper.menuBannerFont.getData().setScale(drawScale * 1.25f);
                FontHelper.renderRotatedText(sb, FontHelper.menuBannerFont, "? ? ?", current_x, current_y,
                        0, -200 * Settings.scale * drawScale * 0.5f, angle, true, EUIColors.cream(transparency));
                FontHelper.menuBannerFont.getData().setScale(1f);
                return;
            }

            cardText.renderLines(sb);
            renderAttributes(sb);

            if (drawScale > 0.3f) {
                renderIcons(sb);

                if (bottomText != null) {
                    BitmapFont font = PCLRenderHelpers.getSmallTextFont(this, bottomText);
                    EUITextHelper.writeOnCard(sb, this, font, bottomText, 0, -0.47f * AbstractCard.RAW_H, Settings.CREAM_COLOR, true);
                    EUITextHelper.resetFont(font);
                }
            }
        }
    }

    // Don't use this, just use renderDescription instead
    @Deprecated
    @SpireOverride
    private void renderDescriptionCN(SpriteBatch sb) {
        renderDescription(sb);
    }

    @SpireOverride
    protected void renderEnergy(SpriteBatch sb) {
        if (this.cost > -2 && !getDarken() && !this.isLocked && this.isSeen) {
            Texture baseCard = getCardBackground();
            float popUpMultiplier = isPopup ? 0.5f : 1f;
            Texture energyOrb = getEnergyOrb();
            PCLRenderHelpers.drawOnCardAuto(sb, this, energyOrb, -baseCard.getWidth() / (isPopup ? 7.7f : 3.85f), baseCard.getHeight() / (isPopup ? 5.3f : 2.64f), energyOrb.getWidth(), energyOrb.getHeight(), getRenderColor(), transparency, popUpMultiplier);

            renderEnergyText(sb);
        }
    }

    protected void renderEnergyText(SpriteBatch sb) {
        renderEnergyText(sb, -132f, 192f);
    }

    protected void renderEnergyText(SpriteBatch sb, float xOffset, float yOffset) {
        String text;
        if (cost == -1) {
            text = PGR.core.strings.subjects_x;
        }
        else {
            text = Integer.toString(Math.max(0, freeToPlay() ? 0 : this.costForTurn));
        }

        if (AbstractDungeon.player != null && AbstractDungeon.player.hand.contains(this) && (!CombatManager.canPlayCard(this, AbstractDungeon.player, null, hasEnoughEnergy()))) {
            energyColor.set(1f, 0.3f, 0.3f, transparency);
        }
        else if (isCostModified || costForTurn < cost || (cost > 0 && this.freeToPlay())) {
            energyColor.set(0.4f, 1f, 0.4f, transparency);
        }
        else {
            energyColor.set(1, 1, 1, transparency);
        }
        BitmapFont font = PCLRenderHelpers.getEnergyFont(this);
        EUITextHelper.writeOnCard(sb, this, font, text, xOffset, yOffset, energyColor);
        EUITextHelper.resetFont(font);
    }

    private float renderFooter(SpriteBatch sb, Texture texture, float y) {
        final float offset_y = y - AbstractCard.RAW_H * 0.46f;
        final float alpha = transparency;

        PCLRenderHelpers.drawOnCardAuto(sb, this, PCLCoreImages.Core.controllableCardPile.texture(), AUGMENT_OFFSET_X, offset_y, FOOTER_SIZE, FOOTER_SIZE, Color.BLACK, alpha * 0.6f, 0.8f);
        PCLRenderHelpers.drawOnCardAuto(sb, this, texture, AUGMENT_OFFSET_X, offset_y, FOOTER_SIZE, FOOTER_SIZE, Color.WHITE, alpha, 0.8f);

        return 38; // y offset
    }

    @Override
    public final void renderForPreview(SpriteBatch sb) {
        render(sb, hovered, false, false);
    }

    @SpireOverride
    public void renderGlow(SpriteBatch sb) {
        if (!canRenderGlow()) {
            return;
        }

        renderGlowManual(sb, 1f);
    }

    protected void renderGlowEffect(SpriteBatch sb, float duration, float scaleMult) {
        TextureAtlas.AtlasRegion img = getBorderTexture();
        float iScale = (1.0F + Interpolation.pow2Out.apply(0.03F, 0.13F * scaleMult, 1.0F - duration)) * drawScale * Settings.scale;
        glowColor.a = duration / 2.0F;
        sb.setColor(glowColor);
        sb.draw(img, current_x + img.offsetX - img.originalWidth / 2.0F, current_y + img.offsetY - img.originalHeight / 2.0F, img.originalWidth / 2.0F - img.offsetX, img.originalHeight / 2.0F - img.offsetY, img.packedWidth, img.packedHeight, iScale, iScale, angle);
    }

    public void renderGlowManual(SpriteBatch sb, float glowScaleMult) {
        renderMainBorder(sb);

        for (float i : fakeGlowList) {
            if (i > 0) {
                renderGlowEffect(sb, i, glowScaleMult);
            }
        }

        sb.setBlendFunction(EUIRenderHelpers.BlendingMode.Normal.srcFunc, EUIRenderHelpers.BlendingMode.Normal.dstFunc);
    }

    protected void renderIcons(SpriteBatch sb) {
        final float alpha = updateBadgeAlpha();

        float offset_y = PCLCardTag.renderTagsOnCard(sb, this, alpha);

        // Render card footers
        offset_y = 0;
        if (isSoulbound()) {
            offset_y += renderFooter(sb, isPopup ? PCLCoreImages.CardIcons.soulboundL.texture() : PCLCoreImages.CardIcons.soulbound.texture(), offset_y);
        }
        if (isUnique()) {
            offset_y += renderFooter(sb, isPopup ? PCLCoreImages.CardIcons.uniqueL.texture() : PCLCoreImages.CardIcons.unique.texture(), offset_y);
        }
        if (cardData.canToggleFromPopup) {
            offset_y += renderFooter(sb, isPopup ? PCLCoreImages.CardIcons.multiformL.texture() : PCLCoreImages.CardIcons.multiform.texture(), offset_y);
        }

        // Render augments
        offset_y = 0;
        for (PCLAugment augment : augments) {
            offset_y += renderAugment(sb, augment, offset_y);
        }
    }

    @SpireOverride
    public void renderImage(SpriteBatch sb, boolean hovered, boolean selected) {
        if (AbstractDungeon.player != null) {
            if (selected) {
                renderAtlas(sb, Color.SKY, getCardBgAtlas(), current_x, current_y, 1.03f);
            }

            renderAtlas(sb, new Color(0, 0, 0, transparency * 0.25f), getCardBgAtlas(), current_x + SHADOW_OFFSET_X * drawScale, current_y - SHADOW_OFFSET_Y * drawScale);
            if ((AbstractDungeon.player.hoveredCard == this) && ((AbstractDungeon.player.isDraggingCard && AbstractDungeon.player.isHoveringDropZone) || AbstractDungeon.player.inSingleTargetMode)) {
                renderAtlas(sb, HOVER_IMG_COLOR, getCardBgAtlas(), current_x, current_y);
            }
            else if (selected) {
                renderAtlas(sb, SELECTED_CARD_COLOR, getCardBgAtlas(), current_x, current_y);
            }
        }

        renderPortrait(sb);
        renderCardBg(sb, current_x, current_y);
        renderPortraitFrame(sb, current_x, current_y);
        renderBannerImage(sb, current_x, current_y);
    }

    @Override
    public final void renderInLibrary(SpriteBatch sb) {
        render(sb, hovered, false, true);
    }

    @Deprecated
    @SpireOverride
    protected void renderJokePortrait(SpriteBatch sb) {
        renderPortrait(sb);
    }

    @SpireOverride
    protected void renderMainBorder(SpriteBatch sb) {
        if (!this.isGlowing) {
            return;
        }

        final TextureAtlas.AtlasRegion img = getBorderTexture();

        if (CombatManager.inBattle()) {
            sb.setColor(this.glowColor);
        }
        else {
            sb.setColor(GREEN_BORDER_GLOW_COLOR);
        }

        sb.setBlendFunction(EUIRenderHelpers.BlendingMode.Glowing.srcFunc, EUIRenderHelpers.BlendingMode.Glowing.dstFunc);
        sb.draw(img, this.current_x + img.offsetX - (img.originalWidth / 2f), this.current_y + img.offsetY - (img.originalWidth / 2f),
                (img.originalWidth / 2f) - img.offsetX, (img.originalWidth / 2f) - img.offsetY, img.packedWidth, img.packedHeight,
                this.drawScale * Settings.scale * 1.04f, this.drawScale * Settings.scale * 1.03f, this.angle);
    }

    @Override
    public final void renderOuterGlow(SpriteBatch sb) {
        super.renderOuterGlow(sb);
    }

    @SpireOverride
    protected void renderPortrait(SpriteBatch sb) {
        if (!isSeen || isLocked) {
            renderPortraitImage(sb, EUIRM.getTexture(QuestionMark.DATA.imagePath), getRenderColor(), 1, false, false, false);
            return;
        }

        final boolean cropPortrait = PGR.config.cropCardImages.get() && !PGR.config.lowVRAM.get();

        // TODO support for animated pictures
        if (portraitImg != null) {
            renderPortraitImage(sb, portraitImg.texture, portraitImg.color, portraitImg.scale, cropPortrait, false, false);
        }
        if (portraitForeground != null) {
            renderPortraitImage(sb, portraitForeground.texture, portraitForeground.color, portraitForeground.scale, cropPortrait, portraitForeground.scale != 1, true);
        }
    }

    @SpireOverride
    protected void renderPortraitFrame(SpriteBatch sb, float x, float y) {
        float sc = isPopup ? 0.5f : 1f;
        if (!tryRenderCentered(sb, getPortraitFrame(), getRarityColor(), sc)) {
            // Copying base game location behavior
            // Use colorize for non base game frames
            switch (rarity) {
                case BASIC:
                case COMMON:
                case CURSE:
                case UNCOMMON:
                case RARE:
                    if (isPopup) {
                        renderAtlas(sb, getRarityVanillaColor(), getPortraitFrameVanillaRegion(), (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F, sc);
                    }
                    else {
                        renderAtlas(sb, getRarityVanillaColor(), getPortraitFrameVanillaRegion(), current_x, current_y, sc);
                    }
                    break;
                default:
                    EUIRenderHelpers.drawColorized(sb, s -> {
                        if (isPopup) {
                            renderAtlas(s, getRarityVanillaColor(), getPortraitFrameVanillaRegion(), (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F, sc);
                        }
                        else {
                            renderAtlas(s, getRarityVanillaColor(), getPortraitFrameVanillaRegion(), current_x, current_y, sc);
                        }
                    });
            }
        }
    }

    protected void renderPortraitImage(SpriteBatch sb, Texture texture, Color color, float scale, boolean cropPortrait, boolean useTextureSize, boolean foreground) {
        if (color == null) {
            color = getRenderColor();
        }

        final float render_width = useTextureSize ? texture.getWidth() : 250;
        final float render_height = useTextureSize ? texture.getWidth() : 190;
        if (cropPortrait && drawScale > 0.6f && drawScale < 1) {
            final int width = texture.getWidth();
            final int offset_x = (int) ((1 - drawScale) * (0.5f * width));
            TextureAtlas.AtlasRegion region = foreground ? jokePortrait : portrait;
            if (region == null || texture != region.getTexture() || (region.getRegionX() != offset_x) || EUI.elapsed50()) {
                final int height = texture.getHeight();
                final int offset_y1 = 0;//(int) ((1-drawScale) * (0.5f * height));
                final int offset_y2 = (int) ((1 - drawScale) * (height));
                if (region == null) {
                    region = new TextureAtlas.AtlasRegion(texture, offset_x, offset_y1, width - (2 * offset_x), height - offset_y1 - offset_y2);
                    if (foreground) {
                        jokePortrait = region; // let's just reuse this.
                    }
                    else {
                        portrait = region;
                    }
                }
                else {
                    region.setRegion(texture);
                    region.setRegion(offset_x, offset_y1, width - (2 * offset_x), height - offset_y1 - offset_y2);
                }
            }

            PCLRenderHelpers.drawOnCardAuto(sb, this, region, 0, 72, render_width, render_height, color, transparency, scale);
        }
        else if (isPopup) {
            PCLRenderHelpers.drawOnCardAuto(sb, this, texture, 0, 72, render_width * 2, render_height * 2, color, transparency, scale * 0.5f);
        }
        else {
            PCLRenderHelpers.drawOnCardAuto(sb, this, texture, 0, 72, render_width, render_height, color, transparency, scale);
        }
    }

    @Override
    public final void renderSmallEnergy(SpriteBatch sb, TextureAtlas.AtlasRegion region, float x, float y) { /* Useless */ }

    @SpireOverride
    protected void renderTint(SpriteBatch sb) {
        SpireSuper.call(sb);
    }

    @SpireOverride
    protected void renderTitle(SpriteBatch sb) {
        final float nameRatio = MathUtils.clamp(originalName.length(), 13, 19) / 13f;
        final float scale = 1 / nameRatio;
        BitmapFont result;
        if (isPopup) {
            result = FontHelper.SCP_cardTitleFont_small;
            result.getData().setScale(drawScale * 0.5f * scale);
        }
        else {
            result = FontHelper.cardTitleFont;
            result.getData().setScale(drawScale * scale);
        }

        Color color;
        String text;
        if (isLocked || !isSeen) {
            color = Color.WHITE;
            text = isLocked ? LOCKED_STRING : UNKNOWN_STRING;
        }
        else {
            color = upgraded ? Settings.GREEN_TEXT_COLOR : Color.WHITE;
            text = originalName; // Use originalName since PCLCard uses this to store the modified name
        }

        // Base game text is SLIGHTLY off
        if (isPopup && !shouldUsePCLFrame()) {
            EUITextHelper.writeOnCardWrapped(sb, this, result, text, 0, RAW_H * 0.4f, HEADER_WIDTH * nameRatio, color);
        }
        else {
            EUITextHelper.writeOnCardWrapped(sb, this, result, text, 0, RAW_H * 0.416f, HEADER_WIDTH * nameRatio, color);
        }
        EUITextHelper.resetFont(result);
    }

    @SpireOverride
    protected void renderType(SpriteBatch sb) {
        if (shouldUsePCLFrame()) {
            Texture texture = getTypeIcon();
            float height = texture.getHeight();
            PCLRenderHelpers.drawOnCardAuto(sb, this, texture, 0, -height * 0.2f, texture.getWidth(), height, Color.WHITE, transparency, Settings.scale * 0.24f);
        }
        else {
            if (isPopup) {
                FontHelper.renderFontCentered(sb, FontHelper.panelNameFont, EUIGameUtils.textForType(type), (float) Settings.WIDTH / 2.0F + 3.0F * Settings.scale, (float) Settings.HEIGHT / 2.0F - 40.0F * Settings.scale, getRarityTextColor());
            }
            else {
                BitmapFont font = FontHelper.cardTypeFont;
                font.getData().setScale(this.drawScale);
                Color typeColor = getTypeColor();
                typeColor.a = getRenderColor().a;
                FontHelper.renderRotatedText(sb, font, EUIGameUtils.textForType(type), this.current_x, this.current_y - 22.0F * this.drawScale * Settings.scale, 0.0F, -1.0F * this.drawScale * Settings.scale, this.angle, false, getRarityTextColor());
            }
        }
    }

    @Override
    public void renderUpgradePreview(SpriteBatch sb) {
        PCLCard upgrade = getCachedUpgrade();

        upgrade.current_x = this.current_x;
        upgrade.current_y = this.current_y;
        upgrade.drawScale = this.drawScale;
        upgrade.render(sb, false);
    }

    @Override
    public final void renderWithSelections(SpriteBatch sb) {
        render(sb, false, true, false);
    }

    @Override
    public final void resetAttributes() {
        // Triggered after being played, discarded, or at end of turn
        super.resetAttributes();
    }

    public void resetTipsForCardText() {
        tooltips.clear();
        for (PSkill<?> skill : getEffects()) {
            skill.recurse(s -> s.onSetupTips(this));
        }
        for (PSkill<?> skill : getPowerEffects()) {
            skill.recurse(s -> s.onSetupTips(this));
        }
    }

    public int rightCount() {
        return rightCount;
    }

    public int rightCountBase() {
        return baseRightCount;
    }

    @Override
    public final Type savedType() {
        return new TypeToken<PCLCardSaveData>() {
        }.getType();
    }

    public void setAttackType(PCLAttackType attackType) {
        this.attackType = attackType;
    }

    public void setCardRarity(CardRarity rarity) {
        this.rarity = rarity;
    }

    public void setCardRarityType(CardRarity rarity, CardType type) {
        this.rarity = rarity;
        this.type = type;
    }

    public void setCardType(CardType type) {
        this.type = type;
    }

    protected void setCost(int value) {
        if (this.cost >= 0) {
            int previousDiff = costForTurn - cost;
            this.cost = Math.max(0, value);
            this.costForTurn = Math.max(0, cost + previousDiff);
        }
    }

    public void setDrawScale(float scale) {
        this.drawScale = this.targetDrawScale = scale;
    }

    public void setEvokeOrbCount(int count) {
        this.showEvokeValue = count > 0;
        this.showEvokeOrbCount = count;
    }

    protected int setForm(Integer form, int timesUpgraded) {
        this.timesUpgraded = timesUpgraded;
        this.upgraded = this.timesUpgraded > 0;

        this.auxiliaryData.form = (form == null) ? 0 : MathUtils.clamp(form, 0, this.maxForms() - 1);
        cardData.invokeTags(this, this.auxiliaryData.form);
        setTarget(cardData.getTargetUpgrade(this.auxiliaryData.form));
        initializeName();
        for (PSkill<?> ef : getEffects()) {
            ef.setAmountFromCard().onUpgrade();
        }
        for (PSkill<?> ef : getPowerEffects()) {
            ef.setAmountFromCard().onUpgrade();
        }

        initializeDescription();

        return this.auxiliaryData.form;
    }

    @Override
    public void setIsPreview(boolean value) {
        isPreview = value;
    }

    public void setMultiDamage(boolean value) {
        this.isMultiDamage = value;
    }

    protected void setNumbers(int damage, int block, int magicNumber, int secondaryValue, int hitCount, int rightCount) {
        this.baseDamage = this.damage = damage;
        this.baseBlock = this.block = block;
        this.baseMagicNumber = this.magicNumber = magicNumber;
        this.currentHealth = this.baseHeal = this.heal = secondaryValue;
        this.baseHitCount = this.hitCount = Math.max(0, hitCount);
        this.baseRightCount = this.rightCount = Math.max(0, rightCount);
    }

    protected void setNumbers(int damage, int block) {
        setNumbers(damage, block, 0, 0);
    }

    protected void setNumbers(int damage, int block, int magicNumber, int secondaryValue) {
        setNumbers(damage, block, magicNumber, secondaryValue, 1);
    }

    protected void setNumbers(int damage, int block, int magicNumber, int secondaryValue, int hitCount) {
        setNumbers(damage, block, magicNumber, secondaryValue, hitCount, 1);
    }

    protected void setNumbers(int damage, int block, int magicNumber) {
        setNumbers(damage, block, magicNumber, 0);
    }

    public void setObtainableInCombat(boolean value) {
        setTag(CardTags.HEALING, !value);
    }

    public void setPosition(float x, float y) {
        this.current_x = this.target_x = x;
        this.current_y = this.target_y = y;
        this.hb.move(current_x, current_y);
    }

    public void setRenderTip(boolean val) {
        ReflectionHacks.setPrivate(this, AbstractCard.class, "renderTip", val);
    }

    protected void setTag(CardTags tag, boolean enable) {
        if (!enable) {
            tags.remove(tag);
        }
        else if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public void setTarget(PCLCardTarget attackTarget) {
        this.pclTarget = attackTarget;
    }

    public void setTiming(DelayTiming timing) {
        this.timing = timing;
    }

    public void setup(Object input) {
    }

    protected void setupFlags() {
        // Set Soulbound
        if (!cardData.removableFromDeck) {
            SoulboundField.soulbound.set(this, true);
        }

        if (cardData.flags != null) {
            for (CardFlag item : cardData.flags) {
                if (item.gameFlag != null) {
                    this.tags.add(item.gameFlag);
                    // Starter Strike tags should automatically be added to Basic cards with the Strike, so they can be upgraded in Simplicity, etc.
                    if (item == CardFlag.Strike && this.rarity == CardRarity.BASIC) {
                        this.tags.add(CardTags.STARTER_STRIKE);
                    }
                }
            }
        }
    }

    public void setupImages(String imagePath) {
        portrait = null;
        loadImage(imagePath);
    }

    protected void setupProperties(PCLCardData cardData, Integer form, int timesUpgraded) {
        setNumbers(cardData.getDamage(form) + cardData.getDamageUpgrade(form) * timesUpgraded,
                cardData.getBlock(form) + cardData.getBlockUpgrade(form) * timesUpgraded,
                cardData.getMagicNumber(form) + cardData.getMagicNumberUpgrade(form) * timesUpgraded,
                cardData.getHp(form) + cardData.getHpUpgrade(form) * timesUpgraded,
                cardData.getHitCount(form) + cardData.getHitCountUpgrade(form) * timesUpgraded,
                cardData.getRightCount(form) + cardData.getRightCountUpgrade(form) * timesUpgraded);
        setMultiDamage(cardData.cardTarget.targetsMulti());
        setTarget(cardData.cardTarget);
        setTiming(cardData.timing);
        setAttackType(cardData.attackType);

        if (timesUpgraded > 0) {
            setCost(cardData.getCost(form) + cardData.getCostUpgrade(form));
        }
        else {
            setCost(cardData.getCost(form));
        }

        this.affinities.initialize(cardData.affinities, form);

        if (!cardData.obtainableInCombat) {
            setObtainableInCombat(false);
        }

        bottomText = getBottomText();
    }

    // Determines whether the card frame used will be the base game's or the PCL frame
    public boolean shouldUsePCLFrame() {
        return PGR.getResources(cardData.cardColor).usePCLFrame;
    }

    public void stopFlash() {
        this.flashVfx = null;
    }

    public void stopGlowing(float delay) {
        super.stopGlowing();

        EUIClassUtils.setField(this, "glowTimer", delay);
    }

    @Override
    public int timesUpgraded() {
        return timesUpgraded;
    }

    @Override
    public void triggerOnEndOfPlayerTurn() {
        super.triggerOnEndOfPlayerTurn();
    }

    @Override
    public void triggerOnEndOfTurnForPlayingCard() {
        super.triggerOnEndOfTurnForPlayingCard();
        boolean shouldPlay = false;
        for (PSkill<?> be : getFullEffects()) {
            shouldPlay = shouldPlay | be.triggerOnEndOfTurn(true);
        }
        // TODO investigate why card queue item causes graphical glitches
        if (shouldPlay) {
            flash();
        }
    }

    @Override
    public void triggerOnExhaust() {
        super.triggerOnExhaust();
        doEffects(be -> be.triggerOnExhaust(this));
    }

    public void triggerOnFetch(CardGroup sourcePile) {
        doEffects(be -> be.triggerOnFetch(this, sourcePile));
    }

    @Override
    public void triggerOnGlowCheck() {
        this.glowColor = CombatManager.playerSystem.getGlowColor(this);

        for (PSkill<?> be : getFullEffects()) {
            Color c = be.getGlowColor();
            if (c != null) {
                this.glowColor = c;
            }
        }
    }

    @Override
    public void triggerOnManualDiscard() {
        super.triggerOnManualDiscard();
        doEffects(be -> be.triggerOnDiscard(this));
    }

    @Override
    public void triggerOnOtherCardPlayed(AbstractCard c) {
        super.triggerOnOtherCardPlayed(c);
        doEffects(be -> be.triggerOnOtherCardPlayed(c));
    }

    public void triggerOnPurge() {
        doEffects(be -> be.triggerOnPurge(this));
    }

    // Triggered when this card is manually reshuffled
    public void triggerOnReshuffle(CardGroup sourcePile) {
        doEffects(be -> be.triggerOnReshuffle(this, sourcePile));
    }

    @Deprecated
    @Override
    public final void triggerOnScry() {
        // Use triggerOnScryThatDoesntLoopOnEnd instead
    }

    // Because the actual trigger on scry LOOPS UNTIL THE ACTION IS DONE WTF
    public void triggerOnScryThatDoesntLoopOnEnd() {
        doEffects(be -> be.triggerOnScry(this));
    }

    // Triggered when this is shuffled along with the deck
    public void triggerOnShuffle() {
        doEffects(PSkill::triggerOnShuffle);
    }

    @Override
    public void triggerAtStartOfTurn() {
        super.triggerAtStartOfTurn();
        doEffects(PSkill::triggerOnStartOfTurn);
    }

    // Only called if the card is upgraded in battle through an action
    public void triggerOnUpgrade() {
        doEffects(be -> be.triggerOnUpgrade(this));
    }

    @Override
    public final void triggerWhenCopied() {
        // this is only used by ShowCardAndAddToHandEffect
        triggerWhenDrawn();
    }

    // Called at the start of a fight, or when a card is created by MakeTempCard.
    public void triggerWhenCreated(boolean startOfBattle) {
        doEffects(be -> be.triggerOnCreate(this, startOfBattle));
    }

    @Override
    public void triggerWhenDrawn() {
        super.triggerWhenDrawn();

        if (PCLCardTag.Autoplay.tryProgress(this)) {
            PCLActions.last.playCard(this, AbstractDungeon.player.hand, null)
                    .spendEnergy(true)
                    .setCondition(AbstractCard::hasEnoughEnergy);
        }

        doEffects(be -> be.triggerOnDraw(this));
    }

    public void triggerWhenKilled(PCLCardAlly ally) {
        doEffects(be -> be.triggerOnAllyDeath(this, ally));
    }

    public void triggerWhenSummoned(PCLCardAlly ally) {
        doEffects(be -> be.triggerOnAllySummon(this, ally));
    }

    public void triggerWhenTriggered(PCLCardAlly ally, AbstractCreature target, PCLCardAlly caller) {
        doEffects(be -> be.triggerOnAllyTrigger(this, target, ally, caller));
    }

    public void triggerWhenWithdrawn(PCLCardAlly ally, boolean triggerEffects) {
        doEffects(be -> be.triggerOnAllyWithdraw(this, ally, triggerEffects));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean tryRender(SpriteBatch sb, Texture texture, float scale, float drawX, float drawY) {
        if (texture != null) {
            PCLRenderHelpers.drawOnCardAuto(sb, this, texture, drawX, drawY, texture.getWidth(), texture.getHeight(), getRenderColor(), transparency, scale);

            return true;
        }

        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean tryRenderCentered(SpriteBatch sb, ColoredTexture texture) {
        if (texture != null) {
            PCLRenderHelpers.drawOnCardAuto(sb, this, texture, 0, 0, texture.getWidth(), texture.getHeight());

            return true;
        }

        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean tryRenderCentered(SpriteBatch sb, ColoredTexture texture, float scale) {
        if (texture != null) {
            PCLRenderHelpers.drawOnCardAuto(sb, this, texture, 0, 0, texture.getWidth(), texture.getHeight(), scale);

            return true;
        }

        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean tryRenderCentered(SpriteBatch sb, Texture texture) {
        if (texture != null) {
            PCLRenderHelpers.drawOnCardAuto(sb, this, texture, getRenderColor(), 0, 0, texture.getWidth(), texture.getHeight());

            return true;
        }

        return false;
    }

    protected boolean tryRenderCentered(SpriteBatch sb, Texture texture, float scale) {
        return tryRenderCentered(sb, texture, getRenderColor(), scale);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean tryRenderCentered(SpriteBatch sb, Texture texture, Color color, float scale) {
        if (texture != null) {
            PCLRenderHelpers.drawOnCardAuto(sb, this, texture, 0, 0, texture.getWidth(), texture.getHeight(), color, transparency, scale);

            return true;
        }

        return false;
    }

    @Override
    public void unhover() {
        if (this.hovered) {
            this.hoverDuration = 0.0F;
            this.targetDrawScale = 0.75F;
        }

        this.hovered = false;
        setRenderTip(false);
    }

    public final void unloadSingleCardView() {
        if (portraitImgBackup != null) {
            portraitImg.texture.dispose();
            portraitImg = portraitImgBackup;
        }
    }

    @Override
    public void untip() {
        this.hoverDuration = 0f;
        setRenderTip(false);
    }

    @Override
    public void update() {
        super.update();

        if (EUIGameUtils.inGame() && AbstractDungeon.player != null && AbstractDungeon.player.hoveredCard != this && !AbstractDungeon.isScreenUp) {
            this.hovered = false;
            setRenderTip(false);
        }

        // For selecting separate forms on the upgrade screen
        if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GRID && AbstractDungeon.gridSelectScreen.forUpgrade && hb.hovered && InputHelper.justClickedLeft) {
            GridCardSelectScreenPatches.selectPCLCardUpgrade(this);
        }
    }

    protected float updateBadgeAlpha() {
        if (isPreview) {
            return transparency - badgeAlphaOffset;
        }

        if (cardsToPreview instanceof PCLCard) {
            ((PCLCard) cardsToPreview).badgeAlphaOffset = badgeAlphaOffset;
        }

        if (canRenderTip() && !isPopup) {
            if (badgeAlphaOffset < badgeAlphaTargetOffset) {
                badgeAlphaOffset += EUI.delta(0.33f);
                if (badgeAlphaOffset > badgeAlphaTargetOffset) {
                    badgeAlphaOffset = badgeAlphaTargetOffset;
                    badgeAlphaTargetOffset = -0.9f;
                }
            }
            else {
                badgeAlphaOffset -= EUI.delta(0.5f);
                if (badgeAlphaOffset < badgeAlphaTargetOffset) {
                    badgeAlphaOffset = badgeAlphaTargetOffset;
                    badgeAlphaTargetOffset = 0.5f;
                }
            }

            if (transparency >= 1 && badgeAlphaOffset > 0) {
                return transparency - badgeAlphaOffset;
            }
        }
        else {
            badgeAlphaOffset = -0.2f;
            badgeAlphaTargetOffset = 0.5f;
        }

        return transparency;
    }

    public void updateBlockVars() {
        this.isBlockModified = (baseBlock != block);
        if (onBlockEffect != null) {
            onBlockEffect.setAmountFromCard();
        }
    }

    public void updateDamageVars() {
        this.isDamageModified = (baseDamage != damage);
        if (onAttackEffect != null) {
            onAttackEffect.setAmountFromCard();
        }
    }

    // Instead of actually adding glow effects, which CRASH OUTSIDE OF COMBAT, cycle an integer to render the glows manually
    @SpireOverride
    public void updateGlow() {
        float delta = Gdx.graphics.getDeltaTime();
        if (this.isGlowing) {
            float newValue = ReflectionHacks.getPrivate(this, AbstractCard.class, "glowTimer");
            newValue -= delta;
            if (newValue < 0.0F) {
                fakeGlowList[glowIndex] = 1.2f;
                glowIndex = (glowIndex + 1) % fakeGlowList.length;
                newValue = 0.5F;
            }
            ReflectionHacks.setPrivate(this, AbstractCard.class, "glowTimer", newValue);
        }

        for (int i = 0; i < fakeGlowList.length; i++) {
            fakeGlowList[i] -= delta;
        }
    }

    public void updateHeal(int amount) {
        this.baseHeal = Math.max(1, amount);
        this.currentHealth = this.heal = this.baseHeal;
        this.upgradedHeal = true;
    }

    public void updateHitCount(int amount) {
        this.baseHitCount = Math.max(0, amount);
        this.hitCount = this.baseHitCount;
        this.upgradedHitCount = true;
        if (onAttackEffect != null) {
            onAttackEffect.setAmountFromCard();
        }
    }

    @Override
    public void updateHoverLogic() {
        this.hb.update();

        if (this.hb.hovered) {
            this.hover();
            this.hoverDuration += EUI.delta();
            setRenderTip(this.hoverDuration > 0.2F && !Settings.hideCards);
        }
        else {
            this.unhover();
        }
    }

    public void updateRightCount(int amount) {
        this.baseRightCount = Math.max(0, amount);
        this.rightCount = this.baseRightCount;
        this.upgradedRightCount = true;
        if (onBlockEffect != null) {
            onBlockEffect.setAmountFromCard();
        }
    }

    @Override
    public void upgrade() {
        if (canUpgrade()) {
            onUpgrade();
            int minPossibleForm = GridCardSelectScreenPatches.clampUpgradeForm(auxiliaryData.form, cardData.branchFactor, timesUpgraded, maxForms());
            changeForm(minPossibleForm, timesUpgraded, timesUpgraded + 1);
        }
    }

    @Override
    protected void upgradeName() {
        ++this.timesUpgraded;
        this.upgraded = true;
        this.initializeName();
    }

    // Should not be used directly unless called through a method that does not know that this is a PCLCard
    @Override
    public final void use(AbstractPlayer p1, AbstractMonster m1) {
        PCLUseInfo info = CombatManager.playerSystem.generateInfo(this, p1, CustomTargeting.getCardTarget(this));
        onUse(info);
    }

    // Used by summons when triggered, as power effects should only be cast when the summon is first summoned
    public void useEffectsWithoutPowers(PCLUseInfo info) {
        doNonPowerEffects(be -> be.use(info, PCLActions.bottom));
    }
}