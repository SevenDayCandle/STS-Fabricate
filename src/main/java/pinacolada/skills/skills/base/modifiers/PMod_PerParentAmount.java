package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.*;
import pinacolada.skills.fields.PField_Random;

@VisibleSkill
public class PMod_PerParentAmount extends PMod_Per<PField_Random> {

    public static final PSkillData<PField_Random> DATA = register(PMod_PerParentAmount.class, PField_Random.class).noTarget();

    public PMod_PerParentAmount() {
        this(1);
    }

    public PMod_PerParentAmount(int amount) {
        super(DATA, amount);
    }

    public PMod_PerParentAmount(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        if (info.data != null) {
            Object obj = info.data;
            if (obj instanceof Integer) {
                return (int) obj;
            }
            if (obj instanceof AbstractPower) {
                return ((AbstractPower) obj).amount;
            }
            if (obj instanceof AbstractOrb) {
                return ((AbstractOrb) obj).passiveAmount;
            }
        }
        return getParentAmount(parent);
    }

    protected int getParentAmount(PSkill<?> skill) {
        if (skill instanceof PDelay || (fields.random && skill instanceof PCond)) {
            return getParentAmount(skill.getParent());
        }
        return skill != null ? skill.amount : 0;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return EUIRM.strings.adjNoun(TEXT.cedit_useParent, getSubSampleText());
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return TEXT.subjects_amount;
    }


}
