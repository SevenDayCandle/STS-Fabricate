package pinacolada.resources.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.rewards.RewardItem;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.ui.tooltips.EUICardPreview;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.powers.common.*;
import pinacolada.powers.replacement.PCLLockOnPower;
import pinacolada.powers.special.ProvokedPower;
import pinacolada.powers.special.SelfImmolationPower;
import pinacolada.powers.special.SilencedPower;
import pinacolada.resources.AbstractTooltips;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;

public class PCLCoreTooltips extends AbstractTooltips {
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

    public EUIKeywordTooltip affinityBlue = EUIKeywordTooltip.findByID("Blue Affinity").forceIcon(true);
    public EUIKeywordTooltip affinityGreen = EUIKeywordTooltip.findByID("Green Affinity").forceIcon(true);
    public EUIKeywordTooltip affinityOrange = EUIKeywordTooltip.findByID("Orange Affinity").forceIcon(true);
    public EUIKeywordTooltip affinityPurple = EUIKeywordTooltip.findByID("Purple Affinity").forceIcon(true);
    public EUIKeywordTooltip affinityRed = EUIKeywordTooltip.findByID("Red Affinity").forceIcon(true);
    public EUIKeywordTooltip affinitySilver = EUIKeywordTooltip.findByID("Silver Affinity").forceIcon(true);
    public EUIKeywordTooltip affinityYellow = EUIKeywordTooltip.findByID("Yellow Affinity").forceIcon(true);
    public EUIKeywordTooltip multicolor = EUIKeywordTooltip.findByID("Multicolor").forceIcon(true);

    public EUIKeywordTooltip attack = EUIKeywordTooltip.findByID("Attack").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip block = EUIKeywordTooltip.findByID("Block").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip buff = EUIKeywordTooltip.findByID("Buff").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip burn = EUIKeywordTooltip.findByID("Burn").setPreviewFunc(() -> makePreview(Burn.ID)).setCanAdd(false);
    public EUIKeywordTooltip create = EUIKeywordTooltip.findByID("Create").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip curse = EUIKeywordTooltip.findByID("Curse").setCanAdd(false);
    public EUIKeywordTooltip dazed = EUIKeywordTooltip.findByID("Dazed").setPreviewFunc(() -> makePreview(Dazed.ID)).setCanAdd(false);
    public EUIKeywordTooltip debuff = EUIKeywordTooltip.findByID("Debuff").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip discard = EUIKeywordTooltip.findByID("Discard").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip draw = EUIKeywordTooltip.findByID("Draw").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip energy = EUIKeywordTooltip.findByName("[E]").setCanAdd(false).forceIcon(true);
    public EUIKeywordTooltip fetch = EUIKeywordTooltip.findByID("Fetch").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip heal = EUIKeywordTooltip.findByID("Heal").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip hp = EUIKeywordTooltip.findByID("HP").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip kill = EUIKeywordTooltip.findByID("Kill").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip maxHP = EUIKeywordTooltip.findByID("Max HP").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip normalDamage = EUIKeywordTooltip.findByID("Normal Damage").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip orb = EUIKeywordTooltip.findByID("Orb").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip pay = EUIKeywordTooltip.findByID("Pay").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip play = EUIKeywordTooltip.findByID("Play").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip power = EUIKeywordTooltip.findByID("Power").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip remove = EUIKeywordTooltip.findByID("Remove").setCanAdd(false).canHighlight(false).canFilter(false);
    public EUIKeywordTooltip reshuffle = EUIKeywordTooltip.findByID("Reshuffle").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip select = EUIKeywordTooltip.findByID("Select").setCanAdd(false).canHighlight(false).canFilter(false);
    public EUIKeywordTooltip skill = EUIKeywordTooltip.findByID("Skill").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip status = EUIKeywordTooltip.findByID("Status").setCanAdd(false);
    public EUIKeywordTooltip upgrade = EUIKeywordTooltip.findByID("Upgrade").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip woundCard = EUIKeywordTooltip.findByID("Wound").setPreviewFunc(() -> makePreview(Wound.ID)).setCanAdd(false);
    public EUIKeywordTooltip voidCard = EUIKeywordTooltip.findByID("Void").setPreviewFunc(() -> makePreview(VoidCard.ID)).setCanAdd(false);

