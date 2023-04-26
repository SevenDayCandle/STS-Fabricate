package pinacolada.powers.common;

import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.powers.RegrowPower;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.powers.PCLPower;

public class StolenGoldPower extends PCLPower {
    public static final String POWER_ID = createFullID(StolenGoldPower.class);
    public static final int GOLD_BOSS = 75;
    public static final int GOLD_ELITE = 50;
    public static final int GOLD_NORMAL = 25;

    public StolenGoldPower(AbstractCreature owner, int amount) {
        super(owner, POWER_ID);

        final AbstractMonster m = EUIUtils.safeCast(owner, AbstractMonster.class);
        if (m == null || m.hasPower(MinionPower.POWER_ID) || m.hasPower(RegrowPower.POWER_ID)) {
            maxAmount = 0;
        }
        else if (m.type == AbstractMonster.EnemyType.BOSS) {
            maxAmount = GOLD_BOSS;
        }
        else if (m.type == AbstractMonster.EnemyType.ELITE) {
            maxAmount = GOLD_ELITE;
        }
        else {
            maxAmount = GOLD_NORMAL;
        }

        initialize(amount, NeutralPowertypePatch.NEUTRAL, false);
    }

    @Override
    public String getUpdatedDescription() {
        return formatDescription(0, amount, (maxAmount - this.amount));
    }

    @Override
    public void stackPower(int stackAmount) {
        int initialGold = this.amount;

        super.stackPower(stackAmount);

        int goldGain = this.amount - initialGold;
        if (goldGain > 0) {
            PCLActions.top.gainGold(goldGain);
        }
    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();

        PCLActions.top.gainGold(amount);
    }
}
