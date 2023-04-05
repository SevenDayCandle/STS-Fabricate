package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.subscribers.OnBlockBrokenSubscriber;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PActiveCond;
import pinacolada.skills.skills.PLimit;

import java.util.HashMap;

@VisibleSkill
public class PCond_UnblockedDamage extends PActiveCond<PField_Not> implements OnBlockBrokenSubscriber
{
    public static final PSkillData<PField_Not> DATA = register(PCond_UnblockedDamage.class, PField_Not.class, 1, 1)
            .selfTarget();

    public PCond_UnblockedDamage()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_UnblockedDamage(PSkillSaveData content)
    {
        super(DATA, content);
    }

    @Override
    public String getSubText()
    {
        String baseString = TEXT.subjects_unblocked(TEXT.subjects_damage);
        if (isWhenClause())
        {
            return getWheneverString(TEXT.act_lose(PGR.core.tooltips.block));
        }

        switch (target)
        {
            case All:
            case Any:
                return TEXT.cond_ifTargetTook(TEXT.subjects_anyone, baseString);
            case AllEnemy:
                return TEXT.cond_ifTargetTook(TEXT.subjects_anyEnemy(), baseString);
            case Single:
                return TEXT.cond_ifTargetTook(TEXT.subjects_target, baseString);
            case Self:
                return TEXT.cond_ifTargetTook(TEXT.subjects_you, baseString);
            default:
                return baseString;
        }
    }

    @Override
    public void onBlockBroken(AbstractCreature t)
    {
        if (target.targetsSingle() ? t == getOwnerCreature() : target.getTargets(t, t).contains(t))
        {
            useFromTrigger(makeInfo(t));
        }
    }

    protected PCLAction<?> useImpl(PCLUseInfo info, ActionT0 onComplete, ActionT0 onFail)
    {
        // Checks to see if any of the targets' health is decreased after this card is used
        HashMap<AbstractCreature, Integer> healthMap = EUIUtils.hashMap(getTargetList(info), c -> c.currentHealth);
        return PCLActions.last.callback(healthMap, (targets, __) -> {
            if (targets.size() > 0 && EUIUtils.any(targets.keySet(), t -> t.currentHealth < targets.get(t)) && (!(parent instanceof PLimit) || ((PLimit) parent).tryActivate(info)))
            {
                onComplete.invoke();
            }
            else
            {
                onFail.invoke();
            }
        });
    }
}
