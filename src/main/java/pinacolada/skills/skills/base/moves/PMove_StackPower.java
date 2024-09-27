package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.BlightHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.powers.PCLDynamicPowerData;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;
import pinacolada.skills.fields.PField_Power;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.GameUtilities;

import java.util.List;

@VisibleSkill
public class PMove_StackPower extends PMove<PField_Power> {
    public static final PSkillData<PField_Power> DATA = register(PMove_StackPower.class, PField_Power.class, -DEFAULT_MAX, DEFAULT_MAX);

    public PMove_StackPower() {
        this(PCLCardTarget.Self, 1);
    }

    public PMove_StackPower(PCLCardTarget target, int amount, PCLPowerData... powers) {
        super(DATA, target, amount, 0);
        fields.setPower(powers);
    }

    public PMove_StackPower(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_applyAmountX(TEXT.subjects_x, TEXT.cedit_powers);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String amountString = getAmountRawString(requestor);
        String joinedString;
        if (fields.random && !fields.powers.isEmpty()) {
            joinedString = fields.getPowerOrString();
            switch (target) {
                case Self:
                    if (isFromCreature() || perspective != PCLCardTarget.Self) {
                        return TEXT.subjects_randomly(TEXT.act_giveTargetAmount(getTargetStringPerspective(perspective), amountString, joinedString));
                    }
                case None:
                    return TEXT.subjects_randomly(amount < 0 ? TEXT.act_loseAmount(amountString, joinedString)
                            : TEXT.act_gainAmount(amountString, joinedString));
                case Single:
                    return TEXT.subjects_randomly(!fields.powers.isEmpty() ? TEXT.act_applyAmountX(amountString, joinedString) : TEXT.act_giveTargetAmount(getTargetStringPerspective(perspective), amountString, joinedString));
                default:
                    return TEXT.subjects_randomly(!fields.powers.isEmpty() ? TEXT.act_applyAmountXToTarget(amountString, joinedString, getTargetStringPerspective(perspective)) : TEXT.act_giveTargetAmount(getTargetStringPerspective(perspective), amountString, joinedString));
            }
        }

        // Keyword power
        if (!fields.powers.isEmpty() && EUIUtils.all(fields.powers, PCLPowerData::isInstant)) {
            String titleString = amount > 0 ? EUIRM.strings.generic2(PField.getPowerTitleAndString(fields.powers), amountString) : PField.getPowerTitleAndString(fields.powers);
            switch (target) {
                case Self:
                case None:
                    return titleString;
                default:
                    return TEXT.subjects_onTarget(titleString, getTargetStringPerspective(perspective));
            }
        }

        joinedString = fields.powers.isEmpty() ? TEXT.subjects_randomX(plural(fields.debuff ? PGR.core.tooltips.debuff : PGR.core.tooltips.buff, requestor)) : fields.getPowerString();
        switch (target) {
            case Self:
                if (isFromCreature() || perspective != PCLCardTarget.Self) {
                    return amount < 0 ? TEXT.act_removeFrom(EUIRM.strings.numNoun(amountString, joinedString), getTargetStringPerspective(perspective)) : TEXT.act_giveTargetAmount(getTargetStringPerspective(perspective), amountString, joinedString);
                }
            case None:
                return amount < 0 ? TEXT.act_loseAmount(amountString, joinedString)
                        : TEXT.act_gainAmount(amountString, joinedString);
            case Single:
                return amount < 0 ? TEXT.act_remove(EUIRM.strings.numNoun(amountString, joinedString)) :
                        !fields.powers.isEmpty() && !useParent ?
                                TEXT.act_applyAmountX(amountString, joinedString) : TEXT.act_giveTargetAmount(getTargetStringPerspective(perspective), amountString, joinedString);
            default:
                return amount < 0 ? TEXT.act_removeFrom(EUIRM.strings.numNoun(amountString, joinedString), getTargetStringPerspective(perspective))
                        : !fields.powers.isEmpty() && !useParent
                        ? TEXT.act_applyAmountXToTarget(amountString, joinedString, getTargetStringPerspective(perspective)) : TEXT.act_giveTargetAmount(getTargetStringPerspective(perspective), amountString, joinedString);
        }
    }

    @Override
    public boolean isDetrimental() {
        return ((target.targetsSelf()) && EUIUtils.any(fields.powers, PCLPowerData::isDebuff)) ||
                ((target.targetsEnemies()) && EUIUtils.any(fields.powers, PCLPowerData::isBuff));
    }

    @Override
    public boolean isMetascaling() {
        return !isDetrimental() && EUIUtils.any(fields.powers, PCLPowerData::isMetascaling);
    }

    @Override
    public void onSetupTips(AbstractCard card) {
        if (card instanceof KeywordProvider && !fields.powers.isEmpty() && EUIUtils.all(fields.powers, PCLPowerData::isInstant)) {
            List<EUIKeywordTooltip> tips = ((KeywordProvider) card).getTips();
            for (String po : fields.powers) {
                PCLPowerData data = PCLPowerData.getStaticDataOrCustom(po);
                if (data != null) {
                    if (data.tooltip != null) {
                        tips.add(data.tooltip);
                    }
                    else {
                        EUIKeywordTooltip tip = EUIKeywordTooltip.findByIDTemp(data.ID);
                        if (tip == null) {
                            tip = new EUIKeywordTooltip(data.getName(), data instanceof PCLDynamicPowerData ? ((PCLDynamicPowerData) data).getEffectTextForTip() : "");
                        }
                        tips.add(tip);
                    }
                }
            }
        }
    }

    @Override
    public void onDrag(AbstractMonster m) {
        if (m != null) {
            for (String power : fields.powers) {
                GameUtilities.getIntent(m).addModifier(power, amount);
            }
        }
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerRandom(editor);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (!fields.powers.isEmpty()) {
            if (fields.random) {
                String powerID = GameUtilities.getRandomElement(fields.powers);
                useApplyPower(powerID, info, order);
            }
            else {
                for (String powerID : fields.powers) {
                    useApplyPower(powerID, info, order);
                }
            }
        }
        else {
            int actualAmount = refreshAmount(info);
            for (int i = 0; i < actualAmount; i++) {
                for (AbstractCreature target : getTargetListAsNew(info)) {
                    order.applyPower(info.source, target, PCLPowerData.getRandom(p -> p.isCommon && fields.debuff ^ !p.isDebuff()), actualAmount);
                }
            }
        }
        super.use(info, order);
    }

    private void useApplyPower(String powerID, PCLUseInfo info, PCLActions order) {
        PCLPowerData power = PCLPowerData.getStaticDataOrCustom(powerID);
        if (power != null) {
            for (AbstractCreature target : getTargetListAsNew(info)) {
                int actualAmount = refreshAmount(info);
                order.applyPower(info.source, target, power, actualAmount);
            }
        }
    }

    @Override
    public String wrapTextAmountSelf(int input) {
        return String.valueOf(Math.abs(input));
    }
}
