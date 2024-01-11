package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
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
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PActiveCond;

@VisibleSkill
public class PCond_TakeDamageTo extends PActiveCond<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PCond_TakeDamageTo.class, PField_Not.class)
            .noTarget();

    public PCond_TakeDamageTo(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_TakeDamageTo() {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_TakeDamageTo(int amount) {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return info != null && (fields.not ? info.source.currentHealth > refreshAmount(info) : info.source.currentHealth + info.source.currentBlock > refreshAmount(info));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.cond_xToY(TEXT.act_takeDamage(TEXT.subjects_x), TEXT.subjects_x);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return fields.not ? TEXT.act_loseAmount(getAmountRawString(), PGR.core.tooltips.hp.title) : TEXT.act_takeDamage(getAmountRawString());
    }

    @Override
    protected PCLAction<?> useImpl(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> onComplete, ActionT1<PCLUseInfo> onFail) {
        return fields.not ? order.loseHP(refreshAmount(info), AbstractGameAction.AttackEffect.NONE).addCallback(c -> onComplete.invoke(info))
                : order.takeDamage(refreshAmount(info), AbstractGameAction.AttackEffect.NONE).addCallback(c -> onComplete.invoke(info));
    }
}
