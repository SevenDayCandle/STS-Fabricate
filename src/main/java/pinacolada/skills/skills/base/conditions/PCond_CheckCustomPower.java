package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnApplyPowerSubscriber;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CustomPowerCheck;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.GameUtilities;

import java.util.List;

@VisibleSkill
public class PCond_CheckCustomPower extends PPassiveCond<PField_CustomPowerCheck> implements OnApplyPowerSubscriber {
    public static final PSkillData<PField_CustomPowerCheck> DATA = register(PCond_CheckCustomPower.class, PField_CustomPowerCheck.class);

    public PCond_CheckCustomPower(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_CheckCustomPower() {
        super(DATA, PCLCardTarget.Self, 1);
    }

    public PCond_CheckCustomPower(PCLCardTarget target, int amount, String... powers) {
        super(DATA, target, amount);
        fields.setCardIDs(powers);
    }

    public PCond_CheckCustomPower(PCLCardTarget target, int amount, List<String> powers) {
        super(DATA, target, amount);
        fields.setCardIDs(powers);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return evaluateTargets(info, t -> fields.allOrAnyR(fields.cardIDs, po -> checkPowers(po, t)));
    }

    private boolean checkPowers(String id, AbstractCreature t) {
        return fields.doesValueMatchThreshold(GameUtilities.getPowerAmountMatching(t, id));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return isUnderWhen(callingSkill, parentSkill) ? TEXT.cond_when(TEXT.act_gainAmount(TEXT.subjects_x, TEXT.cedit_customPower)) : EUIRM.strings.numNoun(TEXT.subjects_x, TEXT.cedit_customPower);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        String baseString = fields.getThresholdRawString(fields.random ? fields.getCardIDOrString() : fields.getCardIDAndString());
        if (isWhenClause()) {
            return getWheneverString(TEXT.act_gainOrdinal(getTargetOrdinalPerspective(perspective), baseString), perspective);
        }

        return getTargetHasStringPerspective(perspective, baseString);
    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature t, AbstractCreature source) {
        AbstractCreature owner = getOwnerCreature();
        PCLUseInfo info = generateInfo(owner, t);
        boolean eval = evaluateTargets(info, c -> c == t);
        // For single target powers, the power target needs to match the owner of this skill
        if (EUIUtils.any(fields.cardIDs, id -> power.ID.contains(id)) && eval) {
            useFromTrigger(info.setData(power));
        }
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerRBoolean(editor, TEXT.cedit_or, null);
    }
}
