package pinacolada.cards.base;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.markers.CardObject;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.interfaces.markers.Hidden;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.monsters.animations.PCLAllyAnimation;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLLoadout;
import pinacolada.utilities.PCLRenderHelpers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import static extendedui.EUIUtils.array;
import static extendedui.EUIUtils.safeIndex;

public class PCLCardData implements CardObject
{
    public final Class<? extends PCLCard> type;
    public Integer[] damage = array(0);
    public Integer[] damageUpgrade = array(0);
    public Integer[] block = array(0);
    public Integer[] blockUpgrade = array(0);
    public Integer[] magicNumber = array(0);
    public Integer[] magicNumberUpgrade = array(0);
    public Integer[] hp = array(0);
    public Integer[] hpUpgrade = array(0);
    public Integer[] hitCount = array(1);
    public Integer[] hitCountUpgrade = array(0);
    public Integer[] rightCount = array(1);
    public Integer[] rightCountUpgrade = array(0);
    public Integer[] cost = array(-2);
    public Integer[] costUpgrade = array(0);
    public HashMap<PCLCardTag, PCLCardTagInfo> tags = new HashMap<>();
    public CardStrings strings;
    public String imagePath;
    public String ID;
    public PCLCardTarget cardTarget = PCLCardTarget.None;
    public PCLCardTarget[] upgradeCardTarget;
    public AbstractCard.CardType cardType = AbstractCard.CardType.SKILL;
    public AbstractCard.CardColor cardColor = AbstractCard.CardColor.COLORLESS;
    public AbstractCard.CardRarity cardRarity = AbstractCard.CardRarity.BASIC;
    public FuncT1<PCLAllyAnimation, PCLCardAlly> customAnimation; // TODO use this
    public PCLResources<?,?,?> resources;
    public PCLAttackType attackType = PCLAttackType.Normal;
    public PCLCardDataAffinityGroup affinities = new PCLCardDataAffinityGroup();
    public PCLLoadout loadout;
    public boolean canToggleFromAlternateForm = false;
    public boolean canToggleFromPopup = false;
    public boolean canToggleOnUpgrade = false;
    public boolean ignoreSimpleMode = false;
    public boolean obtainableInCombat = true;
    public boolean playAtEndOfTurn = false;
    public boolean unique = false;
    public boolean unUpgradedCanToggleForms = false;
    public int maxCopies;
    public int maxForms = 1;
    public int maxUpgradeLevel = 1;
    public int slots;
    public transient PCLCard tempCard = null;
    private Constructor<? extends PCLCard> constructor;
    private TextureAtlas.AtlasRegion cardIcon = null;

    public PCLCardData(Class<? extends PCLCard> type, PCLResources<?,?,?> resources)
    {
        this(type, resources, resources.createID(type.getSimpleName()));
    }

    public PCLCardData(Class<? extends PCLCard> type, PCLResources<?,?,?> resources, String cardID)
    {
        this(type, resources, cardID, PGR.getCardStrings(cardID));

        this.imagePath = PGR.getCardImage(cardID);
    }

    public PCLCardData(Class<? extends PCLCard> type, PCLResources<?,?,?> resources, String cardID, CardStrings strings)
    {
        this.ID = cardID;
        this.resources = resources;
        this.cardColor = resources.cardColor;
        this.maxCopies = -1;
        this.strings = strings != null ? strings : new CardStrings();
        this.type = type;
    }

    // Use our own mock strings because brackets will cause the card not to load
    private static CardStrings getMockCardString()
    {
        CardStrings retVal = new CardStrings();
        retVal.NAME = "NAN";
        retVal.DESCRIPTION = "";
        retVal.UPGRADE_DESCRIPTION = "";
        retVal.EXTENDED_DESCRIPTION = new String[]{""};
        return retVal;
    }

    public PCLCardData addTags(PCLCardTag... tags)
    {
        return addTags(EUIUtils.map(tags, PCLCardTag::make));
    }

    public PCLCardData addTags(Iterable<PCLCardTagInfo> tags)
    {
        for (PCLCardTagInfo tag : tags)
        {
            this.tags.put(tag.tag, tag);
        }
        return this;
    }

    public PCLCardData addUpgrades(PCLCardTag... tags)
    {
        return addTags(EUIUtils.map(tags, tag -> tag.make(0, 1)));
    }

