package pinacolada.skills.skills.base.conditions;

import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

public class PCond_PileHas extends PCond<PField_CardCategory>
{

    public static final PSkillData<PField_CardCategory> DATA = register(PCond_PileHas.class, PField_CardCategory.class)
            .selfTarget();

    public PCond_PileHas()
    {
        this(1);
    }

    public PCond_PileHas(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_PileHas(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    public PCond_PileHas(int amount, PCLCardGroupHelper... groups)
    {
        super(DATA, PCLCardTarget.None, amount);
        fields.setCardGroup(groups);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        int count = EUIUtils.sumInt(fields.groupTypes, g -> EUIUtils.count(g.getCards(),
                c -> fields.getFullCardFilter().invoke(c)));
        return amount == 0 ? count == 0 : fields.random ^ count >= amount;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.ifYouHave(TEXT.subjects.card);
    }

    @Override
    public String getSubText()
    {
        return TEXT.conditions.ifTargetHas(fields.getGroupString(),
                EUIRM.strings.numNoun(getAmountRawString(), fields.getFullCardString()));
    }

    @Override
    public String wrapAmount(int input)
    {
        return input == 0 ? String.valueOf(input) : (fields.random ? (input + "-") : (input + "+"));
    }
}
