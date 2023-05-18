package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.PCLAction;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PActiveMod;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.ListSelection;

import java.util.ArrayList;
import java.util.List;

public abstract class PMod_Do extends PActiveMod<PField_CardCategory> {

    public PMod_Do(PSkillData<PField_CardCategory> data, PSkillSaveData content) {
        super(data, content);
    }

    public PMod_Do(PSkillData<PField_CardCategory> data) {
        super(data);
    }

    public PMod_Do(PSkillData<PField_CardCategory> data, PCLCardTarget target, int amount) {
        super(data, target, amount);
    }

    public PMod_Do(PSkillData<PField_CardCategory> data, PCLCardTarget target, int amount, PCLCardGroupHelper... groups) {
        super(data, target, amount);
        fields.setCardGroup(groups);
    }

    protected PCLAction<ArrayList<AbstractCard>> createPileAction(PCLUseInfo info) {
        SelectFromPile action = fields.createAction(getAction(), info, extra).setAnyNumber(true);
        if (isForced()) {
            action = action.setFilter(c -> fields.getFullCardFilter().invoke(c));
        }
        return action;
    }

    public abstract FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> getAction();

    protected String getActionPast() {
        return getActionTooltip().past;
    }

    protected String getActionTitle() {
        return getActionTooltip().title;
    }

    public abstract EUIKeywordTooltip getActionTooltip();

    @Override
    public String getAmountRawOrAllString() {
        return baseAmount <= 0 ? TEXT.subjects_any : extra > 0 ? TEXT.subjects_xOfY(getExtraRawString(), getAmountRawString()) : getRangeToAmountRawString();
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return EUIRM.strings.verbNoun(getActionTitle(), TEXT.subjects_x) + " " + TEXT.cond_doX(TEXT.subjects_x);
    }

    @Override
    public String getSubText() {
        return isForced() ? PGR.core.strings.subjects_card : fields.getFullCardStringSingular();
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerRequired(editor);
    }

    @Override
    public int getModifiedAmount(PSkill<?> be, PCLUseInfo info) {
        List<? extends AbstractCard> cards = info.getDataAsList(AbstractCard.class);
        return cards == null || be == null ? 0 : be.baseAmount * (isForced() ? cards.size() : (EUIUtils.count(cards,
                c -> fields.getFullCardFilter().invoke(c)
        )));
    }

    @Override
    public String getText(boolean addPeriod) {
        return getMoveString(addPeriod) + LocalizedStrings.PERIOD + (childEffect != null ? (" " +
                (isChildEffectUsingParent() ? childEffect.getText(addPeriod) :
                        (TEXT.cond_per(capital(childEffect.getText(false), addPeriod), EUIRM.strings.nounVerb(getSubText(), getActionPast())) + PCLCoreStrings.period(addPeriod))
                )) : "");
    }

    @Override
    public void use(PCLUseInfo info) {
        getActions().add(createPileAction(info))
                .addCallback(cards -> {
                    if (this.childEffect != null) {
                        info.setData(cards);
                        updateChildAmount(info);
                        this.childEffect.use(info);
                    }
                });
    }

    public String getMoveString(boolean addPeriod) {
        String cardString = isForced() ? fields.getFullCardString() : fields.getShortCardString();
        return !fields.groupTypes.isEmpty() ?
                TEXT.act_genericFrom(getActionTitle(), getAmountRawOrAllString(), cardString, fields.getGroupString())
                : EUIRM.strings.verbNumNoun(getActionTitle(), getAmountRawOrAllString(), cardString);
    }

    protected boolean isChildEffectUsingParent() {
        return childEffect != null && (childEffect.useParent || childEffect instanceof PMultiBase && EUIUtils.all(((PMultiBase<?>) childEffect).getSubEffects(), c -> c.useParent));
    }

    // Useparent on the child should also cause a "forced" filter
    protected boolean isForced() {
        return fields.forced || isChildEffectUsingParent();
    }
}
