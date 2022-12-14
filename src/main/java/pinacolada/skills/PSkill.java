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
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.stances.AbstractStance;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT3;
import extendedui.interfaces.delegates.FuncT0;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.text.EUISmartText;
import extendedui.ui.tooltips.EUICardPreview;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.ColoredString;
import org.apache.commons.lang3.StringUtils;
import pinacolada.augments.PCLAugment;
import pinacolada.cards.base.*;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.interfaces.markers.Hidden;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.interfaces.markers.PointerProvider;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.skills.PMultiSkill;
import pinacolada.skills.skills.base.triggers.PTrigger_Interactable;
import pinacolada.stances.PCLStanceHelper;
import pinacolada.utilities.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import static pinacolada.skills.PCond.CONDITION_PRIORITY;
import static pinacolada.skills.PMod.MODIFIER_PRIORITY;

public abstract class PSkill implements TooltipProvider
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
    public static final int DEFAULT_MAX = Integer.MAX_VALUE;
    public static final int DEFAULT_PRIORITY = 4;
    protected static final String CARD_SEPARATOR = "|";
    protected static final String SUB_SEPARATOR = "<";
    protected static final String BOUND_FORMAT = "¦{0}¦";
    protected static final String CONDITION_FORMAT = "║{0}║";
    protected static final String SINGLE_FORMAT = "1";
    protected static final PCLCoreStrings TEXT = PGR.core.strings;
    public final UUID uuid = UUID.randomUUID();
    public PSkillData data;
    public String effectID;
    public AbstractCard sourceCard;
    public PointerProvider source;
    public ArrayList<AbstractCard.CardRarity> rarities = new ArrayList<>();
    public ArrayList<AbstractCard.CardType> types = new ArrayList<>();
    public ArrayList<AbstractCard> cards = new ArrayList<>();
    public ArrayList<PCLAffinity> affinities = new ArrayList<>();
    public ArrayList<PCLCardGroupHelper> groupTypes = new ArrayList<>();
    public ArrayList<PCLCardTag> tags = new ArrayList<>();
    public ArrayList<PCLOrbHelper> orbs = new ArrayList<>();
    public ArrayList<PCLPowerHelper> powers = new ArrayList<>();
    public ArrayList<PCLStanceHelper> stances = new ArrayList<>();
    public ArrayList<EUITooltip> tips = new ArrayList<>();
    public ArrayList<String> cardIDs = new ArrayList<>();
    public ListSelection<AbstractCard> origin = null;
    public GameActions.ActionOrder order = GameActions.ActionOrder.Bottom;
    public PCLCardTarget target = PCLCardTarget.None;
    public PCLCardValueSource amountSource = PCLCardValueSource.None;
    public PCLCardValueSource extraSource = PCLCardValueSource.None;
    public PSkill parent;
    public ActionT3<PSkill, Integer, Integer>  customUpgrade; // Callback for customizing upgrading properties
    public ArrayList<PCLCardGroupHelper> baseGroupTypes = groupTypes;
    public boolean useParent;
    public boolean alt;
    public boolean alt2;
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
        this.target = PCLCardTarget.valueOf(data.target);
        this.amountSource = PCLCardValueSource.valueOf(data.valueSource);
        this.rootAmount = this.baseAmount = this.amount = data.amount;
        this.rootExtra = this.baseExtra = this.extra = data.extra;
        this.upgrade = data.upgrade;
        this.upgradeExtra = data.upgradeExtra;
        this.alt = data.alt;
        this.alt2 = data.alt2;
        this.useParent = data.useParent;
        this.baseGroupTypes = this.groupTypes = parseCardGroups(data.groupTypes);

        PCLEffectType type = getEffectType(this);
        switch (type)
        {
            case CardGroupFull:
                this.rarities = parseRarities(data.effectData2);
                this.types = parseTypes(data.effectData3);
            case Affinity:
            case CardGroupAffinity:
                this.affinities = parseAffinities(data.effectData);
                break;
            case Card:
                this.cardIDs = EUIUtils.mapAsNonnull(split(data.effectData), s -> s);
                break;
            case Orb:
                this.orbs = EUIUtils.mapAsNonnull(split(data.effectData), PCLOrbHelper::get);
                break;
            case Power:
                this.powers = EUIUtils.mapAsNonnull(split(data.effectData), PCLPowerHelper::get);
                break;
            case Stance:
                this.stances = EUIUtils.mapAsNonnull(split(data.effectData), PCLStanceHelper::get);
                break;
            case Tag:
                this.tags = parseTags(data.effectData);
                break;
            case CardGroupCardType:
                this.types = parseTypes(data.effectData);
            case CardGroupCardRarity:
                this.rarities = parseRarities(data.effectData);
        }

        if (data.children != null)
        {
            this.childEffect = PSkill.get(data.children);
            if (this.childEffect != null)
            {
                this.childEffect.parent = this;
            }
        }
    }

    public PSkill(PSkillData data)
    {
        this(data, PCLCardTarget.None, 1, 0);
    }

    public PSkill(PSkillData data, PCLCardTarget target, int amount, PCLCardGroupHelper... gt)
    {
        this(data, target, amount);
        this.groupTypes.addAll(EUIUtils.filter(gt, Objects::nonNull));
    }

    public PSkill(PSkillData data, PCLCardTarget target, int amount, PCLAffinity... affinities)
    {
        this(data, target, amount);
        this.affinities.addAll(EUIUtils.filter(affinities, Objects::nonNull));
    }

    public PSkill(PSkillData data, PCLCardTarget target, int amount, PCLOrbHelper... orbs)
    {
        this(data, target, amount);
        this.orbs.addAll(EUIUtils.filter(orbs, Objects::nonNull));
    }

    public PSkill(PSkillData data, PCLCardTarget target, int amount, PCLPowerHelper... powers)
    {
        this(data, target, amount);
        this.powers.addAll(EUIUtils.filter(powers, Objects::nonNull));
    }

    public PSkill(PSkillData data, int amount, PCLStanceHelper... stances)
    {
        this(data, PCLCardTarget.Self, amount);
        this.stances.addAll(EUIUtils.filter(stances, Objects::nonNull));
    }

    public PSkill(PSkillData data, PCLStanceHelper... stances)
    {
        this(data, 1, stances);
    }

    public PSkill(PSkillData data, PCLCardTag... tags)
    {
        this(data, 1, tags);
    }

    public PSkill(PSkillData data, PCLCardTarget target, int amount, Collection<String> cards)
    {
        this(data, target, amount);
        this.cardIDs.addAll(EUIUtils.filter(cards, Objects::nonNull));
    }

    public PSkill(PSkillData data, PCLCardTarget target, int amount, String... cards)
    {
        this(data, target, amount);
        this.cardIDs.addAll(EUIUtils.filter(cards, Objects::nonNull));
    }

    public PSkill(PSkillData data, int amount, Collection<String> cards)
    {
        this(data, PCLCardTarget.Self, amount, cards);
    }

    public PSkill(PSkillData data, int amount, String... cards)
    {
        this(data, PCLCardTarget.Self, amount, cards);
    }

    public PSkill(PSkillData data, int amount, PCLCardTag... tags)
    {
        this(data, PCLCardTarget.Self, amount);
        this.tags.addAll(EUIUtils.filter(tags, Objects::nonNull));
    }

    public PSkill(PSkillData data, PCLCardTarget target, int amount, PSkill effect)
    {
        this(data, target, amount);
        setChild(effect);
    }

    public PSkill(PSkillData data, PCLCardTarget target, int amount, PSkill... effect)
    {
        this(data, target, amount);
        setChild(effect);
    }

    public PSkill(PSkillData data, PCLCardTarget target, int amount)
    {
        this(data, target, amount, 0);
    }

    public PSkill(PSkillData data, PCLCardTarget target, int amount, int upgrade)
    {
        this.data = data;
        this.effectID = this.data.ID;
        this.target = target;
        this.rootAmount = this.baseAmount = this.amount = amount;
        this.upgrade = new int[]{upgrade};
    }

    public static String capital(String base, boolean can)
    {
        return can ? StringUtils.capitalize(base) : base;
    }

    public static <T extends PSkill> T chain(T first, PSkill... next)
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

    public static PSkillData getData(PSkill effect)
    {
        return effect != null ? effect.data : null;
    }

    public static int getDefaultPriority(Class<? extends PSkill> targetClass)
    {
        if (PCond.class.isAssignableFrom(targetClass))
        {
            return CONDITION_PRIORITY;
        }
        if (PMod.class.isAssignableFrom(targetClass))
        {
            return MODIFIER_PRIORITY;
        }
        return DEFAULT_PRIORITY;
    }

    public static List<String> getEffectTexts(Collection<? extends PSkill> effects, boolean addPeriod)
    {
        return EUIUtils.mapAsNonnull(effects, e -> e.getText(addPeriod));
    }

    public static PCLEffectType getEffectType(PSkill effect)
    {
        return effect != null ? getData(effect.effectID).effectType : PCLEffectType.General;
    }

    public static PCLEffectType getEffectType(String id)
    {
        return id != null ? getData(id).effectType : PCLEffectType.General;
    }

    public static List<PSkillData> getEligibleClasses(AbstractCard.CardColor co, Integer priority)
    {
        return EUIUtils.filter(getAllClasses(), d -> d.matchesPriority(priority) && d.isColorCompatible(co));
    }

    public static List<PSkill> getEligibleEffects(AbstractCard.CardColor co, Integer priority)
    {
        return EUIUtils.mapAsNonnull(getEligibleClasses(co, priority), cl -> {
            // Do not show composite or hidden effects in the effect editor
            if (PMultiBase.class.isAssignableFrom(cl.effectClass) || Hidden.class.isAssignableFrom(cl.effectClass))
            {
                return null;
            }
            Constructor<? extends PSkill> c = EUIUtils.tryGetConstructor(cl.effectClass);
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

    public static String getGroupString(List<PCLCardGroupHelper> groups)
    {
        return groups.size() >= 3 ? PGR.core.strings.subjects.anyPile : PCLCoreStrings.joinWithOr(EUIUtils.map(groups, g -> g.name.toLowerCase()));
    }

    public static String getTargetString(PCLCardTarget target)
    {
        switch (target)
        {
            case All:
                return TEXT.subjects.allX(PCLCoreStrings.pluralForce(TEXT.subjects.characterN));
            case AllAlly:
                return TEXT.subjects.allX(PCLCoreStrings.pluralForce(TEXT.subjects.allyN));
            case AllEnemy:
                return TEXT.subjects.allX(PCLCoreStrings.pluralForce(TEXT.subjects.enemyN));
            case RandomAlly:
                return TEXT.subjects.randomX(PCLCoreStrings.pluralForce(TEXT.subjects.allyN));
            case RandomEnemy:
                return TEXT.subjects.randomX(PCLCoreStrings.pluralForce(TEXT.subjects.enemyN));
            default:
                return TEXT.subjects.enemyN;
        }
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

    public static PSkillData register(Class<? extends PSkill> type, PCLEffectType effectType)
    {
        return register(type, effectType, getDefaultPriority(type), 0, DEFAULT_MAX);
    }

    public static PSkillData register(Class<? extends PSkill> type, PCLEffectType effectType, AbstractCard.CardColor... cardColors)
    {
        return register(type, effectType, getDefaultPriority(type), 0, DEFAULT_MAX, cardColors);
    }

    public static PSkillData register(Class<? extends PSkill> type, PCLEffectType effectType, int minAmount, int maxAmount)
    {
        return register(type, effectType, getDefaultPriority(type), minAmount, maxAmount);
    }

    public static PSkillData register(Class<? extends PSkill> type, PCLEffectType effectType, int minAmount, int maxAmount, AbstractCard.CardColor... cardColors)
    {
        return register(type, effectType, getDefaultPriority(type), minAmount, maxAmount, cardColors);
    }

    public static PSkillData register(Class<? extends PSkill> type, PCLEffectType effectType, int priority, int minAmount, int maxAmount, AbstractCard.CardColor... cardColors)
    {
        String id = PGR.core.createID(type.getSimpleName());
        PSkillData d = new PSkillData(id, type, effectType, priority, minAmount, maxAmount, cardColors);
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

    public void addAdditionalData(PSkillSaveData data)
    {
    }

    public PSkill addAffinity(PCLAffinity newAf)
    {
        if (newAf != null)
        {
            this.affinities.add(newAf);
        }
        return this;
    }

    public PSkill addAmountForCombat(int amount)
    {
        this.baseAmount = this.amount = MathUtils.clamp(this.amount + amount, data != null ? data.minAmount : 1, data != null ? data.maxAmount : DEFAULT_MAX);
        return this;
    }

    public PSkill addCard(AbstractCard card)
    {
        if (card != null)
        {
            this.cards.add(card);
        }
        return this;
    }

    public PSkill addCardGroup(PCLCardGroupHelper... powers)
    {
        return addCardGroup(Arrays.asList(powers));
    }

    public PSkill addCardGroup(List<PCLCardGroupHelper> powers)
    {
        this.groupTypes.addAll(powers);
        return this;
    }


    public PSkill addExtraForCombat(int amount)
    {
        this.baseExtra = this.extra = MathUtils.clamp(this.amount + amount, data != null ? data.minExtra : -1, data != null ? data.maxExtra : DEFAULT_MAX);
        return this;
    }

    public PSkill addPower(PCLPowerHelper... powers)
    {
        return addPower(Arrays.asList(powers));
    }

    public PSkill addPower(List<PCLPowerHelper> powers)
    {
        this.powers.addAll(powers);
        return this;
    }

    public PSkill addTag(PCLCardTag... powers)
    {
        return addTag(Arrays.asList(powers));
    }

    public PSkill addTag(List<PCLCardTag> powers)
    {
        this.tags.addAll(powers);
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

    public final GameActions getActions()
    {
        switch (order)
        {
            case Top:
                return GameActions.top;
            case Last:
                return GameActions.last;
            case Instant:
                return GameActions.instant;
            case TurnStart:
                return GameActions.turnStart;
            case Delayed:
                return GameActions.delayed;
            case DelayedTop:
                return GameActions.delayedTop;
            case NextCombat:
                return GameActions.nextCombat;
            default:
                return GameActions.bottom;
        }
    }

    public final ArrayList<PCLAffinity> getAffinities()
    {
        return affinities;
    }

    public final String getAffinityAndString()
    {
        return PCLCoreStrings.joinWithAnd(EUIUtils.map(affinities, a -> a.getTooltip().getTitleOrIcon()));
    }

    public final String getAffinityLevelAndString()
    {
        return PCLCoreStrings.joinWithAnd(EUIUtils.map(affinities, a -> a.getLevelTooltip().getTitleOrIcon()));
    }

    public final String getAffinityLevelOrString()
    {
        return PCLCoreStrings.joinWithOr(EUIUtils.map(affinities, a -> a.getLevelTooltip().getTitleOrIcon()));
    }

    public final String getAffinityOrString()
    {
        return PCLCoreStrings.joinWithOr(EUIUtils.map(affinities, a -> a.getTooltip().getTitleOrIcon()));
    }

    public final String getAffinityPowerAndString()
    {
        return PCLCoreStrings.joinWithAnd(EUIUtils.map(affinities, a -> a.getLevelTooltip().getTitleOrIcon()));
    }

    public final String getAffinityPowerOrString()
    {
        return PCLCoreStrings.joinWithOr(EUIUtils.map(affinities, a -> a.getLevelTooltip().getTitleOrIcon()));
    }

    public final String getAffinityPowerString()
    {
        return EUIUtils.joinStrings(" ", EUIUtils.map(affinities, a -> a.getLevelTooltip().getTitleOrIcon()));
    }

    public final String getAffinityString()
    {
        return EUIUtils.joinStrings(" ", EUIUtils.map(affinities, a -> a.getTooltip().getTitleOrIcon()));
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
                        return sourceCard instanceof EditorCard ? ((EditorCard) sourceCard).secondaryValue() : 0;
                    case RightCount:
                        return sourceCard instanceof EditorCard ? ((EditorCard) sourceCard).rightCount() : 1;
                    case Affinity:
                        return getCardAffinityValue();
                    case XValue:
                        return sourceCard instanceof PCLCard ? ((PCLCard) sourceCard).getXValue() : 0;
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

    public int getCardAffinityValue()
    {
        if (sourceCard instanceof EditorCard)
        {
            for (PCLAffinity af : affinities)
            {
                int val = ((EditorCard) sourceCard).getAffinityValue(af);
                if (val > 0)
                {
                    return val;
                }
            }
        }
        return 0;
    }

    public final CardGroup[] getCardGroup()
    {
        if (useParent && cards != null)
        {
            CardGroup g = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard c : cards)
            {
                g.addToBottom(c);
            }
            return new CardGroup[]{g};
        }
        else if (groupTypes.isEmpty() && sourceCard != null)
        {
            CardGroup g = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            g.addToBottom(sourceCard);
            return new CardGroup[]{g};
        }
        else
        {
            return EUIUtils.map(groupTypes, PCLCardGroupHelper::getCardGroup).toArray(new CardGroup[]{});
        }
    }

    public final String getCardNameForID(String cardID)
    {
        if (cardID != null)
        {
            AbstractCard c = CardLibrary.getCard(cardID);
            if (c != null)
            {
                return c.name;
            }
        }
        return "";
    }

    public final String getCardIDAndString()
    {
        return PCLCoreStrings.joinWithAnd(EUIUtils.map(cardIDs, g -> "{" + getCardNameForID(g) + "}"));
    }

    public final String getCardIDOrString()
    {
        return PCLCoreStrings.joinWithOr(EUIUtils.map(cardIDs, g -> "{" + getCardNameForID(g) + "}"));
    }

    public final char getCardPointer()
    {
        if (source != null)
        {
            return (char) (source.getPointers().add(this) + CHAR_OFFSET);
        }
        return CHAR_OFFSET;
    }

    public final ArrayList<AbstractCard.CardRarity> getCardRarities()
    {
        return rarities;
    }

    public final ArrayList<AbstractCard.CardType> getCardTypes()
    {
        return types;
    }

    public final ArrayList<String> getCards()
    {
        return cardIDs;
    }

    public final PSkill getChild()
    {
        return this.childEffect;
    }

    public final <T extends PSkill> T getChild(Class<T> type)
    {
        if (type.isInstance(this))
        {
            return (T) this;
        }
        else if (this.childEffect != null)
        {
            return this.childEffect.getChild(type);
        }
        return null;
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
                        return sourceCard instanceof EditorCard ? ((EditorCard) sourceCard).secondaryValue() : 0;
                    case RightCount:
                        return sourceCard instanceof EditorCard ? ((EditorCard) sourceCard).rightCount() : 1;
                    case Affinity:
                        return getCardAffinityValue();
                    case XValue:
                        return sourceCard instanceof PCLCard ? ((PCLCard) sourceCard).getXValue() : 0;
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

    public final String getExtraRawString()
    {
        return source != null ? EUIUtils.format(BOUND_FORMAT, "G" + getCardPointer()) : wrapExtra(amount);
    }

    public final String getFullCardAndString(Object value)
    {
        return getFullCardXString(this::getAffinityAndString, PCLCoreStrings::joinWithAnd, value);
    }

    public FuncT1<Boolean, AbstractCard> getFullCardFilter()
    {
        return (c -> (affinities.isEmpty() || GameUtilities.hasAnyAffinity(c, affinities))
                && (rarities.isEmpty() || rarities.contains(c.rarity))
                && (tags.isEmpty() || EUIUtils.any(tags, t -> t.has(c)))
                && (types.isEmpty() || types.contains(c.type)));
    }

    public final String getFullCardOrString(Object value)
    {
        return getFullCardXString(this::getAffinityOrString, PCLCoreStrings::joinWithOr, value);
    }

    public String getFullCardString()
    {
        return getFullCardString(getAmountRawString());
    }

    public String getFullCardString(Object value)
    {
        return alt ? TEXT.subjects.randomX(getFullCardOrString(value)) : getFullCardOrString(value);
    }

    public final String getFullCardXString(FuncT0<String> affinityFunc, FuncT1<String, ArrayList<String>> joinFunc, Object value)
    {
        ArrayList<String> stringsToJoin = new ArrayList<>();
        if (!affinities.isEmpty())
        {
            stringsToJoin.add(affinityFunc.invoke());
        }
        if (!tags.isEmpty())
        {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(tags, tag -> tag.getTip().getTitleOrIcon())));
        }
        if (!rarities.isEmpty())
        {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(rarities, type -> EUIGameUtils.textForRarity(type))));
        }
        if (!types.isEmpty())
        {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(types, type -> PCLCoreStrings.plural(GameUtilities.tooltipForType(type), value))));
        }
        else
        {
            stringsToJoin.add(EUIUtils.format(TEXT.subjects.cardN, value));
        }

        return EUIUtils.joinStrings(" ", stringsToJoin);
    }

    public final String getGeneralAffinityAndString()
    {
        return affinities.isEmpty() ? getGeneralAffinityString() : getAffinityAndString();
    }

    public final String getGeneralAffinityOrString()
    {
        return affinities.isEmpty() ? getGeneralAffinityString() : getAffinityOrString();
    }

    public final String getGeneralAffinityString()
    {
        return PGR.core.tooltips.affinityGeneral.getTitleOrIcon();
    }

    public final String getGroupString()
    {
        String base = getGroupString(groupTypes);
        return origin == CardSelection.Top ? TEXT.subjects.topOf(base) : origin == CardSelection.Bottom ? TEXT.subjects.bottomOf(base) : base;
    }

    public final ArrayList<PCLCardGroupHelper> getGroups()
    {
        return groupTypes;
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

    public final String getOrbAndString(Object value)
    {
        return orbs.isEmpty() ? PCLCoreStrings.plural(PGR.core.tooltips.orb, value) : PCLCoreStrings.joinWithAnd(EUIUtils.map(orbs, a -> a.getTooltip().getTitleOrIcon()));
    }

    public final FuncT1<Boolean, AbstractOrb> getOrbFilter()
    {
        return (c -> (orbs.isEmpty() || EUIUtils.any(orbs, orb -> orb.ID.equals(c.ID))));
    }

    public final String getOrbOrString(Object value)
    {
        return orbs.isEmpty() ? PCLCoreStrings.plural(PGR.core.tooltips.orb, value) : PCLCoreStrings.joinWithOr(EUIUtils.map(orbs, a -> a.getTooltip().getTitleOrIcon()));
    }

    public final String getOrbString()
    {
        return EUIUtils.joinStrings(" ", EUIUtils.map(orbs, a -> a.getTooltip().getTitleOrIcon()));
    }

    public final ArrayList<PCLOrbHelper> getOrbs()
    {
        return orbs;
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

    public final String getPowerAndString()
    {
        return PCLCoreStrings.joinWithAnd(EUIUtils.map(powers, a -> a.getTooltip().getTitleOrIcon()));
    }

    public final FuncT1<Boolean, AbstractPower> getPowerFilter()
    {
        return (c -> (powers.isEmpty() || EUIUtils.any(powers, power -> power.ID.equals(c.ID))));
    }

    public final String getPowerOrString()
    {
        return PCLCoreStrings.joinWithOr(EUIUtils.map(powers, a -> a.getTooltip().getTitleOrIcon()));
    }

    public final String getPowerString()
    {
        return EUIUtils.joinStrings(" ", EUIUtils.map(powers, a -> a.getTooltip().getTitleOrIcon()));
    }

    public String getPowerText()
    {
        if (source != null)
        {
            return source.makePowerString(getText(true));
        }
        return getText(true);
    }

    public final ArrayList<PCLPowerHelper> getPowers()
    {
        return powers;
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

    public String getShortCardString()
    {
        return alt ? TEXT.subjects.randomX(pluralCard()) : pluralCard();
    }

    public final extendedui.utilities.TupleT2<PSkill, PSkill> getStackingChild(PSkill other)
    {
        if (hasSameProperties(other))
        {
            if (this.childEffect != null && other.childEffect != null)
            {
                return this.childEffect.getStackingChild(other.childEffect);
            }
            else if (other.childEffect != null)
            {
                return getStackingChild(other.childEffect);
            }
            else if (this.childEffect != null)
            {
                return this.childEffect.getStackingChild(other);
            }
            return new extendedui.utilities.TupleT2<>(this, other);
        }
        return null;
    }

    public final String getStanceString()
    {
        return PCLCoreStrings.joinWithOr(EUIUtils.map(stances, stance -> "{" + (stance.affinity != null ? stance.tooltip.title.replace(stance.affinity.getPowerSymbol(), stance.affinity.getFormattedPowerSymbol()) : stance.tooltip.title) + "}"));
    }

    public final ArrayList<PCLStanceHelper> getStances()
    {
        return stances;
    }

    public abstract String getSubText();

    public final String getTagAndString()
    {
        return tags.isEmpty() ? TEXT.cardEditor.tags : PCLCoreStrings.joinWithAnd(EUIUtils.map(tags, a -> a.getTooltip().getTitleOrIcon()));
    }

    public final String getTagOrString()
    {
        return tags.isEmpty() ? TEXT.cardEditor.tags : PCLCoreStrings.joinWithOr(EUIUtils.map(tags, a -> a.getTooltip().getTitleOrIcon()));
    }

    public final ArrayList<PCLCardTag> getTags()
    {
        return tags;
    }

    public final ArrayList<AbstractCreature> getTargetList(PCLUseInfo info) {return target.getTargets(info.source, info.target);}

    public final String getTargetString()
    {
        return getTargetString(target);
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
        return effectID.equals(other.effectID)
                && powers.equals(other.powers) && affinities.equals(other.affinities) && types.equals(other.types) && groupTypes.equals(other.groupTypes)
                && orbs.equals(other.orbs) && rarities.equals(other.rarities);
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

    public final boolean isTrigger()
    {
        return hasParentType(PTrigger.class) && !hasParentType(PTrigger_Interactable.class) && !(parent != null && parent.hasParentType(PCond.class));
    }

    public PSkill makeCopy()
    {
        PSkill copy = null;
        try
        {
            Constructor<? extends PSkill> c = EUIUtils.tryGetConstructor(this.getClass());
            if (c != null)
            {
                copy = c.newInstance();
            }
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            e.printStackTrace();
        }

        return makeCopyProperties(copy);
    }

    protected PSkill makeCopyProperties(PSkill copy)
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
            copy.alt = alt;
            copy.alt2 = alt2;
            copy.order = order;
            copy.origin = origin;
            copy.useParent = useParent;
            copy.groupTypes.addAll(groupTypes);
            copy.baseGroupTypes = copy.groupTypes;
            copy.rarities.addAll(rarities);
            copy.types.addAll(types);
            copy.affinities.addAll(affinities);
            copy.tags.addAll(tags);
            copy.orbs.addAll(orbs);
            copy.powers.addAll(powers);
            copy.stances.addAll(stances);
            copy.cardIDs.addAll(cardIDs);
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

        return copy;
    }

    protected PCLUseInfo makeInfo(AbstractCreature target)
    {
        return new PCLUseInfo(sourceCard, getSourceCreature(), target);
    }

    public PSkill makePreviews(RotatingList<EUICardPreview> previews)
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

    public PSkill onAddToCard(AbstractCard card)
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

    public PSkill onRemoveFromCard(AbstractCard card)
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

    public PSkill resetTemporaryGroups()
    {
        this.groupTypes = baseGroupTypes;
        return this;
    }

    public final PSkill scanForTips()
    {
        tips.clear();
        EUIGameUtils.scanForTips(getSampleText(), tips);
        return this;
    }

    public final String serialize()
    {
        return EUIUtils.serialize(new PSkillSaveData(this), TToken.getType());
    }

    public PSkill setAffinity(PCLAffinity... affinities)
    {
        return setAffinity(Arrays.asList(affinities));
    }

    public PSkill setAffinity(List<PCLAffinity> affinities)
    {
        this.affinities.clear();
        this.affinities.addAll(affinities);
        return this;
    }

    public PSkill setAlt(boolean value)
    {
        this.alt = value;
        return this;
    }

    public PSkill setAlt2(boolean value)
    {
        this.alt2 = value;
        return this;
    }

    public PSkill setAmount(int amount)
    {
        this.rootAmount = this.baseAmount = this.amount = MathUtils.clamp(amount, data != null ? data.minAmount : 1, data != null ? data.maxAmount : DEFAULT_MAX);
        return this;
    }

    public PSkill setAmount(int amount, int upgrade)
    {
        this.upgrade = new int[]{upgrade};
        setAmount(amount);
        return this;
    }

    public PSkill setAmountFromCard()
    {
        this.baseAmount = this.amount = getAmountFromCard();
        this.baseExtra = this.extra = getExtraFromCard();
        if (this.childEffect != null)
        {
            childEffect.setAmountFromCard();
        }
        return this;
    }

    public PSkill setCardGroup(PCLCardGroupHelper... gt)
    {
        return setCardGroup(Arrays.asList(gt));
    }

    public PSkill setCardGroup(List<PCLCardGroupHelper> gt)
    {
        this.groupTypes.clear();
        this.groupTypes.addAll(gt);
        return this;
    }

    public PSkill setCardIDs(String... cards)
    {
        return setCardIDs(Arrays.asList(cards));
    }

    public PSkill setCardIDs(Collection<String> cards)
    {
        this.cardIDs.clear();
        this.cardIDs.addAll(cards);
        return this;
    }

    public PSkill setCardRarities(AbstractCard.CardRarity... types)
    {
        return setCardRarities(Arrays.asList(types));
    }

    public PSkill setCardRarities(List<AbstractCard.CardRarity> types)
    {
        this.rarities.clear();
        this.rarities.addAll(types);
        return this;
    }

    public PSkill setCardTypes(AbstractCard.CardType... types)
    {
        return setCardTypes(Arrays.asList(types));
    }

    public PSkill setCardTypes(List<AbstractCard.CardType> types)
    {
        this.types.clear();
        this.types.addAll(types);
        return this;
    }

    public PSkill setCards(AbstractCard... cards)
    {
        return setCards(Arrays.asList(cards));
    }

    public PSkill setCards(Collection<? extends AbstractCard> cards)
    {
        this.cards.clear();
        for (AbstractCard card : cards)
        {
            if (card != null)
            {
                this.cards.add(card);
            }
        }
        if (this.childEffect != null)
        {
            this.childEffect.setCards(cards);
        }
        return this;
    }

    public PSkill setChild(PSkill effect)
    {
        this.childEffect = effect;
        if (effect != null)
        {
            effect.parent = this;
        }
        return this;
    }

    public PSkill setChild(PSkill... effects)
    {
        this.childEffect = new PMultiSkill(effects);
        this.childEffect.parent = this;
        return this;
    }

    public PSkill setCustomUpgrade(ActionT3<PSkill, Integer, Integer> customUpgrade)
    {
        this.customUpgrade = customUpgrade;
        return this;
    }

    public PSkill setExtra(int amount)
    {
        this.rootExtra = this.baseExtra = this.extra = MathUtils.clamp(amount, data != null ? data.minExtra : -1, data != null ? data.maxExtra : DEFAULT_MAX);
        return this;
    }

    public PSkill setExtra(int amount, int upgrade)
    {
        this.upgradeExtra = new int[]{upgrade};
        setExtra(amount);
        return this;
    }

    public PSkill setOrb(PCLOrbHelper... orbs)
    {
        return setOrb(Arrays.asList(orbs));
    }

    public PSkill setOrb(List<PCLOrbHelper> orbs)
    {
        this.orbs.clear();
        this.orbs.addAll(orbs);
        return this;
    }

    public PSkill setOrb(PCLOrbHelper newPo)
    {
        if (newPo != null)
        {
            this.orbs.add(newPo);
        }
        return this;
    }

    public final PSkill setOrder(GameActions.ActionOrder order)
    {
        this.order = order;
        return this;
    }

    public final PSkill setOrigin(ListSelection<AbstractCard> origin)
    {
        this.origin = origin;
        return this;
    };

    public PSkill setPower(PCLPowerHelper... powers)
    {
        return setPower(Arrays.asList(powers));
    }

    public PSkill setPower(List<PCLPowerHelper> powers)
    {
        this.powers.clear();
        this.powers.addAll(powers);
        return this;
    }

    public PSkill setSource(PointerProvider card)
    {
        return setSource(card, amountSource);
    }

    public PSkill setSource(PointerProvider card, PCLCardValueSource valueSource)
    {
        return setSource(card, valueSource, extraSource);
    }

    public PSkill setSource(PointerProvider card, PCLCardValueSource valueSource, PCLCardValueSource extraSource)
    {
        this.source = card;
        this.sourceCard = EUIUtils.safeCast(card, AbstractCard.class);
        this.amountSource = valueSource;
        this.extraSource = extraSource;
        this.baseAmount = this.amount = getAmountFromCard();
        this.baseExtra = this.extra = getExtraFromCard();
        if (this.childEffect != null)
        {
            childEffect.setSource(card, valueSource, extraSource);
        }
        return this;
    }

    public PSkill setAmountSource(PCLCardValueSource valueSource)
    {
        this.amountSource = valueSource;
        return this;
    }

    public PSkill setExtraSource(PCLCardValueSource valueSource)
    {
        this.extraSource = valueSource;
        return this;
    }

    public PSkill setStance(PCLStanceHelper... stance)
    {
        return setStance(Arrays.asList(stance));
    }

    public PSkill setStance(List<PCLStanceHelper> stances)
    {
        this.stances.clear();
        this.stances.addAll(stances);
        return this;
    }

    public PSkill setTag(List<PCLCardTag> nt)
    {
        this.tags.clear();
        this.tags.addAll(nt);
        return this;
    }

    public PSkill setTag(PCLCardTag nt)
    {
        if (nt != null)
        {
            this.tags.add(nt);
        }
        return this;
    }

    public final PSkill setTarget(PCLCardTarget target)
    {
        this.target = target;
        return this;
    }

    public PSkill setTemporaryAmount(int amount)
    {
        this.amount = amount;
        return this;
    }

    public PSkill setTemporaryExtra(int amount)
    {
        this.extra = amount;
        return this;
    }

    public PSkill setTemporaryGroups(ArrayList<PCLCardGroupHelper> cardGroups)
    {
        this.groupTypes = cardGroups;
        return this;
    }

    public PSkill setUpgrade(int... upgrade)
    {
        this.upgrade = upgrade;
        return this;
    }

    public PSkill setUpgradeExtra(int... upgrade)
    {
        this.upgradeExtra = upgrade;
        return this;
    }

    public PSkill stack(PSkill other)
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

    public boolean triggerOnPCLPowerUsed(PCLPower c)
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

    public PSkill useParent(boolean value)
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

    // TODO rework to use a list of possible items
    public enum PCLEffectType
    {
        General,
        Affinity,
        Card,
        CardEYB,
        CardGroup,
        CardGroupAffinity,
        CardGroupCardRarity,
        CardGroupCardType,
        CardGroupFull,
        CustomPower,
        Delegate,
        InteractablePower,
        Orb,
        Power,
        Stance,
        Tag,
        Trigger
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
        Affinity,
        XValue,
    }
}
