package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

import java.util.List;

public class PCond_EvokeOrb extends PCond
{
    public static final PSkillData DATA = register(PCond_EvokeOrb.class, PCLEffectType.Orb)
            .selfTarget();

    public PCond_EvokeOrb(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_EvokeOrb()
    {
        super(DATA, PCLCardTarget.None, 1, new PCLOrbHelper[]{});
    }

    public PCond_EvokeOrb(int amount, PCLOrbHelper... powers)
    {
        super(DATA, PCLCardTarget.None, amount, powers);
    }

    public PCond_EvokeOrb(int amount, List<PCLOrbHelper> powers)
    {
        super(DATA, PCLCardTarget.None, amount, powers.toArray(new PCLOrbHelper[]{}));
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
        Object tt = !orbs.isEmpty() ? getOrbString() : plural(PGR.core.tooltips.orb);
        if (isTrigger())
        {
            return TEXT.conditions.wheneverYou(TEXT.actions.evoke(tt));
        }
        return TEXT.actions.evoke(amount <= 1 ? TEXT.subjects.yourFirst(tt) : TEXT.subjects.yourFirst(EUIRM.strings.numNoun(getAmountRawString(), tt)));
    }

    @Override
    public boolean triggerOnOrbEvoke(AbstractOrb o)
    {
        if (this.childEffect != null && getOrbFilter().invoke(o))
        {
            this.childEffect.use(makeInfo(null));
        }
        return true;
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        if ((orbs.isEmpty() && GameUtilities.getOrbCount() < amount) || EUIUtils.any(orbs, o -> GameUtilities.getOrbCount(o.ID) < amount))
        {
            return false;
        }
        if (isUsing)
        {
            getActions().evokeOrb(1, amount).setFilter(getOrbFilter());
        }
        return true;
    }
}
