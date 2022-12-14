package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PMove_Cycle extends PMove
{
    public static final PSkillData DATA = register(PMove_Cycle.class, PCLEffectType.General).selfTarget();

    public PMove_Cycle()
    {
        this(1);
    }

    public PMove_Cycle(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_Cycle(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.cycle("X");
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (useParent && !EUIUtils.isNullOrEmpty(cards))
        {
            CardGroup cg = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            cg.group = cards;
            getActions().discardFromPile(getName(), cg.size(), cg)
                    .setOptions(false, true)
                    .addCallback(cards -> {
                        getActions().draw(cards.size());
                        if (this.childEffect != null)
                        {
                            this.childEffect.setCards(cards);
                        }
                        super.use(info);
                    });
        }
        else
        {
            getActions().cycle(getName(), amount).setOptions(false, true)
                    .addCallback(cards -> {
                        if (this.childEffect != null)
                        {
                            this.childEffect.setCards(cards);
                        }
                        super.use(info);
                    });

        }

    }

    @Override
    public String getSubText()
    {
        return TEXT.actions.cycle(getAmountRawString());
    }
}
