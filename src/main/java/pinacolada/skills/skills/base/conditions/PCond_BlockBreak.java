package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnAttackSubscriber;
import pinacolada.interfaces.subscribers.OnBlockBrokenSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PActiveNonCheckCond;
import pinacolada.skills.skills.PLimit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

@VisibleSkill
public class PCond_BlockBreak extends PActiveNonCheckCond<PField_Not> implements OnBlockBrokenSubscriber {
    public static final PSkillData<PField_Not> DATA = register(PCond_BlockBreak.class, PField_Not.class, 1, 1);

    public PCond_BlockBreak() {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_BlockBreak(PCLCardTarget target) {
        super(DATA, target, 0);
    }

    public PCond_BlockBreak(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return isUnderWhen(callingSkill, parentSkill) ? TEXT.cond_whenSingle(TEXT.act_breakXonY(PGR.core.tooltips.block.title, TEXT.subjects_x)) : TEXT.act_breakXonY(PGR.core.tooltips.block.title, TEXT.subjects_x);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        // TODO proper grammar for other sources
        String baseString = TEXT.act_breakXonY(PGR.core.tooltips.block.title, getTargetStringPerspective(perspective));
        if (isWhenClause()) {
            return TEXT.cond_wheneverYou(baseString);
        }

        return TEXT.cond_ifXDid(TEXT.subjects_you, baseString);
    }

    // When the target loses its block, trigger the effect on the target
    @Override
    public void onBlockBroken(AbstractCreature creature) {
        PCLUseInfo pInfo = generateInfo(creature);
        if (target.getTargets(getOwnerCreature(), creature, pInfo.targetList).contains(creature)) {
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
