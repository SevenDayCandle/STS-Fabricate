package pinacolada.resources.pcl;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.rewards.RewardItem;
import extendedui.EUI;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.powers.common.*;
import pinacolada.powers.replacement.PCLLockOnPower;
import pinacolada.powers.special.ProvokedPower;
import pinacolada.powers.special.SelfImmolationPower;
import pinacolada.powers.special.SilencedPower;
import pinacolada.resources.PCLTooltips;
import pinacolada.resources.PGR;

public class PCLCoreTooltips extends PCLTooltips
{
    public static final String ICON_AFTER_IMAGE = "afterImage";
    public static final String ICON_ARTIFACT = "artifact";
    public static final String ICON_BLUR = "blur";
    public static final String ICON_BUFFER = "buffer";
    public static final String ICON_CONSTRICTED = "constricted";
    public static final String ICON_CURLUP = "closeUp";
    public static final String ICON_DEMON_FORM = "demonForm";
    public static final String ICON_DEXTERITY = "dexterity";
    public static final String ICON_ENVENOM = "envenom";
    public static final String ICON_FLIGHT = "flight";
    public static final String ICON_FRAIL = "frail";
    public static final String ICON_FOCUS = "focus";
    public static final String ICON_INTANGIBLE = "intangible";
    public static final String ICON_LOCKON = "lockon";
    public static final String ICON_MALLEABLE = "malleable";
    public static final String ICON_METALLICIZE = "metallicize";
    public static final String ICON_NEXT_TURN_BLOCK = "defenseNext";
    public static final String ICON_NEXT_TURN_DRAW = "carddraw";
    public static final String ICON_NEXT_TURN_DRAW_LESS = "lessdraw";
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

    public EUITooltip affinityBlue = EUITooltip.findByID("Blue Affinity").showText(false);
    public EUITooltip affinityGreen = EUITooltip.findByID("Green Affinity").showText(false);
    public EUITooltip affinityOrange = EUITooltip.findByID("Orange Affinity").showText(false);
    public EUITooltip affinityPurple = EUITooltip.findByID("Purple Affinity").showText(false);
    public EUITooltip affinityRed = EUITooltip.findByID("Red Affinity").showText(false);
    public EUITooltip affinitySilver = EUITooltip.findByID("Silver Affinity").showText(false);
    public EUITooltip affinityYellow = EUITooltip.findByID("Yellow Affinity").showText(false);
    public EUITooltip attack = EUITooltip.findByID("Attack").showText(false).canHighlight(false);
    public EUITooltip block = EUITooltip.findByID("Block").showText(false).canHighlight(false);
    public EUITooltip buff = EUITooltip.findByID("Buff").showText(false).canHighlight(false);
    public EUITooltip burn = EUITooltip.findByID("Burn").showText(false);
    public EUITooltip create = EUITooltip.findByID("Create").showText(false).canHighlight(false);
    public EUITooltip curse = EUITooltip.findByID("Curse").showText(false);
    public EUITooltip dazed = EUITooltip.findByID("Dazed").showText(false);
    public EUITooltip debuff = EUITooltip.findByID("Debuff").showText(false).canHighlight(false);
    public EUITooltip discard = EUITooltip.findByID("Discard").showText(false).canHighlight(false);
    public EUITooltip draw = EUITooltip.findByID("Draw").showText(false).canHighlight(false);
    public EUITooltip energy = EUITooltip.findByName("[E]").showText(false);
    public EUITooltip fetch = EUITooltip.findByID("~Fetch").showText(false).canHighlight(false);
    public EUITooltip heal = EUITooltip.findByID("Heal").showText(false).canHighlight(false);
    public EUITooltip hp = EUITooltip.findByID("HP").showText(false).canHighlight(false);
    public EUITooltip kill = EUITooltip.findByID("Kill").showText(false).canHighlight(false);
    public EUITooltip maxHP = EUITooltip.findByID("Max HP").showText(false).canHighlight(false);
    public EUITooltip normalDamage = EUITooltip.findByID("Normal Damage").showText(false).canHighlight(false);
    public EUITooltip orb = EUITooltip.findByID("Orb").showText(false).canHighlight(false);
    public EUITooltip pay = EUITooltip.findByID("Pay").showText(false).canHighlight(false);
    public EUITooltip play = EUITooltip.findByID("Play").showText(false).canHighlight(false);
    public EUITooltip power = EUITooltip.findByID("Power").showText(false).canHighlight(false);
    public EUITooltip reshuffle = EUITooltip.findByID("Reshuffle").showText(false).canHighlight(false);
    public EUITooltip select = EUITooltip.findByID("Select").showText(false).canHighlight(false).canFilter(false);
    public EUITooltip skill = EUITooltip.findByID("Skill").showText(false).canHighlight(false);
    public EUITooltip spread = EUITooltip.findByID("Spread").showText(false).canHighlight(false);
    public EUITooltip status = EUITooltip.findByID("Status").showText(false);
    public EUITooltip upgrade = EUITooltip.findByID("Upgrade").showText(false).canHighlight(false);
    public EUITooltip voidCard = EUITooltip.findByID("Void").showText(false);

