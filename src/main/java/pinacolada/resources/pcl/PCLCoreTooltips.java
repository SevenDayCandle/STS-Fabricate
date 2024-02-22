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
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.dungeon.SummonPool;
import pinacolada.orbs.PCLOrbData;
import pinacolada.powers.PCLPowerData;
import pinacolada.powers.common.*;
import pinacolada.powers.replacement.PCLLockOnPower;
import pinacolada.resources.AbstractTooltips;
import pinacolada.resources.PCLEnum;

public class PCLCoreTooltips extends AbstractTooltips {

    public EUIKeywordTooltip energy = EUIKeywordTooltip.findByID(EUI.ENERGY_ID);

    public EUIKeywordTooltip attack = tryLoadTip("Attack").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip block = tryLoadTip("Block").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip buff = tryLoadTip("Buff").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip create = tryLoadTip("Create").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip curse = tryLoadTip("Curse").setCanAdd(false);
    public EUIKeywordTooltip debuff = tryLoadTip("Debuff").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip discard = tryLoadTip("Discard").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip draw = tryLoadTip("Draw").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip fetch = tryLoadTip("Fetch").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip heal = tryLoadTip("Heal").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip hp = tryLoadTip("HP").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip kill = tryLoadTip("Kill").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip maxHP = tryLoadTip("Max HP").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip normalDamage = tryLoadTip("Normal Damage").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip orb = tryLoadTip("Orb").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip pay = tryLoadTip("Pay").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip play = tryLoadTip("Play").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip power = tryLoadTip("Power").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip remove = tryLoadTip("Remove").setCanAdd(false).canHighlight(false).canFilter(false);
    public EUIKeywordTooltip reshuffle = tryLoadTip("Reshuffle").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip select = tryLoadTip("Select").setCanAdd(false).canHighlight(false).canFilter(false);
    public EUIKeywordTooltip skill = tryLoadTip("Skill").setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip status = tryLoadTip("Status").setCanAdd(false);
    public EUIKeywordTooltip upgrade = tryLoadTip("Upgrade").setCanAdd(false).canHighlight(false);

    public EUIKeywordTooltip burn = tryLoadTip("Burn").setPreviewFunc(() -> makePreview(Burn.ID)).setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip dazed = tryLoadTip("Dazed").setPreviewFunc(() -> makePreview(Dazed.ID)).setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip woundCard = tryLoadTip("Wound").setPreviewFunc(() -> makePreview(Wound.ID)).setCanAdd(false).canHighlight(false);
    public EUIKeywordTooltip voidCard = tryLoadTip("Void").setPreviewFunc(() -> makePreview(VoidCard.ID)).setCanAdd(false).canHighlight(false);

