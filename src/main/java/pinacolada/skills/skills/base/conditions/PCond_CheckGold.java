package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveCond;

import java.util.List;

@VisibleSkill
public class PCond_CheckGold extends PPassiveCond<PField_Not>
{
    public static final PSkillData<PField_Not> DATA = register(PCond_CheckGold.class, PField_Not.class).selfTarget();

    public PCond_CheckGold(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_CheckGold()
    {
        super(DATA, PCLCardTarget.Self, 1);
    }

    public PCond_CheckGold(PCLCardTarget target, int amount)
    {
        super(DATA, target, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        List<AbstractCreature> targets = getTargetList(info);
        if (target == PCLCardTarget.Single && info.target == null)
        {
            return false;
        }
        return EUIUtils.any(targets, m -> amount == 0 ? info.target.gold == 0 : info.target.gold >= amount);
    }

    @Override
    public String getSampleText()
    {
        return EUIRM.strings.numNoun(TEXT.subjects_x, PGR.core.tooltips.gold.title);
    }

    @Override
    public String getSubText()
    {
        String baseString = amount > 1 ? EUIRM.strings.numNoun(getAmountRawString() + "+", PGR.core.tooltips.gold) : amount == 0 ? EUIRM.strings.numNoun(getAmountRawString(), PGR.core.tooltips.gold) : PGR.core.tooltips.gold.toString();
        if (isWhenClause())
        {
            return getWheneverString(TEXT.act_gain(baseString));
        }

        switch (target)
        {
            case All:
            case Any:
                return TEXT.cond_ifAnyCharacterHas(baseString);
            case AllEnemy:
                return TEXT.cond_ifAnyEnemyHas(baseString);
            case Single:
                return TEXT.cond_ifTheEnemyHas(baseString);
            case Self:
                return TEXT.cond_ifYouHave(baseString);
            default:
                return baseString;
        }
    }
}
