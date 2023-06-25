package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Power;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMod_PerDistinctPower extends PMod_Per<PField_Power> {

    public static final PSkillData<PField_Power> DATA = register(PMod_PerDistinctPower.class, PField_Power.class);

    public PMod_PerDistinctPower(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerDistinctPower() {
        super(DATA);
    }

    public PMod_PerDistinctPower(int amount, PCLPowerHelper... powerHelpers) {
        this(PCLCardTarget.AllEnemy, amount, powerHelpers);
    }

    public PMod_PerDistinctPower(PCLCardTarget target, int amount, PCLPowerHelper... powerHelpers) {
        super(DATA, target, amount);
        fields.setPower(powerHelpers);
    }

    @Override
    public int getModifiedAmount(PSkill<?> be, PCLUseInfo info) {
        return be.baseAmount * getMultiplier(info);
    }

    public String getConditionText(String childText) {
        if (fields.not) {
            return TEXT.cond_xConditional(childText, TEXT.cond_perDistinct(getAmountRawString(), getSubText()));
        }
        return TEXT.cond_perDistinct(childText,
                this.amount <= 1 ? getSubText() : EUIRM.strings.numNoun(getAmountRawString(), getSubText()));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.cond_perDistinct(TEXT.subjects_x, getSubSampleText());
    }

    @Override
    public String getSubSampleText() {
        return TEXT.cedit_powers;
    }

    @Override
    public int getMultiplier(PCLUseInfo info) {
        return fields.powers.isEmpty() ?
                sumTargets(info, t -> EUIUtils.count(t.powers, fields.debuff ? po -> po.type == AbstractPower.PowerType.DEBUFF : po -> po.type == AbstractPower.PowerType.BUFF)) :
                sumTargets(info, t -> EUIUtils.count(fields.powers, po -> GameUtilities.getPowerAmount(t, po.ID) >= this.amount));
    }

    @Override
    public String getSubText() {
        String baseString = fields.getPowerSubjectString();
        switch (target) {
            case All:
            case Any:
                return TEXT.subjects_onAnyCharacter(baseString);
            case AllEnemy:
                return TEXT.subjects_onAnyEnemy(baseString);
            case Single:
                return TEXT.subjects_onTheEnemy(baseString);
            case Self:
                return TEXT.subjects_onYou(baseString);
            default:
                return baseString;
        }
    }
}
