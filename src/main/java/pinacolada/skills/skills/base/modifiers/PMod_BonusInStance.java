package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.stances.NeutralStance;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.stances.PCLStanceHelper;
import pinacolada.utilities.GameUtilities;

import static pinacolada.skills.PSkill.PCLEffectType.Stance;

public class PMod_BonusInStance extends PMod_BonusOn
{

    public static final PSkillData DATA = register(PMod_BonusInStance.class, Stance).selfTarget();

    public PMod_BonusInStance(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_BonusInStance()
    {
        this(0, null);
    }

    public PMod_BonusInStance(int amount, PCLStanceHelper stance)
    {
        super(DATA, amount, stance);
    }

    @Override
    public String getConditionSampleText()
    {
        return PGR.core.tooltips.stance.title;
    }

    @Override
    public String getSubText()
    {
        String base = stances.isEmpty() ? TEXT.conditions.any(PGR.core.tooltips.stance) : getStanceString();
        return TEXT.conditions.numIf(getAmountRawString(), alt ? TEXT.conditions.not(base) : base);
    }

    @Override
    public boolean meetsCondition(PCLUseInfo info)
    {
        return alt ^ (stances.isEmpty() ? !GameUtilities.inStance(NeutralStance.STANCE_ID) : EUIUtils.any(stances, GameUtilities::inStance));
    }
}
