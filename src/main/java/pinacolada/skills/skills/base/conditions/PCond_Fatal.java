package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnMonsterDeathSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Random;
import pinacolada.skills.skills.PActiveNonCheckCond;
import pinacolada.skills.skills.PLimit;
import pinacolada.skills.skills.base.primary.PTrigger_When;
import pinacolada.utilities.GameUtilities;

import java.util.List;

@VisibleSkill
public class PCond_Fatal extends PActiveNonCheckCond<PField_Random> implements OnMonsterDeathSubscriber
{
    public static final PSkillData<PField_Random> DATA = register(PCond_Fatal.class, PField_Random.class, 1, 1)
            .selfTarget();

    public PCond_Fatal()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_Fatal(PSkillSaveData content)
    {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill)
    {
        return callingSkill instanceof PTrigger_When ? TEXT.cond_whenSingle(PGR.core.tooltips.kill.present()) : super.getSampleText(callingSkill);
    }

    @Override
    public String getSubText()
    {
        if (isWhenClause())
        {
            return TEXT.cond_whenMulti(TEXT.subjects_anyEnemy(), PGR.core.tooltips.kill.present());
        }
        if (fields.random)
        {
            return fields.not ? getTargetIsString(TEXT.cond_not(PGR.core.tooltips.kill.past())) : getTargetIsString(PGR.core.tooltips.kill.past());
        }
        return fields.not ? TEXT.cond_not(PGR.core.tooltips.fatal.title) : TEXT.cond_ifX(PGR.core.tooltips.fatal.title);
    }

    @Override
    public void onMonsterDeath(AbstractMonster monster, boolean triggerRelics)
    {
        useFromTrigger(makeInfo(monster));
    }

    protected PCLAction<?> useImpl(PCLUseInfo info, ActionT0 onComplete, ActionT0 onFail)
    {
        List<AbstractCreature> targetList = getTargetList(info);
        return PCLActions.last.callback(targetList, (targets, __) -> {
            if (targets.size() > 0 && EUIUtils.any(targets, t -> GameUtilities.isFatal(t, fields.random)) && (!(parent instanceof PLimit) || ((PLimit) parent).tryActivate(info)))
            {
                onComplete.invoke();
            }
            else
            {
                onFail.invoke();
            }
        }).isCancellable(false);
    }
}
