package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;

@VisibleSkill
public class PMod_PerParentAmount extends PMod_Per<PField_Not> {

    public static final PSkillData<PField_Not> DATA = register(PMod_PerParentAmount.class, PField_Not.class).selfTarget();

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
        return parent != null ? parent.amount : 0;
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return TEXT.subjects_amount;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return EUIRM.strings.adjNoun(TEXT.cedit_useParent, getSubSampleText());
    }
}
