package pinacolada.skills.skills.special.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.actions.special.KillCharacterAction;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

public class PMove_Kill extends PMove<PField_Empty>
{
    public static final PSkillData<PField_Empty> DATA = register(PMove_Kill.class, PField_Empty.class);

    public PMove_Kill()
    {
        this(1);
    }

    public PMove_Kill(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_Kill(int amount)
    {
        super(DATA, PCLCardTarget.Single, amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.act_stun(TEXT.subjects_x);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        for (AbstractCreature target : getTargetList(info))
        {
            getActions().add(new KillCharacterAction(info.source, target));
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        return TEXT.act_kill(getTargetString());
    }
}