    public EUITooltip affinityGeneral = EUITooltip.findByID("Affinity");
    public EUITooltip affinityUnknown = EUITooltip.findByID("Unknown Affinity");
    public EUITooltip afterImage = EUITooltip.findByID("After Image");
    public EUITooltip afterlife = EUITooltip.findByID("Afterlife");
    public EUITooltip artifact = EUITooltip.findByID("Artifact");
    public EUITooltip autoplay = EUITooltip.findByID("Autoplay");
    public EUITooltip blinded = EUITooltip.findByID("Blinded");
    public EUITooltip blur = EUITooltip.findByID("Blur");
    public EUITooltip bruised = EUITooltip.findByID("Bruised");
    public EUITooltip brutal = EUITooltip.findByID("Brutal Damage");
    public EUITooltip buffer = EUITooltip.findByID("Buffer");
    public EUITooltip calm = EUITooltip.findByID("Calm");
    public EUITooltip chance = EUITooltip.findByID("Chance");
    public EUITooltip channel = EUITooltip.findByID("Channel");
    public EUITooltip constricted = EUITooltip.findByID("Constricted");
    public EUITooltip cooldown = EUITooltip.findByID("Cooldown");
    public EUITooltip critical = EUITooltip.findByID("Critical");
    public EUITooltip curlUp = EUITooltip.findByID("Curl Up");
    public EUITooltip currentAffinity = EUITooltip.findByID("Current Affinity");
    public EUITooltip cycle = EUITooltip.findByID("Cycle");
    public EUITooltip dark = EUITooltip.findByID("Dark");
    public EUITooltip deflection = EUITooltip.findByID("Deflection");
    public EUITooltip delayed = EUITooltip.findByID("Delayed");
    public EUITooltip delayedDamage = EUITooltip.findByID("Delayed Damage");
    public EUITooltip demonForm = EUITooltip.findByID("Demon Form");
    public EUITooltip dexterity = EUITooltip.findByID("Dexterity");
    public EUITooltip divinity = EUITooltip.findByID("Enlightenment");
    public EUITooltip energized = EUITooltip.findByID("Energized");
    public EUITooltip envenom = EUITooltip.findByID("Envenom");
    public EUITooltip ephemeral = EUITooltip.findByID("Ephemeral");
    public EUITooltip ethereal = EUITooltip.findByID("~Ethereal");
    public EUITooltip evoke = EUITooltip.findByID("Evoke");
    public EUITooltip exhaust = EUITooltip.findByID("Exhaust");
    public EUITooltip fatal = EUITooltip.findByID("Fatal");
    public EUITooltip fleeting = EUITooltip.findByID("Fleeting");
    public EUITooltip flight = EUITooltip.findByID("Flight");
    public EUITooltip focus = EUITooltip.findByID("Focus");
    public EUITooltip fortified = EUITooltip.findByID("Fortified");
    public EUITooltip fragile = EUITooltip.findByID("Fragile");
    public EUITooltip frail = EUITooltip.findByID("Frail");
    public EUITooltip frost = EUITooltip.findByID("Frost");
    public EUITooltip grave = EUITooltip.findByID("Grave");
    public EUITooltip haste = EUITooltip.findByID("Haste");
    public EUITooltip immaterialDamage = EUITooltip.findByID("Immaterial Damage");
    public EUITooltip impaired = EUITooltip.findByID("Impaired");
    public EUITooltip innate = EUITooltip.findByID("~Innate");
    public EUITooltip innovation = EUITooltip.findByID("Innovation");
    public EUITooltip intangible = EUITooltip.findByID("Intangible");
    public EUITooltip interactable = EUITooltip.findByID("Interactable");
    public EUITooltip invigorated = EUITooltip.findByID("Invigorated");
    public EUITooltip lastAffinity = EUITooltip.findByID("Last Affinity");
    public EUITooltip level = EUITooltip.findByID("Level");
    public EUITooltip lightning = EUITooltip.findByID("Lightning");
    public EUITooltip limited = EUITooltip.findByID("Limited");
    public EUITooltip lockOn = EUITooltip.findByID("~Lock-On");
    public EUITooltip loyal = EUITooltip.findByID("Loyal");
    public EUITooltip malleable = EUITooltip.findByID("Malleable");
    public EUITooltip match = EUITooltip.findByID("Match");
    public EUITooltip matchCombo = EUITooltip.findByID("Match Combo");
    public EUITooltip metallicize = EUITooltip.findByID("Metallicize");
    public EUITooltip mismatch = EUITooltip.findByID("Mismatch");
    public EUITooltip multicolor = EUITooltip.findByID("Multicolor");
    public EUITooltip multiform = EUITooltip.findByID("Multiform");
    public EUITooltip neutralStance = EUITooltip.findByID("Neutral Stance");
    public EUITooltip nextTurnBlock = EUITooltip.findByID("Next Turn Block");
    public EUITooltip nextTurnDraw = EUITooltip.findByID("Draw Plus");
    public EUITooltip nextTurnDrawLess = EUITooltip.findByID("Draw Minus");
    public EUITooltip noxiousFumes = EUITooltip.findByID("Noxious Fumes");
    public EUITooltip orbSlot = EUITooltip.findByID("Orb Slot");
    public EUITooltip persist = EUITooltip.findByID("Persist");
    public EUITooltip piercing = EUITooltip.findByID("Piercing Damage");
    public EUITooltip plasma = EUITooltip.findByID("Plasma");
    public EUITooltip platedArmor = EUITooltip.findByID("Plated Armor");
    public EUITooltip poison = EUITooltip.findByID("Poison");
    public EUITooltip priority = EUITooltip.findByID("Priority");
    public EUITooltip provoked = EUITooltip.findByID("Provoked");
    public EUITooltip purge = EUITooltip.findByID("~Purge");
    public EUITooltip ranged = EUITooltip.findByID("Ranged Damage");
    public EUITooltip rebound = EUITooltip.findByID("Rebound");
    public EUITooltip recast = EUITooltip.findByID("Recast");
    public EUITooltip regeneration = EUITooltip.findByID("Regeneration");
    public EUITooltip reroll = EUITooltip.findByID("Reroll");
    public EUITooltip resistance = EUITooltip.findByID("Resistance");
    public EUITooltip resolve = EUITooltip.findByID("Resolve");
    public EUITooltip retain = EUITooltip.findByID("~Retain");
    public EUITooltip ritual = EUITooltip.findByID("Ritual");
    public EUITooltip scout = EUITooltip.findByID("Scout");
    public EUITooltip scry = EUITooltip.findByID("Scry");
    public EUITooltip selfImmolation = EUITooltip.findByID("Self-Immolation");
    public EUITooltip semiLimited = EUITooltip.findByID("Semi-Limited");
    public EUITooltip shackles = EUITooltip.findByID("Shackles");
    public EUITooltip silenced = EUITooltip.findByID("Silenced");
    public EUITooltip slow = EUITooltip.findByID("Slow");
    public EUITooltip sorcery = EUITooltip.findByID("Sorcery");
    public EUITooltip soulbound = EUITooltip.findByID("Soulbound");
    public EUITooltip specialAugment = EUITooltip.findByID("Special Augment");
    public EUITooltip stabilize = EUITooltip.findByID("Stabilize");
    public EUITooltip stance = EUITooltip.findByID("Stance");
    public EUITooltip starter = EUITooltip.findByID("Starter");
    public EUITooltip startup = EUITooltip.findByID("Startup");
    public EUITooltip steal = EUITooltip.findByID("~Steal");
    public EUITooltip strength = EUITooltip.findByID("Strength");
    public EUITooltip summon = EUITooltip.findByID("Summon");
    public EUITooltip supercharged = EUITooltip.findByID("Supercharged");
    public EUITooltip tempHP = EUITooltip.findByID("Temporary HP");
    public EUITooltip thorns = EUITooltip.findByID("Thorns");
    public EUITooltip thousandCuts = EUITooltip.findByID("Thousand Cuts");
    public EUITooltip toxicology = EUITooltip.findByID("Toxicology");
    public EUITooltip transform = EUITooltip.findByID("Transform");
    public EUITooltip trigger = EUITooltip.findByID("Trigger");
    public EUITooltip unique = EUITooltip.findByID("Unique");
    public EUITooltip unplayable = EUITooltip.findByID("Unplayable");
    public EUITooltip vigor = EUITooltip.findByID("Vigor");
    public EUITooltip vitality = EUITooltip.findByID("Vitality");
    public EUITooltip vulnerable = EUITooltip.findByID("Vulnerable");
    public EUITooltip weak = EUITooltip.findByID("Weak");
    public EUITooltip withdraw = EUITooltip.findByID("Withdraw");
    public EUITooltip wrath = EUITooltip.findByID("Wrath");

