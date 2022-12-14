package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

import static pinacolada.skills.PSkill.PCLEffectType.Orb;

public class PMod_BonusPerOrb extends PMod_BonusPer
{

    public static final PSkillData DATA = register(PMod_BonusPerOrb.class, Orb).selfTarget();

    public PMod_BonusPerOrb(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_BonusPerOrb()
    {
        this(0, (PCLOrbHelper) null);
    }

    public PMod_BonusPerOrb(int amount, PCLOrbHelper... orbs)
    {
        super(DATA, amount, orbs);
    }

    @Override
    public String getConditionSampleText()
    {
        return PGR.core.tooltips.orb.title;
    }

    @Override
    public String getConditionText()
    {
        return getOrbAndString(getRawString(EFFECT_CHAR));
    }

    @Override
    public int multiplier(PCLUseInfo info)
    {
        return (orbs.isEmpty() && AbstractDungeon.player != null ? AbstractDungeon.player.filledOrbCount() : EUIUtils.sumInt(orbs, GameUtilities::getOrbCount));
    }
}
