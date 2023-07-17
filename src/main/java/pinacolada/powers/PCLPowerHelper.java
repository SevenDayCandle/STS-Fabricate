package pinacolada.powers;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.powers.watcher.MantraPower;
import com.megacrit.cardcrawl.powers.watcher.MarkPower;
import com.megacrit.cardcrawl.powers.watcher.VigorPower;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.interfaces.delegates.FuncT3;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.powers.common.EnergizedPower;
import pinacolada.powers.common.*;
import pinacolada.powers.replacement.PCLConstrictedPower;
import pinacolada.powers.replacement.PCLCurlUpPower;
import pinacolada.powers.replacement.PCLLockOnPower;
import pinacolada.powers.special.ProvokedPower;
import pinacolada.powers.special.SelfImmolationPower;
import pinacolada.powers.special.SilencedPower;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

// Copied and modified from STS-AnimatorMod
// TODO add color filtering
@JsonAdapter(PCLPowerHelper.PCLPowerHelperAdapter.class)
public class PCLPowerHelper implements TooltipProvider {
    private static final Map<String, PCLPowerHelper> ALL = new HashMap<>();
    private static final Map<String, PCLPowerHelper> COMMON_BUFFS = new HashMap<>();
    private static final Map<String, PCLPowerHelper> COMMON_DEBUFFS = new HashMap<>();
    public static final PCLPowerHelper Blinded = new PCLPowerHelper(BlindedPower.POWER_ID, PGR.core.tooltips.blinded, BlindedPower::new, Behavior.TurnBased, true, true, false);
    public static final PCLPowerHelper Bruised = new PCLPowerHelper(BruisedPower.POWER_ID, PGR.core.tooltips.bruised, BruisedPower::new, Behavior.TurnBased, true, true, false);
    public static final PCLPowerHelper Choked = new PCLPowerHelper(ChokePower.POWER_ID, PGR.core.tooltips.choked, ChokePower::new, Behavior.Permanent, false, true, false);
    public static final PCLPowerHelper Confused = new PCLPowerHelper(ConfusionPower.POWER_ID, PGR.core.tooltips.confused, (o, s, a) -> new ConfusionPower(o), Behavior.Permanent, false, true, false);
    public static final PCLPowerHelper Constricted = new PCLPowerHelper(ConstrictedPower.POWER_ID, PGR.core.tooltips.constricted, PCLConstrictedPower::new, Behavior.Permanent, true, true, false);
    public static final PCLPowerHelper DelayedDamage = new PCLPowerHelper(DelayedDamagePower.POWER_ID, PGR.core.tooltips.delayedDamage, (o, s, a) -> new DelayedDamagePower(o, a, PCLEnum.AttackEffect.CLAW), Behavior.SingleTurn, true, true, false);
    public static final PCLPowerHelper DrawMinus = new PCLPowerHelper(DrawMinusPower.POWER_ID, PGR.core.tooltips.nextTurnDrawMinus, DrawMinusPower::new, Behavior.SingleTurn, true, true, false);
    public static final PCLPowerHelper Entangled = new PCLPowerHelper(EntanglePower.POWER_ID, PGR.core.tooltips.entangled, (o,s,a) -> new EntanglePower(o), Behavior.SingleTurn, false, true, false);
    public static final PCLPowerHelper Frail = new PCLPowerHelper(FrailPower.POWER_ID, PGR.core.tooltips.frail, (o, s, a) -> new FrailPower(o, a, shouldExtend(o, s)), Behavior.TurnBased, true, true, true);
    public static final PCLPowerHelper Impaired = new PCLPowerHelper(ImpairedPower.POWER_ID, PGR.core.tooltips.impaired, (o, s, a) -> new ImpairedPower(o, a, shouldExtend(o, s)), Behavior.TurnBased, true, true, true);
    public static final PCLPowerHelper LockOn = new PCLPowerHelper(com.megacrit.cardcrawl.powers.LockOnPower.POWER_ID, PGR.core.tooltips.lockOn, PCLLockOnPower::new, Behavior.TurnBased, true, true, true);
    public static final PCLPowerHelper Mark = new PCLPowerHelper(MarkPower.POWER_ID, PGR.core.tooltips.mark, MarkPower::new, Behavior.Permanent, false, true, false);
    public static final PCLPowerHelper NoBlock = new PCLPowerHelper(NoBlockPower.POWER_ID, PGR.core.tooltips.noBlock, (o, s, a) -> new NoBlockPower(o, a, shouldExtend(o, s)), Behavior.TurnBased, false, true, false);
    public static final PCLPowerHelper NoDraw = new PCLPowerHelper(NoDrawPower.POWER_ID, PGR.core.tooltips.noDraw, (o, s, a) -> new NoDrawPower(o), Behavior.SingleTurn, false, true, false);
    public static final PCLPowerHelper Poison = new PCLPowerHelper(PoisonPower.POWER_ID, PGR.core.tooltips.poison, PoisonPower::new, Behavior.TurnBased, true, true, false);
    public static final PCLPowerHelper Provoked = new PCLPowerHelper(ProvokedPower.POWER_ID, PGR.core.tooltips.provoked, ProvokedPower::new, Behavior.TurnBased, false, true, false);
    public static final PCLPowerHelper SelfImmolation = new PCLPowerHelper(SelfImmolationPower.POWER_ID, PGR.core.tooltips.selfImmolation, (o, s, a) -> new SelfImmolationPower(o, a, shouldExtend(o, s)), Behavior.TurnBased, false, true, false);
    public static final PCLPowerHelper Shackles = new PCLPowerHelper(ShacklesPower.POWER_ID, PGR.core.tooltips.shackles, ShacklesPower::new, Behavior.SingleTurn, true, true, false);
    public static final PCLPowerHelper Silenced = new PCLPowerHelper(SilencedPower.POWER_ID, PGR.core.tooltips.silenced, SilencedPower::new, Behavior.TurnBased, false, true, false);
    public static final PCLPowerHelper Slow = new PCLPowerHelper(SlowPower.POWER_ID, PGR.core.tooltips.slow, SlowPower::new, Behavior.Permanent, false, true, true);
    public static final PCLPowerHelper Vulnerable = new PCLPowerHelper(VulnerablePower.POWER_ID, PGR.core.tooltips.vulnerable, (o, s, a) -> new VulnerablePower(o, a, shouldExtend(o, s)), Behavior.TurnBased, true, true, true);
    public static final PCLPowerHelper Weak = new PCLPowerHelper(WeakPower.POWER_ID, PGR.core.tooltips.weak, (o, s, a) -> new WeakPower(o, a, shouldExtend(o, s)), Behavior.TurnBased, true, true, true);

