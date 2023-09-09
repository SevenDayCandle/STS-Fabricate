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
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.text.EUISmartText;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.ColoredString;
import pinacolada.actions.PCLActions;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.powers.PCLFlashPowerEffect;
import pinacolada.effects.powers.PCLGainPowerEffect;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.ArrayList;
import java.util.List;

// Copied and modified from STS-AnimatorMod
public abstract class PCLPower extends AbstractPower implements CloneablePowerInterface, KeywordProvider {
    protected static final StringBuilder builder = new StringBuilder();
    protected static final float ICON_SIZE = 32f;
    protected static final float HITBOX_SIZE = ICON_SIZE * Settings.scale * 1.5f;
    protected static final Color disabledColor = new Color(0.5f, 0.5f, 0.5f, 1);
    public static final int DUMMY_MULT = 100;
    public static AbstractPlayer player = null;
    public static Random rng = null;
    protected final ArrayList<AbstractGameEffect> effects;
    public final ArrayList<EUIKeywordTooltip> tooltips = new ArrayList<>();
    public final PCLPowerData data;
    protected PowerStrings powerStrings;
    public AbstractCreature source;
    public EUIHitbox hb;
    public EUIKeywordTooltip mainTip;
    public boolean enabled = true;
    public boolean justApplied = false;
    public boolean hideAmount = false;
    public int baseAmount = 0;

    // Should not call this constructor without setting strings up through one of the setupStrings methods
    protected PCLPower(PCLPowerData data, AbstractCreature owner, AbstractCreature source, int amount) {
        this.data = data;
        this.ID = data.ID;
        this.effects = ReflectionHacks.getPrivate(this, AbstractPower.class, "effect");
        this.owner = owner;
        this.source = source;
        hb = new EUIHitbox(HITBOX_SIZE, HITBOX_SIZE);
        setup();
        setupProperties(amount);
        setupImages();
        setupDescription();
    }

    public static String createFullID(Class<? extends PCLPower> type) {
        return createFullID(PGR.core, type);
    }

    public static String createFullID(PCLResources<?, ?, ?, ?> resources, Class<? extends PCLPower> type) {
        return resources.createID(type.getSimpleName());
    }

    public static String deriveID(String base) {
        return base + "Power";
    }

    protected static PCLPowerData register(Class<? extends AbstractPower> type) {
        return register(type, PGR.core);
    }

    protected static PCLPowerData register(Class<? extends AbstractPower> type, PCLResources<?, ?, ?, ?> resources) {
        return registerPowerData(new PCLPowerData(type, resources));
    }

    protected static <T extends PCLPowerData> T registerPowerData(T cardData) {
        return PCLPowerData.registerPCLData(cardData);
    }

    // Remove unrecognized characters and sequences from the description pulled from by the base game and other mods
    // This kills me
    public static String sanitizePowerDescription(String description) {
        if (description == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < description.length(); i++) {
            char c = description.charAt(i);
            switch (c) {
                case '|':
                    sb.append(EUISmartText.NEWLINE);
                    break;
                case '[':
                case '†':
                    StringBuilder sub = new StringBuilder();
                    while (i + 1 < description.length()) {
                        i += 1;
                        c = description.charAt(i);
                        if (c == ']') {
                            break;
                        }
                        else {
                            sub.append(c);
                        }
                    }
                    String key = sub.toString();
                    EUIKeywordTooltip tip = EUIKeywordTooltip.findByIDTemp(key);
                    if (tip != null) {
                        String[] split = EUIUtils.splitString(" ", tip.title);
                        for (String s : split) {
                            sb.append("#y");
                            sb.append(s);
                            sb.append(' ');
                        }
                    }
                    else {
                        sb.append(key);
                    }
                    break;
                case '{':
                    sb.append("#y");
                    break;
                case '}':
                case ']':
                    continue;
                default:
                    sb.append(c);
            }
        }

        return sb.toString();
    }

    public float atDamageFinalGive(PCLUseInfo info, float block, DamageInfo.DamageType type, AbstractCard c) {
        return atDamageFinalGive(block, type, c);
    }

    public float atDamageGive(PCLUseInfo info, float block, DamageInfo.DamageType type, AbstractCard c) {
        return atDamageGive(block, type, c);
    }

    public void atEndOfRound() {
        switch (data.endTurnBehavior) {
            case SingleTurn:
                removePower();
                break;
            case TurnBased:
                if (justApplied) {
                    justApplied = false;
                }
                else {
                    reducePower(1);
                }
                break;
        }
    }

    public void atStartOfTurnPostDraw() {
        if (data.endTurnBehavior == PCLPowerData.Behavior.SingleTurnNext) {
            removePower();
        }
    }