    public EUIKeywordTooltip affinityGeneral = EUIKeywordTooltip.findByID("Affinity");
    public EUIKeywordTooltip affinityUnknown = EUIKeywordTooltip.findByID("Unknown Affinity");
    public EUIKeywordTooltip afterImage = EUIKeywordTooltip.findByID("After Image");
    public EUIKeywordTooltip afterlife = EUIKeywordTooltip.findByID("Afterlife");
    public EUIKeywordTooltip artifact = EUIKeywordTooltip.findByID("Artifact");
    public EUIKeywordTooltip augment = EUIKeywordTooltip.findByID("Augment");
    public EUIKeywordTooltip autoplay = EUIKeywordTooltip.findByID("Autoplay");
    public EUIKeywordTooltip blinded = EUIKeywordTooltip.findByID("Blinded");
    public EUIKeywordTooltip blur = EUIKeywordTooltip.findByID("Blur");
    public EUIKeywordTooltip bounce = EUIKeywordTooltip.findByID("Bounce");
    public EUIKeywordTooltip bruised = EUIKeywordTooltip.findByID("Bruised");
    public EUIKeywordTooltip brutal = EUIKeywordTooltip.findByID("Brutal Damage");
    public EUIKeywordTooltip buffer = EUIKeywordTooltip.findByID("Buffer");
    public EUIKeywordTooltip calm = EUIKeywordTooltip.findByID("Calm");
    public EUIKeywordTooltip chance = EUIKeywordTooltip.findByID("Chance");
    public EUIKeywordTooltip channel = EUIKeywordTooltip.findByID("Channel");
    public EUIKeywordTooltip choked = EUIKeywordTooltip.findByID("Choked");
    public EUIKeywordTooltip confused = EUIKeywordTooltip.findByID("Confused");
    public EUIKeywordTooltip constricted = EUIKeywordTooltip.findByID("Constricted");
    public EUIKeywordTooltip cooldown = EUIKeywordTooltip.findByID("Cooldown");
    public EUIKeywordTooltip corpseExplosion = EUIKeywordTooltip.findByID("Corpse Explosion");
    public EUIKeywordTooltip counter = EUIKeywordTooltip.findByID("Counter");
    public EUIKeywordTooltip critical = EUIKeywordTooltip.findByID("Critical");
    public EUIKeywordTooltip curlUp = EUIKeywordTooltip.findByID("Curl Up");
    public EUIKeywordTooltip cycle = EUIKeywordTooltip.findByID("Cycle");
    public EUIKeywordTooltip dark = EUIKeywordTooltip.findByID("Dark");
    public EUIKeywordTooltip deflection = EUIKeywordTooltip.findByID("Deflection");
    public EUIKeywordTooltip delayed = EUIKeywordTooltip.findByID("Delayed");
    public EUIKeywordTooltip delayedDamage = EUIKeywordTooltip.findByID("Delayed Damage");
    public EUIKeywordTooltip dexterity = EUIKeywordTooltip.findByID("Dexterity");
    public EUIKeywordTooltip divinity = EUIKeywordTooltip.findByID("Enlightenment");
    public EUIKeywordTooltip doubleDamage = EUIKeywordTooltip.findByID("Double Damage");
    public EUIKeywordTooltip duplication = EUIKeywordTooltip.findByID("Duplication");
    public EUIKeywordTooltip energized = EUIKeywordTooltip.findByID("Energized");
    public EUIKeywordTooltip entangled = EUIKeywordTooltip.findByID("Entangled");
    public EUIKeywordTooltip envenom = EUIKeywordTooltip.findByID("Envenom");
    public EUIKeywordTooltip ephemeral = EUIKeywordTooltip.findByID("Ephemeral");
    public EUIKeywordTooltip equilibrium = EUIKeywordTooltip.findByID("Equilibrium");
    public EUIKeywordTooltip escape = EUIKeywordTooltip.findByID("Escape");
    public EUIKeywordTooltip ethereal = EUIKeywordTooltip.findByID("Ethereal");
    public EUIKeywordTooltip evoke = EUIKeywordTooltip.findByID("Evoke");
    public EUIKeywordTooltip exhaust = EUIKeywordTooltip.findByID("Exhaust");
    public EUIKeywordTooltip fatal = EUIKeywordTooltip.findByID("Fatal");
    public EUIKeywordTooltip fleeting = EUIKeywordTooltip.findByID("Fleeting");
    public EUIKeywordTooltip flight = EUIKeywordTooltip.findByID("Flight");
    public EUIKeywordTooltip focus = EUIKeywordTooltip.findByID("Focus");
    public EUIKeywordTooltip foresight = EUIKeywordTooltip.findByID("Foresight");
    public EUIKeywordTooltip fortified = EUIKeywordTooltip.findByID("Fortified");
    public EUIKeywordTooltip fragile = EUIKeywordTooltip.findByID("Fragile");
    public EUIKeywordTooltip frail = EUIKeywordTooltip.findByID("Frail");
    public EUIKeywordTooltip freeAttack = EUIKeywordTooltip.findByID("Free Attack");
    public EUIKeywordTooltip frost = EUIKeywordTooltip.findByID("Frost");
    public EUIKeywordTooltip grave = EUIKeywordTooltip.findByID("Grave");
    public EUIKeywordTooltip haste = EUIKeywordTooltip.findByID("Haste");
    public EUIKeywordTooltip immaterialDamage = EUIKeywordTooltip.findByID("Immaterial Damage");
    public EUIKeywordTooltip impaired = EUIKeywordTooltip.findByID("Impaired");
    public EUIKeywordTooltip innate = EUIKeywordTooltip.findByID("Innate");
    public EUIKeywordTooltip innovation = EUIKeywordTooltip.findByID("Innovation");
    public EUIKeywordTooltip intangible = EUIKeywordTooltip.findByID("Intangible");
    public EUIKeywordTooltip interactable = EUIKeywordTooltip.findByID("Interactable");
    public EUIKeywordTooltip invigorated = EUIKeywordTooltip.findByID("Invigorated");
    public EUIKeywordTooltip invincible = EUIKeywordTooltip.findByID("Invincible");
    public EUIKeywordTooltip juggernaut = EUIKeywordTooltip.findByID("Juggernaut");
    public EUIKeywordTooltip level = EUIKeywordTooltip.findByID("Level");
    public EUIKeywordTooltip lightning = EUIKeywordTooltip.findByID("Lightning");
    public EUIKeywordTooltip limited = EUIKeywordTooltip.findByID("Limited");
    public EUIKeywordTooltip lockOn = EUIKeywordTooltip.findByID("Lock-On");
    public EUIKeywordTooltip loyal = EUIKeywordTooltip.findByID("Loyal");
    public EUIKeywordTooltip malleable = EUIKeywordTooltip.findByID("Malleable");
    public EUIKeywordTooltip mantra = EUIKeywordTooltip.findByID("Prayer"); // YES THIS THING IS CALLED PRAYER
    public EUIKeywordTooltip mark = EUIKeywordTooltip.findByID("Mark");
    public EUIKeywordTooltip metallicize = EUIKeywordTooltip.findByID("Metallicize");
    public EUIKeywordTooltip multiform = EUIKeywordTooltip.findByID("Multiform");
    public EUIKeywordTooltip nextTurnBlock = EUIKeywordTooltip.findByID("Next Turn Block");
    public EUIKeywordTooltip nextTurnDraw = EUIKeywordTooltip.findByID("Draw Plus");
    public EUIKeywordTooltip nextTurnDrawMinus = EUIKeywordTooltip.findByID("Draw Minus");
    public EUIKeywordTooltip noBlock = EUIKeywordTooltip.findByID("No Block");
    public EUIKeywordTooltip noDraw = EUIKeywordTooltip.findByID("No Draw");
    public EUIKeywordTooltip noxiousFumes = EUIKeywordTooltip.findByID("Noxious Fumes");
    public EUIKeywordTooltip obtain = EUIKeywordTooltip.findByID("Obtain");
    public EUIKeywordTooltip orbSlot = EUIKeywordTooltip.findByID("Orb Slot");
    public EUIKeywordTooltip piercing = EUIKeywordTooltip.findByID("Piercing Damage");
    public EUIKeywordTooltip plasma = EUIKeywordTooltip.findByID("Plasma");
    public EUIKeywordTooltip platedArmor = EUIKeywordTooltip.findByID("Plated Armor");
    public EUIKeywordTooltip poison = EUIKeywordTooltip.findByID("Poison");
    public EUIKeywordTooltip provoked = EUIKeywordTooltip.findByID("Provoked");
    public EUIKeywordTooltip purge = EUIKeywordTooltip.findByID("Purge");
    public EUIKeywordTooltip ranged = EUIKeywordTooltip.findByID("Ranged Damage");
    public EUIKeywordTooltip rebound = EUIKeywordTooltip.findByID("Rebound");
    public EUIKeywordTooltip recast = EUIKeywordTooltip.findByID("Recast");
    public EUIKeywordTooltip regeneration = EUIKeywordTooltip.findByID("Regeneration");
    public EUIKeywordTooltip resistance = EUIKeywordTooltip.findByID("Resistance");
    public EUIKeywordTooltip resolve = EUIKeywordTooltip.findByID("Resolve");
    public EUIKeywordTooltip retain = EUIKeywordTooltip.findByID("Retain");
    public EUIKeywordTooltip ritual = EUIKeywordTooltip.findByID("Ritual");
    public EUIKeywordTooltip scout = EUIKeywordTooltip.findByID("Scout");
    public EUIKeywordTooltip scry = EUIKeywordTooltip.findByID("Scry");
    public EUIKeywordTooltip selfImmolation = EUIKeywordTooltip.findByID("Self-Immolation");
    public EUIKeywordTooltip semiLimited = EUIKeywordTooltip.findByID("Semi-Limited");
    public EUIKeywordTooltip shackles = EUIKeywordTooltip.findByID("Shackles");
    public EUIKeywordTooltip silenced = EUIKeywordTooltip.findByID("Silenced");
    public EUIKeywordTooltip slow = EUIKeywordTooltip.findByID("Slow");
    public EUIKeywordTooltip sorcery = EUIKeywordTooltip.findByID("Sorcery");
    public EUIKeywordTooltip soulbound = EUIKeywordTooltip.findByID("Soulbound");
    public EUIKeywordTooltip spread = EUIKeywordTooltip.findByID("Spread");
    public EUIKeywordTooltip stabilize = EUIKeywordTooltip.findByID("Stabilize");
    public EUIKeywordTooltip stance = EUIKeywordTooltip.findByID("Stance");
    public EUIKeywordTooltip starter = EUIKeywordTooltip.findByID("Starter");
    public EUIKeywordTooltip startup = EUIKeywordTooltip.findByID("Startup");
    public EUIKeywordTooltip steal = EUIKeywordTooltip.findByID("Steal");
    public EUIKeywordTooltip strength = EUIKeywordTooltip.findByID("Strength");
    public EUIKeywordTooltip stun = EUIKeywordTooltip.findByID("Stun");
    public EUIKeywordTooltip summon = EUIKeywordTooltip.findByID("Summon");
    public EUIKeywordTooltip supercharged = EUIKeywordTooltip.findByID("Supercharged");
    public EUIKeywordTooltip suspensive = EUIKeywordTooltip.findByID("Suspensive");
    public EUIKeywordTooltip tempHP = EUIKeywordTooltip.findByID("Temporary HP");
    public EUIKeywordTooltip thorns = EUIKeywordTooltip.findByID("Thorns");
    public EUIKeywordTooltip thousandCuts = EUIKeywordTooltip.findByID("Thousand Cuts");
    public EUIKeywordTooltip timing = EUIKeywordTooltip.findByID("Timing");
    public EUIKeywordTooltip toxicology = EUIKeywordTooltip.findByID("Toxicology");
    public EUIKeywordTooltip transform = EUIKeywordTooltip.findByID("Transform");
    public EUIKeywordTooltip trigger = EUIKeywordTooltip.findByID("Trigger");
    public EUIKeywordTooltip turnEnd = EUIKeywordTooltip.findByID("Turn End Timing");
    public EUIKeywordTooltip turnStart = EUIKeywordTooltip.findByID("Turn Start Timing");
    public EUIKeywordTooltip unique = EUIKeywordTooltip.findByID("Unique");
    public EUIKeywordTooltip unplayable = EUIKeywordTooltip.findByID("Unplayable");
    public EUIKeywordTooltip vigor = EUIKeywordTooltip.findByID("Vigor");
    public EUIKeywordTooltip vitality = EUIKeywordTooltip.findByID("Vitality");
    public EUIKeywordTooltip vulnerable = EUIKeywordTooltip.findByID("Vulnerable");
    public EUIKeywordTooltip warding = EUIKeywordTooltip.findByID("Warding");
    public EUIKeywordTooltip weak = EUIKeywordTooltip.findByID("Weak");
    public EUIKeywordTooltip withdraw = EUIKeywordTooltip.findByID("Withdraw");
    public EUIKeywordTooltip wrath = EUIKeywordTooltip.findByID("Wrath");

