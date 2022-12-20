package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PMove_Heal extends PMove
{
    public static final PSkillData DATA = register(PMove_Heal.class, PCLEffectType.General);

    public PMove_Heal()
    {
        this(1);
    }

    public PMove_Heal(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_Heal(int amount)
    {
        super(DATA, PCLCardTarget.Self, amount);
    }

    public PMove_Heal(PCLCardTarget target, int amount)
    {
        super(DATA, target, amount);
    }

    @Override
    public PMove_Heal onAddToCard(AbstractCard card)
    {
        super.onAddToCard(card);
        if (target.targetsSelf() && !card.tags.contains(AbstractCard.CardTags.HEALING))
        {
            card.tags.add(AbstractCard.CardTags.HEALING);
        }
        return this;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.heal("X");
    }

    @Override
    public void use(PCLUseInfo info)
    {
        for (AbstractCreature t : getTargetList(info))
        {
            getActions().heal(info.source, t, amount).isCancellable(t == AbstractDungeon.player);
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        if (target == PCLCardTarget.Self)
        {
            return TEXT.actions.heal(getAmountRawString());
        }
        return TEXT.actions.healOn(getAmountRawString(), getTargetString());
    }
}