    protected void findTooltipsFromText(String text) {

        boolean foundIcon = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (foundIcon) {
                if (']' != c) {
                    builder.append(c);
                    continue;
                }
                foundIcon = false;
                EUIKeywordTooltip tooltip = EUIKeywordTooltip.findByIDTemp(EUIUtils.invokeBuilder(builder));
                if (tooltip != null) {
                    tooltips.add(tooltip);
                }
            }
            else if ('[' == c) {
                foundIcon = true;
            }
        }

    }

    @Override
    public void flash() {
        this.effects.add(new PCLGainPowerEffect(this, true));
        PCLEffects.Queue.add(new PCLFlashPowerEffect(this));
    }

    @Override
    public void flashWithoutSound() {
        this.effects.add(new PCLGainPowerEffect(this, false));
        PCLEffects.Queue.add(new PCLFlashPowerEffect(this));
    }

    protected String formatDescription(int index, Object... args) {
        if (powerStrings == null || powerStrings.DESCRIPTIONS == null || powerStrings.DESCRIPTIONS.length <= index) {
            EUIUtils.logError(this, "powerStrings.DESCRIPTIONS does not exist, " + this.name);
            return "";
        }
        return EUIUtils.format(powerStrings.DESCRIPTIONS[index], args);
    }

    protected Color getBorderColor(Color c) {
        return (enabled) ? c : disabledColor;
    }

    protected Color getImageColor(Color c) {
        return (enabled) ? c : disabledColor;
    }

    public String getDisplayDescription() {
        return mainTip.description;
    }

    public String getID() {
        return ID;
    }

    protected ColoredString getPrimaryAmount(Color c) {
        if (amount != 0) {
            if (isTurnBased) {
                return new ColoredString(amount, Color.WHITE, c.a);
            }
            else if (this.amount >= 0) {
                return new ColoredString(amount, Color.GREEN, c.a);
            }
            else if (this.canGoNegative) {
                return new ColoredString(amount, Color.RED, c.a);
            }
        }

        return null;
    }

    protected ColoredString getSecondaryAmount(Color c) {
        return null;
    }

    @Override
    public List<EUIKeywordTooltip> getTips() {
        return tooltips;
    }

    @Override
    public EUIKeywordTooltip getTooltip() {
        return mainTip;
    }

    public String getUpdatedDescription() {
        return formatDescription(0, amount);
    }

    public boolean isPriorityTarget() {
        return false;
    }

    public void loadImagePath(String imagePath) {
        if (Gdx.files.internal(imagePath).exists()) {
            this.img = EUIRM.getTexture(imagePath);
        }
        if (this.img == null) {
            this.img = PCLCoreImages.CardAffinity.unknown.texture();
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return data.create(owner, source, amount);
    }

    public float modifyBlock(PCLUseInfo info, float block, AbstractCard c) {
        return modifyBlock(block, c);
    }

    public int modifyCost(AbstractCard card, int cost) {
        return cost;
    }

    public float modifyHeal(PCLUseInfo info, float block, AbstractCard c) {
        return block;
    }

    public float modifyHitCount(PCLUseInfo info, float block, AbstractCard c) {
        return block;
    }

    public float modifyOrbIncoming(float initial) {
        return initial;
    }

    public float modifyOrbOutgoing(float initial) {
        return initial;
    }

    public float modifyRightCount(PCLUseInfo info, float block, AbstractCard c) {
        return block;
    }

    public float modifySkillBonus(PCLUseInfo info, float block, AbstractCard c) {
        return block;
    }

    protected void onAmountChanged(int previousAmount, int difference) {
        if (difference != 0) {
            updateDescription();
        }
    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        super.onApplyPower(power, target, source);

        if (owner == target && power.ID.equals(ID)) {
            onSamePowerApplied(power);
        }
    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();

        onAmountChanged(0, amount);
    }

    @Override
    public void onRemove() {
        super.onRemove();

        final int previous = amount;
        amount = 0;
        onAmountChanged(previous, -previous);
    }

    public void onRemoveDamagePowers() {

    }

    protected void onSamePowerApplied(AbstractPower power) {

    }

    @Override
    public void reducePower(int reduceAmount) {
        final int previous = amount;
        super.reducePower(reduceAmount);
        if ((amount == 0) || (!canGoNegative && amount < 0)) {
            removePower();
        }

        onAmountChanged(previous, -Math.max(0, reduceAmount));
    }

    public RemoveSpecificPowerAction removePower() {
        return removePower(PCLActions.bottom);
    }

    public RemoveSpecificPowerAction removePower(PCLActions order) {
        return order.removePower(owner, owner, this);
    }

    @Override
    public void renderAmount(SpriteBatch sb, float x, float y, Color c) {
        if (hideAmount) {
            return;
        }

        ColoredString amount = getPrimaryAmount(c);
        if (amount != null) {
            FontHelper.renderFontRightTopAligned(sb, FontHelper.powerAmountFont, amount.text, x, y, fontScale, amount.color);
        }

        ColoredString amount2 = getSecondaryAmount(c);
        if (amount2 != null) {
            FontHelper.renderFontRightTopAligned(sb, FontHelper.powerAmountFont, amount2.text, x, y + 15f * Settings.scale, 1, amount2.color);
        }
    }

    @Override
    public void renderIcons(SpriteBatch sb, float x, float y, Color c) {
        if (!enabled) {
            disabledColor.a = c.a;
            c = disabledColor;
        }

        if (hb.cX != x || hb.cY != y) {
            hb.move(x, y);
        }

        Color borderColor = getBorderColor(c);
        Color imageColor = getImageColor(c);

        this.renderIconsImpl(sb, x, y, borderColor, imageColor);

        for (AbstractGameEffect e : effects) {
            e.render(sb, x, y);
        }
    }

    protected void renderIconsImpl(SpriteBatch sb, float x, float y, Color borderColor, Color imageColor) {
        if (this.region128 != null) {
            PCLRenderHelpers.drawCentered(sb, imageColor, this.region128, x, y, ICON_SIZE, ICON_SIZE, 1, 0);
        }
        else {
            PCLRenderHelpers.drawCentered(sb, imageColor, this.img, x, y, ICON_SIZE, ICON_SIZE, 1, 0);
        }
    }

    public int resetAmount() {
        final int previous = amount;
        this.amount = baseAmount;
        onAmountChanged(previous, (amount - previous));
        return amount;
    }

    public PCLPower setEnabled(boolean enable) {
        this.enabled = enable;

        return this;
    }

    public PCLPower setHitbox(EUIHitbox hb) {
        this.hb = hb;
        return this;
    }

    public void setup() {

    }

    protected void setupDescription() {
        this.name = powerStrings.NAME;
        String desc = getUpdatedDescription();
        mainTip = new EUIKeywordTooltip(name, desc);
        mainTip.icon = this.region128 != null ? this.region128 : img != null ? new TextureRegion(img) : null;
        tooltips.add(mainTip);
        findTooltipsFromText(desc);
        // Base game descriptions don't support special characters
        this.description = sanitizePowerDescription(desc);
    }

    protected void setupImages() {
        if (data.useRegionImage) {
            loadRegion(data.imagePath);
        }
        else {
            loadImagePath(data.imagePath);
        }
    }

    public void setupProperties(int amount) {
        this.baseAmount = this.amount = Math.min(data.maxAmount, amount);
        this.type = data.type;
        this.isTurnBased = data.endTurnBehavior == PCLPowerData.Behavior.TurnBased;
        this.isPostActionPower = data.isPostActionPower;
        this.justApplied = GameUtilities.isPlayerTurn(false);
        this.canGoNegative = data.minAmount < 0;
        this.powerStrings = data.strings;
    }

    public void stackPower(int stackAmount, boolean updateBaseAmount) {
        if (updateBaseAmount && (baseAmount += stackAmount) > data.maxAmount) {
            baseAmount = data.maxAmount;
        }
        if ((amount + stackAmount) > data.maxAmount) {
            stackAmount = data.maxAmount - amount;
        }

        final int previous = amount;
        super.stackPower(stackAmount);

        onAmountChanged(previous, stackAmount);
    }

    @Override
    public void stackPower(int stackAmount) {
        stackPower(stackAmount, true);
    }

    @Override
    public void update(int slot) {
        super.update(slot);
        updateHitbox();
        if (hb.hovered) {
            updateHoverLogic();
        }
    }

    @Override
    public void updateDescription() {
        if (this instanceof InvisiblePower) {
            this.description = EUIUtils.EMPTY_STRING;
            return;
        }

        String desc = getUpdatedDescription();
        mainTip.setDescription(desc);
        switch (type) {
            case BUFF:
                mainTip.setBackgroundColor(EUITooltip.TIP_BUFF);
                break;
            case DEBUFF:
                mainTip.setBackgroundColor(EUITooltip.TIP_DEBUFF);
            default:
                mainTip.setBackgroundColor(Color.WHITE);
        }
        this.description = sanitizePowerDescription(desc);
    }

    public void updateHitbox() {
        hb.update();
    }

    public void updateHoverLogic() {
        EUITooltip.queueTooltips(tooltips, InputHelper.mX + hb.width, InputHelper.mY + (hb.height * 0.5f));
    }
}