package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.PCLActions;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardGeneric;
import pinacolada.skills.skills.PCallbackMove;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

public abstract class PMove_DoCard<T extends PField_CardGeneric> extends PCallbackMove<T> {
    public PMove_DoCard(PSkillData<T> data, PSkillSaveData content) {
        super(data, content);
    }

    public PMove_DoCard(PSkillData<T> data, int amount, PCLCardGroupHelper... h) {
        super(data, PCLCardTarget.None, amount);
        fields.setCardGroup(h);
    }

    public PMove_DoCard(PSkillData<T> data, int amount, int extra, PCLCardGroupHelper... h) {
        super(data, PCLCardTarget.None, amount, extra);
        fields.setCardGroup(h);
    }

    public PMove_DoCard(PSkillData<T> data, PCLCardTarget target, int amount, PCLCardGroupHelper... h) {
        super(data, target, amount);
        fields.setCardGroup(h);
    }

    public PMove_DoCard(PSkillData<T> data, PCLCardTarget target, int amount, int extra, PCLCardGroupHelper... h) {
        super(data, target, amount, extra);
        fields.setCardGroup(h);
    }

    protected String getActionTitle() {
        return getActionTooltip().title;
    }

    @Override
    public String getAmountRawOrAllString(Object requestor) {
        return shouldActAsAll() ? fields.forced ? TEXT.subjects_all : TEXT.subjects_any
                : extra > 0 ? TEXT.subjects_xOfY(getExtraRawString(requestor), getAmountRawString(requestor))
                : getAmountRawString(requestor);
    }

    public PCLCardGroupHelper getDestinationGroup() {
        return null;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return EUIRM.strings.verbNoun(getActionTitle(), TEXT.subjects_x);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String fcs = fields.getFullCardStringForValue(extra > 1 ? getExtraRawString(requestor) : getAmountRawString(requestor));
        if (fields.destination == PCLCardSelection.Manual || getDestinationGroup() == null) {
            return useParent ? EUIRM.strings.verbNoun(getActionTitle(), getInheritedThemString()) :
                    shouldHideGroupNames() ? TEXT.act_generic3(getActionTitle(), getAmountRawOrAllString(requestor), fcs) :
                            fields.hasGroups() ? TEXT.act_zXFromY(getActionTitle(), getAmountRawOrAllString(requestor), fcs, fields.getGroupString())
                                    : EUIRM.strings.verbNoun(getActionTitle(), TEXT.subjects_thisCard());
        }
        String dest = fields.getDestinationString(getDestinationGroup().name);
        return useParent ? TEXT.act_zToX(getActionTitle(), getInheritedThemString(), dest) :
                shouldHideGroupNames() ? TEXT.act_zXToY(getActionTitle(), getAmountRawOrAllString(requestor), fcs, dest) :
                        fields.hasGroups() ? TEXT.act_zXFromYToZ(getActionTitle(), getAmountRawOrAllString(requestor), fcs, fields.getGroupString(), dest)
                                : TEXT.act_zToX(getActionTitle(), TEXT.subjects_thisCard(), dest);
    }

    public boolean shouldHideGroupNames() {
        return fields.shouldHideGroupNames();
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        registerUseParentBoolean(editor);
        fields.registerRequired(editor);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> callback) {
        fields.getGenericPileAction(getAction(), info, order, extra)
                .addCallback(cards -> {
                    info.setData(cards);
                    callback.invoke(info);
                    if (this.childEffect != null) {
                        this.childEffect.use(info, PCLActions.bottom);
                    }
                });
    }

    public PMove_DoCard<T> useParentForce() {
        fields.setForced(true);
        useParent(true);
        return this;
    }

    @Override
    public String wrapTextAmountSelf(int input) {
        return extra > 0 || fields.forced || fields.origin != PCLCardSelection.Manual ? String.valueOf(input) : TEXT.subjects_upToX(input);
    }

    public abstract FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> getAction();

    public abstract EUITooltip getActionTooltip();
}
