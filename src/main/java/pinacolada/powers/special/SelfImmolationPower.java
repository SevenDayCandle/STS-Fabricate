package pinacolada.powers.special;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.effects.AttackEffects;
import pinacolada.effects.SFX;
import pinacolada.effects.VFX;
import pinacolada.powers.PCLPower;
import pinacolada.utilities.GameActions;
import pinacolada.utilities.GameEffects;
import pinacolada.utilities.GameUtilities;

public class SelfImmolationPower extends PCLPower
{
    public static final String POWER_ID = createFullID(SelfImmolationPower.class);
    public boolean justApplied;

    public SelfImmolationPower(AbstractCreature owner, int amount)
    {
        this(owner, amount, false);
    }

    public SelfImmolationPower(AbstractCreature owner, int amount, boolean justApplied)
    {
        super(owner, POWER_ID);

        this.amount = amount;
        if (this.amount >= 9999)
        {
            this.amount = 9999;
        }
        initialize(amount, PowerType.DEBUFF, true);
        this.justApplied = justApplied;


        updateDescription();
    }

    private void applyDebuff(int amount)
    {
        if (amount > 0)
        {
            for (AbstractCreature cr : GameUtilities.getAllCharacters(true))
            {
                GameActions.bottom.dealDamageAtEndOfTurn(owner, cr, amount, AttackEffects.CLAW);
            }
        }
    }

    @Override
    public void onInitialApplication()
    {
        super.onInitialApplication();

        GameEffects.Queue.add(VFX.bleed(owner.hb));
        SFX.play(SFX.PCL_SPRAY, 1f, 1.15f, 0.95f);
    }

    @Override
    public void playApplyPowerSfx()
    {
        SFX.play(SFX.HEART_BEAT, 1f, 1.15f, 0.95f);
    }

    @Override
    public void atStartOfTurnPostDraw()
    {
        super.atStartOfTurnPostDraw();
        if (justApplied)
        {
            justApplied = false;
        }
        else
        {
            reducePower(1);
        }

    }

    @Override
    public void onPlayCard(AbstractCard card, AbstractMonster m)
    {
        super.onPlayCard(card, m);
        if (card.block > 0)
        {
            applyDebuff(card.block * amount);
            this.flash();
        }
    }
}
