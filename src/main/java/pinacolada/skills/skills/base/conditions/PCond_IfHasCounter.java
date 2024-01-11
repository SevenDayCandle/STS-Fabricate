package pinacolada.skills.skills.base.conditions;

import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.cardText.PointerToken;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.providers.ValueProvider;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PFacetCond;
import pinacolada.skills.skills.base.primary.PTrigger_When;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PCond_IfHasCounter extends PFacetCond<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PCond_IfHasCounter.class, PField_Not.class)
            .noTarget();

    public PCond_IfHasCounter(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_IfHasCounter() {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_IfHasCounter(int amount) {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return info != null &&
                (fields.doesValueMatchThreshold(info, GameUtilities.getCounter(info.card)));
    }

    @Override
    public String getAdditionalWidthString() {
        return !hasParentType(PTrigger_When.class) ? PointerToken.DUMMY : EUIUtils.EMPTY_STRING;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.cond_ifX(PGR.core.tooltips.counter.title);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (isWhenClause() || isPassiveClause()) {
            return EUIRM.strings.adjNoun(fields.getThresholdRawString(plural(PGR.core.tooltips.counter)), TEXT.subjects_card);
        }
        return TEXT.cond_ifTargetHas(TEXT.subjects_this, 1, fields.getThresholdRawString(plural(PGR.core.tooltips.counter))) + getXRawString();
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        String base = super.getText(perspective, requestor, addPeriod);
        if (!isWhenClause() && !isPassiveClause()) {
            return base + getXRawString();
        }
        return base;
    }

    @Override
    public String getXString() {
        // Do not show the x value for when powers
        if (CombatManager.inBattle() && source instanceof ValueProvider && !hasParentType(PTrigger_When.class)) {
            return " (" + getXValue() + ")";
        }
        return EUIUtils.EMPTY_STRING;
    }

    @Override
    public int getXValue() {
        return GameUtilities.getCounter(source);
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        registerUseParentBoolean(editor);
    }
}
