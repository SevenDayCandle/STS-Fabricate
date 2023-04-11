package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.utilities.ColoredString;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.providers.CooldownProvider;
import pinacolada.interfaces.subscribers.OnCooldownTriggeredSubscriber;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.skills.skills.base.primary.PTrigger_When;

@VisibleSkill
public class PCond_Cooldown extends PPassiveCond<PField_Empty> implements CooldownProvider, OnCooldownTriggeredSubscriber
{
    public static final PSkillData<PField_Empty> DATA = register(PCond_Cooldown.class, PField_Empty.class)
            .selfTarget();

    public PCond_Cooldown(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_Cooldown()
    {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_Cooldown(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public PCond_Cooldown onAddToCard(AbstractCard card)
    {
        super.onAddToCard(card);
        return this;
    }

    @Override
    public boolean onCooldownTriggered(AbstractCard card, AbstractCreature m, CooldownProvider cooldown)
    {
        if (cooldown.canActivate())
        {
            useFromTrigger(makeInfo(m));
        }
        return true;
    }

    @Override
    public PCond_Cooldown onRemoveFromCard(AbstractCard card)
    {
        super.onRemoveFromCard(card);
        return this;
    }

    @Override
    public final ColoredString getColoredValueString()
    {
        return getCooldownString();
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill)
    {
        return callingSkill instanceof PTrigger_When ? TEXT.cond_whenSingle(TEXT.act_trigger(PGR.core.tooltips.cooldown.title)) : EUIRM.strings.generic2(PGR.core.tooltips.cooldown.title, TEXT.subjects_x);
    }

    @Override
    public String getSubText()
    {
        if (isWhenClause())
        {
            return getWheneverString(TEXT.act_trigger(PGR.core.tooltips.cooldown.title));
        }
        return EUIRM.strings.generic2(PGR.core.tooltips.cooldown.title, getAmountRawString());
    }

    @Override
    public void use(PCLUseInfo info)
    {
        progressCooldownAndTrigger(sourceCard, info.target, 1);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource)
    {
        return getCooldown() <= 0;
    }

    @Override
    public boolean isDisplayingUpgrade()
    {
        return displayUpgrades && getUpgrade() != 0;
    }

    @Override
    public int getCooldown()
    {
        return amount;
    }

    @Override
    public int getBaseCooldown()
    {
        return baseAmount;
    }

    @Override
    public void setCooldown(int value)
    {
        setTemporaryAmount(value);
    }

    public PCond_Cooldown setTemporaryAmount(int amount)
    {
        this.amount = amount;
        return this;
    }

    @Override
    public void activate(AbstractCard card, AbstractCreature m)
    {
        if (this.childEffect != null)
        {
            this.childEffect.use(makeInfo(m));
        }
    }
}
