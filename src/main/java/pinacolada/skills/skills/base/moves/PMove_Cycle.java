package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

import java.util.ArrayList;

public class PMove_Cycle extends PMove<PField_CardCategory>
{
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_Cycle.class, PField_CardCategory.class).selfTarget();

    public PMove_Cycle()
    {
        this(1);
    }

    public PMove_Cycle(PSkillSaveData content)
    {
        super(DATA, content);
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
        ArrayList<AbstractCard> cards = info.getData(null);
        if (useParent && !EUIUtils.isNullOrEmpty(cards))
        {
            CardGroup cg = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            cg.group = cards;
            getActions().discardFromPile(getName(), cg.size(), cg)
                    .setOptions(false, true)
                    .addCallback(c2 -> {
                        getActions().draw(c2.size());
                        info.setData(c2);
                        super.use(info);
                    });
        }
        else
        {
            getActions().cycle(getName(), amount).setOptions(false, true)
                    .addCallback(c2 -> {
                        info.setData(c2);
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