    public PCLCard createNewInstance(int form, boolean upgrade) throws RuntimeException
    {
        PCLCard card = createNewInstance();
        if (form > 0 && form < card.cardData.maxForms)
        {
            card.setForm(form, 0);
        }
        if (upgrade && card.canUpgrade())
        {
            card.upgrade();
        }

        return card;
    }

    public PCLCard createNewInstance(boolean upgrade) throws RuntimeException
    {
        PCLCard card = createNewInstance();
        if (upgrade && card.canUpgrade())
        {
            card.upgrade();
        }

        return card;
    }

    public PCLCard createNewInstance() throws RuntimeException
    {
        try
        {
            if (constructor == null)
            {
                constructor = type.getConstructor();
                constructor.setAccessible(true);
            }

            return constructor.newInstance();
        }
        catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
        {
            throw new RuntimeException(ID, e);
        }
    }

    public int getBlock(int form)
    {
        return block[Math.min(block.length - 1, form)];
    }

    public int getBlockUpgrade(int form)
    {
        return blockUpgrade[Math.min(blockUpgrade.length - 1, form)];
    }

    @Override
    public AbstractCard getCard()
    {
        return makeCopy(false);
    }

    public TextureAtlas.AtlasRegion getCardIcon()
    {
        if (cardIcon == null)
        {
            cardIcon = PCLRenderHelpers.generateIcon(EUIRM.getTexture(imagePath));
        }

        return cardIcon;
    }

    public int getCost(int form)
    {
        return cost[Math.min(cost.length - 1, form)];
    }

    public int getCostUpgrade(int form)
    {
        return costUpgrade[Math.min(costUpgrade.length - 1, form)];
    }

    public int getDamage(int form)
    {
        return damage[Math.min(damage.length - 1, form)];
    }

    public int getDamageUpgrade(int form)
    {
        return damageUpgrade[Math.min(damageUpgrade.length - 1, form)];
    }

    public int getHp(int form)
    {
        return hp[Math.min(hp.length - 1, form)];
    }

    public int getHpUpgrade(int form)
    {
        return hpUpgrade[Math.min(hpUpgrade.length - 1, form)];
    }

    public int getHitCount(int form)
    {
        return hitCount[Math.min(hitCount.length - 1, form)];
    }

    public int getHitCountUpgrade(int form)
    {
        return hitCountUpgrade[Math.min(hitCountUpgrade.length - 1, form)];
    }

    public String getLoadoutName()
    {
        return loadout != null ? loadout.getName() : strings.DESCRIPTION;
    }

    public Integer[] getNumbers(int form)
    {
        return array(
                safeIndex(damage, form),
                safeIndex(block, form),
                safeIndex(magicNumber, form),
                safeIndex(hp, form),
                safeIndex(hitCount, form),
                safeIndex(rightCount, form)
        );
    }

    public int getRightCount(int form)
    {
        return rightCount[Math.min(rightCount.length - 1, form)];
    }

    public int getRightCountUpgrade(int form)
    {
        return rightCountUpgrade[Math.min(rightCountUpgrade.length - 1, form)];
    }

    public PCLCardTagInfo getTagInfo(PCLCardTag tag)
    {
        return tags.get(tag);
    }

    public Collection<PCLCardTagInfo> getTagInfos()
    {
        return tags.values();
    }

    public PCLCardTarget getTargetUpgrade(int form)
    {
        return upgradeCardTarget == null || upgradeCardTarget.length == 0 ? cardTarget : upgradeCardTarget[Math.min(upgradeCardTarget.length - 1, form)];
    }

    public int getMagicNumber(int form)
    {
        return magicNumber[Math.min(magicNumber.length - 1, form)];
    }

    public int getMagicNumberUpgrade(int form)
    {
        return magicNumberUpgrade[Math.min(magicNumberUpgrade.length - 1, form)];
    }

    public boolean hasColor(AbstractCard.CardColor color)
    {
        return cardColor == color || resources.cardColor == color;
    }

    public void invokeTags(AbstractCard card)
    {
        for (PCLCardTagInfo i : getTagInfos())
        {
            i.invoke(card);
        }
    }

    public void invokeTags(AbstractCard card, int form)
    {
        for (PCLCardTagInfo i : getTagInfos())
        {
            i.invoke(card, form);
        }
    }

