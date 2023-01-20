package pinacolada.resources.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardAffinityStatistics;
import pinacolada.cards.base.PCLCardBuilder;
import pinacolada.cards.base.PCLCardData;
import pinacolada.resources.PCLAbstractPlayerData;
import pinacolada.resources.PGR;
import pinacolada.skills.skills.PSpecialSkill;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.WeightedList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

// Copied and modified from STS-AnimatorMod
// TODO Rework
public class PCLRuntimeLoadout
{
    public final int id;
    public final PCLAbstractPlayerData data;
    public final PCLLoadout loadout;
    public final PCLTrophies trophies;
    public final Map<String, AbstractCard> baseCards = new HashMap<>();
    public final EUITooltip trophyTooltip;
    public final EUITooltip glyphTooltip;
    public final EUITooltip trophy3Tooltip;
    public final EUITooltip unlockTooltip;

    public int bonus;
    public PCLCard card;
    public PCLCardAffinityStatistics affinityStatistics;
    public boolean isLocked;
    protected FakeSkill skill;

    public PCLRuntimeLoadout(PCLLoadout loadout)
    {
        this.id = loadout.id;
        this.loadout = loadout;
        this.trophies = this.loadout.getTrophies();
        this.data = PGR.getPlayerData(loadout.color);

        this.trophyTooltip = new EUITooltip(PGR.core.strings.trophies.trophy, trophies != null && trophies.trophy1 >= 0 ? PGR.core.strings.trophies.bronzeFormatted(trophies.trophy1) : PGR.core.strings.trophies.bronzeLocked);
        this.glyphTooltip = new EUITooltip(PGR.core.strings.trophies.glyph, trophies != null && trophies.trophy2 >= 0 ? PGR.core.strings.trophies.silverFormatted(trophies.trophy2) : PGR.core.strings.trophies.silverLocked);
        this.trophy3Tooltip = new EUITooltip(PGR.core.strings.trophies.gold, trophies != null && trophies.trophy3 >= 0 ? PGR.core.strings.trophies.goldFormatted(trophies.trophy3) : PGR.core.strings.trophies.goldLocked);
        this.unlockTooltip = new EUITooltip(PGR.core.strings.charSelect.rightText, PGR.core.strings.charSelect.unlocksAtLevel(loadout.unlockLevel, data.resources.getUnlockLevel()));
        initializeCards(loadout);

        this.affinityStatistics = new PCLCardAffinityStatistics(getCardPoolInPlay().values(), false);

        this.card = null;
        this.isLocked = data.resources.getUnlockLevel() < this.loadout.unlockLevel;
    }

    public static PCLRuntimeLoadout tryCreate(PCLLoadout loadout)
    {
        PCLRuntimeLoadout result = new PCLRuntimeLoadout(loadout);
        if (result.getCardPoolInPlay().size() > 0 && result.loadout.getSymbolicCard() != null)
        {
            return result;
        }

        return null;
    }

    public PCLCard buildCard()
    {
        final PCLCardData data = loadout.getSymbolicCard();
        if (data == null)
        {
            EUIUtils.logWarning(this, loadout.getName() + " has no symbolic card.");
            return null;
        }

        card = ((PCLCardBuilder) new PCLCardBuilder(String.valueOf(loadout.id))
                .setImagePath(data.imagePath)
                .showTypeText(false)
                .setMaxUpgrades(0))
                .build();

        card.name = loadout.isCore() ? PGR.core.strings.seriesUI.core : loadout.getName();
        card.clearSkills();

        if (isLocked)
        {
            card.isSeen = false;
            card.cardText.overrideDescription(unlockTooltip.description(), false);
            card.tooltips.add(unlockTooltip);
            card.rarity = AbstractCard.CardRarity.COMMON;
            card.type = AbstractCard.CardType.STATUS;
        }
        else
        {
            skill = new FakeSkill();
            card.addUseMove(skill);
            card.rarity = AbstractCard.CardRarity.COMMON;
            card.type = loadout.isCore() ? AbstractCard.CardType.CURSE : AbstractCard.CardType.SKILL;
            card.tooltips.add(trophyTooltip);
            card.tooltips.add(glyphTooltip);
        }

        if (!loadout.isCore())
        {
            int i = 0;
            int maxLevel = 2;
            float maxPercentage = 0;
            for (PCLCardAffinityStatistics.Group g : affinityStatistics)
            {
                float percentage = g.getPercentage(0);
                if (percentage == 0 || i > 2)
                {
                    break;
                }

                if (percentage < maxPercentage || (maxLevel == 2 && percentage < 0.3f))
                {
                    maxLevel -= 1;
                }
                if (maxLevel > 0)
                {
                    card.affinities.add(g.affinity, maxLevel);
                }

                maxPercentage = percentage;
                i += 1;
            }
            card.affinities.collapseDuplicates = true;
            card.affinities.updateSortedList();
        }

        card.initializeDescription();

        return card;
    }

