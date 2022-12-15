package pinacolada.skills.skills.base.moves;

import extendedui.EUIUtils;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PMove_AddLevel extends PMove
{
    public static final PSkillData DATA = register(PMove_AddLevel.class, PCLEffectType.Affinity)
            .setColors(PCLEnum.Cards.THE_CONJURER, PCLEnum.Cards.THE_DECIDER)
            .selfTarget();

    public PMove_AddLevel(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_AddLevel(int amount, PCLAffinity... stance)
    {
        super(DATA, PCLCardTarget.Self, amount, stance);
    }


    @Override
    public String getSampleText()
    {
        return TEXT.actions.gain(PGR.core.tooltips.level);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (affinities.isEmpty())
        {
            getActions().tryChooseAffinitySkill(getName(), amount, info.source, info.target, EUIUtils.map(PCLAffinity.getAvailableAffinities(), a -> PMove.addLevel(amount, a)));
        }
        else if (affinities.size() == 1)
        {
            getActions().addAffinityLevel(affinities.get(0), amount);
        }
        else
        {
            getActions().tryChooseAffinitySkill(getName(), amount, info.source, info.target, EUIUtils.map(affinities, a -> PMove.addLevel(amount, a)));
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        String afs = affinities.isEmpty() ? TEXT.subjects.anyX(PGR.core.tooltips.affinityGeneral) : getAffinityLevelOrString();
        String base = TEXT.actions.giveTargetAmount(afs, (amount > 0 ? ("+ " + getAmountRawString()) : getAmountRawString()), plural(PGR.core.tooltips.level));
        return alt ? TEXT.subjects.randomly(base) : base;
    }
}
