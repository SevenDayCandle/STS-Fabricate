package pinacolada.skills;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.stances.AbstractStance;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT3;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.text.EUISmartText;
import extendedui.ui.tooltips.EUICardPreview;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.ColoredString;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.PCLActions;
import pinacolada.augments.PCLAugment;
import pinacolada.cards.base.*;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.interfaces.markers.Hidden;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.interfaces.markers.PointerProvider;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.powers.PCLClickableUse;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.fields.PField;
import pinacolada.skills.skills.PMultiSkill;
import pinacolada.skills.skills.base.primary.PTrigger_Interactable;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RotatingList;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public abstract class PSkill<T extends PField> implements TooltipProvider
{
    public static final String EFFECT_SEPARATOR = LocalizedStrings.PERIOD + " ";
    private static final HashMap<String, PSkillData> EFFECT_MAP = new HashMap<>();
    private static final TypeToken<PSkillSaveData> TToken = new TypeToken<PSkillSaveData>() {};
    private static final TypeToken<ArrayList<String>> TStringToken = new TypeToken<ArrayList<String>>() {};
    private static final String PREFIX_EFFECTS = "pinacolada.skills.skills";

    public static final char EFFECT_CHAR = 'E';
    public static final char XVALUE_CHAR = 'F';
    public static final char EXTRA_CHAR = 'G';
    public static final char CONDITION_CHAR = 'H';
    public static final int CHAR_OFFSET = 48;
    public static final int DEFAULT_MAX = Integer.MAX_VALUE / 2; // So that upgrade limits will not go out of bounds
    public static final int DEFAULT_EXTRA_MIN = -1;
    public static final int DEFAULT_PRIORITY = 4;
    protected static final String CARD_SEPARATOR = "|";
    protected static final String SUB_SEPARATOR = "<";
    protected static final String BOUND_FORMAT = "¦{0}¦";
    protected static final String CONDITION_FORMAT = "║{0}║";
    protected static final String SINGLE_FORMAT = "1";
    public static final PCLCoreStrings TEXT = PGR.core.strings;
    public final UUID uuid = UUID.randomUUID();
    public final PSkillData<T> data;
    public String effectID;
    public AbstractCard sourceCard;
    public PointerProvider source;
    public ArrayList<EUITooltip> tips = new ArrayList<>();
    public T fields;
    public PCLActions.ActionOrder order = PCLActions.ActionOrder.Bottom;
    public PCLCardTarget target = PCLCardTarget.None;
    public PCLCardValueSource amountSource = PCLCardValueSource.None;
    public PCLCardValueSource extraSource = PCLCardValueSource.None;
    public PSkill parent;
    public ActionT3<PSkill<T>, Integer, Integer> customUpgrade; // Callback for customizing upgrading properties
    public boolean useParent;
    public boolean displayUpgrades;
    public int amount;
    public int baseAmount; // Used for determining PMod modified values
    public int rootAmount; // Used for determining upgrades
    public int extra = -1;
    public int baseExtra = extra;
    public int rootExtra = baseExtra;
    public int[] upgrade = new int[]{0};
    public int[] upgradeExtra = new int[]{0};
    protected PSkill childEffect;

    public PSkill(PSkillSaveData data)
    {
        this.effectID = data.effectID;
        this.data = getData(this.effectID);
        this.target = PCLCardTarget.valueOf(data.target);
        this.amountSource = PCLCardValueSource.valueOf(data.valueSource);
        this.rootAmount = this.baseAmount = this.amount = data.amount;
        this.rootExtra = this.baseExtra = this.extra = data.extra;
        this.upgrade = data.upgrade;
        this.upgradeExtra = data.upgradeExtra;
        this.fields = EUIUtils.deserialize(data.effectData, this.data.fieldType);
        this.fields.skill = this;
        this.useParent = data.useParent;

        if (data.children != null)
        {
            this.childEffect = PSkill.get(data.children);
            if (this.childEffect != null)
            {
                this.childEffect.parent = this;
            }
        }
    }

    public PSkill(PSkillData<T> data)
    {
        this(data, PCLCardTarget.None, 1, 0);
    }

    public PSkill(PSkillData<T> data, PCLCardTarget target, int amount)
    {
        this(data, target, amount, 0);
    }

    public PSkill(PSkillData<T> data, PCLCardTarget target, int amount, int upgrade)
    {
        this.data = data;
        this.fields = this.data.instantiateField();
        this.fields.skill = this;
        this.effectID = this.data.ID;
        this.target = target;
        this.rootAmount = this.baseAmount = this.amount = amount;
        this.upgrade = new int[]{upgrade};
    }

    public static String capital(String base, boolean can)
    {
        return can ? StringUtils.capitalize(base) : base;
    }

    public static <V extends PSkill> V chain(V first, PSkill... next)
    {
        PSkill current = first;
        for (PSkill ef : next)
        {
            if (current != null)
            {
                current.setChild(ef);
            }
            current = ef;
        }
        return first;
    }

    public static String createFullID(Class<? extends PSkill> type)
    {
        return PGR.core.createID(type.getSimpleName());
    }

    public static PSkill get(String serializedString)
    {
        try
        {
            PSkillSaveData data = EUIUtils.deserialize(serializedString, TToken.getType());
            Constructor<? extends PSkill> c = EUIUtils.tryGetConstructor(EFFECT_MAP.get(data.effectID).effectClass, PSkillSaveData.class);
            if (c != null)
            {
                return c.newInstance(data);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            EUIUtils.logError(PSkill.class, "Failed to deserialize: " + serializedString);
        }

        return null;
    }

    public static Collection<PSkillData> getAllClasses()
    {
        return EFFECT_MAP.values();
    }

    public static Set<String> getAllIDs()
    {
        return EFFECT_MAP.keySet();
    }

    public static PSkillData getData(String id)
    {
        return EFFECT_MAP.get(id);
    }

    public static List<String> getEffectTexts(Collection<? extends PSkill> effects, boolean addPeriod)
    {
        return EUIUtils.mapAsNonnull(effects, e -> e.getText(addPeriod));
    }

    public static <U extends PSkill> List<PSkillData> getEligibleClasses(AbstractCard.CardColor co, Class<U> targetClass)
    {
        return EUIUtils.filter(getAllClasses(), d -> targetClass.isAssignableFrom(d.effectClass) && d.isColorCompatible(co));
    }

    public static <U extends PSkill> List<U> getEligibleEffects(AbstractCard.CardColor co, Class<U> targetClass)
    {
        return EUIUtils.mapAsNonnull(getEligibleClasses(co, targetClass), cl -> {
            // Do not show composite or hidden effects in the effect editor
            if (PMultiBase.class.isAssignableFrom(cl.effectClass) || Hidden.class.isAssignableFrom(cl.effectClass))
            {
                return null;
            }
            Constructor<? extends U> c = EUIUtils.tryGetConstructor(cl.effectClass);
            if (c != null)
            {
                try
                {
                    return c.newInstance();
                }
                catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
                {
                    e.printStackTrace();
                }
            }
            return null;
        })
                .stream()
                .sorted((a, b) -> StringUtils.compareIgnoreCase(a.getSampleText(), b.getSampleText()))
                .collect(Collectors.toList());
    }

    public static List<PCLCardGroupHelper> getEligiblePiles(PSkill e)
    {
        return e != null ? e.getEligiblePiles() : new ArrayList<>();
    }

    public static List<PCLCardTarget> getEligibleTargets(PSkill e)
    {
        return e != null ? e.getEligibleTargets() : new ArrayList<>();
    }

    public static String idOf(PSkillData data)
    {
        return data != null ? data.ID : null;
    }

    // Each ID must be called at least once for it to appear in the card editor
    public static void initialize()
    {
        ArrayList<String> effectClassNames = PGR.getClassNamesFromJarFile(PREFIX_EFFECTS);
        for (String s : effectClassNames)
        {
            try
            {
                Class<?> name = Class.forName(s);
                if (!Modifier.isAbstract(name.getModifiers()))
                {
                    PSkillData data = ReflectionHacks.getPrivateStatic(name, "DATA");
                    EUIUtils.logInfoIfDebug(PSkill.class, "Adding effect " + data.ID);
                }
            }
            catch (Exception e)
            {
                EUIUtils.logError(PCLAugment.class, "Failed to load " + s + ": " + e.getLocalizedMessage());
            }
        }
    }

    public static <T> String joinData(T[] items, FuncT1<String, T> stringFunction)
    {
        return joinData(items, stringFunction, SUB_SEPARATOR);
    }

    public static <T> String joinData(T[] items, FuncT1<String, T> stringFunction, String separator)
    {
        return items.length > 0 ? EUIUtils.joinStrings(separator, EUIUtils.map(items, stringFunction)) : null;
    }

    public static <T> String joinData(Collection<T> items, FuncT1<String, T> stringFunction)
    {
        return joinData(items, stringFunction, SUB_SEPARATOR);
    }

    public static <T> String joinData(Collection<T> items, FuncT1<String, T> stringFunction, String separator)
    {
        return items.size() > 0 ? EUIUtils.joinStrings(separator, EUIUtils.mapAsNonnull(items, stringFunction)) : null;
    }

    public static <T> String joinDataAsJson(T[] items, FuncT1<String, T> stringFunction)
    {
        return items.length > 0 ? EUIUtils.serialize(EUIUtils.mapAsNonnull(items, stringFunction), TStringToken.getType()) : null;
    }

    public static <T> String joinDataAsJson(Collection<T> items, FuncT1<String, T> stringFunction)
    {
        return items.size() > 0 ? EUIUtils.serialize(EUIUtils.mapAsNonnull(items, stringFunction), TStringToken.getType()) : null;
    }

    public static String joinEffectTexts(Collection<? extends PSkill> effects)
    {
        return joinEffectTexts(effects, EUIUtils.SPLIT_LINE, true);
    }

    public static String joinEffectTexts(Collection<? extends PSkill> effects, boolean addPeriod)
    {
        return joinEffectTexts(effects, EUIUtils.SPLIT_LINE, addPeriod);
    }

    public static String joinEffectTexts(Collection<? extends PSkill> effects, String delimiter, boolean addPeriod)
    {
        return EUIUtils.joinStrings(delimiter, getEffectTexts(effects, addPeriod));
    }

    public static <T extends PField> PSkillData<T> register(Class<? extends PSkill<T>> type, Class<T> fieldType)
    {
        return register(type, fieldType, 0, DEFAULT_MAX);
    }

    public static <T extends PField> PSkillData<T> register(Class<? extends PSkill<T>> type, Class<T> fieldType, AbstractCard.CardColor... cardColors)
    {
        return register(type, fieldType, 0, DEFAULT_MAX, cardColors);
    }

    public static <T extends PField> PSkillData<T> register(Class<? extends PSkill<T>> type, Class<T> fieldType, int minAmount, int maxAmount, AbstractCard.CardColor... cardColors)
    {
        String id = PGR.core.createID(type.getSimpleName());
        PSkillData<T> d = new PSkillData<T>(id, type, fieldType, minAmount, maxAmount, cardColors);
        EFFECT_MAP.put(id, d);
        return d;
    }

    public static String[] split(String source)
    {
        return split(source, SUB_SEPARATOR);
    }

    public static String[] split(String source, String separator)
    {
        return EUIUtils.splitString(separator, source);
    }

    public static List<String> splitJson(String source)
    {
        return EUIUtils.deserialize(source, TStringToken.getType());
    }

    public String getSpecialData()
    {
        return null;
    }

    public PSkill addAmountForCombat(int amount)
    {
        this.baseAmount = this.amount = MathUtils.clamp(this.amount + amount, data != null ? data.minAmount : 0, data != null ? data.maxAmount : DEFAULT_MAX);
        return this;
    }

    public PSkill addExtraForCombat(int amount)
    {
        this.baseExtra = this.extra = MathUtils.clamp(this.amount + amount, data != null ? data.minExtra : DEFAULT_EXTRA_MIN, data != null ? data.maxExtra : DEFAULT_MAX);
        return this;
    }

    public boolean canMatch(AbstractCard card)
    {
        return this.childEffect != null && this.childEffect.canMatch(card);
    }

    public boolean canPlay(AbstractCard card, AbstractMonster m)
    {
        return this.childEffect == null || this.childEffect.canPlay(card, m);
    }

    public void displayUpgrades()
    {
        displayUpgrades = true;
        setAmountFromCard();
        if (this.childEffect != null)
        {
            childEffect.displayUpgrades();
        }
    }

    public final PSkill<T> edit(ActionT1<T> editFunc)
    {
        editFunc.invoke(fields);
        return this;
    }

    public final PCLActions getActions()
    {
        switch (order)
        {
            case Top:
                return PCLActions.top;
            case Last:
                return PCLActions.last;
            case Instant:
                return PCLActions.instant;
            case TurnStart:
                return PCLActions.turnStart;
            case Delayed:
                return PCLActions.delayed;
            case DelayedTop:
                return PCLActions.delayedTop;
            case NextCombat:
                return PCLActions.nextCombat;
            default:
                return PCLActions.bottom;
        }
    }

    public final int getAmountFromCard()
    {
        if (this.sourceCard != null)
        {
            if (this.amountSource != null)
            {
                switch (amountSource)
                {
                    case Block:
                        return sourceCard.block;
                    case Damage:
                        return sourceCard.damage;
                    case HitCount:
                        return sourceCard instanceof EditorCard ? ((EditorCard) sourceCard).hitCount() : 1;
                    case MagicNumber:
                        return sourceCard.magicNumber;
                    case SecondaryNumber:
                        return sourceCard.heal;
                    case RightCount:
                        return sourceCard instanceof EditorCard ? ((EditorCard) sourceCard).rightCount() : 1;
                    case XValue:
                        return sourceCard instanceof EditorCard ? ((EditorCard) sourceCard).xValue() : 0;
                }
            }

            return rootAmount + sourceCard.timesUpgraded * getUpgrade();
        }
        if (source != null && amountSource == PCLCardValueSource.XValue)
        {
            return source.xValue();
        }
        return rootAmount;
    }

    public final int getAmountBaseFromCard()
    {
        if (this.sourceCard != null && this.amountSource != null)
        {
            switch (amountSource)
            {
                case Block:
                    return sourceCard.baseBlock;
                case Damage:
                    return sourceCard.baseDamage;
                case HitCount:
                    return sourceCard instanceof EditorCard ? ((EditorCard) sourceCard).hitCountBase() : 1;
                case MagicNumber:
                    return sourceCard.baseMagicNumber;
                case SecondaryNumber:
                    return sourceCard.baseHeal;
                case RightCount:
                    return sourceCard instanceof EditorCard ? ((EditorCard) sourceCard).rightCountBase() : 1;
                case XValue:
                    return sourceCard instanceof EditorCard ? ((EditorCard) sourceCard).xValue() : 0;
            }
        }
        return amount;
    }

    public final String getAmountRawString()
    {
        return source != null ? EUIUtils.format(BOUND_FORMAT, "E" + getCardPointer()) : wrapAmount(amount);
    }

    public final int getAttribute(char attributeID)
    {
        switch (attributeID)
        {
            case EFFECT_CHAR:
                return amount;
            case XVALUE_CHAR:
                return getXValue(sourceCard);
            case EXTRA_CHAR:
                return extra;
            default:
                return baseAmount;
        }
    }

    public String getAttributeString(char attributeID)
    {
        switch (attributeID)
        {
            case EFFECT_CHAR:
                return wrapAmount(baseAmount);
            case XVALUE_CHAR:
                return getXString();
            case EXTRA_CHAR:
                return wrapAmount(extra);
            default:
                return "?";
        }
    }

    public final char getCardPointer()
    {
        if (source != null)
        {
            return (char) (source.getPointers().addAndGetIndex(this) + CHAR_OFFSET);
        }
        return CHAR_OFFSET;
    }

    public final PSkill getChild()
    {
        return this.childEffect;
    }

    public final ColoredString getColoredAttributeString(char attributeID)
    {
        switch (attributeID)
        {
            case EFFECT_CHAR:
                return getColoredValueString();
            case XVALUE_CHAR:
                return getColoredXString();
            case EXTRA_CHAR:
                return getColoredExtraString();
            default:
                return new ColoredString("?", Settings.RED_TEXT_COLOR);
        }
    }

    public ColoredString getColoredExtraString()
    {
        return getColoredExtraString(wrapExtra(baseExtra), wrapExtra(extra));
    }

    public ColoredString getColoredExtraString(Object displayBase, Object displayAmount)
    {
        if (hasParentType(PMod.class))
        {
            return new ColoredString(displayBase, (displayUpgrades && getUpgradeExtra() != 0) ? Settings.GREEN_TEXT_COLOR : Settings.CREAM_COLOR);
        }

        return new ColoredString(displayAmount, (displayUpgrades && getUpgradeExtra() != 0) || extra > baseExtra ? Settings.GREEN_TEXT_COLOR : extra < baseExtra ? Settings.RED_TEXT_COLOR : Settings.CREAM_COLOR);
    }

    public ColoredString getColoredValueString()
    {
        return getColoredValueString(wrapAmount(baseAmount), wrapAmount(amount));
    }

    public ColoredString getColoredValueString(Object displayBase, Object displayAmount)
    {
        if (hasParentType(PMod.class))
        {
            return new ColoredString(displayBase, (displayUpgrades && getUpgrade() != 0) ? Settings.GREEN_TEXT_COLOR : Settings.CREAM_COLOR);
        }

        return new ColoredString(displayAmount, (displayUpgrades && getUpgrade() != 0) || amount > baseAmount ? Settings.GREEN_TEXT_COLOR : amount < baseAmount ? Settings.RED_TEXT_COLOR : Settings.CREAM_COLOR);
    }

    public ColoredString getColoredXString()
    {
        String text = getXString();

        return new ColoredString(text, Settings.GREEN_TEXT_COLOR);
    }

    public Color getConditionColor()
    {
        return null;
    }

    public final String getConditionRawString()
    {
        return EUIUtils.format(CONDITION_FORMAT, getCardPointer());
    }

    public final List<PCLCardGroupHelper> getEligiblePiles()
    {
        return data != null && !data.groups.isEmpty() ? data.groups : PCLCardGroupHelper.getAll();
    }

    public final List<PCLCardTarget> getEligibleTargets()
    {
        return data != null && !data.targets.isEmpty() ? data.targets : PCLCardTarget.getAll();
    }

    public String getExportString(char attributeID)
    {
        switch (attributeID)
        {
            case EFFECT_CHAR:
                return baseAmount + " (" + getUpgrade() + ")";
            case EXTRA_CHAR:
                return baseExtra + " (" + getUpgradeExtra() + ")";
            default:
                return "";
        }
    }

    public String getExportText()
    {
        String baseString = getText(true);
        if (source != null)
        {
            return source.makeExportString(baseString);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < baseString.length(); i++)
        {
            char c = baseString.charAt(i);
            if (c == '$')
            {
                StringBuilder sub = new StringBuilder();
                while (i + 1 < baseString.length())
                {
                    i += 1;
                    c = baseString.charAt(i);
                    sub.append(c);
                    if (c == '$')
                    {
                        break;
                    }
                }
                sb.append(EUISmartText.parseLogicString(sub.toString()));
            }
            else if (!(c == '{' || c == '}' || c == '[' || c == ']'))
            {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public final int getExtraFromCard()
    {
        if (this.sourceCard != null)
        {
            if (this.extraSource != null)
            {
                switch (extraSource)
                {
                    case Block:
                        return sourceCard.block;
                    case Damage:
                        return sourceCard.damage;
                    case HitCount:
                        return sourceCard instanceof EditorCard ? ((EditorCard) sourceCard).hitCount() : 1;
                    case MagicNumber:
                        return sourceCard.magicNumber;
                    case SecondaryNumber:
                        return sourceCard.heal;
                    case RightCount:
                        return sourceCard instanceof EditorCard ? ((EditorCard) sourceCard).rightCount() : 1;
                    case XValue:
                        return sourceCard instanceof EditorCard ? ((EditorCard) sourceCard).xValue() : 0;
                }
            }

            return rootExtra + sourceCard.timesUpgraded * getUpgradeExtra();
        }
        if (source != null && extraSource == PCLCardValueSource.XValue)
        {
            return source.xValue();
        }
        return rootExtra;
    }

    public final int getExtraBaseFromCard()
    {
        if (this.sourceCard != null && this.extraSource != null)
        {
            switch (extraSource)
            {
                case Block:
                    return sourceCard.baseBlock;
                case Damage:
                    return sourceCard.baseDamage;
                case HitCount:
                    return sourceCard instanceof EditorCard ? ((EditorCard) sourceCard).hitCountBase() : 1;
                case MagicNumber:
                    return sourceCard.baseMagicNumber;
                case SecondaryNumber:
                    return sourceCard.baseHeal;
                case RightCount:
                    return sourceCard instanceof EditorCard ? ((EditorCard) sourceCard).rightCountBase() : 1;
                case XValue:
                    return sourceCard instanceof EditorCard ? ((EditorCard) sourceCard).xValue() : 0;
            }
        }
        return extra;
    }

    public final String getExtraRawString()
    {
        return source != null ? EUIUtils.format(BOUND_FORMAT, "G" + getCardPointer()) : wrapExtra(amount);
    }

    public final String getInheritedString()
    {
        return parent != null ? parent.getParentString() : this.getParentString();
    }

    public final PSkill getLowestChild()
    {
        if (this.childEffect != null)
        {
            return this.childEffect.getLowestChild();
        }
        return this;
    }

    public final int getMaxAmount()
    {
        return data != null ? data.maxAmount : DEFAULT_MAX;
    }

    public final int getMinAmount()
    {
        return data != null ? data.minAmount : 0;
    }

    public final String getName()
    {
        return source != null ? source.getName() : "";
    }

    public final PSkill getParent()
    {
        return parent;
    }

    public String getParentString()
    {
        return TEXT.subjects.them;
    }

    public PCLCard getPCLSource()
    {
        PCLCard choiceCard = EUIUtils.safeCast(sourceCard, PCLCard.class);
        if (choiceCard == null)
        {
            choiceCard = new QuestionMark();
        }
        return choiceCard;
    }

    public String getPowerText()
    {
        if (source != null)
        {
            return source.makePowerString(getText(true));
        }
        return getText(true);
    }

    public final String getRawString(char attributeID)
    {
        switch (attributeID)
        {
            case EFFECT_CHAR:
                return getAmountRawString();
            case XVALUE_CHAR:
                return getXRawString();
            case EXTRA_CHAR:
                return getExtraRawString();
            default:
                return SINGLE_FORMAT;
        }
    }

    public String getSampleText()
    {
        return getSubText();
    }

    public AbstractCreature getSourceCreature()
    {
        return source != null ? source.getSourceCreature() : AbstractDungeon.player;
    }

    public abstract String getSubText();

    public final ArrayList<AbstractCreature> getTargetList(PCLUseInfo info) {return target.getTargets(info.source, info.target);}

    public String getTargetString()
    {
        return getTargetString(target);
    }

    public String getTargetString(PCLCardTarget target)
    {
        return getTargetString(target, 1);
    }

    public String getTargetString(PCLCardTarget target, int count)
    {
        switch (target)
        {
            case All:
                return TEXT.subjects.allX(PCLCoreStrings.pluralForce(TEXT.subjects.characterN));
            case AllAlly:
                return TEXT.subjects.allX(PCLCoreStrings.pluralForce(TEXT.subjects.allyN));
            case AllEnemy:
                return TEXT.subjects.allX(PCLCoreStrings.pluralForce(TEXT.subjects.enemyN));
            case Any:
                return TEXT.subjects.anyone;
            case RandomAlly:
                return EUIRM.strings.numNoun(count, TEXT.subjects.randomX(PCLCoreStrings.pluralEvaluated(TEXT.subjects.allyN, count)));
            case RandomEnemy:
                return EUIRM.strings.numNoun(count, TEXT.subjects.randomX(PCLCoreStrings.pluralEvaluated(TEXT.subjects.enemyN, count)));
            case Single:
                return EUIRM.strings.numNoun(count, PCLCoreStrings.pluralEvaluated(TEXT.subjects.enemyN, count));
            case SingleAlly:
                return EUIRM.strings.numNoun(count, PCLCoreStrings.pluralEvaluated(TEXT.subjects.allyN, count));
            case Team:
                return TEXT.subjects.your(target.getTitle());
            case Self:
                if (isFromCreature())
                {
                    return TEXT.subjects.thisObj;
                }
            default:
                return TEXT.subjects.you;
        }
    }


    public String getText(int index, boolean addPeriod)
    {
        return childEffect != null ? childEffect.getText(index, addPeriod) : getText(addPeriod);
    }

    public String getText(boolean addPeriod)
    {
        return capital(getSubText(), addPeriod) + (childEffect != null ? PCLCoreStrings.period(true) + " " + childEffect.getText(addPeriod) : PCLCoreStrings.period(addPeriod));
    }

    public final String getText()
    {
        return getText(true);
    }

    @Override
    public List<EUITooltip> getTips()
    {
        return tips;
    }

    public final int getUpgrade()
    {
        if (upgrade == null || upgrade.length == 0)
        {
            return 0;
        }
        return upgrade[Math.min(getUpgradeForm(), upgrade.length - 1)];
    }

    public final int getUpgradeExtra()
    {
        if (upgradeExtra == null || upgradeExtra.length == 0)
        {
            return 0;
        }
        return upgradeExtra[Math.min(getUpgradeForm(), upgradeExtra.length - 1)];
    }

    public final int getUpgradeForm()
    {
        return sourceCard instanceof PCLCard ? ((PCLCard) sourceCard).getForm() : 0;
    }

    public final int getUpgradeLevel()
    {
        return sourceCard != null ? sourceCard.timesUpgraded : 0;
    }

    public final String getWheneverString(Object impl)
    {
        switch (target)
        {
            case Single:
                return TEXT.conditions.whenMulti(TEXT.subjects.target, impl);
            case AllEnemy:
                return TEXT.conditions.whenMulti(TEXT.subjects.anyEnemy(), impl);
            case AllAlly:
                return TEXT.conditions.whenMulti(TEXT.subjects.anyAlly(), impl);
            case All:
                return TEXT.conditions.whenMulti(TEXT.subjects.anyone, impl);
            default:
                return TEXT.conditions.wheneverYou(impl);
        }
    }

    public final String getXRawString()
    {
        return EUIUtils.format(BOUND_FORMAT, "F" + getCardPointer());
    }

    public String getXString()
    {
        // Do not show the x value for powers
        if (GameUtilities.inBattle() && sourceCard != null && !hasParentType(PTrigger.class))
        {
            return " (" + getXValue(sourceCard) + ")";
        }
        return "";
    }

    public int getXValue(AbstractCard card)
    {
        return amount;
    }

    public final boolean hasParentType(Class<? extends PSkill> parentType)
    {
        return parentType.isInstance(this) || (parent != null && parent.hasParentType(parentType));
    }

    public final boolean hasSameProperties(PSkill other)
    {
        return effectID.equals(other.effectID) && fields.equals(other.fields);
    }

    public final boolean isCardColor(AbstractCard.CardColor co)
    {
        return sourceCard != null && GameUtilities.getActingCardColor(sourceCard) == co;
    }

    public final boolean isCompatible(AbstractCard.CardColor co)
    {
        return EFFECT_MAP.get(effectID).isColorCompatible(co);
    }

    public boolean isDetrimental()
    {
        return false;
    }

    public final boolean isFromCreature()
    {
        return (sourceCard != null && sourceCard.type == PCLEnum.CardType.SUMMON) || (getSourceCreature() instanceof AbstractMonster);
    }

    public final boolean isTrigger()
    {
        return hasParentType(PTrigger.class) && !hasParentType(PTrigger_Interactable.class) && !(parent != null && parent.hasParentType(PCond.class));
    }

    public PSkill<T> makeCopy()
    {
        PSkill<T> copy = null;
        try
        {
            Constructor<? extends PSkill> c = EUIUtils.tryGetConstructor(this.getClass());
            if (c != null)
            {
                copy = c.newInstance();
                makeCopyProperties(copy);
            }
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            e.printStackTrace();
        }

        return copy;
    }

    protected void makeCopyProperties(PSkill<T> copy)
    {
        if (copy != null)
        {
            copy.effectID = effectID;
            copy.target = target;
            copy.amountSource = amountSource;
            copy.rootAmount = copy.baseAmount = copy.amount = amount;
            copy.rootExtra = copy.baseExtra = copy.extra = extra;
            copy.upgrade = upgrade.clone();
            copy.upgradeExtra = upgradeExtra.clone();
            copy.fields = (T) fields.makeCopy();
            copy.fields.skill = copy;
            copy.order = order;
            copy.useParent = useParent;
            copy.source = source;
            copy.sourceCard = sourceCard;

            // Copy children
            if (this.childEffect != null)
            {
                copy.childEffect = this.childEffect.makeCopy();
                if (copy.childEffect != null)
                {
                    copy.childEffect.parent = copy;
                }
            }
        }
    }

    protected PCLUseInfo makeInfo(AbstractCreature target)
    {
        return new PCLUseInfo(sourceCard, getSourceCreature(), target);
    }

    public PSkill<T> makePreviews(RotatingList<EUICardPreview> previews)
    {
        if (this.childEffect != null)
        {
            this.childEffect.makePreviews(previews);
        }
        return this;
    }

    public float modifyBlock(AbstractCard card, AbstractMonster m, float amount)
    {
        return this.childEffect != null ? this.childEffect.modifyBlock(card, m, amount) : amount;
    }

    public float modifyDamage(AbstractCard card, AbstractMonster m, float amount)
    {
        return this.childEffect != null ? this.childEffect.modifyDamage(card, m, amount) : amount;
    }

    public float modifyHitCount(PCLCard card, AbstractMonster m, float amount)
    {
        return this.childEffect != null ? this.childEffect.modifyHitCount(card, m, amount) : amount;
    }

    public float modifyMagicNumber(AbstractCard card, AbstractMonster m, float amount)
    {
        return this.childEffect != null ? this.childEffect.modifyMagicNumber(card, m, amount) : amount;
    }

    public float modifyRightCount(PCLCard card, AbstractMonster m, float amount)
    {
        return this.childEffect != null ? this.childEffect.modifyRightCount(card, m, amount) : amount;
    }

    public PSkill<T> onAddToCard(AbstractCard card)
    {
        if (this.childEffect != null)
        {
            this.childEffect.onAddToCard(card);
        }
        return this;
    }

    public void onDrag(AbstractMonster m)
    {
        if (this.childEffect != null)
        {
            this.childEffect.onDrag(m);
        }
    }

    public void onDisplay(AbstractMonster m)
    {
        if (this.childEffect != null)
        {
            this.childEffect.onDisplay(m);
        }
    }

    public PSkill<T> onRemoveFromCard(AbstractCard card)
    {
        if (this.childEffect != null)
        {
            this.childEffect.onRemoveFromCard(card);
        }
        return this;
    }

    public void onUpgrade()
    {
        if (customUpgrade != null)
        {
            customUpgrade.invoke(this, getUpgradeForm(), getUpgradeLevel());
        }
    }

    public final ArrayList<PCLAffinity> parseAffinities(String source)
    {
        return EUIUtils.mapAsNonnull(split(source), PCLAffinity::valueOf);
    }

    public final ArrayList<PCLCardGroupHelper> parseCardGroups(String source)
    {
        return EUIUtils.mapAsNonnull(split(source), PCLCardGroupHelper::get);
    }

    public final ArrayList<AbstractCard.CardRarity> parseRarities(String source)
    {
        return EUIUtils.mapAsNonnull(split(source), AbstractCard.CardRarity::valueOf);
    }

    public final ArrayList<PCLCardTag> parseTags(String source)
    {
        return EUIUtils.mapAsNonnull(split(source), PCLCardTag::get);
    }

    public final ArrayList<AbstractCard.CardType> parseTypes(String source)
    {
        return EUIUtils.mapAsNonnull(split(source), AbstractCard.CardType::valueOf);
    }

    public final String plural(EUITooltip obj)
    {
        return PCLCoreStrings.plural(obj, getRawString(EFFECT_CHAR));
    }

    public final String plural(EUITooltip obj, char effect)
    {
        return PCLCoreStrings.plural(obj, getRawString(effect));
    }

    public final String pluralCard()
    {
        return EUIRM.strings.numNoun(getAmountRawString(), PGR.core.strings.subjects.cardN);
    }

    public void refresh(AbstractCreature m, AbstractCard c, boolean conditionMet)
    {
        if (this.childEffect != null)
        {
            this.childEffect.refresh(m, c, conditionMet);
        }
    }

    public boolean removable()
    {
        return this.childEffect == null || this.childEffect.removable();
    }

    public boolean requiresTarget()
    {
        return target == PCLCardTarget.Single || (this.childEffect != null && this.childEffect.requiresTarget());
    }

    public final PSkill<T> scanForTips()
    {
        tips.clear();
        EUIGameUtils.scanForTips(getSampleText(), tips);
        return this;
    }

    public final String serialize()
    {
        return EUIUtils.serialize(new PSkillSaveData(this), TToken.getType());
    }

    public PSkill<T> setAmount(int amount)
    {
        this.rootAmount = this.baseAmount = this.amount = MathUtils.clamp(amount, data != null ? data.minAmount : 0, data != null ? data.maxAmount : DEFAULT_MAX);
        return this;
    }

    public PSkill<T> setAmount(int amount, int upgrade)
    {
        this.upgrade = new int[]{upgrade};
        setAmount(amount);
        return this;
    }

    public PSkill<T> setAmountFromCard()
    {
        this.amount = getAmountFromCard();
        this.baseAmount = getAmountBaseFromCard();
        this.extra = getExtraFromCard();
        this.baseExtra = getExtraBaseFromCard();
        if (this.childEffect != null)
        {
            childEffect.setAmountFromCard();
        }
        return this;
    }

    public PSkill<T> setChild(PSkill effect)
    {
        this.childEffect = effect;
        if (effect != null)
        {
            effect.parent = this;
        }
        return this;
    }

    public PSkill<T> setChild(PSkill... effects)
    {
        this.childEffect = new PMultiSkill(effects);
        this.childEffect.parent = this;
        return this;
    }

    public PSkill<T> setCustomUpgrade(ActionT3<PSkill<T>, Integer, Integer> customUpgrade)
    {
        this.customUpgrade = customUpgrade;
        return this;
    }

    public PSkill<T> setExtra(int amount)
    {
        this.rootExtra = this.baseExtra = this.extra = MathUtils.clamp(amount, data != null ? data.minExtra : DEFAULT_EXTRA_MIN, data != null ? data.maxExtra : DEFAULT_MAX);
        return this;
    }

    public PSkill<T> setExtra(int amount, int upgrade)
    {
        this.upgradeExtra = new int[]{upgrade};
        setExtra(amount);
        return this;
    }

    public final PSkill<T> setOrder(PCLActions.ActionOrder order)
    {
        this.order = order;
        return this;
    }

    public PSkill<T> setSource(PointerProvider card)
    {
        return setSource(card, amountSource);
    }

    public PSkill<T> setSource(PointerProvider card, PCLCardValueSource valueSource)
    {
        return setSource(card, valueSource, extraSource);
    }

    public PSkill<T> setSource(PointerProvider card, PCLCardValueSource valueSource, PCLCardValueSource extraSource)
    {
        this.source = card;
        this.sourceCard = EUIUtils.safeCast(card, AbstractCard.class);
        this.amountSource = valueSource;
        this.extraSource = extraSource;
        this.amount = getAmountFromCard();
        this.baseAmount = getAmountBaseFromCard();
        this.extra = getExtraFromCard();
        this.baseExtra = getExtraBaseFromCard();
        if (this.childEffect != null)
        {
            childEffect.setSource(card, valueSource, extraSource);
        }
        return this;
    }

    public PSkill<T> setAmountSource(PCLCardValueSource valueSource)
    {
        this.amountSource = valueSource;
        this.amount = getAmountFromCard();
        this.baseAmount = getAmountBaseFromCard();
        return this;
    }

    public PSkill<T> setExtraSource(PCLCardValueSource valueSource)
    {
        this.extraSource = valueSource;
        this.extra = getExtraFromCard();
        this.baseExtra = getExtraBaseFromCard();
        return this;
    }

    public final PSkill<T> setTarget(PCLCardTarget target)
    {
        this.target = target;
        return this;
    }

    public PSkill<T> setTemporaryAmount(int amount)
    {
        this.amount = amount;
        return this;
    }

    public PSkill<T> setTemporaryExtra(int amount)
    {
        this.extra = amount;
        return this;
    }

    public PSkill<T> setUpgrade(int... upgrade)
    {
        this.upgrade = upgrade;
        return this;
    }

    public PSkill<T> setUpgradeExtra(int... upgrade)
    {
        this.upgradeExtra = upgrade;
        return this;
    }

    public PSkill<T> stack(PSkill other)
    {
        if (rootAmount > 0 && other.rootAmount > 0)
        {
            setAmount(rootAmount + other.rootAmount);
        }
        if (rootExtra > 0 && other.rootExtra > 0)
        {
            setExtra(rootExtra + other.rootExtra);
        }
        setAmountFromCard();

        if (this.childEffect != null && other.childEffect != null)
        {
            this.childEffect.stack(other.childEffect);
        }

        return this;
    }

    public boolean triggerOnAllyDeath(PCLCard c, PCLCardAlly ally)
    {
        return this.childEffect != null && this.childEffect.triggerOnAllySummon(c, ally);
    }

    public boolean triggerOnAllySummon(PCLCard c, PCLCardAlly ally)
    {
        return this.childEffect != null && this.childEffect.triggerOnAllySummon(c, ally);
    }

    public boolean triggerOnAllyTrigger(PCLCard c, PCLCardAlly ally)
    {
        return this.childEffect != null && this.childEffect.triggerOnAllySummon(c, ally);
    }

    public boolean triggerOnAllyWithdraw(PCLCard c, PCLCardAlly ally)
    {
        return this.childEffect != null && this.childEffect.triggerOnAllySummon(c, ally);
    }

    public boolean triggerOnApplyPower(AbstractCreature source, AbstractCreature target, AbstractPower c)
    {
        return this.childEffect != null && this.childEffect.triggerOnApplyPower(source, target, c);
    }

    public int triggerOnAttack(DamageInfo info, int damageAmount, AbstractCreature target)
    {
        return this.childEffect != null ? this.childEffect.triggerOnAttack(info, damageAmount, target) : damageAmount;
    }

    public int triggerOnAttacked(DamageInfo info, int damageAmount)
    {
        return this.childEffect != null ? this.childEffect.triggerOnAttacked(info, damageAmount) : damageAmount;
    }

    public boolean triggerOnCreate(AbstractCard c, boolean startOfBattle)
    {
        return this.childEffect != null && this.childEffect.triggerOnCreate(c, startOfBattle);
    }

    public boolean triggerOnDiscard(AbstractCard c)
    {
        return this.childEffect != null && this.childEffect.triggerOnDiscard(c);
    }

    public boolean triggerOnDraw(AbstractCard c)
    {
        return this.childEffect != null && this.childEffect.triggerOnDraw(c);
    }

    public boolean triggerOnElementReact(AffinityReactions reactions, AbstractCreature target)
    {
        return this.childEffect != null && this.childEffect.triggerOnElementReact(reactions, target);
    }

    public boolean triggerOnEndOfTurn(boolean isUsing)
    {
        return this.childEffect != null && this.childEffect.triggerOnEndOfTurn(isUsing);
    }

    public boolean triggerOnExhaust(AbstractCard c)
    {
        return this.childEffect != null && this.childEffect.triggerOnExhaust(c);
    }

    public boolean triggerOnIntensify(PCLAffinity af)
    {
        return this.childEffect != null && this.childEffect.triggerOnIntensify(af);
    }

    public boolean triggerOnMatch(AbstractCard c, PCLUseInfo info)
    {
        return this.childEffect != null && this.childEffect.triggerOnMatch(c, info);
    }

    public boolean triggerOnMismatch(AbstractCard c, PCLUseInfo info)
    {
        return this.childEffect != null && this.childEffect.triggerOnMismatch(c, info);
    }

    public boolean triggerOnOrbChannel(AbstractOrb c)
    {
        return this.childEffect != null && this.childEffect.triggerOnOrbChannel(c);
    }

    public boolean triggerOnOrbEvoke(AbstractOrb c)
    {
        return this.childEffect != null && this.childEffect.triggerOnOrbEvoke(c);
    }

    public boolean triggerOnOrbFocus(AbstractOrb c)
    {
        return this.childEffect != null && this.childEffect.triggerOnOrbFocus(c);
    }

    public boolean triggerOnOrbTrigger(AbstractOrb c)
    {
        return this.childEffect != null && this.childEffect.triggerOnOrbTrigger(c);
    }

    public boolean triggerOnOtherCardPlayed(AbstractCard c)
    {
        return this.childEffect != null && this.childEffect.triggerOnOtherCardPlayed(c);
    }

    public boolean triggerOnPCLPowerUsed(PCLClickableUse c)
    {
        return this.childEffect != null && this.childEffect.triggerOnPCLPowerUsed(c);
    }

    public boolean triggerOnPurge(AbstractCard c)
    {
        return this.childEffect != null && this.childEffect.triggerOnPurge(c);
    }

    public boolean triggerOnReshuffle(AbstractCard c, CardGroup sourcePile)
    {
        return this.childEffect != null && this.childEffect.triggerOnReshuffle(c, sourcePile);
    }

    public boolean triggerOnScry()
    {
        return this.childEffect != null && this.childEffect.triggerOnScry();
    }

    public boolean triggerOnShuffle(boolean triggerRelics)
    {
        return this.childEffect != null && this.childEffect.triggerOnShuffle(triggerRelics);
    }

    public boolean triggerOnStanceChange(AbstractStance stance1, AbstractStance stance2)
    {
        return false;
    }

    public boolean triggerOnStartOfTurn()
    {
        return this.childEffect != null && this.childEffect.triggerOnStartOfTurn();
    }

    public boolean triggerOnStartup()
    {
        return this.childEffect != null && this.childEffect.triggerOnStartup();
    }

    public void use(PCLUseInfo info)
    {
        if (this.childEffect != null)
        {
            this.childEffect.use(info);
        }
    }

    public void use(PCLUseInfo info, int index)
    {
        use(info);
    }

    public void use(PCLUseInfo info, boolean isUsing)
    {
        use(info);
    }

    public PSkill<T> useParent(boolean value)
    {
        this.useParent = value;
        return this;
    }

    public String wrapAmount(int input)
    {
        return String.valueOf(input);
    }

    public String wrapExtra(int input)
    {
        return wrapAmount(input);
    }

    public enum PCLCardValueSource
    {
        None,
        Damage,
        Block,
        MagicNumber,
        SecondaryNumber,
        HitCount,
        RightCount,
        XValue,
    }
}
