package pinacolada.skills.skills.special.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

public class PMove_PermanentUpgrade extends PMove<PField_CardCategory>
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
    public String getSampleText(PSkill<?> callingSkill)
    {
        return TEXT.subjects_permanentlyX(TEXT.act_upgrade(TEXT.subjects_x));
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
        return TEXT.subjects_permanentlyX(TEXT.act_upgrade(TEXT.subjects_thisCard));
    }
}
