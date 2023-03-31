package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

import java.util.List;

@VisibleSkill
public class PMod_PerGold extends PMod_Per<PField_Empty>
{
    public static final PSkillData<PField_Empty> DATA = register(PMod_PerGold.class, PField_Empty.class).selfTarget();

    public PMod_PerGold(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_PerGold()
    {
        super(DATA);
    }

    public PMod_PerGold(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public int getMultiplier(PCLUseInfo info)
    {
        List<AbstractCreature> targetList = getTargetList(info);
        return EUIUtils.sumInt(targetList, t -> t.gold);
    }

    @Override
    public String getSubText()
    {
        return PGR.core.tooltips.gold.getTitleOrIcon();
    }
}
