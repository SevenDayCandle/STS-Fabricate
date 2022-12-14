package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.PSkillAttribute;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrigger;

import java.util.List;

public class PCond_IfIsOrb extends PCond implements PSkillAttribute
{
    public static final PSkillData DATA = register(PCond_IfIsOrb.class, PCLEffectType.Orb)
            .selfTarget();

    public PCond_IfIsOrb(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_IfIsOrb()
    {
        super(DATA, PCLCardTarget.None, 0, new PCLOrbHelper[]{});
    }

    public PCond_IfIsOrb(PCLOrbHelper... affinities)
    {
        super(DATA, PCLCardTarget.None, 0, affinities);
    }

    public PCond_IfIsOrb(List<PCLOrbHelper> affinities)
    {
        super(DATA, PCLCardTarget.None, 0, affinities.toArray(new PCLOrbHelper[]{}));
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.ifX(TEXT.cardEditor.orbs);
    }

    @Override
    public String getSubText()
    {
        return hasParentType(PTrigger.class) ? getOrbAndString(0) :
                TEXT.conditions.ifX(getOrbAndString(1));
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return hasParentType(PTrigger.class) ? TEXT.actions.objectHas(getSubText(), childEffect != null ? childEffect.getText(addPeriod) : PCLCoreStrings.period(addPeriod)) : super.getText(addPeriod);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        if (info != null)
        {
            AbstractOrb orb = EUIUtils.safeCast(info.getData(null), AbstractOrb.class);
            return orb != null && EUIUtils.any(orbs, o -> o.ID.equals(orb.ID));
        }
        return false;
    }
}
