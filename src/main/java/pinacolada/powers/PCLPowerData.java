package pinacolada.powers;

import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.powers.watcher.*;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.misc.PCLGenericData;
import pinacolada.powers.common.EnergizedPower;
import pinacolada.powers.common.*;
import pinacolada.powers.replacement.PCLCurlUpPower;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonAdapter(PCLPowerData.PCLPowerDataAdapter.class)
public class PCLPowerData extends PCLGenericData<AbstractPower> implements KeywordProvider {
    private static final Map<String, PCLPowerData> STATIC_DATA = new HashMap<>();
    public static final int DEFAULT_POWER_MAX = 9999;
    public static final String ICON_AFTER_IMAGE = "afterImage";
    public static final String ICON_ARTIFACT = "artifact";
    public static final String ICON_BLUR = "blur";
    public static final String ICON_BUFFER = "buffer";
    public static final String ICON_CHOKED = "choke";
    public static final String ICON_CONFUSION = "confusion";
    public static final String ICON_CONSTRICTED = "constricted";
    public static final String ICON_CORPSE_EXPLOSION = "cExplosion";
    public static final String ICON_CURLUP = "closeUp";
    public static final String ICON_DEXTERITY = "dexterity";
    public static final String ICON_DOUBLE_DAMAGE = "doubleDamage";
    public static final String ICON_DUPLICATION = "doubleTap";
    public static final String ICON_ENTANGLE = "entangle";
    public static final String ICON_ENVENOM = "envenom";
    public static final String ICON_EQUILIBRIUM = "retain";
    public static final String ICON_FLIGHT = "flight";
    public static final String ICON_FOCUS = "focus";
    public static final String ICON_FORESIGHT = "wireheading";
    public static final String ICON_FRAIL = "frail";
    public static final String ICON_FREEATTACK = "swivel";
    public static final String ICON_INTANGIBLE = "intangible";
    public static final String ICON_INVINCIBLE = "heartDef";
    public static final String ICON_JUGGERNAUT = "juggernaut";
    public static final String ICON_LOCKON = "lockon";
    public static final String ICON_MALLEABLE = "malleable";
    public static final String ICON_MANTRA = "mantra";
    public static final String ICON_MARKED = "pressure_points";
    public static final String ICON_METALLICIZE = "armor";
    public static final String ICON_NEXT_TURN_BLOCK = "defenseNext";
    public static final String ICON_NEXT_TURN_DRAW = "carddraw";
    public static final String ICON_NEXT_TURN_DRAW_LESS = "lessdraw";
    public static final String ICON_NO_BLOCK = "noBlock";
    public static final String ICON_NO_DRAW = "noDraw";
    public static final String ICON_NOXIOUSFUMES = "fumes";
    public static final String ICON_PLATEDARMOR = "platedarmor";
    public static final String ICON_POISON = "poison";
    public static final String ICON_REBOUND = "rebound";
    public static final String ICON_REGEN = "regen";
    public static final String ICON_RITUAL = "ritual";
    public static final String ICON_SHACKLE = "shackle";
    public static final String ICON_SLOW = "slow";
    public static final String ICON_STRENGTH = "strength";
    public static final String ICON_THORNS = "thorns";
    public static final String ICON_THOUSAND_CUTS = "thousandCuts";
    public static final String ICON_VIGOR = "vigor";
    public static final String ICON_WEAK = "weak";
    public static final String ICON_VULNERABLE = "vulnerable";

