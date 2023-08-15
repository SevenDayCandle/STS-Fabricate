package pinacolada.cards.base;

import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import extendedui.EUIUtils;
import extendedui.utilities.ColoredTexture;
import pinacolada.cards.base.fields.*;
import pinacolada.cards.base.tags.CardFlag;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.skills.PSkill;
import pinacolada.skills.delay.DelayTiming;
import pinacolada.skills.skills.PTrigger;
import pinacolada.skills.skills.special.primary.PCardPrimary_DealDamage;
import pinacolada.skills.skills.special.primary.PCardPrimary_GainBlock;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLDynamicCardData extends PCLCardData implements EditorMaker {
    private static final TypeToken<HashMap<Settings.GameLanguage, CardStrings>> TStrings = new TypeToken<HashMap<Settings.GameLanguage, CardStrings>>() {
    };
    public final HashMap<Settings.GameLanguage, CardStrings> languageMap = new HashMap<>();
    public final ArrayList<PSkill<?>> moves = new ArrayList<>();
    public final ArrayList<PTrigger> powers = new ArrayList<>();
    public ColoredTexture portraitForeground;
    public ColoredTexture portraitImage;
    public PCLCard source;
    public PCardPrimary_DealDamage attackSkill;
    public PCardPrimary_GainBlock blockSkill;
    public boolean showTypeText = true;

    public PCLDynamicCardData(String id) {
        super(PCLDynamicCard.class, PGR.core, id, null);
    }

    public PCLDynamicCardData(String id, PCLResources<?, ?, ?, ?> resources) {
        super(PCLDynamicCard.class, resources, id, null);
    }

    public PCLDynamicCardData(PCLCard card, boolean copyProperties) {
        this(card, card.name, "", copyProperties);
    }

    public PCLDynamicCardData(PCLCard card, String name, String text, boolean copyProperties) {
        this(card.cardData, name, text, copyProperties);
        this.source = card;
        setImage(card.portraitImg, card.portraitForeground);
        if (copyProperties) {
            setPSkill(card.getEffects(), true, true);
            setPPower(card.getPowerEffects(), true, true);
        }
    }

    public PCLDynamicCardData(PCLCard card, String text, boolean copyProperties) {
        this(card, card.name, text, copyProperties);
    }

    public PCLDynamicCardData(PCLCardData card, boolean copyProperties) {
        this(card, card.strings.NAME, "", copyProperties);
    }

    public PCLDynamicCardData(PCLCardData card, String text, boolean copyProperties) {
        this(card, card.strings.NAME, text, copyProperties);
    }

    public PCLDynamicCardData(PCLDynamicCardData original) {
        this(original, true);
        this.source = original.source;

        setImage(original.portraitImage, original.portraitForeground);
        setLanguageMap(original.languageMap);
        setPSkill(original.moves, true, true);
        setPPower(original.powers, true, true);
        if (original.attackSkill != null) {
            setAttackSkill(original.attackSkill.makeCopy());
        }
        if (original.blockSkill != null) {
            setBlockSkill(original.blockSkill.makeCopy());
        }
    }

    public PCLDynamicCardData(PCLCardData original, String name, String text, boolean copyProperties) {
        this(original.ID, original.resources);

        if (copyProperties) {
            setNumbers(original);
            setAttackType(original.attackType);
            setTarget(original.cardTarget);
            setTiming(original.timing);
            setTags(original.tags);
            setMaxUpgrades(original.maxUpgradeLevel);
            setMaxCopies(original.maxCopies);
            setUnique(original.unique);
            setBranchFactor(original.branchFactor);
            setRemovableFromDeck(original.removableFromDeck);
            affinities = new PCLCardDataAffinityGroup(original.affinities);
        }

        setImagePath(original.imagePath);
        setColor(original.cardColor);
        setRarity(original.cardRarity);
        setType(original.cardType);
        setLoadout(original.loadout);
        setFlags(original.flags);
        setText(name, text, text);
    }

    public PCLDynamicCardData(PCLCustomCardSlot data, PCLCustomCardSlot.CardForm f) {
        this(data.ID);
        safeLoadValue(() -> setColor(data.slotColor));
        safeLoadValue(() -> setRarity(AbstractCard.CardRarity.valueOf(data.rarity)));
        safeLoadValue(() -> setType(AbstractCard.CardType.valueOf(data.type)));
        safeLoadValue(() -> damage = data.damage.clone());
        safeLoadValue(() -> damageUpgrade = data.damageUpgrade.clone());
        safeLoadValue(() -> block = data.block.clone());
        safeLoadValue(() -> blockUpgrade = data.blockUpgrade.clone());
        safeLoadValue(() -> magicNumber = data.tempHP.clone());
        safeLoadValue(() -> magicNumberUpgrade = data.tempHPUpgrade.clone());
        safeLoadValue(() -> hp = data.heal.clone());
        safeLoadValue(() -> hpUpgrade = data.healUpgrade.clone());
        safeLoadValue(() -> hitCount = data.hitCount.clone());
        safeLoadValue(() -> hitCountUpgrade = data.hitCountUpgrade.clone());
        safeLoadValue(() -> rightCount = data.rightCount.clone());
        safeLoadValue(() -> rightCountUpgrade = data.rightCountUpgrade.clone());
        safeLoadValue(() -> cost = data.cost.clone());
        safeLoadValue(() -> costUpgrade = data.costUpgrade.clone());
        safeLoadValue(() -> setLanguageMap(parseLanguageStrings(data.languageStrings)));
        safeLoadValue(() -> setTags(EUIUtils.mapAsNonnull(data.tags, PCLDynamicCardData::getSafeTag)));
        safeLoadValue(() -> setFlags(EUIUtils.mapAsNonnull(data.flags, CardFlag::get)));
        if (data.loadout != null) {
            setLoadout(PCLLoadout.get(data.loadout));
        }
        if (data.affinities != null) {
            safeLoadValue(() -> setAffinities(EUIUtils.deserialize(data.affinities, PCLCardDataAffinityGroup.class)));
        }
        safeLoadValue(() -> setMaxUpgrades(data.maxUpgradeLevel));
        safeLoadValue(() -> setMaxCopies(data.maxCopies));
        safeLoadValue(() -> setUnique(data.unique));
        safeLoadValue(() -> setRemovableFromDeck(data.removableFromDeck));
        safeLoadValue(() -> setBranchFactor(data.branchUpgradeFactor));
        safeLoadValue(() -> setTarget(PCLCardTarget.valueOf(f.target)));
        safeLoadValue(() -> setTiming(DelayTiming.valueOf(f.timing)));
        safeLoadValue(() -> setAttackType(PCLAttackType.valueOf(f.attackType)));
        if (f.damageEffect != null) {
            safeLoadValue(() -> setAttackSkill(EUIUtils.safeCast(PSkill.get(f.damageEffect), PCardPrimary_DealDamage.class)));
        }
        if (f.blockEffect != null) {
            safeLoadValue(() -> setBlockSkill(EUIUtils.safeCast(PSkill.get(f.blockEffect), PCardPrimary_GainBlock.class)));
        }
        safeLoadValue(() -> setPSkill(EUIUtils.mapAsNonnull(f.effects, PSkill::get), true, true));
        safeLoadValue(() -> setPPower(EUIUtils.mapAsNonnull(f.powerEffects, pe -> EUIUtils.safeCast(PSkill.get(pe), PTrigger.class))));
        setMultiformData(data.forms.length);
    }

    protected static CardStrings getInitialStrings() {
        CardStrings retVal = new CardStrings();
        retVal.NAME = GameUtilities.EMPTY_STRING;
        retVal.DESCRIPTION = GameUtilities.EMPTY_STRING;
        retVal.UPGRADE_DESCRIPTION = GameUtilities.EMPTY_STRING;
        retVal.EXTENDED_DESCRIPTION = new String[]{};
        return retVal;
    }

    // Prevent a tag info with an invalid tag from being loaded
    protected static PCLCardTagInfo getSafeTag(String value) {
        PCLCardTagInfo info = EUIUtils.deserialize(value, PCLCardTagInfo.class);
        return info.tag != null ? info : null;
    }

    public static CardStrings getStringsForLanguage(HashMap<Settings.GameLanguage, CardStrings> languageMap) {
        return getStringsForLanguage(languageMap, Settings.language);
    }

    public static CardStrings getStringsForLanguage(HashMap<Settings.GameLanguage, CardStrings> languageMap, Settings.GameLanguage language) {
        return languageMap.getOrDefault(language,
                languageMap.getOrDefault(Settings.GameLanguage.ENG,
                        languageMap.size() > 0 ? languageMap.entrySet().iterator().next().getValue() : getInitialStrings()));
    }

    public static HashMap<Settings.GameLanguage, CardStrings> parseLanguageStrings(String languageStrings) {
        return EUIUtils.deserialize(languageStrings, TStrings.getType());
    }

    public PCLDynamicCard create() {
        return createImplWithForms(0, 0, true);
    }

    public PCLDynamicCard createImplWithForms(int form, int timesUpgraded, boolean shouldFindForms) {
        return createImplWithForms(form, timesUpgraded, shouldFindForms, true);
    }

    public PCLDynamicCard createImplWithForms(int form, int timesUpgraded, boolean shouldFindForms, boolean shouldSetTextures) {
        setTextForLanguage();

        if (imagePath == null) {
            imagePath = QuestionMark.DATA.imagePath;
        }

        return new PCLDynamicCard(this, form, timesUpgraded, shouldFindForms, shouldSetTextures);
    }

    @Override
    public AbstractCard.CardColor getCardColor() {
        return cardColor;
    }

    @Override
    public ArrayList<PSkill<?>> getMoves() {
        return moves;
    }

    @Override
    public ArrayList<PTrigger> getPowers() {
        return powers;
    }

    public CardStrings getStringsForLanguage(Settings.GameLanguage language) {
        return languageMap.getOrDefault(language,
                languageMap.getOrDefault(Settings.GameLanguage.ENG,
                        languageMap.size() > 0 ? languageMap.entrySet().iterator().next().getValue() : getInitialStrings()));
    }

    @Override
    public void initializeImage() {
    }

    @Override
    public AbstractCard makeCardFromLibrary(int upgrade) {
        return create(upgrade);
    }

    @Override
    public PCLDynamicCardData makeCopy() {
        return new PCLDynamicCardData(this);
    }

    public PCLDynamicCardData removePMove(PSkill<?> effect) {
        moves.remove(effect);
        return this;
    }

    public PCLDynamicCardData removePowerEffect(PTrigger effect) {
        powers.remove(effect);
        return this;
    }

    public PCLDynamicCardData setAffinity(PCLAffinity affinity, int level) {
        this.affinities.set(affinity, level);

        return this;
    }

    public PCLDynamicCardData setAttackSkill(PCardPrimary_DealDamage damageEffect) {
        this.attackSkill = damageEffect;

        return this;
    }

    public PCLDynamicCardData setAttackType(PCLAttackType attackType) {
        this.attackType = attackType;

        return this;
    }

    public PCLDynamicCardData setBlockSkill(PCardPrimary_GainBlock blockEffect) {
        this.blockSkill = blockEffect;

        return this;
    }

    public PCLDynamicCardData setColor(AbstractCard.CardColor color) {
        super.setColor(color);
        return this;
    }

    public PCLDynamicCardData setDescription(String description) {
        this.strings.DESCRIPTION = description;

        return this;
    }

    public PCLDynamicCardData setFlags(List<CardFlag> flags) {
        this.flags = flags == null || flags.isEmpty() ? null : new ArrayList<>(flags);

        return this;
    }

    public PCLDynamicCardData setID(String id) {
        this.ID = id;
        return this;
    }

    public PCLDynamicCardData setImage(ColoredTexture portraitImage, ColoredTexture portraitForeground) {
        this.portraitImage = portraitImage;
        this.portraitForeground = portraitForeground;

        return this;
    }

    public PCLDynamicCardData setImage(ColoredTexture portraitImage) {
        this.portraitImage = portraitImage;

        return this;
    }

    public PCLDynamicCardData setImagePath(String imagePath) {
        this.imagePath = imagePath;

        return this;
    }

    public PCLDynamicCardData setLanguageMap(HashMap<Settings.GameLanguage, CardStrings> languageMap) {
        this.languageMap.putAll(languageMap);
        return setTextForLanguage();
    }

    public PCLDynamicCardData setLanguageMapEntry(Settings.GameLanguage language) {
        this.languageMap.put(language, this.strings);
        return this;
    }

    public PCLDynamicCardData setName(String name) {
        this.strings.NAME = name;

        return this;
    }

    public PCLDynamicCardData setRarity(AbstractCard.CardRarity rarity) {
        this.cardRarity = rarity;
        return this;
    }

    public PCLDynamicCardData setText(String name, String description, String upgradeDescription) {
        return setText(name, description, upgradeDescription != null ? upgradeDescription : description, new String[0]);
    }

    public PCLDynamicCardData setText(String name, String description, String upgradeDescription, String[] extendedDescription) {
        this.strings.NAME = name;
        this.strings.DESCRIPTION = description;
        this.strings.UPGRADE_DESCRIPTION = upgradeDescription;
        this.strings.EXTENDED_DESCRIPTION = extendedDescription;

        return this;
    }

    public PCLDynamicCardData setText(CardStrings cardStrings) {
        return setText(cardStrings.NAME, cardStrings.DESCRIPTION, cardStrings.UPGRADE_DESCRIPTION);
    }

    public PCLDynamicCardData setText(String name) {
        return setText(name, "", "", new String[0]);
    }

    public PCLDynamicCardData setTextForLanguage() {
        return setTextForLanguage(Settings.language);
    }

    public PCLDynamicCardData setTextForLanguage(Settings.GameLanguage language) {
        return setText(getStringsForLanguage(language));
    }

    public PCLDynamicCardData setType(AbstractCard.CardType type) {
        this.cardType = type;
        return this;
    }

    public PCLDynamicCardData showTypeText(boolean showTypeText) {
        this.showTypeText = showTypeText;

        return this;
    }
}