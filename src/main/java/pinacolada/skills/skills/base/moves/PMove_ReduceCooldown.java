package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

public class PMove_ReduceCooldown extends PMove_Modify<PField_CardCategory>
{
    public static final PSkillData<PField_CardCategory> DATA = PMove_Modify.register(PMove_ReduceCooldown.class, PField_CardCategory.class);

    public PMove_ReduceCooldown()
    {
        this(1, 1);
    }

    public PMove_ReduceCooldown(PSkillSaveData content)
    {
        super(content);
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
        return useParent ? TEXT.actions.reduceBy(TEXT.subjects.theirX(getObjectText()), getExtraRawString()) :
                fields.hasGroups() ?
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
