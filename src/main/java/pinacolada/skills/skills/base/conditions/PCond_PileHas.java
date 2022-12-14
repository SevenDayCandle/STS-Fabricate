package pinacolada.skills.skills.base.conditions;

import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PCond_PileHas extends PCond
{

    public static final PSkillData DATA = register(PCond_PileHas.class, PCLEffectType.CardGroupFull)
            .selfTarget();

    public PCond_PileHas()
    {
        this(1);
    }

    public PCond_PileHas(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_PileHas(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    public PCond_PileHas(int amount, PCLCardGroupHelper... groups)
    {
        super(DATA, PCLCardTarget.None, amount, groups);
    }

    public PCond_PileHas(PSkill effect)
    {
        this();
        setChild(effect);
    }

    public PCond_PileHas(PSkill... effect)
    {
        this();
        setChild(effect);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        int count = EUIUtils.sumInt(groupTypes, g -> EUIUtils.count(g.getCards(),
                c -> getFullCardFilter().invoke(c)));
        return amount == 0 ? count == 0 : alt ^ count >= amount;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.ifYouHave(TEXT.subjects.card);
    }

    @Override
    public String getSubText()
    {
        return TEXT.conditions.ifTargetHas(getGroupString(),
                EUIRM.strings.numNoun(getAmountRawString(), getFullCardString(getRawString(EFFECT_CHAR))));
    }

    @Override
    public String wrapAmount(int input)
    {
        return input == 0 ? String.valueOf(input) : (alt ? (input + "-") : (input + "+"));
    }
}