    public EUIKeywordTooltip accuracy = tryLoadTip("Accuracy");
    public EUIKeywordTooltip affinityGeneral = tryLoadTip("A-W");
    public EUIKeywordTooltip affinityUnknown = tryLoadTip("A-U");
    public EUIKeywordTooltip afterImage = tryLoadTip("After Image");
    public EUIKeywordTooltip amplify = tryLoadTip("Amplify");
    public EUIKeywordTooltip angry = tryLoadTip("Angry");
    public EUIKeywordTooltip artifact = tryLoadTip("Artifact");
    public EUIKeywordTooltip augment = tryLoadTip("Augment");
    public EUIKeywordTooltip autoplay = tryLoadTip("Autoplay");
    public EUIKeywordTooltip barricade = tryLoadTip("Barricade");
    public EUIKeywordTooltip beatOfDeath = tryLoadTip("Beat Of Death");
    public EUIKeywordTooltip blinded = tryLoadTip("Blinded");
    public EUIKeywordTooltip blockReturn = tryLoadTip("Block Return");
    public EUIKeywordTooltip blur = tryLoadTip("Blur");
    public EUIKeywordTooltip bounce = tryLoadTip("Bounce");
    public EUIKeywordTooltip bruised = tryLoadTip("Bruised");
    public EUIKeywordTooltip brutal = tryLoadTip("Brutal Damage");
    public EUIKeywordTooltip brutality = tryLoadTip("Brutality");
    public EUIKeywordTooltip buffer = tryLoadTip("Buffer");
    public EUIKeywordTooltip burst = tryLoadTip("Burst");
    public EUIKeywordTooltip calm = tryLoadTip("Calm");
    public EUIKeywordTooltip chance = tryLoadTip("Chance");
    public EUIKeywordTooltip channel = tryLoadTip("Channel");
    public EUIKeywordTooltip choked = tryLoadTip("Choked");
    public EUIKeywordTooltip confused = tryLoadTip("Confused");
    public EUIKeywordTooltip constricted = tryLoadTip("Constricted");
    public EUIKeywordTooltip cooldown = tryLoadTip("Cooldown");
    public EUIKeywordTooltip corpseExplosion = tryLoadTip("Corpse Explosion");
    public EUIKeywordTooltip corruption = tryLoadTip("Corruption");
    public EUIKeywordTooltip counter = tryLoadTip("Counter");
    public EUIKeywordTooltip critical = tryLoadTip("Critical");
    public EUIKeywordTooltip curiosity = tryLoadTip("Curiosity");
    public EUIKeywordTooltip curlUp = tryLoadTip("Curl Up");
    public EUIKeywordTooltip cycle = tryLoadTip("Cycle");
    public EUIKeywordTooltip dark = tryLoadTip("Dark");
    public EUIKeywordTooltip deflection = tryLoadTip("Deflection");
    public EUIKeywordTooltip delayed = tryLoadTip("Delayed");
    public EUIKeywordTooltip delayedDamage = tryLoadTip("Delayed Damage");
    public EUIKeywordTooltip deva = tryLoadTip("Deva");
    public EUIKeywordTooltip devotion = tryLoadTip("Devotion");
    public EUIKeywordTooltip dexterity = tryLoadTip("Dexterity");
    public EUIKeywordTooltip divinity = tryLoadTip("Enlightenment");
    public EUIKeywordTooltip doubleDamage = tryLoadTip("Double Damage");
    public EUIKeywordTooltip doubleTap = tryLoadTip("Double Tap");
    public EUIKeywordTooltip duplication = tryLoadTip("Duplication");
    public EUIKeywordTooltip echo = tryLoadTip("Echo");
    public EUIKeywordTooltip energized = tryLoadTip("Energized");
    public EUIKeywordTooltip enrage = tryLoadTip("Enrage");
    public EUIKeywordTooltip entangled = tryLoadTip("Entangled");
    public EUIKeywordTooltip envenom = tryLoadTip("Envenom");
    public EUIKeywordTooltip ephemeral = tryLoadTip("Ephemeral");
    public EUIKeywordTooltip equilibrium = tryLoadTip("Equilibrium");
    public EUIKeywordTooltip escape = tryLoadTip("Escape");
    public EUIKeywordTooltip ethereal = tryLoadTip("Ethereal");
    public EUIKeywordTooltip evoke = tryLoadTip("Evoke");
    public EUIKeywordTooltip exhaust = tryLoadTip("Exhaust");
    public EUIKeywordTooltip explosive = tryLoadTip("Explosive");
    public EUIKeywordTooltip fading = tryLoadTip("Fading");
    public EUIKeywordTooltip fatal = tryLoadTip("Fatal");
    public EUIKeywordTooltip fleeting = tryLoadTip("Fleeting");
    public EUIKeywordTooltip flight = tryLoadTip("Flight");
    public EUIKeywordTooltip focus = tryLoadTip("Focus");
    public EUIKeywordTooltip foresight = tryLoadTip("Foresight");
    public EUIKeywordTooltip fortified = tryLoadTip("Fortified");
    public EUIKeywordTooltip fragile = tryLoadTip("Fragile");
    public EUIKeywordTooltip frail = tryLoadTip("Frail");
    public EUIKeywordTooltip freeAttack = tryLoadTip("Free Attack");
    public EUIKeywordTooltip frost = tryLoadTip("Frost");
    public EUIKeywordTooltip grave = tryLoadTip("Grave");
    public EUIKeywordTooltip haste = tryLoadTip("Haste");
    public EUIKeywordTooltip hex = tryLoadTip("Hex");
    public EUIKeywordTooltip immaterialDamage = tryLoadTip("Immaterial Damage");
    public EUIKeywordTooltip impaired = tryLoadTip("Impaired");
    public EUIKeywordTooltip innate = tryLoadTip("Innate");
    public EUIKeywordTooltip innovation = tryLoadTip("Innovation");
    public EUIKeywordTooltip intangible = tryLoadTip("Intangible");
    public EUIKeywordTooltip interactable = tryLoadTip("Interactable");
    public EUIKeywordTooltip invigorated = tryLoadTip("Invigorated");
    public EUIKeywordTooltip invincible = tryLoadTip("Invincible");
    public EUIKeywordTooltip juggernaut = tryLoadTip("Juggernaut");
    public EUIKeywordTooltip level = tryLoadTip("Level");
    public EUIKeywordTooltip lightning = tryLoadTip("Lightning");
    public EUIKeywordTooltip limited = tryLoadTip("Limited");
    public EUIKeywordTooltip lockOn = tryLoadTip("Lock-On");
    public EUIKeywordTooltip loyal = tryLoadTip("Loyal");
    public EUIKeywordTooltip malleable = tryLoadTip("Malleable");
    public EUIKeywordTooltip mantra = tryLoadTip("Prayer"); // YES THIS THING IS CALLED PRAYER
    public EUIKeywordTooltip mark = tryLoadTip("Mark");
    public EUIKeywordTooltip metallicize = tryLoadTip("Metallicize");
    public EUIKeywordTooltip multiform = tryLoadTip("Multiform");
    public EUIKeywordTooltip nextTurnBlock = tryLoadTip("Next Turn Block");
    public EUIKeywordTooltip nextTurnDraw = tryLoadTip("Draw Plus");
    public EUIKeywordTooltip nextTurnDrawMinus = tryLoadTip("Draw Minus");
    public EUIKeywordTooltip noBlock = tryLoadTip("No Block");
    public EUIKeywordTooltip noDraw = tryLoadTip("No Draw");
    public EUIKeywordTooltip noxiousFumes = tryLoadTip("Noxious Fumes");
    public EUIKeywordTooltip obtain = tryLoadTip("Obtain");
    public EUIKeywordTooltip orbSlot = tryLoadTip("Orb Slot");
    public EUIKeywordTooltip piercing = tryLoadTip("Piercing Damage");
    public EUIKeywordTooltip plasma = tryLoadTip("Plasma");
    public EUIKeywordTooltip platedArmor = tryLoadTip("Plated Armor");
    public EUIKeywordTooltip poison = tryLoadTip("Poison");
    public EUIKeywordTooltip provoked = tryLoadTip("Provoked");
    public EUIKeywordTooltip purge = tryLoadTip("Purge");
    public EUIKeywordTooltip ranged = tryLoadTip("Ranged Damage");
    public EUIKeywordTooltip rebound = tryLoadTip("Rebound");
    public EUIKeywordTooltip recast = tryLoadTip("Recast");
    public EUIKeywordTooltip regeneration = tryLoadTip("Regeneration");
    public EUIKeywordTooltip resistance = tryLoadTip("Resistance");
    public EUIKeywordTooltip retain = tryLoadTip("Retain");
    public EUIKeywordTooltip ritual = tryLoadTip("Ritual");
    public EUIKeywordTooltip scout = tryLoadTip("Scout");
    public EUIKeywordTooltip scry = tryLoadTip("Scry");
    public EUIKeywordTooltip selfImmolation = tryLoadTip("Self-Immolation");
    public EUIKeywordTooltip semiLimited = tryLoadTip("Semi-Limited");
    public EUIKeywordTooltip shackles = tryLoadTip("Shackles");
    public EUIKeywordTooltip shifting = tryLoadTip("Shifting");
    public EUIKeywordTooltip silenced = tryLoadTip("Silenced");
    public EUIKeywordTooltip slow = tryLoadTip("Slow");
    public EUIKeywordTooltip sorcery = tryLoadTip("Sorcery");
    public EUIKeywordTooltip soulbound = tryLoadTip("Soulbound");
    public EUIKeywordTooltip sporeCloud = tryLoadTip("Spore Cloud");
    public EUIKeywordTooltip spread = tryLoadTip("Spread");
    public EUIKeywordTooltip stabilize = tryLoadTip("Stabilize");
    public EUIKeywordTooltip stance = tryLoadTip("Stance");
    public EUIKeywordTooltip starter = tryLoadTip("Starter");
    public EUIKeywordTooltip startup = tryLoadTip("Startup");
    public EUIKeywordTooltip steal = tryLoadTip("Steal");
    public EUIKeywordTooltip strength = tryLoadTip("Strength");
    public EUIKeywordTooltip stun = tryLoadTip("Stun");
    public EUIKeywordTooltip summon = tryLoadTip("Summon");
    public EUIKeywordTooltip summonSlot = tryLoadTip("Summon Slot");
    public EUIKeywordTooltip supercharged = tryLoadTip("Supercharged");
    public EUIKeywordTooltip suspensive = tryLoadTip("Suspensive");
    public EUIKeywordTooltip tempHP = tryLoadTip("Temporary HP");
    public EUIKeywordTooltip thorns = tryLoadTip("Thorns");
    public EUIKeywordTooltip thousandCuts = tryLoadTip("Thousand Cuts");
    public EUIKeywordTooltip timeWarp = tryLoadTip("Time Warp");
    public EUIKeywordTooltip timing = tryLoadTip("Timing");
    public EUIKeywordTooltip toxicology = tryLoadTip("Toxicology");
    public EUIKeywordTooltip transform = tryLoadTip("Transform");
    public EUIKeywordTooltip trigger = tryLoadTip("Trigger");
    public EUIKeywordTooltip turnEnd = tryLoadTip("Turn End Timing");
    public EUIKeywordTooltip turnStart = tryLoadTip("Turn Start Timing");
    public EUIKeywordTooltip unique = tryLoadTip("Unique");
    public EUIKeywordTooltip unplayable = tryLoadTip("Unplayable");
    public EUIKeywordTooltip vigor = tryLoadTip("Vigor");
    public EUIKeywordTooltip vitality = tryLoadTip("Vitality");
    public EUIKeywordTooltip vulnerable = tryLoadTip("Vulnerable");
    public EUIKeywordTooltip warding = tryLoadTip("Warding");
    public EUIKeywordTooltip weak = tryLoadTip("Weak");
    public EUIKeywordTooltip withdraw = tryLoadTip("Withdraw");
    public EUIKeywordTooltip wrath = tryLoadTip("Wrath");

