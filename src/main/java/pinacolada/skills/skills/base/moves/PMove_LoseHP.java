package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PMove_LoseHP extends PMove<PField_Empty>
{
    public static final PSkillData<PField_Empty> DATA = register(PMove_LoseHP.class, PField_Empty.class);

    public PMove_LoseHP()
    {
        this(1);
    }

    public PMove_LoseHP(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_LoseHP(int amount)
    {
        super(DATA, PCLCardTarget.Self, amount);
    }

    public PMove_LoseHP(PCLCardTarget target, int amount)
    {
        super(DATA, target, amount);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill)
    {
        return TEXT.act_loseAmount(TEXT.subjects_x, PGR.core.tooltips.hp.title);
    }

    @Override
    public boolean isDetrimental()
    {
        return target.targetsSelf() || target.targetsAllies();
    }

    @Override
    public void use(PCLUseInfo info)
    {
        for (AbstractCreature t : getTargetList(info))
        {
            getActions().loseHP(info.source, t, amount, AbstractGameAction.AttackEffect.NONE).isCancellable(false);
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        if (target == PCLCardTarget.Self && !isFromCreature())
        {
            return TEXT.act_loseAmount(getAmountRawString(), PGR.core.tooltips.hp.title);
        }
        return TEXT.act_objectLoses(getTargetString(), getAmountRawString(), PGR.core.tooltips.hp.title);

    }
}
