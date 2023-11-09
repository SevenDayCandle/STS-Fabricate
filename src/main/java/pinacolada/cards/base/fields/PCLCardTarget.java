package pinacolada.cards.base.fields;

import com.megacrit.cardcrawl.cards.AbstractCard;
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
    AllAllyEnemy,
    AllEnemy,
    Any,
    RandomAlly,
    RandomAllyEnemy,
    RandomEnemy,
    Self,
    SelfAllEnemy,
    SelfPlayer,
    SelfSingle,
    SelfSingleAlly,
    Single,
    SingleAlly,
    Team,
    UseParent;

    public static final String S_SelfAllEnemy = selfPlus(PGR.core.strings.ctype_tagAoE);
    public static final String S_SelfSingle = selfPlus(PGR.core.strings.ctype_tagSingle);
    public static final String S_SelfSingleAlly = selfPlus(PGR.core.strings.ctype_tagAlly);
    public static final TargetFilter T_AllAlly = new TargetFilter(PGR.core.strings.ctype_allAlly);
    public static final TargetFilter T_AllAllyEnemy = new TargetFilter(PGR.core.strings.ctype_allAllyEnemy);
    public static final TargetFilter T_RandomAlly = new TargetFilter(PGR.core.strings.ctype_randomAlly);
    public static final TargetFilter T_RandomAllyEnemy = new TargetFilter(PGR.core.strings.ctype_randomAllyEnemy);
    public static final TargetFilter T_RandomEnemy = new TargetFilter(PGR.core.strings.ctype_randomEnemy);
    public static final TargetFilter T_SelfAllEnemy = new TargetFilter(selfPlus(EUIRM.strings.target_allEnemy));
    public static final TargetFilter T_SelfPlayer = new TargetFilter(selfPlus(EUIRM.strings.target_none));
    public static final TargetFilter T_SelfSingle = new TargetFilter(selfPlus(EUIRM.strings.target_singleTarget));
    public static final TargetFilter T_SelfSingleAlly = new TargetFilter(selfPlus(PGR.core.strings.ctype_singleAlly));
    public static final TargetFilter T_SingleAlly = new TargetFilter(PGR.core.strings.ctype_singleAlly);
    public static final TargetFilter T_Team = new TargetFilter(PGR.core.strings.ctype_team);

    public static void fillWithPlayerTeam(ArrayList<AbstractCreature> targets, boolean isAlive) {
        GameUtilities.fillWithSummons(true, targets);
        targets.add(AbstractDungeon.player);
    }

    public static PCLCardTarget forVanilla(AbstractCard.CardTarget target) {
        switch (target) {
            case ENEMY:
                return PCLCardTarget.Single;
            case SELF_AND_ENEMY:
                return PCLCardTarget.Any;
            case ALL:
            case ALL_ENEMY:
                return PCLCardTarget.AllEnemy;
            case SELF:
                return PCLCardTarget.Self;
        }
        return PCLCardTarget.None;
    }

    public static List<PCLCardTarget> getAll() {
        return Arrays.stream(PCLCardTarget.values()).sorted((a, b) -> StringUtils.compare(a.getTitle(), b.getTitle())).collect(Collectors.toList());
    }

    public static void popRandomTargets(RandomizedList<? extends AbstractCreature> source, int allowedSize) {
        while (source.size() > allowedSize) {
            source.retrieve(AbstractDungeon.cardRandomRng);
        }
    }

    private static String selfPlus(String targ) {
        return EUIUtils.withSlash(EUIRM.strings.target_self, targ);
    }

    public final boolean evaluateTargets(PCLUseInfo info, FuncT1<Boolean, AbstractCreature> tFunc) {
        fillTargetsForEvaluation(info.source, info.target, info.tempTargets);
        int prevSize = info.tempTargets.size();
        EUIUtils.filterInPlace(info.tempTargets, tFunc);
        switch (this) {
            case AllAlly:
            case All:
            case AllEnemy:
            case AllAllyEnemy:
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
                return PGR.core.strings.ctype_tagAoEAlly;
            case Team:
                return PGR.core.strings.ctype_team;
            case AllAllyEnemy:
            case AllEnemy:
            case SelfAllEnemy:
                return PGR.core.strings.ctype_tagAoE;
            case RandomEnemy:
            case RandomAlly:
            case RandomAllyEnemy:
                return PGR.core.strings.ctype_tagRandom;
        }
        return null;
    }

    public final String getShortStringForTag() {
        switch (this) {
            case All:
                return PGR.core.strings.ctype_tagAll;
            case AllAlly:
                return PGR.core.strings.ctype_tagAoEAlly;
            case Team:
                return PGR.core.strings.ctype_team;
            case AllEnemy:
                return PGR.core.strings.ctype_tagAoE;
            case Single:
                return PGR.core.strings.ctype_tagSingle;
            case RandomEnemy:
                return PGR.core.strings.ctype_tagRandom;
            case SelfAllEnemy:
                return S_SelfAllEnemy;
            case SelfSingle:
                return S_SelfSingle;
            case SelfSingleAlly:
                return S_SelfSingleAlly;
        }
        return getTitle();
    }

    public final AbstractCreature getTarget(PCLUseInfo info, int autoAmount) {
        List<? extends AbstractCreature> mons = getTargets(info, autoAmount);
        return mons.size() > 0 ? mons.get(0) : null;
    }

    public final AbstractCreature getTarget(AbstractCreature p, AbstractCreature m) {
        List<? extends AbstractCreature> mons = getTargets(p, m);
        return mons.size() > 0 ? mons.get(0) : null;
    }

    public TargetFilter getTargetFilter() {
        switch (this) {
            case None:
                return TargetFilter.None;
            case AllEnemy:
                return TargetFilter.AllEnemy;
            case AllAlly:
                return T_AllAlly;
            case AllAllyEnemy:
                return T_AllAllyEnemy;
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
            case RandomAllyEnemy:
                return T_RandomAllyEnemy;
            case SelfAllEnemy:
                return T_SelfAllEnemy;
            case SelfPlayer:
                return T_SelfPlayer;
            case SelfSingle:
                return T_SelfSingle;
            case SelfSingleAlly:
                return T_SelfSingleAlly;
        }
        return TargetFilter.None;
    }

    public final ArrayList<AbstractCreature> getTargets(PCLUseInfo info, int autoAmount) {
        return getTargets(info.source, info.target, info.tempTargets, autoAmount);
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
                    sourceList.remove(source);
                }
                else {
                    GameUtilities.fillWithSummons(true, sourceList);
                }
                break;
            }
            case AllAllyEnemy: {
                if (GameUtilities.isEnemy(source)) {
                    GameUtilities.fillWithEnemies(true, sourceList);
                    fillWithPlayerTeam(sourceList, true);
                    sourceList.remove(source);
                }
                else {
                    GameUtilities.fillWithEnemies(true, sourceList);
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
                    sourceList.remove(source);
                }
                else {
                    GameUtilities.fillWithSummons(true, sourceList);
                }
                popRandomTargets(sourceList, autoAmount);
                break;
            }
            case RandomAllyEnemy: {
                if (GameUtilities.isEnemy(source)) {
                    GameUtilities.fillWithEnemies(true, sourceList);
                    fillWithPlayerTeam(sourceList, true);
                    sourceList.remove(source);
                }
                else {
                    GameUtilities.fillWithEnemies(true, sourceList);
                    GameUtilities.fillWithSummons(true, sourceList);
                }
                popRandomTargets(sourceList, autoAmount);
                break;
            }
            case SelfPlayer:
                if (source != AbstractDungeon.player) {
                    sourceList.addAll(source, AbstractDungeon.player);
                }
                else {
                    sourceList.add(AbstractDungeon.player);
                }
                break;
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
        return this == UseParent ? PGR.core.strings.cedit_useParent : getTargetFilter().name;
    }

    public final boolean targetsAllies() {
        switch (this) {
            case Single:
            case SingleAlly:
            case AllAlly:
            case AllAllyEnemy:
            case Team:
            case All:
            case RandomAlly:
            case RandomAllyEnemy:
            case SelfSingleAlly:
            case Any:
                return true;
        }
        return false;
    }

    public final boolean targetsEnemies() {
        switch (this) {
            case Single:
            case AllAllyEnemy:
            case AllEnemy:
            case All:
            case RandomAllyEnemy:
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
            case AllAllyEnemy:
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
            case RandomAllyEnemy:
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
            case All:
            case Team:
            case SelfAllEnemy:
            case SelfPlayer:
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
            case AllAlly:
            case AllAllyEnemy:
            case RandomAlly:
            case RandomAllyEnemy:
            case SelfPlayer:
            case SelfSingleAlly:
            case SingleAlly:
            case Team:
                return false;
        }
        return true;
    }
}