    public static final PCLPowerHelper Artifact = new PCLPowerHelper(ArtifactPower.POWER_ID, PGR.core.tooltips.artifact, ArtifactPower::new, Behavior.Permanent, true, false, false);
    public static final PCLPowerHelper Blur = new PCLPowerHelper(BlurPower.POWER_ID, PGR.core.tooltips.blur, BlurPower::new, Behavior.TurnBased, true, false, false);
    public static final PCLPowerHelper Buffer = new PCLPowerHelper(BufferPower.POWER_ID, PGR.core.tooltips.buffer, BufferPower::new, Behavior.Permanent, true, false, false);
    public static final PCLPowerHelper Critical = new PCLPowerHelper(CriticalPower.POWER_ID, PGR.core.tooltips.critical, CriticalPower::new, Behavior.Permanent, false, false, true);
    public static final PCLPowerHelper CurlUp = new PCLPowerHelper(PCLCurlUpPower.POWER_ID, PGR.core.tooltips.curlUp, PCLCurlUpPower::new, Behavior.Permanent, true, false, false);
    public static final PCLPowerHelper Deflection = new PCLPowerHelper(DeflectionPower.POWER_ID, PGR.core.tooltips.deflection, DeflectionPower::new, Behavior.SingleTurn, false, false, false);
    public static final PCLPowerHelper Dexterity = new PCLPowerHelper(DexterityPower.POWER_ID, PGR.core.tooltips.dexterity, DexterityPower::new, Behavior.Permanent, true, false, false);
    public static final PCLPowerHelper Energized = new PCLPowerHelper(EnergizedPower.POWER_ID, PGR.core.tooltips.energized, EnergizedPower::new, Behavior.SingleTurn, true, false, false);
    public static final PCLPowerHelper Envenom = new PCLPowerHelper(EnvenomPower.POWER_ID, PGR.core.tooltips.envenom, EnvenomPower::new, Behavior.Permanent, false, false, false);
    public static final PCLPowerHelper Equilibrium = new PCLPowerHelper(EquilibriumPower.POWER_ID, PGR.core.tooltips.equilibrium, EquilibriumPower::new, Behavior.TurnBased, false, false, false);
    public static final PCLPowerHelper Flight = new PCLPowerHelper(FlightPower.POWER_ID, PGR.core.tooltips.flight, FlightPower::new, Behavior.Permanent, false, false, true);
    public static final PCLPowerHelper Focus = new PCLPowerHelper(FocusPower.POWER_ID, PGR.core.tooltips.focus, FocusPower::new, Behavior.Permanent, true, false, false);
    public static final PCLPowerHelper Fortified = new PCLPowerHelper(FortifiedPower.POWER_ID, PGR.core.tooltips.fortified, FortifiedPower::new, Behavior.TurnBased, true, false, true);
    public static final PCLPowerHelper Innovation = new PCLPowerHelper(InnovationPower.POWER_ID, PGR.core.tooltips.innovation, InnovationPower::new, Behavior.Permanent, true, false, false);
    public static final PCLPowerHelper Intangible = new PCLPowerHelper(IntangiblePlayerPower.POWER_ID, PGR.core.tooltips.intangible, IntangiblePlayerPower::new, Behavior.TurnBased, false, false, false);
    public static final PCLPowerHelper Invigorated = new PCLPowerHelper(InvigoratedPower.POWER_ID, PGR.core.tooltips.invigorated, InvigoratedPower::new, Behavior.TurnBased, true, false, true);
    public static final PCLPowerHelper Juggernaut = new PCLPowerHelper(JuggernautPower.POWER_ID, PGR.core.tooltips.juggernaut, DemonFormPower::new, Behavior.Permanent, false, false, false);
    public static final PCLPowerHelper Malleable = new PCLPowerHelper(MalleablePower.POWER_ID, PGR.core.tooltips.malleable, MalleablePower::new, Behavior.Permanent, true, false, false);
    public static final PCLPowerHelper Mantra = new PCLPowerHelper(MantraPower.POWER_ID, PGR.core.tooltips.mantra, MantraPower::new, Behavior.Permanent, false, false, false);
    public static final PCLPowerHelper Metallicize = new PCLPowerHelper(MetallicizePower.POWER_ID, PGR.core.tooltips.metallicize, MetallicizePower::new, Behavior.Permanent, true, false, false);
    public static final PCLPowerHelper NextTurnBlock = new PCLPowerHelper(NextTurnBlockPower.POWER_ID, PGR.core.tooltips.nextTurnBlock, (o, s, a) -> new NextTurnBlockPower(o, a), Behavior.SingleTurn, true, false, false);
    public static final PCLPowerHelper NextTurnDraw = new PCLPowerHelper(DrawCardNextTurnPower.POWER_ID, PGR.core.tooltips.nextTurnDraw, DrawCardNextTurnPower::new, Behavior.SingleTurn, true, false, false);
    public static final PCLPowerHelper NoxiousFumes = new PCLPowerHelper(NoxiousFumesPower.POWER_ID, PGR.core.tooltips.noxiousFumes, NoxiousFumesPower::new, Behavior.Permanent, false, false, false);
    public static final PCLPowerHelper PlatedArmor = new PCLPowerHelper(PlatedArmorPower.POWER_ID, PGR.core.tooltips.platedArmor, PlatedArmorPower::new, Behavior.Permanent, true, false, false);
    public static final PCLPowerHelper Rebound = new PCLPowerHelper(ReboundPower.POWER_ID, PGR.core.tooltips.rebound, (o, s, a) -> setAmount(new ReboundPower(o), a), Behavior.Permanent, true, false, false);
    public static final PCLPowerHelper Regen = new PCLPowerHelper(RegenPower.POWER_ID, PGR.core.tooltips.regeneration, RegenPower::new, Behavior.TurnBased, false, false, false);
    public static final PCLPowerHelper Resistance = new PCLPowerHelper(ResistancePower.POWER_ID, PGR.core.tooltips.resistance, ResistancePower::new, Behavior.Permanent, true, false, true);
    public static final PCLPowerHelper Ritual = new PCLPowerHelper(RitualPower.POWER_ID, PGR.core.tooltips.ritual, (o, s, a) -> new RitualPower(o, a, GameUtilities.isPlayer(o)), Behavior.Permanent, true, false, false);
    public static final PCLPowerHelper Sorcery = new PCLPowerHelper(SorceryPower.POWER_ID, PGR.core.tooltips.sorcery, SorceryPower::new, Behavior.Permanent, true, false, false);
    public static final PCLPowerHelper Strength = new PCLPowerHelper(StrengthPower.POWER_ID, PGR.core.tooltips.strength, StrengthPower::new, Behavior.Permanent, true, false, false);
    public static final PCLPowerHelper Supercharged = new PCLPowerHelper(SuperchargedPower.POWER_ID, PGR.core.tooltips.supercharged, SuperchargedPower::new, Behavior.Permanent, false, false, false);
    public static final PCLPowerHelper Thorns = new PCLPowerHelper(ThornsPower.POWER_ID, PGR.core.tooltips.thorns, ThornsPower::new, Behavior.Permanent, true, false, false);
    public static final PCLPowerHelper ThousandCuts = new PCLPowerHelper(ThousandCutsPower.POWER_ID, PGR.core.tooltips.thousandCuts, ThousandCutsPower::new, Behavior.Permanent, false, false, false);
    public static final PCLPowerHelper Toxicology = new PCLPowerHelper(ToxicologyPower.POWER_ID, PGR.core.tooltips.toxicology, ToxicologyPower::new, Behavior.Permanent, false, false, false);
    public static final PCLPowerHelper Vigor = new PCLPowerHelper(VigorPower.POWER_ID, PGR.core.tooltips.vigor, VigorPower::new, Behavior.Permanent, true, false, false);
    public static final PCLPowerHelper Vitality = new PCLPowerHelper(VitalityPower.POWER_ID, PGR.core.tooltips.vitality, VitalityPower::new, Behavior.Permanent, true, false, false);
    public static final PCLPowerHelper Warding = new PCLPowerHelper(WardingPower.POWER_ID, PGR.core.tooltips.warding, WardingPower::new, Behavior.Permanent, true, false, false);
    protected final FuncT2<AbstractPower, AbstractCreature, Integer> constructorT2;
    protected final FuncT3<AbstractPower, AbstractCreature, AbstractCreature, Integer> constructorT3;
    public final String ID;
    public final Behavior endTurnBehavior;
    public final boolean isCommon;
    public final boolean isDebuff;
    public final boolean isPercentageBonus;
    public EUIKeywordTooltip tooltip;

