package pinacolada.skills.skills.special.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.base.moves.PMove_Gain;

public class PMove_GainMaxHP extends PMove_Gain
{
    public static final PSkillData<PField_Empty> DATA = register(PMove_GainMaxHP.class, PField_Empty.class);

    public PMove_GainMaxHP()
    {
        this(1);
    }

    public PMove_GainMaxHP(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_GainMaxHP(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public String gainText()
    {
        return PGR.core.tooltips.maxHP.title;
    }

    @Override
    public PMove_GainMaxHP onAddToCard(AbstractCard card)
    {
        super.onAddToCard(card);
        if (!card.tags.contains(AbstractCard.CardTags.HEALING))
        {
            card.tags.add(AbstractCard.CardTags.HEALING);
        }
        return this;
    }

    @Override
    public boolean isDetrimental()
    {
        return amount < 0;
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (amount < 0)
        {
            info.source.decreaseMaxHealth(amount);
        }
        else
        {
            info.source.increaseMaxHp(amount, true);
        }
        super.use(info);
    }
}
