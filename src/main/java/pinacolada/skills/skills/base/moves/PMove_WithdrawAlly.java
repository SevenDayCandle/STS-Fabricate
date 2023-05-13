package pinacolada.skills.skills.base.moves;

import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PCallbackMove;
import pinacolada.ui.combat.SummonPool;

import java.util.List;

@VisibleSkill
public class PMove_WithdrawAlly extends PCallbackMove<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PMove_WithdrawAlly.class, PField_Empty.class)
            .setTargets(PCLCardTarget.AllAlly, PCLCardTarget.RandomAlly, PCLCardTarget.SingleAlly)
            .pclOnly();

    public PMove_WithdrawAlly() {
        this(SummonPool.BASE_TRIGGER);
    }

    public PMove_WithdrawAlly(int amount) {
        super(DATA, PCLCardTarget.SingleAlly, amount);
    }

    public PMove_WithdrawAlly(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_WithdrawAlly(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.act_withdraw(PGR.core.tooltips.summon.title);
    }

    @Override
    public String getSubText() {
        return TEXT.act_withdraw(getTargetString());
    }

    @Override
    public void use(PCLUseInfo info, ActionT1<PCLUseInfo> callback) {
        List<PCLCardAlly> targets = EUIUtils.map(getTargetList(info), t -> EUIUtils.safeCast(t, PCLCardAlly.class));
        getActions().withdrawAlly(targets).setTriggerTimes(amount).addCallback(cards ->
        {
            info.setData(cards);
            callback.invoke(info);
            if (this.childEffect != null) {
                this.childEffect.use(info);
            }
        });
    }
}
