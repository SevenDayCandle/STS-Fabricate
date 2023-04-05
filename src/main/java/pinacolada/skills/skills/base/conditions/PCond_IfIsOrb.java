package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.skills.skills.PTrigger;

@VisibleSkill
public class PCond_IfIsOrb extends PPassiveCond<PField_Orb>
{
    public static final PSkillData<PField_Orb> DATA = register(PCond_IfIsOrb.class, PField_Orb.class)
            .selfTarget();

    public PCond_IfIsOrb(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_IfIsOrb()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_IfIsOrb(PCLOrbHelper... orbs)
    {
        super(DATA, PCLCardTarget.None, 0);
        fields.setOrb(orbs);
    }


    @Override
    public String getSampleText()
    {
        return TEXT.cond_ifX(TEXT.cedit_orbs);
    }

    @Override
    public String getSubText()
    {
        return hasParentType(PTrigger.class) ? fields.getOrbAndString(0) :
                TEXT.cond_ifX(fields.getOrbAndString(1));
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return hasParentType(PTrigger.class) ? TEXT.act_objectHas(getSubText(), childEffect != null ? childEffect.getText(addPeriod) : PCLCoreStrings.period(addPeriod)) : super.getText(addPeriod);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        if (info != null)
        {
            AbstractOrb orb = info.getData(AbstractOrb.class);
            return orb != null && EUIUtils.any(fields.orbs, o -> o.ID.equals(orb.ID));
        }
        return false;
    }
}
