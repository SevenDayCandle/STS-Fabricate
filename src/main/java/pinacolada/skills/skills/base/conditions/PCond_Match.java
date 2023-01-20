package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrigger;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PCond_Match extends PCond<PField_CardCategory>
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
            name =  TEXT.subjects.withX(name, fields.getFullCardString());
        }

        if (hasParentType(PTrigger.class))
        {
            return TEXT.conditions.wheneverYou(name);
        }
        return fields.forced ? TEXT.conditions.not(name) : name;
    }

    @Override
    public boolean triggerOnMatch(AbstractCard c, PCLUseInfo info)
    {
        if (fields.getFullCardFilter().invoke(c))
        {
            if (this.childEffect != null)
            {
                this.childEffect.use(makeInfo(null));
            }
            return true;
        }
        return false;
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
