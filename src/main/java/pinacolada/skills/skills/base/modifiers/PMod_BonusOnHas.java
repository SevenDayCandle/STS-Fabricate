package pinacolada.skills.skills.base.modifiers;

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
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.List;

public abstract class PMod_BonusOnHas extends PMod_BonusOn<PField_CardCategory> {
    public PMod_BonusOnHas(PSkillData<PField_CardCategory> data, PSkillSaveData content) {
        super(data, content);
    }

    public PMod_BonusOnHas(PSkillData<PField_CardCategory> data) {
        this(data, 0, 1);
    }

    public PMod_BonusOnHas(PSkillData<PField_CardCategory> data, int amount, int count) {
        super(data, amount, count);
    }

    @Override
    public String getConditionText(PCLCardTarget perspective) {
        return EUIRM.strings.generic2(getAmountRawString(), getSubText(perspective));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.cond_bonusIf(TEXT.subjects_x, PCLCoreStrings.past(getActionTooltip()));
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        if (fields.groupTypes.isEmpty() && sourceCard != null) {
            String base = fields.forced ? TEXT.cond_ifYouDidThisCombat(PCLCoreStrings.past(getActionTooltip()), TEXT.subjects_thisCard()) :
                    TEXT.cond_ifYouDidThisTurn(PCLCoreStrings.past(getActionTooltip()), TEXT.subjects_thisCard());
            return baseAmount > 1 ? TEXT.act_generic2(base, TEXT.subjects_times(getAmountRawString())) : base;
        }
        return fields.forced ? TEXT.cond_ifYouDidThisCombat(PCLCoreStrings.past(getActionTooltip()), EUIRM.strings.numNoun(getExtraRawString(), fields.getFullCardOrString(getExtraRawString()))) :
                TEXT.cond_ifYouDidThisTurn(PCLCoreStrings.past(getActionTooltip()), EUIRM.strings.numNoun(getExtraRawString(), fields.getFullCardOrString(getExtraRawString())));
    }

    @Override
    public boolean meetsCondition(PCLUseInfo info, boolean isUsing) {
        int count = fields.groupTypes.isEmpty() && sourceCard != null
                ? EUIUtils.count(getCardPile(info, isUsing), c -> c.uuid == sourceCard.uuid)
                : EUIUtils.count(getCardPile(info, isUsing), c -> fields.getFullCardFilter().invoke(c));
        return extra == 0 ? count == 0 : fields.not ^ count >= extra;
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerFBoolean(editor, TEXT.cedit_combat, null);
    }

    abstract public EUIKeywordTooltip getActionTooltip();

    abstract public List<AbstractCard> getCardPile(PCLUseInfo info, boolean isUsing);
}
