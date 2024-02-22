package pinacolada.skills.skills.base.traits;

import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PStatTrait;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

@VisibleSkill
public class PTrait_MinBlock extends PStatTrait<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PTrait_MinBlock.class, PField_Not.class)
            .setSourceTypes(PSkillData.SourceType.Card, PSkillData.SourceType.Power);

    public PTrait_MinBlock() {
        this(1);
    }

    public PTrait_MinBlock(int amount) {
        super(DATA, amount);
    }

    public PTrait_MinBlock(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return getSubSampleText();
    }

    @Override
    public String getSubDescText(PCLCardTarget perspective) {
        return TEXT.subjects_min(EUIRM.strings.numNoun(getAmountRawString(), PGR.core.tooltips.block));
    }

    @Override
    public String getSubSampleText() {
        return TEXT.subjects_min(PGR.core.tooltips.block.title);
    }

    @Override
    public float modifyBlockLast(PCLUseInfo info, float amount) {
        int actualAmount = refreshAmount(info);
        return Math.max(amount, actualAmount);
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        fields.setupEditor(editor);
    }

    @Override
    public String wrapTextAmountSelf(int input) {
        return String.valueOf(input);
    }
}
