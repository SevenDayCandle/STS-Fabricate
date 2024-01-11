package pinacolada.skills.skills.base.modifiers;

import com.badlogic.gdx.math.MathUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PMathMod;

@VisibleSkill
public class PMod_SinX extends PMathMod {

    public static final PSkillData<PField_Empty> DATA = register(PMod_SinX.class, PField_Empty.class).noTarget();

    public PMod_SinX(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_SinX() {
        super(DATA);
    }

    public PMod_SinX(int amount) {
        super(DATA, amount);
    }

    @Override
    public int getModifiedAmount(PCLUseInfo info, int baseAmount, boolean isUsing) {
        if (isUsing) {
            return amount * (int) MathUtils.sin(baseAmount * MathUtils.PI / 2);
        }
        return baseAmount;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return PGR.core.strings.subjects_sin(PGR.core.strings.subjects_x);
    }

    @Override
    public String wrapTextAmountChild(String input) {
        return amount + (parent != null ? parent.wrapTextAmountChild(PGR.core.strings.subjects_sin(input)) : super.wrapTextAmountChild(PGR.core.strings.subjects_sin(input)));
    }
}
