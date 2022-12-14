package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import java.util.List;

public class PMove_LoseHP extends PMove
{
    public static final PSkillData DATA = register(PMove_LoseHP.class, PCLEffectType.General);

    public PMove_LoseHP()
    {
        this(1);
    }

    public PMove_LoseHP(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_LoseHP(int amount)
    {
        super(DATA, PCLCardTarget.Self, amount);
    }

    public PMove_LoseHP(PCLCardTarget target, int amount)
    {
        super(DATA, target, amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.loseAmount("X", PGR.core.tooltips.hp.title);
    }

    @Override
    public boolean isDetrimental()
    {
        return true;
    }

    @Override
    public void use(PCLUseInfo info)
    {
        List<AbstractCreature> targets = getTargetList(info);
        for (AbstractCreature t : targets)
        {
            getActions().loseHP(info.source, t, amount, AbstractGameAction.AttackEffect.NONE).isCancellable(false);
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        if (target == PCLCardTarget.Self)
        {
            return TEXT.actions.loseAmount(getAmountRawString(), PGR.core.tooltips.hp.title);
        }
        return TEXT.actions.objectLoses(getTargetString(), getAmountRawString(), PGR.core.tooltips.hp.title);

    }
}
