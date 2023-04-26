package pinacolada.powers;

public class PCLTriggerUsePool {
    public int baseUses = 1;
    public int uses = 1;
    public boolean refreshEachTurn = true;
    public boolean stackAutomatically;

    public PCLTriggerUsePool() {

    }

    public PCLTriggerUsePool(int uses, boolean refreshEachTurn, boolean stackAutomatically) {
        this.baseUses = this.uses = uses;
        this.refreshEachTurn = refreshEachTurn;
        this.stackAutomatically = stackAutomatically;
    }

    public PCLTriggerUsePool addUses(int uses) {
        this.baseUses += uses;
        this.uses += uses;
        return this;
    }

    public boolean canUse() {
        return canUse(1);
    }

    public boolean canUse(int amount) {
        return hasInfiniteUses() || uses >= amount;
    }

    public boolean hasInfiniteUses() {
        return baseUses == -1;
    }

    public void refresh() {
        if (refreshEachTurn) {
            this.uses = this.baseUses;
        }
    }

    public PCLTriggerUsePool setUses(int uses, boolean refreshEachTurn, boolean stackAutomatically) {
        return setUses(uses, uses, refreshEachTurn, stackAutomatically);
    }

    public PCLTriggerUsePool setUses(int uses, int baseUses, boolean refreshEachTurn, boolean stackAutomatically) {
        this.baseUses = this.uses = uses;
        this.refreshEachTurn = refreshEachTurn;
        this.stackAutomatically = stackAutomatically;
        return this;
    }

    public void use() {
        use(1);
    }

    public void use(int amount) {
        if (!hasInfiniteUses()) {
            uses = Math.max(0, uses - amount);
        }
    }
}
