package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PMove_ModifyDamage extends PMove_Modify<PField_CardCategory>
{
    public static final PSkillData<PField_CardCategory> DATA = PMove_Modify.register(PMove_ModifyDamage.class, PField_CardCategory.class);

    public PMove_ModifyDamage()
    {
        this(1, 1);
    }

    public PMove_ModifyDamage(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_ModifyDamage(int amount, int damage, PCLCardGroupHelper... groups)
    {
        super(DATA, amount, damage, groups);
    }

    @Override
    public ActionT1<AbstractCard> getAction()
    {
        return (c) -> getActions().modifyDamage(c, extra, true, true);
    }

    @Override
    public String getObjectSampleText()
    {
        return TEXT.subjects_damage;
    }

    @Override
    public String getObjectText()
    {
        return EUIRM.strings.numNoun(getExtraRawString(), TEXT.subjects_damage);
    }

    @Override
    public boolean isDetrimental()
    {
        return extra < 0;
    }
}
