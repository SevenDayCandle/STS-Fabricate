package pinacolada.skills.skills.special.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.Hidden;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

public class PMove_PermanentUpgrade extends PMove<PField_CardCategory> implements Hidden
{
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_PermanentUpgrade.class, PField_CardCategory.class)
            .selfTarget();

    public PMove_PermanentUpgrade()
    {
        this(1);
    }

    public PMove_PermanentUpgrade(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_PermanentUpgrade(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.subjects.permanentlyX(TEXT.actions.upgrade("X"));
    }

    @Override
    public void use(PCLUseInfo info)
    {
        for (int i = 0; i < amount; i++)
        {
            getActions().modifyAllInstances(sourceCard.uuid, AbstractCard::upgrade)
                    .includeMasterDeck(true)
                    .isCancellable(false);
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        return TEXT.subjects.permanentlyX(TEXT.actions.upgrade(TEXT.subjects.thisObj));
    }
}
