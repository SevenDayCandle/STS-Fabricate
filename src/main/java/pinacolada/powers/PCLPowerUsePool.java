package pinacolada.powers;

public class PCLPowerUsePool
{
    public int baseUses = 1;
    public int uses = 1;
    public boolean refreshEachTurn = true;
    public boolean stackAutomatically;

    public PCLPowerUsePool()
    {

    }

    public PCLPowerUsePool(int uses, boolean refreshEachTurn, boolean stackAutomatically)
    {
        this.baseUses = this.uses = uses;
        this.refreshEachTurn = refreshEachTurn;
        this.stackAutomatically = stackAutomatically;
    }

    public PCLPowerUsePool addUses(int uses)
    {
        this.baseUses += uses;
        this.uses += uses;
        return this;
    }

    public boolean canUse()
    {
        return hasInfiniteUses() || uses > 0;
    }

    public boolean hasInfiniteUses()
    {
        return baseUses == -1;
    }

    public void refresh()
    {
        if (refreshEachTurn)
        {
            this.uses = this.baseUses;
        }
    }

    public PCLPowerUsePool setUses(int uses, boolean refreshEachTurn, boolean stackAutomatically)
    {
        this.baseUses = this.uses = uses;
        this.refreshEachTurn = refreshEachTurn;
        this.stackAutomatically = stackAutomatically;
        return this;
    }

    public void use()
    {
        if (!hasInfiniteUses())
        {
            uses -= 1;
        }
    }
}
