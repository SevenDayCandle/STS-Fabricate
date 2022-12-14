package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import java.util.ArrayList;

public class PMove_ModifyDamage extends PMove_Modify
{
    public static final PSkillData DATA = PMove_Modify.register(PMove_ModifyDamage.class, PCLEffectType.CardGroup);

    public PMove_ModifyDamage()
    {
        this(1, 1, new ArrayList<>());
    }

    public PMove_ModifyDamage(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_ModifyDamage(int amount, int damage)
    {
        super(DATA, amount, damage);
    }

    public PMove_ModifyDamage(int amount, int damage, ArrayList<AbstractCard> cards)
    {
        super(DATA, amount, damage, cards);
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
        return TEXT.subjects.damage;
    }

    @Override
    public String getObjectText()
    {
        return EUIRM.strings.numNoun(getExtraRawString(), TEXT.subjects.damage);
    }

    @Override
    public boolean isDetrimental()
    {
        return extra < 0;
    }
}
