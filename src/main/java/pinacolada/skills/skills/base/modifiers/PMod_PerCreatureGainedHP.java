package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIRM;
import org.apache.commons.lang3.StringUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Random;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

@VisibleSkill
public class PMod_PerCreatureGainedHP extends PMod_Per<PField_Random> {
    public static final PSkillData<PField_Random> DATA = register(PMod_PerCreatureGainedHP.class, PField_Random.class);

    public PMod_PerCreatureGainedHP(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerCreatureGainedHP() {
        super(DATA);
    }

    public PMod_PerCreatureGainedHP(int amount) {
        super(DATA, PCLCardTarget.Self, amount);
    }

    public PMod_PerCreatureGainedHP(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return fields.random ?
                sumTargets(info, CombatManager::hpGainedThisCombat) :
                sumTargets(info, CombatManager::hpGainedThisTurn);
    }

    @Override
    public String getSubSampleText() {
        return PGR.core.strings.act_heal(PGR.core.tooltips.hp.title);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String base = TEXT.subjects_onTarget(EUIRM.strings.nounVerb(PGR.core.tooltips.hp.title, PGR.core.tooltips.heal.past()), getTargetStringPerspective(perspective));
        return fields.random ? TEXT.subjects_thisCombat(base) : TEXT.subjects_thisTurn(base);
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        fields.registerNotBoolean(editor, StringUtils.capitalize(TEXT.subjects_bonus), TEXT.cetut_bonus);
        fields.registerRBoolean(editor, TEXT.cedit_combat, null);
    }
}
