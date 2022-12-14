package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.CardSelection;

public class PMove_PlayTop extends PMove
{
    public static final PSkillData DATA = register(PMove_PlayTop.class, PCLEffectType.CardGroupFull);

    public PMove_PlayTop()
    {
        this(1, (PCLCardGroupHelper) null);
    }

    public PMove_PlayTop(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_PlayTop(int amount, PCLCardGroupHelper... h)
    {
        super(DATA, PCLCardTarget.None, amount, h);
    }

    public PMove_PlayTop(int amount, PCLCardTarget target, PCLCardGroupHelper... h)
    {
        super(DATA, target, amount, h);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.playFrom("X", "Y", "Z");
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().playFromPile(getName(), amount, EUIUtils.safeCast(info.target, AbstractMonster.class), getCardGroup())
                .setFilter(getFullCardFilter())
                .setOptions(alt ? CardSelection.Bottom : CardSelection.Top, true);
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        return !groupTypes.isEmpty() ? TEXT.actions.playFrom(getAmountRawString(), getFullCardString(), alt ? TEXT.subjects.bottomOf(getGroupString()) : TEXT.subjects.topOf(getGroupString())) : TEXT.actions.play(getFullCardString());
    }
}
