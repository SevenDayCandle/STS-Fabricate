package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
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
public class PCond_PayEnergy extends PActiveCond<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PCond_PayEnergy.class, PField_Empty.class)
            .noTarget();

    public PCond_PayEnergy(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_PayEnergy() {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_PayEnergy(int amount) {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return EnergyPanel.getCurrentEnergy() >= refreshAmount(info);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return capital(TEXT.act_pay(TEXT.subjects_x, PGR.core.tooltips.energy.title), true);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return capital(TEXT.act_pay(getAmountRawString(requestor), PGR.core.tooltips.energy), true);
    }

    @Override
    protected PCLAction<?> useImpl(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> onComplete, ActionT1<PCLUseInfo> onFail) {
        return order.spendEnergy(refreshAmount(info), false)
                .addCallback(res -> {
                    if (res != null) {
                        onComplete.invoke(info);
                    }
                    else {
                        onFail.invoke(info);
                    }
                });
    }
}
