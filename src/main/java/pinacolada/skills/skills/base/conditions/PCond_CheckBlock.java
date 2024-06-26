package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnBlockGainedSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveCond;

@VisibleSkill
public class PCond_CheckBlock extends PPassiveCond<PField_Not> implements OnBlockGainedSubscriber {
    public static final PSkillData<PField_Not> DATA = register(PCond_CheckBlock.class, PField_Not.class);

    public PCond_CheckBlock(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_CheckBlock() {
        super(DATA, PCLCardTarget.Self, 1);
    }

    public PCond_CheckBlock(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return evaluateTargets(info, m -> fields.doesValueMatchThreshold(m.currentBlock, refreshAmount(info)));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return isUnderWhen(callingSkill, parentSkill) ? TEXT.cond_when(TEXT.act_gainAmount(TEXT.subjects_x, PGR.core.tooltips.block.title)) : EUIRM.strings.numNoun(TEXT.subjects_x, PGR.core.tooltips.block.title);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String baseString = fields.getThresholdRawString(PGR.core.tooltips.block.toString(), requestor);
        if (isWhenClause()) {
            return getWheneverString(TEXT.act_gainOrdinal(getTargetOrdinalPerspective(perspective), baseString), perspective);
        }

        return getTargetHasStringPerspective(perspective, baseString);
    }

    @Override
    public int onBlockGained(AbstractCreature t, int block) {
        AbstractCreature owner = getOwnerCreature();
        PCLUseInfo info = generateInfo(owner, t);
        boolean eval = evaluateTargets(info, c -> c == t);
        if (eval && fields.doesValueMatchThreshold(block, refreshAmount(info))) {
            useFromTrigger(info.setData(block));
        }
        return block;
    }
}
