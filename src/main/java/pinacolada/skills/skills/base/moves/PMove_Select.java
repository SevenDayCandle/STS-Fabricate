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

public abstract class PMove_Select<T extends PField_CardGeneric> extends PCallbackMove<T> {
    public PMove_Select(PSkillData<T> data, PSkillSaveData content) {
        super(data, content);
    }

    public PMove_Select(PSkillData<T> data, int amount, PCLCardGroupHelper... h) {
        super(data, PCLCardTarget.None, amount);
        fields.setCardGroup(h);
    }

    public PMove_Select(PSkillData<T> data, int amount, int extra, PCLCardGroupHelper... h) {
        super(data, PCLCardTarget.None, amount, extra);
        fields.setCardGroup(h);
    }

    public PMove_Select(PSkillData<T> data, PCLCardTarget target, int amount, PCLCardGroupHelper... h) {
        super(data, target, amount);
        fields.setCardGroup(h);
    }

    public PMove_Select(PSkillData<T> data, PCLCardTarget target, int amount, int extra, PCLCardGroupHelper... h) {
        super(data, target, amount, extra);
        fields.setCardGroup(h);
    }

    protected String getActionTitle() {
        return getActionTooltip().title;
    }

    public PCLCardGroupHelper getDestinationGroup() {
        return null;
    }

    @Override
    public String getAmountRawOrAllString() {
        return shouldActAsAll() ? fields.forced ? TEXT.subjects_all : TEXT.subjects_any
                : extra > 0 ? TEXT.subjects_xOfY(getExtraRawString(), getAmountRawString())
                : (fields.forced || fields.origin != PCLCardSelection.Manual) ? getAmountRawString() : getRangeToAmountRawString();
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return EUIRM.strings.verbNoun(getActionTitle(), TEXT.subjects_x);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        String fcs = fields.getFullCardString(extra > 1 ? getExtraRawString() : getAmountRawString());
        if (fields.destination == PCLCardSelection.Manual || getDestinationGroup() == null) {
            return useParent ? EUIRM.strings.verbNoun(getActionTitle(), getInheritedThemString()) :
                    fields.shouldHideGroupNames() ? TEXT.act_generic3(getActionTitle(), getAmountRawOrAllString(), fcs) :
                            fields.hasGroups() ? TEXT.act_zXFromY(getActionTitle(), getAmountRawOrAllString(), fcs, fields.getGroupString())
                                    : EUIRM.strings.verbNoun(getActionTitle(), TEXT.subjects_thisCard());
        }
        String dest = fields.getDestinationString(getDestinationGroup().name);
        return useParent ? TEXT.act_zToX(getActionTitle(), getInheritedThemString(), dest) :
                fields.shouldHideGroupNames() ? TEXT.act_zXToY(getActionTitle(), getAmountRawOrAllString(), fcs, dest) :
                        fields.hasGroups() ? TEXT.act_zXFromYToZ(getActionTitle(), getAmountRawOrAllString(), fcs, fields.getGroupString(), dest)
                                : TEXT.act_zToX(getActionTitle(), TEXT.subjects_thisCard(), dest);
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
                        this.childEffect.use(info, order);
                    }
                });
    }

    public PMove_Select<T> useParentForce() {
        fields.setForced(true);
        useParent(true);
        return this;
    }

    public abstract FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> getAction();

    public abstract EUITooltip getActionTooltip();
}
