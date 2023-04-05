package pinacolada.cards.base;

import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.utilities.ColoredTexture;
import pinacolada.cards.base.fields.*;
import pinacolada.cards.base.tags.CardTagItem;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrigger;
import pinacolada.skills.skills.special.primary.PCardPrimary_DealDamage;
import pinacolada.skills.skills.special.primary.PCardPrimary_GainBlock;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PCLDynamicData extends PCLCardData
{
    private static final TypeToken<HashMap<Settings.GameLanguage, CardStrings>> TStrings = new TypeToken<HashMap<Settings.GameLanguage, CardStrings>>() {};
    public final HashMap<Settings.GameLanguage, CardStrings> languageMap = new HashMap<>();
    public final ArrayList<PSkill<?>> moves = new ArrayList<>();
    public final ArrayList<PTrigger> powers = new ArrayList<>();
    public ColoredTexture portraitForeground;
    public ColoredTexture portraitImage;
    public PCLCard source;
    public PCardPrimary_DealDamage attackSkill;
    public PCardPrimary_GainBlock blockSkill;
    public boolean linearUpgrade;
    public boolean showTypeText = true;

    public static HashMap<Settings.GameLanguage, CardStrings> parseLanguageStrings(String languageStrings)
    {
        return EUIUtils.deserialize(languageStrings, TStrings.getType());
    }

    public static CardStrings getStringsForLanguage(HashMap<Settings.GameLanguage, CardStrings> languageMap)
    {
        return getStringsForLanguage(languageMap, Settings.language);
    }

    public static CardStrings getStringsForLanguage(HashMap<Settings.GameLanguage, CardStrings> languageMap, Settings.GameLanguage language)
    {
        return languageMap.getOrDefault(language,
                languageMap.getOrDefault(Settings.GameLanguage.ENG,
                        languageMap.size() > 0 ? languageMap.entrySet().iterator().next().getValue() : getInitialStrings()));
    }

    protected static CardStrings getInitialStrings()
    {
        CardStrings retVal = new CardStrings();
        retVal.NAME = GameUtilities.EMPTY_STRING;
        retVal.DESCRIPTION = GameUtilities.EMPTY_STRING;
        retVal.UPGRADE_DESCRIPTION = GameUtilities.EMPTY_STRING;
        retVal.EXTENDED_DESCRIPTION = new String[]{};
        return retVal;
    }

    // Prevent a tag info with an invalid tag from being loaded
    protected static PCLCardTagInfo getSafeTag(String value)
    {
        PCLCardTagInfo info = EUIUtils.deserialize(value, PCLCardTagInfo.class);
        return info.tag != null ? info : null;
    }

    public PCLDynamicData(String id)
    {
        super(PCLDynamicCard.class, PGR.core, id, null);
    }

    public PCLDynamicData(String id, PCLResources<?,?,?,?> resources)
    {
        super(PCLDynamicCard.class, resources, id, null);
    }

    public PCLDynamicData(PCLCard card, boolean copyProperties)
    {
        this(card, card.name, "", copyProperties);
    }

    public PCLDynamicData(PCLCard card, String text, boolean copyProperties)
    {
        this(card, card.name, text, copyProperties);
    }

    public PCLDynamicData(PCLCard card, String name, String text, boolean copyProperties)
    {
        this(card.cardData, name, text, copyProperties);
        this.source = card;
        setImage(card.portraitImg, card.portraitForeground);
        if (copyProperties)
        {
            setPSkill(card.getEffects(), true, true);
            setPPower(card.getPowerEffects(), true, true);
        }
    }

    public PCLDynamicData(PCLCardData card, boolean copyProperties)
    {
        this(card, card.strings.NAME, "", copyProperties);
    }

    public PCLDynamicData(PCLCardData card, String text, boolean copyProperties)
    {
        this(card, card.strings.NAME, text, copyProperties);
    }

    public PCLDynamicData(PCLCardData original, String name, String text, boolean copyProperties)
    {
        this(original.ID, original.resources);

        if (copyProperties)
        {
            setNumbers(original);
            setAttackType(original.attackType);
            setTarget(original.cardTarget);
            setTags(original.tags);
            setMaxUpgrades(original.maxUpgradeLevel);
            setMaxCopies(original.maxCopies);
            setUnique(original.unique);
            setRemovableFromDeck(original.removableFromDeck);
            affinities = new PCLCardDataAffinityGroup(original.affinities);
        }

        setImagePath(original.imagePath);

        setColor(original.cardColor);
        setRarity(original.cardRarity);
        setType(original.cardType);
        setLoadout(original.loadout);
        setText(name, text, text);
    }

    public PCLDynamicData(PCLDynamicData original)
    {
        this(original, true);
        this.source = original.source;

        setImage(original.portraitImage, original.portraitForeground);
        setLanguageMap(original.languageMap);
        setLinearUpgrade(original.linearUpgrade);
        setPSkill(original.moves, true, true);
        setPPower(original.powers, true, true);
        if (original.attackSkill != null)
        {
            setAttackSkill(original.attackSkill.makeCopy());
        }
        if (original.blockSkill != null)
        {
            setBlockSkill(original.blockSkill.makeCopy());
        }
    }

    public PCLDynamicData(PCLCustomCardSlot data, PCLCustomCardSlot.CardForm f)
    {
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
        safeLoadValue(() -> setTags(EUIUtils.map(data.tags, PCLDynamicData::getSafeTag)));
        if (data.loadout != null)
        {
             setLoadout(PCLLoadout.get(data.slotColor, data.loadout));
        }
        safeLoadValue(() -> setAffinities(EUIUtils.deserialize(data.affinities, PCLCardDataAffinityGroup.class)));
        safeLoadValue(() -> setMaxUpgrades(data.maxUpgradeLevel));
        safeLoadValue(() -> setMaxCopies(data.maxCopies));
        safeLoadValue(() -> setUnique(data.unique));
        safeLoadValue(() -> setRemovableFromDeck(data.removableFromDeck));
        safeLoadValue(() -> setLinearUpgrade(data.linearUpgrade));
        safeLoadValue(() -> setTarget(PCLCardTarget.valueOf(f.target)));
        safeLoadValue(() -> setAttackType(PCLAttackType.valueOf(f.attackType)));
        if (f.damageEffect != null)
        {
            safeLoadValue(() -> setAttackSkill(EUIUtils.safeCast(PSkill.get(f.damageEffect), PCardPrimary_DealDamage.class)));
        }
        if (f.blockEffect != null)
        {
            safeLoadValue(() -> setBlockSkill(EUIUtils.safeCast(PSkill.get(f.blockEffect), PCardPrimary_GainBlock.class)));
        }
        safeLoadValue(() -> setPSkill(EUIUtils.mapAsNonnull(f.effects, PSkill::get), true, true));
        safeLoadValue(() -> setPPower(EUIUtils.mapAsNonnull(f.powerEffects, pe -> EUIUtils.safeCast(PSkill.get(pe), PTrigger.class))));
        setMultiformData(data.forms.length);
    }

    private void safeLoadValue(ActionT0 loadFunc)
    {
        try
        {
            loadFunc.invoke();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            EUIUtils.logError(this, "Failed to load field: " + e.getMessage());
        }
    }

    public PCLDynamicCard createImpl()
    {
        return createImplWithForms(true);
    }

    public PCLDynamicCard createImplWithForms(boolean shouldFindForms)
    {
        setTextForLanguage();

        if (imagePath == null)
        {
            imagePath = QuestionMark.DATA.imagePath;
        }

        return new PCLDynamicCard(this, shouldFindForms);
    }

    public CardStrings getStringsForLanguage(Settings.GameLanguage language)
    {
        return languageMap.getOrDefault(language,
                languageMap.getOrDefault(Settings.GameLanguage.ENG,
                        languageMap.size() > 0 ? languageMap.entrySet().iterator().next().getValue() : getInitialStrings()));
    }

    @Override
    public AbstractCard makeCopyFromLibrary(int upgrade)
    {
        return create(upgrade);
    }

    public PCLDynamicData setImagePath(String imagePath)
    {
        this.imagePath = imagePath;

        return this;
    }

    public PCLDynamicData removePMove(PSkill<?> effect)
    {
        moves.remove(effect);
        return this;
    }

    public PCLDynamicData removePowerEffect(PTrigger effect)
    {
        powers.remove(effect);
        return this;
    }

    public PCLDynamicData setAffinity(PCLAffinity affinity, int level)
    {
        this.affinities.set(affinity, level);

        return this;
    }

    public PCLDynamicData setAttackType(PCLAttackType attackType)
    {
        this.attackType = attackType;

        return this;
    }

    public PCLDynamicData setDescription(String description)
    {
        this.strings.DESCRIPTION = description;

        return this;
    }

    public PCLDynamicData setExtraTags(List<CardTagItem> extraTags)
    {
        this.extraTags = extraTags;

        return this;
    }

    public PCLDynamicData setID(String id)
    {
        this.ID = id;
        return this;
    }

    public PCLDynamicData setImage(ColoredTexture portraitImage)
    {
        this.portraitImage = portraitImage;

        return this;
    }

    public PCLDynamicData setImage(ColoredTexture portraitImage, ColoredTexture portraitForeground)
    {
        this.portraitImage = portraitImage;
        this.portraitForeground = portraitForeground;

        return this;
    }

    public PCLDynamicData setLanguageMap(HashMap<Settings.GameLanguage, CardStrings> languageMap)
    {
        this.languageMap.putAll(languageMap);
        return setTextForLanguage();
    }

    public PCLDynamicData setLanguageMapEntry(Settings.GameLanguage language)
    {
        this.languageMap.put(language, this.strings);
        return this;
    }

    public PCLDynamicData setName(String name)
    {
        this.strings.NAME = name;

        return this;
    }

    public PCLDynamicData setAttackSkill(PCardPrimary_DealDamage damageEffect)
    {
        this.attackSkill = damageEffect;

        return this;
    }

    public PCLDynamicData setBlockSkill(PCardPrimary_GainBlock blockEffect)
    {
        this.blockSkill = blockEffect;

        return this;
    }

    public PCLDynamicData setPSkill(Iterable<PSkill<?>> currentEffects)
    {
        return setPSkill(currentEffects, false, true);
    }

    public PCLDynamicData setPSkill(Iterable<PSkill<?>> currentEffects, boolean makeCopy, boolean clear)
    {
        if (clear)
        {
            moves.clear();
        }
        for (PSkill<?> be : currentEffects)
        {
            addPSkill(be, makeCopy);
        }
        return this;
    }

    public PCLDynamicData setPSkill(PSkill<?>... effect)
    {
        return setPSkill(Arrays.asList(effect));
    }

    public PCLDynamicData addPSkill(PSkill<?> effect)
    {
        return addPSkill(effect, false);
    }

    public PCLDynamicData addPSkill(PSkill<?> effect, boolean makeCopy)
    {
        if (makeCopy && effect != null)
        {
            effect = effect.makeCopy();
        }
        moves.add(effect);

        return this;
    }

    public PCLDynamicData setPPower(Iterable<PTrigger> currentEffects)
    {
        return setPPower(currentEffects, false, true);
    }

    public PCLDynamicData setPPower(Iterable<PTrigger> currentEffects, boolean makeCopy, boolean clear)
    {
        if (clear)
        {
            powers.clear();
        }
        for (PTrigger be : currentEffects)
        {
            addPPower(be, makeCopy);
        }
        return this;
    }

    public PCLDynamicData setPPower(PTrigger... effect)
    {
        return setPPower(Arrays.asList(effect));
    }

    public PCLDynamicData addPPower(PTrigger effect)
    {
        return addPPower(effect, false);
    }

    public PCLDynamicData addPPower(PTrigger effect, boolean makeCopy)
    {
        if (makeCopy && effect != null)
        {
            effect = effect.makeCopy();
        }
        powers.add(effect);

        return this;
    }

    public PCLDynamicData setColor(AbstractCard.CardColor color)
    {
        super.setColor(color);
        return this;
    }

    public PCLDynamicData setLinearUpgrade(boolean linearUpgrade)
    {
        this.linearUpgrade = linearUpgrade;

        return this;
    }

    public PCLDynamicData setRarity(AbstractCard.CardRarity rarity)
    {
        this.cardRarity = rarity;
        return this;
    }

    public PCLDynamicData setType(AbstractCard.CardType type)
    {
        this.cardType = type;
        return this;
    }

    public PCLDynamicData setText(CardStrings cardStrings)
    {
        return setText(cardStrings.NAME, cardStrings.DESCRIPTION, cardStrings.UPGRADE_DESCRIPTION);
    }

    public PCLDynamicData setText(String name)
    {
        return setText(name, "", "", new String[0]);
    }

    public PCLDynamicData setText(String name, String description, String upgradeDescription)
    {
        return setText(name, description, upgradeDescription != null ? upgradeDescription : description, new String[0]);
    }

    public PCLDynamicData setText(String name, String description, String upgradeDescription, String[] extendedDescription)
    {
        this.strings.NAME = name;
        this.strings.DESCRIPTION = description;
        this.strings.UPGRADE_DESCRIPTION = upgradeDescription;
        this.strings.EXTENDED_DESCRIPTION = extendedDescription;

        return this;
    }

    public PCLDynamicData setTextForLanguage()
    {
        return setTextForLanguage(Settings.language);
    }

    public PCLDynamicData setTextForLanguage(Settings.GameLanguage language)
    {
        return setText(getStringsForLanguage(language));
    }

    public PCLDynamicData showTypeText(boolean showTypeText)
    {
        this.showTypeText = showTypeText;

        return this;
    }
}