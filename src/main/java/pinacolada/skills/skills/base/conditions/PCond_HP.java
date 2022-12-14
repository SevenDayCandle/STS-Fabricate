package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import java.util.List;

public class PCond_HP extends PCond
{
    public static final PSkillData DATA = register(PCond_HP.class, PCLEffectType.General)
            .selfTarget();

    public PCond_HP(PSkillSaveData content)
    {
        super(content);
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
        return EUIUtils.any(targetList, t -> alt ? t.currentHealth * 100 / t.maxHealth <= amount : t.currentHealth * 100 / t.maxHealth >= amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.generic2(PGR.core.tooltips.hp.title, "X%");
    }

    @Override
    public String getSubText()
    {
        String baseString = amount + (alt ? "%- " : "%+ ") + PGR.core.tooltips.hp.title;
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
