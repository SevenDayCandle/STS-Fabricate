package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;

import java.util.List;

@VisibleSkill
public class PCond_HP extends PCond<PField_Not>
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
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        List<AbstractCreature> targetList = getTargetList(info);
        return EUIUtils.any(targetList, t -> fields.not ? t.currentHealth * 100 / t.maxHealth <= amount : t.currentHealth * 100 / t.maxHealth >= amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.generic2(PGR.core.tooltips.hp.title, "X%");
    }

    @Override
    public String getSubText()
    {
        String baseString = amount + (fields.not ? "%- " : "%+ ") + PGR.core.tooltips.hp.title;
        switch (target)
        {
            case All:
            case Any:
                return TEXT.conditions.ifAnyCharacterHas(baseString);
            case AllEnemy:
                return TEXT.conditions.ifAnyEnemyHas(baseString);
            case Single:
                return TEXT.conditions.ifTheEnemyHas(baseString);
            case Self:
                return TEXT.conditions.ifYouHave(baseString);
            default:
                return baseString;
        }
    }
}
