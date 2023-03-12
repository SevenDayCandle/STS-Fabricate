package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.subscribers.OnMonsterDeathSubscriber;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PActiveCond;
import pinacolada.skills.skills.PLimit;
import pinacolada.utilities.GameUtilities;

import java.util.List;

@VisibleSkill
public class PCond_Fatal extends PActiveCond<PField_Not> implements OnMonsterDeathSubscriber
{
    public static final PSkillData<PField_Not> DATA = register(PCond_Fatal.class, PField_Not.class, 1, 1)
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
    public String getSubText()
    {
        if (isWhenClause())
        {
            return TEXT.cond_whenMulti(TEXT.subjects_anyEnemy(), PGR.core.tooltips.kill.present());
        }
        return fields.not ? TEXT.cond_not(PGR.core.tooltips.fatal.title) : TEXT.cond_ifX(PGR.core.tooltips.fatal.title);
    }

    @Override
    public void onMonsterDeath(AbstractMonster monster, boolean triggerRelics)
    {
        useFromTrigger(makeInfo(monster));
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (childEffect != null)
        {
            useImpl(info, () -> childEffect.use(info));
        }
    }

    public void use(PCLUseInfo info, int index)
    {
        if (childEffect != null)
        {
            useImpl(info, () -> childEffect.use(info, index));
        }
    }

    public void use(PCLUseInfo info, boolean isUsing)
    {
        if (isUsing && childEffect != null)
        {
            useImpl(info, () -> childEffect.use(info));
        }
    }

    protected void useImpl(PCLUseInfo info, ActionT0 callback)
    {
        List<AbstractCreature> targetList = getTargetList(info);
        PCLActions.last.callback(targetList, (targets, __) -> {
            if (targets.size() > 0 && EUIUtils.any(targets, GameUtilities::isDeadOrEscaped) && (!(parent instanceof PLimit) || ((PLimit) parent).tryActivate(info)))
            {
                callback.invoke();
            }
        }).isCancellable(false);
    }
}