    public boolean isNotSeen()
    {
        return UnlockTracker.isCardLocked(ID) || !UnlockTracker.isCardSeen(ID);
    }

    // CardLibrary.getCopy may return an EYBCard if it determines a card should be replaced, so we cannot guarantee it is a PCLCard
    public AbstractCard makeCopy(boolean upgraded)
    {
        return (type.isAssignableFrom(Hidden.class) ? createNewInstance(upgraded) : CardLibrary.getCopy(ID, upgraded ? 1 : 0, 0));
    }

    public void markSeen()
    {
        if (!UnlockTracker.isCardSeen(ID))
        {
            UnlockTracker.markCardAsSeen(ID);
        }
    }

    public PCLCardData removeTags(Iterable<PCLCardTag> tags)
    {
        tags.forEach(this.tags::remove);

        return this;
    }

    public PCLCardData removeTags(PCLCardTag... tags)
    {
        return removeTags(Arrays.asList(tags));
    }

    public PCLCardData setAffinities(PCLAffinity... affinity)
    {
        return setAffinities(1, affinity);
    }

    public PCLCardData setAffinities(int amount, PCLAffinity... affinity)
    {
        for (PCLAffinity af : affinity)
        {
            setAffinities(af, amount, 0);
        }
        return this;
    }

    public PCLCardData setAffinities(PCLCardDataAffinity... affinity)
    {
        for (PCLCardDataAffinity af : affinity)
        {
            setAffinities(af);
        }
        return this;
    }

    public PCLCardData setAffinities(PCLCardDataAffinityGroup group)
    {
        this.affinities = group;

        return this;
    }

    public PCLCardData setAffinities(PCLAffinity affinity, int base, int upgrade)
    {
        affinities.set(affinity, base, upgrade);
        return this;
    }

    public PCLCardData setAffinities(PCLCardDataAffinity affinity)
    {
        affinities.set(affinity);
        return this;
    }

    public PCLCardData setAttack(int cost, AbstractCard.CardRarity rarity)
    {
        return setAttack(cost, rarity, PCLAttackType.Normal, PCLCardTarget.Single);
    }

    public PCLCardData setAttack(int cost, AbstractCard.CardRarity rarity, PCLAttackType attackType)
    {
        return setAttack(cost, rarity, attackType, PCLCardTarget.Single);
    }

    public PCLCardData setAttack(int cost, AbstractCard.CardRarity rarity, PCLAttackType attackType, PCLCardTarget target)
    {
        setRarity(rarity);

        cardTarget = target;
        cardType = AbstractCard.CardType.ATTACK;
        this.attackType = attackType;
        this.cost = array(cost);

        return this;
    }

    public PCLCardData setBlock(int block, int blockUpgrade)
    {
        return setBlock(array(block), array(blockUpgrade));
    }

    public PCLCardData setBlock(int block, Integer[] blockUpgrade)
    {
        return setBlock(array(block), blockUpgrade);
    }

    public PCLCardData setBlock(Integer[] block, Integer[] blockUpgrade)
    {
        this.block = block;
        this.blockUpgrade = blockUpgrade;
        return this;
    }

    public PCLCardData setBlock(int block, int blockUpgrade, int rightCount)
    {
        return setBlock(block, blockUpgrade, rightCount, 0);
    }

    public PCLCardData setBlock(int block, int blockUpgrade, int rightCount, int rightCountUpgrade)
    {
        return setBlock(array(block), array(blockUpgrade), array(rightCount), array(rightCountUpgrade));
    }

    public PCLCardData setBlock(int block, int blockUpgrade, Integer[] rightCount, Integer[] rightCountUpgrade)
    {
        return setBlock(array(block), array(blockUpgrade), rightCount, rightCountUpgrade);
    }

    public PCLCardData setBlock(int block, Integer[] blockUpgrade, int rightCount, Integer[] rightCountUpgrade)
    {
        return setBlock(array(block), blockUpgrade, array(rightCount), rightCountUpgrade);
    }

    public PCLCardData setBlock(Integer[] block, Integer[] blockUpgrade, Integer[] rightCount, Integer[] rightCountUpgrade)
    {
        this.block = block;
        this.blockUpgrade = blockUpgrade;
        this.rightCount = rightCount;
        this.rightCountUpgrade = rightCountUpgrade;
        return this;
    }

