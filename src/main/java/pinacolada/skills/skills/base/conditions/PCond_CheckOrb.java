package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.subscribers.OnOrbChannelSubscriber;
import pinacolada.misc.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PCond_CheckOrb extends PPassiveCond<PField_Orb> implements OnOrbChannelSubscriber
{
    public static final PSkillData<PField_Orb> DATA = register(PCond_CheckOrb.class, PField_Orb.class)
            .selfTarget();

    public PCond_CheckOrb(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_CheckOrb(int amount, PCLOrbHelper... orb)
    {
        super(DATA, PCLCardTarget.None, amount);
        fields.setOrb(orb);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.act_channel(TEXT.subjects_x);
    }

    @Override
    public String getSubText()
    {
        String tt = fields.getOrbAndOrString();
        if (isTrigger())
        {
            return TEXT.cond_wheneverYou(TEXT.act_channel(tt));
        }
        return TEXT.cond_ifYouHave(amount == 1 ? tt : EUIRM.strings.numNoun(amount <= 0 ? amount : amount + "+", tt));
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        if (fields.orbs.isEmpty())
        {
            return amount <= 0 ? GameUtilities.getOrbCount() == 0 : GameUtilities.getOrbCount() >= amount;
        }
        return fields.random ? EUIUtils.any(fields.orbs, o -> GameUtilities.getOrbCount(o.ID) >= amount) : EUIUtils.all(fields.orbs, o -> GameUtilities.getOrbCount(o.ID) >= amount);
    }

    @Override
    public void onChannelOrb(AbstractOrb orb)
    {
        if (fields.getOrbFilter().invoke(orb))
        {
            useFromTrigger(makeInfo(null).setData(orb));
        }
    }
}
