package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardGeneric;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.List;

public abstract class PCond_HaveCardThis extends PPassiveCond<PField_CardGeneric> {
    public PCond_HaveCardThis(PSkillData<PField_CardGeneric> data, PSkillSaveData content) {
        super(data, content);
    }

    public PCond_HaveCardThis(PSkillData<PField_CardGeneric> data) {
        super(data, PCLCardTarget.None, 1);
    }

    public PCond_HaveCardThis(PSkillData<PField_CardGeneric> data, int amount) {
        super(data, PCLCardTarget.None, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        int count = sourceCard != null ? EUIUtils.count(getCardPile(),
                c -> c.uuid == sourceCard.uuid) : 0;
        return amount == 0 ? count == 0 : fields.not ^ count >= amount;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.cond_ifX(EUIRM.strings.verbNoun(PCLCoreStrings.past(getActionTooltip()), TEXT.subjects_thisCard));
    }

    @Override
    public String wrapAmount(int input) {
        return fields.getThresholdValString(input);
    }

    @Override
    public String getSubText() {
        // TODO proper grammar formattting for "Do X Y times" format
        String base = fields.forced ? TEXT.cond_ifYouDidThisCombat(PCLCoreStrings.past(getActionTooltip()), TEXT.subjects_thisCard) :
                TEXT.cond_ifYouDidThisTurn(PCLCoreStrings.past(getActionTooltip()), TEXT.subjects_thisCard);
        return baseAmount > 1 ? TEXT.act_generic2(base, TEXT.subjects_times(getAmountRawString())) : base;
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerFBoolean(editor, TEXT.cedit_combat, null);
    }

    abstract public EUIKeywordTooltip getActionTooltip();

    abstract public List<AbstractCard> getCardPile();
}
