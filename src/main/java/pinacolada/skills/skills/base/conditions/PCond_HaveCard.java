package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.List;

public abstract class PCond_HaveCard extends PPassiveCond<PField_CardCategory> {
    public PCond_HaveCard(PSkillData<PField_CardCategory> data, PSkillSaveData content) {
        super(data, content);
    }

    public PCond_HaveCard(PSkillData<PField_CardCategory> data) {
        super(data, PCLCardTarget.None, 1);
    }

    public PCond_HaveCard(PSkillData<PField_CardCategory> data, int amount) {
        super(data, PCLCardTarget.None, amount);
    }

    public PCond_HaveCard(PSkillData<PField_CardCategory> data, int amount, PCLCardGroupHelper group) {
        super(data, PCLCardTarget.None, amount);
        fields.setCardGroup(group);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        int count = fields.groupTypes.isEmpty() && sourceCard != null
                ? EUIUtils.count(getCardPile(info, isUsing), c -> c.uuid == sourceCard.uuid)
                : EUIUtils.count(getCardPile(info, isUsing), c -> fields.getFullCardFilter().invoke(c));
        return amount == 0 ? count == 0 : fields.not ^ count >= amount;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.cond_ifX(PCLCoreStrings.past(getActionTooltip()));
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        if (fields.groupTypes.isEmpty() && sourceCard != null) {
            String base = fields.forced ? TEXT.cond_ifYouDidThisCombat(PCLCoreStrings.past(getActionTooltip()), TEXT.subjects_thisCard()) :
                    TEXT.cond_ifYouDidThisTurn(PCLCoreStrings.past(getActionTooltip()), TEXT.subjects_thisCard());
            return baseAmount > 1 ? TEXT.act_generic2(base, TEXT.subjects_times(getAmountRawString())) : base;
        }
        return fields.forced ? TEXT.cond_ifYouDidThisCombat(PCLCoreStrings.past(getActionTooltip()), fields.getThresholdRawString(fields.getFullCardString())) :
                TEXT.cond_ifYouDidThisTurn(PCLCoreStrings.past(getActionTooltip()), fields.getThresholdRawString(fields.getFullCardString()));
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerFBoolean(editor, TEXT.cedit_combat, null);
    }

    abstract public EUIKeywordTooltip getActionTooltip();

    abstract public List<AbstractCard> getCardPile(PCLUseInfo info, boolean isUsing);

}
