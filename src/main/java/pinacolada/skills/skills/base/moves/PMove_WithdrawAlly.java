package pinacolada.skills.skills.base.moves;

import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

import java.util.List;

@VisibleSkill
public class PMove_WithdrawAlly extends PMove<PField_Empty>
{
    public static final PSkillData<PField_Empty> DATA = register(PMove_WithdrawAlly.class, PField_Empty.class)
            .setTargets(PCLCardTarget.AllAlly, PCLCardTarget.RandomAlly, PCLCardTarget.SingleAlly)
            .pclOnly();

    public PMove_WithdrawAlly()
    {
        this(1);
    }

    public PMove_WithdrawAlly(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_WithdrawAlly(int amount)
    {
        super(DATA, PCLCardTarget.SingleAlly, amount);
    }

    public PMove_WithdrawAlly(PCLCardTarget target, int amount)
    {
        super(DATA, target, amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.withdraw(PGR.core.tooltips.summon.title);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        List<PCLCardAlly> targets = EUIUtils.map(getTargetList(info), t -> EUIUtils.safeCast(t, PCLCardAlly.class));
        getActions().withdrawAlly(targets).addCallback(cards ->
        {
            if (this.childEffect != null)
            {
                info.setData(cards);
                this.childEffect.use(info);
            }
        });
    }

    @Override
    public String getSubText()
    {
        return TEXT.actions.withdraw(getTargetString());
    }
}
