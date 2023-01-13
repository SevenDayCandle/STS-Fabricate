package pinacolada.skills;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.interfaces.delegates.FuncT0;
import extendedui.utilities.EUIColors;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.powers.PCLClickableUse;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PActionCond;
import pinacolada.skills.skills.base.primary.PLimit_Limited;
import pinacolada.skills.skills.base.primary.PLimit_SemiLimited;
import pinacolada.utilities.GameUtilities;

// TODO refactor to remove unnecessary elements
public abstract class PLimit extends PPrimary<PField_Empty>
{
    protected boolean limitCache = false;

    public PLimit(PSkillData<PField_Empty> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PLimit(PSkillData<PField_Empty> data)
    {
        super(data, PCLCardTarget.None, 0);
    }

    public static PLimit_Limited limited()
    {
        return new PLimit_Limited();
    }

    public static PLimit_SemiLimited semiLimited()
    {
        return new PLimit_SemiLimited();
    }

    public PLimit setChild(PSkill effect)
    {
        super.setChild(effect);
        return this;
    }

    public PLimit setChild(PSkill... effects)
    {
        super.setChild(effects);
        return this;
    }

    @Override
    public void refresh(AbstractCreature m, AbstractCard c, boolean conditionMet)
    {
        limitCache = checkLimit(makeInfo(m), false, false);
        super.refresh(m, c, limitCache & conditionMet);
    }

    @Override
    public Color getConditionColor()
    {
        return GameUtilities.inBattle() && !limitCache ? EUIColors.gold(0.6f) : Settings.GOLD_COLOR;
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return getConditionRawString() + (childEffect != null ? ((childEffect instanceof PCond ? EFFECT_SEPARATOR : ": ") + childEffect.getText(addPeriod)) : PCLCoreStrings.period(addPeriod));
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (checkLimit(info, true, false) && childEffect != null)
        {
            childEffect.use(info);
        }
    }

    public void use(PCLUseInfo info, int index)
    {
        if (checkLimit(info, true, false) && childEffect != null)
        {
            childEffect.use(info, index);
        }
    }

    public void use(PCLUseInfo info, boolean isUsing)
    {
        if (checkLimit(info, isUsing, false) && childEffect != null)
        {
            childEffect.use(info);
        }
    }

    // The try activation should not be triggered when the condition is not actually being used from a card effect or a power (i.e. when both isUsing and fromTrigger are false)
    public boolean checkLimit(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return canActivate(info) && checkChild(info, isUsing, fromTrigger) && testTry(info, isUsing);
    }

    public final boolean checkChild(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return !(this.childEffect instanceof PCond) || this.childEffect instanceof PActionCond || ((PCond) this.childEffect).checkCondition(info, isUsing, fromTrigger);
    }

    public boolean testTry(PCLUseInfo info, boolean isUsing)
    {
        if (!isUsing)
        {
            return true;
        }
        return tryActivate(info);
    }

    @Override
    public int triggerOnAttack(DamageInfo info, int damageAmount, AbstractCreature target)
    {
        return this.childEffect != null && checkLimit(makeInfo(target), false, true) ? this.childEffect.triggerOnAttack(info, damageAmount, target) : damageAmount;
    }

    @Override
    public int triggerOnAttacked(DamageInfo info, int damageAmount)
    {
        return this.childEffect != null && checkLimit(makeInfo(null), false, true) ? this.childEffect.triggerOnAttacked(info, damageAmount) : damageAmount;
    }

    @Override
    public boolean triggerOnReshuffle(AbstractCard c, CardGroup sourcePile)
    {
        return triggerOn(() -> this.childEffect.triggerOnReshuffle(c, sourcePile));
    }

    @Override
    public boolean triggerOnApplyPower(AbstractCreature source, AbstractCreature target, AbstractPower c)
    {
        return triggerOn(() -> this.childEffect.triggerOnApplyPower(source, target, c), makeInfo(target).setData(c));
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
    public boolean triggerOnOrbFocus(AbstractOrb c)
    {
        return triggerOn(() -> this.childEffect.triggerOnOrbFocus(c), makeInfo(null).setData(c));
    }

    @Override
    public boolean triggerOnOrbEvoke(AbstractOrb c)
    {
        return triggerOn(() -> this.childEffect.triggerOnOrbEvoke(c), makeInfo(null).setData(c));
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

    protected boolean triggerOn(FuncT0<Boolean> childAction)
    {
        return triggerOn(childAction, makeInfo(null));
    }

    // Only trigger the condition use effects if the child actually triggers for the specified action
    protected boolean triggerOn(FuncT0<Boolean> childAction, PCLUseInfo info)
    {
        return this.childEffect != null && sourceCard != null
                && checkLimit(info, false, true) && childAction.invoke()
                && checkLimit(info, true, false);
    }

    abstract public boolean canActivate(PCLUseInfo info);
    abstract public boolean tryActivate(PCLUseInfo info);
}
