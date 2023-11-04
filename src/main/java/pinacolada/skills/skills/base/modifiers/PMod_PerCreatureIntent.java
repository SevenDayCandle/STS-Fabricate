package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Intent;

@VisibleSkill
public class PMod_PerCreatureIntent extends PMod_Per<PField_Intent> {
    public static final PSkillData<PField_Intent> DATA = register(PMod_PerCreatureIntent.class, PField_Intent.class);

    public PMod_PerCreatureIntent(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerCreatureIntent() {
        super(DATA);
    }

    public PMod_PerCreatureIntent(int amount) {
        super(DATA, PCLCardTarget.AllEnemy, amount);
    }

    public PMod_PerCreatureIntent(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return EUIUtils.count(getTargetList(info), t -> fields.hasIntent(t));
    }

    @Override
    public String getSubSampleText() {
        return EUIRM.strings.adjNoun(TEXT.subjects_intent, TEXT.subjects_character);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return EUIRM.strings.adjNoun(fields.getAnyIntentString(), getTargetStringSingular());
    }
}