    public PCLPowerHelper(String powerID, EUIKeywordTooltip tooltip, FuncT2<AbstractPower, AbstractCreature, Integer> constructor, Behavior endTurnBehavior, boolean isCommon, boolean isDebuff, boolean isPercentageBonus) {
        this.ID = powerID;
        this.tooltip = tooltip;
        this.constructorT2 = constructor;
        this.constructorT3 = null;
        this.endTurnBehavior = endTurnBehavior;
        this.isCommon = isCommon;
        this.isDebuff = isDebuff;
        this.isPercentageBonus = isPercentageBonus;

        registerHelper(powerID);
    }

    public PCLPowerHelper(String powerID, EUIKeywordTooltip tooltip, FuncT3<AbstractPower, AbstractCreature, AbstractCreature, Integer> constructor, Behavior endTurnBehavior, boolean isCommon, boolean isDebuff, boolean isPercentageBonus) {
        this.ID = powerID;
        this.tooltip = tooltip;
        this.constructorT2 = null;
        this.constructorT3 = constructor;
        this.endTurnBehavior = endTurnBehavior;
        this.isCommon = isCommon;
        this.isDebuff = isDebuff;
        this.isPercentageBonus = isPercentageBonus;

        registerHelper(powerID);
    }

    public static ArrayList<PCLPowerHelper> commonBuffs() {
        return new ArrayList<>(COMMON_BUFFS.values());
    }

