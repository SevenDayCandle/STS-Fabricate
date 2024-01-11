package pinacolada.skills.skills.base.modifiers;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.random.Random;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PMathMod;

@VisibleSkill
public class PMod_PowX extends PMathMod {

    public static final PSkillData<PField_Empty> DATA = register(PMod_PowX.class, PField_Empty.class).noTarget();

    public PMod_PowX(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PowX() {
        super(DATA);
    }

    public PMod_PowX(int amount) {
        super(DATA, amount);
    }

    @Override
    public int getModifiedAmount(PCLUseInfo info, int baseAmount, boolean isUsing) {
        if (isUsing) {
            return (int) Math.pow(baseAmount, amount);
        }
        return baseAmount;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return PGR.core.strings.subjects_pow(PGR.core.strings.subjects_x);
    }

    @Override
    public String wrapTextAmountChild(String input) {
        String res = input + "^" + this.amount;
        return parent != null ? parent.wrapTextAmountChild(res) : super.wrapTextAmountChild(res);
    }
}
