package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.subscribers.OnOrbEvokeSubscriber;
import pinacolada.misc.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PCond_EvokeOrb extends PPassiveCond<PField_Orb> implements OnOrbEvokeSubscriber
{
    public static final PSkillData<PField_Orb> DATA = register(PCond_EvokeOrb.class, PField_Orb.class)
            .selfTarget();

    public PCond_EvokeOrb(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_EvokeOrb()
    {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_EvokeOrb(int amount, PCLOrbHelper... orbs)
    {
        super(DATA, PCLCardTarget.None, amount);
        fields.setOrb(orbs);
    }

    @Override
    public PCond_EvokeOrb onAddToCard(AbstractCard card)
    {
        super.onAddToCard(card);
        card.showEvokeValue = amount > 0;
        card.showEvokeOrbCount = amount;
        return this;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.act_evoke(TEXT.subjects_x);
    }

    @Override
    public String getSubText()
    {
        Object tt = fields.getOrbAndOrString();
        if (isWhenClause())
        {
            return TEXT.cond_wheneverYou(TEXT.act_evoke(tt));
        }
        return TEXT.act_evoke(amount <= 1 ? TEXT.subjects_yourFirst(tt) : TEXT.subjects_yourFirst(EUIRM.strings.numNoun(getAmountRawString(), tt)));
    }

    @Override
    public void onEvokeOrb(AbstractOrb orb)
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
        if (isUsing && !isWhenClause())
        {
            getActions().evokeOrb(1, amount).setFilter(fields.getOrbFilter());
        }
        return true;
    }
}
