package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.utilities.GameUtilities;

public class PCond_EvokeOrb extends PCond<PField_Orb>
{
    public static final PSkillData<PField_Orb> DATA = register(PCond_EvokeOrb.class, PField_Orb.class)
            .selfTarget();

    public PCond_EvokeOrb(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_EvokeOrb()
    {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_EvokeOrb(int amount, PCLOrbHelper... orbs)
    {
        super(DATA, PCLCardTarget.None, amount);
        fields.setOrb(orbs);
    }

    @Override
    public PCond_EvokeOrb onAddToCard(AbstractCard card)
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
    public String getSubText()
    {
        Object tt = fields.getOrbAndOrString();
        if (isTrigger())
        {
            return TEXT.conditions.wheneverYou(TEXT.actions.evoke(tt));
        }
        return TEXT.actions.evoke(amount <= 1 ? TEXT.subjects.yourFirst(tt) : TEXT.subjects.yourFirst(EUIRM.strings.numNoun(getAmountRawString(), tt)));
    }

    @Override
    public boolean triggerOnOrbEvoke(AbstractOrb o)
    {
        if (this.childEffect != null && fields.getOrbFilter().invoke(o))
        {
            this.childEffect.use(makeInfo(null));
        }
        return true;
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        if ((fields.orbs.isEmpty() && GameUtilities.getOrbCount() < amount) || EUIUtils.any(fields.orbs, o -> GameUtilities.getOrbCount(o.ID) < amount))
        {
            return false;
        }
        if (isUsing)
        {
            getActions().evokeOrb(1, amount).setFilter(fields.getOrbFilter());
        }
        return true;
    }
}
