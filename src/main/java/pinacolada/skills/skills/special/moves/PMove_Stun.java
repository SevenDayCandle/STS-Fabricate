package pinacolada.skills.skills.special.moves;

import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

public class PMove_Stun extends PMove<PField_Empty>
{
    public static final PSkillData<PField_Empty> DATA = register(PMove_Stun.class, PField_Empty.class);

    public PMove_Stun()
    {
        this(1);
    }

    public PMove_Stun(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_Stun(int amount)
    {
        super(DATA, PCLCardTarget.Single, amount);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill)
    {
        return TEXT.act_stun(TEXT.subjects_x);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (info.target instanceof AbstractMonster)
        {
            getActions().applyPower(info.source, new StunMonsterPower((AbstractMonster) info.target, amount));
        }
        else
        {
            getActions().add(new PressEndTurnButtonAction());
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        return TEXT.act_stun(getTargetString());
    }
}
