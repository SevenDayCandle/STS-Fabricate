package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.actions.utility.LoseBlockAction;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PActiveCond;

@VisibleSkill
public class PCond_PayBlock extends PActiveCond<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PCond_PayBlock.class, PField_Empty.class)
            .noTarget();

    public PCond_PayBlock(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_PayBlock() {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_PayBlock(int amount) {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return info != null && info.source.currentBlock >= amount;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return capital(TEXT.act_pay(TEXT.subjects_x, PGR.core.tooltips.block.title), true);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return capital(TEXT.act_pay(getAmountRawString(), PGR.core.tooltips.block), true);
    }

    @Override
    protected PCLAction<?> useImpl(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> onComplete, ActionT1<PCLUseInfo> onFail) {
        if (!conditionMetCache) {
            return order.callback(() -> onFail.invoke(info));
        }
        return order.callback(new LoseBlockAction(info.source, info.source, amount), () -> {
            if (conditionMetCache) {
                onComplete.invoke(info);
            }
            else {
                onFail.invoke(info);
            }
        });
    }
}
