package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.subscribers.OnBlockGainedSubscriber;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.skills.skills.base.primary.PTrigger_When;

import java.util.List;

@VisibleSkill
public class PCond_CheckBlock extends PPassiveCond<PField_Not> implements OnBlockGainedSubscriber
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
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource)
    {
        List<AbstractCreature> targets = getTargetList(info);
        return EUIUtils.any(targets, m -> amount == 0 ? m.currentBlock == 0 : m.currentBlock >= amount);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill)
    {
        return callingSkill instanceof PTrigger_When ? TEXT.cond_whenSingle(TEXT.act_gain(PGR.core.tooltips.block.title)) : EUIRM.strings.numNoun(TEXT.subjects_x, PGR.core.tooltips.block.title);
    }

    @Override
    public String getSubText()
    {
        String baseString = amount > 1 ? EUIRM.strings.numNoun(getAmountRawString() + "+", PGR.core.tooltips.block) : amount == 0 ? EUIRM.strings.numNoun(getAmountRawString(), PGR.core.tooltips.block) : PGR.core.tooltips.block.toString();
        if (isWhenClause())
        {
            return getWheneverString(TEXT.act_gain(baseString));
        }

        return getTargetHasString(baseString);
    }

    @Override
    public void onBlockGained(AbstractCreature t, int block)
    {
        if (target.getTargets(getOwnerCreature(), t).contains(t))
        {
            useFromTrigger(makeInfo(t));
        }
    }
}
