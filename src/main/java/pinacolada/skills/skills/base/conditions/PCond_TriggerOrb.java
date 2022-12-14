package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

import java.util.List;

public class PCond_TriggerOrb extends PCond
{
    public static final PSkillData DATA = register(PCond_TriggerOrb.class, PCLEffectType.Orb)
            .selfTarget();

    public PCond_TriggerOrb(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_TriggerOrb()
    {
        super(DATA, PCLCardTarget.None, 1, new PCLOrbHelper[]{});
    }

    public PCond_TriggerOrb(int amount, PCLOrbHelper... powers)
    {
        super(DATA, PCLCardTarget.None, amount, powers);
    }

    public PCond_TriggerOrb(int amount, List<PCLOrbHelper> powers)
    {
        super(DATA, PCLCardTarget.None, amount, powers.toArray(new PCLOrbHelper[]{}));
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.trigger("X");
    }

    @Override
    public String getSubText()
    {
        Object tt = !orbs.isEmpty() ? getOrbString() : TEXT.cardEditor.orbs;
        if (isTrigger())
        {
            return TEXT.conditions.wheneverYou(TEXT.actions.trigger(tt));
        }
        return TEXT.actions.trigger(amount <= 1 ? TEXT.subjects.yourFirst(tt) : TEXT.subjects.yourFirst(EUIRM.strings.numNoun(getAmountRawString(), tt)));
    }

    @Override
    public boolean triggerOnOrbTrigger(AbstractOrb o)
    {
        if (this.childEffect != null && getOrbFilter().invoke(o))
        {
            this.childEffect.use(makeInfo(null));
        }
        return true;
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        if ((orbs.isEmpty() && GameUtilities.getOrbCount() < amount) || EUIUtils.any(orbs, o -> GameUtilities.getOrbCount(o.ID) < amount))
        {
            return false;
        }
        if (isUsing)
        {
            getActions().triggerOrbPassive(1, amount, false).setFilter(getOrbFilter());
        }
        return true;
    }
}
