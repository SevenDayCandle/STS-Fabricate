package pinacolada.skills.delay;

import extendedui.interfaces.delegates.ActionT1;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.PCLEffects;
import pinacolada.interfaces.subscribers.PCLCombatSubscriber;
import pinacolada.utilities.GameUtilities;

public abstract class DelayUse implements PCLCombatSubscriber {
    protected int baseTurns;
    protected int turns;
    protected PCLUseInfo capturedCardUseInfo;
    protected ActionT1<PCLUseInfo> onUse;

    public DelayUse(int turns, PCLUseInfo info, ActionT1<PCLUseInfo> action) {
        baseTurns = this.turns = turns;
        capturedCardUseInfo = info;
        onUse = action;
    }

    public static DelayUse turnEnd(PCLUseInfo info, ActionT1<PCLUseInfo> action) {
        return turnEnd(0, info, action);
    }

    public static DelayUse turnEnd(int amount, PCLUseInfo info, ActionT1<PCLUseInfo> action) {
        return new DelayUseEndOfTurnFirst(amount, info, action);
    }

    public static DelayUse turnEndLast(PCLUseInfo info, ActionT1<PCLUseInfo> action) {
        return turnEndLast(0, info, action);
    }

    public static DelayUse turnEndLast(int amount, PCLUseInfo info, ActionT1<PCLUseInfo> action) {
        return new DelayUseEndOfTurnLast(amount, info, action);
    }

    public static DelayUse turnStart(int amount, PCLUseInfo info, ActionT1<PCLUseInfo> action) {
        return new DelayUseStartOfTurn(amount, info, action);
    }

    public static DelayUse turnStartLast(int amount, PCLUseInfo info, ActionT1<PCLUseInfo> action) {
        return new DelayUseStartOfTurnPostDraw(amount, info, action);
    }

    protected void act() {
        turns -= 1;
        if (turns <= 0) {
            if (capturedCardUseInfo.card != null) {
                PCLEffects.Queue.showCardBriefly(capturedCardUseInfo.card.makeStatEquivalentCopy());
            }
            if (capturedCardUseInfo.target == null || GameUtilities.isDeadOrEscaped(capturedCardUseInfo.target)) {
                capturedCardUseInfo = CombatManager.playerSystem.generateInfo(capturedCardUseInfo.card, capturedCardUseInfo.source, GameUtilities.getRandomEnemy(true));
            }
            onUse.invoke(capturedCardUseInfo);
            CombatManager.unsubscribe(this);
        }
    }

    public void start() {
        turns = baseTurns;
        CombatManager.subscribe(this);
    }
}
