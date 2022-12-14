package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

import java.util.List;

import static pinacolada.skills.PSkill.PCLEffectType.Orb;

public class PMod_PerOrb extends PMod
{

    public static final PSkillData DATA = register(PMod_PerOrb.class, Orb).selfTarget();

    public PMod_PerOrb(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_PerOrb()
    {
        super(DATA);
    }

    public PMod_PerOrb(int amount, PCLOrbHelper... orbs)
    {
        super(DATA, PCLCardTarget.None, amount, orbs);
    }

    public PMod_PerOrb(int amount, List<PCLOrbHelper> orbs)
    {
        super(DATA, PCLCardTarget.None, amount, orbs.toArray(new PCLOrbHelper[]{}));
    }

    @Override
    public int getModifiedAmount(PSkill be, PCLUseInfo info)
    {
        return be.baseAmount * (orbs.isEmpty() ? GameUtilities.getOrbCount() : EUIUtils.sumInt(orbs, GameUtilities::getOrbCount)) / Math.max(1, this.amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.per("X", TEXT.cardEditor.orbs);
    }

    @Override
    public String getSubText()
    {
        return this.amount <= 1 ? getOrbAndString(1) : EUIRM.strings.numNoun(getAmountRawString(), getOrbAndString(getRawString(EFFECT_CHAR)));
    }
}