    public EUITooltip gold = new EUITooltip(RewardItem.TEXT[1].trim(), (AbstractPlayer.PlayerClass) null);

    public PCLCoreTooltips()
    {
        EUITooltip.registerID(PCLAffinity.Red.getAffinitySymbol(), affinityRed);
        EUITooltip.registerID(PCLAffinity.Green.getAffinitySymbol(), affinityGreen);
        EUITooltip.registerID(PCLAffinity.Blue.getAffinitySymbol(), affinityBlue);
        EUITooltip.registerID(PCLAffinity.Orange.getAffinitySymbol(), affinityOrange);
        EUITooltip.registerID(PCLAffinity.Yellow.getAffinitySymbol(), affinityYellow);
        EUITooltip.registerID(PCLAffinity.Purple.getAffinitySymbol(), affinityPurple);
        EUITooltip.registerID(PCLAffinity.Silver.getAffinitySymbol(), affinitySilver);
        EUITooltip.registerID(PCLAffinity.Star.getAffinitySymbol(), multicolor);
        EUITooltip.registerID(PCLAffinity.General.getAffinitySymbol(), affinityGeneral);
        EUITooltip.registerID(PCLAffinity.Unknown.getAffinitySymbol(), affinityUnknown);
        EUITooltip.registerID("NTB", nextTurnBlock);
        EUITooltip.registerID("NTD", nextTurnDraw);
        EUITooltip.registerID("THP", tempHP);
        EUITooltip.registerID("Gold", gold);
    }

