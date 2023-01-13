package pinacolada.skills;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT0;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.powers.PCLClickableUse;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.base.primary.PTrigger_Interactable;
import pinacolada.skills.skills.base.primary.PTrigger_Passive;
import pinacolada.skills.skills.base.primary.PTrigger_When;

public abstract class PTrigger extends PPrimary<PField_Not>
{
    public static final int TRIGGER_PRIORITY = 0;
    protected int usesThisTurn;

    public PTrigger(PSkillData<PField_Not> data)
    {
        this(data, PCLCardTarget.None, -1);
    }

    public PTrigger(PSkillData<PField_Not> data, PSkillSaveData content)
    {
        super(data, content);
        this.usesThisTurn = this.amount;
    }

    public PTrigger(PSkillData<PField_Not> data, PCLCardTarget target, int maxUses)
    {
        super(data, target, maxUses);
        this.usesThisTurn = this.amount;
    }

    public static PTrigger interactAny(PSkill... effects)
    {
        return chain(new PTrigger_Interactable(), effects).setAmount(-1);
    }

    public static PTrigger interactable(PSkill... effects)
    {
        return chain(new PTrigger_Interactable(), effects);
    }

    public static PTrigger interactable(int amount, PSkill... effects)
    {
        return chain(new PTrigger_Interactable(amount), effects);
    }

    public static PTrigger interactablePerCombat(int amount, PSkill... effects)
    {
        return chain((PTrigger) new PTrigger_Interactable(amount).edit(f -> f.setNot(true)), effects);
    }

    public static PTrigger passive(PSkill... effects)
    {
        return chain(new PTrigger_Passive(), effects);
    }

    public static PTrigger when(PSkill... effects)
    {
        return chain(new PTrigger_When(), effects);
    }

    public static PTrigger when(int perTurn, PSkill... effects)
    {
        return chain(new PTrigger_When().setAmount(perTurn), effects);
    }

    public float atBlockGain(AbstractCreature owner, float block, AbstractCard card)
    {
        return modifyBlock(EUIUtils.safeCast(card, PCLCard.class), EUIUtils.safeCast(owner, AbstractMonster.class), block);
    }

    public float atDamageGive(AbstractCreature owner, float damage, DamageInfo.DamageType type, AbstractCard card)
    {
        return modifyDamage(EUIUtils.safeCast(card, PCLCard.class), EUIUtils.safeCast(owner, AbstractMonster.class), damage);
    }

    public float atDamageReceive(AbstractCreature owner, float damage, DamageInfo.DamageType type, AbstractCard card)
    {
        return modifyDamage(EUIUtils.safeCast(card, PCLCard.class), EUIUtils.safeCast(owner, AbstractMonster.class), damage);
    }

    public PTrigger chain(PSkill... effects)
    {
        return PSkill.chain(this, effects);
    }

    @Override
    public String getSubText()
    {
        return fields.not ? TEXT.conditions.timesPerCombat(amount) : amount > 0 ? TEXT.conditions.timesPerTurn(amount) + ", " : "";
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return getSubText() + (childEffect != null ? childEffect.getText(addPeriod) : "");
    }

    @Override
    public PTrigger makeCopy()
    {
        PTrigger copy = (PTrigger) super.makeCopy();
        copy.usesThisTurn = this.usesThisTurn;
        return copy;
    }

    public PTrigger setAmount(int amount)
    {
        super.setAmount(amount);
        this.usesThisTurn = this.amount;
        return this;
    }

    public PTrigger setAmount(int amount, int upgrade)
    {
        super.setAmount(amount, upgrade);
        this.usesThisTurn = this.amount;
        return this;
    }

    @Override
    public PTrigger setAmountFromCard()
    {
        super.setAmountFromCard();
        this.usesThisTurn = this.amount;
        return this;
    }

    public PTrigger setChild(PSkill effect)
    {
        super.setChild(effect);
        return this;
    }

    public PTrigger setChild(PSkill... effects)
    {
        super.setChild(effects);
        return this;
    }

    public PTrigger setExtra(int amount)
    {
        super.setExtra(amount);
        this.usesThisTurn = this.amount;
        return this;
    }

    public PTrigger setExtra(int amount, int upgrade)
    {
        super.setExtra(amount, upgrade);
        this.usesThisTurn = this.amount;
        return this;
    }

    public PTrigger setTemporaryAmount(int amount)
    {
        super.setTemporaryAmount(amount);
        this.usesThisTurn = this.amount;
        return this;
    }

    @Override
    public boolean triggerOnAllyDeath(PCLCard c, PCLCardAlly ally)
    {
        return triggerOn(() -> this.childEffect.triggerOnAllyDeath(c, ally));
    }

    @Override
    public boolean triggerOnAllySummon(PCLCard c, PCLCardAlly ally)
    {
        return triggerOn(() -> this.childEffect.triggerOnAllySummon(c, ally));
    }

    @Override
    public boolean triggerOnAllyTrigger(PCLCard c, PCLCardAlly ally)
    {
        return triggerOn(() -> this.childEffect.triggerOnAllyTrigger(c, ally));
    }

    @Override
    public boolean triggerOnAllyWithdraw(PCLCard c, PCLCardAlly ally)
    {
        return triggerOn(() -> this.childEffect.triggerOnAllyWithdraw(c, ally));
    }

    @Override
    public boolean triggerOnApplyPower(AbstractCreature source, AbstractCreature target, AbstractPower c)
    {
        return triggerOn(() -> this.childEffect.triggerOnApplyPower(source, target, c), makeInfo(target).setData(c));
    }

    @Override
    public boolean triggerOnCreate(AbstractCard c, boolean startOfBattle)
    {
        return triggerOn(() -> this.childEffect.triggerOnCreate(c, startOfBattle));
    }