    public static final PCLPowerData Choked = registerBaseBuff(ChokePower.class, ChokePower.POWER_ID, PGR.core.tooltips.choked).setImageRegion(ICON_CHOKED).setEndTurnBehavior(Behavior.Permanent);
    public static final PCLPowerData Confused = registerBaseDebuff(ConfusionPower.class, ConfusionPower.POWER_ID, PGR.core.tooltips.confused).setImageRegion(ICON_CONFUSION).setEndTurnBehavior(Behavior.Permanent);
    public static final PCLPowerData Constricted = registerBaseDebuffCommon(ConstrictedPower.class, ConstrictedPower.POWER_ID, PGR.core.tooltips.constricted).setImageRegion(ICON_CONSTRICTED).setEndTurnBehavior(Behavior.Permanent);
    public static final PCLPowerData CorpseExplosion = registerBaseDebuff(CorpseExplosionPower.class, CorpseExplosionPower.POWER_ID, PGR.core.tooltips.corpseExplosion).setImageRegion(ICON_CORPSE_EXPLOSION).setEndTurnBehavior(PCLPowerData.Behavior.Permanent);
    public static final PCLPowerData Entangled = registerBaseDebuff(EntanglePower.class, EntanglePower.POWER_ID, PGR.core.tooltips.entangled).setImageRegion(ICON_ENTANGLE).setEndTurnBehavior(Behavior.SingleTurn);
    public static final PCLPowerData Frail = registerBaseDebuffCommon(FrailPower.class, FrailPower.POWER_ID, PGR.core.tooltips.frail).setImageRegion(ICON_FRAIL).setEndTurnBehavior(Behavior.TurnBased);
    public static final PCLPowerData Mark = registerBaseDebuff(MarkPower.class, MarkPower.POWER_ID, PGR.core.tooltips.mark).setImageRegion(ICON_MARKED).setEndTurnBehavior(Behavior.Permanent);
    public static final PCLPowerData NoBlock = registerBaseDebuff(NoBlockPower.class, NoBlockPower.POWER_ID, PGR.core.tooltips.noBlock).setImageRegion(ICON_NO_BLOCK).setEndTurnBehavior(PCLPowerData.Behavior.TurnBased);
    public static final PCLPowerData NoDraw = registerBaseDebuff(NoDrawPower.class, NoDrawPower.POWER_ID, PGR.core.tooltips.noDraw).setImageRegion(ICON_NO_DRAW).setEndTurnBehavior(PCLPowerData.Behavior.SingleTurn);
    public static final PCLPowerData Poison = registerBaseDebuffCommon(PoisonPower.class, PoisonPower.POWER_ID, PGR.core.tooltips.poison).setImageRegion(ICON_POISON).setEndTurnBehavior(Behavior.TurnBased);
    public static final PCLPowerData Slow = registerBaseDebuff(SlowPower.class, SlowPower.POWER_ID, PGR.core.tooltips.slow).setImageRegion(ICON_SLOW).setEndTurnBehavior(Behavior.Permanent);
    public static final PCLPowerData Vulnerable = registerBaseDebuffCommon(VulnerablePower.class, VulnerablePower.POWER_ID, PGR.core.tooltips.vulnerable).setImageRegion(ICON_VULNERABLE).setEndTurnBehavior(Behavior.TurnBased);
    public static final PCLPowerData Weak = registerBaseDebuffCommon(WeakPower.class, WeakPower.POWER_ID, PGR.core.tooltips.weak).setImageRegion(ICON_WEAK).setEndTurnBehavior(Behavior.TurnBased);

