package pinacolada.skills.skills.special.moves;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIUtils;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

import java.util.List;

public class PMove_ExhaustAlly extends PMove<PField_Empty>
{
    public static final PSkillData<PField_Empty> DATA = register(PMove_ExhaustAlly.class, PField_Empty.class)
            .setTargets(PCLCardTarget.AllAlly, PCLCardTarget.RandomAlly, PCLCardTarget.SingleAlly)
            .pclOnly();

    public PMove_ExhaustAlly()
    {
        this(1);
    }

    public PMove_ExhaustAlly(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_ExhaustAlly(int amount)
    {
        super(DATA, PCLCardTarget.SingleAlly, amount);
    }

    public PMove_ExhaustAlly(PCLCardTarget target, int amount)
    {
        super(DATA, target, amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.act_withdraw(PGR.core.tooltips.summon.title);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        List<PCLCardAlly> targets = EUIUtils.map(getTargetList(info), t -> EUIUtils.safeCast(t, PCLCardAlly.class));
        getActions().withdrawAlly(targets).setDestination(AbstractDungeon.player.exhaustPile).setOptions(true, false).addCallback(cards ->
        {
            if (this.childEffect != null)
            {
                info.setData(cards);
                this.childEffect.use(info);
            }
        });
    }

    @Override
    public String getSubText()
    {
        return TEXT.act_exhaust(getTargetString());
    }
}