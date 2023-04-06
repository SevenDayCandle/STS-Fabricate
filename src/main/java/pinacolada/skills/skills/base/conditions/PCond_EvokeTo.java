package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import pinacolada.actions.PCLAction;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.skills.skills.PActiveCond;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PCond_EvokeTo extends PActiveCond<PField_Orb>
{
    public static final PSkillData<PField_Orb> DATA = register(PCond_EvokeTo.class, PField_Orb.class)
            .selfTarget();

    public PCond_EvokeTo(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_EvokeTo()
    {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_EvokeTo(int amount, PCLOrbHelper... orbs)
    {
        super(DATA, PCLCardTarget.None, amount);
        fields.setOrb(orbs);
    }

    @Override
    public PCond_EvokeTo onAddToCard(AbstractCard card)
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
        return TEXT.act_evoke(amount <= 1 ? TEXT.subjects_yourFirst(tt) : TEXT.subjects_yourFirst(EUIRM.strings.numNoun(getAmountRawString(), tt)));
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource)
    {
        return (!fields.orbs.isEmpty() || GameUtilities.getOrbCount() >= amount) && !EUIUtils.any(fields.orbs, o -> GameUtilities.getOrbCount(o.ID) < amount);
    }

    @Override
    protected PCLAction<?> useImpl(PCLUseInfo info, ActionT0 onComplete, ActionT0 onFail)
    {
        return getActions().evokeOrb(1, amount).setFilter(fields.getOrbFilter())
                .addCallback(orbs -> {
                    if (orbs.size() >= amount)
                    {
                        info.setData(orbs);
                        onComplete.invoke();
                    }
                    else
                    {
                        onFail.invoke();
                    }
                });
    }
}