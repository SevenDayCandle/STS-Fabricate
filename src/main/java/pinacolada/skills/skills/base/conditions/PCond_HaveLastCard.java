package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
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

public abstract class PCond_HaveLastCard extends PPassiveCond<PField_CardCategory> {
    public PCond_HaveLastCard(PSkillData<PField_CardCategory> data, PSkillSaveData content) {
        super(data, content);
    }

    public PCond_HaveLastCard(PSkillData<PField_CardCategory> data) {
        super(data, PCLCardTarget.None, 1);
    }

    public PCond_HaveLastCard(PSkillData<PField_CardCategory> data, int amount) {
        super(data, PCLCardTarget.None, amount);
    }

    public PCond_HaveLastCard(PSkillData<PField_CardCategory> data, int amount, PCLCardGroupHelper group) {
        super(data, PCLCardTarget.None, amount);
        fields.setCardGroup(group);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        List<AbstractCard> base = getCardPile(info, isUsing);
        int am = refreshAmount(info);
        if (base.size() < am) {
            return fields.not;
        }
        if (fields.groupTypes.isEmpty() && source instanceof AbstractCard) {
            for (int i = base.size() - 1; i >= Math.max(0, base.size() - am); i--) {
                AbstractCard c = base.get(i);
                if (!(c.uuid == ((AbstractCard) source).uuid)) {
                    return fields.not;
                }
            }
        }
        else {
            for (int i = base.size() - 1; i >= Math.max(0, base.size() - am); i--) {
                AbstractCard c = base.get(i);
                if (!fields.getFullCardFilter().invoke(c)) {
                    return fields.not;
                }
            }
        }
        return !fields.not;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.cond_ifLast(TEXT.subjects_x, PCLCoreStrings.past(getActionTooltip()), 1, TEXT.subjects_x);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String verb = fields.forced ? TEXT.subjects_thisCombat(PCLCoreStrings.past(getActionTooltip())) : TEXT.subjects_thisTurn(PCLCoreStrings.past(getActionTooltip()));
        return TEXT.cond_ifLast(fields.getThresholdRawString(fields.getShortCardString()), PCLCoreStrings.past(getActionTooltip()), getAmountRawString(), fields.groupTypes.isEmpty() && source instanceof AbstractCard ? TEXT.subjects_thisCard() : fields.getFullCardString());
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerFBoolean(editor, TEXT.cedit_combat, null);
    }

    abstract public EUIKeywordTooltip getActionTooltip();

    abstract public List<AbstractCard> getCardPile(PCLUseInfo info, boolean isUsing);

}
