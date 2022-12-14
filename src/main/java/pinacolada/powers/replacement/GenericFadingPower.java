package pinacolada.powers.replacement;

import basemod.interfaces.CloneablePowerInterface;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.PowerIconShowEffect;
import pinacolada.actions.special.KillCharacterAction;
import pinacolada.utilities.GameActions;
import pinacolada.utilities.GameEffects;

public class GenericFadingPower extends AbstractPower implements CloneablePowerInterface
{
    public static final String POWER_ID = "GenericFadingPower";
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private static final PowerStrings powerStrings;

    static
    {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("Fading");
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }

    private final boolean isPlayer;

    public GenericFadingPower(AbstractCreature owner, int turns)
    {
        this.isPlayer = owner instanceof AbstractPlayer;
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = turns;
        this.type = PowerType.DEBUFF;
        this.updateDescription();
        this.loadRegion("fading");
    }

    private void triggerEffect()
    {
        GameEffects.Queue.add(new PowerIconShowEffect(this));

        if (this.amount == 1 && !this.owner.isDying)
        {
            GameActions.bottom.add(new KillCharacterAction(owner, owner));
        }
        else
        {
            GameActions.bottom.reducePower(this, 1);
            this.updateDescription();
        }
    }

    @Override
    public AbstractPower makeCopy()
    {
        return new GenericFadingPower(owner, amount);
    }

    public void updateDescription()
    {
        if (this.amount == 1)
        {
            this.description = DESCRIPTIONS[2];
        }
        else
        {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
        }
    }

    @Override
    public void duringTurn()
    {
        super.duringTurn();

        if (!isPlayer)
        {
            triggerEffect();
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer)
    {
        super.atEndOfTurn(isPlayer);

        if (isPlayer)
        {
            triggerEffect();
        }
    }
}