    public PCLCardData setColor(AbstractCard.CardColor color)
    {
        cardColor = color;
        return this;
    }

    public PCLCardData setCore()
    {
        return setLoadout(PGR.getPlayerData(cardColor).getCoreLoadout());
    }

    public PCLCardData setColorless()
    {
        cardColor = AbstractCard.CardColor.COLORLESS;
        return this;
    }

    public PCLCardData setColorless(PCLLoadout loadout)
    {
        cardColor = AbstractCard.CardColor.COLORLESS;
        return setLoadout(loadout);
    }

    public PCLCardData setCostUpgrades(Integer... costUpgrades)
    {
        costUpgrade = costUpgrades;
        return this;
    }

    public PCLCardData setCosts(Integer... costs)
    {
        cost = costs;
        return this;
    }

    public PCLCardData setCurse(int cost, PCLCardTarget target, boolean special)
    {
        return setCurse(cost, target, special, false);
    }

    public PCLCardData setCurse(int cost, PCLCardTarget target, boolean special, boolean playAtEndOfTurn)
    {
        setRarity(special ? AbstractCard.CardRarity.SPECIAL : AbstractCard.CardRarity.CURSE);

        cardColor = AbstractCard.CardColor.CURSE;
        cardType = AbstractCard.CardType.CURSE;
        cardTarget = target;
        this.cost = array(cost);
        this.playAtEndOfTurn = playAtEndOfTurn;
        maxUpgradeLevel = 0;

        return this;
    }

    public PCLCardData setDamage(int damage, int damageUpgrade)
    {
        return setDamage(array(damage), array(damageUpgrade));
    }

    public PCLCardData setDamage(int damage, Integer[] damageUpgrade)
    {
        return setDamage(array(damage), damageUpgrade);
    }

    public PCLCardData setDamage(Integer[] damage, Integer[] damageUpgrade)
    {
        this.damage = damage;
        this.damageUpgrade = damageUpgrade;
        return this;
    }

    public PCLCardData setDamage(int damage, int damageUpgrade, int hitCount)
    {
        return setDamage(damage, damageUpgrade, hitCount, 0);
    }

    public PCLCardData setDamage(int damage, int damageUpgrade, int hitCount, int hitCountUpgrade)
    {
        return setDamage(array(damage), array(damageUpgrade), array(hitCount), array(hitCountUpgrade));
    }

    public PCLCardData setDamage(int damage, int damageUpgrade, Integer[] hitCount, Integer[] hitCountUpgrade)
    {
        return setDamage(array(damage), array(damageUpgrade), hitCount, hitCountUpgrade);
    }

    public PCLCardData setDamage(int damage, Integer[] damageUpgrade, int hitCount, Integer[] hitCountUpgrade)
    {
        return setDamage(array(damage), damageUpgrade, array(hitCount), hitCountUpgrade);
    }

    public PCLCardData setDamage(Integer[] damage, Integer[] damageUpgrade, Integer[] hitCount, Integer[] hitCountUpgrade)
    {
        this.damage = damage;
        this.damageUpgrade = damageUpgrade;
        this.hitCount = hitCount;
        this.hitCountUpgrade = hitCountUpgrade;
        return this;
    }

    public PCLCardData setHitCount(int heal, int healUpgrade)
    {
        hitCount[0] = heal;
        hitCountUpgrade[0] = healUpgrade;
        return this;
    }

    public PCLCardData setHp(int heal, int healUpgrade)
    {
        this.hp[0] = heal;
        this.hpUpgrade[0] = healUpgrade;
        return this;
    }

    public PCLCardData setIgnoreSimpleMode(boolean ignore)
    {
        this.ignoreSimpleMode = ignore;

        return this;
    }

    public PCLCardData setImagePath(String imagePath)
    {
        this.imagePath = imagePath;

        return this;
    }

    public PCLCardData setMaxCopies(int maxCopies)
    {
        this.maxCopies = maxCopies;

        return this;
    }

    public PCLCardData setMaxUpgrades(int maxUpgradeLevel)
    {
        this.maxUpgradeLevel = MathUtils.clamp(maxUpgradeLevel, -1, Integer.MAX_VALUE);

        return this;
    }

    public PCLCardData setMultiformData(int maxForms)
    {
        return setMultiformData(maxForms, false, true, false, false);
    }

