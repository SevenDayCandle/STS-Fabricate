package pinacolada.skills;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT3;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.text.EUITextHelper;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUIPreview;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.ColoredString;
import extendedui.utilities.RotatingList;
import extendedui.utilities.TupleT2;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.PCLActions;
import pinacolada.actions.cards.TryChooseChoice;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLDynamicCardData;
import pinacolada.cards.base.cardText.ConditionToken;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.interfaces.providers.ClickableProvider;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.interfaces.providers.ValueProvider;
import pinacolada.interfaces.subscribers.*;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.powers.PCLClickableUse;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.fields.PField;
import pinacolada.skills.skills.PMultiSkill;
import pinacolada.skills.skills.base.primary.PTrigger_When;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.GameUtilities;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class PSkill<T extends PField> implements TooltipProvider {
    private static final HashMap<String, PSkillData<? extends PField>> EFFECT_MAP = new HashMap<>();
    private static final ArrayList<PSkillData<? extends PField>> AVAILABLE_SKILLS = new ArrayList<>();
    private static final TypeToken<PSkillSaveData> TToken = new TypeToken<PSkillSaveData>() {
    };
    private static final TypeToken<ArrayList<String>> TStringToken = new TypeToken<ArrayList<String>>() {
    };
    private static final String SUB_SEPARATOR = "<";
    private static final String BOUND_FORMAT = "¦{0}¦";
    private static final String CONDITION_FORMAT = "║{0}║";
    private static final String SINGLE_FORMAT = "1";
    protected final static String[] PTEXT = CardCrawlGame.languagePack.getUIString("RunHistoryPathNodes").TEXT;
    public static final String EFFECT_SEPARATOR = LocalizedStrings.PERIOD + " ";
    public static final String COLON_SEPARATOR = ": ";
    public static final String COMMA_SEPARATOR = ", ";
    public static final char CASCADE_CHAR = '~';
    public static final char EFFECT_CHAR = 'E';
    public static final char XVALUE_CHAR = 'F';
    public static final char EXTRA_CHAR = 'G';
    public static final char EXTRA2_CHAR = 'H';
    public static final char SCOPE_CHAR = 'I';
    public static final char UPGRADE_CHAR = 'U';
    public static final char CAPITAL_CHAR = 'C';
    public static final char LOWER_CHAR = 'c';
    public static final int CHAR_OFFSET = 48;
    public static final int DEFAULT_MAX = Integer.MAX_VALUE / 2; // So that upgrade limits will not go out of bounds
    public static final int DEFAULT_EXTRA_MIN = -1; // Denotes infinity for tags and certain skills
    public static final PCLCoreStrings TEXT = PGR.core.strings;
    public final PSkillData<T> data;
    protected ActionT3<PSkill<T>, Integer, Integer> customUpgrade; // Callback for customizing upgrading properties
    protected List<EUIKeywordTooltip> tips;
    protected PSkill<?> childEffect;
    protected UUID uuid = UUID.randomUUID();
    public Object source;
    public PCLCardTarget target = PCLCardTarget.None;
    public PSkill<?> parent;
    public String effectID;
    public String overrideDesc;
    public T fields;
    public boolean useParent;
    public boolean displayUpgrades;
    public int amount;
    public int baseAmount; // Used for determining PMod modified values
    public int rootAmount; // Used for determining upgrades
    public int extra = -1;
    public int baseExtra = extra;
    public int rootExtra = baseExtra;
    public int extra2 = 0;
    public int baseExtra2 = extra2;
    public int rootExtra2 = baseExtra2;
    public int scope = 1;
    public int baseScope = scope;
    public int rootScope = baseScope;
    public int[] upgrade;
    public int[] upgradeExtra;
    public int[] upgradeExtra2;
    public int[] upgradeScope;

    public PSkill(PSkillData<T> data, PSkillSaveData saveData) {
        this.data = data;
        initializeFromSaveData(saveData);
    }

    public PSkill(PSkillData<T> data) {
        this(data, PCLCardTarget.None, 1, -1);
    }

    public PSkill(PSkillData<T> data, T fields) {
        this(data, fields, PCLCardTarget.None, 1, -1);
    }

    public PSkill(PSkillData<T> data, PCLCardTarget target, int amount, int extra) {
        this(data, data.instantiateField(), target, amount, extra);
    }

    public PSkill(PSkillData<T> data, T fields, PCLCardTarget target, int amount, int extra) {
        this.data = data;
        this.fields = fields;
        if (this.fields != null) {
            this.fields.skill = this;
        }
        this.effectID = this.data.ID;
        this.target = target;
        this.rootAmount = this.baseAmount = this.amount = amount;
        this.rootExtra = this.baseExtra = this.extra = extra;
    }

    public PSkill(PSkillData<T> data, PCLCardTarget target, int amount) {
        this(data, target, amount, -1);
    }

    /**
     * Try to capitalize the first letter in the entire effect text. For condition tokens, we should change the character token instead
     */
    public static String capital(String base) {
        if (base != null && base.length() > 0 && base.charAt(0) == ConditionToken.TOKEN) {
            return base.substring(0, 2).toUpperCase() + base.substring(2);
        }
        return StringUtils.capitalize(base);
    }

    public static String capital(String base, boolean can) {
        return can ? capital(base) : base;
    }

    /**
     * Establishes a parent child relationship from left to right
     * The first skill in the argument will be the parent of the second, the second the parent of the third, and so on
     * Returns the highest skill in the resulting chain
     */
    public static <V extends PSkill<?>> V chain(V first, PSkill<?>... next) {
        PSkill<?> current = first;
        for (PSkill<?> ef : next) {
            if (current != null) {
                current.setChild(ef);
            }
            current = ef;
        }
        return first;
    }

    /**
     * Establish an effectID for this effect based on its class name
     */
    public static String createFullID(Class<? extends PSkill<?>> type) {
        return PGR.core.createID(type.getSimpleName());
    }

    /**
     * Attempts to deserialize the saved data of an effect expressed as a JSON. This method fetches the skill data listed in the save data and tries to initialize it with the save data's parameters.
     * Note that the "register" method of the DATA for a skill must have been called at some point for this method to work.
     * Any skill that has the @VisibleSkill annotation will be available. Other skills will need to have been called in some other class first for the static register method to be invoked.
     */
    public static PSkill<?> get(String serializedString) {
        try {
            PSkillSaveData saveData = EUIUtils.deserialize(serializedString, TToken.getType());
            PSkillData<?> skillData = EFFECT_MAP.get(saveData.effectID);
            // First attempt to load the PSkillSaveData constructor
            Constructor<? extends PSkill<?>> c = EUIUtils.tryGetConstructor(skillData.effectClass, PSkillSaveData.class);
            if (c != null) {
                return c.newInstance(saveData);
            }

            // If this fails, load the empty constructor and try to apply the save data through that
            c = EUIUtils.tryGetConstructor(skillData.effectClass);
            if (c != null) {
                return c.newInstance().initializeFromSaveData(saveData);
            }

            EUIUtils.logError(PSkill.class, "Unable to find constructor for skill " + saveData.effectID + " for effect class " + skillData.effectClass);
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(PSkill.class, "Failed to deserialize: " + serializedString + ". Error was: " + e.getLocalizedMessage());
        }

        return null;
    }

    public static Set<String> getAllIDs() {
        return EFFECT_MAP.keySet();
    }

    public static PSkillData<?> getData(String id) {
        return EFFECT_MAP.get(id);
    }

    public static List<String> getEffectDisplayTexts(Collection<? extends PSkill<?>> effects, PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return EUIUtils.mapAsNonnull(effects, e -> e.getTextForDisplay(perspective, requestor, addPeriod));
    }

    public static List<String> getEffectTexts(Collection<? extends PSkill<?>> effects, PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return EUIUtils.mapAsNonnull(effects, e -> e.getText(perspective, requestor, addPeriod));
    }

    public static List<String> getEffectTextsWithoutPeriod(Collection<? extends PSkill<?>> effects, PCLCardTarget perspective, Object requestor, boolean capitalize) {
        List<String> efTexts = getEffectTexts(effects, perspective, requestor, false);
        if (capitalize && !efTexts.isEmpty()) {
            efTexts.set(0, capital(efTexts.get(0), true));
        }
        return efTexts;
    }

    public static <U extends PSkill<?>> ArrayList<PSkillData<?>> getEligibleClasses(Class<U> targetClass) {
        return EUIUtils.filter(AVAILABLE_SKILLS, d -> targetClass.isAssignableFrom(d.effectClass));
    }

    @SafeVarargs
    public static <U extends PSkill<?>> ArrayList<PSkillData<?>> getEligibleClasses(Class<? extends U>... targetClasses) {
        return EUIUtils.filter(AVAILABLE_SKILLS, d -> EUIUtils.any(targetClasses, targetClass -> targetClass.isAssignableFrom(d.effectClass)));
    }

    public static <U extends PSkill<?>> ArrayList<PSkillData<?>> getEligibleClasses(AbstractCard.CardColor co, Class<U> targetClass) {
        return EUIUtils.filter(AVAILABLE_SKILLS, d -> targetClass.isAssignableFrom(d.effectClass) && d.isColorCompatible(co));
    }

    @SafeVarargs
    public static <U extends PSkill<?>> ArrayList<PSkillData<?>> getEligibleClasses(AbstractCard.CardColor co, Class<? extends U>... targetClasses) {
        return EUIUtils.filter(AVAILABLE_SKILLS, d -> d.isColorCompatible(co) && EUIUtils.any(targetClasses, targetClass -> targetClass.isAssignableFrom(d.effectClass)));
    }

    public static List<PCLCardSelection> getEligibleDestinations(PSkill<?> e) {
        return e != null ? e.getEligibleDestinations() : new ArrayList<>();
    }

    public static <U extends PSkill<?>> ArrayList<U> getEligibleEffects(Class<U> targetClass) {
        return getEligibleEffects(targetClass, getEligibleClasses(targetClass));
    }

    @SafeVarargs
    public static <U extends PSkill<?>> ArrayList<U> getEligibleEffects(Class<U> targetClass, Class<? extends U>... subClasses) {
        return getEligibleEffects(targetClass, getEligibleClasses(subClasses));
    }

    public static <U extends PSkill<?>> ArrayList<U> getEligibleEffects(AbstractCard.CardColor co, Class<U> targetClass) {
        return getEligibleEffects(targetClass, getEligibleClasses(co, targetClass));
    }

    @SafeVarargs
    public static <U extends PSkill<?>> ArrayList<U> getEligibleEffects(AbstractCard.CardColor co, Class<U> targetClass, Class<? extends U>... subClasses) {
        return getEligibleEffects(targetClass, getEligibleClasses(co, subClasses));
    }

    /**
     * Returns a list of all available skills that the current card color can use in the card editor, and that fall under the specified PSkill superclass
     */
    public static <U extends PSkill<?>> ArrayList<U> getEligibleEffects(Class<U> targetClass, List<PSkillData<?>> effects) {
        return EUIUtils.mapAsNonnull(effects, cl -> {
                    Constructor<? extends PSkill<?>> c = EUIUtils.tryGetConstructor(cl.effectClass);
                    if (c != null) {
                        try {
                            return targetClass.cast(c.newInstance());
                        }
                        catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                            EUIUtils.logError(targetClass, "Failed to load effect class for " + cl + ": " + e.getLocalizedMessage());
                        }
                    }
                    else {
                        EUIUtils.logError(targetClass, "Constructor is missing " + cl);
                    }
                    return null;
                })
                .stream()
                .sorted((a, b) -> StringUtils.compareIgnoreCase(a.getSampleText(null, null), b.getSampleText(null, null)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static List<PCLCardSelection> getEligibleOrigins(PSkill<?> e) {
        return e != null ? e.getEligibleOrigins() : new ArrayList<>();
    }

    public static List<PCLCardGroupHelper> getEligiblePiles(PSkill<?> e) {
        return e != null ? e.getEligiblePiles() : new ArrayList<>();
    }

    public static List<PCLCardTarget> getEligibleTargets(PSkill<?> e) {
        return e != null ? e.getEligibleTargets() : new ArrayList<>();
    }

    /**
     * Load the data for all skills annotated with @VisibleSkill for usage in the card editor
     * Note that even though they don't appear in the editor, PMultiBase and PCardPrimary skills still need to be loaded with this method to allow them to be deserialized in the editor
     */
    public static void initialize() {
        for (Class<?> ct : GameUtilities.getClassesWithAnnotation(VisibleSkill.class)) {
            try {
                VisibleSkill a = ct.getAnnotation(VisibleSkill.class);
                PSkillData<?> data = ReflectionHacks.getPrivateStatic(ct, a.data());
                if (!PMultiBase.class.isAssignableFrom(ct)) {
                    AVAILABLE_SKILLS.add(data);
                }
                EUIUtils.logInfoIfDebug(PSkill.class, "Adding skill " + data.ID);
            }
            catch (Exception e) {
                EUIUtils.logError(PSkill.class, "Failed to load skill " + ct.getName() + ": " + e.getLocalizedMessage());
            }
        }
    }

    public static boolean isSkillBlank(PSkill<?> skill) {
        return skill == null || skill.isBlank();
    }

    public static boolean isVerbose() {
        return !PGR.config.abbreviateEffects.get();
    }

    public static <T> String joinData(T[] items, FuncT1<String, T> stringFunction) {
        return joinData(items, stringFunction, SUB_SEPARATOR);
    }

    public static <T> String joinData(T[] items, FuncT1<String, T> stringFunction, String separator) {
        return items.length > 0 ? EUIUtils.joinStringsMap(separator, stringFunction, items) : null;
    }

    public static <T> String joinData(Collection<T> items, FuncT1<String, T> stringFunction) {
        return joinData(items, stringFunction, SUB_SEPARATOR);
    }

    public static <T> String joinData(Collection<T> items, FuncT1<String, T> stringFunction, String separator) {
        return items.size() > 0 ? EUIUtils.joinStringsMapNonnull(separator, stringFunction, items) : null;
    }

    public static <T> String joinDataAsJson(T[] items, FuncT1<String, T> stringFunction) {
        return items.length > 0 ? EUIUtils.serialize(EUIUtils.mapAsNonnull(items, stringFunction), TStringToken.getType()) : null;
    }

    /**
     * Used by multibase effects to join a list of PSkills into a single JSON object for saving in the special field of PSkillSaveData
     */
    public static <T> String joinDataAsJson(Collection<T> items, FuncT1<String, T> stringFunction) {
        return items.size() > 0 ? EUIUtils.serialize(EUIUtils.mapAsNonnull(items, stringFunction), TStringToken.getType()) : null;
    }

    public static String joinEffectDisplayTexts(Collection<? extends PSkill<?>> effects, String delimiter, PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return EUIUtils.joinStrings(delimiter, getEffectDisplayTexts(effects, perspective, requestor, addPeriod));
    }

    /**
     * Creates a conglomerate string consisting of the text of all of the effects in the collection, in order
     */
    public static String joinEffectTexts(Collection<? extends PSkill<?>> effects) {
        return joinEffectTexts(effects, EUIUtils.SPLIT_LINE, PCLCardTarget.Self, null, true);
    }

    public static String joinEffectTexts(Collection<? extends PSkill<?>> effects, boolean addPeriod) {
        return joinEffectTexts(effects, EUIUtils.SPLIT_LINE, PCLCardTarget.Self, null, addPeriod);
    }

    public static String joinEffectTexts(Collection<? extends PSkill<?>> effects, String delimiter, PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return EUIUtils.joinStrings(delimiter, getEffectTexts(effects, perspective, requestor, addPeriod));
    }

    public static String joinEffectTextsWithoutPeriod(Collection<? extends PSkill<?>> effects) {
        return joinEffectTextsWithoutPeriod(effects, ", ", PCLCardTarget.Self, null, true);
    }

    public static String joinEffectTextsWithoutPeriod(Collection<? extends PSkill<?>> effects, boolean addPeriod) {
        return joinEffectTextsWithoutPeriod(effects, ", ", PCLCardTarget.Self, null, addPeriod);
    }

    public static String joinEffectTextsWithoutPeriod(Collection<? extends PSkill<?>> effects, String delimiter, PCLCardTarget perspective, Object requestor, boolean capitalize) {
        return EUIUtils.joinStrings(delimiter, getEffectTextsWithoutPeriod(effects, perspective, requestor, capitalize));
    }

    public static <T extends PField> PSkillData<T> register(Class<? extends PSkill<T>> type, Class<T> fieldType) {
        return register(type, fieldType, 0, DEFAULT_MAX);
    }

    public static <T extends PField> PSkillData<T> register(Class<? extends PSkill<T>> type, Class<T> fieldType, int minAmount, int maxAmount, AbstractCard.CardColor... cardColors) {
        String id = PGR.core.createID(type.getSimpleName());
        PSkillData<T> d = new PSkillData<T>(id, type, fieldType, minAmount, maxAmount, cardColors);
        EFFECT_MAP.put(id, d);
        return d;
    }

    public static <T extends PField> PSkillData<T> register(Class<? extends PSkill<T>> type, Class<T> fieldType, AbstractCard.CardColor... cardColors) {
        return register(type, fieldType, 0, DEFAULT_MAX, cardColors);
    }

    public static String[] split(String source) {
        return split(source, SUB_SEPARATOR);
    }

    public static String[] split(String source, String separator) {
        return EUIUtils.splitString(separator, source);
    }

    public static List<String> splitJson(String source) {
        return EUIUtils.deserialize(source, TStringToken.getType());
    }

    public static String xToRangeString(int start, int input) {
        return start + "-" + input;
    }

    public static String zeroToRangeString(int input) {
        return "0-" + input;
    }

    public PSkill<T> addAmountForCombat(int amount, int limit) {
        this.baseAmount = this.amount = MathUtils.clamp(this.amount + amount, data != null ? data.minAmount : 0, Math.min(limit, data != null ? data.maxAmount : DEFAULT_MAX));
        return this;
    }

    public PSkill<T> multiplyAmountForCombat(int amount, int limit) {
        this.baseAmount = this.amount = MathUtils.clamp((int) (this.amount * amount / 100f), data != null ? data.minAmount : 0, Math.min(limit, data != null ? data.maxAmount : DEFAULT_MAX));
        return this;
    }

    public boolean canPlay(PCLUseInfo info, PSkill<?> triggerSource) {
        return this.childEffect == null || this.childEffect.canPlay(info, triggerSource);
    }

    public TryChooseChoice<PSkill<?>> chooseEffect(PCLUseInfo info, PCLActions order, List<? extends PSkill<?>> effects) {
        return chooseEffect(info.source, info.target, order, effects);
    }

    public TryChooseChoice<PSkill<?>> chooseEffect(AbstractCreature sc, AbstractCreature tc, PCLActions order, List<? extends PSkill<?>> effects) {
        PCLCard choiceCard = EUIUtils.safeCast(source, PCLCard.class);
        PCLCardData cardData;
        if (choiceCard == null) {
            cardData = new PCLDynamicCardData(QuestionMark.DATA, false);

            if (source instanceof AbstractRelic) {
                cardData.strings.NAME = ((AbstractRelic) source).name;
            }
            else if (source instanceof AbstractPotion) {
                cardData.strings.NAME = ((AbstractPotion) source).name;
            }
            else if (source instanceof AbstractBlight) {
                cardData.strings.NAME = ((AbstractBlight) source).name;
            }
            else {
                cardData.strings.NAME = getSourceCreature().name;
            }
        }
        else {
            cardData = choiceCard.cardData;
        }

        return order.tryChooseSkill(cardData, amount, sc, tc, effects);
    }

    public void displayUpgrades(boolean value) {
        displayUpgrades = value;
        setAmountFromCard();
        if (this.childEffect != null) {
            childEffect.displayUpgrades(value);
        }
    }

    public PSkill<T> edit(ActionT1<T> editFunc) {
        editFunc.invoke(fields);
        return this;
    }

    public final boolean evaluateTargets(PCLUseInfo info, FuncT1<Boolean, AbstractCreature> evalFunc) {
        return info != null && target.evaluateTargets(info, evalFunc);
    }

    public PCLUseInfo generateInfo(AbstractCreature target) {
        return generateInfo(getOwnerCreature(), target);
    }

    public PCLUseInfo generateInfo(AbstractCreature source, AbstractCreature target) {
        return CombatManager.playerSystem.generateInfo(EUIUtils.safeCast(source, AbstractCard.class), source, target);
    }

    public final int getAmountBaseFromCard() {
        PCLCardValueSource amountSource = getAmountSource();
        if (this.source instanceof AbstractCard && amountSource != null) {
            switch (amountSource) {
                case Block:
                    return ((AbstractCard) source).baseBlock;
                case Damage:
                    return ((AbstractCard) source).baseDamage;
                case HitCount:
                    return source instanceof EditorCard ? ((EditorCard) source).hitCountBase() : 1;
                case MagicNumber:
                    return ((AbstractCard) source).baseMagicNumber;
                case SecondaryNumber:
                    return ((AbstractCard) source).baseHeal;
                case RightCount:
                    return source instanceof EditorCard ? ((EditorCard) source).rightCountBase() : 1;
                case XValue:
                    return source instanceof EditorCard ? ((EditorCard) source).getXValue() : 0;
            }
        }
        return amount;
    }

    public final int getAmountFromCard() {
        PCLCardValueSource amountSource = getAmountSource();
        if (this.source instanceof AbstractCard) {
            if (amountSource != null) {
                switch (amountSource) {
                    case Block:
                        return ((AbstractCard) source).block;
                    case Damage:
                        return ((AbstractCard) source).damage;
                    case HitCount:
                        return source instanceof EditorCard ? ((EditorCard) source).hitCount() : 1;
                    case MagicNumber:
                        return ((AbstractCard) source).magicNumber;
                    case SecondaryNumber:
                        return ((AbstractCard) source).heal;
                    case RightCount:
                        return source instanceof EditorCard ? ((EditorCard) source).rightCount() : 1;
                    case XValue:
                        return source instanceof EditorCard ? ((EditorCard) source).getXValue() : 0;
                }
            }

            return rootAmount + ((AbstractCard) source).timesUpgraded * getUpgrade();
        }
        if (source instanceof ValueProvider) {
            if (amountSource == PCLCardValueSource.XValue) {
                return ((ValueProvider) source).getXValue();
            }
            return rootAmount + ((ValueProvider) source).timesUpgraded() * getUpgrade();
        }
        return rootAmount;
    }

    /**
     * Effects whose BASE amount is set to 0 target any number of cards
     */
    public String getAmountRawOrAllString() {
        return baseAmount <= 0 ? TEXT.subjects_all : getAmountRawString();
    }

    public final String getAmountRawString() {
        return source instanceof PointerProvider ? EUIUtils.format(BOUND_FORMAT, EFFECT_CHAR + String.valueOf(getCardPointer())) : wrapAmountChild(this, amount);
    }

    public PCLCardValueSource getAmountSource() {
        return PCLCardValueSource.None;
    }

    public EUIKeywordTooltip getAttackTooltip() {
        return source instanceof PCLCard ? ((PCLCard) source).attackType.getTooltip() : PGR.core.tooltips.normalDamage;
    }

    public final int getAttribute(char attributeID) {
        switch (attributeID) {
            case EFFECT_CHAR:
                return amount;
            case XVALUE_CHAR:
                return getXValue();
            case EXTRA_CHAR:
                return extra;
            case EXTRA2_CHAR:
                return extra2;
            case SCOPE_CHAR:
                return scope;
            default:
                return baseAmount;
        }
    }

    public String getAttributeString(char attributeID) {
        switch (attributeID) {
            case EFFECT_CHAR:
                return wrapAmountChild(this, baseAmount);
            case XVALUE_CHAR:
                return getXString();
            case EXTRA_CHAR:
                return wrapExtra(extra);
            case EXTRA2_CHAR:
                return wrapExtra(extra2);
            case SCOPE_CHAR:
                return String.valueOf(scope);
            default:
                return "?";
        }
    }

    public String getCapitalSubText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return capital(getSubText(perspective, requestor), addPeriod);
    }

    public final char getCardPointer() {
        if (source instanceof PointerProvider) {
            return (char) (((PointerProvider) source).getPointers().addAndGetIndex(this) + CHAR_OFFSET);
        }
        return CHAR_OFFSET;
    }

    public final PSkill<?> getChild() {
        return this.childEffect;
    }

    public PCLClickableUse getClickable(ClickableProvider provider) {
        return childEffect != null ? childEffect.getClickable(provider) : null;
    }

    public final ColoredString getColoredAttributeString(char attributeID) {
        switch (attributeID) {
            case EFFECT_CHAR:
                return getColoredValueString();
            case XVALUE_CHAR:
                return getColoredXString();
            case EXTRA_CHAR:
                return getColoredExtraString();
            case SCOPE_CHAR:
                return getColoredScopeString();
            default:
                return new ColoredString("?", Settings.RED_TEXT_COLOR);
        }
    }

    public ColoredString getColoredExtra2String() {
        return getColoredExtra2String(wrapExtra(baseExtra2), wrapExtra(extra2));
    }

    public ColoredString getColoredExtra2String(Object displayBase, Object displayAmount) {
        if (hasParentType(PMod.class)) {
            return new ColoredString(displayBase, (displayUpgrades && getUpgradeExtra2() != 0) ? Settings.GREEN_TEXT_COLOR : Settings.CREAM_COLOR);
        }

        return new ColoredString(displayAmount, (displayUpgrades && getUpgradeExtra2() != 0) || extra2 > baseExtra2 ? Settings.GREEN_TEXT_COLOR : extra2 < baseExtra2 ? Settings.RED_TEXT_COLOR : Settings.CREAM_COLOR);
    }

    public ColoredString getColoredExtraString() {
        return getColoredExtraString(wrapExtra(baseExtra), wrapExtra(extra));
    }

    public ColoredString getColoredExtraString(Object displayBase, Object displayAmount) {
        if (hasParentType(PMod.class)) {
            return new ColoredString(displayBase, (displayUpgrades && getUpgradeExtra() != 0) ? Settings.GREEN_TEXT_COLOR : Settings.CREAM_COLOR);
        }

        return new ColoredString(displayAmount, (displayUpgrades && getUpgradeExtra() != 0) || extra > baseExtra ? Settings.GREEN_TEXT_COLOR : extra < baseExtra ? Settings.RED_TEXT_COLOR : Settings.CREAM_COLOR);
    }

    public ColoredString getColoredScopeString() {
        return getColoredExtraString(baseScope, scope);
    }

    public ColoredString getColoredScopeString(Object displayBase, Object displayAmount) {
        if (hasParentType(PMod.class)) {
            return new ColoredString(displayBase, (displayUpgrades && getUpgradeScope() != 0) ? Settings.GREEN_TEXT_COLOR : Settings.CREAM_COLOR);
        }

        return new ColoredString(displayAmount, (displayUpgrades && getUpgradeScope() != 0) || scope > baseScope ? Settings.GREEN_TEXT_COLOR : scope < baseScope ? Settings.RED_TEXT_COLOR : Settings.CREAM_COLOR);
    }

    public ColoredString getColoredValueString() {
        return getColoredValueString(wrapAmountChild(this, baseAmount), wrapAmountChild(this, amount));
    }

    public ColoredString getColoredValueString(Object displayBase, Object displayAmount) {
        if (hasParentType(PMod.class)) {
            return new ColoredString(displayBase, (displayUpgrades && getUpgrade() != 0) ? Settings.GREEN_TEXT_COLOR : Settings.CREAM_COLOR);
        }

        return new ColoredString(displayAmount, (displayUpgrades && getUpgrade() != 0) || amount > baseAmount ? Settings.GREEN_TEXT_COLOR : amount < baseAmount ? Settings.RED_TEXT_COLOR : Settings.CREAM_COLOR);
    }

    public ColoredString getColoredXString() {
        String text = getXString();

        return new ColoredString(text, Settings.GREEN_TEXT_COLOR);
    }

    public Color getConditionColor() {
        return null;
    }

    public final String getConditionRawString(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return source instanceof PointerProvider ? EUIUtils.format(CONDITION_FORMAT, (addPeriod ? "C" : "c") + getCardPointer()) : getCapitalSubText(perspective, requestor, addPeriod);
    }

    public TupleT2<PSkill<?>, Integer> getEffectAtIndex(int ind) {
        if (ind == 0) {
            return new TupleT2<>(this, ind);
        }
        if (childEffect == null) {
            return new TupleT2<>(null, ind);
        }
        return childEffect.getEffectAtIndex(ind - 1);
    }

    public final List<PCLCardSelection> getEligibleDestinations() {
        return data != null && data.destinations != null ? data.destinations : Arrays.asList(PCLCardSelection.values());
    }

    public final List<PCLCardSelection> getEligibleOrigins() {
        return data != null && data.origins != null ? data.origins : Arrays.asList(PCLCardSelection.values());
    }

    public final List<PCLCardGroupHelper> getEligiblePiles() {
        return data != null && data.groups != null ? data.groups : PCLCardGroupHelper.getStandard();
    }

    public final List<PCLCardTarget> getEligibleTargets() {
        return data != null && data.targets != null ? data.targets : PCLCardTarget.getAll();
    }

    public String getExportText() {
        String baseString = getText();
        if (source instanceof PointerProvider) {
            return ((PointerProvider) source).makeExportString(baseString);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < baseString.length(); i++) {
            char c = baseString.charAt(i);
            switch (c) {
                case '$':
                    StringBuilder sub = new StringBuilder();
                    while (i + 1 < baseString.length()) {
                        i += 1;
                        c = baseString.charAt(i);
                        sub.append(c);
                        if (c == '$') {
                            break;
                        }
                    }
                    sb.append(EUITextHelper.parseLogicString(sub.toString()));
                    break;
                case '{':
                case '}':
                case '[':
                case ']':
                case '|':
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }

        return sb.toString();
    }

    public String getExportValueUpgradeString(char attributeID) {
        int upgrade;
        int base;
        switch (attributeID) {
            case EFFECT_CHAR:
                upgrade = getUpgrade();
                base = baseAmount;
                break;
            case EXTRA_CHAR:
                upgrade = getUpgradeExtra();
                base = baseExtra;
                break;
            case SCOPE_CHAR:
                upgrade = getUpgradeScope();
                base = baseScope;
                break;
            default:
                upgrade = 0;
                base = 0;
        }
        int upSum = base + upgrade;
        return upgrade != 0 ? base + " (" + upSum + ")" : String.valueOf(base);
    }

    public final int getExtra2BaseFromCard() {
        PCLCardValueSource extraSource = getExtra2Source();
        if (this.source instanceof AbstractCard && extraSource != null) {
            switch (extraSource) {
                case Block:
                    return ((AbstractCard) source).baseBlock;
                case Damage:
                    return ((AbstractCard) source).baseDamage;
                case HitCount:
                    return source instanceof EditorCard ? ((EditorCard) source).hitCountBase() : 1;
                case MagicNumber:
                    return ((AbstractCard) source).baseMagicNumber;
                case SecondaryNumber:
                    return ((AbstractCard) source).baseHeal;
                case RightCount:
                    return source instanceof EditorCard ? ((EditorCard) source).rightCountBase() : 1;
                case XValue:
                    return source instanceof EditorCard ? ((EditorCard) source).getXValue() : 0;
            }
        }
        return extra2;
    }

    public final int getExtra2FromCard() {
        PCLCardValueSource extraSource = getExtra2Source();
        if (this.source instanceof AbstractCard) {
            if (extraSource != null) {
                switch (extraSource) {
                    case Block:
                        return ((AbstractCard) source).block;
                    case Damage:
                        return ((AbstractCard) source).damage;
                    case HitCount:
                        return source instanceof EditorCard ? ((EditorCard) source).hitCount() : 1;
                    case MagicNumber:
                        return ((AbstractCard) source).magicNumber;
                    case SecondaryNumber:
                        return ((AbstractCard) source).heal;
                    case RightCount:
                        return source instanceof EditorCard ? ((EditorCard) source).rightCount() : 1;
                    case XValue:
                        return source instanceof EditorCard ? ((EditorCard) source).getXValue() : 0;
                }
            }

            return rootExtra2 + ((AbstractCard) source).timesUpgraded * getUpgradeExtra2();
        }
        if (source instanceof ValueProvider) {
            if (extraSource == PCLCardValueSource.XValue) {
                return ((ValueProvider) source).getXValue();
            }
            return rootExtra2 + ((ValueProvider) source).timesUpgraded() * getUpgradeExtra2();
        }
        return rootExtra2;
    }

    public final String getExtra2RawString() {
        return source instanceof PointerProvider ? EUIUtils.format(BOUND_FORMAT, EXTRA2_CHAR + String.valueOf(getCardPointer())) : wrapExtra(extra2);
    }

    public PCLCardValueSource getExtra2Source() {
        return PCLCardValueSource.None;
    }

    public final int getExtraBaseFromCard() {
        PCLCardValueSource extraSource = getExtraSource();
        if (this.source instanceof AbstractCard && extraSource != null) {
            switch (extraSource) {
                case Block:
                    return ((AbstractCard) source).baseBlock;
                case Damage:
                    return ((AbstractCard) source).baseDamage;
                case HitCount:
                    return source instanceof EditorCard ? ((EditorCard) source).hitCountBase() : 1;
                case MagicNumber:
                    return ((AbstractCard) source).baseMagicNumber;
                case SecondaryNumber:
                    return ((AbstractCard) source).baseHeal;
                case RightCount:
                    return source instanceof EditorCard ? ((EditorCard) source).rightCountBase() : 1;
                case XValue:
                    return source instanceof EditorCard ? ((EditorCard) source).getXValue() : 0;
            }
        }
        return extra;
    }

    public final int getExtraFromCard() {
        PCLCardValueSource extraSource = getExtraSource();
        if (this.source instanceof AbstractCard) {
            if (extraSource != null) {
                switch (extraSource) {
                    case Block:
                        return ((AbstractCard) source).block;
                    case Damage:
                        return ((AbstractCard) source).damage;
                    case HitCount:
                        return source instanceof EditorCard ? ((EditorCard) source).hitCount() : 1;
                    case MagicNumber:
                        return ((AbstractCard) source).magicNumber;
                    case SecondaryNumber:
                        return ((AbstractCard) source).heal;
                    case RightCount:
                        return source instanceof EditorCard ? ((EditorCard) source).rightCount() : 1;
                    case XValue:
                        return source instanceof EditorCard ? ((EditorCard) source).getXValue() : 0;
                }
            }

            return rootExtra + ((AbstractCard) source).timesUpgraded * getUpgradeExtra();
        }
        if (source instanceof ValueProvider) {
            if (extraSource == PCLCardValueSource.XValue) {
                return ((ValueProvider) source).getXValue();
            }
            return rootExtra + ((ValueProvider) source).timesUpgraded() * getUpgradeExtra();
        }
        return rootExtra;
    }

    public final String getExtraRawString() {
        return source instanceof PointerProvider ? EUIUtils.format(BOUND_FORMAT, EXTRA_CHAR + String.valueOf(getCardPointer())) : wrapExtra(extra);
    }

    public PCLCardValueSource getExtraSource() {
        return PCLCardValueSource.None;
    }

    public Color getGlowColor() {
        return childEffect != null ? childEffect.getGlowColor() : null;
    }

    public String getHeaderTextForAmount() {
        return PGR.core.strings.cedit_value;
    }

    public String getHeaderTextForExtra() {
        return PGR.core.strings.cedit_extraValue;
    }

    public String getHeaderTextForExtra2() {
        return PGR.core.strings.cedit_upgrades;
    }

    public String getHeaderTextForScope() {
        return PGR.core.strings.cedit_scope;
    }

    public final PSkill<?> getHighestParent() {
        if (this.parent != null) {
            return this.parent.getHighestParent();
        }
        return this;
    }

    public PCLUseInfo getInfo(AbstractCreature target) {
        return getInfo(getOwnerCreature(), target);
    }

    public PCLUseInfo getInfo(AbstractCreature source, AbstractCreature target) {
        return CombatManager.playerSystem.getInfo(EUIUtils.safeCast(this.source, AbstractCard.class), source, target);
    }

    public final String getInheritedThemString() {
        return parent != null ? (parent.useParent ? parent.getInheritedThemString() : parent.getThemString()) : this.getThemString();
    }

    public final String getInheritedTheyString() {
        return parent != null ? (parent.useParent ? parent.getInheritedTheyString() : parent.getTheyString()) : this.getTheyString();
    }

    public AbstractMonster.Intent getIntent() {
        return childEffect != null ? childEffect.getIntent() : AbstractMonster.Intent.NONE;
    }

    public String getLegacyText() {
        return getLegacyText(getText());
    }

    public String getLegacyText(String baseString) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < baseString.length(); i++) {
            char c = baseString.charAt(i);
            switch (c) {
                case '$':
                    StringBuilder sub = new StringBuilder();
                    while (i + 1 < baseString.length()) {
                        i += 1;
                        c = baseString.charAt(i);
                        sub.append(c);
                        if (c == '$') {
                            break;
                        }
                    }
                    sb.append(EUITextHelper.parseLogicString(sub.toString()));
                    break;
                case '|':
                    sb.append(EUITextHelper.NEWLINE);
                    break;
                case '{':
                case '}':
                case '[':
                case ']':
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }

        return sb.toString();
    }

    public final PSkill<?> getLowestChild() {
        if (this.childEffect != null) {
            return this.childEffect.getLowestChild();
        }
        return this;
    }

    public final int getMaxAmount() {
        return data != null ? data.maxAmount : DEFAULT_MAX;
    }

    public final int getMinAmount() {
        return data != null ? data.minAmount : 0;
    }

    public final String getName() {
        return source instanceof PointerProvider ? ((PointerProvider) source).getName() : "";
    }

    /**
     * If this skill is on a power, get the creature that this power is attached to. Otherwise, acts the same as getSourceCreature
     */
    public AbstractCreature getOwnerCreature() {
        return parent != null ? parent.getOwnerCreature() : getSourceCreature();
    }

    public final PSkill<?> getParent() {
        return parent;
    }

    public String getPowerText(Object requestor) {
        if (source instanceof PointerProvider) {
            return ((PointerProvider) source).makePowerString(getText(requestor));
        }
        return getText(requestor);
    }

    public final String getPowerTextForDisplay(Object requestor) {
        if (overrideDesc != null) {
            return getUncascadedPowerOverride(requestor);
        }
        if (source instanceof PointerProvider) {
            return ((PointerProvider) source).makePowerString(getText(requestor));
        }
        return getText(requestor);
    }

    public final String getPowerTextForTooltip(Object requestor) {
        if (source instanceof PointerProvider) {
            return ((PointerProvider) source).makePowerString(getText(requestor), true);
        }
        return getText(requestor);
    }

    public int getQualifierRange() {
        return fields.getQualiferRange();
    }

    public String getQualifierText(int i) {
        return fields.getQualifierText(i);
    }

    public List<Integer> getQualifiers(PCLUseInfo info, boolean conditionPassed) {
        return fields.getQualifiers(info);
    }

    public final String getRawString(char attributeID) {
        switch (attributeID) {
            case EFFECT_CHAR:
                return getAmountRawString();
            case XVALUE_CHAR:
                return getXRawString();
            case EXTRA_CHAR:
                return getExtraRawString();
            case EXTRA2_CHAR:
                return getExtra2RawString();
            case SCOPE_CHAR:
                return getScopeRawString();
            default:
                return SINGLE_FORMAT;
        }
    }

    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return getSubText(PCLCardTarget.Self, null);
    }

    public final int getScopeBaseFromCard() {
        PCLCardValueSource scopeSource = getScopeSource();
        if (this.source instanceof AbstractCard && scopeSource != null) {
            switch (scopeSource) {
                case Block:
                    return ((AbstractCard) source).baseBlock;
                case Damage:
                    return ((AbstractCard) source).baseDamage;
                case HitCount:
                    return source instanceof EditorCard ? ((EditorCard) source).hitCountBase() : 1;
                case MagicNumber:
                    return ((AbstractCard) source).baseMagicNumber;
                case SecondaryNumber:
                    return ((AbstractCard) source).baseHeal;
                case RightCount:
                    return source instanceof EditorCard ? ((EditorCard) source).rightCountBase() : 1;
                case XValue:
                    return source instanceof EditorCard ? ((EditorCard) source).getXValue() : 0;
            }
        }
        return scope;
    }

    public final int getScopeFromCard() {
        PCLCardValueSource scopeSource = getScopeSource();
        if (this.source instanceof AbstractCard) {
            if (scopeSource != null) {
                switch (scopeSource) {
                    case Block:
                        return ((AbstractCard) source).block;
                    case Damage:
                        return ((AbstractCard) source).damage;
                    case HitCount:
                        return source instanceof EditorCard ? ((EditorCard) source).hitCount() : 1;
                    case MagicNumber:
                        return ((AbstractCard) source).magicNumber;
                    case SecondaryNumber:
                        return ((AbstractCard) source).heal;
                    case RightCount:
                        return source instanceof EditorCard ? ((EditorCard) source).rightCount() : 1;
                    case XValue:
                        return source instanceof EditorCard ? ((EditorCard) source).getXValue() : 0;
                }
            }

            return MathUtils.clamp(rootScope + ((AbstractCard) source).timesUpgraded * getUpgradeScope(), 1, DEFAULT_MAX);
        }
        if (source instanceof ValueProvider) {
            if (scopeSource == PCLCardValueSource.XValue) {
                return ((ValueProvider) source).getXValue();
            }
            return MathUtils.clamp(rootScope + ((ValueProvider) source).timesUpgraded() * getUpgradeScope(), 1, DEFAULT_MAX);
        }
        return rootScope;
    }

    public final String getScopeRawString() {
        return source instanceof PointerProvider ? EUIUtils.format(BOUND_FORMAT, SCOPE_CHAR + String.valueOf(getCardPointer())) : String.valueOf(scope);
    }

    public PCLCardValueSource getScopeSource() {
        return PCLCardValueSource.None;
    }

    /**
     * Get the creature that owns this skill. Defaults to the player if no source was defined
     */
    public AbstractCreature getSourceCreature() {
        return source instanceof ValueProvider ? ((ValueProvider) source).getSourceCreature() : AbstractDungeon.player;
    }

    public String getSpecialData() {
        return null;
    }

    public final PCLCardTarget getTargetForPerspective(PCLCardTarget perspective) {
        return target == PCLCardTarget.Self ? perspective : target;
    }

    public final String getTargetIsString(PCLCardTarget target, String subject) {
        return TEXT.cond_ifX(TEXT.cond_xIsY(getTargetSubjectString(target), getTargetOrdinal(target), subject));
    }

    public final ArrayList<? extends AbstractCreature> getTargetList(PCLUseInfo info) {
        return info != null ? target.getTargets(info, scope) : new ArrayList<>();
    }

    public final String getTargetOnString(PCLCardTarget target, String baseString) {
        switch (target) {
            case Team:
            case AllAlly:
            case RandomAlly:
                return TEXT.subjects_onAnyAlly(baseString);
            case RandomAllyEnemy:
            case AllAllyEnemy:
                return TEXT.subjects_onAnyAllyOrEnemy(baseString);
            case All:
            case Any:
                return TEXT.subjects_onAnyCharacter(baseString);
            case AllEnemy:
            case SelfAllEnemy:
            case RandomEnemy:
                return TEXT.subjects_onAnyEnemy(baseString);
            case SingleAlly:
            case SelfSingleAlly:
            case Single:
            case SelfSingle:
                return TEXT.subjects_onTheEnemy(baseString);
            case UseParent:
                return TEXT.subjects_onTarget(baseString, TEXT.subjects_them(0));
            case Self:
                if (isFromCreature()) {
                    return TEXT.subjects_onThis(baseString);
                }
            default:
                return TEXT.subjects_onYou(baseString);
        }
    }

    public String getTargetOnStringPerspective(PCLCardTarget target, String desc) {
        return getTargetOnString(getTargetForPerspective(target), desc);
    }

    public final int getTargetOrdinal(PCLCardTarget target) {
        return target == PCLCardTarget.Self && !isFromCreature() ? 0 : target.ordinal();
    }

    public final int getTargetOrdinalPerspective(PCLCardTarget perspective) {
        return getTargetOrdinal(getTargetForPerspective(perspective));
    }

    public String getTargetString(PCLCardTarget target) {
        return getTargetString(target, scope);
    }

    public String getTargetString(PCLCardTarget target, int count) {
        switch (target) {
            case All:
                return TEXT.subjects_allX(PCLCoreStrings.pluralForce(TEXT.subjects_characterN));
            case AllAlly:
                return TEXT.subjects_allAllies();
            case AllEnemy:
                return TEXT.subjects_allEnemies();
            case AllAllyEnemy:
                return TEXT.subjects_allAlliesAndEnemies();
            case Any:
                return TEXT.subjects_anyone;
            case RandomAlly:
                return EUIRM.strings.numNoun(count, TEXT.subjects_randomX(PCLCoreStrings.pluralEvaluated(TEXT.subjects_allyN, count)));
            case RandomAllyEnemy:
                return EUIRM.strings.numNoun(count, TEXT.subjects_randomX(TEXT.cond_xOrY(PCLCoreStrings.pluralEvaluated(TEXT.subjects_allyN, count), PCLCoreStrings.pluralEvaluated(TEXT.subjects_enemyN, count))));
            case RandomEnemy:
                return EUIRM.strings.numNoun(count, TEXT.subjects_randomX(PCLCoreStrings.pluralEvaluated(TEXT.subjects_enemyN, count)));
            case Single:
                return count > 1 ? EUIRM.strings.numNoun(count, PCLCoreStrings.pluralEvaluated(TEXT.subjects_enemyN, count)) : TEXT.subjects_target;
            case SingleAlly:
                return count > 1 ? EUIRM.strings.numNoun(count, PCLCoreStrings.pluralEvaluated(TEXT.subjects_allyN, count)) : TEXT.subjects_ally;
            case Team:
                return TEXT.subjects_your(target.getTitle().toLowerCase());
            case SelfAllEnemy:
                if (isFromCreature()) {
                    return PCLCoreStrings.joinWithAnd(TEXT.subjects_this, TEXT.subjects_allEnemies());
                }
                return PCLCoreStrings.joinWithAnd(TEXT.subjects_you, TEXT.subjects_allEnemies());
            case SelfSingle:
                String base = count > 1 ? EUIRM.strings.numNoun(count, PCLCoreStrings.pluralEvaluated(TEXT.subjects_enemyN, count)) : TEXT.subjects_target;
                if (isFromCreature()) {
                    return PCLCoreStrings.joinWithAnd(TEXT.subjects_this, base);
                }
                return PCLCoreStrings.joinWithAnd(TEXT.subjects_you, base);
            case SelfSingleAlly:
                String allyBase = count > 1 ? EUIRM.strings.numNoun(count, PCLCoreStrings.pluralEvaluated(TEXT.subjects_allyN, count)) : TEXT.subjects_ally;
                if (isFromCreature()) {
                    return PCLCoreStrings.joinWithAnd(TEXT.subjects_this, allyBase);
                }
                return PCLCoreStrings.joinWithAnd(TEXT.subjects_you, allyBase);
            case SelfPlayer:
                if (isFromCreature()) {
                    return PCLCoreStrings.joinWithAnd(TEXT.subjects_this, TEXT.subjects_you);
                }
                return TEXT.subjects_you;
            case UseParent:
                return TEXT.subjects_them(parent != null && parent.target.targetsSingle() ? 2 : 1);
            case Self:
                if (isFromCreature()) {
                    return TEXT.subjects_thisCard();
                }
            default:
                return TEXT.subjects_you;
        }
    }

    public String getTargetStringPerspective(PCLCardTarget target) {
        return getTargetString(getTargetForPerspective(target), scope);
    }

    public final String getTargetStringPluralSuffix() {
        return getTargetStringPluralSuffix(target);
    }

    public final String getTargetStringPluralSuffix(PCLCardTarget target) {
        return getTargetStringPluralSuffix(target, amount);
    }

    public final String getTargetStringPluralSuffix(PCLCardTarget target, int amount) {
        switch (target) {
            case AllAlly:
            case RandomAlly:
            case SelfSingleAlly:
            case SingleAlly:
            case Team:
                return PCLCoreStrings.pluralEvaluated(TEXT.subjects_allyN, amount);
            case AllEnemy:
            case RandomEnemy:
            case SelfSingle:
            case Single:
                return PCLCoreStrings.pluralEvaluated(TEXT.subjects_enemyN, amount);
            case AllAllyEnemy:
            case RandomAllyEnemy:
                return PCLCoreStrings.joinWithOr(PCLCoreStrings.pluralEvaluated(TEXT.subjects_allyN, amount), PCLCoreStrings.pluralEvaluated(TEXT.subjects_enemyN, amount));
            default:
                return PCLCoreStrings.pluralEvaluated(TEXT.subjects_characterN, amount);
        }
    }

    public final String getTargetStringSingular() {
        return getTargetStringSingular(target);
    }

    public final String getTargetStringSingular(PCLCardTarget target) {
        switch (target) {
            case AllAlly:
            case RandomAlly:
            case SelfSingleAlly:
            case SingleAlly:
            case Team:
                return TEXT.subjects_ally;
            case AllEnemy:
            case RandomEnemy:
            case SelfSingle:
            case Single:
                return TEXT.subjects_enemy;
            case AllAllyEnemy:
            case RandomAllyEnemy:
                return PCLCoreStrings.joinWithOr(TEXT.subjects_ally, TEXT.subjects_enemy);
            default:
                return TEXT.subjects_character;
        }
    }

    public String getTargetSubjectString(PCLCardTarget target) {
        switch (target) {
            case Single:
                return PGR.core.strings.subjects_target;
            case SingleAlly:
                return PGR.core.strings.subjects_ally;
            case AllEnemy:
                return PGR.core.strings.subjects_allEnemies();
            case RandomAlly:
                return PGR.core.strings.subjects_anyAlly();
            case RandomAllyEnemy:
                return PGR.core.strings.subjects_anyAllyOrEnemy();
            case RandomEnemy:
                return PGR.core.strings.subjects_anyEnemy();
            case AllAlly:
                return PGR.core.strings.subjects_allAllies();
            case AllAllyEnemy:
                return PGR.core.strings.subjects_allAlliesOrEnemies();
            case Team:
                return PGR.core.strings.ctype_team;
            case All:
                return PGR.core.strings.subjects_everyone;
            case Any:
                return PGR.core.strings.subjects_anyone;
            case SelfAllEnemy:
                if (isFromCreature()) {
                    return PCLCoreStrings.joinWithAnd(TEXT.subjects_this, TEXT.subjects_allEnemies());
                }
                return PCLCoreStrings.joinWithAnd(TEXT.subjects_you, TEXT.subjects_allEnemies());
            case SelfSingle:
                if (isFromCreature()) {
                    return PCLCoreStrings.joinWithAnd(TEXT.subjects_this, TEXT.subjects_target);
                }
                return PCLCoreStrings.joinWithAnd(TEXT.subjects_you, TEXT.subjects_target);
            case SelfSingleAlly:
                if (isFromCreature()) {
                    return PCLCoreStrings.joinWithAnd(TEXT.subjects_this, TEXT.subjects_ally);
                }
                return PCLCoreStrings.joinWithAnd(TEXT.subjects_you, TEXT.subjects_ally);
            case SelfPlayer:
                if (isFromCreature()) {
                    return PCLCoreStrings.joinWithAnd(TEXT.subjects_this, TEXT.subjects_you);
                }
                return TEXT.subjects_you;
            case UseParent:
                return TEXT.subjects_they(parent != null && parent.target.targetsSingle() ? 2 : 1);
            case Self:
                if (isFromCreature()) {
                    return TEXT.subjects_thisCard();
                }
            default:
                return PGR.core.strings.subjects_you;
        }
    }

    public String getTargetSubjectStringPerspective(PCLCardTarget target) {
        return getTargetSubjectString(getTargetForPerspective(target));
    }

    public String getText(int index, PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return childEffect != null ? childEffect.getText(index, perspective, requestor, addPeriod) : getText(perspective, requestor, addPeriod);
    }

    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return getCapitalSubText(perspective, requestor, addPeriod) + (childEffect != null && !childEffect.shouldHideText() ? PCLCoreStrings.period(true) + " " + capital(childEffect.getText(perspective, requestor, addPeriod), true) : PCLCoreStrings.period(addPeriod));
    }

    public final String getText() {
        return getText(PCLCardTarget.Self, null, true);
    }

    public final String getText(Object source) {
        return getText(PCLCardTarget.Self, source, true);
    }

    public final String getTextForDisplay() {
        return overrideDesc != null ? getUncascadedOverride(null) : getText();
    }

    public final String getTextForDisplay(Object requestor) {
        return overrideDesc != null ? getUncascadedOverride(requestor) : getText(requestor);
    }

    public final String getTextForDisplay(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return overrideDesc != null ? getUncascadedOverride(requestor) : getText(perspective, requestor, addPeriod);
    }

    public String getThemString() {
        return EUITextHelper.parseLogicString(TEXT.subjects_them(amount));
    }

    public String getTheyString() {
        return EUITextHelper.parseLogicString(TEXT.subjects_they(amount));
    }

    @Override
    public List<EUIKeywordTooltip> getTips() {
        return tips != null ? tips : Collections.emptyList();
    }

    public final UUID getUUID() {
        return uuid;
    }

    public String getUncascadedOverride(Object requestor) {
        return getUncascadedOverride(overrideDesc, requestor);
    }

    public String getUncascadedOverride(String overrideDesc, Object requestor) {
        if (overrideDesc == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < overrideDesc.length(); i++) {
            char c = overrideDesc.charAt(i);
            if (c == CASCADE_CHAR) {
                if (EUIRenderHelpers.isCharAt(overrideDesc, i + 3, CASCADE_CHAR)) {
                    PSkill<?> move = getEffectAtIndex(overrideDesc.charAt(i + 2) - CHAR_OFFSET).v1;
                    if (move != null) {
                        sb.append(move.getRawString(overrideDesc.charAt(i + 1)));
                    }
                    i += 3;
                }
                else if (EUIRenderHelpers.isCharAt(overrideDesc, i + 2, CASCADE_CHAR)) {
                    sb.append(String.valueOf(getUncascadedOverrideValue(overrideDesc.charAt(i + 1), requestor)));
                    i += 2;
                }
            }
            else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    // TODO more override options
    private int getUncascadedOverrideValue(char key, Object requestor) {
        if (source != null) {
            return getUpgradeLevel();
        }
        if (requestor instanceof Integer) {
            return (Integer) requestor;
        }
        return 0;
    }

    public String getUncascadedPowerOverride(Object requestor) {
        return getUncascadedPowerOverride(overrideDesc, requestor);
    }

    public String getUncascadedPowerOverride(String overrideDesc, Object requestor) {
        if (overrideDesc == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < overrideDesc.length(); i++) {
            char c = overrideDesc.charAt(i);
            if (c == CASCADE_CHAR) {
                if (EUIRenderHelpers.isCharAt(overrideDesc, i + 3, CASCADE_CHAR)) {
                    PSkill<?> move = getEffectAtIndex(overrideDesc.charAt(i + 2) - CHAR_OFFSET).v1;
                    if (move != null) {
                        sb.append(move.getAttributeString(overrideDesc.charAt(i + 1)));
                    }
                    i += 3;
                }
                else if (EUIRenderHelpers.isCharAt(overrideDesc, i + 2, CASCADE_CHAR)) {
                    sb.append(String.valueOf(getUncascadedOverrideValue(overrideDesc.charAt(i + 1), requestor)));
                    i += 2;
                }
            }
            else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public final int getUpgrade() {
        if (upgrade == null || upgrade.length == 0) {
            return 0;
        }
        return upgrade[Math.min(getUpgradeForm(), upgrade.length - 1)];
    }

    public final int getUpgradeExtra() {
        if (upgradeExtra == null || upgradeExtra.length == 0) {
            return 0;
        }
        return upgradeExtra[Math.min(getUpgradeForm(), upgradeExtra.length - 1)];
    }

    public final int getUpgradeExtra2() {
        if (upgradeExtra2 == null || upgradeExtra2.length == 0) {
            return 0;
        }
        return upgradeExtra2[Math.min(getUpgradeForm(), upgradeExtra2.length - 1)];
    }


    public final int getUpgradeForm() {
        return source instanceof PointerProvider ? ((PointerProvider) source).getForm() : 0;
    }

    public final int getUpgradeLevel() {
        return source instanceof ValueProvider ? ((ValueProvider) source).timesUpgraded() : source instanceof AbstractCard ? ((AbstractCard) source).timesUpgraded : 0;
    }

    public final int getUpgradeScope() {
        if (upgradeScope == null || upgradeScope.length == 0) {
            return 0;
        }
        return upgradeScope[Math.min(getUpgradeForm(), upgradeScope.length - 1)];
    }

    public final String getXRawString() {
        return source instanceof PointerProvider ? EUIUtils.format(BOUND_FORMAT, XVALUE_CHAR + String.valueOf(getCardPointer())) : "";
    }

    public String getXString() {
        // Do not show the x value for when powers
        if (CombatManager.inBattle() && source instanceof ValueProvider && !hasParentType(PTrigger_When.class)) {
            return " (" + getXValue() + ")";
        }
        return EUIUtils.EMPTY_STRING;
    }

    public int getXValue() {
        return amount;
    }

    // Necessary because we need to pass in class names, which are not reified
    public boolean hasChildType(Class<?> childType) {
        return childType.isInstance(childEffect) || (childEffect != null && childEffect.hasChildType(childType));
    }

    public boolean hasChildWarning() {
        return false;
    }

    // Necessary because we need to pass in class names, which are not reified
    public final boolean hasParentType(Class<?> parentType) {
        return parentType.isInstance(this) || (parent != null && parent.hasParentType(parentType));
    }

    public final boolean hasSameProperties(PSkill<?> other) {
        return other != null && effectID.equals(other.effectID) && fields.equals(other.fields);
    }

    public final boolean hasSameUUID(PSkill<?> other) {
        return other != null && other.uuid.equals(this.uuid);
    }

    protected PSkill<T> initializeFromSaveData(PSkillSaveData saveData) {
        this.effectID = saveData.effectID;
        this.target = PCLCardTarget.valueOf(saveData.target);
        this.rootAmount = this.baseAmount = this.amount = saveData.amount;
        this.rootExtra = this.baseExtra = this.extra = saveData.extra;
        this.rootExtra2 = this.baseExtra2 = this.extra2 = saveData.extra2;
        this.rootScope = this.baseScope = this.scope = saveData.scope;
        this.upgrade = saveData.upgrade;
        this.upgradeExtra = saveData.upgradeExtra;
        this.upgradeExtra2 = saveData.upgradeExtra2;
        this.upgradeScope = saveData.upgradeScope;
        this.fields = EUIUtils.deserialize(saveData.effectData, this.data.fieldType);
        this.fields.skill = this;
        this.useParent = saveData.useParent;

        if (saveData.children != null) {
            this.childEffect = PSkill.get(saveData.children);
            if (this.childEffect != null) {
                this.childEffect.parent = this;
            }
        }
        return this;
    }

    public <U> void invokeCastChildren(Class<U> targetClass, ActionT1<U> onUse) {
        if (targetClass.isInstance(this)) {
            onUse.invoke(targetClass.cast(this));
        }
        if (this.childEffect != null) {
            this.childEffect.invokeCastChildren(targetClass, onUse);
        }
    }

    public boolean isAffectedByMods() {
        return !useParent;
    }

    // Used to determine whether the effect should actually be saved or rendered on the card
    public boolean isBlank() {
        return this.childEffect != null && this.childEffect.isBlank();
    }

    public final boolean isCardColor(AbstractCard.CardColor co) {
        return source instanceof AbstractCard && GameUtilities.getActingCardColor((AbstractCard) source) == co;
    }

    public final boolean isCompatible(AbstractCard.CardColor co) {
        return EFFECT_MAP.get(effectID).isColorCompatible(co);
    }

    // Used to determine whether this effect is detrimental to the owner
    public boolean isDetrimental() {
        return childEffect != null && childEffect.isDetrimental();
    }

    public final boolean isFromCreature() {
        return (source instanceof AbstractCard && ((AbstractCard) source).type == PCLEnum.CardType.SUMMON) || (getSourceCreature() instanceof AbstractMonster);
    }

    /* Determines whether this effect should render cards unobtainable through card generation effects */
    public boolean isMetascaling() {
        return childEffect != null && childEffect.isMetascaling();
    }

    /* Effects that consist only of passive elements (i.e. no conds, modifiers) should be hidden in text, except on augments */
    public boolean isPassiveOnly() {
        return false;
    }

    public final boolean isSelfOnlyTarget(PCLCardTarget perpsective) {
        return (target == PCLCardTarget.None || (target == PCLCardTarget.Self && !isFromCreature() && perpsective == PCLCardTarget.Self));
    }

    /*
        Make a copy of this skill with copies of its properties
        Suppressing rawtype warning because we cannot reify the constructor class any further
    */
    @SuppressWarnings("rawtypes")
    public PSkill<T> makeCopy() {
        PSkill<T> copy = null;
        try {
            Constructor<? extends PSkill> c = EUIUtils.tryGetConstructor(this.getClass());
            if (c != null) {
                copy = c.newInstance();
                makeCopyProperties(copy);
            }
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return copy;
    }

    protected void makeCopyProperties(PSkill<T> copy) {
        if (copy != null) {
            copy.effectID = effectID;
            copy.target = target;
            copy.rootAmount = rootAmount;
            copy.baseAmount = baseAmount;
            copy.amount = amount;
            copy.rootExtra = rootExtra;
            copy.baseExtra = baseExtra;
            copy.extra = extra;
            copy.rootExtra2 = rootExtra2;
            copy.baseExtra2 = baseExtra2;
            copy.extra2 = extra2;
            copy.rootScope = rootScope;
            copy.baseScope = baseScope;
            copy.scope = scope;
            if (upgrade != null) {
                copy.upgrade = upgrade.clone();
            }
            if (upgradeExtra != null) {
                copy.upgradeExtra = upgradeExtra.clone();
            }
            if (upgradeExtra2 != null) {
                copy.upgradeExtra2 = upgradeExtra2.clone();
            }
            if (upgradeScope != null) {
                copy.upgradeScope = upgradeScope.clone();
            }
            copy.fields = (T) fields.makeCopy();
            copy.fields.skill = copy;
            copy.useParent = useParent;
            copy.source = source;
            copy.uuid = uuid;
            copy.overrideDesc = overrideDesc;

            // Copy children
            if (this.childEffect != null) {
                PSkill<?> cEffect = this.childEffect.makeCopy();
                if (cEffect != null) {
                    copy.setChild(cEffect);
                }
            }
        }
    }

    public PSkill<T> makePreviews(RotatingList<EUIPreview> previews) {
        fields.makePreviews(previews);
        if (this.childEffect != null) {
            this.childEffect.makePreviews(previews);
        }
        return this;
    }

    public float modifyBlockFirst(PCLUseInfo info, float amount) {
        return this.childEffect != null ? this.childEffect.modifyBlockFirst(info, amount) : amount;
    }

    public float modifyBlockLast(PCLUseInfo info, float amount) {
        return this.childEffect != null ? this.childEffect.modifyBlockLast(info, amount) : amount;
    }

    public int modifyCost(PCLUseInfo info, int cost) {
        return this.childEffect != null ? this.childEffect.modifyCost(info, cost) : cost;
    }

    public float modifyDamageGiveFirst(PCLUseInfo info, float amount) {
        return this.childEffect != null ? this.childEffect.modifyDamageGiveFirst(info, amount) : amount;
    }

    public float modifyDamageGiveLast(PCLUseInfo info, float amount) {
        return this.childEffect != null ? this.childEffect.modifyDamageGiveLast(info, amount) : amount;
    }

    public float modifyDamageReceiveFirst(PCLUseInfo info, float amount, DamageInfo.DamageType type) {
        return this.childEffect != null ? this.childEffect.modifyDamageReceiveFirst(info, amount, type) : amount;
    }

    public float modifyDamageReceiveLast(PCLUseInfo info, float amount, DamageInfo.DamageType type) {
        return this.childEffect != null ? this.childEffect.modifyDamageReceiveLast(info, amount, type) : amount;
    }

    public float modifyHeal(PCLUseInfo info, float amount) {
        return this.childEffect != null ? this.childEffect.modifyHeal(info, amount) : amount;
    }

    public float modifyHitCount(PCLUseInfo info, float amount) {
        return this.childEffect != null ? this.childEffect.modifyHitCount(info, amount) : amount;
    }

    public float modifyOrbIncoming(PCLUseInfo info, float amount) {
        return this.childEffect != null ? this.childEffect.modifyOrbIncoming(info, amount) : amount;
    }

    public float modifyOrbOutgoing(PCLUseInfo info, float amount) {
        return this.childEffect != null ? this.childEffect.modifyOrbOutgoing(info, amount) : amount;
    }

    public float modifyRightCount(PCLUseInfo info, float amount) {
        return this.childEffect != null ? this.childEffect.modifyRightCount(info, amount) : amount;
    }

    public float modifySkillBonus(PCLUseInfo info, float amount) {
        return this.childEffect != null ? this.childEffect.modifySkillBonus(info, amount) : amount;
    }

    public PSkill<T> onAddToCard(AbstractCard card) {
        if (isMetascaling() && !card.tags.contains(AbstractCard.CardTags.HEALING)) {
            card.tags.add(AbstractCard.CardTags.HEALING);
        }
        if (this.childEffect != null) {
            this.childEffect.onAddToCard(card);
        }
        return this;
    }

    public void onDisplay(AbstractMonster m) {
        if (this.childEffect != null) {
            this.childEffect.onDisplay(m);
        }
    }

    public void onDrag(AbstractMonster m) {
        if (this.childEffect != null) {
            this.childEffect.onDrag(m);
        }
    }

    public PSkill<T> onRemoveFromCard(AbstractCard card) {
        if (this.childEffect != null) {
            this.childEffect.onRemoveFromCard(card);
        }
        return this;
    }

    public void onUpgrade() {
        if (customUpgrade != null) {
            customUpgrade.invoke(this, getUpgradeForm(), getUpgradeLevel());
        }
    }

    public final String plural(EUIKeywordTooltip obj) {
        return PCLCoreStrings.plural(obj, getRawString(EFFECT_CHAR));
    }

    public final String plural(EUIKeywordTooltip obj, char effect) {
        return PCLCoreStrings.plural(obj, getRawString(effect));
    }

    public final String pluralCard() {
        return EUIUtils.format(PGR.core.strings.subjects_cardN, baseAmount);
    }

    public final String pluralCardExtra() {
        return EUIUtils.format(PGR.core.strings.subjects_cardN, baseExtra);
    }

    public void recurse(ActionT1<PSkill<?>> onRecurse) {
        onRecurse.invoke(this);
        if (this.childEffect != null) {
            this.childEffect.recurse(onRecurse);
        }
    }

    public void refresh(PCLUseInfo info, boolean conditionMet, boolean isUsing) {
        if (this.childEffect != null) {
            this.childEffect.refresh(info, conditionMet, isUsing);
        }
    }

    public void registerUseParentBoolean(PCLCustomEffectEditingPane editor) {
        editor.registerBoolean(PGR.core.strings.cedit_useParent, PGR.core.strings.cetut_useParent, v -> useParent = v, useParent);
    }

    public boolean removable() {
        return this.childEffect == null || this.childEffect.removable();
    }

    public float renderIntentIcon(SpriteBatch sb, PCLCardAlly ally, float startY, boolean isPreview) {
        return startY;
    }

    public boolean requiresTarget() {
        return target == PCLCardTarget.Single || (this.childEffect != null && this.childEffect.requiresTarget());
    }

    public void resetUses() {

    }

    public PSkill<T> scanForTips(String source) {
        if (tips == null) {
            tips = new ArrayList<>();
        }
        else {
            tips.clear();
        }
        EUITooltip.scanForTips(source, tips);
        return this;
    }

    public final String serialize() {
        return EUIUtils.serialize(new PSkillSaveData(this), TToken.getType());
    }

    public PSkill<T> setAmount(int amount, int upgrade) {
        this.upgrade = new int[]{upgrade};
        setAmount(amount);
        return this;
    }

    public PSkill<T> setAmount(int amount) {
        this.rootAmount = this.baseAmount = this.amount = MathUtils.clamp(amount, data != null ? data.minAmount : 0, data != null ? data.maxAmount : DEFAULT_MAX);
        return this;
    }

    public PSkill<T> setAmountFromCard() {
        this.amount = getAmountFromCard();
        this.baseAmount = getAmountBaseFromCard();
        this.extra = getExtraFromCard();
        this.baseExtra = getExtraBaseFromCard();
        this.extra2 = getExtra2FromCard();
        this.baseExtra2 = getExtra2BaseFromCard();
        this.scope = getScopeFromCard();
        this.baseScope = getScopeBaseFromCard();
        if (this.childEffect != null) {
            childEffect.setAmountFromCard();
        }
        return this;
    }

    public PSkill<T> setChain(PSkill<?> effect, PSkill<?>... effects) {
        this.childEffect = PSkill.chain(effect, effects);
        this.childEffect.parent = this;
        this.childEffect.setSource(this.source);
        return this;
    }

    public PSkill<T> setChild(PSkill<?> effect) {
        this.childEffect = effect;
        if (effect != null) {
            effect.parent = this;
            effect.setSource(this.source);
        }
        return this;
    }

    public PSkill<T> setChild(PSkill<?>... effects) {
        this.childEffect = new PMultiSkill(effects);
        this.childEffect.parent = this;
        this.childEffect.setSource(this.source);
        return this;
    }

    public PSkill<T> setCustomUpgrade(ActionT3<PSkill<T>, Integer, Integer> customUpgrade) {
        this.customUpgrade = customUpgrade;
        return this;
    }

    public PSkill<T> setExtra(int amount, int upgrade) {
        this.upgradeExtra = new int[]{upgrade};
        setExtra(amount);
        return this;
    }

    public PSkill<T> setExtra(int amount) {
        this.rootExtra = this.baseExtra = this.extra = MathUtils.clamp(amount, data != null ? data.minExtra : DEFAULT_EXTRA_MIN, data != null ? data.maxExtra : DEFAULT_MAX);
        return this;
    }

    public PSkill<T> setExtra2(int amount, int upgrade) {
        this.upgradeExtra2 = new int[]{upgrade};
        setExtra2(amount);
        return this;
    }

    public PSkill<T> setExtra2(int amount) {
        this.rootExtra2 = this.baseExtra2 = this.extra2 = MathUtils.clamp(amount, data != null ? data.minExtra2 : DEFAULT_EXTRA_MIN, data != null ? data.maxExtra2 : DEFAULT_MAX);
        return this;
    }

    public PSkill<T> setScope(int amount, int upgrade) {
        this.upgradeScope = new int[]{upgrade};
        setScope(amount);
        return this;
    }

    public PSkill<T> setScope(int amount) {
        this.rootScope = this.baseScope = this.scope = MathUtils.clamp(amount, 1, DEFAULT_MAX);
        return this;
    }

    public PSkill<T> setSource(Object card) {
        this.source = card;
        this.amount = getAmountFromCard();
        this.baseAmount = getAmountBaseFromCard();
        this.extra = getExtraFromCard();
        this.baseExtra = getExtraBaseFromCard();
        this.extra2 = getExtra2FromCard();
        this.baseExtra2 = getExtra2BaseFromCard();
        this.scope = getScopeFromCard();
        this.baseScope = getScopeBaseFromCard();
        if (this.childEffect != null) {
            this.childEffect.setSource(card);
        }
        return this;
    }

    public final PSkill<T> setTarget(PCLCardTarget target) {
        this.target = target;
        return this;
    }

    public PSkill<T> setTemporaryAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public PSkill<T> setTemporaryExtra(int amount) {
        this.extra = amount;
        return this;
    }

    public PSkill<T> setTemporaryExtra2(int amount) {
        this.extra2 = amount;
        return this;
    }

    public PSkill<T> setUpgrade(int... upgrade) {
        this.upgrade = upgrade;
        return this;
    }

    public PSkill<T> setUpgradeExtra(int... upgrade) {
        this.upgradeExtra = upgrade;
        return this;
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        fields.setupEditor(editor);
    }

    public boolean shouldActAsAll() {
        return baseAmount <= 0 && getUpgrade() <= 0;
    }

    public boolean shouldHideText() {
        return false;
    }

    public boolean shouldOverrideTarget() {
        return false;
    }

    public boolean shouldUseWhenText() {
        return true;
    }

    public PSkill<T> stack(PSkill<?> other) {
        if (rootAmount > 0 && other.rootAmount > 0) {
            setAmount(rootAmount + other.rootAmount);
        }
        if (rootExtra > 0 && other.rootExtra > 0) {
            setExtra(rootExtra + other.rootExtra);
        }
        if (rootExtra2 > 0 && other.rootExtra2 > 0) {
            setExtra(rootExtra2 + other.rootExtra2);
        }
        setAmountFromCard();

        if (this.childEffect != null && other.childEffect != null) {
            this.childEffect.stack(other.childEffect);
        }

        return this;
    }

    public void subscribeChildren() {
        if (this instanceof PCLCombatSubscriber) {
            ((PCLCombatSubscriber) this).subscribeToAll();
        }
        if (this.childEffect != null) {
            this.childEffect.subscribeChildren();
        }
    }

    public final int sumTargets(PCLUseInfo info, FuncT1<Integer, AbstractCreature> evalFunc) {
        return info != null ? EUIUtils.sumInt(getTargetList(info), evalFunc) : 0;
    }

    public void triggerOnAllyDeath(PCLCard c, PCLCardAlly ally) {
        if (this instanceof OnAllyDeathSubscriber) {
            ((OnAllyDeathSubscriber) this).onAllyDeath(c, ally);
        }
        else if (this.childEffect != null) {
            this.childEffect.triggerOnAllyDeath(c, ally);
        }
    }

    public void triggerOnAllySummon(PCLCard c, PCLCardAlly ally) {
        if (this instanceof OnAllySummonSubscriber) {
            ((OnAllySummonSubscriber) this).onAllySummon(ally, c, c);
        }
        else if (this.childEffect != null) {
            this.childEffect.triggerOnAllySummon(c, ally);
        }
    }

    public void triggerOnAllyTrigger(PCLCard c, AbstractCreature target, PCLCardAlly ally, PCLCardAlly caller) {
        if (this instanceof OnAllyTriggerSubscriber) {
            ((OnAllyTriggerSubscriber) this).onAllyTrigger(c, target, ally, caller);
        }
        else if (this.childEffect != null) {
            this.childEffect.triggerOnAllyTrigger(c, target, ally, caller);
        }
    }

    public void triggerOnAllyWithdraw(PCLCard c, PCLCardAlly ally) {
        if (this instanceof OnAllyWithdrawSubscriber) {
            ((OnAllyWithdrawSubscriber) this).onAllyWithdraw(c, ally);
        }
        else if (this.childEffect != null) {
            this.childEffect.triggerOnAllyWithdraw(c, ally);
        }
    }

    public void triggerOnCreate(AbstractCard c, boolean startOfBattle) {
        if (this instanceof OnCardCreatedSubscriber) {
            ((OnCardCreatedSubscriber) this).onCardCreated(c, startOfBattle);
        }
        else if (this.childEffect != null) {
            this.childEffect.triggerOnCreate(c, startOfBattle);
        }
    }

    public void triggerOnCreateGeneric(Object o) {
        if (this.childEffect != null) {
            this.childEffect.triggerOnCreateGeneric(o);
        }
    }

    public void triggerOnDiscard(AbstractCard c) {
        if (this instanceof OnCardDiscardedSubscriber) {
            ((OnCardDiscardedSubscriber) this).onCardDiscarded(c);
        }
        else if (this.childEffect != null) {
            this.childEffect.triggerOnDiscard(c);
        }
    }

    public void triggerOnDraw(AbstractCard c) {
        if (this instanceof OnCardDrawnSubscriber) {
            ((OnCardDrawnSubscriber) this).onCardDrawn(c);
        }
        else if (this.childEffect != null) {
            this.childEffect.triggerOnDraw(c);
        }
    }

    public boolean triggerOnEndOfTurn(boolean isUsing) {
        if (this instanceof OnEndOfTurnFirstSubscriber) {
            ((OnEndOfTurnFirstSubscriber) this).onEndOfTurnFirst(isUsing);
            return true;
        }
        else if (this.childEffect != null) {
            return this.childEffect.triggerOnEndOfTurn(isUsing);
        }
        return false;
    }

    public void triggerOnExhaust(AbstractCard c) {
        if (this instanceof OnCardExhaustedSubscriber) {
            ((OnCardExhaustedSubscriber) this).onCardExhausted(c);
        }
        else if (this.childEffect != null) {
            this.childEffect.triggerOnExhaust(c);
        }
    }

    public void triggerOnFetch(AbstractCard c, CardGroup sourcePile) {
        if (this instanceof OnCardFetchedSubscriber) {
            ((OnCardFetchedSubscriber) this).onCardFetched(c, sourcePile);
        }
        else if (this.childEffect != null) {
            this.childEffect.triggerOnFetch(c, sourcePile);
        }
    }

    public void triggerOnObtain() {
    }

    public void triggerOnOtherCardPlayed(AbstractCard c) {
        if (this instanceof OnCardPlayedSubscriber) {
            ((OnCardPlayedSubscriber) this).onCardPlayed(c);
        }
        else if (this.childEffect != null) {
            this.childEffect.triggerOnOtherCardPlayed(c);
        }
    }

    public void triggerOnPurge(AbstractCard c) {
        if (this instanceof OnCardPurgedSubscriber) {
            ((OnCardPurgedSubscriber) this).onPurge(c);
        }
        else if (this.childEffect != null) {
            this.childEffect.triggerOnPurge(c);
        }
    }

    public void triggerOnRemove(Object o) {
        if (this.childEffect != null) {
            this.childEffect.triggerOnRemove(o);
        }
    }

    public void triggerOnRemoveFromInventory() {
    }

    public void triggerOnReshuffle(AbstractCard c, CardGroup sourcePile) {
        if (this instanceof OnCardReshuffledSubscriber) {
            ((OnCardReshuffledSubscriber) this).onCardReshuffled(c, sourcePile);
        }
        else if (this.childEffect != null) {
            this.childEffect.triggerOnReshuffle(c, sourcePile);
        }
    }

    public void triggerOnRetain(AbstractCard c) {
        if (this instanceof OnCardRetainSubscriber) {
            ((OnCardRetainSubscriber) this).onRetain(c);
        }
        else if (this.childEffect != null) {
            this.childEffect.triggerOnRetain(c);
        }
    }

    public void triggerOnScry(AbstractCard c) {
        if (this instanceof OnCardScrySubscriber) {
            ((OnCardScrySubscriber) this).onScry(c);
        }
        else if (this.childEffect != null) {
            this.childEffect.triggerOnScry(c);
        }
    }

    public void triggerOnShuffle() {
        if (this instanceof OnShuffleSubscriber) {
            ((OnShuffleSubscriber) this).onShuffle(true);
        }
        else if (this.childEffect != null) {
            this.childEffect.triggerOnShuffle();
        }
    }

    public void triggerOnStartOfBattleForRelic() {
    }

    public boolean triggerOnStartOfTurn() {
        if (this instanceof OnStartOfTurnPostDrawSubscriber) {
            ((OnStartOfTurnPostDrawSubscriber) this).onStartOfTurnPostDraw();
            return true;
        }
        else if (this.childEffect != null) {
            return this.childEffect.triggerOnStartOfTurn();
        }
        return false;
    }

    public void triggerOnUpgrade(AbstractCard c) {
        if (this instanceof OnCardUpgradeSubscriber) {
            ((OnCardUpgradeSubscriber) this).onUpgrade(c);
        }
        else if (this.childEffect != null) {
            this.childEffect.triggerOnUpgrade(c);
        }
    }

    public boolean tryPassParent(PSkill<?> source, PCLUseInfo info) {
        return parent == null || parent.tryPassParent(source, info);
    }

    public void unsubscribeChildren() {
        if (this instanceof PCLCombatSubscriber) {
            ((PCLCombatSubscriber) this).unsubscribeFromAll();
        }
        if (this.childEffect != null) {
            this.childEffect.unsubscribeChildren();
        }
    }

    public void use(PCLUseInfo info, PCLActions order) {
        if (this.childEffect != null) {
            this.childEffect.use(info, order);
        }
    }

    public void use(PCLUseInfo info, PCLActions order, boolean shouldPay) {
        use(info, order);
    }

    public void useOutsideOfBattle(PCLUseInfo info) {
        if (this.childEffect != null) {
            this.childEffect.useOutsideOfBattle(info);
        }
    }

    public PSkill<T> useParent(boolean value) {
        this.useParent = value;
        return this;
    }

    public String wrapAmount(int input) {
        return String.valueOf(input);
    }

    public String wrapAmountChild(PSkill<?> source, int input) {
        return wrapAmountChild(source, wrapAmount(input));
    }

    public String wrapAmountChild(PSkill<?> source, String input) {
        return parent != null ? parent.wrapAmountChild(source, input) : input;
    }

    public String wrapExtra(int input) {
        return wrapAmount(input);
    }

    public String wrapExtraChild(PSkill<?> source, String input) {
        return parent != null ? parent.wrapExtraChild(source, input) : input;
    }

    public abstract String getSubText(PCLCardTarget perspective, Object requestor);

    public enum PCLCardValueSource {
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
