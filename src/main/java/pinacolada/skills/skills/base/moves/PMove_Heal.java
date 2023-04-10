package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PMove_Heal extends PMove<PField_Empty>
{
    public static final PSkillData<PField_Empty> DATA = register(PMove_Heal.class, PField_Empty.class);

    public PMove_Heal()
    {
        this(1);
    }

    public PMove_Heal(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_Heal(int amount)
    {
        super(DATA, PCLCardTarget.Self, amount);
    }

    public PMove_Heal(PCLCardTarget target, int amount)
    {
        super(DATA, target, amount);
    }

    @Override
    public PMove_Heal onAddToCard(AbstractCard card)
    {
        super.onAddToCard(card);
        if (target.targetsSelf() && !card.tags.contains(AbstractCard.CardTags.HEALING))
        {
            card.tags.add(AbstractCard.CardTags.HEALING);
        }
        return this;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill)
    {
        return TEXT.act_heal(TEXT.subjects_x);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        for (AbstractCreature t : getTargetList(info))
        {
            getActions().heal(info.source, t, amount);
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        if (isSelfOnlyTarget())
        {
            return TEXT.act_heal(getAmountRawString());
        }
        return TEXT.act_healOn(getAmountRawString(), getTargetString());
    }
}
