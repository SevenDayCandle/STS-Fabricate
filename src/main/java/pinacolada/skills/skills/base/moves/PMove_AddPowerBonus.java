package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.powers.IntangiblePower;
import com.megacrit.cardcrawl.powers.LockOnPower;
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
    public String getSubText(PCLCardTarget perspective) {
        return TEXT.act_zGainsBonus(PField.getPowerString(fields.powers), (amount > 0 ? ("+ " + getAmountRawString()) : getAmountRawString()), TEXT.subjects_effectBonus);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        for (PCLPowerData power : fields.powers) {
            order.addPowerEffectBonus(power.ID, amount, !power.isDebuff());
            // Handle powers that are equivalent in terms of what the player sees but that have different IDs
            if (power == PCLPowerData.Intangible) {
                order.addPowerEffectBonus(IntangiblePower.POWER_ID, amount, !power.isDebuff());
            }
            else if (power == PCLPowerData.LockOn) {
                order.addPowerEffectBonus(LockOnPower.POWER_ID, amount, !power.isDebuff());
            }
        }
        super.use(info, order);
    }
}
