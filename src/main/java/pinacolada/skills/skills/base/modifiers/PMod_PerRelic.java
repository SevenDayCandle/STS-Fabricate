package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Relic;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

@VisibleSkill
public class PMod_PerRelic extends PMod_Per<PField_Relic> {
    public static final PSkillData<PField_Relic> DATA = register(PMod_PerRelic.class, PField_Relic.class).noTarget();

    public PMod_PerRelic(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerRelic() {
        super(DATA);
    }

    public PMod_PerRelic(int amount, String... relic) {
        super(DATA, amount);
        fields.setRelicID(relic);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return (EUIUtils.count(AbstractDungeon.player.relics, r -> fields.getFullRelicFilter().invoke(r)));
    }

    @Override
    public String getSubSampleText() {
        return TEXT.subjects_relic;
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return this.amount <= 1 ? fields.getFullRelicStringSingular() : fields.getFullRelicString();
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
    }
}
