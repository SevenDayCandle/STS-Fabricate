package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnBlockBrokenSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PActiveNonCheckCond;
import pinacolada.skills.skills.PLimit;

import java.util.ArrayList;

@VisibleSkill
public class PCond_BlockBreak extends PActiveNonCheckCond<PField_Not> implements OnBlockBrokenSubscriber {
    public static final PSkillData<PField_Not> DATA = register(PCond_BlockBreak.class, PField_Not.class, 1, 1);

    public PCond_BlockBreak() {
        super(DATA, PCLCardTarget.Single, 0);
    }

    public PCond_BlockBreak(PCLCardTarget target) {
        super(DATA, target, 0);
    }

    public PCond_BlockBreak(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return isUnderWhen(callingSkill, parentSkill) ? TEXT.cond_when(TEXT.cond_xOnYIsBroken(PGR.core.tooltips.block.title, TEXT.subjects_x)) : TEXT.cond_xOnYIsBroken(PGR.core.tooltips.block.title, TEXT.subjects_x);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        String baseString = TEXT.cond_xOnYIsBroken(PGR.core.tooltips.block.title, getTargetSubjectStringPerspective(perspective));
        if (isWhenClause()) {
            return baseString;
        }

        return TEXT.cond_ifX(baseString);
    }

    // When the target loses its block, trigger the effect on the target
    @Override
    public void onBlockBroken(AbstractCreature creature) {
        AbstractCreature owner = getOwnerCreature();
        PCLUseInfo pInfo = generateInfo(owner, creature);
        if (target.getTargets(owner, creature, pInfo.tempTargets).contains(creature)) {
            useFromTrigger(pInfo);
        }
    }

    protected PCLAction<?> useImpl(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> onComplete, ActionT1<PCLUseInfo> onFail) {
        // Checks to see if any of the targets had block before this effect
        ArrayList<AbstractCreature> creaturesWithBlock = EUIUtils.filter(getTargetList(info), c -> c.currentBlock > 0);
        return PCLActions.last.callback(creaturesWithBlock, (targets, __) -> {
            if (EUIUtils.any(targets, t -> t.currentBlock <= 0) && (!(parent instanceof PLimit) || ((PLimit) parent).tryActivate(info))) {
                onComplete.invoke(info);
            }
            else {
                onFail.invoke(info);
            }
        });
    }
}
