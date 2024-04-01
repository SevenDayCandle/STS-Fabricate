package pinacolada.skills.skills.base.moves;

import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.powers.PCLPowerData;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;
import pinacolada.skills.fields.PField_Power;

@VisibleSkill
public class PMove_AddPowerBonus extends PMove<PField_Power> {
    public static final PSkillData<PField_Power> DATA = register(PMove_AddPowerBonus.class, PField_Power.class, -DEFAULT_MAX, DEFAULT_MAX);

    public PMove_AddPowerBonus() {
        this(1);
    }

    public PMove_AddPowerBonus(int amount, PCLPowerData... powers) {
        super(DATA, PCLCardTarget.None, amount);
        fields.setPower(powers);
    }

    public PMove_AddPowerBonus(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_zGainsBonus(TEXT.subjects_x, TEXT.subjects_x, TEXT.subjects_effectBonus);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return TEXT.act_zGainsBonus(PField.getPowerString(fields.powers), (amount > 0 ? ("+ " + getAmountRawString(requestor)) : getAmountRawString(requestor)), TEXT.subjects_effectBonus);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        for (String powerID : fields.powers) {
            PCLPowerData power = PCLPowerData.getStaticData(powerID);
            power.doFor(p -> order.addPowerEffectBonus(p, refreshAmount(info), !power.isDebuff()));
        }
        super.use(info, order);
    }
}
