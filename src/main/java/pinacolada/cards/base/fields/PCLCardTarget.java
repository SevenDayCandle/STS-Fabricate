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
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum PCLCardTarget implements Comparable<PCLCardTarget> {
    None(AbstractCard.CardTarget.NONE),
    All(AbstractCard.CardTarget.ALL),
    AllAlly(AbstractCard.CardTarget.ALL_ENEMY),
    AllEnemy(AbstractCard.CardTarget.ALL_ENEMY),
    Any(AbstractCard.CardTarget.SELF_AND_ENEMY),
    RandomAlly(AbstractCard.CardTarget.ALL),
    RandomEnemy(AbstractCard.CardTarget.ALL_ENEMY),
    Self(AbstractCard.CardTarget.SELF),
    Single(AbstractCard.CardTarget.ENEMY),
    SingleAlly(AbstractCard.CardTarget.ENEMY),
    Team(AbstractCard.CardTarget.ALL_ENEMY);

    public static AbstractCreature source;
    public static AbstractCreature target;
    public final AbstractCard.CardTarget cardTarget;

    PCLCardTarget(AbstractCard.CardTarget cardTarget) {
        this.cardTarget = cardTarget;
    }

    public static List<PCLCardTarget> getAll() {
        return Arrays.stream(PCLCardTarget.values()).sorted((a, b) -> StringUtils.compare(a.getTitle(), b.getTitle())).collect(Collectors.toList());
    }

    public final boolean evaluateTargets(AbstractCreature source, AbstractCreature target, Predicate<AbstractCreature> tFunc) {
        return evaluateTargets(getTargetsForEvaluation(source, target), tFunc);
    }

    public final boolean evaluateTargets(Iterable<? extends AbstractCreature> targets, Predicate<AbstractCreature> tFunc) {
        switch (this) {
            case AllAlly:
            case All:
            case AllEnemy:
                return EUIUtils.all(targets, tFunc);
        }
        return EUIUtils.any(targets, tFunc);
    }

    // These strings cannot be put in as an enum variable because cards are initialized before these strings are
    public final String getShortString() {
        switch (this) {
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

    public final AbstractMonster getTarget(AbstractCreature m) {
        return getTarget(AbstractDungeon.player, m);
    }

    public final AbstractMonster getTarget(AbstractCreature p, AbstractCreature m) {
        List<? extends AbstractCreature> mons = getTargets(p, m);
        return mons.size() > 0 ? EUIUtils.safeCast(mons.get(0), AbstractMonster.class) : null;
    }

    public final ArrayList<? extends AbstractCreature> getTargets(AbstractCreature source, AbstractCreature target) {
        return getTargets(source, target, 1);
    }

    public final ArrayList<? extends AbstractCreature> getTargets(AbstractCreature source, AbstractCreature target, int autoAmount) {
        switch (this) {
            case None: {
                return EUIUtils.arrayList(AbstractDungeon.player);
            }
            case Single:
            case SingleAlly: {
                if (target != null) {
                    return EUIUtils.arrayList(target);
                }
                return EUIUtils.arrayList();
            }
            case AllEnemy: {
                return GameUtilities.getEnemies(true);
            }
            case Self: {
                return EUIUtils.arrayList(source);
            }
            case Any: {
                if (target != null) {
                    return EUIUtils.arrayList(target);
                }
                else {
                    final RandomizedList<AbstractCreature> list = new RandomizedList<>(GameUtilities.getAllCharacters(true));
                    final ArrayList<AbstractCreature> targets = new ArrayList<>();
                    while (list.size() > 0 && targets.size() < autoAmount) {
                        targets.add(list.retrieve(GameUtilities.getRNG()));
                    }
                    return targets;
                }
            }
            case RandomEnemy: {
                final RandomizedList<AbstractCreature> list = new RandomizedList<>(GameUtilities.getEnemies(true));
                final ArrayList<AbstractCreature> targets = new ArrayList<>();
                while (list.size() > 0 && targets.size() < autoAmount) {
                    targets.add(list.retrieve(GameUtilities.getRNG()));
                }
                return targets;
            }
            case All: {
                return GameUtilities.getAllCharacters(true);
            }
            case AllAlly: {
                return GameUtilities.getSummons(true);
            }
            case Team: {
                final ArrayList<AbstractCreature> targets = new ArrayList<AbstractCreature>(GameUtilities.getSummons(true));
                targets.add(AbstractDungeon.player);
                return targets;
            }
            case RandomAlly: {
                final RandomizedList<AbstractCreature> list = new RandomizedList<>(GameUtilities.getSummons(true));
                final ArrayList<AbstractCreature> targets = new ArrayList<>();
                while (list.size() > 0 && targets.size() < autoAmount) {
                    targets.add(list.retrieve(GameUtilities.getRNG()));
                }
                return targets;
            }
        }

        return new ArrayList<>();
    }

    public final ArrayList<? extends AbstractCreature> getTargetsForEvaluation(AbstractCreature source, AbstractCreature target) {
        switch (this) {
            case RandomAlly:
                return GameUtilities.getSummons(true);
            case RandomEnemy:
                GameUtilities.getEnemies(true);
            case Any:
                return GameUtilities.getAllCharacters(true);
        }
        return getTargets(source, target);
    }

    // These strings cannot be put in as an enum variable because cards are initialized before these strings are
    public final String getTitle() {
        switch (this) {
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

    public final boolean targetsAllies() {
        switch (this) {
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

    public final boolean targetsEnemies() {
        switch (this) {
            case Single:
            case AllEnemy:
            case All:
            case RandomEnemy:
                return true;
        }
        return false;
    }

    public final boolean targetsMulti() {
        switch (this) {
            case All:
            case AllAlly:
            case Team:
            case AllEnemy:
                return true;
        }
        return false;
    }

    public final boolean targetsSelf() {
        switch (this) {
            case Self:
            case Any:
            case Team:
                return true;
        }
        return false;
    }

    public final boolean targetsSingle() {
        switch (this) {
            case Single:
            case SingleAlly:
                return true;
        }
        return false;
    }

    public final boolean targetsRandom() {
        switch (this) {
            case RandomAlly:
            case RandomEnemy:
            case Any:
                return true;
        }
        return false;
    }

    public final boolean vanillaCompatible() {
        switch (this) {
            case SingleAlly:
            case AllAlly:
            case Team:
            case RandomAlly:
                return false;
        }
        return true;
    }
}
