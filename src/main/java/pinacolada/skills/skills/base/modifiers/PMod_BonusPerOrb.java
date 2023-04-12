package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMod_BonusPerOrb extends PMod_BonusPer<PField_Orb>
{

    public static final PSkillData<PField_Orb> DATA = register(PMod_BonusPerOrb.class, PField_Orb.class).selfTarget();

    public PMod_BonusPerOrb(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_BonusPerOrb()
    {
        this(0);
    }

    public PMod_BonusPerOrb(int amount, PCLOrbHelper... orbs)
    {
        super(DATA, amount);
        fields.setOrb(orbs);
    }

    @Override
    public String getSubText()
    {
        return PGR.core.tooltips.orb.title;
    }

    @Override
    public String getConditionText()
    {
        return fields.getOrbAndString();
    }

    @Override
    public int getMultiplier(PCLUseInfo info)
    {
        return (fields.orbs.isEmpty() && AbstractDungeon.player != null ? AbstractDungeon.player.filledOrbCount() : EUIUtils.sumInt(fields.orbs, GameUtilities::getOrbCount));
    }
}
