package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;

@VisibleSkill
public class PMod_PerBlock extends PMod_Per<PField_Not> {

    public static final PSkillData<PField_Not> DATA = register(PMod_PerBlock.class, PField_Not.class).selfTarget();

    public PMod_PerBlock() {
        this(1);
    }

    public PMod_PerBlock(int amount) {
        super(DATA, amount);
    }

    public PMod_PerBlock(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return (sourceCard != null ? sourceCard.block / PGR.dungeon.getDivisor() : 0);
    }

    @Override
    public String getSubSampleText() {
        return EUIRM.strings.adjNoun(TEXT.subjects_card, PGR.core.tooltips.block.title);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return PGR.core.tooltips.block.title;
    }
}
