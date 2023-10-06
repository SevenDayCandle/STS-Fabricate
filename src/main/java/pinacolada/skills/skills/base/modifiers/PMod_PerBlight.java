package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Blight;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

@VisibleSkill
public class PMod_PerBlight extends PMod_Per<PField_Blight> {
    public static final PSkillData<PField_Blight> DATA = register(PMod_PerBlight.class, PField_Blight.class).noTarget();

    public PMod_PerBlight(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerBlight() {
        super(DATA);
    }

    public PMod_PerBlight(int amount, String... blight) {
        super(DATA, amount);
        fields.setBlightID(blight);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return (EUIUtils.count(AbstractDungeon.player.blights, r -> fields.getFullBlightFilter().invoke(r)));
    }

    @Override
    public String getSubSampleText() {
        return TEXT.subjects_blight;
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return this.amount <= 1 ? fields.getFullBlightStringSingular() : fields.getFullBlightString();
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
    }
}