    public static ArrayList<PCLPowerHelper> commonDebuffs() {
        return new ArrayList<>(COMMON_DEBUFFS.values());
    }

    public static PCLPowerHelper get(String powerID) {
        return ALL.get(powerID);
    }

    public static PCLPowerHelper randomBuff() {
        return GameUtilities.getRandomElement(commonBuffs());
    }

    public static PCLPowerHelper randomDebuff() {
        return GameUtilities.getRandomElement(commonDebuffs());
    }

    private static AbstractPower setAmount(AbstractPower original, Integer amount) {
        original.amount = amount;
        return original;
    }

    public static boolean shouldExtend(AbstractCreature o, AbstractCreature s) {
        return GameUtilities.isMonster(s) || GameUtilities.isPlayer(o);
    }

    public static Collection<PCLPowerHelper> sortedCommons() {
        return ALL.values().stream().sorted((a, b) -> StringUtils.compare(a.tooltip.title, b.tooltip.title)).filter(p -> p.isCommon).collect(Collectors.toList());
    }

    public static Collection<PCLPowerHelper> sortedValues() {
        return ALL.values().stream().sorted((a, b) -> StringUtils.compare(a.tooltip.title, b.tooltip.title)).collect(Collectors.toList());
    }

    protected void applyPriority(AbstractPower po) {
        if (isPercentageBonus) {
            po.priority += 1;
        }
    }

