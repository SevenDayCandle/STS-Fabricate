package pinacolada.powers.special;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.actions.PCLActions;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.PCLSFX;
import pinacolada.effects.VFX;
import pinacolada.powers.PCLPower;
import pinacolada.resources.PCLEnum;
import pinacolada.utilities.GameUtilities;

public class SelfImmolationPower extends PCLPower {
    public static final String POWER_ID = createFullID(SelfImmolationPower.class);
    public boolean justApplied;

    public SelfImmolationPower(AbstractCreature owner, int amount) {
        this(owner, amount, false);
    }

    public SelfImmolationPower(AbstractCreature owner, int amount, boolean justApplied) {
        super(owner, POWER_ID);

        this.amount = amount;
        if (this.amount >= 9999) {
            this.amount = 9999;
        }
        initialize(amount, PowerType.DEBUFF, true);
        this.justApplied = justApplied;


        updateDescription();
    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();

        PCLEffects.Queue.add(VFX.bleed(owner.hb));
        PCLSFX.play(PCLSFX.PCL_SPRAY, 1f, 1.15f, 0.95f);
    }

    @Override
    public void playApplyPowerSfx() {
        PCLSFX.play(PCLSFX.HEART_BEAT, 1f, 1.15f, 0.95f);
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
    public void onPlayCard(AbstractCard card, AbstractMonster m) {
        super.onPlayCard(card, m);
        if (card.block > 0) {
            applyDebuff(card.block * amount);
            this.flash();
        }
    }

    private void applyDebuff(int amount) {
        if (amount > 0) {
            for (AbstractCreature cr : GameUtilities.getAllCharacters(true)) {
                PCLActions.bottom.dealDamageAtEndOfTurn(owner, cr, amount, PCLEnum.AttackEffect.CLAW);
            }
        }
    }
}
