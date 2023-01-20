package pinacolada.skills.skills.special.moves;

import pinacolada.cards.base.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.interfaces.markers.Hidden;
import pinacolada.misc.CombatManager;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.utilities.GameUtilities;

// TODO Different text for different classes
public class PMove_GainMorph extends PMove<PField_Empty> implements Hidden
{
    public static final PSkillData<PField_Empty> DATA = register(PMove_GainMorph.class, PField_Empty.class)
            .pclOnly()
            .selfTarget();

    public PMove_GainMorph()
    {
        this(1);
    }

    public PMove_GainMorph(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_GainMorph(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.gainAmount(TEXT.subjects.x, PGR.core.tooltips.reroll.title);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        CombatManager.playerSystem.addSkip(amount);
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        return TEXT.actions.gainAmount(getAmountRawString(), plural(PGR.getResources(GameUtilities.getActingCardColor(sourceCard)).tooltips.getRerollTooltip()));
    }
}
