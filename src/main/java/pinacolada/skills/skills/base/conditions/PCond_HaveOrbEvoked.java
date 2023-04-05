package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.subscribers.OnOrbEvokeSubscriber;
import pinacolada.misc.CombatManager;
import pinacolada.misc.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.skills.skills.PPassiveCond;

@VisibleSkill
public class PCond_HaveOrbEvoked extends PPassiveCond<PField_Orb> implements OnOrbEvokeSubscriber
{
    public static final PSkillData<PField_Orb> DATA = register(PCond_HaveOrbEvoked.class, PField_Orb.class)
            .selfTarget();

    public PCond_HaveOrbEvoked()
    {
        this(1);
    }

    public PCond_HaveOrbEvoked(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_HaveOrbEvoked(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    public PCond_HaveOrbEvoked(int amount, PCLOrbHelper... orbs)
    {
        super(DATA, PCLCardTarget.None, amount);
        fields.setOrb(orbs);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        int count = EUIUtils.count(fields.random ? CombatManager.orbsEvokedThisCombat() : CombatManager.orbsEvokedThisTurn(),
                c -> fields.getOrbFilter().invoke(c));
        return amount == 0 ? count == 0 : fields.not ^ count >= amount;
    }

    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.evoke;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.cond_ifX(PCLCoreStrings.past(getActionTooltip()));
    }

    @Override
    public String getSubText()
    {
        Object tt = fields.getOrbAndOrString();
        if (isWhenClause())
        {
            return TEXT.cond_wheneverYou(TEXT.act_evoke(tt));
        }
        return fields.random ? TEXT.cond_ifYouDidThisCombat(PCLCoreStrings.past(getActionTooltip()), EUIRM.strings.numNoun(getAmountRawString(), tt)) :
                TEXT.cond_ifYouDidThisTurn(PCLCoreStrings.past(getActionTooltip()), EUIRM.strings.numNoun(getAmountRawString(), tt));
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
    public String wrapAmount(int input)
    {
        return input == 0 ? String.valueOf(input) : (fields.not ? (input + "-") : (input + "+"));
    }
}
