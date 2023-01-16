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
import pinacolada.skills.fields.PField_Orb;
import pinacolada.utilities.GameUtilities;

public class PCond_TriggerOrb extends PCond<PField_Orb>
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
        return TEXT.actions.trigger(TEXT.subjects.x);
    }

    @Override
    public String getSubText()
    {
        Object tt = fields.getOrbOrString();
        if (isTrigger())
        {
            return TEXT.conditions.wheneverYou(TEXT.actions.trigger(tt));
        }
        return TEXT.actions.trigger(amount <= 1 ? TEXT.subjects.yourFirst(tt) : TEXT.subjects.yourFirst(EUIRM.strings.numNoun(getAmountRawString(), tt)));
    }

    @Override
    public boolean triggerOnOrbTrigger(AbstractOrb o)
    {
        if (this.childEffect != null && fields.getOrbFilter().invoke(o))
        {
            this.childEffect.use(makeInfo(null));
        }
        return true;
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