    public EUIKeywordTooltip gold = new EUIKeywordTooltip(RewardItem.TEXT[1].trim());

    public PCLCoreTooltips() {
        EUIKeywordTooltip.registerID(PCLAffinity.Red.getAffinitySymbol(), affinityRed);
        EUIKeywordTooltip.registerID(PCLAffinity.Green.getAffinitySymbol(), affinityGreen);
        EUIKeywordTooltip.registerID(PCLAffinity.Blue.getAffinitySymbol(), affinityBlue);
        EUIKeywordTooltip.registerID(PCLAffinity.Orange.getAffinitySymbol(), affinityOrange);
        EUIKeywordTooltip.registerID(PCLAffinity.Yellow.getAffinitySymbol(), affinityYellow);
        EUIKeywordTooltip.registerID(PCLAffinity.Purple.getAffinitySymbol(), affinityPurple);
        EUIKeywordTooltip.registerID(PCLAffinity.Silver.getAffinitySymbol(), affinitySilver);
        EUIKeywordTooltip.registerID(PCLAffinity.Star.getAffinitySymbol(), multicolor);
        EUIKeywordTooltip.registerID(PCLAffinity.General.getAffinitySymbol(), affinityGeneral);
        EUIKeywordTooltip.registerID(PCLAffinity.Unknown.getAffinitySymbol(), affinityUnknown);
        EUIKeywordTooltip.registerID("THP", tempHP);
        EUIKeywordTooltip.registerID("Gold", gold);
    }