    public void initializeIcons()
    {
        for (PCLCardTag tag : PCLCardTag.values())
        {
            tag.getTip().setIcon(tag.getTextureCache().texture(), 6).setBadgeBackground(tag.color);
        }

        ranged.setIcon(PCLCoreImages.CardIcons.ranged.texture());
        piercing.setIcon(PCLCoreImages.CardIcons.piercing.texture());
        immaterialDamage.setIcon(PCLCoreImages.CardIcons.magic.texture());
        brutal.setIcon(PCLCoreImages.CardIcons.brutal.texture());
        tempHP.setIcon(PCLCoreImages.CardIcons.tempHP.texture());
        hp.setIcon(PCLCoreImages.CardIcons.hp.texture());
        block.setIcon(PCLCoreImages.CardIcons.block.texture());
        normalDamage.setIcon(PCLCoreImages.CardIcons.damage.texture());
        unique.setIcon(PCLCoreImages.CardIcons.unique.texture()).setIconSizeMulti(0.85f, 0.85f);
        soulbound.setIcon(PCLCoreImages.CardIcons.soulbound.texture()).setIconSizeMulti(0.85f, 0.85f);
        multiform.setIcon(PCLCoreImages.CardIcons.multiform.texture()).setIconSizeMulti(0.85f, 0.85f);
        energy.setIconFunc(EUI::getEnergyIcon);

        affinityRed.setIconFunc(PCLAffinity.Red::getTextureRegion).setIconSizeMulti(0.85f, 0.85f);
        affinityGreen.setIconFunc(PCLAffinity.Green::getTextureRegion).setIconSizeMulti(0.85f, 0.85f);
        affinityBlue.setIconFunc(PCLAffinity.Blue::getTextureRegion).setIconSizeMulti(0.85f, 0.85f);
        affinityOrange.setIconFunc(PCLAffinity.Orange::getTextureRegion).setIconSizeMulti(0.85f, 0.85f);
        affinityYellow.setIconFunc(PCLAffinity.Yellow::getTextureRegion).setIconSizeMulti(0.85f, 0.85f);
        affinityPurple.setIconFunc(PCLAffinity.Purple::getTextureRegion).setIconSizeMulti(0.85f, 0.85f);
        affinitySilver.setIconFunc(PCLAffinity.Silver::getTextureRegion).setIconSizeMulti(0.85f, 0.85f);
        affinityGeneral.setIcon(PCLAffinity.General.getDefaultIcon().texture()).setIconSizeMulti(0.85f, 0.85f);
        affinityUnknown.setIcon(PCLAffinity.Unknown.getDefaultIcon().texture()).setIconSizeMulti(0.85f, 0.85f);
        multicolor.setIcon(PCLAffinity.Star.getDefaultIcon().texture()).setIconSizeMulti(0.85f, 0.85f);

        lightning.setIcon(PCLCoreImages.Tooltips.lightning.texture(), 6);
        plasma.setIcon(PCLCoreImages.Tooltips.plasma.texture(), 6);
        dark.setIcon(PCLCoreImages.Tooltips.dark.texture(), 6);
        frost.setIcon(PCLCoreImages.Tooltips.frost.texture(), 6);
        gold.setIcon(PCLCoreImages.Tooltips.gold.texture(), 6);
        orbSlot.setIcon(PCLCoreImages.Tooltips.orbSlot.texture(), 6);

        afterImage.setIconFromPowerRegion(ICON_AFTER_IMAGE);
        artifact.setIconFromPowerRegion(ICON_ARTIFACT);
        blur.setIconFromPowerRegion(ICON_BLUR);
        buffer.setIconFromPowerRegion(ICON_BUFFER);
        constricted.setIconFromPowerRegion(ICON_CONSTRICTED);
        curlUp.setIconFromPowerRegion(ICON_CURLUP);
        demonForm.setIconFromPowerRegion(ICON_DEMON_FORM);
        dexterity.setIconFromPowerRegion(ICON_DEXTERITY);
        envenom.setIconFromPowerRegion(ICON_ENVENOM);
        flight.setIconFromPowerRegion(ICON_FLIGHT);
        frail.setIconFromPowerRegion(ICON_FRAIL);
        focus.setIconFromPowerRegion(ICON_FOCUS);
        intangible.setIconFromPowerRegion(ICON_INTANGIBLE);
        lockOn.setIconFromPowerRegion(ICON_LOCKON);
        malleable.setIconFromPowerRegion(ICON_MALLEABLE);
        metallicize.setIconFromPowerRegion(ICON_METALLICIZE);
        nextTurnBlock.setIconFromPowerRegion(ICON_NEXT_TURN_BLOCK);
        nextTurnDraw.setIconFromPowerRegion(ICON_NEXT_TURN_DRAW);
        nextTurnDrawLess.setIconFromPowerRegion(ICON_NEXT_TURN_DRAW_LESS);
        noxiousFumes.setIconFromPowerRegion(ICON_NOXIOUSFUMES);
        platedArmor.setIconFromPowerRegion(ICON_PLATEDARMOR);
        poison.setIconFromPowerRegion(ICON_POISON);
        rebound.setIconFromPowerRegion(ICON_REBOUND);
        regeneration.setIconFromPowerRegion(ICON_REGEN);
        ritual.setIconFromPowerRegion(ICON_RITUAL);
        shackles.setIconFromPowerRegion(ICON_SHACKLE);
        slow.setIconFromPowerRegion(ICON_SLOW);
        strength.setIconFromPowerRegion(ICON_STRENGTH);
        thorns.setIconFromPowerRegion(ICON_THORNS);
        thousandCuts.setIconFromPowerRegion(ICON_THOUSAND_CUTS);
        vigor.setIconFromPowerRegion(ICON_VIGOR);
        weak.setIconFromPowerRegion(ICON_WEAK);
        vulnerable.setIconFromPowerRegion(ICON_VULNERABLE);

        bruised.setIconFromPath(PGR.getPowerImage(BruisedPower.POWER_ID));
        blinded.setIconFromPath(PGR.getPowerImage(BlindedPower.POWER_ID));
        deflection.setIconFromPath(PGR.getPowerImage(DeflectionPower.POWER_ID));
        critical.setIconFromPath(PGR.getPowerImage(CriticalPower.POWER_ID));
        delayedDamage.setIconFromPath(PGR.getPowerImage(DelayedDamagePower.POWER_ID));
        energized.setIconFromPath(PGR.getPowerImage(EnergizedPower.POWER_ID));
        fortified.setIconFromPath(PGR.getPowerImage(FortifiedPower.POWER_ID));
        impaired.setIconFromPath(PGR.getPowerImage(ImpairedPower.POWER_ID));
        innovation.setIconFromPath(PGR.getPowerImage(InnovationPower.POWER_ID));
        invigorated.setIconFromPath(PGR.getPowerImage(InvigoratedPower.POWER_ID));
        provoked.setIconFromPath(PGR.getPowerImage(ProvokedPower.POWER_ID));
        resistance.setIconFromPath(PGR.getPowerImage(ResistancePower.POWER_ID));
        selfImmolation.setIconFromPath(PGR.getPowerImage(SelfImmolationPower.POWER_ID));
        silenced.setIconFromPath(PGR.getPowerImage(SilencedPower.POWER_ID));
        sorcery.setIconFromPath(PGR.getPowerImage(SorceryPower.POWER_ID));
        supercharged.setIconFromPath(PGR.getPowerImage(SuperchargedPower.POWER_ID));
        sorcery.setIconFromPath(PGR.getPowerImage(SorceryPower.POWER_ID));
        toxicology.setIconFromPath(PGR.getPowerImage(ToxicologyPower.POWER_ID));
        vitality.setIconFromPath(PGR.getPowerImage(VitalityPower.POWER_ID));

        critical.formatDescription(CriticalPower.MULTIPLIER);
        fortified.formatDescription(FortifiedPower.MULTIPLIER);
        impaired.formatDescription(ImpairedPower.MULTIPLIER);
        invigorated.formatDescription(InvigoratedPower.MULTIPLIER);
        lockOn.formatDescription(PCLLockOnPower.BASE);
        provoked.formatDescription(ProvokedPower.ATTACK_MULTIPLIER);
        steal.formatDescription(StolenGoldPower.GOLD_BOSS, StolenGoldPower.GOLD_ELITE, StolenGoldPower.GOLD_NORMAL);

        EUITooltip.updateTooltipIcons();
    }
}
