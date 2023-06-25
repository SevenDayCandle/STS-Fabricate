package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Power;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMove_StackPower extends PMove<PField_Power> {
    public static final PSkillData<PField_Power> DATA = register(PMove_StackPower.class, PField_Power.class, -DEFAULT_MAX, DEFAULT_MAX)
            .setExtra(1, 1);

    public PMove_StackPower() {
        this(PCLCardTarget.Self, 1);
    }

    public PMove_StackPower(PCLCardTarget target, int amount, PCLPowerHelper... powers) {
        super(DATA, target, amount, 1);
        fields.setPower(powers);
    }

    public PMove_StackPower(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.act_applyAmountX(TEXT.subjects_x, TEXT.cedit_powers);
    }

    @Override
    public String getSubText() {
        String joinedString;
        if (fields.random && !fields.powers.isEmpty()) {
            joinedString = fields.getPowerOrString();
            switch (target) {
                case Self:
                    if (isFromCreature()) {
                        return TEXT.subjects_randomly(TEXT.act_giveTargetAmount(getTargetString(), getAmountRawString(), joinedString));
                    }
                case None:
                    return TEXT.subjects_randomly(amount < 0 ? TEXT.act_loseAmount(getAmountRawString(), joinedString)
                            : TEXT.act_gainAmount(getAmountRawString(), joinedString));
                case Single:
                case SingleAlly:
                    return TEXT.subjects_randomly(fields.powers.size() > 0 && fields.powers.get(0).isDebuff ? TEXT.act_applyAmountX(getAmountRawString(), joinedString) : TEXT.act_giveTargetAmount(getTargetString(), getAmountRawString(), joinedString));
                default:
                    return TEXT.subjects_randomly(fields.powers.size() > 0 && fields.powers.get(0).isDebuff ? TEXT.act_applyAmountXToTarget(getAmountRawString(), joinedString, getTargetString()) : TEXT.act_giveTargetAmount(getTargetString(), getAmountRawString(), joinedString));
            }
        }
        joinedString = fields.powers.isEmpty() ? TEXT.subjects_randomX(plural(fields.debuff ? PGR.core.tooltips.debuff : PGR.core.tooltips.buff)) : fields.getPowerString();
        switch (target) {
            case Self:
                if (isFromCreature()) {
                    return amount < 0 ? TEXT.act_removeFrom(EUIRM.strings.numNoun(getAmountRawString(), joinedString), getTargetString()) : TEXT.act_giveTargetAmount(getTargetString(), getAmountRawString(), joinedString);
                }
            case None:
                return amount < 0 ? TEXT.act_loseAmount(getAmountRawString(), joinedString)
                        : TEXT.act_gainAmount(getAmountRawString(), joinedString);
            case Single:
            case SingleAlly:
                return amount < 0 ? TEXT.act_remove(EUIRM.strings.numNoun(getAmountRawString(), joinedString)) :
                        fields.powers.size() > 0 && fields.powers.get(0).isDebuff && !useParent ?
                                TEXT.act_applyAmountX(getAmountRawString(), joinedString) : TEXT.act_giveTargetAmount(getTargetString(), getAmountRawString(), joinedString);
            default:
                return amount < 0 ? TEXT.act_removeFrom(EUIRM.strings.numNoun(getAmountRawString(), joinedString), getTargetString())
                        : fields.powers.size() > 0 && fields.powers.get(0).isDebuff && !useParent
                        ? TEXT.act_applyAmountXToTarget(getAmountRawString(), joinedString, getTargetString()) : TEXT.act_giveTargetAmount(getTargetString(), getAmountRawString(), joinedString);
        }
    }

    @Override
    public boolean isDetrimental() {
        return ((target.targetsSelf()) && EUIUtils.any(fields.powers, po -> po.isDebuff)) ||
                ((!target.targetsSelf()) && EUIUtils.any(fields.powers, po -> !po.isDebuff));
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
    public String wrapAmount(int input) {
        return String.valueOf(Math.abs(input));
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (!fields.powers.isEmpty()) {
            if (fields.random) {
                PCLPowerHelper power = GameUtilities.getRandomElement(fields.powers);
                if (power != null) {
                    order.applyPower(info.source, info.target, target, power, amount);
                }
            }
            else {
                for (PCLPowerHelper power : fields.powers) {
                    order.applyPower(info.source, info.target, target, power, amount);
                }
            }
        }
        else {
            for (int i = 0; i < amount; i++) {
                order.applyPower(info.source, info.target, target, fields.debuff ? PCLPowerHelper.randomDebuff() : PCLPowerHelper.randomBuff(), extra);
            }
        }
        super.use(info, order);
    }
}