    public static EUICardPreview makePreview(String cardID) {
        AbstractCard copy = CardLibrary.getCard(cardID);
        if (copy != null) {
            return new EUICardPreview(copy, false);
        }
        return null;
    }

    public void initializeIcons() {
        for (PCLCardTag tag : PCLCardTag.values()) {
            tag.getTooltip().setIcon(tag.getTextureCache().texture(), 6).setBadgeBackground(tag.color);
        }

        ranged.setIcon(PCLCoreImages.Tooltips.ranged.texture());
        piercing.setIcon(PCLCoreImages.Tooltips.piercing.texture());
        immaterialDamage.setIcon(PCLCoreImages.Tooltips.magic.texture());
        brutal.setIcon(PCLCoreImages.Tooltips.brutal.texture());
        tempHP.setIcon(PCLCoreImages.Tooltips.tempHP.texture());
        hp.setIcon(PCLCoreImages.CardIcons.hp.texture());
        block.setIcon(PCLCoreImages.Tooltips.block.texture());
        cooldown.setIcon(PCLCoreImages.Tooltips.cooldown.texture());
        normalDamage.setIcon(PCLCoreImages.Tooltips.damage.texture());
        unique.setIcon(PCLCoreImages.CardIcons.unique.texture()).setIconSizeMulti(0.85f, 0.85f);
        soulbound.setIcon(PCLCoreImages.CardIcons.soulbound.texture()).setIconSizeMulti(0.85f, 0.85f);
        multiform.setIcon(PCLCoreImages.CardIcons.multiform.texture()).setIconSizeMulti(0.85f, 0.85f);
        turnEnd.setIcon(PCLCoreImages.CardIcons.priorityMinus.texture()).setIconSizeMulti(0.85f, 0.85f);
        turnStart.setIcon(PCLCoreImages.CardIcons.priorityPlus.texture()).setIconSizeMulti(0.85f, 0.85f);
        energy.setIconFunc(EUI::getEnergyIcon);

        affinityRed.setIconFunc(PCLAffinity.Red::getTextureRegion);
        affinityGreen.setIconFunc(PCLAffinity.Green::getTextureRegion);
        affinityBlue.setIconFunc(PCLAffinity.Blue::getTextureRegion);
        affinityOrange.setIconFunc(PCLAffinity.Orange::getTextureRegion);
        affinityYellow.setIconFunc(PCLAffinity.Yellow::getTextureRegion);
        affinityPurple.setIconFunc(PCLAffinity.Purple::getTextureRegion);
        affinitySilver.setIconFunc(PCLAffinity.Silver::getTextureRegion);
        affinityGeneral.setIcon(PCLAffinity.General.getDefaultIcon().texture());
        affinityUnknown.setIcon(PCLAffinity.Unknown.getDefaultIcon().texture());
        multicolor.setIcon(PCLAffinity.Star.getDefaultIcon().texture());

        attack.setIcon(EUIGameUtils.iconForType(AbstractCard.CardType.ATTACK));
        skill.setIcon(EUIGameUtils.iconForType(AbstractCard.CardType.SKILL));
        power.setIcon(EUIGameUtils.iconForType(AbstractCard.CardType.POWER));
        curse.setIcon(EUIGameUtils.iconForType(AbstractCard.CardType.CURSE));
        status.setIcon(EUIGameUtils.iconForType(AbstractCard.CardType.STATUS));
        summon.setIcon(EUIGameUtils.iconForType(PCLEnum.CardType.SUMMON));

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
        choked.setIconFromPowerRegion(ICON_CHOKED);
        confused.setIconFromPowerRegion(ICON_CONFUSION);
        constricted.setIconFromPowerRegion(ICON_CONSTRICTED);
        corpseExplosion.setIconFromPowerRegion(ICON_CORPSE_EXPLOSION);
        curlUp.setIconFromPowerRegion(ICON_CURLUP);
        dexterity.setIconFromPowerRegion(ICON_DEXTERITY);
        doubleDamage.setIconFromPowerRegion(ICON_DOUBLE_DAMAGE);
        duplication.setIconFromPowerRegion(ICON_DUPLICATION);
        entangled.setIconFromPowerRegion(ICON_ENTANGLE);
        envenom.setIconFromPowerRegion(ICON_ENVENOM);
        equilibrium.setIconFromPowerRegion(ICON_EQUILIBRIUM);
        flight.setIconFromPowerRegion(ICON_FLIGHT);
        frail.setIconFromPowerRegion(ICON_FRAIL);
        freeAttack.setIconFromPowerRegion(ICON_FREEATTACK);
        focus.setIconFromPowerRegion(ICON_FOCUS);
        foresight.setIconFromPowerRegion(ICON_FORESIGHT);
        intangible.setIconFromPowerRegion(ICON_INTANGIBLE);
        invincible.setIconFromPowerRegion(ICON_INVINCIBLE);
        juggernaut.setIconFromPowerRegion(ICON_JUGGERNAUT);
        lockOn.setIconFromPowerRegion(ICON_LOCKON);
        malleable.setIconFromPowerRegion(ICON_MALLEABLE);
        mantra.setIconFromPowerRegion(ICON_MANTRA);
        mark.setIconFromPowerRegion(ICON_MARKED);
        metallicize.setIconFromPowerRegion(ICON_METALLICIZE);
        nextTurnBlock.setIconFromPowerRegion(ICON_NEXT_TURN_BLOCK);
        nextTurnDraw.setIconFromPowerRegion(ICON_NEXT_TURN_DRAW);
        nextTurnDrawMinus.setIconFromPowerRegion(ICON_NEXT_TURN_DRAW_LESS);
        noBlock.setIconFromPowerRegion(ICON_NO_BLOCK);
        noDraw.setIconFromPowerRegion(ICON_NO_DRAW);
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
        warding.setIconFromPath(PGR.getPowerImage(WardingPower.POWER_ID));

        critical.formatDescription(CriticalPower.MULTIPLIER);
        fortified.formatDescription(FortifiedPower.MULTIPLIER);
        impaired.formatDescription(ImpairedPower.MULTIPLIER);
        invigorated.formatDescription(InvigoratedPower.MULTIPLIER);
        lockOn.formatDescription(PCLLockOnPower.BASE);
        provoked.formatDescription(ProvokedPower.ATTACK_MULTIPLIER);

        EUIKeywordTooltip.updateTooltipIcons();
    }
}
