package pinacolada.skills.delay;

import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.ColoredString;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.PCLEffects;
import pinacolada.interfaces.subscribers.PCLCombatSubscriber;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.utilities.GameUtilities;

import java.util.Collection;
import java.util.PriorityQueue;

public abstract class DelayUse implements PCLCombatSubscriber, Comparable<DelayUse> {
    protected static final PriorityQueue<DelayUse> DELAYS = new PriorityQueue<>();
    protected int baseTurns;
    protected int turns;
    protected PCLUseInfo capturedCardUseInfo;
    protected ActionT1<PCLUseInfo> onUse;
    protected EUITooltip tip;

    public DelayUse(int turns, PCLUseInfo info, ActionT1<PCLUseInfo> action, String title, String description) {
        baseTurns = this.turns = turns;
        capturedCardUseInfo = info;
        onUse = action;
        this.tip = new EUITooltip(title, description);
        this.tip.setSubheader(new ColoredString());
    }

    public static void clear() {
        DELAYS.clear();
    }

    public static int delayCount() {
        return DELAYS.size();
    }

    public static Collection<EUITooltip> getTooltips() {
        return EUIUtils.map(DELAYS, DelayUse::getTooltip);
    }

    public static int minTurns() {
        return DELAYS.size() > 0 ? DELAYS.peek().turns : 0;
    }

    public static DelayUse turnEnd(int amount, PCLUseInfo info, ActionT1<PCLUseInfo> action, String title, String description) {
        return new DelayUseEndOfTurnFirst(amount, info, action, title, description);
    }

    public static DelayUse turnEndLast(int amount, PCLUseInfo info, ActionT1<PCLUseInfo> action, String title, String description) {
        return new DelayUseEndOfTurnLast(amount, info, action, title, description);
    }

    public static DelayUse turnStart(int amount, PCLUseInfo info, ActionT1<PCLUseInfo> action, String title, String description) {
        return new DelayUseStartOfTurn(amount, info, action, title, description);
    }

    public static DelayUse turnStartLast(int amount, PCLUseInfo info, ActionT1<PCLUseInfo> action, String title, String description) {
        return new DelayUseStartOfTurnPostDraw(amount, info, action, title, description);
    }

    protected void act() {
        turns -= 1;
        if (turns <= 0) {
            if (capturedCardUseInfo != null) {
                if (capturedCardUseInfo.card != null) {
                    PCLEffects.Queue.showCardBriefly(capturedCardUseInfo.card.makeStatEquivalentCopy());
                }
                if (capturedCardUseInfo.target == null || GameUtilities.isDeadOrEscaped(capturedCardUseInfo.target)) {
                    capturedCardUseInfo = CombatManager.playerSystem.generateInfo(capturedCardUseInfo.card, capturedCardUseInfo.source, GameUtilities.getRandomEnemy(true));
                }
            }
            onUse.invoke(capturedCardUseInfo);
            CombatManager.unsubscribe(this);
            DELAYS.remove(this);
        }
    }

    @Override
    public int compareTo(DelayUse o) {
        if (o.turns == this.turns) {
            return this.getTiming().ordinal() - o.getTiming().ordinal();
        }
        return this.turns - o.turns;
    }

    public EUITooltip getTooltip() {
        this.tip.subHeader.text = EUIRM.strings.numNoun(turns, PCLCoreStrings.pluralEvaluated(PGR.core.strings.combat_turns, turns));
        this.tip.subHeader.color = turns <= 0 ? Settings.GREEN_TEXT_COLOR : Settings.RED_TEXT_COLOR;
        return tip;
    }

    public void start() {
        turns = baseTurns;
        CombatManager.subscribe(this);
        DELAYS.add(this);
    }

    public abstract DelayTiming getTiming();
}
