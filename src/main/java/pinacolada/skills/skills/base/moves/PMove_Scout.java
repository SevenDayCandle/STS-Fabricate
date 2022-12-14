package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PMove_Scout extends PMove
{
    public static final PSkillData DATA = register(PMove_Scout.class, PCLEffectType.General).selfTarget();

    public PMove_Scout()
    {
        this(1);
    }

    public PMove_Scout(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_Scout(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.scout("X");
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (useParent && !EUIUtils.isNullOrEmpty(cards))
        {
            CardGroup cg = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            cg.group = cards;
            getActions().fetchFromPile(getName(), cg.size(), cg)
                    .setOptions(true, false)
                    .addCallback(cards -> {
                        getActions().reshuffleFromPile(getName(), cards.size(), AbstractDungeon.player.hand);
                        if (this.childEffect != null)
                        {
                            this.childEffect.setCards(cards);
                        }
                        super.use(info);
                    });
        }
        else
        {
            getActions().scout(getName(), amount).setOptions(false, true)
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
        return TEXT.actions.scout(getAmountRawString());
    }
}
