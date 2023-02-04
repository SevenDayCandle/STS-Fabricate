package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.subscribers.OnOrbPassiveEffectSubscriber;
import pinacolada.misc.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PCond_TriggerOrb extends PPassiveCond<PField_Orb> implements OnOrbPassiveEffectSubscriber
{
    public static final PSkillData<PField_Orb> DATA = register(PCond_TriggerOrb.class, PField_Orb.class)
            .selfTarget();

    public PCond_TriggerOrb(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_TriggerOrb()
    {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_TriggerOrb(int amount, PCLOrbHelper... orbs)
    {
        super(DATA, PCLCardTarget.None, amount);
        fields.setOrb(orbs);
    }


    @Override
    public String getSampleText()
    {
        return TEXT.act_trigger(TEXT.subjects_x);
    }

    @Override
    public String getSubText()
    {
        Object tt = fields.getOrbOrString();
        if (isTrigger())
        {
            return TEXT.cond_wheneverYou(TEXT.act_trigger(tt));
        }
        return TEXT.act_trigger(amount <= 1 ? TEXT.subjects_yourFirst(tt) : TEXT.subjects_yourFirst(EUIRM.strings.numNoun(getAmountRawString(), tt)));
    }

    @Override
    public void onOrbPassiveEffect(AbstractOrb orb)
    {
        if (fields.getOrbFilter().invoke(orb))
        {
            useFromTrigger(makeInfo(null).setData(orb));
        }
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        if ((fields.orbs.isEmpty() && GameUtilities.getOrbCount() < amount) || EUIUtils.any(fields.orbs, o -> GameUtilities.getOrbCount(o.ID) < amount))
        {
            return false;
        }
        if (isUsing)
        {
            getActions().triggerOrbPassive(1, amount, false).setFilter(fields.getOrbFilter());
        }
        return true;
    }
}
