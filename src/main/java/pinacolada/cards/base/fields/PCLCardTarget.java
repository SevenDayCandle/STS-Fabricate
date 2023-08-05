package pinacolada.cards.base.fields;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.utilities.TargetFilter;
import org.apache.commons.lang3.StringUtils;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PCLCardTarget implements Comparable<PCLCardTarget> {
    None,
    All,
    AllAlly,
    AllEnemy,
    Any,
    RandomAlly,
    RandomEnemy,
    Self,
    SelfAllEnemy,
    SelfSingle,
    SelfSingleAlly,
    Single,
    SingleAlly,
    Team,
    UseParent;

    public static final TargetFilter T_AllAlly = new TargetFilter(PGR.core.strings.ctype_allAlly);
    public static final TargetFilter T_RandomAlly = new TargetFilter(PGR.core.strings.ctype_randomAlly);
    public static final TargetFilter T_RandomEnemy = new TargetFilter(PGR.core.strings.ctype_randomEnemy);
    public static final TargetFilter T_SelfAllEnemy = new TargetFilter(PGR.core.strings.ctype_selfAllEnemy);
    public static final TargetFilter T_SelfSingle = new TargetFilter(PGR.core.strings.ctype_selfSingle);
    public static final TargetFilter T_SelfSingleAlly = new TargetFilter(PGR.core.strings.ctype_selfSingleAlly);
    public static final TargetFilter T_SingleAlly = new TargetFilter(PGR.core.strings.ctype_singleAlly);
    public static final TargetFilter T_Team = new TargetFilter(PGR.core.strings.ctype_team);

    public static void fillWithPlayerTeam(ArrayList<AbstractCreature> targets, boolean isAlive) {
        GameUtilities.fillWithSummons(true, targets);
        targets.add(AbstractDungeon.player);
    }

    public static List<PCLCardTarget> getAll() {
        return Arrays.stream(PCLCardTarget.values()).sorted((a, b) -> StringUtils.compare(a.getTitle(), b.getTitle())).collect(Collectors.toList());
    }

    public static void popRandomTargets(RandomizedList<? extends AbstractCreature> source, int allowedSize) {
        while (source.size() > allowedSize) {
            source.retrieve(GameUtilities.getRNG());
        }
    }

    public final boolean evaluateTargets(PCLUseInfo info, FuncT1<Boolean, AbstractCreature> tFunc) {
        fillTargetsForEvaluation(info.source, info.target, info.tempTargets);
        int prevSize = info.tempTargets.size();
        EUIUtils.filterInPlace(info.tempTargets, tFunc);
        switch (this) {
            case AllAlly:
            case All:
            case AllEnemy:
                return info.tempTargets.size() == prevSize;
        }
        return info.tempTargets.size() > 0;
    }

    public final void fillTargetsForEvaluation(AbstractCreature source, AbstractCreature target, RandomizedList<AbstractCreature> sourceList) {
        sourceList.clear();
        switch (this) {
            case RandomAlly:
                if (GameUtilities.isEnemy(source)) {
                    GameUtilities.fillWithEnemies(true, sourceList);
                }
                else {
                    GameUtilities.fillWithSummons(true, sourceList);
                }
                break;
            case RandomEnemy:
                if (GameUtilities.isEnemy(source)) {
                    fillWithPlayerTeam(sourceList, true);
                }
                else {
                    GameUtilities.fillWithEnemies(true, sourceList);
                }
                break;
            case Any:
                GameUtilities.fillWithAllCharacters(true, sourceList);
                break;
            default:
                getTargets(source, target, sourceList, 1);
        }
    }

    // These strings cannot be put in as an enum variable because cards are initialized before these strings are
    public final String getShortString() {
        switch (this) {
            case All:
                return PGR.core.strings.ctype_tagAll;
            case AllAlly:
            case Team:
                return PGR.core.strings.ctype_team;
            case AllEnemy:
            case SelfAllEnemy:
                return PGR.core.strings.ctype_tagAoE;
            case RandomEnemy:
            case RandomAlly:
                return PGR.core.strings.ctype_tagRandom;
        }
        return null;
    }

    public final AbstractCreature getTarget(AbstractCreature m) {
        return getTarget(AbstractDungeon.player, m);
    }

    public final AbstractCreature getTarget(AbstractCreature p, AbstractCreature m) {
        List<? extends AbstractCreature> mons = getTargets(p, m);
        return mons.size() > 0 ? mons.get(0): null;
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
            case SelfAllEnemy:
                return T_SelfAllEnemy;
            case SelfSingle:
                return T_SelfSingle;
            case SelfSingleAlly:
                return T_SelfSingleAlly;
        }
        return TargetFilter.None;
    }

    public final ArrayList<AbstractCreature> getTargets(PCLUseInfo info) {
        return getTargets(info.source, info.target, info.tempTargets, 1);
    }

    public final ArrayList<AbstractCreature> getTargets(AbstractCreature source, AbstractCreature target) {
        return getTargets(source, target, new RandomizedList<>(), 1);
    }

    public final ArrayList<AbstractCreature> getTargets(AbstractCreature source, AbstractCreature target, RandomizedList<AbstractCreature> sourceList) {
        return getTargets(source, target, sourceList, 1);
    }

    public final ArrayList<AbstractCreature> getTargets(AbstractCreature source, AbstractCreature target, RandomizedList<AbstractCreature> sourceList, int autoAmount) {
        if (this == UseParent) {
            return sourceList;
        }
        sourceList.clear();
        switch (this) {
            case None: {
                sourceList.add(AbstractDungeon.player);
                break;
            }
            case Single:
            case SingleAlly: {
                if (target != null) {
                    sourceList.add(target);
                }
                break;
            }
            case SelfAllEnemy: {
                sourceList.add(source);
            }
            case AllEnemy: {
                if (GameUtilities.isEnemy(source)) {
                    fillWithPlayerTeam(sourceList, true);
                }
                else {
                    GameUtilities.fillWithEnemies(true, sourceList);
                }
                break;
            }
            case Self: {
                sourceList.add(source);
                break;
            }
            case Any: {
                if (target != null) {
                    sourceList.add(target);
                }
                else {
                    GameUtilities.fillWithAllCharacters(true, sourceList);
                    popRandomTargets(sourceList, autoAmount);
                }
                break;
            }
            case RandomEnemy: {
                if (GameUtilities.isEnemy(source)) {
                    fillWithPlayerTeam(sourceList, true);
                }
                else {
                    GameUtilities.fillWithEnemies(true, sourceList);
                }
                popRandomTargets(sourceList, autoAmount);
                break;
            }
            case All: {
                GameUtilities.fillWithAllCharacters(true, sourceList);
                break;
            }
            case AllAlly: {
                if (GameUtilities.isEnemy(source)) {
                    GameUtilities.fillWithEnemies(true, sourceList);
                }
                else {
                    GameUtilities.fillWithSummons(true, sourceList);
                }
                break;
            }
            case Team: {
                if (GameUtilities.isEnemy(source)) {
                    GameUtilities.fillWithEnemies(true, sourceList);
                }
                else {
                    fillWithPlayerTeam(sourceList, true);
                }
                break;
            }
            case RandomAlly: {
                if (GameUtilities.isEnemy(source)) {
                    GameUtilities.fillWithEnemies(true, sourceList);
                }
                else {
                    GameUtilities.fillWithSummons(true, sourceList);
                }
                popRandomTargets(sourceList, autoAmount);
            }
            case SelfSingle:
            case SelfSingleAlly: {
                if (target != null) {
                    sourceList.addAll(source, target);
                }
                else {
                    sourceList.add(source);
                }
                break;
            }
        }

        return sourceList;
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
            case SelfAllEnemy:
                return PGR.core.strings.ctype_selfAllEnemy;
            case SelfSingle:
                return PGR.core.strings.ctype_selfSingle;
            case SelfSingleAlly:
                return PGR.core.strings.ctype_selfSingleAlly;
            case UseParent:
                return PGR.core.strings.cedit_useParent;
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
            case SelfSingleAlly:
            case Any:
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
            case SelfAllEnemy:
            case SelfSingle:
            case Any:
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
            case SelfAllEnemy:
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

    public final boolean targetsSelf() {
        switch (this) {
            case Self:
            case Any:
            case Team:
            case SelfAllEnemy:
            case SelfSingle:
            case SelfSingleAlly:
                return true;
        }
        return false;
    }

    public final boolean targetsSingle() {
        switch (this) {
            case Single:
            case SingleAlly:
            case SelfSingle:
            case SelfSingleAlly:
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
            case SelfSingleAlly:
                return false;
        }
        return true;
    }
}
