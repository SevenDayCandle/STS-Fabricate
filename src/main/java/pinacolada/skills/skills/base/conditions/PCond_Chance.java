package pinacolada.skills.skills.base.conditions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.actions.special.CooldownProgressAction;
import pinacolada.actions.utility.CallbackAction;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PActiveCond;
import pinacolada.skills.skills.PActiveNonCheckCond;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PCond_Chance extends PActiveNonCheckCond<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PCond_Chance.class, PField_Not.class)
            .noTarget();

    public PCond_Chance(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_Chance() {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_Chance(int amount) {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_generic2(PGR.core.tooltips.chance.title, "X%");
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return TEXT.cond_ifX(TEXT.act_generic2(PGR.core.tooltips.chance.title, getAmountRawString(requestor) + "%"));
    }

    @Override
    protected PCLAction<?> useImpl(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> onComplete, ActionT1<PCLUseInfo> onFail) {
        return order.callback(() -> {
            conditionMetCache = GameUtilities.chance(refreshAmount(info));
            if (conditionMetCache) {
                onComplete.invoke(info);
            }
            else {
                onFail.invoke(info);
            }
        });
    }
}
