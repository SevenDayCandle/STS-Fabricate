package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMod_PerCreatureBuffing extends PMod_Per<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PMod_PerCreatureBuffing.class, PField_Not.class);

    public PMod_PerCreatureBuffing(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerCreatureBuffing() {
        super(DATA);
    }

    public PMod_PerCreatureBuffing(int amount) {
        super(DATA, PCLCardTarget.AllEnemy, amount);
    }

    public PMod_PerCreatureBuffing(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public String getSubSampleText() {
        return EUIRM.strings.adjNoun(PGR.core.tooltips.buff.progressive(), TEXT.subjects_character);
    }

    @Override
    public int getMultiplier(PCLUseInfo info) {
        return EUIUtils.count(getTargetList(info), GameUtilities::isBuffing);
    }

    @Override
    public String getSubText() {
        return EUIRM.strings.adjNoun(PGR.core.tooltips.buff.progressive(), target == PCLCardTarget.Any ? TEXT.subjects_character : TEXT.subjects_enemy);
    }
}
