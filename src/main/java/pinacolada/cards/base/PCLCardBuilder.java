package pinacolada.cards.base;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import extendedui.EUIUtils;
import extendedui.utilities.ColoredTexture;
import pinacolada.cards.base.fields.CardTagItem;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLLoadout;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrigger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PCLCardBuilder extends PCLCardData
{
    private static final TypeToken<HashMap<Settings.GameLanguage, CardStrings>> TStrings = new TypeToken<HashMap<Settings.GameLanguage, CardStrings>>() {};
    public final HashMap<Settings.GameLanguage, CardStrings> languageMap = new HashMap<>();
    public final ArrayList<PSkill<?>> moves = new ArrayList<>();
    public final ArrayList<PTrigger> powers = new ArrayList<>();
    public AbstractGameAction.AttackEffect attackEffect = AbstractGameAction.AttackEffect.NONE;
    public ColoredTexture portraitForeground;
    public ColoredTexture portraitImage;
    public List<CardTagItem> extraTags = new ArrayList<>();
    public PCLCard source;
    public TextureAtlas.AtlasRegion fakePortrait;
    public boolean showTypeText = true;

    public PCLCardBuilder(String id)
    {
        super(PCLDynamicCard.class, PGR.core, id, null);
    }

    public PCLCardBuilder(String id, PCLResources<?,?,?> resources)
    {
        super(PCLDynamicCard.class, resources, id, null);
    }

    public PCLCardBuilder(PCLCard card, boolean copyProperties)
    {
        this(card, card.name, "", copyProperties);
    }

    public PCLCardBuilder(PCLCard card, String text, boolean copyProperties)
    {
        this(card, card.name, text, copyProperties);
    }

    public PCLCardBuilder(PCLCard card, String name, String text, boolean copyProperties)
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

    public PCLCardBuilder(PCLCardData card, boolean copyProperties)
    {
        this(card, card.strings.NAME, "", copyProperties);
    }

    public PCLCardBuilder(PCLCardData card, String text, boolean copyProperties)
    {
        this(card, card.strings.NAME, text, copyProperties);
    }

    public PCLCardBuilder(PCLCardData original, String name, String text, boolean copyProperties)
    {
        this(original.ID, original.resources);

        if (copyProperties)
        {
            setNumbers(original);
            setMaxUpgrades(original.maxUpgradeLevel);
            setAttackType(original.attackType);
            setTarget(original.cardTarget);
            setTags(original.tags);
            affinities = new PCLCardDataAffinityGroup(original.affinities);
        }

        setImagePath(original.imagePath);
        setProperties(original.cardType, original.cardColor, original.cardRarity);
        setLoadout(original.loadout);
        setText(name, text, text);
    }

    public PCLCardBuilder(PCLCardBuilder original)
    {
        this(original, true);
        this.source = original.source;

        setImage(original.portraitImage, original.portraitForeground);
        setLanguageMap(original.languageMap);
        setPSkill(original.moves, true, true);
        setPPower(original.powers, true, true);
    }

    public PCLCardBuilder(PCLCustomCardSlot data)
    {
        this(data.ID);
        damage = data.damage.clone();
        damageUpgrade = data.damageUpgrade.clone();
        block = data.block.clone();
        blockUpgrade = data.blockUpgrade.clone();
        magicNumber = data.tempHP.clone();
        magicNumberUpgrade = data.tempHPUpgrade.clone();
        hp = data.heal.clone();
        hpUpgrade = data.healUpgrade.clone();
        hitCount = data.hitCount.clone();
        hitCountUpgrade = data.hitCountUpgrade.clone();
        rightCount = data.rightCount.clone();
        rightCountUpgrade = data.rightCountUpgrade.clone();
        cost = data.cost.clone();
        costUpgrade = data.costUpgrade.clone();
        setLanguageMap(EUIUtils.deserialize(data.languageStrings, TStrings.getType()));
        setProperties(AbstractCard.CardType.valueOf(data.type), AbstractCard.CardRarity.valueOf(data.rarity));
        setTarget(PCLCardTarget.valueOf(data.target));
        setTags(EUIUtils.map(data.tags, t -> EUIUtils.deserialize(t, PCLCardTagInfo.class)));
        setColor(data.slotColor);
        if (data.loadout != null)
        {
             setLoadout(PCLLoadout.get(data.slotColor, data.loadout));
        }
        setAffinities(EUIUtils.deserialize(data.affinities, PCLCardDataAffinityGroup.class));
    }

    public PCLDynamicCard build()
    {
        return build(false);
    }

    public PCLDynamicCard build(boolean shouldFindForms)
    {
        setTextForLanguage();

        if (imagePath == null)
        {
            imagePath = QuestionMark.DATA.imagePath;
        }

        return new PCLDynamicCard(this, shouldFindForms);
    }

    protected CardStrings getInitialStrings()
    {
        CardStrings retVal = new CardStrings();
        retVal.NAME = "";
        retVal.DESCRIPTION = "";
        retVal.UPGRADE_DESCRIPTION = "";
        retVal.EXTENDED_DESCRIPTION = new String[]{};
        return retVal;
    }

    public CardStrings getStringsForLanguage(Settings.GameLanguage language)
    {
        return languageMap.getOrDefault(language,
                languageMap.getOrDefault(Settings.GameLanguage.ENG,
                        languageMap.size() > 0 ? languageMap.entrySet().iterator().next().getValue() : getInitialStrings()));
    }

    @Override
    public AbstractCard makeCopy(boolean upgraded)
    {
        return build(true);
    }

    public PCLCardBuilder setImagePath(String imagePath)
    {
        this.imagePath = imagePath;

        return this;
    }

    public PCLCardBuilder removePMove(PSkill effect)
    {
        moves.remove(effect);
        return this;
    }

    public PCLCardBuilder removePowerEffect(PTrigger effect)
    {
        powers.remove(effect);
        return this;
    }

    public PCLCardBuilder setAffinity(PCLAffinity affinity, int level)
    {
        this.affinities.set(affinity, level);

        return this;
    }

    public PCLCardBuilder setAttackEffect(AbstractGameAction.AttackEffect attackEffect)
    {
        this.attackEffect = attackEffect;

        return this;
    }

    public PCLCardBuilder setAttackType(PCLAttackType attackType)
    {
        this.attackType = attackType;

        return this;
    }

    public PCLCardBuilder setDescription(String description)
    {
        this.strings.DESCRIPTION = description;

        return this;
    }

    public PCLCardBuilder setExtraTags(List<CardTagItem> extraTags)
    {
        this.extraTags = extraTags;

        return this;
    }


    public PCLCardBuilder setID(String id)
    {
        this.ID = id;
        return this;
    }

    public PCLCardBuilder setImage(ColoredTexture portraitImage, ColoredTexture portraitForeground)
    {
        this.portraitImage = portraitImage;
        this.portraitForeground = portraitForeground;

        return this;
    }

    public PCLCardBuilder setLanguageMap(HashMap<Settings.GameLanguage, CardStrings> languageMap)
    {
        this.languageMap.putAll(languageMap);
        return setTextForLanguage();
    }

    public PCLCardBuilder setLanguageMapEntry(Settings.GameLanguage language)
    {
        this.languageMap.put(language, this.strings);
        return this;
    }

    public PCLCardBuilder setName(String name)
    {
        this.strings.NAME = name;

        return this;
    }

    public PCLCardBuilder setPSkill(Iterable<PSkill<?>> currentEffects)
    {
        return setPSkill(currentEffects, false, true);
    }

    public PCLCardBuilder setPSkill(Iterable<PSkill<?>> currentEffects, boolean makeCopy, boolean clear)
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

    public PCLCardBuilder setPSkill(PSkill<?>... effect)
    {
        return setPSkill(Arrays.asList(effect));
    }

    public PCLCardBuilder addPSkill(PSkill<?> effect)
    {
        return addPSkill(effect, false);
    }

    public PCLCardBuilder addPSkill(PSkill<?> effect, boolean makeCopy)
    {
        if (makeCopy && effect != null)
        {
            effect = effect.makeCopy();
        }
        moves.add(effect);

        return this;
    }

    public PCLCardBuilder setPPower(Iterable<PTrigger> currentEffects)
    {
        return setPPower(currentEffects, false, true);
    }

    public PCLCardBuilder setPPower(Iterable<PTrigger> currentEffects, boolean makeCopy, boolean clear)
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

    public PCLCardBuilder setPPower(PTrigger... effect)
    {
        return setPPower(Arrays.asList(effect));
    }

    public PCLCardBuilder addPPower(PTrigger effect)
    {
        return addPPower(effect, false);
    }

    public PCLCardBuilder addPPower(PTrigger effect, boolean makeCopy)
    {
        if (makeCopy && effect != null)
        {
            effect = effect.makeCopy();
        }
        powers.add(effect);

        return this;
    }

    public PCLCardBuilder setPortrait(TextureAtlas.AtlasRegion portrait)
    {
        this.fakePortrait = portrait;

        return this;
    }

    public PCLCardBuilder setProperties(AbstractCard.CardType type, AbstractCard.CardRarity rarity)
    {
        return setProperties(type, AbstractCard.CardColor.COLORLESS, rarity);
    }

    public PCLCardBuilder setProperties(AbstractCard.CardType type, AbstractCard.CardColor color, AbstractCard.CardRarity rarity)
    {
        this.cardType = type;
        this.cardColor = color;
        this.cardRarity = rarity;
        return this;
    }

    public PCLCardBuilder setColor(AbstractCard.CardColor color)
    {
        super.setColor(color);
        return this;
    }

    public PCLCardBuilder setText(CardStrings cardStrings)
    {
        return setText(cardStrings.NAME, cardStrings.DESCRIPTION, cardStrings.UPGRADE_DESCRIPTION);
    }

    public PCLCardBuilder setText(String name)
    {
        return setText(name, "", "", new String[0]);
    }

    public PCLCardBuilder setText(String name, String description, String upgradeDescription)
    {
        return setText(name, description, upgradeDescription != null ? upgradeDescription : description, new String[0]);
    }

    public PCLCardBuilder setText(String name, String description, String upgradeDescription, String[] extendedDescription)
    {
        this.strings.NAME = name;
        this.strings.DESCRIPTION = description;
        this.strings.UPGRADE_DESCRIPTION = upgradeDescription;
        this.strings.EXTENDED_DESCRIPTION = extendedDescription;

        return this;
    }

    public PCLCardBuilder setTextForLanguage()
    {
        return setTextForLanguage(Settings.language);
    }

    public PCLCardBuilder setTextForLanguage(Settings.GameLanguage language)
    {
        return setText(getStringsForLanguage(language));
    }

    public PCLCardBuilder setType(AbstractCard.CardType type)
    {
        this.cardType = type;
        return this;
    }

    public PCLCardBuilder showTypeText(boolean showTypeText)
    {
        this.showTypeText = showTypeText;

        return this;
    }
}