    @Override
    public boolean triggerOnDiscard(AbstractCard c)
    {
        return triggerOn(() -> this.childEffect.triggerOnDiscard(c));
    }

    @Override
    public boolean triggerOnDraw(AbstractCard c)
    {
        return triggerOn(() -> this.childEffect.triggerOnDraw(c));
    }

    @Override
    public boolean triggerOnEndOfTurn(boolean isUsing)
    {
        return triggerOn(() -> this.childEffect.triggerOnEndOfTurn(isUsing));
    }

    @Override
    public boolean triggerOnExhaust(AbstractCard c)
    {
        return triggerOn(() -> this.childEffect.triggerOnExhaust(c));
    }

    @Override
    public boolean triggerOnIntensify(PCLAffinity c)
    {
        return triggerOn(() -> this.childEffect.triggerOnIntensify(c), makeInfo(null).setData(c));
    }

    @Override
    public boolean triggerOnMatch(AbstractCard c, PCLUseInfo info)
    {
        return triggerOn(() -> this.childEffect.triggerOnMatch(c, info), info);
    }

    @Override
    public boolean triggerOnMismatch(AbstractCard c, PCLUseInfo info)
    {
        return triggerOn(() -> this.childEffect.triggerOnMismatch(c, info), info);
    }

    @Override
    public boolean triggerOnOrbChannel(AbstractOrb c)
    {
        return triggerOn(() -> this.childEffect.triggerOnOrbChannel(c), makeInfo(null).setData(c));
    }

    @Override
    public boolean triggerOnOrbEvoke(AbstractOrb c)
    {
        return triggerOn(() -> this.childEffect.triggerOnOrbEvoke(c), makeInfo(null).setData(c));
    }

    @Override
    public boolean triggerOnOrbFocus(AbstractOrb c)
    {
        return triggerOn(() -> this.childEffect.triggerOnOrbFocus(c), makeInfo(null).setData(c));
    }

    @Override
    public boolean triggerOnOrbTrigger(AbstractOrb c)
    {
        return triggerOn(() -> this.childEffect.triggerOnOrbTrigger(c), makeInfo(null).setData(c));
    }

    @Override
    public boolean triggerOnOtherCardPlayed(AbstractCard c)
    {
        return triggerOn(() -> this.childEffect.triggerOnOtherCardPlayed(c));
    }

    @Override
    public boolean triggerOnPCLPowerUsed(PCLClickableUse c)
    {
        return triggerOn(() -> this.childEffect.triggerOnPCLPowerUsed(c));
    }

    @Override
    public boolean triggerOnPurge(AbstractCard c)
    {
        return triggerOn(() -> this.childEffect.triggerOnPurge(c));
    }

    @Override
    public boolean triggerOnReshuffle(AbstractCard c, CardGroup sourcePile)
    {
        return triggerOn(() -> this.childEffect.triggerOnReshuffle(c, sourcePile));
    }

    @Override
    public boolean triggerOnScry()
    {
        return triggerOn(() -> this.childEffect.triggerOnScry());
    }

    @Override
    public boolean triggerOnShuffle(boolean r)
    {
        return triggerOn(() -> this.childEffect.triggerOnShuffle(r));
    }

    @Override
    public boolean triggerOnStartOfTurn()
    {
        return triggerOn(() -> this.childEffect.triggerOnStartOfTurn());
    }

    @Override
    public boolean triggerOnStartup()
    {
        return triggerOn(() -> this.childEffect.triggerOnStartup());
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (usesThisTurn != 0)
        {
            if (usesThisTurn > 0)
            {
                usesThisTurn -= 1;
            }
            this.childEffect.use(info);
        }
    }

    @Override
    public void use(PCLUseInfo info, int index)
    {
        if (usesThisTurn != 0)
        {
            if (usesThisTurn > 0)
            {
                usesThisTurn -= 1;
            }
            this.childEffect.use(info, index);
        }
    }

    @Override
    public void use(PCLUseInfo info, boolean isUsing)
    {
        if (usesThisTurn != 0)
        {
            if (usesThisTurn > 0)
            {
                usesThisTurn -= 1;
            }
            this.childEffect.use(info, isUsing);
        }
    }

    public void resetUses()
    {
        if (!fields.not)
        {
            this.usesThisTurn = this.amount;
        }
    }

    public PTrigger setUpgrade(int upgrade)
    {
        super.setUpgrade(upgrade);
        this.usesThisTurn = this.amount;
        return this;
    }

    public PTrigger setUpgradeExtra(int upgrade)
    {
        super.setUpgradeExtra(upgrade);
        this.usesThisTurn = this.amount;
        return this;
    }

    public PTrigger stack(PSkill other)
    {
        if (rootAmount > 0 && other.rootAmount > 0)
        {
            setAmount(rootAmount + other.rootAmount);
        }
        if (rootExtra > 0 && other.rootExtra > 0)
        {
            setExtra(rootExtra + other.rootExtra);
        }
        setAmountFromCard();
        resetUses();

        // Only update child effects if uses per turn is infinite
        if (rootAmount <= 0 && this.childEffect != null && other.childEffect != null)
        {
            this.childEffect.stack(other.childEffect);
        }
        return this;
    }

    public int getUses() {
        return this.usesThisTurn;
    }

    protected boolean triggerOn(FuncT0<Boolean> childAction)
    {
        return triggerOn(childAction, makeInfo(null));
    }

    protected boolean triggerOn(FuncT0<Boolean> childAction, PCLUseInfo info)
    {
        if (this.childEffect != null && sourceCard != null && usesThisTurn != 0)
        {
            if (usesThisTurn > 0)
            {
                usesThisTurn -= 1;
            }
            return childAction.invoke();
        }
        return false;
    }
}
