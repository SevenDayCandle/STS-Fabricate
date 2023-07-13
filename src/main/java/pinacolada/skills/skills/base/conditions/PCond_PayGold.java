package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.actions.common.GainGoldAction;
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
public class PCond_PayGold extends PActiveCond<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PCond_PayGold.class, PField_Empty.class)
            .selfTarget();

    public PCond_PayGold(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_PayGold() {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_PayGold(int amount) {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return info != null && info.source.gold >= amount;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.act_pay(TEXT.subjects_x, PGR.core.tooltips.gold.title);
    }

    @Override
    public String getSubText() {
        return capital(TEXT.act_pay(getAmountRawString(), PGR.core.tooltips.gold), true);
    }

    @Override
    protected PCLAction<?> useImpl(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> onComplete, ActionT1<PCLUseInfo> onFail) {
        return order.callback(new GainGoldAction(-amount), () -> {
            if (conditionMetCache) {
                onComplete.invoke(info);
            }
            else {
                onFail.invoke(info);
            }
        });
    }
}
