package pinacolada.skills;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
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
import extendedui.utilities.RotatingList;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.interfaces.subscribers.*;
import pinacolada.misc.CombatManager;
import pinacolada.misc.PCLUseInfo;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.fields.PField;
import pinacolada.skills.skills.PMultiSkill;
import pinacolada.skills.skills.PTrigger;
import pinacolada.utilities.GameUtilities;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class PSkill<T extends PField> implements TooltipProvider
{
    public static final String EFFECT_SEPARATOR = LocalizedStrings.PERIOD + " ";
    private static final HashMap<String, PSkillData<? extends PField>> EFFECT_MAP = new HashMap<>();
    private static final ArrayList<PSkillData<? extends PField>> AVAILABLE_SKILLS = new ArrayList<>();
    private static final TypeToken<PSkillSaveData> TToken = new TypeToken<PSkillSaveData>() {};
    private static final TypeToken<ArrayList<String>> TStringToken = new TypeToken<ArrayList<String>>() {};

    public static final char EFFECT_CHAR = 'E';
    public static final char XVALUE_CHAR = 'F';
    public static final char EXTRA_CHAR = 'G';
    public static final char CONDITION_CHAR = 'H';
    public static final int CHAR_OFFSET = 48;
    public static final int DEFAULT_MAX = Integer.MAX_VALUE / 2; // So that upgrade limits will not go out of bounds
    public static final int DEFAULT_EXTRA_MIN = -1; // Denotes infinity for tags and certain skills
    protected static final String CARD_SEPARATOR = "|";
    protected static final String SUB_SEPARATOR = "<";
    protected static final String BOUND_FORMAT = "¦{0}¦";
    protected static final String CONDITION_FORMAT = "║{0}║";
    protected static final String SINGLE_FORMAT = "1";
    public static final PCLCoreStrings TEXT = PGR.core.strings;
    public final PSkillData<T> data;
    public AbstractCard sourceCard;
    public ActionT3<PSkill<T>, Integer, Integer> customUpgrade; // Callback for customizing upgrading properties
    public ArrayList<EUITooltip> tips = new ArrayList<>();
    public PCLActions.ActionOrder order = PCLActions.ActionOrder.Bottom;
    public PCLCardTarget target = PCLCardTarget.None;
    public PCLCardValueSource amountSource = PCLCardValueSource.None;
    public PCLCardValueSource extraSource = PCLCardValueSource.None;
    public PSkill<?> parent;
    public PointerProvider source;
    public String effectID;
    public T fields;
    public UUID uuid = UUID.randomUUID();
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
    protected PSkill<?> childEffect;

    public PSkill(PSkillData<T> data, PSkillSaveData saveData)
    {
        this.data = data;
        initializeFromSaveData(saveData);
    }

    public PSkill(PSkillData<T> data)
    {
        this(data, PCLCardTarget.None, 1, -1);
    }

    public PSkill(PSkillData<T> data, PCLCardTarget target, int amount)
    {
        this(data, target, amount, -1);
    }

    public PSkill(PSkillData<T> data, PCLCardTarget target, int amount, int extra)
    {
        this.data = data;
        this.fields = this.data.instantiateField();
        if (this.fields != null)
        {
            this.fields.skill = this;
        }
        this.effectID = this.data.ID;
        this.target = target;
        this.rootAmount = this.baseAmount = this.amount = amount;
        this.rootExtra = this.baseExtra = this.extra = extra;
    }

    /** Try to capitalize the first letter in the entire effect text */
    public static String capital(String base, boolean can)
    {
        return can ? StringUtils.capitalize(base) : base;
    }

    /**
     * Establishes a parent child relationship from left to right
     * The first skill in the argument will be the parent of the second, the second the parent of the third, and so on
     * Returns the highest skill in the resulting chain
     * */
    public static <V extends PSkill<?>> V chain(V first, PSkill<?>... next)
    {
        PSkill<?> current = first;
        for (PSkill<?> ef : next)
        {
            if (current != null)
            {
                current.setChild(ef);
            }
            current = ef;
        }
        return first;
    }

    /** Establish an effectID for this effect based on its class name */
    public static String createFullID(Class<? extends PSkill<?>> type)
    {
        return PGR.core.createID(type.getSimpleName());
    }

    /** Attempts to deserialize the saved data of an effect expressed as a JSON. This method fetches the skill data listed in the save data and tries to initialize it with the save data's parameters.
     * Note that the "register" method of the DATA for a skill must have been called at some point for this method to work.
     * Any skill that has the @VisibleSkill annotation will be available. Other skills will need to have been called in some other class first for the static register method to be invoked. */
    public static PSkill<?> get(String serializedString)
    {
        try
        {
            PSkillSaveData saveData = EUIUtils.deserialize(serializedString, TToken.getType());
            PSkillData<?> skillData = EFFECT_MAP.get(saveData.effectID);
            // First attempt to load the PSkillSaveData constructor
            Constructor<? extends PSkill<?>> c = EUIUtils.tryGetConstructor(skillData.effectClass, PSkillSaveData.class);
            if (c != null)
            {
                return c.newInstance(saveData);
            }

            // If this fails, load the empty constructor and try to apply the save data through that
            c = EUIUtils.tryGetConstructor(skillData.effectClass);
            if (c != null)
            {
                return c.newInstance().initializeFromSaveData(saveData);
            }

            EUIUtils.logError(PSkill.class, "Unable to find constructor for skill " + saveData.effectID + " for effect class " + skillData.effectClass);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            EUIUtils.logError(PSkill.class, "Failed to deserialize: " + serializedString);
        }

        return null;
    }

    public static Set<String> getAllIDs()
    {
        return EFFECT_MAP.keySet();
    }

    public static PSkillData<?> getData(String id)
    {
        return EFFECT_MAP.get(id);
    }

    public static List<String> getEffectTexts(Collection<? extends PSkill<?>> effects, boolean addPeriod)
    {
        return EUIUtils.mapAsNonnull(effects, e -> e.getText(addPeriod));
    }

    public static List<String> getEffectTextsWithoutPeriod(Collection<? extends PSkill<?>> effects, boolean capitalize)
    {
        List<String> efTexts = getEffectTexts(effects, false);
        if (capitalize && efTexts.size() > 0)
        {
            efTexts.set(0, capital(efTexts.get(0), true));
        }
        return efTexts;
    }

    public static <U extends PSkill<?>> List<PSkillData<?>> getEligibleClasses(Class<U> targetClass)
    {
        return EUIUtils.filter(AVAILABLE_SKILLS, d -> targetClass.isAssignableFrom(d.effectClass));
    }

    public static <U extends PSkill<?>> List<PSkillData<?>> getEligibleClasses(Class<U> targetClass, AbstractCard.CardColor co)
    {
        return EUIUtils.filter(AVAILABLE_SKILLS, d -> targetClass.isAssignableFrom(d.effectClass) && d.isColorCompatible(co));
    }

    public static <U extends PSkill<?>> List<U> getEligibleEffects(Class<U> targetClass)
    {
        return getEligibleEffectsImpl(targetClass, getEligibleClasses(targetClass));
    }

    public static <U extends PSkill<?>> List<U> getEligibleEffects(Class<U> targetClass, AbstractCard.CardColor co)
    {
        return getEligibleEffectsImpl(targetClass, getEligibleClasses(targetClass, co));
    }

    /** Returns a list of all available skills that the current card color can use in the card editor, and that fall under the specified PSkill superclass */
    private static <U extends PSkill<?>> List<U> getEligibleEffectsImpl(Class<U> targetClass, List<PSkillData<?>> effects)
    {
        return EUIUtils.mapAsNonnull(effects, cl -> {
            Constructor<? extends PSkill<?>> c = EUIUtils.tryGetConstructor(cl.effectClass);
            if (c != null)
            {
                try
                {
                    return (U) c.newInstance();
                }
                catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
                {
                    e.printStackTrace();
                    EUIUtils.logError(targetClass, "Failed to load effect class for " + String.valueOf(cl));
                }
            }
            return null;
        })
                .stream()
                .sorted((a, b) -> StringUtils.compareIgnoreCase(a.getSampleText(), b.getSampleText()))
                .collect(Collectors.toList());
    }

    public static List<PCLCardSelection> getEligibleOrigins(PSkill<?> e)
    {
        return e != null ? e.getEligibleOrigins() : new ArrayList<>();
    }

    public static List<PCLCardGroupHelper> getEligiblePiles(PSkill<?> e)
    {
        return e != null ? e.getEligiblePiles() : new ArrayList<>();
    }

    public static List<PCLCardTarget> getEligibleTargets(PSkill<?> e)
    {
        return e != null ? e.getEligibleTargets() : new ArrayList<>();
    }

    /** Load the data for all skills annotated with @VisibleSkill for usage in the card editor
     * Note that even though they don't appear in the editor, PMultiBase and PCardPrimary skills still need to be loaded with this method to allow them to be deserialized in the editor
     * */
    public static void initialize()
    {
        for (Class<?> ct : GameUtilities.getClassesWithAnnotation(VisibleSkill.class))
        {
            try
            {
                VisibleSkill a = ct.getAnnotation(VisibleSkill.class);
                PSkillData<?> data = ReflectionHacks.getPrivateStatic(ct, a.data());
                if (!PMultiBase.class.isAssignableFrom(ct))
                {
                    AVAILABLE_SKILLS.add(data);
                }
                EUIUtils.logInfoIfDebug(PSkill.class, "Adding skill " + data.ID);
            }
            catch (Exception e)
            {
                EUIUtils.logError(PSkill.class, "Failed to load skill " + ct.getName() + ": " + e.getLocalizedMessage());
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

    /** Used by multibase effects to join a list of PSkills into a single JSON object for saving in the special field of PSkillSaveData */
    public static <T> String joinDataAsJson(Collection<T> items, FuncT1<String, T> stringFunction)
    {
        return items.size() > 0 ? EUIUtils.serialize(EUIUtils.mapAsNonnull(items, stringFunction), TStringToken.getType()) : null;
    }

    /** Creates a conglomerate string consisting of the text of all of the effects in the collection, in order */
    public static String joinEffectTexts(Collection<? extends PSkill<?>> effects)
    {
        return joinEffectTexts(effects, EUIUtils.SPLIT_LINE, true);
    }

    public static String joinEffectTexts(Collection<? extends PSkill<?>> effects, boolean addPeriod)
    {
        return joinEffectTexts(effects, EUIUtils.SPLIT_LINE, addPeriod);
    }

    public static String joinEffectTexts(Collection<? extends PSkill<?>> effects, String delimiter, boolean addPeriod)
    {
        return EUIUtils.joinStrings(delimiter, getEffectTexts(effects, addPeriod));
    }

    public static String joinEffectTextsWithoutPeriod(Collection<? extends PSkill<?>> effects)
    {
        return joinEffectTextsWithoutPeriod(effects, ", ", true);
    }

    public static String joinEffectTextsWithoutPeriod(Collection<? extends PSkill<?>> effects, boolean addPeriod)
    {
        return joinEffectTextsWithoutPeriod(effects, ", ", addPeriod);
    }

    public static String joinEffectTextsWithoutPeriod(Collection<? extends PSkill<?>> effects, String delimiter, boolean capitalize)
    {
        return EUIUtils.joinStrings(delimiter, getEffectTextsWithoutPeriod(effects, capitalize));
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

    public PSkill<T> addAmountForCombat(int amount)
    {
        this.baseAmount = this.amount = MathUtils.clamp(this.amount + amount, data != null ? data.minAmount : 0, data != null ? data.maxAmount : DEFAULT_MAX);
        return this;
    }

    public PSkill<T> addExtraForCombat(int amount)
    {
        this.baseExtra = this.extra = MathUtils.clamp(this.amount + amount, data != null ? data.minExtra : DEFAULT_EXTRA_MIN, data != null ? data.maxExtra : DEFAULT_MAX);
        return this;
    }

    public boolean canMatch(AbstractCard card)
    {
        return this.childEffect != null && this.childEffect.canMatch(card);
    }

    public boolean canPlay(PCLUseInfo info)
    {
        return this.childEffect == null || this.childEffect.canPlay(info);
    }

    public void displayUpgrades(boolean value)
    {
        displayUpgrades = value;
        setAmountFromCard();
        if (this.childEffect != null)
        {
            childEffect.displayUpgrades(value);
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
        return source != null ? EUIUtils.format(BOUND_FORMAT, "E" + getCardPointer()) : wrapAmountChild(amount);
    }

    /** Effects whose BASE amount is set to 0 target any number of cards */
    public String getAmountRawOrAllString()
    {
        return baseAmount <= 0 ? TEXT.subjects_all : getAmountRawString();
    }

    public EUITooltip getAttackTooltip()
    {
        return sourceCard instanceof PCLCard ? ((PCLCard) sourceCard).attackType.getTooltip() : PGR.core.tooltips.normalDamage;
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
                return wrapAmountChild(baseAmount);
            case XVALUE_CHAR:
                return getXString();
            case EXTRA_CHAR:
                return wrapAmount(extra);
            default:
                return "?";
        }
    }

    public String getCapitalSubText(boolean addPeriod)
    {
        return capital(getSubText(), addPeriod);
    }

    public final char getCardPointer()
    {
        if (source != null)
        {
            return (char) (source.getPointers().addAndGetIndex(this) + CHAR_OFFSET);
        }
        return CHAR_OFFSET;
    }

    public final PSkill<?> getChild()
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
        return getColoredValueString(wrapAmountChild(baseAmount), wrapAmountChild(amount));
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

    public final List<PCLCardSelection> getEligibleOrigins()
    {
        return data != null && !data.origins.isEmpty() ? data.origins : Arrays.asList(PCLCardSelection.values());
    }

    public final List<PCLCardGroupHelper> getEligiblePiles()
    {
        return data != null && !data.groups.isEmpty() ? data.groups : PCLCardGroupHelper.getStandard();
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

    public Color getGlowColor()
    {
        return childEffect != null ? childEffect.getGlowColor() : null;
    }

    public final PSkill<?> getHighestParent()
    {
        if (this.parent != null)
        {
            return this.parent.getHighestParent();
        }
        return this;
    }

    public final String getInheritedString()
    {
        return parent != null ? parent.getParentString() : this.getParentString();
    }

    public AbstractMonster.Intent getIntent() {
        return childEffect != null ? childEffect.getIntent() : AbstractMonster.Intent.MAGIC;
    }

    public final PSkill<?> getLowestChild()
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

    /**
     * If this skill is on a power, get the creature that this power is attached to. Otherwise, acts the same as getSourceCreature
     * */
    public AbstractCreature getOwnerCreature() {return parent != null ? parent.getOwnerCreature() : getSourceCreature();}

    public final PSkill<?> getParent()
    {
        return parent;
    }

    public String getParentString()
    {
        return TEXT.subjects_them;
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

    /**
     * Get the creature that owns this skill. Defaults to the player if no source was defined
     */
    public AbstractCreature getSourceCreature()
    {
        return source != null ? source.getSourceCreature() : AbstractDungeon.player;
    }

    public abstract String getSubText();

    public final ArrayList<AbstractCreature> getTargetList(PCLUseInfo info) {return info != null ? target.getTargets(info.source, info.target) : new ArrayList<>();}

    public String getTargetHasString(String desc)
    {
        return getTargetHasString(target, desc);
    }

    public String getTargetHasString(PCLCardTarget target, String desc)
    {
        return TEXT.cond_ifTargetHas(getTargetSubjectString(target), target.ordinal(), desc);
    }

    public String getTargetHasYouString(String desc)
    {
        return getTargetHasString(PCLCardTarget.None, desc);
    }

    public String getTargetSubjectString()
    {
        return getTargetSubjectString(target);
    }

    public String getTargetSubjectString(PCLCardTarget target)
    {
        switch (target)
        {
            case Single:
                return PGR.core.strings.subjects_target;
            case AllEnemy:
            case RandomEnemy:
                return PGR.core.strings.subjects_anyEnemy();
            case AllAlly:
            case RandomAlly:
            case Team:
                return PGR.core.strings.subjects_anyAlly();
            case All:
            case Any:
                return PGR.core.strings.subjects_anyone;
            case Self:
                if (isFromCreature())
                {
                    return TEXT.subjects_thisCard;
                }
            default:
                return PGR.core.strings.subjects_you;
        }
    }

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
                return TEXT.subjects_allX(PCLCoreStrings.pluralForce(TEXT.subjects_characterN));
            case AllAlly:
                return TEXT.subjects_allX(PCLCoreStrings.pluralForce(TEXT.subjects_allyN));
            case AllEnemy:
                return TEXT.subjects_allX(PCLCoreStrings.pluralForce(TEXT.subjects_enemyN));
            case Any:
                return TEXT.subjects_anyone;
            case RandomAlly:
                return EUIRM.strings.numNoun(count, TEXT.subjects_randomX(PCLCoreStrings.pluralEvaluated(TEXT.subjects_allyN, count)));
            case RandomEnemy:
                return EUIRM.strings.numNoun(count, TEXT.subjects_randomX(PCLCoreStrings.pluralEvaluated(TEXT.subjects_enemyN, count)));
            case Single:
                return count > 1 ? EUIRM.strings.numNoun(count, PCLCoreStrings.pluralEvaluated(TEXT.subjects_enemyN, count)) : TEXT.subjects_enemy;
            case SingleAlly:
                return count > 1 ? EUIRM.strings.numNoun(count, PCLCoreStrings.pluralEvaluated(TEXT.subjects_allyN, count)) : TEXT.subjects_ally;
            case Team:
                return TEXT.subjects_your(target.getTitle().toLowerCase());
            case Self:
                if (isFromCreature())
                {
                    return TEXT.subjects_thisCard;
                }
            default:
                return TEXT.subjects_you;
        }
    }

    public String getText(int index, boolean addPeriod)
    {
        return childEffect != null ? childEffect.getText(index, addPeriod) : getText(addPeriod);
    }

    public String getText(boolean addPeriod)
    {
        return getCapitalSubText(addPeriod) + (childEffect != null ? PCLCoreStrings.period(true) + " " + childEffect.getText(addPeriod) : PCLCoreStrings.period(addPeriod));
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
        return TEXT.cond_whenMulti(getTargetSubjectString(), impl);
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

    // Necessary because we need to pass in class names, which are not reified
    @SuppressWarnings("rawtypes")
    public boolean hasChildType(Class<? extends PSkill> childType)
    {
        return childType.isInstance(childEffect) || (childEffect != null && childEffect.hasChildType(childType));
    }

    // Necessary because we need to pass in class names, which are not reified
    @SuppressWarnings("rawtypes")
    public final boolean hasParentType(Class<? extends PSkill> parentType)
    {
        return parentType.isInstance(this) || (parent != null && parent.hasParentType(parentType));
    }

    public final boolean hasSameProperties(PSkill<?> other)
    {
        return other != null && effectID.equals(other.effectID) && fields.equals(other.fields);
    }

    public final boolean hasSameUUID(PSkill<?> other)
    {
        return other != null && other.uuid.equals(this.uuid);
    }

    public final boolean isCardColor(AbstractCard.CardColor co)
    {
        return sourceCard != null && GameUtilities.getActingCardColor(sourceCard) == co;
    }

    public final boolean isCompatible(AbstractCard.CardColor co)
    {
        return EFFECT_MAP.get(effectID).isColorCompatible(co);
    }

    // Used to determine whether the effect should actually be saved or rendered on the card
    public boolean isBlank() {return this.childEffect != null && this.childEffect.isBlank();}

    // Used to determine whether this effect is detrimental to the owner
    public boolean isDetrimental()
    {
        return false;
    }

    protected PSkill<T> initializeFromSaveData(PSkillSaveData saveData)
    {
        this.effectID = saveData.effectID;
        this.target = PCLCardTarget.valueOf(saveData.target);
        this.amountSource = PCLCardValueSource.valueOf(saveData.valueSource);
        this.rootAmount = this.baseAmount = this.amount = saveData.amount;
        this.rootExtra = this.baseExtra = this.extra = saveData.extra;
        this.upgrade = saveData.upgrade;
        this.upgradeExtra = saveData.upgradeExtra;
        this.fields = EUIUtils.deserialize(saveData.effectData, this.data.fieldType);
        this.fields.skill = this;
        this.useParent = saveData.useParent;

        if (saveData.children != null)
        {
            this.childEffect = PSkill.get(saveData.children);
            if (this.childEffect != null)
            {
                this.childEffect.parent = this;
            }
        }
        return this;
    }

    public final boolean isFromCreature()
    {
        return (sourceCard != null && sourceCard.type == PCLEnum.CardType.SUMMON) || (getSourceCreature() instanceof AbstractMonster);
    }

    public final boolean isSelfOnlyTarget()
    {
        return (target == PCLCardTarget.None || (target == PCLCardTarget.Self && !isFromCreature()));
    }

    /*
        Make a copy of this skill with copies of its properties
        Suppressing rawtype warning because we cannot reify the constructor class any further
    */
    @SuppressWarnings("rawtypes")
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
            copy.uuid = uuid;

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

    public PCLUseInfo makeInfo(AbstractCreature target)
    {
        return CombatManager.playerSystem.generateInfo(sourceCard, getSourceCreature(), target);
    }

    public PSkill<T> makePreviews(RotatingList<EUICardPreview> previews)
    {
        if (this.childEffect != null)
        {
            this.childEffect.makePreviews(previews);
        }
        return this;
    }

    public float modifyBlock(PCLUseInfo info, float amount)
    {
        return this.childEffect != null ? this.childEffect.modifyBlock(info, amount) : amount;
    }

    public float modifyDamage(PCLUseInfo info, float amount)
    {
        return this.childEffect != null ? this.childEffect.modifyDamage(info, amount) : amount;
    }

    public float modifyHeal(PCLUseInfo info, float amount)
    {
        return this.childEffect != null ? this.childEffect.modifyHeal(info, amount) : amount;
    }

    public float modifyHitCount(PCLUseInfo info, float amount)
    {
        return this.childEffect != null ? this.childEffect.modifyHitCount(info, amount) : amount;
    }

    public float modifyMagicNumber(PCLUseInfo info, float amount)
    {
        return this.childEffect != null ? this.childEffect.modifyMagicNumber(info, amount) : amount;
    }

    public float modifyRightCount(PCLUseInfo info, float amount)
    {
        return this.childEffect != null ? this.childEffect.modifyRightCount(info, amount) : amount;
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
        return EUIUtils.format(PGR.core.strings.subjects_cardN, baseAmount);
    }

    public final String pluralCardExtra()
    {
        return EUIUtils.format(PGR.core.strings.subjects_cardN, baseExtra);
    }

    public void refresh(PCLUseInfo info, boolean conditionMet)
    {
        if (this.childEffect != null)
        {
            this.childEffect.refresh(info, conditionMet);
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

    public PSkill<T> setChain(PSkill<?> effect, PSkill<?>... effects)
    {
        this.childEffect = PSkill.chain(effect, effects);
        this.childEffect.parent = this;
        this.childEffect.setSource(this.source);
        return this;
    }

    public PSkill<T> setChild(PSkill<?> effect)
    {
        this.childEffect = effect;
        if (effect != null)
        {
            effect.parent = this;
            effect.setSource(this.source);
        }
        return this;
    }

    public PSkill<T> setChild(PSkill<?>... effects)
    {
        this.childEffect = new PMultiSkill(effects);
        this.childEffect.parent = this;
        this.childEffect.setSource(this.source);
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
        this.source = card;
        this.sourceCard = EUIUtils.safeCast(card, AbstractCard.class);
        this.amount = getAmountFromCard();
        this.baseAmount = getAmountBaseFromCard();
        this.extra = getExtraFromCard();
        this.baseExtra = getExtraBaseFromCard();
        if (this.childEffect != null)
        {
            this.childEffect.setSource(card);
        }
        return this;
    }

    public PSkill<T> setSource(PointerProvider card, PCLCardValueSource valueSource)
    {
        return setSource(card, valueSource, extraSource);
    }

    public PSkill<T> setSource(PointerProvider card, PCLCardValueSource valueSource, PCLCardValueSource extraSource)
    {
        this.amountSource = valueSource;
        this.extraSource = extraSource;
        return setSource(card);
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

    public PSkill<T> stack(PSkill<?> other)
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

    public void subscribeChildren()
    {
        if (this instanceof PCLCombatSubscriber)
        {
            ((PCLCombatSubscriber) this).subscribeToAll();
        }
        if (this.childEffect != null)
        {
            this.childEffect.subscribeChildren();
        }
    }

    public void unsubscribeChildren()
    {
        if (this instanceof PCLCombatSubscriber)
        {
            ((PCLCombatSubscriber) this).unsubscribeFromAll();
        }
        if (this.childEffect != null)
        {
            this.childEffect.unsubscribeChildren();
        }
    }

    public void triggerOnAllyDeath(PCLCard c, PCLCardAlly ally)
    {
        if (this instanceof OnAllyDeathSubscriber)
        {
            ((OnAllyDeathSubscriber) this).onAllyDeath(c, ally);
        }
        else if (this.childEffect != null)
        {
            this.childEffect.triggerOnAllyDeath(c, ally);
        }
    }

    public void triggerOnAllySummon(PCLCard c, PCLCardAlly ally)
    {
        if (this instanceof OnAllySummonSubscriber)
        {
            ((OnAllySummonSubscriber) this).onAllySummon(c, ally);
        }
        else if (this.childEffect != null)
        {
            this.childEffect.triggerOnAllySummon(c, ally);
        }
    }

    public void triggerOnAllyTrigger(PCLCard c, PCLCardAlly ally)
    {
        if (this instanceof OnAllyTriggerSubscriber)
        {
            ((OnAllyTriggerSubscriber) this).onAllyTrigger(c, ally);
        }
        else if (this.childEffect != null)
        {
            this.childEffect.triggerOnAllyTrigger(c, ally);
        }
    }

    public void triggerOnAllyWithdraw(PCLCard c, PCLCardAlly ally)
    {
        if (this instanceof OnAllyWithdrawSubscriber)
        {
            ((OnAllyWithdrawSubscriber) this).onAllyWithdraw(c, ally);
        }
        else if (this.childEffect != null)
        {
            this.childEffect.triggerOnAllyWithdraw(c, ally);
        }
    }

    public void triggerOnCreate(AbstractCard c, boolean startOfBattle)
    {
        if (this instanceof OnCardCreatedSubscriber)
        {
            ((OnCardCreatedSubscriber) this).onCardCreated(c, startOfBattle);
        }
        else if (this.childEffect != null)
        {
            this.childEffect.triggerOnCreate(c, startOfBattle);
        }
    }

    public void triggerOnDiscard(AbstractCard c)
    {
        if (this instanceof OnCardDiscardedSubscriber)
        {
            ((OnCardDiscardedSubscriber) this).onCardDiscarded(c);
        }
        else if (this.childEffect != null)
        {
            this.childEffect.triggerOnDiscard(c);
        }
    }

    public void triggerOnDraw(AbstractCard c)
    {
        if (this instanceof OnCardDrawnSubscriber)
        {
            ((OnCardDrawnSubscriber) this).onCardDrawn(c);
        }
        else if (this.childEffect != null)
        {
            this.childEffect.triggerOnDraw(c);
        }
    }

    public boolean triggerOnEndOfTurn(boolean isUsing)
    {
        if (this instanceof OnEndOfTurnFirstSubscriber)
        {
            ((OnEndOfTurnFirstSubscriber) this).onEndOfTurnFirst(isUsing);
            return true;
        }
        else if (this.childEffect != null)
        {
            return this.childEffect.triggerOnEndOfTurn(isUsing);
        }
        return false;
    }

    public void triggerOnExhaust(AbstractCard c)
    {
        if (this instanceof OnCardExhaustedSubscriber)
        {
            ((OnCardExhaustedSubscriber) this).onCardExhausted(c);
        }
        else if (this.childEffect != null)
        {
            this.childEffect.triggerOnExhaust(c);
        }
    }

    public void triggerOnOtherCardPlayed(AbstractCard c)
    {
        if (this instanceof OnCardPlayedSubscriber)
        {
            ((OnCardPlayedSubscriber) this).onCardPlayed(c);
        }
        else if (this.childEffect != null)
        {
            this.childEffect.triggerOnOtherCardPlayed(c);
        }
    }

    public void triggerOnPurge(AbstractCard c)
    {
        if (this instanceof OnCardPurgedSubscriber)
        {
            ((OnCardPurgedSubscriber) this).onPurge(c);
        }
        else if (this.childEffect != null)
        {
            this.childEffect.triggerOnPurge(c);
        }
    }

    public void triggerOnReshuffle(AbstractCard c, CardGroup sourcePile)
    {
        if (this instanceof OnCardReshuffledSubscriber)
        {
            ((OnCardReshuffledSubscriber) this).onCardReshuffled(c, sourcePile);
        }
        else if (this.childEffect != null)
        {
            this.childEffect.triggerOnReshuffle(c, sourcePile);
        }
    }

    public void triggerOnRetain(AbstractCard c)
    {
        if (this instanceof OnCardRetainSubscriber)
        {
            ((OnCardRetainSubscriber) this).onRetain(c);
        }
        else if (this.childEffect != null)
        {
            this.childEffect.triggerOnRetain(c);
        }
    }

    public void triggerOnScry(AbstractCard c)
    {
        if (this instanceof OnCardScrySubscriber)
        {
            ((OnCardScrySubscriber) this).onScry(c);
        }
        else if (this.childEffect != null)
        {
            this.childEffect.triggerOnScry(c);
        }
    }

    public boolean tryPassParent(PSkill<?> source, PCLUseInfo info)
    {
        return parent == null || parent.tryPassParent(source, info);
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

    public String wrapAmountChild(int input)
    {
        return wrapAmountChild(wrapAmount(input));
    }

    public String wrapAmountChild(String input)
    {
        return parent != null ? parent.wrapAmountChild(input) : input;
    }

    public String wrapExtra(int input)
    {
        return wrapAmount(input);
    }

    public String wrapExtraChild(String input)
    {
        return parent != null ? parent.wrapExtraChild(input) : input;
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
