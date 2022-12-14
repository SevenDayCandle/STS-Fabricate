package pinacolada.skills.skills.base.moves;

import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PMove_Scry extends PMove
{
    public static final PSkillData DATA = register(PMove_Scry.class, PCLEffectType.General).selfTarget();

    public PMove_Scry()
    {
        this(1);
    }

    public PMove_Scry(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_Scry(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.scry("X");
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().scry(amount).addCallback(cards -> {
            if (this.childEffect != null)
            {
                this.childEffect.setCards(cards);
                this.childEffect.use(info);
            }
        });
    }

    @Override
    public String getSubText()
    {
        return TEXT.actions.scry(getAmountRawString());
    }
}
