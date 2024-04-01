package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PCallbackMove;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.List;

public abstract class PMove_Modify<T extends PField_CardCategory> extends PCallbackMove<T> {
    public PMove_Modify(PSkillData<T> data, PSkillSaveData content) {
        super(data, content);
    }

    public PMove_Modify(PSkillData<T> data, int amount, int extraAmount, PCLCardGroupHelper... groups) {
        super(data, PCLCardTarget.None, amount, extraAmount);
        fields.setCardGroup(groups);
    }

    public boolean canCardPass(AbstractCard c) {
        return fields.getFullCardFilter().invoke(c);
    }

    public void cardAction(List<AbstractCard> cards, PCLUseInfo info, PCLActions order) {
        for (AbstractCard c : cards) {
            getAction(info, order).invoke(c);
        }
    }

    public String getBasicAddString(Object requestor) {
        String giveString = getObjectText(requestor);
        if (fields.not) {
            return useParent ? TEXT.act_setOf(giveString, getInheritedThemString(), getAmountRawString(requestor)) :
                    fields.hasGroups() ?
                            TEXT.act_setOfFrom(giveString, EUIRM.strings.numNoun(baseExtra <= 0 ? TEXT.subjects_all : getExtraRawString(requestor), fields.getFullCardStringForValue(getExtraRawString(requestor))), fields.getGroupString(), getAmountRawString(requestor)) :
                            TEXT.act_setOf(giveString, TEXT.subjects_thisCard(), getAmountRawString(requestor));
        }
        if (amount >= 0) {
            return useParent ? TEXT.act_increasePropertyBy(giveString, getInheritedThemString(), getAmountRawString(requestor)) :
                    fields.hasGroups() ?
                            TEXT.act_increasePropertyFromBy(giveString, EUIRM.strings.numNoun(baseExtra <= 0 ? TEXT.subjects_all : getExtraRawString(requestor), fields.getFullCardStringForValue(getExtraRawString(requestor))), fields.getGroupString(), getAmountRawString(requestor)) :
                            TEXT.act_increasePropertyBy(giveString, TEXT.subjects_thisCard(), getAmountRawString(requestor));
        }
        return useParent ? TEXT.act_reducePropertyBy(giveString, getInheritedThemString(), getAmountRawString(requestor)) :
                fields.hasGroups() ?
                        TEXT.act_reducePropertyFromBy(giveString, EUIRM.strings.numNoun(baseExtra <= 0 ? TEXT.subjects_all : getExtraRawString(requestor), fields.getFullCardStringForValue(getExtraRawString(requestor))), fields.getGroupString(), getAmountRawString(requestor)) :
                        TEXT.act_reducePropertyBy(giveString, TEXT.subjects_thisCard(), getAmountRawString(requestor));
    }

    public String getBasicGiveString(Object requestor) {
        return getBasicGiveString(getNumericalObjectText(requestor), requestor);
    }

    public String getBasicGiveString(String giveString, Object requestor) {
        String destString = EUIRM.strings.numNoun(baseExtra <= 0 ? TEXT.subjects_all : getExtraRawString(requestor), fields.getFullCardStringForValue(getExtraRawString(requestor)));
        return useParent ? TEXT.act_giveTarget(getInheritedThemString(), giveString) :
                fields.hasGroups() ?
                        TEXT.act_giveFrom(destString, fields.getGroupString(), giveString) :
                        TEXT.act_giveTarget(TEXT.subjects_thisCard(), giveString);
    }

    public String getBasicRemoveString(Object requestor) {
        return getBasicGiveString(requestor);
    }

    public String getBasicRemoveString(String giveString, Object requestor) {
        String destString = EUIRM.strings.numNoun(baseExtra <= 0 ? TEXT.subjects_all : getExtraRawString(requestor), fields.getFullCardStringForValue(getExtraRawString(requestor)));
        return useParent ? TEXT.act_removeFrom(giveString, getInheritedThemString()) :
                fields.hasGroups() ?
                        TEXT.act_removeFromPlace(giveString, destString, fields.getGroupString()) :
                        TEXT.act_removeFrom(giveString, TEXT.subjects_thisCard());
    }

    public String getNumericalObjectText(Object requestor) {
        return EUIRM.strings.numNoun(getAmountRawString(requestor), getObjectText(requestor));
    }

    public String getObjectSampleText() {
        return getObjectText(null);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_giveTarget(TEXT.subjects_card, getObjectSampleText());
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return getBasicGiveString(requestor);
    }

    public String getUntilPlayedString() {
        return EUIUtils.capitalize(TEXT.subjects_untilX("", PGR.core.tooltips.play.past()).trim());
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        registerUseParentBoolean(editor);
        fields.registerNotBoolean(editor, TEXT.cedit_exact, null);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> callback) {
        boolean selectAll = baseExtra <= 0 || useParent;
        order.selectFromPile(getName(), selectAll ? Integer.MAX_VALUE : extra, fields.getCardGroup(info))
                .setFilter(this::canCardPass)
                .setOptions((selectAll || fields.groupTypes.isEmpty() ? PCLCardSelection.Random : fields.origin), !fields.forced)
                .addCallback(cards -> {
                    info.setData(cards);
                    callback.invoke(info);
                    cardAction(cards, info, order);
                    if (this.childEffect != null) {
                        this.childEffect.use(info, order);
                    }
                });
    }

    public PMove_Modify<T> useParentForce() {
        fields.setForced(true);
        useParent(true);
        return this;
    }

    @Override
    public String wrapTextAmountSelf(int input) {
        return input > 0 && !fields.not ? "+" + input : String.valueOf(input);
    }

    public abstract ActionT1<AbstractCard> getAction(PCLUseInfo info, PCLActions order);

    public abstract String getObjectText(Object requestor);
}
