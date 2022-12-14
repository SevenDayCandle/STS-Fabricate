package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.*;

public class PCond_Match extends PCond
{

    public static final PSkillData DATA = register(PCond_Match.class, PCLEffectType.CardGroupFull, 1, 1)
            .pclOnly()
            .selfTarget();

    public PCond_Match()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_Match(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_Match(PSkill effect)
    {
        this();
        setChild(effect);
    }

    public PCond_Match(PSkill... effect)
    {
        this();
        setChild(effect);
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
            name =  TEXT.subjects.withX(name, getFullCardString());
        }

        if (hasParentType(PTrigger.class))
        {
            return TEXT.conditions.wheneverYou(name);
        }
        return alt ? TEXT.conditions.not(name) : name;
    }

    @Override
    public boolean triggerOnMatch(AbstractCard c, PCLUseInfo info)
    {
        if (getFullCardFilter().invoke(c))
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
        return alt ^ info.isMatch;
    }

    @Override
    public boolean canMatch(AbstractCard other)
    {
        return extendsMatch() && getFullCardFilter().invoke(other);
    }

    protected boolean extendsMatch()
    {
        return (!affinities.isEmpty() || !rarities.isEmpty() || !tags.isEmpty() || !types.isEmpty());
    }
}
