package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
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
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.List;


public abstract class PMod_PerCardHas extends PMod_Per<PField_CardCategory> {
    public PMod_PerCardHas(PSkillData<PField_CardCategory> data, PSkillSaveData content) {
        super(data, content);
    }

    public PMod_PerCardHas(PSkillData<PField_CardCategory> data) {
        this(data, 0, 0);
    }

    public PMod_PerCardHas(PSkillData<PField_CardCategory> data, int amount, int count) {
        super(data, amount, count);
    }

    public PMod_PerCardHas(PSkillData<PField_CardCategory> data, int amount, int count, PCLCardGroupHelper group) {
        super(data, amount, count);
        fields.setCardGroup(group);
    }

    @Override
    public String getConditionText(PCLCardTarget perspective, String childText) {
        if (fields.groupTypes.isEmpty() && sourceCard != null) {
            return fields.forced ? TEXT.cond_perThisCombat(childText, TEXT.subjects_times(getAmountRawString()), PCLCoreStrings.past(getActionTooltip()))
                    : TEXT.cond_perThisTurn(childText, TEXT.subjects_times(getAmountRawString()), PCLCoreStrings.past(getActionTooltip()));
        }
        if (fields.not) {
            return TEXT.cond_xConditional(childText,
                    fields.forced ? TEXT.cond_perThisCombat(getAmountRawString(), fields.getFullCardStringSingular(), PCLCoreStrings.past(getActionTooltip()))
                            : TEXT.cond_perThisTurn(getAmountRawString(), fields.getFullCardStringSingular(), PCLCoreStrings.past(getActionTooltip())));
        }
        String subjString = this.amount <= 1 ? fields.getFullCardStringSingular() : EUIRM.strings.numNoun(getAmountRawString(), fields.getFullCardStringSingular());
        return fields.forced ? TEXT.cond_perThisCombat(childText, subjString, PCLCoreStrings.past(getActionTooltip())) : TEXT.cond_perThisTurn(childText, subjString, PCLCoreStrings.past(getActionTooltip()));
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return fields.groupTypes.isEmpty() && sourceCard != null
                ? EUIUtils.count(getCardPile(info, isUsing), c -> c.uuid == sourceCard.uuid)
                : EUIUtils.count(getCardPile(info, isUsing), c -> fields.getFullCardFilter().invoke(c));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.cond_xPerYZ(TEXT.subjects_x, TEXT.subjects_card, PCLCoreStrings.past(getActionTooltip()));
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return fields.getFullCardString();
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerFBoolean(editor, TEXT.cedit_combat, null);
    }

    abstract public EUIKeywordTooltip getActionTooltip();

    abstract public List<AbstractCard> getCardPile(PCLUseInfo info, boolean isUsing);
}
