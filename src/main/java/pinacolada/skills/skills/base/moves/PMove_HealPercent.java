package pinacolada.skills.skills.base.moves;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PMove_HealPercent extends PMove<PField_Empty>
{
    public static final PSkillData<PField_Empty> DATA = register(PMove_HealPercent.class, PField_Empty.class);

    public PMove_HealPercent()
    {
        this(1);
    }

    public PMove_HealPercent(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_HealPercent(int amount)
    {
        super(DATA, PCLCardTarget.Self, amount);
    }

    public PMove_HealPercent(PCLCardTarget target, int amount)
    {
        super(DATA, target, amount);
    }

    @Override
    public PMove_HealPercent onAddToCard(AbstractCard card)
    {
        super.onAddToCard(card);
        if (target.targetsSelf() && !card.tags.contains(AbstractCard.CardTags.HEALING))
        {
            card.tags.add(AbstractCard.CardTags.HEALING);
        }
        return this;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.act_heal(TEXT.subjects_x + "%");
    }

    @Override
    public void use(PCLUseInfo info)
    {
        for (AbstractCreature t : getTargetList(info))
        {
            int heal = MathUtils.ceil(info.source.maxHealth * amount / 100f);
            getActions().heal(info.source, t, heal);
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        String percentLoss = getAmountRawString() + "%";
        if (isSelfOnlyTarget())
        {
            return TEXT.act_heal(percentLoss);
        }
        return TEXT.act_healOn(percentLoss, getTargetString());
    }
}
