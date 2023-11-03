package pinacolada.skills.skills.base.modifiers;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import org.apache.commons.lang3.StringUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.fields.PField_Random;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

@VisibleSkill
public class PMod_PerCreatureHPPercent extends PMod_Per<PField_Random> {
    public static final PSkillData<PField_Random> DATA = register(PMod_PerCreatureHPPercent.class, PField_Random.class);

    public PMod_PerCreatureHPPercent(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerCreatureHPPercent() {
        super(DATA);
    }

    public PMod_PerCreatureHPPercent(int amount) {
        super(DATA, PCLCardTarget.Self, amount);
    }

    public PMod_PerCreatureHPPercent(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return fields.random ?
                sumTargets(info, t -> MathUtils.ceil((Math.max(0, t.maxHealth - t.currentHealth + TempHPField.tempHp.get(t))) * 100f / t.maxHealth)) :
                sumTargets(info, t -> MathUtils.ceil((t.currentHealth + TempHPField.tempHp.get(t)) * 100f / t.maxHealth));
    }

    @Override
    public String getSubSampleText() {
        return PGR.core.tooltips.hp.title + "%";
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
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
