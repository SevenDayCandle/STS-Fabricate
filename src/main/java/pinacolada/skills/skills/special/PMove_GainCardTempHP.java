package pinacolada.skills.skills.special;

import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.Hidden;
import pinacolada.interfaces.markers.PointerProvider;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;

public class PMove_GainCardTempHP extends PMove implements Hidden
{
    public static final PSkillData DATA = register(PMove_GainCardTempHP.class, PCLEffectType.General);


    public PMove_GainCardTempHP(PointerProvider card)
    {
        super(DATA, PCLCardTarget.Self, 0);
        setSource(card, PCLCardValueSource.MagicNumber);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.gainAmount("X", PGR.core.tooltips.tempHP.title);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().gainTemporaryHP(amount);
    }

    @Override
    public String getSubText()
    {
        return null;
    }
}
