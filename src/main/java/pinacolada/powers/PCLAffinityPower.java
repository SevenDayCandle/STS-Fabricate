package pinacolada.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardData;
import pinacolada.relics.PCLRelic;

public abstract class PCLAffinityPower extends PCLPower
{
    public final PCLAffinity affinity;
    public boolean isActive;
    protected int temporaryAmount;

    protected PCLAffinityPower(PCLAffinity affinity, AbstractCreature owner, AbstractCreature source)
    {
        super(owner, source);
        this.affinity = affinity;
    }

    public PCLAffinityPower(PCLAffinity affinity, AbstractCreature owner, PCLRelic relic)
    {
        super(owner, relic);
        this.affinity = affinity;
    }

    public PCLAffinityPower(PCLAffinity affinity, AbstractCreature owner, AbstractCreature source, PCLRelic relic)
    {
        super(owner, source, relic);
        this.affinity = affinity;
    }

    public PCLAffinityPower(PCLAffinity affinity, AbstractCreature owner, PCLCardData cardData)
    {
        super(owner, cardData);
        this.affinity = affinity;
    }

    public PCLAffinityPower(PCLAffinity affinity, AbstractCreature owner, AbstractCreature source, PCLCardData cardData)
    {
        super(owner, source, cardData);
        this.affinity = affinity;
    }

    public PCLAffinityPower(PCLAffinity affinity, AbstractCreature owner, String id)
    {
        super(owner, id);
        this.affinity = affinity;
    }

    public PCLAffinityPower(PCLAffinity affinity, AbstractCreature owner, AbstractCreature source, String id)
    {
        super(owner, source, id);
        this.affinity = affinity;
    }

    public boolean canSpend(int amount)
    {
        return isEnabled() && this.amount >= amount;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public int getEffectiveScaling()
    {
        return 1;
    }

    public void stack(int amount) {
        stack(amount, false, false);
    }

    public void stack(int amount, boolean pay) {
        stack(amount, pay, false);
    }

    public void stack(int amount, boolean pay, boolean temporary)
    {
        if (temporary) {
            this.temporaryAmount += amount;
        } else if (pay && amount < 0) {
            this.temporaryAmount = Math.max(0, this.temporaryAmount + amount);
        }
        super.stackPower(amount, false);
    }

    public boolean trySpend(int amount) {
        if (canSpend(amount)) {
            stack(-amount, true, false);

            return true;
        }
        return false;
    }

    public void unstackTemporary() {
        if (temporaryAmount != 0) {
            stack(-temporaryAmount, false);
            this.temporaryAmount = 0;
        }
    }
}
