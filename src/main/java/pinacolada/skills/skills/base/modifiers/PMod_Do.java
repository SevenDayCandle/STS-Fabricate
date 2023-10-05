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
import pinacolada.actions.PCLActions;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
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
        SelectFromPile action = fields.createAction(getAction(), info, extra, true).setAnyNumber(true);
        if (isForced()) {
            action = action.setFilter(c -> fields.getFullCardFilter().invoke(c));
        }
        return action;
    }

    protected String getActionPast() {
        return getActionTooltip().past;
    }

    protected String getActionTitle() {
        return getActionTooltip().title;
    }

    @Override
    public String getAmountRawOrAllString() {
        return shouldActAsAll() ? (isForced() ? TEXT.subjects_all : TEXT.subjects_any)
                : extra > 0 ? TEXT.subjects_xOfY(getExtraRawString(), getAmountRawString())
                : getAmountRawString();
    }

    public PCLCardGroupHelper getDestinationGroup() {
        return null;
    }

    @Override
    public int getModifiedAmount(PSkill<?> be, PCLUseInfo info, boolean isUsing) {
        List<? extends AbstractCard> cards = info.getDataAsList(AbstractCard.class);
        return cards == null || be == null ? 0 : be.baseAmount * (isForced() ? cards.size() : (EUIUtils.count(cards,
                c -> fields.getFullCardFilter().invoke(c)
        )));
    }

    public String getMoveString(boolean addPeriod) {
        String cardString = isForced() ? fields.getFullCardString() : fields.getShortCardString();
        if (fields.destination == PCLCardSelection.Manual || getDestinationGroup() == null) {
            return useParent ? EUIRM.strings.verbNoun(getActionTitle(), getInheritedThemString()) :
                    fields.shouldHideGroupNames() ? TEXT.act_generic3(getActionTitle(), getAmountRawOrAllString(), cardString) :
                            fields.hasGroups() ? TEXT.act_zXFromY(getActionTitle(), getAmountRawOrAllString(), cardString, fields.getGroupString())
                                    : EUIRM.strings.verbNoun(getActionTitle(), TEXT.subjects_thisCard());
        }
        String dest = fields.getDestinationString(getDestinationGroup().name);
        return useParent ? TEXT.act_zToX(getActionTitle(), getInheritedThemString(), dest) :
                fields.shouldHideGroupNames() ? TEXT.act_zXToY(getActionTitle(), getAmountRawOrAllString(), cardString, dest) :
                        fields.hasGroups() ? TEXT.act_zXFromYToZ(getActionTitle(), getAmountRawOrAllString(), cardString, fields.getGroupString(), dest)
                                : TEXT.act_zToX(getActionTitle(), TEXT.subjects_thisCard(), dest);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return EUIRM.strings.verbNoun(getActionTitle(), TEXT.subjects_x) + " " + TEXT.cond_doX(TEXT.subjects_x);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return isForced() ? PGR.core.strings.subjects_card : fields.getFullCardStringSingular();
    }

    @Override
    public String getText(PCLCardTarget perspective, boolean addPeriod) {
        return getMoveString(addPeriod) + LocalizedStrings.PERIOD + (childEffect != null ? (" " +
                (isChildEffectUsingParent() ? capital(childEffect.getText(perspective, addPeriod), true) :
                        (TEXT.cond_xPerY(capital(childEffect.getText(perspective, false), true), EUIRM.strings.nounVerb(getSubText(perspective), getActionPast())) + PCLCoreStrings.period(addPeriod))
                )) : "");
    }

    protected boolean isChildEffectUsingParent() {
        return childEffect != null && (childEffect.useParent || childEffect instanceof PMultiBase && EUIUtils.all(((PMultiBase<?>) childEffect).getSubEffects(), c -> c.useParent));
    }

    // Useparent on the child should also cause a "forced" filter
    protected boolean isForced() {
        return fields.forced || isChildEffectUsingParent() || fields.origin != PCLCardSelection.Manual;
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerRequired(editor);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        order.add(createPileAction(info))
                .addCallback(cards -> {
                    if (this.childEffect != null) {
                        info.setData(cards);
                        updateChildAmount(info, true);
                        this.childEffect.use(info, order);
                    }
                });
    }

    @Override
    public String wrapAmount(int input) {
        return extra > 0 || isForced() ? String.valueOf(input) : zeroToRangeString(input);
    }

    public abstract FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> getAction();

    public abstract EUIKeywordTooltip getActionTooltip();
}
