package pinacolada.skills.skills.special.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.subscribers.OnMatchSubscriber;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PPassiveCond;

public class PCond_Match extends PPassiveCond<PField_CardCategory> implements OnMatchSubscriber
{

    public static final PSkillData<PField_CardCategory> DATA = register(PCond_Match.class, PField_CardCategory.class, 1, 1)
            .pclOnly()
            .selfTarget();

    public PCond_Match()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_Match(PSkillSaveData content)
    {
        super(DATA, content);
    }

    @Override
    public String getSampleText()
    {
        return PGR.core.tooltips.match.title;
    }

    @Override
    public String getSubText()
    {
        String name = PGR.core.tooltips.match.title;
        if (extendsMatch())
        {
            name =  TEXT.subjects_withX(name, fields.getFullCardString());
        }

        if (isWhenClause())
        {
            return TEXT.cond_wheneverYou(name);
        }
        return fields.forced ? TEXT.cond_not(name) : name;
    }

    @Override
    public void onMatch(AbstractCard card, PCLUseInfo info)
    {
        if (fields.getFullCardFilter().invoke(card))
        {
            useFromTrigger(info);
        }
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return fields.forced ^ info.isMatch;
    }

    @Override
    public boolean canMatch(AbstractCard other)
    {
        return extendsMatch() && fields.getFullCardFilter().invoke(other);
    }

    protected boolean extendsMatch()
    {
        return (!fields.affinities.isEmpty() || !fields.rarities.isEmpty() || !fields.tags.isEmpty() || !fields.types.isEmpty());
    }
}
