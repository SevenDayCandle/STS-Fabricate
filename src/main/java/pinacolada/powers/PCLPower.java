package pinacolada.powers;

import basemod.ReflectionHacks;
import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.ColoredString;
import extendedui.utilities.EUIColors;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCardData;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.SFX;
import pinacolada.effects.powers.PCLFlashPowerEffect;
import pinacolada.effects.powers.PCLGainPowerEffect;
import pinacolada.interfaces.providers.ClickableProvider;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.relics.PCLRelic;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PSpecialSkill;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

// Copied and modified from STS-AnimatorMod
public abstract class PCLPower extends AbstractPower implements CloneablePowerInterface, TooltipProvider, ClickableProvider
{
    protected static final StringBuilder builder = new StringBuilder();
    protected static final float ICON_SIZE = 32f;
    protected static final float ICON_SIZE2 = 48f;
    protected static final float CLICKABLE_SIZE = ICON_SIZE * Settings.scale * 1.5f;
    protected static final Color disabledColor = new Color(0.5f, 0.5f, 0.5f, 1);
    public static final int DUMMY_MULT = 100;
    public static AbstractPlayer player = null;
    public static Random rng = null;
    public final ArrayList<EUITooltip> tooltips = new ArrayList<>();
    protected final ArrayList<AbstractGameEffect> effects;
    public EUIHitbox hb;
    public AbstractCreature source;
    public EUITooltip mainTip;
    public PCLClickableUse triggerCondition;
    public boolean clickable;
    public boolean enabled = true;
    public boolean hideAmount = false;
    public int baseAmount = 0;
    public int maxAmount = 9999;
    protected PowerStrings powerStrings;

    // Should not call this constructor without setting strings up through one of the setupStrings methods
    protected PCLPower(AbstractCreature owner, AbstractCreature source)
    {
        this.effects = ReflectionHacks.getPrivate(this, AbstractPower.class, "effect");
        this.owner = owner;
        this.source = source;
        hb = new EUIHitbox(CLICKABLE_SIZE, CLICKABLE_SIZE);
    }

    public PCLPower(AbstractCreature owner, PCLRelic relic)
    {
        this(owner, null, relic);
    }

    public PCLPower(AbstractCreature owner, AbstractCreature source, PCLRelic relic)
    {
        this(owner, source);
        setupStrings(relic);
    }

    public PCLPower(AbstractCreature owner, PCLCardData cardData)
    {
        this(owner, null, cardData);
    }

    public PCLPower(AbstractCreature owner, AbstractCreature source, PCLCardData cardData)
    {
        this(owner, source);
        setupStrings(cardData);
    }

    public PCLPower(AbstractCreature owner, String id)
    {
        this(owner, null, id);
    }

    public PCLPower(AbstractCreature owner, AbstractCreature source, String id)
    {
        this(owner, source);
        setupStrings(id);
    }

    public static String createFullID(Class<? extends PCLPower> type)
    {
        return createFullID(PGR.core, type);
    }

    public static String createFullID(PCLResources<?,?,?,?> resources, Class<? extends PCLPower> type)
    {
        return resources.createID(type.getSimpleName());
    }

    public static String deriveID(String base)
    {
        return base + "Power";
    }

    public PCLClickableUse createTrigger(ActionT2<PSpecialSkill, PCLUseInfo> onUse)
    {
        triggerCondition = new PCLClickableUse(this, onUse);
        return triggerCondition;
    }

    public PCLClickableUse createTrigger(ActionT2<PSpecialSkill, PCLUseInfo> onUse, int uses, boolean refreshEachTurn, boolean stackAutomatically)
    {
        triggerCondition = new PCLClickableUse(this, onUse, uses, refreshEachTurn, stackAutomatically);
        return triggerCondition;
    }

    public PCLClickableUse createTrigger(ActionT2<PSpecialSkill, PCLUseInfo> onUse, PCLTriggerUsePool pool)
    {
        triggerCondition = new PCLClickableUse(this, onUse, pool);
        return triggerCondition;
    }

