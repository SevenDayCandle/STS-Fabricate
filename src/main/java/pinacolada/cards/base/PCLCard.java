package pinacolada.cards.base;

import basemod.ReflectionHacks;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.SoulboundField;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.evacipated.cardcrawl.modthespire.lib.SpireSuper;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.cards.green.Tactician;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FlightPower;
import com.megacrit.cardcrawl.powers.FocusPower;
import com.megacrit.cardcrawl.powers.LockOnPower;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.*;
import extendedui.interfaces.delegates.*;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUICardPreview;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.ColoredString;
import extendedui.utilities.ColoredTexture;
import extendedui.utilities.EUIClassUtils;
import pinacolada.actions.PCLActions;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.cardText.PCLCardText;
import pinacolada.cards.base.fields.*;
import pinacolada.cards.base.tags.CardTagItem;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.effects.EffekseerEFK;
import pinacolada.effects.PCLAttackVFX;
import pinacolada.effects.card.PCLCardGlowBorderEffect;
import pinacolada.interfaces.listeners.OnRemovedFromDeckListener;
import pinacolada.interfaces.listeners.OnSetFormListener;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.interfaces.markers.SummonOnlyMove;
import pinacolada.misc.CombatManager;
import pinacolada.misc.PCLUseInfo;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.patches.screens.GridCardSelectScreenMultiformPatches;
import pinacolada.powers.PCLPower;
import pinacolada.powers.replacement.PCLLockOnPower;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.skills.Skills;
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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public abstract class PCLCard extends AbstractCard implements TooltipProvider, EditorCard, OnRemovedFromDeckListener, CustomSavable<PCLCardSaveData>
{
    protected static final TextureAtlas CARD_ATLAS = ReflectionHacks.getPrivateStatic(AbstractCard.class, "cardAtlas");
    protected static final Color COLORLESS_ORB_COLOR = new Color(0.7f, 0.7f, 0.7f, 1);
    protected static final Color CURSE_COLOR = new Color(0.22f, 0.22f, 0.22f, 1);
    protected static final Color COLOR_SECRET = new Color(0.2f, 0.99f, 0.6f, 1f);
    protected static final Color COLOR_ULTRA_RARE = new Color(0.99f, 0.3f, 0.2f, 1f);
    protected static final Color HOVER_IMG_COLOR = new Color(1f, 0.815f, 0.314f, 0.8f);
    protected static final Color SELECTED_CARD_COLOR = new Color(0.5f, 0.9f, 0.9f, 1f);
    protected static final String UNPLAYABLE_MESSAGE = CardCrawlGame.languagePack.getCardStrings(Tactician.ID).EXTENDED_DESCRIPTION[0];
    protected static final float SHADOW_OFFSET_X = 18f * Settings.scale;
    protected static final float SHADOW_OFFSET_Y = 14f * Settings.scale;
    public static final Color CARD_TYPE_COLOR = new Color(0.35F, 0.35F, 0.35F, 1.0F);
    public static final Color REGULAR_GLOW_COLOR = new Color(0.2F, 0.9F, 1.0F, 0.25F);
    public static final Color SHADOW_COLOR = new Color(0, 0, 0, 0.25f);
    public static final Color SYNERGY_GLOW_COLOR = new Color(1, 0.843f, 0, 0.25f);
    public static final int CHAR_OFFSET = 97;
    public static AbstractPlayer player = null;
    public static Random rng = null;
    public static boolean canCropPortraits = true;

    public final Skills skills = new Skills();
    public final ArrayList<EUITooltip> tooltips = new ArrayList<>();
    public final ArrayList<PCLAugment> augments = new ArrayList<>();
    public final PCLCardAffinities affinities;
    public final PCLCardData cardData;
    public final PCLCardText cardText;
    public ColoredString bottomText;
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
    public boolean renderTip;
    public boolean showTypeText = true;
    public boolean upgradedHeal = false;
    public boolean upgradedHitCount = false;
    public boolean upgradedRightCount = false;
    public float hoverDuration;
    public int baseHitCount = 1;
    public int baseRightCount = 1;
    public int currentHealth = 1; // Used for storing the card's current HP in battle
    public int hitCount = 1;
    public int maxUpgradeLevel;
    public int rightCount = 1;
    public transient AbstractCreature owner;
    public transient PCLCard parent;
    public transient PowerFormulaDisplay formulaDisplay;
    protected ColoredTexture portraitForeground;
    protected ColoredTexture portraitImg;
    protected boolean playAtEndOfTurn;
    protected final ArrayList<PCLCardGlowBorderEffect> glowList = new ArrayList<>();

    protected PCLCard(PCLCardData cardData)
    {
        this(cardData, cardData.ID, cardData.imagePath, cardData.getCost(0), cardData.cardType, cardData.cardColor, cardData.cardRarity, cardData.cardTarget.cardTarget, 0, 0, null);
    }

    protected PCLCard(PCLCardData cardData, Object input)
    {
        this(cardData, cardData.ID, cardData.imagePath, cardData.getCost(0), cardData.cardType, cardData.cardColor, cardData.cardRarity, cardData.cardTarget.cardTarget, 0, 0, input);
    }

    protected PCLCard(PCLCardData cardData, int form, int timesUpgraded)
    {
        this(cardData, cardData.ID, cardData.imagePath, cardData.getCost(0), cardData.cardType, cardData.cardColor, cardData.cardRarity, cardData.cardTarget.cardTarget, form, timesUpgraded, null);
    }

    protected PCLCard(PCLCardData cardData, int form, int timesUpgraded, Object input)
    {
        this(cardData, cardData.ID, cardData.imagePath, cardData.getCost(0), cardData.cardType, cardData.cardColor, cardData.cardRarity, cardData.cardTarget.cardTarget, form, timesUpgraded, input);
    }

    protected PCLCard(PCLCardData cardData, String id, String imagePath, int cost, CardType type, CardColor color, CardRarity rarity, CardTarget target)
    {
        this(cardData, id, imagePath, cost, type, color, rarity, target, 0, 0, null);
    }

    protected PCLCard(PCLCardData cardData, String id, String imagePath, int cost, CardType type, CardColor color, CardRarity rarity, CardTarget target, int form, int timesUpgraded, Object input)
    {
        super(id, cardData.strings.NAME, "status/beta", "status/beta", cost, "", type, color, rarity, target);
        this.rawDescription = cardData.strings.NAME; // Only writing this to ensure mods that check for this at runtime don't crash; this gets overwritten anyways
        this.cardData = cardData;
        this.cardText = new PCLCardText(this);
        this.affinities = new PCLCardAffinities(this);
        this.playAtEndOfTurn = cardData.playAtEndOfTurn;
        this.maxUpgradeLevel = cardData.maxUpgradeLevel;

        for (int i = 0; i < cardData.slots; i++)
        {
            augments.add(null);
        }

        setupExtraTags();
        setupProperties(cardData, form, timesUpgraded);
        setup(input);
        setForm(form, timesUpgraded);
        setupImages(imagePath);
    }

    @SafeVarargs
    public static <T> T[] array(T... items)
    {
        return EUIUtils.array(items);
    }

    public static PCLCard cast(AbstractCard card)
    {
        return EUIUtils.safeCast(card, PCLCard.class);
    }

    protected static int[] nums(int damage, int block)
    {
        return nums(damage, block, 0, 0);
    }

    protected static int[] nums(int damage, int block, int magicNumber)
    {
        return nums(damage, block, magicNumber, 0);
    }

    protected static int[] nums(int damage, int block, int magicNumber, int secondaryValue)
    {
        return nums(damage, block, magicNumber, secondaryValue, 1);
    }

    protected static int[] nums(int damage, int block, int magicNumber, int secondaryValue, int hitCount)
    {
        return nums(damage, block, magicNumber, secondaryValue, hitCount, 1);
    }

    protected static int[] nums(int damage, int block, int magicNumber, int secondaryValue, int hitCount, int rightCount)
    {
        return new int[]{damage, block, magicNumber, secondaryValue, hitCount, rightCount};
    }

    protected static PCLCardData register(Class<? extends PCLCard> type)
    {
        return PCLCard.register(type, PGR.core);
    }

    protected static PCLCardData register(Class<? extends PCLCard> type, PCLResources<?,?,?,?> resources)
    {
        return PCLCard.registerCardData(new PCLCardData(type, resources));
    }

    protected static PCLCardData registerCardData(PCLCardData cardData)
    {
        return PCLCardData.registerCardData(cardData);
    }

    protected static int[] ups(int damage, int block)
    {
        return ups(damage, block, 0, 0);
    }

    protected static int[] ups(int damage, int block, int magicNumber)
    {
        return ups(damage, block, magicNumber, 0);
    }

    protected static int[] ups(int damage, int block, int magicNumber, int secondaryValue)
    {
        return ups(damage, block, magicNumber, secondaryValue, 0);
    }

    protected static int[] ups(int damage, int block, int magicNumber, int secondaryValue, int hitCount)
    {
        return ups(damage, block, magicNumber, secondaryValue, hitCount, 0);
    }

    protected static int[] ups(int damage, int block, int magicNumber, int secondaryValue, int hitCount, int rightCount)
    {
        return new int[]{damage, block, magicNumber, secondaryValue, hitCount, rightCount};
    }

    protected PMove_StackCustomPower addApplyPower(PCLCardTarget target, int amount, PTrigger... effects) {
        PMove_StackCustomPower added = getApplyPower(target, amount, effects);
        getEffects().add(added);
        return added;
    }

    public void addAttackDisplay(AbstractPower p, float oldDamage, float tempDamage) {
        if (formulaDisplay != null && baseDamage > 0) {
            formulaDisplay.addAttackPower(p, oldDamage, tempDamage);
        }
    }

    public void addAttackDisplay(PCLAffinity p, float oldDamage, float tempDamage) {
        if (formulaDisplay != null && baseDamage > 0) {
            formulaDisplay.addAttackAffinity(p, oldDamage, tempDamage);
        }
    }

    public void addAttackResult(float oldDamage, float tempDamage) {
        if (formulaDisplay != null && baseDamage > 0) {
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
        } else {
            augments.add(augment);
        }
        if (save) {
            auxiliaryData.augments.add(augment.ID);
        }
        augment.onAddToCard(this);
        refresh(null);
    }

    protected PCardPrimary_GainBlock addBlockMove() {
        onBlockEffect = new PCardPrimary_GainBlock(this);
        return onBlockEffect;
    }

    protected PCardPrimary_DealDamage addDamageMove() {
        onAttackEffect = new PCardPrimary_DealDamage(this);
        return onAttackEffect;
    }

    protected PCardPrimary_DealDamage addDamageMove(PCLAttackVFX attackEffect) {
        onAttackEffect = new PCardPrimary_DealDamage(this, attackEffect.key);
        return onAttackEffect;
    }

    protected PCardPrimary_DealDamage addDamageMove(AbstractGameAction.AttackEffect attackEffect) {
        onAttackEffect = new PCardPrimary_DealDamage(this, attackEffect);
        return onAttackEffect;
    }

    protected PCardPrimary_DealDamage addDamageMove(EffekseerEFK efx) {
        onAttackEffect = new PCardPrimary_DealDamage(this).setDamageEffect(efx);
        return onAttackEffect;
    }

    protected PCardPrimary_DealDamage addDamageMove(AbstractGameAction.AttackEffect attackEffect, EffekseerEFK efx) {
        onAttackEffect = new PCardPrimary_DealDamage(this, attackEffect).setDamageEffect(efx);
        return onAttackEffect;
    }

    public void addDefendDisplay(AbstractPower p, float oldDamage, float tempDamage) {
        if (formulaDisplay != null && baseBlock > 0) {
            formulaDisplay.addDefendPower(p, oldDamage, tempDamage);
        }
    }

    public void addDefendDisplay(PCLAffinity p, float oldDamage, float tempDamage) {
        if (formulaDisplay != null && baseBlock > 0) {
            formulaDisplay.addDefendAffinity(p, oldDamage, tempDamage);
        }
    }

    public void addDefendResult(float oldDamage, float tempDamage) {
        if (formulaDisplay != null && baseBlock > 0) {
            formulaDisplay.setDefendResult(oldDamage, tempDamage);
        }
    }

    protected PMove_StackCustomPower addGainPower(PTrigger... effect) {
        return addApplyPower(PCLCardTarget.Self, -1, effect);
    }

    protected PMove_StackCustomPower addGainPower(int amount, PTrigger... effect) {
        return addApplyPower(PCLCardTarget.Self, amount, effect);
    }

    protected PSpecialCond addSpecialCond(int descIndex, FuncT3<Boolean, PSpecialCond, PCLUseInfo, Boolean> onUse) {
        return addSpecialCond(descIndex, onUse, 1, 0);
    }

    protected PSpecialCond addSpecialCond(int descIndex, FuncT3<Boolean, PSpecialCond, PCLUseInfo, Boolean> onUse, int amount) {
        return addSpecialCond(descIndex, onUse, amount, 0);
    }

    protected PSpecialCond addSpecialCond(int descIndex, FuncT3<Boolean, PSpecialCond, PCLUseInfo, Boolean> onUse, int amount, int extra) {
        PSpecialCond move = (PSpecialCond) getSpecialCond(descIndex, onUse, amount, extra).setSource(this).onAddToCard(this);
        getEffects().add(move);
        return move;
    }

    protected PSpecialSkill addSpecialMove(int descIndex, ActionT2<PSpecialSkill, PCLUseInfo> onUse) {
        return addSpecialMove(cardData.strings.EXTENDED_DESCRIPTION[descIndex], onUse, 1, 0);
    }

    protected PSpecialSkill addSpecialMove(int descIndex, ActionT2<PSpecialSkill, PCLUseInfo> onUse, int amount) {
        return addSpecialMove(cardData.strings.EXTENDED_DESCRIPTION[descIndex], onUse, amount, 0);
    }

    protected PSpecialSkill addSpecialMove(int descIndex, ActionT2<PSpecialSkill, PCLUseInfo> onUse, int amount, int extra) {
        return addSpecialMove(cardData.strings.EXTENDED_DESCRIPTION[descIndex], onUse, amount, extra);
    }

    protected PSpecialSkill addSpecialMove(String description, ActionT2<PSpecialSkill, PCLUseInfo> onUse, int amount) {
        return addSpecialMove(description, onUse, amount, 0);
    }

    protected PSpecialSkill addSpecialMove(String description, ActionT2<PSpecialSkill, PCLUseInfo> onUse, int amount, int extra) {
        PSpecialSkill move = (PSpecialSkill) getSpecialMove(description, onUse, amount, extra).setSource(this).onAddToCard(this);
        getEffects().add(move);
        return move;
    }

    protected PSpecialPowerSkill addSpecialPower(int descIndex, FuncT2<? extends PCLPower, PSpecialPowerSkill, PCLUseInfo> onUse) {
        return addSpecialPower(cardData.strings.EXTENDED_DESCRIPTION[descIndex], onUse, 1, 0);
    }

    protected PSpecialPowerSkill addSpecialPower(int descIndex, FuncT2<? extends PCLPower, PSpecialPowerSkill, PCLUseInfo> onUse, int amount) {
        return addSpecialPower(cardData.strings.EXTENDED_DESCRIPTION[descIndex], onUse, amount, 0);
    }

    protected PSpecialPowerSkill addSpecialPower(int descIndex, FuncT2<? extends PCLPower, PSpecialPowerSkill, PCLUseInfo> onUse, int amount, int extra) {
        return addSpecialPower(cardData.strings.EXTENDED_DESCRIPTION[descIndex], onUse, amount, extra);
    }

    protected PSpecialPowerSkill addSpecialPower(String description, FuncT2<? extends PCLPower, PSpecialPowerSkill, PCLUseInfo> onUse, int amount) {
        return addSpecialPower(description, onUse, amount, 0);
    }

    protected PSpecialPowerSkill addSpecialPower(String description, FuncT2<? extends PCLPower, PSpecialPowerSkill, PCLUseInfo> onUse, int amount, int extra) {
        PSpecialPowerSkill move = (PSpecialPowerSkill) getSpecialPower(description, onUse, amount, extra).setSource(this).onAddToCard(this);
        getEffects().add(move);
        return move;
    }

    public int changeForm(Integer form, int timesUpgraded) {
        return changeForm(form, timesUpgraded, timesUpgraded);
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
        int costDiff = cost - (prevUpgrade > 0 ? (baseCost + cardData.getCostUpgrade(this.auxiliaryData.form)) : baseCost);


        form = MathUtils.clamp(form, 0, this.getMaxForms() - 1);

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

        int newCost = timesUpgraded > 0 ? cardData.getCost(form) + cardData.getCostUpgrade(form) : cardData.getCost(form);
        setCost(newCost + costDiff);
        upgradedCost = cost != newCost;

        return setForm(form, timesUpgraded);
    }

    protected void doEffects(ActionT1<PSkill<?>> action) {
        for (PSkill<?> be : getFullEffects()) {
            action.invoke(be);
        }
    }

    protected void doNonPowerEffects(ActionT1<PSkill<?>> action) {
        for (PSkill<?> be : getFullEffects()) {
            if (!(be instanceof SummonOnlyMove))
            {
                action.invoke(be);
            }
        }
    }

    public void fullReset() {
        this.clearSkills();
        this.onAttackEffect = null;
        this.onBlockEffect = null;

        setupProperties(cardData, this.auxiliaryData.form, timesUpgraded);
        setup(null);
        setForm(this.auxiliaryData.form, timesUpgraded);
    }

    protected PMove_StackCustomPower getApplyPower(PCLCardTarget target, int amount, PTrigger... effects) {
        Integer[] powerIndexes = EUIUtils.range(getPowerEffects().size(), getPowerEffects().size() + effects.length - 1);
        for (PTrigger effect : effects) {
            addPowerMove(effect);
        }
        return (PMove_StackCustomPower) new PMove_StackCustomPower(target, amount, powerIndexes).setSource(this).onAddToCard(this);
    }

    @Deprecated
    public String getAttributeString(char attributeID) {
        switch (attributeID) {
            case 'D':
                return String.valueOf(damage);
            case 'B':
                return String.valueOf(block);
            case 'M':
                return String.valueOf(magicNumber);
            case 'S':
                return String.valueOf(heal);
            case 'K':
                return String.valueOf(misc);
            case 'X':
                return GameUtilities.inBattle() && player != null ? String.valueOf(getXValue()) : PGR.core.strings.subjects_x;
            default:
                return "?";
        }
    }

    public PCLAugment getAugment(int index) {
        return augments.size() > index ? augments.get(index) : null;
    }

    public List<PSkill<?>> getAugmentSkills() {
        return EUIUtils.mapAsNonnull(augments, aug -> aug != null ? aug.skill : null);
    }

    public List<PCLAugment> getAugments() {
        return EUIUtils.filter(augments, Objects::nonNull);
    }

    public ColoredString getBlockString() {
        if (isBlockModified) {
            return new ColoredString(block, block >= baseBlock ? Settings.GREEN_TEXT_COLOR : Settings.RED_TEXT_COLOR);
        }

        return new ColoredString(baseBlock, Settings.CREAM_COLOR);
    }

    public ColoredString getBottomText() {
        String loadoutName = cardData.getLoadoutName();
        return (loadoutName == null || loadoutName.isEmpty()) ? null : new ColoredString(loadoutName, Settings.CREAM_COLOR);
    }

    @Override
    public PCLCard getCachedUpgrade() {
        PCLCard upgrade = cardData.tempCard;

        if (upgrade == null || upgrade.uuid != this.uuid || (upgrade.timesUpgraded != (timesUpgraded + 1))) {
            upgrade = cardData.tempCard = (PCLCard) this.makeSameInstanceOf();
            upgrade.changeForm(auxiliaryData.form, timesUpgraded);
            upgrade.isPreview = true;
            upgrade.upgrade();
            upgrade.displayUpgrades();
        }

        return upgrade;
    }

    public PCardPrimary_GainBlock getCardBlock()
    {
        return onBlockEffect;
    }

    public PCardPrimary_DealDamage getCardDamage()
    {
        return onAttackEffect;
    }

    public ColoredTexture getCardAttributeBanner() {
        if (rarity == PCLEnum.CardRarity.LEGENDARY || rarity == PCLEnum.CardRarity.SECRET)
        {
            return new ColoredTexture((isPopup ? PCLCoreImages.CardUI.cardBannerAttribute2L : PCLCoreImages.CardUI.cardBannerAttribute2).texture(), getRarityColor());
        }
        return new ColoredTexture((isPopup ? PCLCoreImages.CardUI.cardBannerAttributeL : PCLCoreImages.CardUI.cardBannerAttribute).texture(), getRarityColor());
    }

    protected Texture getCardBackground() {
        PCLResources<?,?,?,?> resources = PGR.getResources(color);
        if (resources == null || resources.images == null)
        {
            resources = PGR.core;
        }

        if (type == PCLEnum.CardType.SUMMON)
        {
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
        if (shouldUsePCLFrame())
        {
            if (rarity == PCLEnum.CardRarity.LEGENDARY || rarity == PCLEnum.CardRarity.SECRET)
            {
                return (isPopup ? PCLCoreImages.CardUI.cardBanner2L : PCLCoreImages.CardUI.cardBanner2).texture();
            }
            return (isPopup ? PCLCoreImages.CardUI.cardBannerL : PCLCoreImages.CardUI.cardBanner).texture();
        }
        return null;
    }

    protected TextureAtlas.AtlasRegion getCardBannerVanillaRegion()
    {
        if (isPopup)
        {
            switch (rarity)
            {
                case RARE:
                    return ImageMaster.CARD_BANNER_RARE_L;
                case UNCOMMON:
                    return ImageMaster.CARD_BANNER_UNCOMMON_L;
                default:
                    return ImageMaster.CARD_BANNER_COMMON_L;
            }
        }
        else
        {
            switch (rarity)
            {
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
        return shouldUsePCLFrame() ? ImageMaster.CARD_SKILL_BG_SILHOUETTE : super.getCardBgAtlas();
    }

    public ColoredString getColoredAttributeString(char attributeID) {
        switch (attributeID) {
            case 'D':
                return getDamageString();
            case 'B':
                return getBlockString();
            case 'M':
                return getMagicNumberString();
            case 'S':
                return getHPString();
            case 'K':
                return getSpecialVariableString();
            case 'X':
                return getXString(false);
            case 'x':
                return getXString(true);
            default:
                return new ColoredString("?", Settings.RED_TEXT_COLOR);
        }
    }

    protected ColoredString getCostString() {
        final ColoredString result = new ColoredString();

        if (cost == -1) {
            result.text = PGR.core.strings.subjects_x;
        } else {
            result.text = freeToPlay() ? "0" : Integer.toString(Math.max(0, this.costForTurn));
        }

        if (player != null && player.hand.contains(this) && (!this.hasEnoughEnergy() || GameUtilities.isUnplayableThisTurn(this))) {
            result.color = new Color(1f, 0.3f, 0.3f, transparency);
        } else if ((upgradedCost && isCostModified) || costForTurn < cost || (cost > 0 && this.freeToPlay())) {
            result.color = new Color(0.4f, 1f, 0.4f, transparency);
        } else {
            result.color = new Color(1f, 1f, 1f, transparency);
        }

        return result;
    }

    public Texture getPortraitImageTexture() {
        return portraitImg.texture;
    }

    public int hitCount() {
        return hitCount;
    }

    public int hitCountBase() {
        return baseHitCount;
    }

    public int rightCount() {
        return rightCount;
    }

    public int rightCountBase() {
        return baseRightCount;
    }

    public void loadImage(String path) {
       loadImage(path, false);
    }

    public void loadImage(String path, boolean refresh) {
        Texture t = EUIRM.getTexture(path, true, refresh, true);
        if (t == null) {
            t = EUIRM.getLocalTexture(path, true, refresh, true);
            if (t == null) {
                path = QuestionMark.DATA.imagePath;
                t = EUIRM.getTexture(path, true, false, true);
            }
        }
        assetUrl = path;
        portraitImg = new ColoredTexture(t, null);
    }

    public ColoredString getDamageString() {
        if (isDamageModified) {
            return new ColoredString(damage, damage >= baseDamage * PGR.dungeon.getDivisor() ? Settings.GREEN_TEXT_COLOR : Settings.RED_TEXT_COLOR);
        }

        return new ColoredString(baseDamage, Settings.CREAM_COLOR);
    }

    protected Texture getEnergyOrb() {
        // For non-custom cards, use the original resource card color so that colorless/curses have their resource's energy orb
        PCLResources<?,?,?,?> resources = PGR.getResources(cardData.resources.cardColor);
        if (resources == null || resources.images == null)
        {
            resources = PGR.core;
        }
        return (isPopup ? resources.images.cardEnergyOrbL : resources.images.cardEnergyOrb).texture();
    }

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

    public ColoredString getHPString() {
        return new ColoredString(currentHealth, currentHealth < heal ? Settings.RED_TEXT_COLOR : isHealModified || heal > baseHeal ? Settings.GREEN_TEXT_COLOR : Settings.CREAM_COLOR);
    }

    public ColoredString getMagicNumberString() {
        if (isMagicNumberModified) {
            return new ColoredString(magicNumber, magicNumber >= baseMagicNumber ? Settings.GREEN_TEXT_COLOR : Settings.RED_TEXT_COLOR);
        }

        return new ColoredString(baseMagicNumber, Settings.CREAM_COLOR);
    }

    public int getMaxForms() {
        return cardData != null ? cardData.maxForms : 1;
    }

    protected Texture getPortraitFrame() {
        if (shouldUsePCLFrame())
        {
            if (type == PCLEnum.CardType.SUMMON)
            {
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

    protected TextureAtlas.AtlasRegion getPortraitFrameVanillaRegion()
    {
        if (isPopup)
        {
            switch (type)
            {
                case ATTACK:
                    switch (rarity)
                    {
                        case RARE:
                            return ImageMaster.CARD_FRAME_ATTACK_RARE_L;
                        case UNCOMMON:
                            return ImageMaster.CARD_FRAME_ATTACK_UNCOMMON_L;
                        default:
                            return ImageMaster.CARD_FRAME_ATTACK_COMMON_L;
                    }
                case POWER:
                    switch (rarity)
                    {
                        case RARE:
                            return ImageMaster.CARD_FRAME_POWER_RARE_L;
                        case UNCOMMON:
                            return ImageMaster.CARD_FRAME_POWER_UNCOMMON_L;
                        default:
                            return ImageMaster.CARD_FRAME_POWER_COMMON_L;
                    }
                default:
                    switch (rarity)
                    {
                        case RARE:
                            return ImageMaster.CARD_FRAME_SKILL_RARE_L;
                        case UNCOMMON:
                            return ImageMaster.CARD_FRAME_SKILL_UNCOMMON_L;
                        default:
                            return ImageMaster.CARD_FRAME_SKILL_COMMON_L;
                    }
            }
        }
        else
        {
            switch (type)
            {
                case ATTACK:
                    switch (rarity)
                    {
                        case RARE:
                            return ImageMaster.CARD_FRAME_ATTACK_RARE;
                        case UNCOMMON:
                            return ImageMaster.CARD_FRAME_ATTACK_UNCOMMON;
                        default:
                            return ImageMaster.CARD_FRAME_ATTACK_COMMON;
                    }
                case POWER:
                    switch (rarity)
                    {
                        case RARE:
                            return ImageMaster.CARD_FRAME_POWER_RARE;
                        case UNCOMMON:
                            return ImageMaster.CARD_FRAME_POWER_UNCOMMON;
                        default:
                            return ImageMaster.CARD_FRAME_POWER_COMMON;
                    }
                default:
                    switch (rarity)
                    {
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

    public Color getRarityColor() {

        if (rarity == PCLEnum.CardRarity.SECRET)
        {
            return COLOR_SECRET;
        }
        else if (rarity == PCLEnum.CardRarity.LEGENDARY)
        {
            return COLOR_ULTRA_RARE;
        }

        return EUIGameUtils.colorForRarity(rarity);
    }

    public Texture getTypeIcon() {
        return EUIGameUtils.iconForType(type).texture();
    }

    public String getTagTipString()
    {
        ArrayList<String> tagNames = new ArrayList<>();
        for (PCLCardTag tag : PCLCardTag.values())
        {
            int value = tag.getInt(this);
            switch (value)
            {
                case 1:
                    tagNames.add(tag.getTooltip().title);
                    break;
                case -1:
                    // Only show the infinite label for cards that allow it
                    if (tag.minValue == -1)
                    {
                        tagNames.add(EUIRM.strings.generic2(tag.getTooltip().title, PGR.core.strings.subjects_infinite));
                    }
                    break;
                case 0:
                    break;
                default:
                    tagNames.add(EUIRM.strings.generic2(tag.getTooltip().title, value));
                    break;
            }
        }
        return tagNames.size() > 0 ? EUIUtils.joinStrings(PSkill.EFFECT_SEPARATOR, tagNames) + LocalizedStrings.PERIOD : "";
    }

    public Skills getSkills() {
        return skills;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return cardID;
    }

    public ArrayList<PSkill<?>> getFullEffects() {
        ArrayList<PSkill<?>> result = new ArrayList<>();
        if (onAttackEffect != null)
        {
            result.add(onAttackEffect);
        }
        if (onBlockEffect != null)
        {
            result.add(onBlockEffect);
        }
        result.addAll(getEffects());
        result.addAll(getAugmentSkills());
        return result;
    }

    public PTrigger addPowerMove(PTrigger effect) {
        PTrigger added = (PTrigger) effect.setSource(this).onAddToCard(this);
        getPowerEffects().add(added);
        return added;
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

    protected PSpecialCond getSpecialCond(int descIndex, FuncT3<Boolean, PSpecialCond, PCLUseInfo, Boolean> onUse, int amount) {
        return getSpecialCond(descIndex, onUse, amount, 0);
    }

    protected PSpecialCond getSpecialCond(int descIndex, FuncT3<Boolean, PSpecialCond, PCLUseInfo, Boolean> onUse, int amount, int extra) {
        return new PSpecialCond(cardData, descIndex, onUse, amount, extra);
    }

    protected PSpecialSkill getSpecialMove(int descIndex, ActionT2<PSpecialSkill, PCLUseInfo> onUse) {
        return getSpecialMove(cardData.strings.EXTENDED_DESCRIPTION[descIndex], onUse, 1, 0);
    }

    protected PSpecialSkill getSpecialMove(int descIndex, ActionT2<PSpecialSkill, PCLUseInfo> onUse, int amount) {
        return getSpecialMove(cardData.strings.EXTENDED_DESCRIPTION[descIndex], onUse, amount, 0);
    }

    protected PSpecialSkill getSpecialMove(int descIndex, ActionT2<PSpecialSkill, PCLUseInfo> onUse, int amount, int extra) {
        return getSpecialMove(cardData.strings.EXTENDED_DESCRIPTION[descIndex], onUse, amount, extra);
    }

    protected PSpecialSkill getSpecialMove(String description, ActionT2<PSpecialSkill, PCLUseInfo> onUse, int amount, int extra) {
        return new PSpecialSkill(this.cardID + this.getEffects().size(), description, onUse, amount, extra);
    }

    protected PSpecialSkill getSpecialMove(FuncT1<String, PSpecialSkill> strFunc, ActionT2<PSpecialSkill, PCLUseInfo> onUse, int amount) {
        return getSpecialMove(strFunc, onUse, amount, 0);
    }

    protected PSpecialSkill getSpecialMove(FuncT1<String, PSpecialSkill> strFunc, ActionT2<PSpecialSkill, PCLUseInfo> onUse, int amount, int extra) {
        return new PSpecialSkill(this.cardID + this.getEffects().size(), strFunc, onUse, amount, extra);
    }

    protected PSpecialPowerSkill getSpecialPower(int descIndex, FuncT2<? extends PCLPower, PSpecialPowerSkill, PCLUseInfo> onUse) {
        return getSpecialPower(cardData.strings.EXTENDED_DESCRIPTION[descIndex], onUse, 1, 0);
    }

    protected PSpecialPowerSkill getSpecialPower(int descIndex, FuncT2<? extends PCLPower, PSpecialPowerSkill, PCLUseInfo> onUse, int amount) {
        return getSpecialPower(cardData.strings.EXTENDED_DESCRIPTION[descIndex], onUse, amount, 0);
    }

    protected PSpecialPowerSkill getSpecialPower(int descIndex, FuncT2<? extends PCLPower, PSpecialPowerSkill, PCLUseInfo> onUse, int amount, int extra) {
        return getSpecialPower(cardData.strings.EXTENDED_DESCRIPTION[descIndex], onUse, amount, extra);
    }

    protected PSpecialPowerSkill getSpecialPower(String description, FuncT2<? extends PCLPower, PSpecialPowerSkill, PCLUseInfo> onUse, int amount, int extra) {
        return new PSpecialPowerSkill(this.cardID + this.getEffects().size(), description, onUse, amount, extra);
    }

    protected PSpecialPowerSkill getSpecialPower(FuncT1<String, PSpecialPowerSkill> strFunc, FuncT2<? extends PCLPower, PSpecialPowerSkill, PCLUseInfo> onUse, int amount) {
        return getSpecialPower(strFunc, onUse, amount, 0);
    }

    protected PSpecialPowerSkill getSpecialPower(FuncT1<String, PSpecialPowerSkill> strFunc, FuncT2<? extends PCLPower, PSpecialPowerSkill, PCLUseInfo> onUse, int amount, int extra) {
        return new PSpecialPowerSkill(this.cardID + this.getEffects().size(), strFunc, onUse, amount, extra);
    }

    public ColoredString getSpecialVariableString() {
        return new ColoredString(misc, misc > 0 ? Settings.GREEN_TEXT_COLOR : Settings.CREAM_COLOR);
    }

    @Override
    public List<EUITooltip> getTips() {
        return tooltips;
    }

    @Override
    public EUICardPreview getPreview() {
        return PCLCardPreviews.getCardPreview(this);
    }

    @Override
    public boolean isPopup() {
        return isPopup;
    }

    public void generateDynamicTooltips(ArrayList<EUITooltip> dynamicTooltips) {
        // Only show these tooltips outside of combat
        if (!GameUtilities.inBattle() || isPopup || (player != null && player.masterDeck.contains(this))) {
            if (isSoulbound()) {
                dynamicTooltips.add(PGR.core.tooltips.soulbound);
            }
            if (cardData.canToggleFromPopup && (upgraded || cardData.unUpgradedCanToggleForms)) {
                dynamicTooltips.add(PGR.core.tooltips.multiform);
            }
            else if (isUnique()) {
                dynamicTooltips.add(PGR.core.tooltips.unique);
            }
        }

        // Add tips from tags
        for (PCLCardTag tag : PCLCardTag.getAll()) {
            if (tag.has(this)) {
                dynamicTooltips.add(tag.getTip());
            }
        }

        // Do not show the Normal damage tooltip for card tooltips
        EUITooltip attackTooltip = attackType.getTooltip();
        if (attackTooltip != PGR.core.tooltips.normalDamage) {
            dynamicTooltips.add(attackTooltip);
        }

    }

    @Override
    public void setIsPreview(boolean value) {
        isPreview = value;
    }

    protected String getTypeText() {
        return EUIGameUtils.textForType(this.type);
    }

    // Upgrade name is determined by number of upgrades and the current form (if multiple exist)
    // E.g. Form 0 -> +A, Form 1 -> +B, etc.
    protected String getUpgradeName() {
        if (!upgraded)
        {
            return cardData.strings.NAME;
        }
        StringBuilder sb = new StringBuilder(cardData.strings.NAME);
        sb.append("+");

        if (maxUpgradeLevel < 0 || maxUpgradeLevel > 1) {
            sb.append(this.timesUpgraded);
        }

        if (this.cardData.maxForms > 1) {
            char appendix = (char) (auxiliaryData.form + CHAR_OFFSET);
            sb.append(appendix);
        }

        return sb.toString();
    }

    public ColoredString getXString(boolean onlyInBattle) {
        if (GameUtilities.inBattle() && player != null) {
            int value = getXValue();
            if (value >= 0) {
                return new ColoredString(onlyInBattle ? " (" + value + ")" : value, Settings.GREEN_TEXT_COLOR);
            }
        }

        return new ColoredString(onlyInBattle ? "" : "X", Settings.CREAM_COLOR);
    }

    public int getXValue() {
        return -1;
    }

    public boolean isAoE() {
        return isMultiDamage;
    }

    protected boolean isEffectPlayable(AbstractMonster m) {
        PCLUseInfo info = new PCLUseInfo(this, getSourceCreature(), m);
        for (PSkill<?> be : getFullEffects()) {
            if (!be.canPlay(info)) {
                return false;
            }
        }
        return true;
    }

    public boolean isOnScreen() {
        return current_y >= -200f * Settings.scale && current_y <= Settings.HEIGHT + 200f * Settings.scale;
    }

    public boolean isSoulbound() {
        return SoulboundField.soulbound.get(this);
    }

    public boolean isStarter() {
        return GameUtilities.isStarter(this);
    }

    public boolean isUnique() {
        return cardData.unique;
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
        return makePowerString(rawDescription);
    }

    // Block subeffects may affect the final block output (but they should not be triggering any other triggers)
    protected float modifyBlock(PCLUseInfo info, float amount) {
        if (onBlockEffect != null)
        {
            amount = onBlockEffect.modifyBlock(info, amount);
        }
        for (PSkill<?> be : getFullEffects()) {
            amount = be.modifyBlock(info, amount);
        }
        return amount;
    }

    protected float modifyDamage(PCLUseInfo info, float amount) {
        for (PSkill<?> be : getFullEffects()) {
            amount = be.modifyDamage(info, amount);
        }
        return amount;
    }

    protected float modifyHitCount(PCLUseInfo info, float amount) {
        for (PSkill<?> be : getFullEffects()) {
            amount = be.modifyHitCount(info, amount);
        }
        return amount;
    }

    protected float modifyMagicNumber(PCLUseInfo info, float amount) {
        for (PSkill<?> be : getFullEffects()) {
            amount = be.modifyMagicNumber(info, amount);
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

    public void onDrag(AbstractMonster m) {
        doEffects(be -> be.onDrag(m));
    }

    @Override
    public void onRemovedFromDeck()
    {
        for (PCLAugment augment : getAugments()) {
            if (augment.canRemove()) {
                PGR.dungeon.addAugment(augment.ID, 1);
            }
        }
    }

    protected void onUpgrade() {
    }

    public void onUse(PCLUseInfo info) {
        if (dontTriggerOnUseCard) {
            doEffects(be -> be.triggerOnEndOfTurn(true));
        } else {
            doEffects(be -> be.use(info));
        }
    }

    // Used by summons when triggered, as power effects should only be cast when the summon is first summoned
    public void useEffectsWithoutPowers(PCLUseInfo info)
    {
        doNonPowerEffects(be -> be.use(info));
    }

    public AbstractCreature getSourceCreature()
    {
        return owner != null ? owner : player;
    }

    // Update damage, block, and magic number from the powers on a given target
    // Every step of the calculation is recorded for display in the damage formula widget
    public void refresh(AbstractMonster enemy) {
        PCLUseInfo info = new PCLUseInfo(this, getSourceCreature(), enemy);
        boolean applyEnemyPowers = (enemy != null && !GameUtilities.isDeadOrEscaped(enemy));
        float tempBlock = baseBlock;
        float tempDamage = baseDamage;
        float tempMagicNumber = CombatManager.onModifyMagicNumber(baseMagicNumber, this);
        tempDamage = modifyDamage(info, tempDamage);
        tempBlock = modifyBlock(info, tempBlock);

        AbstractCreature owner = getSourceCreature();
        if (owner != null) {
            int applyCount = attackType == PCLAttackType.Brutal ? 2 : 1;

            if (owner instanceof AbstractPlayer)
            {
                for (AbstractRelic r : ((AbstractPlayer) owner).relics) {
                    tempDamage = r.atDamageModify(tempDamage, this);
                }
            }

            if (attackType.useFocus) {
                for (AbstractPower p : owner.powers) {
                    float oldBlock = tempBlock;
                    float oldDamage = tempDamage;
                    tempBlock = p.modifyBlock(tempBlock, this);
                    if (FocusPower.POWER_ID.equals(p.ID))
                    {
                        tempDamage += p.amount;
                    }
                    else if (p instanceof PCLPower) {
                        tempDamage = ((PCLPower) p).modifyOrbOutgoing(tempDamage);
                    }
                    addAttackDisplay(p, oldDamage, tempDamage);
                    addDefendDisplay(p, oldBlock, tempBlock);
                }
            } else {
                for (AbstractPower p : owner.powers) {
                    float oldBlock = tempBlock;
                    float oldDamage = tempDamage;
                    tempBlock = p.modifyBlock(tempBlock, this);
                    for (int i = 0; i < applyCount; i++) {
                        tempDamage = p.atDamageGive(tempDamage, damageTypeForTurn, this);
                    }
                    addAttackDisplay(p, oldDamage, tempDamage);
                    addDefendDisplay(p, oldBlock, tempBlock);
                }
            }

            tempBlock = CombatManager.playerSystem.modifyBlock(tempBlock, parent != null ? parent : this, this, enemy != null ? enemy : owner);
            tempDamage = CombatManager.playerSystem.modifyDamage(tempDamage, parent != null ? parent : this,this, enemy);

            for (AbstractPower p : owner.powers) {
                tempBlock = p.modifyBlockLast(tempBlock);
            }

            if (applyEnemyPowers) {
                if (attackType.bypassFlight && EUIUtils.any(enemy.powers, po -> FlightPower.POWER_ID.equals(po.ID))) {
                    tempDamage *= 2f * applyCount;
                }

                if (attackType.useFocus)
                {
                    for (AbstractPower p : enemy.powers) {
                        float oldDamage = tempDamage;
                        // Lock-on calculations are hardcoded in AbstractOrb so we are falling back on PCLLockOn's multiplier for now
                        if (LockOnPower.POWER_ID.equals(p.ID))
                        {
                            tempDamage *= PCLLockOnPower.getOrbMultiplier();
                        }
                        else if (p instanceof PCLPower)
                        {
                            for (int i = 0; i < applyCount; i++) {
                                tempDamage = ((PCLPower) p).modifyOrbIncoming(tempDamage);
                            }
                        }
                        addAttackDisplay(p, oldDamage, tempDamage);
                    }
                }
                else
                {
                    for (AbstractPower p : enemy.powers) {
                        float oldDamage = tempDamage;
                        for (int i = 0; i < applyCount; i++) {
                            tempDamage = p.atDamageReceive(tempDamage, damageTypeForTurn, this);
                        }
                        addAttackDisplay(p, oldDamage, tempDamage);
                    }
                }

            }

            if (owner instanceof AbstractPlayer)
            {
                tempDamage = ((AbstractPlayer) owner).stance.atDamageGive(tempDamage, damageTypeForTurn, this);
            }

            for (AbstractPower p : owner.powers) {
                float oldDamage = tempDamage;
                for (int i = 0; i < applyCount; i++) {
                    tempDamage = p.atDamageFinalGive(tempDamage, damageTypeForTurn, this);
                }
                addAttackDisplay(p, oldDamage, tempDamage);
            }

            if (applyEnemyPowers) {
                for (AbstractPower p : enemy.powers) {
                    float oldDamage = tempDamage;
                    for (int i = 0; i < applyCount; i++) {
                        tempDamage = p.atDamageFinalReceive(tempDamage, damageTypeForTurn, this);
                    }
                    addAttackDisplay(p, oldDamage, tempDamage);
                }
                tempDamage = CombatManager.onDamageOverride(enemy, damageTypeForTurn, tempDamage, this);
            }
        }

        updateBlock(tempBlock);
        updateDamage(tempDamage);
        updateMagicNumber(modifyMagicNumber(info, baseMagicNumber));
        updateHitCount(modifyHitCount(info, baseHitCount));
        updateRightCount(modifyRightCount(info, baseRightCount));

        doEffects(be -> be.refresh(info, true));

        addAttackResult(baseDamage, tempDamage);
        addDefendResult(baseBlock, tempBlock);

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
                    auxiliaryData.augments.set(index, null);
                }
                augment.onRemoveFromCard(this);
                refresh(null);
                return augment;
            }
        }
        return null;
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

        if (GameUtilities.canShowUpgrades(library) && !isPreview && !isPopup && canUpgrade()) {
            updateGlow();
            renderGlow(sb);
            renderUpgradePreview(sb);
            return;
        }

        updateGlow();
        renderGlow(sb);
        renderImage(sb, hovered, selected);
        renderTitle(sb);
        renderType(sb);
        renderDescription(sb);
        renderTint(sb);
        renderEnergy(sb);
        hb.render(sb);
    }

    protected void renderAtlas(SpriteBatch sb, Color color, TextureAtlas.AtlasRegion img, float drawX, float drawY)
    {
        sb.setColor(color);
        sb.draw(img, drawX + img.offsetX - (float) img.originalWidth / 2f, drawY + img.offsetY - (float) img.originalHeight / 2f, (float) img.originalWidth / 2f - img.offsetX, (float) img.originalHeight / 2f - img.offsetY, (float) img.packedWidth, (float) img.packedHeight, this.drawScale * Settings.scale, this.drawScale * Settings.scale, this.angle);
    }

    protected void renderAtlas(SpriteBatch sb, Color color, TextureAtlas.AtlasRegion img, float drawX, float drawY, float scale)
    {
        sb.setColor(color);
        sb.draw(img, drawX + img.offsetX - (float) img.originalWidth / 2f, drawY + img.offsetY - (float) img.originalHeight / 2f, (float) img.originalWidth / 2f - img.offsetX, (float) img.originalHeight / 2f - img.offsetY, (float) img.packedWidth, (float) img.packedHeight, this.drawScale * Settings.scale * scale, this.drawScale * Settings.scale * scale, this.angle);
    }

    protected void renderPortraitImage(SpriteBatch sb, Texture texture, Color color, float scale, boolean cropPortrait, boolean useTextureSize, boolean foreground)
    {
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
                    } else {
                        portrait = region;
                    }
                } else {
                    region.setRegion(texture);
                    region.setRegion(offset_x, offset_y1, width - (2 * offset_x), height - offset_y1 - offset_y2);
                }
            }

            PCLRenderHelpers.drawOnCardAuto(sb, this, region, new Vector2(0, 72), render_width, render_height, color, transparency, scale);
        } else if (isPopup) {
            PCLRenderHelpers.drawOnCardAuto(sb, this, texture, new Vector2(0, 72), render_width * 2, render_height * 2, color, transparency, scale * 0.5f);
        } else {
            PCLRenderHelpers.drawOnCardAuto(sb, this, texture, new Vector2(0, 72), render_width, render_height, color, transparency, scale);
        }
    }

    public void setAttackType(PCLAttackType attackType) {
        this.attackType = attackType;
    }

    public void setTarget(PCLCardTarget attackTarget) {
        this.pclTarget = attackTarget;
        this.target = attackTarget.cardTarget;
    }

    protected void setCost(int value) {
        if (this.cost >= 0) {
            int previousDiff = costForTurn - cost;
            this.cost = Math.max(0, value);
            this.costForTurn = Math.max(0, cost + previousDiff);
        }
    }

    public void setEvokeOrbCount(int count) {
        this.showEvokeValue = count > 0;
        this.showEvokeOrbCount = count;
    }

    protected int setForm(Integer form, int timesUpgraded) {
        int oldForm = this.auxiliaryData.form;
        this.auxiliaryData.form = (form == null) ? 0 : MathUtils.clamp(form, 0, this.getMaxForms() - 1);
        cardData.invokeTags(this, this.auxiliaryData.form);
        setTarget(cardData.getTargetUpgrade(this.auxiliaryData.form));
        this.name = getUpgradeName();
        if (onAttackEffect != null)
        {
            onAttackEffect.setAmountFromCard().onUpgrade();
        }
        if (onBlockEffect != null)
        {
            onBlockEffect.setAmountFromCard().onUpgrade();
        }
        for (PSkill<?> ef : getEffects()) {
            ef.setAmountFromCard().onUpgrade();
        }
        for (PSkill<?> ef : getPowerEffects()) {
            ef.setAmountFromCard().onUpgrade();
        }

        initializeDescription();

        if (player != null && player.relics != null) {
            for (AbstractRelic relic : player.relics) {
                if (relic instanceof OnSetFormListener) {
                    ((OnSetFormListener) relic).onSetForm(this, oldForm, this.auxiliaryData.form);
                }
            }
        }

        return this.auxiliaryData.form;
    }

    public void setCardRarity(CardRarity rarity)
    {
        this.rarity = rarity;
    }

    public void setCardRarityType(CardRarity rarity, CardType type)
    {
        this.rarity = rarity;
        this.type = type;
    }

    public void setCardType(CardType type)
    {
        this.type = type;
    }

    public void setMultiDamage(boolean value) {
        this.isMultiDamage = value;
    }

    protected void setNumbers(int damage, int block) {
        setNumbers(damage, block, 0, 0);
    }

    protected void setNumbers(int damage, int block, int magicNumber) {
        setNumbers(damage, block, magicNumber, 0);
    }

    protected void setNumbers(int damage, int block, int magicNumber, int secondaryValue) {
        setNumbers(damage, block, magicNumber, secondaryValue, 1);
    }

    protected void setNumbers(int damage, int block, int magicNumber, int secondaryValue, int hitCount) {
        setNumbers(damage, block, magicNumber, secondaryValue, hitCount, 1);
    }

    protected void setNumbers(int damage, int block, int magicNumber, int secondaryValue, int hitCount, int rightCount) {
        this.baseDamage = this.damage = damage;
        this.baseBlock = this.block = block;
        this.baseMagicNumber = this.magicNumber = magicNumber;
        this.currentHealth = this.baseHeal = this.heal = secondaryValue;
        this.baseHitCount = this.hitCount = Math.max(1, hitCount);
        this.baseRightCount = this.rightCount = Math.max(1, rightCount);
    }

    public void setObtainableInCombat(boolean value) {
        setTag(CardTags.HEALING, !value);
    }

    protected void setTag(CardTags tag, boolean enable) {
        if (!enable) {
            tags.remove(tag);
        } else if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public void setup(Object input) {
    }

    public void setupImages(String imagePath)
    {
        portrait = null;
        loadImage(imagePath);
    }

    protected void setupExtraTags()
    {
        // Set Soulbound
        if (!cardData.removableFromDeck)
        {
            SoulboundField.soulbound.set(this, true);
        }

        if (cardData.extraTags != null)
        {
            for (CardTagItem item : cardData.extraTags)
            {
                this.tags.add(item.tag);
                // Starter Strike tags should automatically be added to Basic cards with the Strike, so they can be upgraded in Simplicity, etc.
                if (item == CardTagItem.Strike && this.rarity == CardRarity.BASIC)
                {
                    this.tags.add(CardTags.STARTER_STRIKE);
                }
            }
        }
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
        setAttackType(cardData.attackType);

        if (timesUpgraded > 0) {
            setCost(cardData.getCost(form) + cardData.getCostUpgrade(form));
        }

        this.affinities.initialize(cardData.affinities, form);

        if (!cardData.obtainableInCombat) {
            setObtainableInCombat(false);
        }

        bottomText = getBottomText();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean tryRender(SpriteBatch sb, Texture texture, float scale, Vector2 offset) {
        if (texture != null) {
            PCLRenderHelpers.drawOnCardAuto(sb, this, texture, offset, texture.getWidth(), texture.getHeight(), getRenderColor(), transparency, scale);

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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean tryRenderCentered(SpriteBatch sb, Texture texture, Color color, float scale) {
        if (texture != null) {
            PCLRenderHelpers.drawOnCardAuto(sb, this, texture, new Vector2(0, 0), texture.getWidth(), texture.getHeight(), color, transparency, scale);

            return true;
        }

        return false;
    }

    protected boolean tryRenderCentered(SpriteBatch sb, Texture texture, float scale)
    {
        return tryRenderCentered(sb, texture, getRenderColor(), scale);
    }

    protected boolean tryUpgrade() {
        return tryUpgrade(true);
    }

    protected boolean tryUpgrade(boolean updateDescription) {
        if (this.canUpgrade()) {
            this.timesUpgraded += 1;
            this.upgraded = true;


            initializeTitle();

            if (updateDescription) {
                initializeDescription();
            }

            return true;
        }

        return false;
    }

    public void updateMaxBlock(int amount) {
        baseBlock = block = Math.max(0, amount);
        this.isBlockModified = false;
        if (onBlockEffect != null)
        {
            onBlockEffect.setAmountFromCard();
        }
    }

    public void updateMaxDamage(int amount) {
        baseDamage = damage = Math.max(0, amount);
        this.isDamageModified = false;
        if (onAttackEffect != null)
        {
            onAttackEffect.setAmountFromCard();
        }
    }

    public void updateBlock(float amount) {
        block = Math.max(0, MathUtils.floor(amount));
        this.isBlockModified = (baseBlock != block);
        if (onBlockEffect != null)
        {
            onBlockEffect.setAmountFromCard();
        }
    }

    public void updateDamage(float amount) {
        damage = Math.max(0, MathUtils.floor(amount));
        this.isDamageModified = (baseDamage != damage);
        if (onAttackEffect != null)
        {
            onAttackEffect.setAmountFromCard();
        }
    }

    public void updateHeal(float amount) {
        int prevHeal = heal;
        heal = Math.max(0,MathUtils.floor(amount));
        this.isHealModified = (baseHeal != heal);
        if (prevHeal != heal)
        {
            currentHealth += (prevHeal - heal);
        }
    }

    public void updateHitCount(float amount) {
        hitCount = Math.max(1,MathUtils.floor(amount));
        this.isHitCountModified = (baseHitCount != hitCount);
    }

    public void updateMagicNumber(float amount) {
        magicNumber = Math.max(0, MathUtils.floor(amount));
        this.isMagicNumberModified = (baseMagicNumber != magicNumber);
    }

    public void updateRightCount(float amount) {
        rightCount = Math.max(1,MathUtils.floor(amount));
        this.isRightCountModified = (baseRightCount != rightCount);
    }

    @Override
    public void initializeDescription() {
        if (cardText != null) {
            this.cardText.forceRefresh();
        }
    }

    @Override
    public final void initializeDescriptionCN() {
        initializeDescription();
    }

    @Override
    public boolean canUpgrade() {
        return timesUpgraded < maxUpgradeLevel || maxUpgradeLevel < 0;
    }

    @Override
    public void upgrade() {
        int prevTimesUpgraded = timesUpgraded;
        if (tryUpgrade()) {
            changeForm(auxiliaryData.form, prevTimesUpgraded, timesUpgraded);

            onUpgrade();

            affinities.applyUpgrades(cardData.affinities, auxiliaryData.form);
        }
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

        for (PSkill<?> ef : getEffects()) {
            ef.displayUpgrades();
        }
        for (PSkill<?> ef : getPowerEffects()) {
            ef.displayUpgrades();
        }

        // Force refresh the descriptions, affinities, and augments
        initializeDescription();
        affinities.updateSortedList();
    }

    @Override
    protected void upgradeName() {
        ++this.timesUpgraded;
        this.upgraded = true;
        this.name = getUpgradeName();
        this.initializeTitle();
    }

    @Override
    public PCLCard makeStatEquivalentCopy() {
        PCLCard copy = (PCLCard) super.makeStatEquivalentCopy();
        copy.auxiliaryData = new PCLCardSaveData(auxiliaryData);
        copy.changeForm(auxiliaryData.form, timesUpgraded);

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
        copy.originalName = originalName;
        copy.name = name;

        for (PCLAugment augment : getAugments()) {
            copy.addAugment(augment.makeCopy());
        }

        copy.initializeDescription();

        return copy;
    }

    @Override
    public boolean cardPlayable(AbstractMonster m) {
        cantUseMessage = PCLCard.UNPLAYABLE_MESSAGE;
        return !GameUtilities.isUnplayableThisTurn(this)
                && isEffectPlayable(m)
                && super.cardPlayable(m);
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        return cardPlayable(m) && this.hasEnoughEnergy();
    }

    @Override
    public final void use(AbstractPlayer p1, AbstractMonster m1) {
        PCLUseInfo info = CombatManager.playerSystem.generateInfo(this, p1, m1);
        onUse(info);
    }

    @Override
    public void update() {
        super.update();

        if (EUIGameUtils.inGame() && AbstractDungeon.player != null && AbstractDungeon.player.hoveredCard != this && !AbstractDungeon.isScreenUp) {
            this.hovered = false;
            this.renderTip = false;
        }

        // For selecting separate forms on the upgrade screen
        if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GRID && AbstractDungeon.gridSelectScreen.forUpgrade && hb.hovered && InputHelper.justClickedLeft) {
            GridCardSelectScreenMultiformPatches.selectPCLCardUpgrade(this);
        }
    }

    @Override
    public final void render(SpriteBatch sb) {
        render(sb, hovered, false, false);
    }

    @Override
    public final void renderInLibrary(SpriteBatch sb) {
        render(sb, hovered, false, true);
    }

    @Override
    public final void render(SpriteBatch sb, boolean selected) {
        render(sb, hovered, selected, false);
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
    public final void renderOuterGlow(SpriteBatch sb) {
        super.renderOuterGlow(sb);
    }

    @Override
    public final void renderSmallEnergy(SpriteBatch sb, TextureAtlas.AtlasRegion region, float x, float y) { /* Useless */ }

    @Override
    public final void resetAttributes() {
        // Triggered after being played, discarded, or at end of turn
        super.resetAttributes();
    }

    @Override
    public void hover() {
        if (!this.hovered) {
            this.drawScale = 1.0F;
            this.targetDrawScale = 1.0F;
        }

        this.hovered = true;

        if (player != null && player.hand.contains(this)) {
            if (hb.justHovered) {
                triggerOnGlowCheck();
            }
        }
    }

    @Override
    public void unhover() {
        if (this.hovered) {
            this.hoverDuration = 0.0F;
            this.targetDrawScale = 0.75F;
        }

        this.hovered = false;
        this.renderTip = false;
    }

    @Override
    public void updateHoverLogic() {
        this.hb.update();

        if (this.hb.hovered) {
            this.hover();
            this.hoverDuration += EUI.delta();
            this.renderTip = this.hoverDuration > 0.2F && !Settings.hideCards;
        } else {
            this.unhover();
        }
    }

    @Override
    public void untip() {
        this.hoverDuration = 0f;
        this.renderTip = false;
    }

    @Override
    public void renderCardTip(SpriteBatch sb) {
        if (!Settings.hideCards && !isFlipped && !isLocked && isSeen && (isPopup || renderTip) && EUITooltip.canRenderTooltips() && (AbstractDungeon.player == null || !AbstractDungeon.player.isDraggingCard || Settings.isTouchScreen)) {
            EUITooltip.queueTooltips(this);
        }
    }

    @Override
    public final void renderCardPreviewInSingleView(SpriteBatch sb) { /* Useless */ }

    @Override
    public final void renderCardPreview(SpriteBatch sb) { /* Useless */ }

    @Override
    public void triggerWhenDrawn() {
        super.triggerWhenDrawn();

        if (PCLCardTag.Autoplay.tryProgress(this)) {
            PCLActions.last.playCard(this, player.hand, null)
                    .spendEnergy(true)
                    .addCondition(AbstractCard::hasEnoughEnergy);
        }

        doEffects(be -> be.triggerOnDraw(this));
    }

    @Override
    public final void triggerWhenCopied() {
        // this is only used by ShowCardAndAddToHandEffect
        triggerWhenDrawn();
    }

    @Override
    public void triggerOnEndOfPlayerTurn() {
        super.triggerOnEndOfPlayerTurn();
    }

    @Override
    public void triggerOnEndOfTurnForPlayingCard() {
        boolean shouldPlay = playAtEndOfTurn;
        for (PSkill<?> be : getFullEffects()) {
            shouldPlay = shouldPlay | be.triggerOnEndOfTurn(false);
        }
        if (playAtEndOfTurn) {
            dontTriggerOnUseCard = true;

            AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(this, true));
        }
    }

    @Override
    public void triggerOnOtherCardPlayed(AbstractCard c) {
        super.triggerOnOtherCardPlayed(c);
        doEffects(be -> be.triggerOnOtherCardPlayed(c));
    }

    @Override
    public void triggerOnManualDiscard() {
        super.triggerOnManualDiscard();
        doEffects(be -> be.triggerOnDiscard(this));
    }

    @Override
    public void triggerOnScry()
    {
        super.triggerOnScry();
        doEffects(be -> be.triggerOnScry(this));
    }

    @Override
    public void onRetained() {
        super.onRetained();
        doEffects(be -> be.triggerOnRetain(this));
    }

    public void triggerOnExhaust() {
        super.triggerOnExhaust();
        doEffects(be -> be.triggerOnExhaust(this));
    }

    @Override
    public void applyPowers() {
        if (isMultiDamage) {
            calculateCardDamage(null);
        } else {
            refresh(null);
        }
    }

    @Override
    protected final void applyPowersToBlock() { /* Useless */ }

    @Override
    public void calculateDamageDisplay(AbstractMonster mo) {
        calculateCardDamage(mo);
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        if (isMultiDamage) {
            ArrayList<AbstractMonster> m = AbstractDungeon.getCurrRoom().monsters.monsters;
            multiDamage = new int[m.size()];

            int best = -PSkill.DEFAULT_MAX;
            for (int i = 0; i < multiDamage.length; i++) {
                refresh(m.get(i));
                multiDamage[i] = damage;

                if (damage > best) {
                    best = damage;
                }
            }

            if (best > 0) {
                updateDamage(best);
            }
        } else {
            refresh(mo);
        }
    }

    @Override
    public void triggerOnGlowCheck() {
        this.glowColor = CombatManager.playerSystem.getGlowColor(this);

        for (PSkill<?> be : getFullEffects()) {
            Color c = be.getGlowColor();
            if (c != null)
            {
                this.glowColor = c;
            }
        }
    }

    @Override
    public AbstractCard makeCopy() {
        PCLCard card = cardData.createNewInstance();
        if (card != null) {
            card.changeForm(auxiliaryData.form, timesUpgraded);
        }
        return card;
    }

    public boolean isMultiUpgrade() {
        return maxUpgradeLevel < 0 || maxUpgradeLevel > 1;
    }

    @Override
    public PCLCardSaveData onSave() {
        return auxiliaryData;
    }

    @Override
    public void onLoad(PCLCardSaveData data) {
        if (data != null) {
            changeForm(data.form, timesUpgraded);
            this.auxiliaryData = new PCLCardSaveData(data);
            if (data.modifiedDamage != 0) {
                GameUtilities.modifyDamage(this, baseDamage + data.modifiedDamage, false);
            }
            if (data.modifiedBlock != 0) {
                GameUtilities.modifyBlock(this, baseBlock + data.modifiedBlock, false);
            }
            if (data.modifiedMagicNumber != 0) {
                GameUtilities.modifyMagicNumber(this, baseMagicNumber + data.modifiedMagicNumber, false);
            }
            if (data.modifiedHeal != 0) {
                GameUtilities.modifySecondaryValue(this, baseHeal + data.modifiedHeal, false);
            }
            if (data.modifiedHitCount != 0) {
                GameUtilities.modifyHitCount(this, baseHitCount + data.modifiedHitCount, false);
            }
            if (data.modifiedRightCount != 0) {
                GameUtilities.modifyRightCount(this, baseRightCount + data.modifiedRightCount, false);
            }
            if (cost >= 0 && data.modifiedCost != 0) {
                GameUtilities.modifyCostForCombat(this, data.modifiedCost, true);
            }
            if (data.modifiedAffinities != null) {
                for (PCLAffinity affinity : PCLAffinity.basic()) {
                    affinities.add(affinity, data.modifiedAffinities[affinity.id]);
                }
            }
            if (data.modifiedScaling != null) {
                for (PCLAffinity affinity : PCLAffinity.basic()) {
                    affinities.add(affinity, data.modifiedScaling[affinity.id]);
                }
            }
            if (data.removedTags != null) {
                for (PCLCardTag tag : data.removedTags) {
                    tag.set(this, 0);
                }
            }
            if (data.addedTags != null) {
                for (PCLCardTag tag : data.addedTags) {
                    tag.set(this, 1);
                }
            }
            if (data.augments != null) {
                for (String id : data.augments) {
                    PCLAugmentData augment = PCLAugment.get(id);
                    if (augment != null) {
                        addAugment(augment.create(), false);
                    }
                }
            }
        }
    }

    @Override
    public Type savedType() {
        return new TypeToken<PCLCardSaveData>() {
        }.getType();
    }

    @SpireOverride
    protected void renderBack(SpriteBatch sb, boolean hovered, boolean selected)
    {
        SpireSuper.call(sb, hovered, selected);
    }

    @SpireOverride
    protected void renderBannerImage(SpriteBatch sb, float drawX, float drawY) {
        if (isSeen && (PGR.config.showIrrelevantProperties.get() || GameUtilities.isPCLActingCardColor(this))) {
            affinities.renderOnCard(sb, this, player != null && player.hand.contains(this));
        }
        float sc = isPopup ? 0.5f : 1f;
        if (!tryRenderCentered(sb, getCardBanner(), getRarityColor(), sc))
        {
            // Copying base game behavior
            if (isPopup)
            {
                renderAtlas(sb, Color.WHITE, getCardBannerVanillaRegion(), (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, sc);
            }
            else
            {
                renderAtlas(sb, Color.WHITE, getCardBannerVanillaRegion(), current_x, current_y, sc);
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
                            new Vector2(0, 0), card.getWidth(), card.getHeight(),
                            this.color == CardColor.CURSE ? PCLCard.CURSE_COLOR : PCLCard.COLORLESS_ORB_COLOR, transparency, popUpMultiplier));
        } else {
            PCLRenderHelpers.drawOnCardAuto(sb, this, card, new Vector2(0, 0), card.getWidth(), card.getHeight(), getRenderColor(), transparency, popUpMultiplier);
        }
    }

    @SpireOverride
    public void renderDescription(SpriteBatch sb) {
        if (!Settings.hideCards && !isFlipped) {
            this.cardText.renderDescription(sb);
        }
    }

    @SpireOverride
    private void renderDescriptionCN(SpriteBatch sb) { /* Useless */ }

    @SpireOverride
    protected void renderEnergy(SpriteBatch sb) {
        if (this.cost > -2 && !getDarken() && !this.isLocked && this.isSeen) {
            Texture baseCard = getCardBackground();
            float popUpMultiplier = isPopup ? 0.5f : 1f;
            Vector2 offset = new Vector2(-baseCard.getWidth() / (isPopup ? 7.7f : 3.85f), baseCard.getHeight() / (isPopup ? 5.3f : 2.64f));
            Texture energyOrb = getEnergyOrb();
            PCLRenderHelpers.drawOnCardAuto(sb, this, energyOrb, offset, energyOrb.getWidth(), energyOrb.getHeight(), getRenderColor(), transparency, popUpMultiplier);

            renderEnergyText(sb);
        }
    }

    protected void renderEnergyText(SpriteBatch sb)
    {
        renderEnergyText(sb, -132f, 192f);
    }

    protected void renderEnergyText(SpriteBatch sb, float xOffset, float yOffset)
    {
        ColoredString costString = getCostString();
        if (costString != null) {
            BitmapFont font = PCLRenderHelpers.getEnergyFont(this);
            PCLRenderHelpers.writeOnCard(sb, this, font, costString.text, xOffset, yOffset, costString.color);
            PCLRenderHelpers.resetFont(font);
        }
    }

    @SpireOverride
    public void renderGlow(SpriteBatch sb) {
        if (transparency < 0.7f) {
            return;
        }

        renderMainBorder(sb);

        for (PCLCardGlowBorderEffect glowBorder : glowList) {
            glowBorder.render(sb);
        }

        sb.setBlendFunction(770, 771);
    }

    @SpireOverride
    public void renderImage(SpriteBatch sb, boolean hovered, boolean selected) {
        if (player != null) {
            if (selected) {
                renderAtlas(sb, Color.SKY, getCardBgAtlas(), current_x, current_y, 1.03f);
            }

            renderAtlas(sb, new Color(0, 0, 0, transparency * 0.25f), getCardBgAtlas(), current_x + SHADOW_OFFSET_X * drawScale, current_y - SHADOW_OFFSET_Y * drawScale);
            if ((player.hoveredCard == this) && ((player.isDraggingCard && player.isHoveringDropZone) || player.inSingleTargetMode)) {
                renderAtlas(sb, HOVER_IMG_COLOR, getCardBgAtlas(), current_x, current_y);
            } else if (selected) {
                renderAtlas(sb, SELECTED_CARD_COLOR, getCardBgAtlas(), current_x, current_y);
            }
        }

        renderPortrait(sb);
        renderCardBg(sb, current_x, current_y);
        renderPortraitFrame(sb, current_x, current_y);
        renderBannerImage(sb, current_x, current_y);
    }

    @SpireOverride
    protected void renderJokePortrait(SpriteBatch sb) {
        renderPortrait(sb);
    }

    @SpireOverride
    protected void renderMainBorder(SpriteBatch sb) {
        if (!this.isGlowing) {
            return;
        }

        final TextureAtlas.AtlasRegion img;
        switch (this.type) {
            case ATTACK:
                img = ImageMaster.CARD_ATTACK_BG_SILHOUETTE;
                break;

            case POWER:
                img = ImageMaster.CARD_POWER_BG_SILHOUETTE;
                break;

            default:
                img = ImageMaster.CARD_SKILL_BG_SILHOUETTE;
                break;
        }

        if (GameUtilities.inBattle(false)) {
            sb.setColor(this.glowColor);
        } else {
            sb.setColor(GREEN_BORDER_GLOW_COLOR);
        }

        sb.setBlendFunction(EUIRenderHelpers.BlendingMode.Glowing.srcFunc, EUIRenderHelpers.BlendingMode.Glowing.dstFunc);
        sb.draw(img, this.current_x + img.offsetX - (img.originalWidth / 2f), this.current_y + img.offsetY - (img.originalWidth / 2f),
                (img.originalWidth / 2f) - img.offsetX, (img.originalWidth / 2f) - img.offsetY, img.packedWidth, img.packedHeight,
                this.drawScale * Settings.scale * 1.04f, this.drawScale * Settings.scale * 1.03f, this.angle);
    }

    @SpireOverride
    protected void renderPortrait(SpriteBatch sb) {
        if (!isSeen || isLocked) {
            renderPortraitImage(sb, EUIRM.getTexture(QuestionMark.DATA.imagePath), getRenderColor(), 1, false, false, false);
            return;
        }

        final boolean cropPortrait = canCropPortraits && PGR.config.cropCardImages.get();

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
        if (!tryRenderCentered(sb, getPortraitFrame(), getRarityColor(), sc))
        {
            // Copying base game location behavior
            if (isPopup)
            {
                renderAtlas(sb, Color.WHITE, getPortraitFrameVanillaRegion(), (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, sc);
            }
            else
            {
                renderAtlas(sb, Color.WHITE, getPortraitFrameVanillaRegion(), current_x, current_y, sc);
            }
        }
    }

    @SpireOverride
    protected void renderTint(SpriteBatch sb) {
        SpireSuper.call(sb);
    }

    @SpireOverride
    protected void renderTitle(SpriteBatch sb) {
        final BitmapFont font = PCLRenderHelpers.getTitleFont(this);

        Color color;
        String text;
        if (isLocked || !isSeen) {
            color = Color.WHITE;
            text = isLocked ? LOCKED_STRING : UNKNOWN_STRING;
        } else {
            color = upgraded ? Settings.GREEN_TEXT_COLOR : Color.WHITE;
            text = name;
        }

        PCLRenderHelpers.writeOnCard(sb, this, font, text, 0, RAW_H * 0.416f, color, false);
        PCLRenderHelpers.resetFont(font);
    }

    @SpireOverride
    protected void renderType(SpriteBatch sb) {
        if (showTypeText) {
            if (shouldUsePCLFrame())
            {
                Texture texture = getTypeIcon();
                float height = texture.getHeight();
                PCLRenderHelpers.drawOnCardAuto(sb, this, texture, new Vector2(0, -height * 0.2f), texture.getWidth(), height, Color.WHITE, transparency, Settings.scale * 0.24f);
            }
            else
            {
                if (isPopup)
                {
                    FontHelper.renderFontCentered(sb, FontHelper.panelNameFont, EUIGameUtils.textForType(type), (float)Settings.WIDTH / 2.0F + 3.0F * Settings.scale, (float)Settings.HEIGHT / 2.0F - 40.0F * Settings.scale, CARD_TYPE_COLOR);
                }
                else
                {
                    SpireSuper.call(sb);
                }
            }
        }
    }

    // Determines whether the card frame used will be the base game's or the PCL frame
    protected boolean shouldUsePCLFrame()
    {
        return PGR.getResources(this.color).usePCLFrame;
    }

    public void setDrawScale(float scale)
    {
        this.drawScale = this.targetDrawScale = scale;
    }

    public void setPosition(float x, float y)
    {
        this.current_x = this.target_x = x;
        this.current_y = this.target_y = y;
        this.hb.move(current_x, current_y);
    }

    public void stopGlowing(float delay) {
        super.stopGlowing();

        EUIClassUtils.setField(this, "glowTimer", delay);
    }

    public void triggerOnPurge() {
        doEffects(be -> be.triggerOnPurge(this));
    }

    public void triggerOnReshuffle(CardGroup sourcePile) {
        doEffects(be -> be.triggerOnReshuffle(this, sourcePile));
    }

    // Called at the start of a fight, or when a card is created by MakeTempCard.
    public void triggerWhenCreated(boolean startOfBattle) {
        doEffects(be -> be.triggerOnCreate(this, startOfBattle));
    }

    public void triggerWhenKilled(PCLCardAlly ally) {
        doEffects(be -> be.triggerOnAllyDeath(this, ally));
    }

    public void triggerWhenSummoned(PCLCardAlly ally) {
        doEffects(be -> be.triggerOnAllySummon(this, ally));
    }

    public void triggerWhenTriggered(PCLCardAlly ally) {
        doEffects(be -> be.triggerOnAllyTrigger(this, ally));
    }

    public void triggerWhenWithdrawn(PCLCardAlly ally) {
        doEffects(be -> be.triggerOnAllyWithdraw(this, ally));
    }

    @SpireOverride
    public void updateGlow() {
        updateGlow(1f);
    }

    // Use PCLCardGlowBorderEffect instead of the base glow effect, which will allow glows to be used outside of runs
    public void updateGlow(float mult) {
        float newValue = ReflectionHacks.getPrivate(this, AbstractCard.class, "glowTimer");
        if (this.isGlowing) {
            newValue -= Gdx.graphics.getDeltaTime();
            if (newValue < 0.0F) {
                glowList.add(new PCLCardGlowBorderEffect(this, this.glowColor, mult));
                newValue = 0.5F;
            }
            ReflectionHacks.setPrivate(this, AbstractCard.class, "glowTimer", newValue);
        }

        Iterator<PCLCardGlowBorderEffect> i = this.glowList.iterator();

        while (i.hasNext()) {
            PCLCardGlowBorderEffect e = i.next();
            e.update();
            if (e.isDone) {
                i.remove();
            }
        }
    }

    protected void upgradeHitCount(int amount) {
        this.baseHitCount += amount;
        this.hitCount = this.baseHitCount;
        this.upgradedHitCount = true;
    }

    protected void upgradeRightCount(int amount) {
        this.baseRightCount += amount;
        this.rightCount = this.baseRightCount;
        this.upgradedRightCount = true;
    }

    protected void upgradeSecondaryValue(int amount) {
        this.baseHeal += amount;
        this.currentHealth = this.heal = this.baseHeal;
        this.upgradedHeal = true;
    }

    protected boolean getDarken()
    {
        return ReflectionHacks.getPrivate(this, AbstractCard.class, "darken");
    }

    protected Color getRenderColor()
    {
        return ReflectionHacks.getPrivate(this, AbstractCard.class, "renderColor");
    }
}