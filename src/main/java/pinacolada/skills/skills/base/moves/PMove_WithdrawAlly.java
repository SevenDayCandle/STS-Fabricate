package pinacolada.skills.skills.base.moves;

import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import java.util.List;

public class PMove_WithdrawAlly extends PMove
{
    public static final PSkillData DATA = register(PMove_WithdrawAlly.class, PCLEffectType.General)
            .setTargets(PCLCardTarget.AllAlly, PCLCardTarget.RandomAlly, PCLCardTarget.SingleAlly)
            .pclOnly();

    public PMove_WithdrawAlly()
    {
        this(1);
    }

    public PMove_WithdrawAlly(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_WithdrawAlly(int amount)
    {
        super(DATA, PCLCardTarget.Self, amount);
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
                this.childEffect.setCards(cards);
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