    public static final PCLPowerData AfterImage = registerBaseBuff(AfterImagePower.class, AfterImagePower.POWER_ID, PGR.core.tooltips.afterImage).setImageRegion(ICON_AFTER_IMAGE).setEndTurnBehavior(Behavior.Permanent);
    public static final PCLPowerData Artifact = registerBaseBuffCommon(ArtifactPower.class, ArtifactPower.POWER_ID, PGR.core.tooltips.artifact).setImageRegion(ICON_ARTIFACT).setEndTurnBehavior(Behavior.Permanent);
    public static final PCLPowerData Blur = registerBaseBuffCommon(BlurPower.class, BlurPower.POWER_ID, PGR.core.tooltips.blur).setImageRegion(ICON_BLUR).setEndTurnBehavior(Behavior.TurnBased);
    public static final PCLPowerData Buffer = registerBaseBuff(BufferPower.class, BufferPower.POWER_ID, PGR.core.tooltips.buffer).setImageRegion(ICON_BUFFER).setEndTurnBehavior(Behavior.Permanent);
    public static final PCLPowerData CurlUp = registerBaseBuff(PCLCurlUpPower.class, PCLCurlUpPower.POWER_ID, PGR.core.tooltips.curlUp).setImageRegion(ICON_CURLUP).setEndTurnBehavior(PCLPowerData.Behavior.Permanent);
    public static final PCLPowerData Dexterity = registerBaseBuffCommon(DexterityPower.class, DexterityPower.POWER_ID, PGR.core.tooltips.dexterity).setImageRegion(ICON_DEXTERITY).setEndTurnBehavior(Behavior.Permanent);
    public static final PCLPowerData DoubleDamage = registerBaseBuff(DoubleDamagePower.class, DoubleDamagePower.POWER_ID, PGR.core.tooltips.doubleDamage).setImageRegion(ICON_DOUBLE_DAMAGE).setEndTurnBehavior(PCLPowerData.Behavior.TurnBased);
    public static final PCLPowerData Duplication = registerBaseBuff(DuplicationPower.class, DuplicationPower.POWER_ID, PGR.core.tooltips.duplication).setImageRegion(ICON_DUPLICATION).setEndTurnBehavior(Behavior.Permanent);
    public static final PCLPowerData Envenom = registerBaseBuff(EnvenomPower.class, EnvenomPower.POWER_ID, PGR.core.tooltips.envenom).setImageRegion(ICON_ENVENOM).setEndTurnBehavior(Behavior.Permanent);
    public static final PCLPowerData Equilibrium = registerBaseBuff(EquilibriumPower.class, EquilibriumPower.POWER_ID, PGR.core.tooltips.equilibrium).setImageRegion(ICON_EQUILIBRIUM).setEndTurnBehavior(Behavior.TurnBased);
    public static final PCLPowerData Flight = registerBaseBuff(FlightPower.class, FlightPower.POWER_ID, PGR.core.tooltips.flight).setImageRegion(ICON_FLIGHT).setEndTurnBehavior(Behavior.Permanent);
    public static final PCLPowerData Focus = registerBaseBuffCommon(FocusPower.class, FocusPower.POWER_ID, PGR.core.tooltips.focus).setImageRegion(ICON_FOCUS).setEndTurnBehavior(Behavior.Permanent);
    public static final PCLPowerData Foresight = registerBaseBuff(ForesightPower.class, ForesightPower.POWER_ID, PGR.core.tooltips.foresight).setImageRegion(ICON_FORESIGHT).setEndTurnBehavior(Behavior.Permanent);
    public static final PCLPowerData FreeAttack = registerBaseBuff(FreeAttackPower.class, FreeAttackPower.POWER_ID, PGR.core.tooltips.freeAttack).setImageRegion(ICON_FREEATTACK).setEndTurnBehavior(PCLPowerData.Behavior.Permanent);
    public static final PCLPowerData Intangible = registerBaseBuff(IntangiblePlayerPower.class, IntangiblePlayerPower.POWER_ID, PGR.core.tooltips.intangible).setImageRegion(ICON_INTANGIBLE).setEndTurnBehavior(Behavior.TurnBased)
            .setEquivalents(IntangiblePower.POWER_ID);
    public static final PCLPowerData Invincible = registerBaseBuff(InvinciblePower.class, InvinciblePower.POWER_ID, PGR.core.tooltips.invincible).setImageRegion(ICON_INVINCIBLE).setEndTurnBehavior(Behavior.Permanent);
    public static final PCLPowerData Juggernaut = registerBaseBuff(JuggernautPower.class, JuggernautPower.POWER_ID, PGR.core.tooltips.juggernaut).setImageRegion(ICON_JUGGERNAUT).setEndTurnBehavior(Behavior.Permanent);
    public static final PCLPowerData Malleable = registerBaseBuff(MalleablePower.class, MalleablePower.POWER_ID, PGR.core.tooltips.malleable).setImageRegion(ICON_MALLEABLE).setEndTurnBehavior(Behavior.Permanent);
    public static final PCLPowerData Mantra = registerBaseBuff(MantraPower.class, MantraPower.POWER_ID, PGR.core.tooltips.mantra).setImageRegion(ICON_MANTRA).setEndTurnBehavior(Behavior.Permanent);
    public static final PCLPowerData Metallicize = registerBaseBuffCommon(MetallicizePower.class, MetallicizePower.POWER_ID, PGR.core.tooltips.metallicize).setImageRegion(ICON_METALLICIZE).setEndTurnBehavior(Behavior.Permanent);
    public static final PCLPowerData NextTurnBlock = registerBaseBuffCommon(NextTurnBlockPower.class, NextTurnBlockPower.POWER_ID, PGR.core.tooltips.nextTurnBlock).setImageRegion(ICON_NEXT_TURN_BLOCK).setEndTurnBehavior(PCLPowerData.Behavior.SingleTurn);
    public static final PCLPowerData NextTurnDraw = registerBaseBuffCommon(DrawCardNextTurnPower.class, DrawCardNextTurnPower.POWER_ID, PGR.core.tooltips.nextTurnDraw).setImageRegion(ICON_NEXT_TURN_DRAW).setEndTurnBehavior(PCLPowerData.Behavior.SingleTurn);
    public static final PCLPowerData NoxiousFumes = registerBaseBuff(NoxiousFumesPower.class, NoxiousFumesPower.POWER_ID, PGR.core.tooltips.noxiousFumes).setImageRegion(ICON_NOXIOUSFUMES).setEndTurnBehavior(PCLPowerData.Behavior.Permanent);
    public static final PCLPowerData PlatedArmor = registerBaseBuffCommon(PlatedArmorPower.class, PlatedArmorPower.POWER_ID, PGR.core.tooltips.platedArmor).setImageRegion(ICON_PLATEDARMOR).setEndTurnBehavior(PCLPowerData.Behavior.Permanent);
    public static final PCLPowerData Rebound = registerBaseBuff(ReboundPower.class, ReboundPower.POWER_ID, PGR.core.tooltips.rebound).setImageRegion(ICON_REBOUND).setEndTurnBehavior(Behavior.Permanent);
    public static final PCLPowerData Regen = registerBaseBuff(RegenPower.class, RegenPower.POWER_ID, PGR.core.tooltips.regeneration).setImageRegion(ICON_REGEN).setEndTurnBehavior(Behavior.TurnBased).setIsMetascaling(true);
    public static final PCLPowerData Ritual = registerBaseBuffCommon(RitualPower.class, RitualPower.POWER_ID, PGR.core.tooltips.ritual).setImageRegion(ICON_RITUAL).setEndTurnBehavior(Behavior.Permanent);
    public static final PCLPowerData Strength = registerBaseBuffCommon(StrengthPower.class, StrengthPower.POWER_ID, PGR.core.tooltips.strength).setImageRegion(ICON_STRENGTH).setEndTurnBehavior(Behavior.Permanent);
    public static final PCLPowerData Thorns = registerBaseBuffCommon(ThornsPower.class, ThornsPower.POWER_ID, PGR.core.tooltips.thorns).setImageRegion(ICON_THORNS).setEndTurnBehavior(Behavior.Permanent);
    public static final PCLPowerData ThousandCuts = registerBaseBuff(ThousandCutsPower.class, ThousandCutsPower.POWER_ID, PGR.core.tooltips.thousandCuts).setImageRegion(ICON_THOUSAND_CUTS).setEndTurnBehavior(PCLPowerData.Behavior.Permanent);
    public static final PCLPowerData Vigor = registerBaseBuffCommon(VigorPower.class, VigorPower.POWER_ID, PGR.core.tooltips.vigor).setImageRegion(ICON_VIGOR).setEndTurnBehavior(Behavior.Permanent);

