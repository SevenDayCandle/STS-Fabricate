package pinacolada.actions.player;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.actions.PCLConditionalAction;
import pinacolada.dungeon.CombatManager;

// Copied and modified from STS-AnimatorMod
public class SpendEnergy extends PCLConditionalAction<Integer, Integer> {
    protected boolean canSpendLess;

    public SpendEnergy(int amount, boolean canSpendLess) {
        super(ActionType.ENERGY);

        this.canSpendLess = canSpendLess;

        initialize(amount);
    }

    @Override
    protected void firstUpdate() {
        int energy = EnergyPanel.getCurrentEnergy();
        if (energy >= amount || canSpendLess) {
            energy = Math.min(energy, amount);
            if (checkCondition(energy)) {
                int finalEnergy = CombatManager.onTrySpendEnergy(null, AbstractDungeon.player, energy);
                player.energy.use(finalEnergy);
                complete(finalEnergy);
                return;
            }
        }

        completeImpl();
    }

    public SpendEnergy setCondition(FuncT1<Boolean, Integer> condition) {
        super.setCondition(condition);

        return this;
    }
}
