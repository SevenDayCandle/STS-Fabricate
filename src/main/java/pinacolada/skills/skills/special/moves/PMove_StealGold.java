package pinacolada.skills.skills.special.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.powers.common.StolenGoldPower;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.utilities.GameUtilities;

public class PMove_StealGold extends PMove<PField_Empty>
{
    public static final PSkillData<PField_Empty> DATA = register(PMove_StealGold.class, PField_Empty.class);

    public PMove_StealGold()
    {
        this(PCLCardTarget.Single, 1);
    }

    public PMove_StealGold(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_StealGold(PCLCardTarget target, int amount)
    {
        super(DATA, target, amount);
    }

    @Override
    public PMove_StealGold onAddToCard(AbstractCard card)
    {
        super.onAddToCard(card);
        if (card.tags.contains(AbstractCard.CardTags.HEALING))
        {
            card.tags.add(AbstractCard.CardTags.HEALING);
        }
        return this;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.act_stealAmount(TEXT.subjects_x, PGR.core.tooltips.gold.title);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        AbstractCreature m = info.target;
        if (m == null)
        {
            m = GameUtilities.getRandomEnemy(true);
        }
        if (m instanceof AbstractMonster)
        {
            getActions().applyPower(info.source, new StolenGoldPower(m, amount));
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        return TEXT.act_stealFrom(getAmountRawString(), PGR.core.tooltips.gold, getTargetString());
    }
}
