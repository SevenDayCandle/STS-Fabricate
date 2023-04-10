package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.subscribers.OnOrbChannelSubscriber;
import pinacolada.misc.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.skills.skills.base.primary.PTrigger_When;

@VisibleSkill
public class PCond_HaveOrbChanneled extends PPassiveCond<PField_Orb> implements OnOrbChannelSubscriber
{
    public static final PSkillData<PField_Orb> DATA = register(PCond_HaveOrbChanneled.class, PField_Orb.class)
            .selfTarget();

    public PCond_HaveOrbChanneled()
    {
        this(1);
    }

    public PCond_HaveOrbChanneled(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_HaveOrbChanneled(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    public PCond_HaveOrbChanneled(int amount, PCLOrbHelper... orbs)
    {
        super(DATA, PCLCardTarget.None, amount);
        fields.setOrb(orbs);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource)
    {
        int count = EUIUtils.count(fields.random ? AbstractDungeon.actionManager.orbsChanneledThisCombat : AbstractDungeon.actionManager.orbsChanneledThisTurn,
                c -> fields.getOrbFilter().invoke(c));
        return amount == 0 ? count == 0 : fields.not ^ count >= amount;
    }

    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.channel;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill)
    {
        return callingSkill instanceof PTrigger_When ? TEXT.cond_wheneverYou(TEXT.act_channel(PGR.core.tooltips.orb.title)) : TEXT.cond_ifX(PCLCoreStrings.past(getActionTooltip()));
    }

    @Override
    public String getSubText()
    {
        String tt = fields.getOrbAndOrString();
        if (isWhenClause())
        {
            return TEXT.cond_wheneverYou(TEXT.act_channel(tt));
        }
        return fields.random ? TEXT.cond_ifYouDidThisCombat(PCLCoreStrings.past(getActionTooltip()), EUIRM.strings.numNoun(getAmountRawString(), tt)) :
                TEXT.cond_ifYouDidThisTurn(PCLCoreStrings.past(getActionTooltip()), EUIRM.strings.numNoun(getAmountRawString(), tt));
    }

    @Override
    public void onChannelOrb(AbstractOrb orb)
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
