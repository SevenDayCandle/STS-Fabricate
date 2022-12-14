package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import java.util.ArrayList;

public class PMove_ReduceCooldown extends PMove_Modify
{
    public static final PSkillData DATA = PMove_Modify.register(PMove_ReduceCooldown.class, PCLEffectType.CardGroup);

    public PMove_ReduceCooldown()
    {
        this(1, 1, new ArrayList<>());
    }

    public PMove_ReduceCooldown(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_ReduceCooldown(int amount, int cooldown, ArrayList<AbstractCard> cards)
    {
        super(DATA, amount, cooldown, cards);
    }

    public PMove_ReduceCooldown(int amount, int cooldown, PCLCardGroupHelper... groups)
    {
        super(DATA, amount, cooldown, groups);
    }

    @Override
    public ActionT1<AbstractCard> getAction()
    {
        return (c) -> getActions().progressCooldown(c, extra);
    }

    @Override
    public String getObjectSampleText()
    {
        return TEXT.subjects.damage;
    }

    @Override
    public String getObjectText()
    {
        return PGR.core.tooltips.cooldown.title;
    }

    @Override
    public String getSubText()
    {
        return useParent || (cards != null && !cards.isEmpty()) ? TEXT.actions.reduceBy(TEXT.subjects.theirX(getObjectText()), getExtraRawString()) :
                groupTypes != null && !groupTypes.isEmpty() ?
                        TEXT.actions.reduceCooldown(EUIRM.strings.numNoun(getAmountRawString(), pluralCard()), getExtraRawString()) :
                        TEXT.actions.reduceBy(getObjectText(), getExtraRawString());
    }

    @Override
    public String wrapExtra(int input)
    {
        return String.valueOf(input);
    }

    @Override
    public boolean isDetrimental()
    {
        return extra < 0;
    }
}
