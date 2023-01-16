package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.utilities.ColoredString;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.CooldownProvider;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

public class PCond_Cooldown extends PCond<PField_Empty> implements CooldownProvider
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
    public String getSampleText()
    {
        return EUIRM.strings.generic2(PGR.core.tooltips.cooldown.title, TEXT.subjects.x);
    }

    @Override
    public String getSubText()
    {
        return EUIRM.strings.generic2(PGR.core.tooltips.cooldown.title, getAmountRawString());
    }

    @Override
    public void use(PCLUseInfo info)
    {
        progressCooldownAndTrigger(sourceCard, info.target, 1);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return getCooldown() <= 0;
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

    @Override
    public void activate(AbstractCard card, AbstractCreature m)
    {
        if (this.childEffect != null)
        {
            this.childEffect.use(makeInfo(m));
        }
    }
}