    public CardGroup getCardPool()
    {
        final CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard c : getCardPoolInPlay().values())
        {
            final CardGroup pool = GameUtilities.getCardPool(c.rarity);
            if (pool != null)
            {
                c = pool.findCardById(c.cardID);

                if (c != null)
                {
                    final AbstractCard copy = c.makeCopy();
                    copy.isSeen = c.isSeen;
                    group.group.add(copy);
                }
            }
        }

        group.sortByRarity(true);

        return group;
    }

    public Map<String, AbstractCard> getCardPoolInPlay()
    {
        return baseCards;
    }

    public AbstractCard getRandomCard()
    {
        return GameUtilities.getRandomElement(new ArrayList<>(getCardPoolInPlay().values()), PCLCard.rng);
    }

    public ArrayList<AbstractCard> getSeenCards(Map<String, AbstractCard> source)
    {
        return EUIUtils.filter(source.values(), c -> c.isSeen);
    }

    public AbstractCard getWeightedRandomCard(boolean inCombat)
    {
        WeightedList<AbstractCard> rewards = new WeightedList<>();

        for (AbstractCard c : getCardPoolInPlay().values())
        {
            if (canSelect(c) && (!inCombat || GameUtilities.isObtainableInCombat(c)))
            {
                switch (c.rarity)
                {
                    case COMMON:
                        rewards.add(c, 45);
                        break;

                    case UNCOMMON:
                        rewards.add(c, 40);
                        break;

                    case RARE:
                        rewards.add(c, 20);
                        break;
                }
            }
        }
        return rewards.retrieve(PCLCard.rng);
    }

    public void updateCounts(HashSet<String> banned)
    {
        if (skill != null)
        {
            skill.setAmount(EUIUtils.count(baseCards.keySet(), id -> !banned.contains(id)));
        }
    }

    private boolean canSelect(AbstractCard c)
    {
        return c.type != AbstractCard.CardType.CURSE && c.type != AbstractCard.CardType.STATUS;
    }

    private void initializeCards(PCLLoadout series)
    {
        for (AbstractCard card : CardLibrary.getAllCards())
        {
            PCLCard c = EUIUtils.safeCast(card, PCLCard.class);
            if (c != null && card.color == data.resources.cardColor && (series == null || series.equals(c.cardData.loadout))
                    && card.rarity != AbstractCard.CardRarity.SPECIAL
                    && card.rarity != AbstractCard.CardRarity.BASIC)
            {
                baseCards.put(c.cardID, c);
            }
        }
    }

    protected class FakeSkill extends PSpecialSkill
    {
        public FakeSkill()
        {
            super("", PGR.core.strings.seriesSelection.selected, (a, b) -> {
            }, 0, baseCards.size());
        }
    }

    protected class FakeSkill2 extends PSpecialSkill
    {
        public FakeSkill2()
        {
            super("", PGR.core.strings.seriesSelection.unlocked, (a, b) -> {
            }, 0, baseCards.size());
        }
    }
}
