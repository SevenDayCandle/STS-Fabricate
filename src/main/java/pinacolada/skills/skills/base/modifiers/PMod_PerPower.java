package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.powers.PCLPowerData;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Power;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMod_PerPower extends PMod_Per<PField_Power> {

    public static final PSkillData<PField_Power> DATA = register(PMod_PerPower.class, PField_Power.class);

    public PMod_PerPower(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerPower() {
        super(DATA);
    }

    public PMod_PerPower(int amount, PCLPowerData... powerHelpers) {
        this(PCLCardTarget.AllEnemy, amount, powerHelpers);
    }

    public PMod_PerPower(PCLCardTarget target, int amount, PCLPowerData... powerHelpers) {
        super(DATA, target, amount);
        fields.setPower(powerHelpers);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return fields.powers.isEmpty() ?
                // Negative powers should be treated as positive stacks for the sake of counting debuffs
                sumTargets(info, t -> t.powers != null ? EUIUtils.sumInt(t.powers, po -> (po.type == AbstractPower.PowerType.DEBUFF) ^ !fields.debuff ? Math.abs(po.amount) : 0) : 0) :
                sumTargets(info, t -> EUIUtils.sumInt(fields.powers, po -> GameUtilities.getPowerAmount(t, po)));
    }

    @Override
    public String getSubSampleText() {
        return TEXT.cedit_powers;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return getTargetOnStringPerspective(perspective, fields.getPowerSubjectString());
    }

    public void use(PCLUseInfo info, PCLActions order) {
        target.getTargets(info, scope);
        super.use(info, order);
    }
}
