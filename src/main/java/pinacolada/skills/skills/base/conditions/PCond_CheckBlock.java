package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;

import java.util.List;

@VisibleSkill
public class PCond_CheckBlock extends PCond<PField_Not>
{
    public static final PSkillData<PField_Not> DATA = register(PCond_CheckBlock.class, PField_Not.class);

    public PCond_CheckBlock(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_CheckBlock()
    {
        super(DATA, PCLCardTarget.Self, 1);
    }

    public PCond_CheckBlock(PCLCardTarget target, int amount)
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
        return EUIUtils.any(targets, m -> amount == 0 ? info.target.currentBlock == 0 : info.target.currentBlock >= amount);
    }

    @Override
    public String getSampleText()
    {
        return EUIRM.strings.numNoun(TEXT.subjects.x, PGR.core.tooltips.block.title);
    }

    @Override
    public String getSubText()
    {
        String baseString = amount > 1 ? EUIRM.strings.numNoun(amount + "+", PGR.core.tooltips.block) : amount == 0 ? EUIRM.strings.numNoun(amount, PGR.core.tooltips.block) : PGR.core.tooltips.block.toString();
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
