package pinacolada.skills.skills.special.moves;

import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.Hidden;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PMove_Stun extends PMove implements Hidden
{
    public static final PSkillData DATA = register(PMove_Stun.class, PCLEffectType.General);

    public PMove_Stun()
    {
        this(1);
    }

    public PMove_Stun(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_Stun(int amount)
    {
        super(DATA, PCLCardTarget.Single, amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.stun("X");
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
        return TEXT.actions.stun(getTargetString());
    }
}
