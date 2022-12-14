package pinacolada.skills.skills.special.moves;

import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.Hidden;
import pinacolada.misc.CombatStats;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

// TODO Different text for different classes
public class PMove_GainMorph extends PMove implements Hidden
{
    public static final PSkillData DATA = register(PMove_GainMorph.class, PCLEffectType.General)
            .pclOnly()
            .selfTarget();

    public PMove_GainMorph()
    {
        this(1);
    }

    public PMove_GainMorph(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_GainMorph(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.gainAmount("X", PGR.core.tooltips.reroll.title);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        CombatStats.playerSystem.addSkip(amount);
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        return TEXT.actions.gainAmount(getAmountRawString(), plural(PGR.getResources(GameUtilities.getActingCardColor(sourceCard)).tooltips.getRerollTooltip()));
    }
}
