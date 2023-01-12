package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.utilities.GameUtilities;

public class PMove_EvokeOrb extends PMove<PField_Orb>
{
    public static final PSkillData<PField_Orb> DATA = register(PMove_EvokeOrb.class, PField_Orb.class)
            .setExtra(0, Integer.MAX_VALUE)
            .selfTarget();

    public PMove_EvokeOrb()
    {
        this(1, 1);
    }

    public PMove_EvokeOrb(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_EvokeOrb(int amount, PCLOrbHelper... orb)
    {
        this(amount, 1, orb);
    }

    public PMove_EvokeOrb(int amount, int orbs, PCLOrbHelper... orb)
    {
        super(DATA, PCLCardTarget.None, amount, orbs);
        fields.setOrb(orb);
    }

    @Override
    public PMove_EvokeOrb onAddToCard(AbstractCard card)
    {
        super.onAddToCard(card);
        card.showEvokeValue = amount > 0;
        card.showEvokeOrbCount = amount;
        return this;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.evoke("X");
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().evokeOrb(amount, extra <= 0 ? GameUtilities.getOrbCount() : extra, fields.random).setFilter(fields.orbs.isEmpty() ? null : fields.getOrbFilter());
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        String orbStr = fields.getOrbExtraString();
        return amount == 1 ? TEXT.actions.evoke(orbStr) : TEXT.actions.evokeXTimes(orbStr, getAmountRawString());
    }
}
