package pinacolada.powers.common;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisiblePower;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.PCLSFX;
import pinacolada.effects.VFX;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

@VisiblePower
public class SelfImmolationPower extends PCLPower {
    public static final PCLPowerData DATA = register(SelfImmolationPower.class)
            .setType(PowerType.DEBUFF)
            .setEndTurnBehavior(PCLPowerData.Behavior.TurnBased)
            .setTooltip(PGR.core.tooltips.selfImmolation);

    public SelfImmolationPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(DATA, owner, source, amount);
    }

    private void applyDebuff(int amount) {
        if (amount > 0) {
            for (AbstractCreature cr : GameUtilities.getAllCharacters(true)) {
                PCLActions.bottom.applyPower(new DelayedDamagePower(cr, owner, amount, PCLEnum.AttackEffect.CLAW));
            }
        }
    }

    @Override
    public void atStartOfTurnPostDraw() {
        super.atStartOfTurnPostDraw();
        if (justApplied) {
            justApplied = false;
        }
        else {
            reducePower(1);
        }

    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();

        PCLEffects.Queue.add(VFX.bleed(owner.hb));
        PCLSFX.play(PCLSFX.PCL_SPRAY, 1f, 1.15f, 0.95f);
    }

    @Override
    public void onPlayCard(AbstractCard card, AbstractMonster m) {
        super.onPlayCard(card, m);
        if (card.block > 0) {
            applyDebuff(card.block * amount);
            this.flash();
        }
    }

    @Override
    public void playApplyPowerSfx() {
        PCLSFX.play(PCLSFX.HEART_BEAT, 1f, 1.15f, 0.95f);
    }
}
