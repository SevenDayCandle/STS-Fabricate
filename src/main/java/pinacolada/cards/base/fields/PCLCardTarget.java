package pinacolada.cards.base.fields;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.utilities.TargetFilter;
import org.apache.commons.lang3.StringUtils;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PCLCardTarget implements Comparable<PCLCardTarget> {
    None(AbstractCard.CardTarget.NONE),
    All(AbstractCard.CardTarget.ALL),
    AllAlly(AbstractCard.CardTarget.ALL_ENEMY),
    AllEnemy(AbstractCard.CardTarget.ALL_ENEMY),
    Any(AbstractCard.CardTarget.SELF_AND_ENEMY),
    RandomAlly(AbstractCard.CardTarget.ALL_ENEMY),
    RandomEnemy(AbstractCard.CardTarget.ALL_ENEMY),
    Self(AbstractCard.CardTarget.SELF),
    Single(AbstractCard.CardTarget.ENEMY),
    SingleAlly(AbstractCard.CardTarget.ENEMY),
    Team(AbstractCard.CardTarget.SELF);

    public static final TargetFilter T_AllAlly = new TargetFilter(PGR.core.strings.ctype_allAlly);
    public static final TargetFilter T_RandomAlly = new TargetFilter(PGR.core.strings.ctype_randomAlly);
    public static final TargetFilter T_RandomEnemy = new TargetFilter(PGR.core.strings.ctype_randomEnemy);
    public static final TargetFilter T_SingleAlly = new TargetFilter(PGR.core.strings.ctype_singleAlly);
    public static final TargetFilter T_Team = new TargetFilter(PGR.core.strings.ctype_team);

    public static AbstractCreature source;
    public static AbstractCreature target;
    public final AbstractCard.CardTarget cardTarget;

    PCLCardTarget(AbstractCard.CardTarget cardTarget) {
        this.cardTarget = cardTarget;
    }

    public static List<PCLCardTarget> getAll() {
        return Arrays.stream(PCLCardTarget.values()).sorted((a, b) -> StringUtils.compare(a.getTitle(), b.getTitle())).collect(Collectors.toList());
    }

    public static ArrayList<AbstractCreature> getPlayerTeam() {
        final ArrayList<AbstractCreature> targets = new ArrayList<AbstractCreature>(GameUtilities.getSummons(true));
        targets.add(AbstractDungeon.player);
        return targets;
    }

    public static ArrayList<AbstractCreature> getRandomTargets(List<? extends AbstractCreature> source, int autoAmount) {
        final RandomizedList<AbstractCreature> list = new RandomizedList<>(source);
        final ArrayList<AbstractCreature> targets = new ArrayList<>();
        while (list.size() > 0 && targets.size() < autoAmount) {
            targets.add(list.retrieve(GameUtilities.getRNG()));
        }
        return targets;
    }

    public final boolean evaluateTargets(AbstractCreature source, AbstractCreature target, FuncT1<Boolean, AbstractCreature> tFunc) {
        return evaluateTargets(getTargetsForEvaluation(source, target), tFunc);
    }

    public final boolean evaluateTargets(Iterable<? extends AbstractCreature> targets, FuncT1<Boolean, AbstractCreature> tFunc) {
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
                return GameUtilities.isEnemy(source) ? getPlayerTeam() : GameUtilities.getEnemies(true);
            }
            case Self: {
                return EUIUtils.arrayList(source);
            }
            case Any: {
                if (target != null) {
                    return EUIUtils.arrayList(target);
                }
                else {
                    return getRandomTargets(GameUtilities.getAllCharacters(true), autoAmount);
                }
            }
            case RandomEnemy: {
                return getRandomTargets(GameUtilities.isEnemy(source) ? getPlayerTeam() : GameUtilities.getEnemies(true), autoAmount);
            }
            case All: {
                return GameUtilities.getAllCharacters(true);
            }
            case AllAlly: {
                return GameUtilities.isEnemy(source) ? GameUtilities.getEnemies(true) : GameUtilities.getSummons(true);
            }
            case Team: {
                return GameUtilities.isEnemy(source) ? GameUtilities.getEnemies(true) : getPlayerTeam();
            }
            case RandomAlly: {
                return getRandomTargets(GameUtilities.isEnemy(source) ? GameUtilities.getEnemies(true) : GameUtilities.getSummons(true), autoAmount);
            }
        }

        return new ArrayList<>();
    }

    public final ArrayList<? extends AbstractCreature> getTargetsForEvaluation(AbstractCreature source, AbstractCreature target) {
        switch (this) {
            case RandomAlly:
                return GameUtilities.isEnemy(source) ? GameUtilities.getEnemies(true) : GameUtilities.getSummons(true);
            case RandomEnemy:
                return GameUtilities.isEnemy(source) ? getPlayerTeam() : GameUtilities.getEnemies(true);
            case Any:
                return GameUtilities.getAllCharacters(true);
        }
        return getTargets(source, target);
    }

    public TargetFilter getTargetFilter() {
        switch (this) {
            case None:
                return TargetFilter.None;
            case AllEnemy:
                return TargetFilter.AllEnemy;
            case AllAlly:
                return T_AllAlly;
            case Team:
                return T_Team;
            case All:
                return TargetFilter.All;
            case Self:
                return TargetFilter.Self;
            case Single:
                return TargetFilter.Single;
            case SingleAlly:
                return T_SingleAlly;
            case Any:
                return TargetFilter.Any;
            case RandomEnemy:
                return T_RandomEnemy;
            case RandomAlly:
                return T_RandomAlly;
        }
        return TargetFilter.None;
    }

    // These strings cannot be put in as an enum variable because cards are initialized before these strings are
    public final String getTitle() {
        switch (this) {
            case None:
                return EUIRM.strings.target_none;
            case AllEnemy:
                return EUIRM.strings.target_allEnemy;
            case AllAlly:
                return PGR.core.strings.ctype_allAlly;
            case Team:
                return PGR.core.strings.ctype_team;
            case All:
                return EUIRM.strings.target_allCharacter;
            case Self:
                return EUIRM.strings.target_self;
            case Single:
                return EUIRM.strings.target_singleTarget;
            case SingleAlly:
                return PGR.core.strings.ctype_singleAlly;
            case Any:
                return EUIRM.strings.target_any;
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
