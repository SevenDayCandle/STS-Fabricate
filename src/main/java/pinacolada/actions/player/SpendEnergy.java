package pinacolada.actions.player;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import pinacolada.actions.PCLActionWithCallbackT2;
import pinacolada.misc.CombatManager;

// Copied and modified from STS-AnimatorMod
public class SpendEnergy extends PCLActionWithCallbackT2<Integer, Integer>
{
    protected boolean canSpendLess;

    public SpendEnergy(int amount, boolean canSpendLess)
    {
        super(ActionType.ENERGY);

        this.canSpendLess = canSpendLess;

        initialize(amount);
    }

    @Override
    protected void firstUpdate()
    {
        int energy = EnergyPanel.getCurrentEnergy();
        if (energy >= amount || canSpendLess)
        {
            energy = Math.min(energy, amount);
            if (checkConditions(energy))
            {
                int finalEnergy = CombatManager.onTrySpendEnergy(null, AbstractDungeon.player, energy);
                player.loseEnergy(finalEnergy);
                complete(finalEnergy);
                return;
            }
        }

        complete();
    }
}
