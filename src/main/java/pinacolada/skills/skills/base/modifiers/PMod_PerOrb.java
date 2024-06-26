package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.orbs.PCLOrbData;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMod_PerOrb extends PMod_Per<PField_Orb> {
    public static final PSkillData<PField_Orb> DATA = register(PMod_PerOrb.class, PField_Orb.class).noTarget();

    public PMod_PerOrb(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerOrb() {
        super(DATA);
    }

    public PMod_PerOrb(int amount, PCLOrbData... orbs) {
        super(DATA, amount);
        fields.setOrb(orbs);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return (fields.orbs.isEmpty() ? GameUtilities.getOrbCount() : EUIUtils.sumInt(fields.orbs, GameUtilities::getOrbCount));
    }

    @Override
    public String getSubSampleText() {
        return PGR.core.tooltips.orb.title;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return this.amount <= 1 ? fields.getOrbAndStringSingular() : fields.getOrbAndString(requestor);
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
    }
}
