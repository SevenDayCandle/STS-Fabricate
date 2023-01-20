package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.powers.common.StolenGoldPower;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
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
        return TEXT.actions.stealAmount(TEXT.subjects.x, PGR.core.tooltips.gold.title);
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
        return TEXT.actions.stealFrom(getAmountRawString(), PGR.core.tooltips.gold, getTargetString());
    }
}
