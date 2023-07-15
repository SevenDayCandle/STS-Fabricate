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

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        int count = EUIUtils.count(getCardPile(),
                c -> fields.getFullCardFilter().invoke(c));
        return amount == 0 ? count == 0 : fields.not ^ count >= amount;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.cond_ifX(PCLCoreStrings.past(getActionTooltip()));
    }

    @Override
    public String getSubText() {
        return fields.forced ? TEXT.cond_ifYouDidThisCombat(PCLCoreStrings.past(getActionTooltip()), EUIRM.strings.numNoun(getAmountRawString(), fields.getFullCardString())) :
                TEXT.cond_ifYouDidThisTurn(PCLCoreStrings.past(getActionTooltip()), EUIRM.strings.numNoun(getAmountRawString(), fields.getFullCardString()));
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerFBoolean(editor, TEXT.cedit_combat, null);
    }

    abstract public EUIKeywordTooltip getActionTooltip();

    abstract public List<AbstractCard> getCardPile();

}
