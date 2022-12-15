package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.PCondWithoutCheck;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

import java.util.List;

public class PCond_Fatal extends PCond implements PCondWithoutCheck
{

    public static final PSkillData DATA = register(PCond_Fatal.class, PCLEffectType.General, 1, 1)
            .selfTarget();

    public PCond_Fatal()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_Fatal(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_Fatal(PSkill effect)
    {
        this();
        setChild(effect);
    }

    public PCond_Fatal(PSkill... effect)
    {
        this();
        setChild(effect);
    }

    @Override
    public String getSubText()
    {
        return alt ? TEXT.conditions.not(PGR.core.tooltips.fatal.title) : TEXT.conditions.ifX(PGR.core.tooltips.fatal.title);
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

    // Use check is handled separately
    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return false;
    }

    protected void useImpl(PCLUseInfo info, ActionT0 callback)
    {
        List<AbstractCreature> targetList = getTargetList(info);
        PCLActions.last.callback(targetList, (targets, __) -> {
            if (targets.size() > 0 && EUIUtils.any(targets, GameUtilities::isDeadOrEscaped) && (!(parent instanceof PCond_Info) || ((PCond_Info) parent).tryActivate(info)))
            {
                callback.invoke();

            }
        }).isCancellable(false);
    }
}
