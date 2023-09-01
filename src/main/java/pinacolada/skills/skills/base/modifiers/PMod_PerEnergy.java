package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;

@VisibleSkill
public class PMod_PerEnergy extends PMod_Per<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PMod_PerEnergy.class, PField_Not.class).noTarget();

    public PMod_PerEnergy(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerEnergy() {
        super(DATA);
    }

    public PMod_PerEnergy(int amount) {
        super(DATA, amount);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return EnergyPanel.getCurrentEnergy();
    }

    @Override
    public String getSubSampleText() {
        return PGR.core.tooltips.energy.title;
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return PGR.core.tooltips.energy.getTitleOrIcon();
    }
}
