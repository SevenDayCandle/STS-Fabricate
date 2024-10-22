package pinacolada.skills.skills.base.modifiers;

import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMod_PerUpgrade extends PMod_Per<PField_Not> {

    public static final PSkillData<PField_Not> DATA = register(PMod_PerUpgrade.class, PField_Not.class).noTarget();

    public PMod_PerUpgrade() {
        this(1);
    }

    public PMod_PerUpgrade(int amount) {
        super(DATA, amount);
    }

    public PMod_PerUpgrade(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return getUpgradeLevel();
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return PGR.core.tooltips.upgrade.title;
    }
}
