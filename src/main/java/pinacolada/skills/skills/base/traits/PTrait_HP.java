package pinacolada.skills.skills.base.traits;

import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.fields.PField_Not;

@VisibleSkill
public class PTrait_HP extends PTrait<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PTrait_HP.class, PField_Not.class)
            .setSourceTypes(PSkillData.SourceType.Card, PSkillData.SourceType.Power);

    public PTrait_HP() {
        this(1);
    }

    public PTrait_HP(int amount) {
        super(DATA, amount);
    }

    public PTrait_HP(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSubDescText(PCLCardTarget perspective) {
        return EUIRM.strings.numNoun(getAmountRawString(), PGR.core.tooltips.hp.title);
    }

    @Override
    public String getSubSampleText() {
        return PGR.core.tooltips.hp.title;
    }

    @Override
    public float modifyHeal(PCLUseInfo info, float amount) {
        int actualAmount = refreshAmount(info);
        return fields.not ? actualAmount : amount + actualAmount;
    }
}
