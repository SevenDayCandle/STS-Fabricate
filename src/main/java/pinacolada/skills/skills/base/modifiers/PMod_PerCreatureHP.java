package pinacolada.skills.skills.base.modifiers;

import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import org.apache.commons.lang3.StringUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Random;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

@VisibleSkill
public class PMod_PerCreatureHP extends PMod_Per<PField_Random> {
    public static final PSkillData<PField_Random> DATA = register(PMod_PerCreatureHP.class, PField_Random.class);

    public PMod_PerCreatureHP(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerCreatureHP() {
        super(DATA);
    }

    public PMod_PerCreatureHP(int amount) {
        super(DATA, PCLCardTarget.Self, amount);
    }

    public PMod_PerCreatureHP(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return fields.random ?
                sumTargets(info, t -> Math.max(0, t.maxHealth - (t.currentHealth + TempHPField.tempHp.get(t)))) :
                sumTargets(info, t -> t.currentHealth + TempHPField.tempHp.get(t));
    }

    @Override
    public String getSubSampleText() {
        return PGR.core.tooltips.hp.title;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (fields.random) {
            return TEXT.subjects_missingX(getSubSampleText(), getTargetStringPerspective(perspective));
        }
        return getTargetOnStringPerspective(perspective, getSubSampleText());
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        fields.registerNotBoolean(editor, StringUtils.capitalize(TEXT.subjects_bonus), TEXT.cetut_bonus);
        fields.registerRBoolean(editor, TEXT.cedit_invert, null);
    }
}
