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
import pinacolada.skills.fields.PField_Orb;
import pinacolada.utilities.GameUtilities;

public class PMod_PerOrb extends PMod<PField_Orb>
{

    public static final PSkillData<PField_Orb> DATA = register(PMod_PerOrb.class, PField_Orb.class).selfTarget();

    public PMod_PerOrb(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_PerOrb()
    {
        super(DATA);
    }

    public PMod_PerOrb(int amount, PCLOrbHelper... orbs)
    {
        super(DATA, PCLCardTarget.None, amount);
        fields.setOrb(orbs);
    }

    @Override
    public int getModifiedAmount(PSkill be, PCLUseInfo info)
    {
        return be.baseAmount * (fields.orbs.isEmpty() ? GameUtilities.getOrbCount() : EUIUtils.sumInt(fields.orbs, GameUtilities::getOrbCount)) / Math.max(1, this.amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.per("X", TEXT.cardEditor.orbs);
    }

    @Override
    public String getSubText()
    {
        return this.amount <= 1 ? fields.getOrbAndString(1) : EUIRM.strings.numNoun(getAmountRawString(), fields.getOrbAndString());
    }
}