    public PCLCardData setMultiformData(int maxForms, boolean canToggleFromPopup)
    {
        return setMultiformData(maxForms, canToggleFromPopup, !canToggleFromPopup, canToggleFromPopup, false);
    }

    public PCLCardData setMultiformData(int maxForms, boolean canToggleFromPopup, boolean canToggleOnUpgrade, boolean canToggleFromAlternateForm, boolean unUpgradedCanToggleForms)
    {
        this.maxForms = maxForms;
        this.canToggleFromPopup = canToggleFromPopup;
        this.canToggleOnUpgrade = canToggleOnUpgrade;
        this.canToggleFromAlternateForm = canToggleFromAlternateForm;
        this.unUpgradedCanToggleForms = unUpgradedCanToggleForms;

        return this;
    }

    public PCLCardData setNumbers(PCLCardData data)
    {
        damage = data.damage.clone();
        damageUpgrade = data.damageUpgrade.clone();
        block = data.block.clone();
        blockUpgrade = data.blockUpgrade.clone();
        magicNumber = data.magicNumber.clone();
        magicNumberUpgrade = data.magicNumberUpgrade.clone();
        hp = data.hp.clone();
        hpUpgrade = data.hpUpgrade.clone();
        hitCount = data.hitCount.clone();
        hitCountUpgrade = data.hitCountUpgrade.clone();
        rightCount = data.rightCount.clone();
        rightCountUpgrade = data.rightCountUpgrade.clone();
        cost = data.cost.clone();
        costUpgrade = data.costUpgrade.clone();
        return this;
    }

    public PCLCardData setObtainableInCombat(boolean obtainable)
    {
        this.obtainableInCombat = obtainable;

        return this;
    }

    public PCLCardData setPlayAtEndOfTurn(boolean playAtEndOfTurn)
    {
        this.playAtEndOfTurn = playAtEndOfTurn;

        return this;
    }

    public PCLCardData setPower(int cost, AbstractCard.CardRarity rarity)
    {
        setRarity(rarity);

        cardTarget = PCLCardTarget.None;
        cardType = AbstractCard.CardType.POWER;
        this.cost = array(cost);

        return this;
    }

    public PCLCardData setPriority(int heal)
    {
        this.magicNumber[0] = heal;
        return this;
    }

    public PCLCardData setPriority(int heal, int healUpgrade)
    {
        this.magicNumber[0] = heal;
        this.magicNumberUpgrade[0] = healUpgrade;
        return this;
    }

    public PCLCardData setPriority(Integer[] heal, Integer[] healUpgrade)
    {
        this.magicNumber = heal;
        this.magicNumberUpgrade = healUpgrade;
        return this;
    }

    public PCLCardData setRTags(Collection<PCLCardTag> tags)
    {
        for (PCLCardTag tag : tags)
        {
            this.tags.put(tag, tag.make(1, 0));
        }
        return this;
    }

    public PCLCardData setRTags(PCLCardTag... tags)
    {
        return setRTags(Arrays.asList(tags));
    }

    public PCLCardData setRarity(AbstractCard.CardRarity rarity)
    {
        cardRarity = rarity;

        if (maxCopies == -1)
        {
            switch (rarity)
            {
                case COMMON:
                    return setMaxCopies(5);
                case UNCOMMON:
                    return setMaxCopies(4);
                case RARE:
                    return setMaxCopies(3);
                default:
                    return setMaxCopies(0);
            }
        }

        return this;
    }

    public PCLCardData setRightCount(int heal, int healUpgrade)
    {
        rightCount[0] = heal;
        rightCountUpgrade[0] = healUpgrade;
        return this;
    }

    public PCLCardData setLoadout(PCLLoadout loadout)
    {
        this.loadout = loadout;
        if (this.loadout != null)
        {
            if (cardColor == resources.cardColor)
            {
                this.loadout.cardData.add(this);
            }
            else
            {
                this.loadout.colorlessData.add(this);
            }
        }

        // Non-loadout cards, curses, statuses, and special cards cannot get slots
        if (slots <= 0 && this.loadout != null && this.loadout.id >= 0 && cardType != AbstractCard.CardType.CURSE && cardType != AbstractCard.CardType.STATUS && cardRarity != AbstractCard.CardRarity.SPECIAL)
        {
            // Commons and Attacks/Skills get an extra slot
            slots = (cardType == AbstractCard.CardType.POWER ? 1 : 2) + (cardRarity == AbstractCard.CardRarity.COMMON ? 1 : 0);
        }

        return this;
    }