    public static final PCLPowerData Blinded = BlindedPower.DATA;
    public static final PCLPowerData Bruised = BruisedPower.DATA;
    public static final PCLPowerData Critical = CriticalPower.DATA;
    public static final PCLPowerData Deflection = DeflectionPower.DATA;
    public static final PCLPowerData DelayedDamage = DelayedDamagePower.DATA;
    public static final PCLPowerData DrawMinus = DrawMinusPower.DATA;
    public static final PCLPowerData Energized = EnergizedPower.DATA;
    public static final PCLPowerData Fortified = FortifiedPower.DATA;
    public static final PCLPowerData Impaired = ImpairedPower.DATA;
    public static final PCLPowerData Innovation = InnovationPower.DATA;
    public static final PCLPowerData Invigorated = InvigoratedPower.DATA;
    public static final PCLPowerData Provoked = ProvokedPower.DATA;
    public static final PCLPowerData Resistance = ResistancePower.DATA;
    public static final PCLPowerData SelfImmolation = SelfImmolationPower.DATA;
    public static final PCLPowerData Shackles = ShacklesPower.DATA;
    public static final PCLPowerData Silenced = SilencedPower.DATA;
    public static final PCLPowerData Sorcery = SorceryPower.DATA;
    public static final PCLPowerData Supercharged = SuperchargedPower.DATA;
    public static final PCLPowerData Toxicology = ToxicologyPower.DATA;
    public static final PCLPowerData Vitality = VitalityPower.DATA;
    public static final PCLPowerData Warding = WardingPower.DATA;

