package pinacolada.powers.common;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.actions.PCLActions;
import pinacolada.effects.PCLSFX;
import pinacolada.powers.PCLPower;

public class DeflectionPower extends PCLPower {
    public static final String POWER_ID = createFullID(DeflectionPower.class);

    public DeflectionPower(AbstractCreature owner, int amount) {
        super(owner, POWER_ID);

        initialize(amount, PowerType.BUFF, false);
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        super.atEndOfTurn(isPlayer);
        reducePower(1);
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        int blockedAmount = info.output - damageAmount;
        if (info.type == DamageInfo.DamageType.NORMAL && blockedAmount > 0 && info.owner != null) {
            PCLActions.bottom.dealDamage(owner, info.owner, blockedAmount, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
        }

        return super.onAttacked(info, damageAmount);
    }

    @Override
    public void playApplyPowerSfx() {
        PCLSFX.play(PCLSFX.ATTACK_IRON_1, 1.25f, 1.35f, 0.7f);
    }
}