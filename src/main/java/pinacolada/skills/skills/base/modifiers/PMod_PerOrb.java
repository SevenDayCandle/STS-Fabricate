package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.ui.cardEditor.PCLCustomEffectEditor;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMod_PerOrb extends PMod_Per<PField_Orb> {
    public static final PSkillData<PField_Orb> DATA = register(PMod_PerOrb.class, PField_Orb.class).selfTarget();

    public PMod_PerOrb(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerOrb() {
        super(DATA);
    }

    public PMod_PerOrb(int amount, PCLOrbHelper... orbs) {
        super(DATA, amount);
        fields.setOrb(orbs);
    }

    @Override
    public int getMultiplier(PCLUseInfo info) {
        return (fields.orbs.isEmpty() ? GameUtilities.getOrbCount() : EUIUtils.sumInt(fields.orbs, GameUtilities::getOrbCount));
    }

    @Override
    public String getSubSampleText() {
        return PGR.core.tooltips.orb.title;
    }

    @Override
    public void setupEditor(PCLCustomEffectEditor<?> editor) {
        super.setupEditor(editor);
    }

    @Override
    public String getSubText() {
        return this.amount <= 1 ? fields.getOrbAndString(1) : fields.getOrbAndString();
    }
}
