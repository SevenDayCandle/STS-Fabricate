package pinacolada.interfaces.markers;

import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.skills.PSkill;

// Custom cards do not have inherent cardStrings so checks on those (e.g. FlavorText) should be skipped
public interface FabricateItem extends PointerProvider {
    EditorMaker getDynamicData();

    default void putCustomDesc(PSkill<?> skill, int index) {
        if (skill != null) {
            String[] desc = getDynamicData().getDescString();
            if (desc != null && desc.length > index) {
                String res = desc[index];
                if (res != null) {
                    skill.overrideDesc = res;
                }
            }
        }
    }

    default void setupMoves(EditorMaker<?,?> builder) {
        clearSkills();
        int exDescInd = -1;
        for (PSkill<?> effect : builder.getMoves()) {
            exDescInd += 1;
            if (PSkill.isSkillBlank(effect)) {
                continue;
            }
            PSkill<?> eff = effect.makeCopy();
            putCustomDesc(eff, exDescInd);
            addUseMove(eff);
        }

        for (PSkill<?> pe : builder.getPowers()) {
            exDescInd += 1;
            if (PSkill.isSkillBlank(pe)) {
                continue;
            }
            PSkill<?> pec = pe.makeCopy();
            putCustomDesc(pec, exDescInd);
            addPowerMove(pec);
        }
    }
}
