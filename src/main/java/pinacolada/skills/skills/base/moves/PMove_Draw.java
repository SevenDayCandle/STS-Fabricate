package pinacolada.skills.skills.base.moves;

import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PMove_Draw extends PMove
{
    public static final PSkillData DATA = register(PMove_Draw.class, PCLEffectType.CardGroup)
            .selfTarget()
            .setGroups(PCLCardGroupHelper.DrawPile, PCLCardGroupHelper.DiscardPile, PCLCardGroupHelper.ExhaustPile);

    public PMove_Draw()
    {
        this(1);
    }

    public PMove_Draw(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_Draw(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.draw("X");
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().draw(amount)
                .setFilter(getFullCardFilter(), true)
                .addCallback(ca -> {
                    if (this.childEffect != null)
                    {
                        this.childEffect.receivePayload(ca);
                        this.childEffect.use(info);
                    }
                });
    }

    @Override
    public String getSubText()
    {
        return TEXT.actions.drawType(getAmountRawString(), getFullCardString());
    }
}