    public PCLClickableUse createTrigger(PSkill<?> move)
    {
        triggerCondition = new PCLClickableUse(this, move);
        return triggerCondition;
    }

    public PCLClickableUse createTrigger(PSkill<?>  move, int uses, boolean refreshEachTurn, boolean stackAutomatically)
    {
        triggerCondition = new PCLClickableUse(this, move, uses, refreshEachTurn, stackAutomatically);
        return triggerCondition;
    }

    public PCLClickableUse createTrigger(PSkill<?>  move, PCLTriggerUsePool pool)
    {
        triggerCondition = new PCLClickableUse(this, move, pool);
        return triggerCondition;
    }

    protected void findTooltipsFromText(String text)
    {

        boolean foundIcon = false;
        for (int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);

            if (foundIcon)
            {
                if (']' != c)
                {
                    builder.append(c);
                    continue;
                }
                foundIcon = false;
                EUITooltip tooltip = EUITooltip.findByID(EUIUtils.invokeBuilder(builder));
                if (tooltip != null)
                {
                    tooltips.add(tooltip);
                }
            }
            else if ('[' == c)
            {
                foundIcon = true;
            }
        }

    }

    protected String formatDescription(int index, Object... args)
    {
        if (powerStrings == null || powerStrings.DESCRIPTIONS == null || powerStrings.DESCRIPTIONS.length <= index)
        {
            EUIUtils.logError(this, "powerStrings.DESCRIPTIONS does not exist, " + this.name);
            return "";
        }
        return EUIUtils.format(powerStrings.DESCRIPTIONS[index], args);
    }

    protected ColoredString getPrimaryAmount(Color c)
    {
        if (amount != 0)
        {
            if (isTurnBased)
            {
                return new ColoredString(amount, Color.WHITE, c.a);
            }
            else if (this.amount >= 0)
            {
                return new ColoredString(amount, Color.GREEN, c.a);
            }
            else if (this.canGoNegative)
            {
                return new ColoredString(amount, Color.RED, c.a);
            }
        }

        return null;
    }

    protected ColoredString getSecondaryAmount(Color c)
    {
        return null;
    }

    @Override
    public List<EUITooltip> getTips()
    {
        return tooltips;
    }

    @Override
    public EUITooltip getTooltip()
    {
        return mainTip;
    }

    public String getUpdatedDescription()
    {
        return formatDescription(0, amount);
    }

    public void initialize(int amount)
    {
        initialize(amount, PowerType.BUFF, false);
    }

    protected void initialize(int amount, PowerType type, boolean turnBased)
    {
        this.baseAmount = this.amount = Math.min(maxAmount, amount);
        this.type = type;
        this.isTurnBased = turnBased;
        updateDescription();
    }

    public int modifyCost(AbstractCard card, int cost)
    {
        return cost;
    }

    public float modifyOrbIncoming(float initial)
    {
        return initial;
    }

    public float modifyOrbOutgoing(float initial)
    {
        return initial;
    }

    protected void onSamePowerApplied(AbstractPower power)
    {

    }

    public RemoveSpecificPowerAction removePower()
    {
        return removePower(PCLActions.bottom);
    }

    public RemoveSpecificPowerAction removePower(PCLActions order)
    {
        return order.removePower(owner, owner, this);
    }

    public int resetAmount()
    {
        final int previous = amount;
        this.amount = baseAmount;
        onAmountChanged(previous, (amount - previous));
        return amount;
    }

    public PCLPower setEnabled(boolean enable)
    {
        this.enabled = enable;

        return this;
    }

    public PCLPower setHitbox(EUIHitbox hb)
    {
        this.hb = hb;
        return this;
    }

    protected void setupDescription()
    {
        this.name = powerStrings.NAME;
        this.description = getUpdatedDescription();
        mainTip = new EUITooltip(name, description);
        mainTip.icon = this.region48 != null ? this.region48 : img != null ? new TextureRegion(img) : null;
        tooltips.add(mainTip);
        findTooltipsFromText(description);
    }

    protected void setupStrings(String originalID)
    {
        final String imagePath = PGR.getPowerImage(originalID);
        if (Gdx.files.internal(imagePath).exists())
        {
            this.img = EUIRM.getTexture(imagePath);
        }
        if (this.img == null)
        {
            this.img = PCLCoreImages.CardAffinity.unknown.texture();
        }

        this.ID = originalID;
        this.powerStrings = CardCrawlGame.languagePack.getPowerStrings(originalID);
        setupDescription();
    }

    protected void setupStrings(PCLCardData cardData)
    {
        this.ID = deriveID(cardData.ID);
        // Vanilla rendering cannot render generated region48 properly
        if (PGR.config.vanillaPowerRender.get())
        {
            this.img = PCLCoreImages.CardAffinity.unknown.texture();
        }
        else
        {
            this.region48 = cardData.getCardIcon();
        }
        this.powerStrings = new PowerStrings();
        this.powerStrings.NAME = cardData.strings.NAME;
        this.powerStrings.DESCRIPTIONS = cardData.strings.EXTENDED_DESCRIPTION;
        setupDescription();
    }

    protected void setupStrings(PCLRelic relic)
    {
        this.ID = deriveID(relic.relicId);
        // Vanilla rendering cannot render generated region48 properly
        if (PGR.config.vanillaPowerRender.get())
        {
            this.img = PCLCoreImages.CardAffinity.unknown.texture();
        }
        else
        {
            this.region48 = relic.getPowerIcon();
        }
        this.powerStrings = new PowerStrings();
        this.powerStrings.NAME = relic.name;
        this.powerStrings.DESCRIPTIONS = relic.DESCRIPTIONS;
        setupDescription();
    }

    @Override
    public AbstractPower makeCopy()
    {
        if (this instanceof InvisiblePower)
        {
            EUIUtils.logError(this, "Do not clone powers which implement InvisiblePower");
            return null;
        }

        Constructor<? extends PCLPower> c;
        try
        {
            c = EUIUtils.tryGetConstructor(getClass(), AbstractCreature.class, int.class);
            if (c != null)
            {
                return c.newInstance(owner, amount);
            }
            c = EUIUtils.tryGetConstructor(getClass(), AbstractCreature.class);
            if (c != null)
            {
                return c.newInstance(owner);
            }
            c = EUIUtils.tryGetConstructor(getClass(), AbstractCreature.class, AbstractCreature.class, int.class);
            if (c != null)
            {
                return c.newInstance(owner, source, amount);
            }
            c = EUIUtils.tryGetConstructor(getClass());
            if (c != null)
            {
                return c.newInstance();
            }
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    protected void onAmountChanged(int previousAmount, int difference)
    {
        if (difference != 0)
        {
            updateDescription();
        }
    }

    protected void renderIconsImpl(SpriteBatch sb, float x, float y, Color borderColor, Color imageColor)
    {
        float scale = 1;
        if (triggerCondition != null)
        {
            PCLRenderHelpers.drawCentered(sb, borderColor, PCLCoreImages.Menu.squaredbuttonEmptycenter.texture(), x, y, ICON_SIZE2, ICON_SIZE2, 1f, 0);
            scale = 0.75f;
        }

        if (this.region48 != null)
        {
            PCLRenderHelpers.drawCentered(sb, imageColor, this.region48, x, y, ICON_SIZE, ICON_SIZE, scale, 0);
        }
        else
        {
            PCLRenderHelpers.drawCentered(sb, imageColor, this.img, x, y, ICON_SIZE, ICON_SIZE, scale, 0);
        }

        if (triggerCondition != null && enabled && hb.hovered && clickable)
        {
            PCLRenderHelpers.drawCentered(sb, EUIColors.white(0.3f), EUIRM.images.squaredButton.texture(), x, y, ICON_SIZE2, ICON_SIZE2, 1f, 0);
        }

    }

    public void stackPower(int stackAmount, boolean updateBaseAmount)
    {
        if (updateBaseAmount && (baseAmount += stackAmount) > maxAmount)
        {
            baseAmount = maxAmount;
        }
        if ((amount + stackAmount) > maxAmount)
        {
            stackAmount = maxAmount - amount;
        }

        final int previous = amount;
        super.stackPower(stackAmount);

        onAmountChanged(previous, stackAmount);
    }

    @Override
    public void update(int slot)
    {
        super.update(slot);
        hb.update();
        if (triggerCondition != null)
        {
            triggerCondition.refresh(false, hb.justHovered);
        }

        if (hb.hovered)
        {
            EUITooltip.queueTooltips(tooltips, InputHelper.mX + hb.width, InputHelper.mY + (hb.height * 0.5f));
            clickable = triggerCondition != null && triggerCondition.interactable();
            if (clickable)
            {
                if (hb.justHovered)
                {
                    SFX.play(SFX.UI_HOVER);
                }

                if (InputHelper.justClickedLeft)
                {
                    hb.clickStarted = true;
                    SFX.play(SFX.UI_CLICK_1);
                }
                else if (hb.clicked)
                {
                    hb.clicked = false;
                    triggerCondition.targetToUse(1);
                }
            }
        }
    }

    @Override
    public void updateDescription()
    {
        if (this instanceof InvisiblePower)
        {
            this.description = GameUtilities.EMPTY_STRING;
            return;
        }

        this.description = getUpdatedDescription();
        mainTip.setDescription(this.description);
    }

    @Override
    public void stackPower(int stackAmount)
    {
        stackPower(stackAmount, true);
    }

    @Override
    public void reducePower(int reduceAmount)
    {
        final int previous = amount;
        super.reducePower(reduceAmount);
        if ((amount == 0) || (!canGoNegative && amount < 0))
        {
            removePower();
        }

        onAmountChanged(previous, -Math.max(0, reduceAmount));
    }

    @Override
    public void renderIcons(SpriteBatch sb, float x, float y, Color c)
    {
        if (!enabled)
        {
            disabledColor.a = c.a;
            c = disabledColor;
        }

        if (hb.cX != x || hb.cY != y)
        {
            hb.move(x, y);
        }
        Color borderColor = (enabled && triggerCondition != null && triggerCondition.interactable()) ? c : disabledColor;
        Color imageColor = enabled ? c : disabledColor;

        this.renderIconsImpl(sb, x, y, borderColor, imageColor);

        for (AbstractGameEffect e : effects)
        {
            e.render(sb, x, y);
        }
    }

    @Override
    public void renderAmount(SpriteBatch sb, float x, float y, Color c)
    {
        if (hideAmount)
        {
            return;
        }

        ColoredString amount = getPrimaryAmount(c);
        if (amount != null)
        {
            FontHelper.renderFontRightTopAligned(sb, FontHelper.powerAmountFont, amount.text, x, y, fontScale, amount.color);
        }

        ColoredString amount2 = getSecondaryAmount(c);
        if (amount2 != null)
        {
            FontHelper.renderFontRightTopAligned(sb, FontHelper.powerAmountFont, amount2.text, x, y + 15f * Settings.scale, 1, amount2.color);
        }
    }

    @Override
    public void atStartOfTurn()
    {
        super.atStartOfTurn();

        if (triggerCondition != null)
        {
            triggerCondition.refresh(true, true);
        }

    }

    @Override
    public void onRemove()
    {
        super.onRemove();

        final int previous = amount;
        amount = 0;
        onAmountChanged(previous, -previous);
    }

    @Override
    public void onInitialApplication()
    {
        super.onInitialApplication();

        onAmountChanged(0, amount);
    }

    @Override
    public void flash()
    {
        this.effects.add(new PCLGainPowerEffect(this, true));
        PCLEffects.Queue.add(new PCLFlashPowerEffect(this));
    }

    @Override
    public void flashWithoutSound()
    {
        this.effects.add(new PCLGainPowerEffect(this, false));
        PCLEffects.Queue.add(new PCLFlashPowerEffect(this));
    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source)
    {
        super.onApplyPower(power, target, source);

        if (owner == target && power.ID.equals(ID))
        {
            onSamePowerApplied(power);
            if (triggerCondition != null && triggerCondition.pool.stackAutomatically)
            {
                triggerCondition.addUses(1);
            }
        }
    }

    public String getID()
    {
        return ID;
    }
}