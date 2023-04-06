package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveCond;

import java.util.List;

@VisibleSkill
public class PCond_HP extends PPassiveCond<PField_Not>
{
    public static final PSkillData<PField_Not> DATA = register(PCond_HP.class, PField_Not.class)
            .selfTarget();

    public PCond_HP(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_HP()
    {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_HP(int amount)
    {
        super(DATA, PCLCardTarget.Self, amount);
    }

    public PCond_HP(PCLCardTarget target, int amount)
    {
        super(DATA, target, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource)
    {
        List<AbstractCreature> targetList = getTargetList(info);
        return EUIUtils.any(targetList, t -> fields.not ? t.currentHealth <= amount : t.currentHealth >= amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.act_generic2(PGR.core.tooltips.hp.title, TEXT.subjects_x);
    }

    @Override
    public String getSubText()
    {
        String baseString = amount + (fields.not ? "- " : "+ ") + PGR.core.tooltips.hp.title;
        return getTargetHasString(baseString);
    }
}