    public AbstractPower create(AbstractCreature owner, AbstractCreature source, int amount, boolean temporary) {
        AbstractPower po = null;
        if (constructorT2 != null) {
            po = constructorT2.invoke(owner, amount);
        }
        else if (constructorT3 != null) {
            po = constructorT3.invoke(owner, source, amount);
        }
        else {
            EUIUtils.logError(this, "Do not create a PowerHelper with a null constructor.");
            return null;
        }

        applyPriority(po);

        return temporary ? new TemporaryPower(owner, po) : po;
    }

    public AbstractPower create(AbstractCreature owner, AbstractCreature source, int amount) {
        return create(owner, source, amount, false);
    }

    @Override
    public EUIKeywordTooltip getTooltip() {
        return tooltip;
    }

    @Override
    public List<EUIKeywordTooltip> getTips() {
        return Collections.singletonList(tooltip);
    }

    protected void registerHelper(String powerID) {
        ALL.putIfAbsent(powerID, this);
        if (isCommon) {
            if (isDebuff) {
                COMMON_DEBUFFS.putIfAbsent(powerID, this);
            }
            else {
                COMMON_BUFFS.putIfAbsent(powerID, this);
            }
        }
    }

    public enum Behavior {
        Permanent,
        SingleTurn,
        TurnBased,
    }

    public static class PCLPowerHelperAdapter extends TypeAdapter<PCLPowerHelper> {
        @Override
        public void write(JsonWriter writer, PCLPowerHelper value) throws IOException {
            writer.value(value.ID);
        }

        @Override
        public PCLPowerHelper read(JsonReader in) throws IOException {
            return get(in.nextString());
        }
    }
}
