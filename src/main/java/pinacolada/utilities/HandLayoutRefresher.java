package pinacolada.utilities;

import com.megacrit.cardcrawl.actions.GameActionManager;
import pinacolada.interfaces.subscribers.OnPhaseChangedSubscriber;
import pinacolada.misc.CombatManager;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

// Copied and modified from STS-AnimatorMod
public class HandLayoutRefresher implements OnPhaseChangedSubscriber
{
    @Override
    public void onPhaseChanged(GameActionManager.Phase phase)
    {
        if (phase == GameActionManager.Phase.WAITING_ON_USER)
        {
            refresh();

            CombatManager.onPhaseChanged.unsubscribe(this);
        }
    }

    public void refresh()
    {
        if (GameUtilities.getCurrentRoom(false) != null)
        {
            player.hand.refreshHandLayout();
            player.hand.applyPowers();
            player.hand.glowCheck();
        }
    }
}
