package pinacolada.cards.base.fields;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import org.apache.commons.lang3.StringUtils;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PCLCardTarget implements Comparable<PCLCardTarget>
{
    // The ordering of this enum determines which targeting system takes priority
    None(AbstractCard.CardTarget.NONE),
    All(AbstractCard.CardTarget.ALL),
    AllEnemy(AbstractCard.CardTarget.ALL_ENEMY),
    Team(AbstractCard.CardTarget.ALL_ENEMY),
    Self(AbstractCard.CardTarget.SELF),
    Single(AbstractCard.CardTarget.ENEMY),
    RandomEnemy(AbstractCard.CardTarget.ALL_ENEMY),
    AllAlly(AbstractCard.CardTarget.ALL_ENEMY),
    SingleAlly(AbstractCard.CardTarget.ENEMY),
    RandomAlly(AbstractCard.CardTarget.ALL),
    Any(AbstractCard.CardTarget.SELF_AND_ENEMY);

    public static AbstractCreature source;
    public static AbstractCreature target;
    public final AbstractCard.CardTarget cardTarget;

    PCLCardTarget(AbstractCard.CardTarget cardTarget)
    {
        this.cardTarget = cardTarget;
    }

    public static List<PCLCardTarget> getAll()
    {
        return Arrays.stream(PCLCardTarget.values()).sorted((a,b) -> StringUtils.compare(a.getTitle(), b.getTitle())).collect(Collectors.toList());
    }

    public final AbstractMonster getTarget(AbstractCreature m)
    {
        return getTarget(AbstractDungeon.player, m);
    }

    public final AbstractMonster getTarget(AbstractCreature p, AbstractCreature m)
    {
        List<AbstractCreature> mons = getTargets(p, m);
        return mons.size() > 0 ? EUIUtils.safeCast(mons.get(0), AbstractMonster.class) : null;
    }

    public final ArrayList<AbstractCreature> getTargets(AbstractCreature source, AbstractCreature target)
    {
        return getTargets(source, target, 1);
    }

    public final ArrayList<AbstractCreature> getTargets(AbstractCreature source, AbstractCreature target, int autoAmount)
    {
        ArrayList<AbstractCreature> targets = new ArrayList<>();
        switch (this)
        {
            case None:
            {
                targets.add(AbstractDungeon.player);
                break;
            }

            case Single:
            case SingleAlly:
            {
                if (target != null)
                {
                    targets.add(target);
                }
                break;
            }

            case AllEnemy:
            {
                targets.addAll(GameUtilities.getEnemies(true));
                break;
            }

            case Self:
            {
                targets.add(source);
                break;
            }

            case Any:
            {
                if (target != null)
                {
                    targets.add(target);
                }
                else
                {
                    final RandomizedList<AbstractCreature> list = new RandomizedList<>(GameUtilities.getAllCharacters(true));
                    while (list.size() > 0 && targets.size() < autoAmount)
                    {
                        targets.add(list.retrieve(GameUtilities.getRNG()));
                    }
                }
                break;
            }

            case RandomEnemy:
            {
                final RandomizedList<AbstractCreature> list = new RandomizedList<>(GameUtilities.getEnemies(true));
                while (list.size() > 0 && targets.size() < autoAmount)
                {
                    targets.add(list.retrieve(GameUtilities.getRNG()));
                }
                break;
            }

            case All:
            {
                targets.addAll(GameUtilities.getAllCharacters(true));
                break;
            }

            case AllAlly:
            {
                targets.addAll(GameUtilities.getSummons(true));
                break;
            }

            case Team:
            {
                targets.addAll(GameUtilities.getSummons(true));
                targets.add(AbstractDungeon.player);
                break;
            }

            case RandomAlly:
            {
                final RandomizedList<AbstractCreature> list = new RandomizedList<>(GameUtilities.getSummons(true));
                while (list.size() > 0 && targets.size() < autoAmount)
                {
                    targets.add(list.retrieve(GameUtilities.getRNG()));
                }
                break;
            }
        }

        return targets;
    }

    // These strings cannot be put in as an enum variable because cards are initialized before these strings are
    public final String getTitle()
    {
        switch (this)
        {
            case None:
                return PGR.core.strings.ctype_none;
            case AllEnemy:
                return PGR.core.strings.ctype_allEnemy;
            case AllAlly:
                return PGR.core.strings.ctype_allAlly;
            case Team:
                return PGR.core.strings.ctype_team;
            case All:
                return PGR.core.strings.ctype_allCharacter;
            case Self:
                return PGR.core.strings.ctype_self;
            case Single:
                return PGR.core.strings.ctype_singleTarget;
            case SingleAlly:
                return PGR.core.strings.ctype_singleAlly;
            case Any:
                return PGR.core.strings.ctype_any;
            case RandomEnemy:
                return PGR.core.strings.ctype_randomEnemy;
            case RandomAlly:
                return PGR.core.strings.ctype_randomAlly;
        }
        return "";
    }

    // These strings cannot be put in as an enum variable because cards are initialized before these strings are
    // TODO tags for other targeting types
    public final String getShortString()
    {
        switch (this)
        {
            case All:
                return PGR.core.strings.ctype_tagAll;
            case AllEnemy:
            case AllAlly:
            case Team:
                return PGR.core.strings.ctype_tagAoE;
            case RandomEnemy:
            case RandomAlly:
                return PGR.core.strings.ctype_tagRandom;
        }
        return null;
    }

    public final boolean targetsAllies()
    {
        switch (this)
        {
            case Single:
            case SingleAlly:
            case AllAlly:
            case Team:
            case All:
            case RandomAlly:
                return true;
        }
        return false;
    }

    public final boolean targetsEnemies()
    {
        switch (this)
        {
            case Single:
            case AllEnemy:
            case All:
            case RandomEnemy:
                return true;
        }
        return false;
    }

    public final boolean targetsSelf()
    {
        switch (this)
        {
            case Self:
            case Any:
            case Team:
                return true;
        }
        return false;
    }

    public final boolean targetsSingle()
    {
        switch (this)
        {
            case Single:
            case SingleAlly:
                return true;
        }
        return false;
    }

    public final boolean targetsMulti()
    {
        switch (this)
        {
            case All:
            case AllAlly:
            case Team:
            case AllEnemy:
                return true;
        }
        return false;
    }
}
