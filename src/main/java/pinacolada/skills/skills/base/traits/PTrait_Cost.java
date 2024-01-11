package pinacolada.skills.skills.base.traits;

import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.*;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.base.primary.PTrigger_Passive;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

@VisibleSkill
public class PTrait_Cost extends PTrait<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PTrait_Cost.class, PField_Not.class)
            .setSourceTypes(PSkillData.SourceType.Card, PSkillData.SourceType.Power);

    public PTrait_Cost() {
        this(1);
    }

    public PTrait_Cost(int amount) {
        super(DATA, amount);
    }

    public PTrait_Cost(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_costs("+X");
    }

    @Override
    public String getSubDescText(PCLCardTarget perspective) {
        return getAmountRawString();
    }

    @Override
    public String getSubSampleText() {
        return TEXT.subjects_cost;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (hasParentType(PTrigger_Passive.class)) {
            return TEXT.act_zCosts(getParentCardString(perspective, requestor), 2, getSubDescText(perspective));
        }
        return TEXT.act_costs(getSubDescText(perspective));
    }

    @Override
    public boolean isDetrimental() {
        return amount > 0;
    }

    @Override
    public int modifyCost(PCLUseInfo info, int amount) {
        int actualAmount = refreshAmount(info);
        return fields.not ? actualAmount : amount + actualAmount;
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerNotBoolean(editor, TEXT.cedit_exact, null);
    }

    @Override
    public boolean shouldHideText() {
        return !fields.not && baseAmount == 0 && !hasParentType(PMod.class);
    }

    @Override
    public String wrapTextAmountSelf(int input) {
        return input >= 0 && !fields.not ? "+" + input : String.valueOf(input);
    }
}
