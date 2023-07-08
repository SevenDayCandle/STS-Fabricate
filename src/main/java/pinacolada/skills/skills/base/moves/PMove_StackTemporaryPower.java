package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Power;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMove_StackTemporaryPower extends PMove<PField_Power> {
    public static final PSkillData<PField_Power> DATA = register(PMove_StackTemporaryPower.class, PField_Power.class, -DEFAULT_MAX, DEFAULT_MAX);

    public PMove_StackTemporaryPower() {
        this(PCLCardTarget.Self, 1);
    }

    public PMove_StackTemporaryPower(PCLCardTarget target, int amount, PCLPowerHelper... powers) {
        super(DATA, target, amount);
        fields.setPower(powers);
    }

    public PMove_StackTemporaryPower(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.act_applyAmountX(EUIUtils.format(TEXT.misc_tempPowerPrefix, TEXT.subjects_x), TEXT.cedit_powers);
    }

    @Override
    public void onDrag(AbstractMonster m) {
        if (m != null) {
            for (PCLPowerHelper power : fields.powers) {
                GameUtilities.getIntent(m).addModifier(power.ID, amount);
            }
        }
    }

    @Override
    public String getSubText() {
        String joinedString = EUIUtils.format(TEXT.misc_tempPowerPrefix, fields.getPowerString());
        switch (target) {
            case RandomEnemy:
            case AllAlly:
            case AllEnemy:
            case All:
            case Team:
                return fields.powers.size() > 0 && fields.powers.get(0).isDebuff ? TEXT.act_applyAmountXToTarget(getAmountRawString(), joinedString, getTargetString()) : TEXT.act_giveTargetAmount(getTargetString(), getAmountRawString(), joinedString);
            case Single:
            case SingleAlly:
                return fields.powers.size() > 0 && fields.powers.get(0).isDebuff ? TEXT.act_applyAmountX(getAmountRawString(), joinedString) : TEXT.act_giveTargetAmount(getTargetString(), getAmountRawString(), joinedString);
            case Self:
                if (isFromCreature()) {
                    return TEXT.act_giveTargetAmount(getTargetString(), getAmountRawString(), joinedString);
                }
            default:
                return amount < 0 ? TEXT.act_loseAmount(getAmountRawString(), joinedString)
                        : TEXT.act_gainAmount(getAmountRawString(), joinedString);
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        for (PCLPowerHelper power : fields.powers) {
            for (AbstractCreature target : getTargetList(info)) {
                order.applyPower(info.source, target, power, amount, true);
            }
        }
        super.use(info, order);
    }
}
