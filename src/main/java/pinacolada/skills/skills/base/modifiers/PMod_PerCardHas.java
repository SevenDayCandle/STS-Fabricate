package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.ui.cardEditor.PCLCustomEffectEditingPane;

import java.util.List;


public abstract class PMod_PerCardHas extends PMod_Per<PField_CardCategory> {
    public PMod_PerCardHas(PSkillData<PField_CardCategory> data, PSkillSaveData content) {
        super(data, content);
    }

    public PMod_PerCardHas(PSkillData<PField_CardCategory> data) {
        this(data, 0, 1);
    }

    public PMod_PerCardHas(PSkillData<PField_CardCategory> data, int amount, int count) {
        super(data, amount, count);
    }

    abstract public EUITooltip getActionTooltip();

    abstract public List<AbstractCard> getCardPile();

    @Override
    public String getConditionText(String childText) {
        if (fields.not) {
            return TEXT.cond_genericConditional(childText,
                    fields.forced ? TEXT.cond_perThisCombat(getAmountRawString(), fields.getFullCardStringSingular(), PCLCoreStrings.past(getActionTooltip())) : TEXT.cond_perThisTurn(getAmountRawString(), fields.getFullCardStringSingular(), PCLCoreStrings.past(getActionTooltip())));
        }
        String subjString = this.amount <= 1 ? fields.getFullCardStringSingular() : EUIRM.strings.numNoun(getAmountRawString(), fields.getFullCardStringSingular());
        return fields.forced ? TEXT.cond_perThisCombat(childText, subjString, PCLCoreStrings.past(getActionTooltip())) : TEXT.cond_perThisTurn(childText, subjString, PCLCoreStrings.past(getActionTooltip()));
    }

    @Override
    public int getMultiplier(PCLUseInfo info) {
        return EUIUtils.count(fields.forced ? CombatManager.cardsDiscardedThisCombat() : CombatManager.cardsDiscardedThisTurn(),
                c -> fields.getFullCardFilter().invoke(c));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.cond_perXY(TEXT.subjects_x, TEXT.subjects_card, PCLCoreStrings.past(getActionTooltip()));
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerFBoolean(editor, TEXT.cedit_combat, null);
    }

    @Override
    public String getSubText() {
        return fields.getFullCardString();
    }
}
