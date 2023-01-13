package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrigger;
import pinacolada.skills.fields.PField_Orb;

public class PCond_IfIsOrb extends PCond<PField_Orb>
{
    public static final PSkillData<PField_Orb> DATA = register(PCond_IfIsOrb.class, PField_Orb.class)
            .selfTarget();

    public PCond_IfIsOrb(PSkillSaveData content)
    {
        super(content);
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
        return TEXT.conditions.ifX(TEXT.cardEditor.orbs);
    }

    @Override
    public String getSubText()
    {
        return hasParentType(PTrigger.class) ? fields.getOrbAndString(0) :
                TEXT.conditions.ifX(fields.getOrbAndString(1));
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
            return orb != null && EUIUtils.any(fields.orbs, o -> o.ID.equals(orb.ID));
        }
        return false;
    }
}