    public EUIKeywordTooltip gold = new EUIKeywordTooltip(RewardItem.TEXT[1].trim());

    public PCLCoreTooltips() {
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
        critical.formatDescription(CriticalPower.MULTIPLIER);
        fortified.formatDescription(FortifiedPower.MULTIPLIER);
        impaired.formatDescription(ImpairedPower.MULTIPLIER);
        invigorated.formatDescription(InvigoratedPower.MULTIPLIER);
        lockOn.formatDescription(PCLLockOnPower.BASE);
        provoked.formatDescription(ProvokedPower.ATTACK_MULTIPLIER);
        withdraw.formatDescription(SummonPool.BASE_DAMAGE_BONUS);

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

        attack.setIcon(EUIGameUtils.iconForType(AbstractCard.CardType.ATTACK));
        skill.setIcon(EUIGameUtils.iconForType(AbstractCard.CardType.SKILL));
        power.setIcon(EUIGameUtils.iconForType(AbstractCard.CardType.POWER));
        curse.setIcon(EUIGameUtils.iconForType(AbstractCard.CardType.CURSE));
        status.setIcon(EUIGameUtils.iconForType(AbstractCard.CardType.STATUS));
        summon.setIcon(EUIGameUtils.iconForType(PCLEnum.CardType.SUMMON));

        gold.setIcon(PCLCoreImages.Tooltips.gold.texture(), 6);
        orbSlot.setIcon(PCLCoreImages.Tooltips.orbSlot.texture(), 6);

        for (PCLCardTag tag : PCLCardTag.values()) {
            tag.getTooltip().setIcon(tag.getTextureCache().texture(), 6).setBadgeBackground(tag.color);
        }
        PCLAffinity.loadIconsIntoKeywords();
        PCLOrbData.loadIconsIntoKeywords();
        PCLPowerData.loadIconsIntoKeywords();
        EUIKeywordTooltip.updateTooltipIcons();

        // Refresh the descriptions of all cards to account for loaded icons
        for (AbstractCard c : CardLibrary.getAllCards()) {
            if (c instanceof PCLCard) {
                c.initializeDescription();
            }
        }
    }
}
