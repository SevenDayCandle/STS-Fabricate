package pinacolada.cards.base;

import com.badlogic.gdx.graphics.Texture;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import extendedui.EUIUtils;
import extendedui.utilities.ColoredTexture;
import pinacolada.cards.base.fields.*;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.skills.PSkill;
import pinacolada.skills.delay.DelayTiming;
import pinacolada.skills.skills.PTrigger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLDynamicCardData extends PCLCardData implements EditorMaker<PCLDynamicCard, CardStrings> {
    private static final TypeToken<HashMap<Settings.GameLanguage, CardStrings>> TStrings = new TypeToken<HashMap<Settings.GameLanguage, CardStrings>>() {
    };
    public final HashMap<Settings.GameLanguage, CardStrings> languageMap = new HashMap<>();
    public final ArrayList<PSkill<?>> moves = new ArrayList<>();
    public final ArrayList<PSkill<?>> powers = new ArrayList<>();
    public ColoredTexture portraitForeground;
    public ColoredTexture portraitImage;
    public PCLCard source;

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
        setLoadoutValue(original.getLoadoutValue());
        setSlots(original.slots);
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
        safeLoadValue(() -> parseLanguageStrings(data.languageStrings, f));
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
        clearPowers();
        clearSkills();
        // Legacy damage/block
        if (f.damageEffect != null) {
            safeLoadValue(() -> addPSkill(PSkill.get(f.damageEffect)));
        }
        if (f.blockEffect != null) {
            safeLoadValue(() -> addPSkill(PSkill.get(f.blockEffect)));
        }
        safeLoadValue(() -> setPSkill(EUIUtils.mapAsNonnull(f.effects, PSkill::get), true, false));
        safeLoadValue(() -> setPPower(EUIUtils.mapAsNonnull(f.powerEffects, PSkill::get), true, false));

        setMultiformData(data.forms.length);
        safeLoadValue(() -> setLoadoutValue(data.loadoutValue));
        // TODO convert to safeLoadValue
        if (data.augmentSlots != null) {
            setSlots(data.augmentSlots);
        }
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
                        !languageMap.isEmpty() ? languageMap.entrySet().iterator().next().getValue() : getInitialStrings()));
    }

    public PCLDynamicCard create() {
        return createImplWithForms(0, 0);
    }

    public PCLDynamicCard createImplWithForms(int form, int timesUpgraded) {
        return createImplWithForms(form, timesUpgraded, true);
    }

    public PCLDynamicCard createImplWithForms(int form, int timesUpgraded, boolean shouldSetTextures) {
        setTextForLanguage();

        if (imagePath == null) {
            imagePath = QuestionMark.DATA.imagePath;
        }

        return new PCLDynamicCard(this, form, timesUpgraded, shouldSetTextures);
    }

    @Override
    public AbstractCard.CardColor getCardColor() {
        return cardColor;
    }

    @Override
    public CardStrings getDefaultStrings() {
        return getInitialStrings();
    }

    @Override
    public String[] getDescString() {
        return getDescString(getStringsForLanguage(Settings.language));
    }

    @Override
    public String[] getDescString(CardStrings item) {
        return item.EXTENDED_DESCRIPTION;
    }

    @Override
    public Texture getImage() {
        return portraitImage != null ? portraitImage.texture : null;
    }

    @Override
    public ArrayList<PSkill<?>> getMoves() {
        return moves;
    }

    @Override
    public ArrayList<PSkill<?>> getPowers() {
        return powers;
    }

    @Override
    public CardStrings getStrings() {
        return strings;
    }

    @Override
    public HashMap<Settings.GameLanguage, CardStrings> getLanguageMap() {
        return languageMap;
    }

    public CardStrings getStringsForLanguage(Settings.GameLanguage language) {
        return languageMap.getOrDefault(language,
                languageMap.getOrDefault(Settings.GameLanguage.ENG,
                        !languageMap.isEmpty() ? languageMap.entrySet().iterator().next().getValue() : getInitialStrings()));
    }

    @Override
    public void initializeImage() {
    }

    @Override
    public PCLDynamicCardData makeCopy() {
        return new PCLDynamicCardData(this);
    }

    @Override
    public AbstractCard makeUpgradedCardCopy(int upgrade) {
        return create(upgrade);
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

    public PCLDynamicCardData setAttackType(PCLAttackType attackType) {
        this.attackType = attackType;

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

    // Do not actually add this card to the available loadout cards
    @Override
    public PCLDynamicCardData setLoadout(PCLLoadout loadout, boolean colorless) {
        this.loadout = loadout;
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

    @Override
    public PCLDynamicCardData setText(CardStrings cardStrings) {
        return setText(cardStrings.NAME, cardStrings.DESCRIPTION, cardStrings.UPGRADE_DESCRIPTION, cardStrings.EXTENDED_DESCRIPTION);
    }

    public PCLDynamicCardData setText(String name) {
        return setText(name, "", "", new String[0]);
    }

    @Override
    public CardStrings copyStrings(CardStrings initial) {
        return copyStrings(initial, new CardStrings());
    }

    @Override
    public CardStrings copyStrings(CardStrings initial, CardStrings dest) {
        dest.NAME = initial.NAME;
        dest.DESCRIPTION = initial.DESCRIPTION;
        dest.UPGRADE_DESCRIPTION = initial.UPGRADE_DESCRIPTION;
        if (initial.EXTENDED_DESCRIPTION != null) {
            dest.EXTENDED_DESCRIPTION = initial.EXTENDED_DESCRIPTION.clone();
        }
        else {
            dest.EXTENDED_DESCRIPTION = null;
        }
        return dest;
    }

    public PCLDynamicCardData setType(AbstractCard.CardType type) {
        this.cardType = type;
        return this;
    }

    @Override
    public Type typeToken() {
        return TStrings.getType();
    }
}