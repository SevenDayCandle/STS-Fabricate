package pinacolada.powers.replacement;

import basemod.interfaces.CloneablePowerInterface;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.actions.PCLActions;
import pinacolada.interfaces.markers.MultiplicativePower;

public class PlayerFlightPower extends AbstractPower implements CloneablePowerInterface, MultiplicativePower
{
    public static final String POWER_ID = "PlayerFlight";
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private static final PowerStrings powerStrings;

    static
    {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("Flight");
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }

    private final int storedAmount;

    public PlayerFlightPower(AbstractCreature owner, int amount)
    {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.storedAmount = amount;
        this.updateDescription();
        this.loadRegion("flight");
        this.priority = 50;
    }

    private float calculateDamageTakenAmount(float damage, DamageInfo.DamageType type)
    {
        return type != DamageInfo.DamageType.HP_LOSS && type != DamageInfo.DamageType.THORNS ? damage / 2f : damage;
    }

    @Override
    public AbstractPower makeCopy()
    {
        return new PlayerFlightPower(owner, amount);
    }

    public void playApplyPowerSfx()
    {
        CardCrawlGame.sound.play("POWER_FLIGHT", 0.05f);
    }

    public void updateDescription()
    {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    public float atDamageFinalReceive(float damage, DamageInfo.DamageType type)
    {
        return this.calculateDamageTakenAmount(damage, type);
    }

    public void atStartOfTurn()
    {
        this.amount = this.storedAmount;
        this.updateDescription();
    }

    public int onAttacked(DamageInfo info, int damageAmount)
    {
        boolean willLive = this.calculateDamageTakenAmount((float) damageAmount, info.type) < (float) this.owner.currentHealth;
        if (info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS && damageAmount > 0 && willLive)
        {
            this.flash();
            PCLActions.bottom.reducePower(this, 1);
        }

        return damageAmount;
    }

    public void onRemove()
    {
        //AbstractDungeon.actionManager.addToBottom(new ChangeStateAction((AbstractMonster) this.owner, "GROUNDED"));
    }
}