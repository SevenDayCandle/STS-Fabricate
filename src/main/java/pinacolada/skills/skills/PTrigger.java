package pinacolada.skills.skills;

import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT0;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.powers.PSkillPower;
import pinacolada.skills.PPrimary;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.base.primary.PTrigger_Interactable;
import pinacolada.skills.skills.base.primary.PTrigger_When;

public abstract class PTrigger extends PPrimary<PField_Not> {
    public static final int TRIGGER_PRIORITY = 0;
    protected int usesThisTurn;
    public PSkillPower power;

    public PTrigger(PSkillData<PField_Not> data) {
        this(data, PCLCardTarget.None, -1);
    }

    public PTrigger(PSkillData<PField_Not> data, PCLCardTarget target, int maxUses) {
        super(data, target, maxUses);
        this.usesThisTurn = this.amount;
    }

    public PTrigger(PSkillData<PField_Not> data, PSkillSaveData content) {
        super(data, content);
        this.usesThisTurn = this.amount;
    }

    public static PTrigger interactAny(PSkill<?>... effects) {
        return chain(new PTrigger_Interactable(), effects).setAmount(-1);
    }

    public static PTrigger interactable(PSkill<?>... effects) {
        return chain(new PTrigger_Interactable(), effects);
    }

    public static PTrigger interactable(int amount, PSkill<?>... effects) {
        return chain(new PTrigger_Interactable(amount), effects);
    }

    public static PTrigger interactablePerCombat(int amount, PSkill<?>... effects) {
        return chain((PTrigger) new PTrigger_Interactable(amount).edit(f -> f.setNot(true)), effects);
    }

    public static PTrigger when(PSkill<?>... effects) {
        return chain(new PTrigger_When(), effects);
    }

    public static PTrigger when(int perTurn, PSkill<?>... effects) {
        return chain(new PTrigger_When().setAmount(perTurn), effects);
    }

    public PTrigger chain(PSkill<?>... effects) {
        return PSkill.chain(this, effects);
    }

    // When attached to a power, get the power this is attached to
    @Override
    public AbstractCreature getOwnerCreature() {
        return power != null ? power.owner : super.getOwnerCreature();
    }

    @Override
    public String getSubText() {
        return fields.not ? TEXT.cond_timesPerCombat(getAmountRawString()) : amount > 0 ? TEXT.cond_timesPerTurn(getAmountRawString()) + ", " : "";
    }

    @Override
    public String getText(boolean addPeriod) {
        return getSubText() + (childEffect != null ? childEffect.getText(addPeriod) : "");
    }

    @Override
    public PTrigger makeCopy() {
        PTrigger copy = (PTrigger) super.makeCopy();
        copy.usesThisTurn = this.usesThisTurn;
        return copy;
    }

    public PTrigger setAmount(int amount, int upgrade) {
        super.setAmount(amount, upgrade);
        this.usesThisTurn = this.amount;
        return this;
    }

    public PTrigger setAmount(int amount) {
        super.setAmount(amount);
        this.usesThisTurn = this.amount;
        return this;
    }

    @Override
    public PTrigger setAmountFromCard() {
        super.setAmountFromCard();
        this.usesThisTurn = this.amount;
        return this;
    }

    public PTrigger setChild(PSkill<?> effect) {
        super.setChild(effect);
        return this;
    }

    public PTrigger setChild(PSkill<?>... effects) {
        super.setChild(effects);
        return this;
    }

    public PTrigger setExtra(int amount, int upgrade) {
        super.setExtra(amount, upgrade);
        this.usesThisTurn = this.amount;
        return this;
    }

    public PTrigger setExtra(int amount) {
        super.setExtra(amount);
        this.usesThisTurn = this.amount;
        return this;
    }

    public PTrigger setTemporaryAmount(int amount) {
        super.setTemporaryAmount(amount);
        this.usesThisTurn = this.amount;
        return this;
    }

    public PTrigger stack(PSkill<?> other) {
        if (rootAmount > 0 && other.rootAmount > 0) {
            setAmount(rootAmount + other.rootAmount);
        }
        if (rootExtra > 0 && other.rootExtra > 0) {
            setExtra(rootExtra + other.rootExtra);
        }
        setAmountFromCard();
        resetUses();

        // Only update child effects if uses per turn is infinite
        if (rootAmount <= 0 && this.childEffect != null && other.getChild() != null) {
            this.childEffect.stack(other.getChild());
        }
        return this;
    }

    public boolean tryPassParent(PSkill<?> source, PCLUseInfo info) {
        if (usesThisTurn != 0) {
            if (usesThisTurn > 0) {
                usesThisTurn -= 1;
            }
            boolean result = super.tryPassParent(source, info);

            if (result && power != null) {
                power.flash();
            }

            return result;
        }
        return false;
    }

    @Override
    public void use(PCLUseInfo info) {
        if (usesThisTurn != 0) {
            if (usesThisTurn > 0) {
                usesThisTurn -= 1;
            }
            this.childEffect.use(info);
        }
    }

    @Override
    public void use(PCLUseInfo info, boolean isUsing) {
        if (usesThisTurn != 0) {
            if (usesThisTurn > 0) {
                usesThisTurn -= 1;
            }
            this.childEffect.use(info, isUsing);
        }
    }

    public int getUses() {
        return this.usesThisTurn;
    }

    public void resetUses() {
        if (!fields.not) {
            this.usesThisTurn = this.amount;
        }
    }

    public PTrigger setUpgrade(int upgrade) {
        super.setUpgrade(upgrade);
        this.usesThisTurn = this.amount;
        return this;
    }

    public PTrigger setUpgradeExtra(int upgrade) {
        super.setUpgradeExtra(upgrade);
        this.usesThisTurn = this.amount;
        return this;
    }

    protected boolean triggerOn(FuncT0<Boolean> childAction, PCLUseInfo info) {
        if (this.childEffect != null && sourceCard != null && usesThisTurn != 0) {
            if (usesThisTurn > 0) {
                usesThisTurn -= 1;
            }
            return childAction.invoke();
        }
        return false;
    }

    protected boolean triggerOn(FuncT0<Boolean> childAction) {
        return triggerOn(childAction, makeInfo(null));
    }
}