    public PCLCardData setSkill(int cost, AbstractCard.CardRarity rarity)
    {
        return setSkill(cost, rarity, PCLCardTarget.Single);
    }

    public PCLCardData setSkill(int cost, AbstractCard.CardRarity rarity, PCLCardTarget target)
    {
        setRarity(rarity);

        cardTarget = target;
        cardType = AbstractCard.CardType.SKILL;
        this.cost = array(cost);

        return this;
    }

    public PCLCardData setSlots(int slots)
    {
        this.slots = slots;
        return this;
    }

    public PCLCardData setStatus(int cost, AbstractCard.CardRarity rarity, PCLCardTarget target)
    {
        return setStatus(cost, rarity, target, false);
    }

    public PCLCardData setStatus(int cost, AbstractCard.CardRarity rarity, PCLCardTarget target, boolean playAtEndOfTurn)
    {
        setRarity(rarity);

        cardColor = AbstractCard.CardColor.COLORLESS;
        cardType = AbstractCard.CardType.STATUS;
        cardTarget = target;
        this.cost = array(cost);
        this.playAtEndOfTurn = playAtEndOfTurn;
        maxUpgradeLevel = 0;

        return this;
    }

    public PCLCardData setSummon(int cost, AbstractCard.CardRarity rarity)
    {
        return setSummon(cost, rarity, PCLAttackType.Normal, PCLCardTarget.Single);
    }

    public PCLCardData setSummon(int cost, AbstractCard.CardRarity rarity, PCLAttackType attackType)
    {
        return setSummon(cost, rarity, attackType, PCLCardTarget.Single);
    }

    public PCLCardData setSummon(int cost, AbstractCard.CardRarity rarity, PCLAttackType attackType, PCLCardTarget target)
    {
        setRarity(rarity);

        cardTarget = target;
        cardType = PCLEnum.CardType.SUMMON;
        this.attackType = attackType;
        this.cost = array(cost);

        return this;
    }

    public PCLCardData setTags(HashMap<PCLCardTag, PCLCardTagInfo> other)
    {
        tags.clear();
        for (PCLCardTag tag : other.keySet())
        {
            tags.put(tag, other.get(tag));
        }
        return this;
    }

    public PCLCardData setTags(Iterable<PCLCardTagInfo> tags)
    {
        this.tags.clear();
        for (PCLCardTagInfo tag : tags)
        {
            this.tags.put(tag.tag, tag);
        }
        return this;
    }

    public PCLCardData setTags(PCLCardTagInfo... tags)
    {
        return setTags(Arrays.asList(tags));
    }

    public PCLCardData setTags(PCLCardTag... tags)
    {
        return setTags(EUIUtils.map(tags, PCLCardTag::make));
    }

    public PCLCardData setTarget(PCLCardTarget target)
    {
        cardTarget = target;
        return this;
    }

    public PCLCardData setMagicNumber(int thp, int thpUpgrade)
    {
        return setMagicNumber(array(thp), array(thpUpgrade));
    }

    public PCLCardData setMagicNumber(int thp, Integer[] thpUpgrade)
    {
        return setMagicNumber(array(thp), thpUpgrade);
    }

    public PCLCardData setMagicNumber(Integer[] thp, Integer[] thpUpgrade)
    {
        magicNumber = thp;
        magicNumberUpgrade = thpUpgrade;
        return this;
    }

    public PCLCardData setUTags(Collection<PCLCardTag> tags)
    {
        for (PCLCardTag tag : tags)
        {
            this.tags.put(tag, tag.make(0, 1));
        }
        return this;
    }

    public PCLCardData setUTags(PCLCardTag... tags)
    {
        return setUTags(Arrays.asList(tags));
    }

    public PCLCardData setUTarget(PCLCardTarget... target)
    {
        upgradeCardTarget = target;
        return this;
    }

    public PCLCardData setUnique(boolean unique)
    {
        this.unique = unique;

        return this;
    }

    public PCLCardData setUnique(boolean unique, int maxUpgradeLevel)
    {
        this.unique = unique;
        this.maxUpgradeLevel = maxUpgradeLevel;

        return this;
    }

}