    protected String[] equivalents;
    public AbstractPower.PowerType type = NeutralPowertypePatch.NEUTRAL;
    public EUIKeywordTooltip tooltip;
    public PowerStrings strings;
    public Behavior endTurnBehavior = Behavior.Permanent;
    public boolean isCommon;
    public boolean isMetascaling;
    public boolean isPostActionPower;
    public boolean useRegionImage;
    public int maxAmount = DEFAULT_POWER_MAX;
    public int minAmount = 0;
    public int priority = 5;
    public int turns = 1;

    public PCLPowerData(Class<? extends AbstractPower> invokeClass, PCLResources<?, ?, ?, ?> resources) {
        this(invokeClass, resources, resources.createID(invokeClass.getSimpleName()));
    }

    public PCLPowerData(Class<? extends AbstractPower> invokeClass, PCLResources<?, ?, ?, ?> resources, EUIKeywordTooltip tip) {
        this(invokeClass, resources);
        this.tooltip = tip;
    }

    public PCLPowerData(Class<? extends AbstractPower> invokeClass, PCLResources<?, ?, ?, ?> resources, String cardID) {
        this(invokeClass, resources, cardID, PGR.getPowerStrings(cardID));
    }

    public PCLPowerData(Class<? extends AbstractPower> invokeClass, PCLResources<?, ?, ?, ?> resources, String cardID, EUIKeywordTooltip tip) {
        this(invokeClass, resources, cardID);
        this.tooltip = tip;
    }

    public PCLPowerData(Class<? extends AbstractPower> invokeClass, PCLResources<?, ?, ?, ?> resources, String cardID, PowerStrings strings) {
        super(cardID, invokeClass, resources);
        this.strings = strings != null ? strings : new PowerStrings();
        this.initializeImage();
    }

    public PCLPowerData(Class<? extends AbstractPower> invokeClass, PCLResources<?, ?, ?, ?> resources, String cardID, PowerStrings strings, EUIKeywordTooltip tip) {
        this(invokeClass, resources, cardID, strings);
        this.tooltip = tip;
    }

    public static Collection<PCLPowerData> getAllData() {
        return getAllData(true, null);
    }

    public static List<PCLPowerData> getAllData(boolean sort, FuncT1<Boolean, PCLPowerData> filterFunc) {
        Stream<PCLPowerData> stream = STATIC_DATA
                .values()
                .stream();
        if (filterFunc != null) {
            stream = stream.filter(filterFunc::invoke);
        }
        if (sort) {
            stream = stream.sorted((a, b) -> StringUtils.compare(a.strings.NAME, b.strings.NAME));
        }
        return stream
                .distinct()
                .collect(Collectors.toList());
    }

    public static PCLPowerData getRandom() {
        return getRandom(null);
    }

    public static PCLPowerData getRandom(FuncT1<Boolean, PCLPowerData> filterFunc) {
        List<PCLPowerData> powers = getAllData(false, filterFunc);
        return GameUtilities.getRandomElement(powers);
    }

    public static PCLPowerData getStaticData(String cardID) {
        return STATIC_DATA.get(cardID);
    }

    public static PCLPowerData getStaticDataOrCustom(String key) {
        PCLPowerData data = getStaticData(key);
        if (data != null) {
            return data;
        }
        PCLCustomPowerSlot slot = PCLCustomPowerSlot.get(key);
        if (slot != null) {
            return slot.getBuilder(0);
        }
        return null;
    }

    public static boolean isBuff(String key) {
        PCLPowerData data = getStaticDataOrCustom(key);
        return data != null && data.isBuff();
    }

    public static boolean isDebuff(String key) {
        PCLPowerData data = getStaticDataOrCustom(key);
        return data != null && data.isDebuff();
    }

    public static boolean isMetascaling(String key) {
        PCLPowerData data = getStaticDataOrCustom(key);
        return data != null && data.isMetascaling;
    }

    public static void loadIconsIntoKeywords() {
        for (PCLPowerData data : STATIC_DATA.values()) {
            data.loadImageIntoTooltip();
        }
    }

    public static PCLPowerData registerBaseBuff(Class<? extends AbstractPower> powerClass, String id, EUIKeywordTooltip tip) {
        return registerData(new PCLPowerData(powerClass, PGR.core, id, tip));
    }

    public static PCLPowerData registerBaseBuffCommon(Class<? extends AbstractPower> powerClass, String id, EUIKeywordTooltip tip) {
        return registerBaseBuff(powerClass, id, tip).setIsCommon(true);
    }

    public static PCLPowerData registerBaseDebuff(Class<? extends AbstractPower> powerClass, String id, EUIKeywordTooltip tip) {
        return registerData(new PCLPowerData(powerClass, PGR.core, id, tip)).setType(AbstractPower.PowerType.DEBUFF);
    }

    public static PCLPowerData registerBaseDebuffCommon(Class<? extends AbstractPower> powerClass, String id, EUIKeywordTooltip tip) {
        return registerBaseDebuff(powerClass, id, tip).setIsCommon(true);
    }

    // Should only be used by equivalents
    private static <T extends PCLPowerData> T registerData(String id, T cardData) {
        STATIC_DATA.put(id, cardData);
        return cardData;
    }

    protected static <T extends PCLPowerData> T registerData(T cardData) {
        STATIC_DATA.put(cardData.ID, cardData);
        return cardData;
    }

    protected static <T extends PCLPowerData> T registerPCLData(T cardData) {
        registerData(cardData);
        cardData.initializeImage();
        return cardData;
    }

    public AbstractPower create(AbstractCreature owner, AbstractCreature source, int amount) {
        try {
            if (constructor == null) {
                constructor = createConstructor(AbstractCreature.class, AbstractCreature.class, int.class);
                if (constructor == null) {
                    constructor = createConstructor(AbstractCreature.class, int.class);
                }
                if (constructor == null) {
                    constructor = createConstructor(AbstractCreature.class, int.class, boolean.class);
                }
                if (constructor == null) {
                    constructor = createConstructor(AbstractCreature.class);
                }
                if (constructor == null) {
                    constructor = invokeClass.getConstructor();
                }
                constructor.setAccessible(true);
            }

            switch (constructor.getParameterCount()) {
                case 3:
                    if (constructor.getParameterTypes()[2].equals(boolean.class)) {
                        return constructor.newInstance(owner, amount, GameUtilities.isMonster(source));
                    }
                    return constructor.newInstance(owner, source, amount);
                case 2:
                    return constructor.newInstance(owner, amount);
                case 1:
                    return constructor.newInstance(owner);
                default:
                    return constructor.newInstance();
            }

        }
        catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | NullPointerException e) {
            throw new RuntimeException(ID, e);
        }
    }

    private Constructor<? extends AbstractPower> createConstructor(Class<?>... paramtypes) {
        try {
            return invokeClass.getDeclaredConstructor(paramtypes);
        }
        catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    public TemporaryPower createTemporary(AbstractCreature owner, AbstractCreature source, int amount) {
        AbstractPower po = create(owner, source, amount);
        return new TemporaryPower(owner, po);
    }

    public void doFor(ActionT1<String> func) {
        func.invoke(ID);
        if (equivalents != null) {
            for (String e : equivalents) {
                func.invoke(e);
            }
        }
    }

    public String getName() {
        return strings.NAME;
    }

    public String getText() {
        return tooltip != null ? tooltip.description : strings.DESCRIPTIONS.length > 0 ? strings.DESCRIPTIONS[0] : EUIUtils.EMPTY_STRING;
    }

    @Override
    public List<EUIKeywordTooltip> getTips() {
        return Collections.singletonList(tooltip);
    }

    @Override
    public EUIKeywordTooltip getTooltip() {
        return tooltip;
    }

    public boolean ifAny(FuncT1<Boolean, String> ifFunc) {
        boolean val = ifFunc.invoke(ID);
        return val ? val : equivalents != null && EUIUtils.any(equivalents, ifFunc);
    }

    public void initializeImage() {
        this.imagePath = PGR.getPowerImage(ID);
    }

    public boolean isBuff() {
        return type == AbstractPower.PowerType.BUFF;
    }

    public boolean isDebuff() {
        return type == AbstractPower.PowerType.DEBUFF;
    }

    public void loadImageIntoTooltip() {
        if (tooltip != null && tooltip.icon == null) {
            if (useRegionImage) {
                tooltip.setIconFromPowerRegion(imagePath);
            }
            else {
                tooltip.setIconFromPath(imagePath);
            }
        }
    }

    public boolean matches(AbstractPower po) {
        return matches(po.ID);
    }

    public boolean matches(String powerID) {
        return ID.equals(powerID) || (equivalents != null && EUIUtils.any(equivalents, e -> e.equals(powerID)));
    }

    public PCLPowerData setEndTurnBehavior(Behavior val) {
        this.endTurnBehavior = val;

        return this;
    }

    public PCLPowerData setEquivalents(String... powers) {
        equivalents = powers;
        for (String po : powers) {
            registerData(po, this);
        }
        return this;
    }

    public PCLPowerData setImagePath(String imagePath) {
        this.imagePath = imagePath;

        return this;
    }

    public PCLPowerData setImageRegion(String imagePath) {
        this.imagePath = imagePath;
        this.useRegionImage = true;

        return this;
    }

    public PCLPowerData setIsCommon(boolean val) {
        this.isCommon = val;

        return this;
    }

    public PCLPowerData setIsMetascaling(boolean val) {
        this.isMetascaling = val;

        return this;
    }

    public PCLPowerData setIsPostActionPower(boolean val) {
        this.isPostActionPower = val;

        return this;
    }

    public PCLPowerData setLimits(int min, int max) {
        this.minAmount = min;
        this.maxAmount = Math.max(min, max);

        return this;
    }

    public PCLPowerData setPriority(int priority) {
        this.priority = priority;

        return this;
    }

    public PCLPowerData setTooltip(EUIKeywordTooltip tooltip) {
        this.tooltip = tooltip;

        return this;
    }

    public PCLPowerData setTurns(int turns) {
        this.turns = turns;

        return this;
    }

    public PCLPowerData setType(AbstractPower.PowerType val) {
        this.type = val;

        return this;
    }

    public int sum(FuncT1<Integer, String> ifFunc) {
        int val = ifFunc.invoke(ID);
        if (equivalents != null) {
            val += EUIUtils.sumInt(equivalents, ifFunc);
        }
        return val;
    }

    public enum Behavior implements TooltipProvider {
        Permanent,
        TurnBased,
        TurnBasedNext,
        SingleTurn,
        SingleTurnNext,
        Plated,
        Special;

        private EUITooltip tip;

        public String getAddendum(int turns) {
            switch (this) {
                case SingleTurn:
                case SingleTurnNext:
                    return EUIUtils.format(PGR.core.strings.power_lastsForX, turns);
                case Permanent:
                case Special:
                    return null;
            }
            return getDesc();
        }

        public String getDesc() {
            switch (this) {
                case Permanent:
                    return PGR.core.strings.power_permanentDesc;
                case TurnBased:
                    return PGR.core.strings.power_turnBasedDesc;
                case TurnBasedNext:
                    return PGR.core.strings.power_turnBasedNextDesc;
                case SingleTurn:
                    return PGR.core.strings.power_singleTurnDesc;
                case SingleTurnNext:
                    return PGR.core.strings.power_singleTurnNextDesc;
                case Plated:
                    return PGR.core.strings.power_platedDesc;
            }
            return EUIUtils.EMPTY_STRING;
        }

        public String getText() {
            switch (this) {
                case Permanent:
                    return PGR.core.strings.power_permanent;
                case TurnBased:
                    return PGR.core.strings.power_turnBased;
                case TurnBasedNext:
                    return PGR.core.strings.power_turnBasedNext;
                case SingleTurn:
                    return PGR.core.strings.power_singleTurn;
                case SingleTurnNext:
                    return PGR.core.strings.power_singleTurnNext;
                case Plated:
                    return PGR.core.strings.power_plated;
            }
            return PGR.core.strings.power_custom;
        }

        public EUITooltip getTip() {
            if (tip == null) {
                tip = new EUITooltip(getText(), getDesc());
            }
            return tip;
        }

        @Override
        public List<EUITooltip> getTips() {
            return Collections.singletonList(getTip());
        }
    }

    public static class PCLPowerDataAdapter extends TypeAdapter<PCLPowerData> {
        @Override
        public PCLPowerData read(JsonReader in) throws IOException {
            String key = in.nextString();
            PCLPowerData data = getStaticDataOrCustom(key);
            if (data != null) {
                return data;
            }
            EUIUtils.logError(PCLPowerDataAdapter.class, "Failed to read power " + key);
            return PCLPowerData.Artifact;
        }

        @Override
        public void write(JsonWriter writer, PCLPowerData value) throws IOException {
            writer.value(value.ID);
        }
    }
}
