package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUITooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.ListSelection;

public abstract class PCond_DoBranch extends PCond_Branch<PField_CardCategory, AbstractCard> {

    public PCond_DoBranch(PSkillData<PField_CardCategory> data, PSkillSaveData content) {
        super(data, content);
    }

    public PCond_DoBranch(PSkillData<PField_CardCategory> data) {
        super(data);
    }

    public PCond_DoBranch(PSkillData<PField_CardCategory> data, PCLCardTarget target, int amount) {
        super(data, target, amount);
    }

    public PCond_DoBranch(PSkillData<PField_CardCategory> data, PCLCardTarget target, int amount, PCLCardGroupHelper... groups) {
        super(data, target, amount);
        fields.setCardGroup(groups);
    }

    @Override
    public boolean matchesBranch(AbstractCard c, int i, PCLUseInfo info) {
        boolean valid = true;
        if (fields.types.size() > 0) {
            AbstractCard.CardType type = i < fields.types.size() ? fields.types.get(i) : null;
            valid = type != null ? c.type == type : !fields.types.contains(c.type);
        }
        if (fields.affinities.size() > 0) {
            PCLAffinity affinity = i < fields.affinities.size() ? fields.affinities.get(i) : null;
            valid = valid & affinity != null ? GameUtilities.hasAffinity(c, affinity) : EUIUtils.all(fields.affinities, af -> !GameUtilities.hasAffinity(c, affinity));
        }
        return valid;
    }

    @Override
    public String getText(boolean addPeriod) {
        if (this.childEffect instanceof PMultiBase) {
            return getSubText() + EFFECT_SEPARATOR +
                    TEXT.cond_doForEach() + ": | " + getEffectTexts(addPeriod);
        }
        return getSubText();
    }

    public String getQualifier(int i) {
        PCLAffinity affinity = i < fields.affinities.size() ? fields.affinities.get(i) : null;
        AbstractCard.CardType type = i < fields.types.size() ? fields.types.get(i) : null;
        return affinity != null && type != null ? affinity.getTooltip().getTitleOrIcon() + " " + EUIGameUtils.textForType(type) :
                affinity != null ? affinity.getTooltip().getTitleOrIcon() :
                        type != null ? EUIGameUtils.textForType(type) : TEXT.subjects_other;
    }

    protected String getActionTitle() {
        return getActionTooltip().title;
    }

    @Override
    public String getAmountRawOrAllString() {
        return baseAmount <= 0 ? fields.forced ? TEXT.subjects_all : TEXT.subjects_any
                : extra > 0 ? TEXT.subjects_xOfY(getExtraRawString(), getAmountRawString())
                : (fields.forced || fields.origin != PCLCardSelection.Manual) ? getAmountRawString() : getRangeToAmountRawString();
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return EUIRM.strings.verbNoun(getActionTitle(), TEXT.subjects_x) + EFFECT_SEPARATOR + StringUtils.capitalize(TEXT.cond_doForEach());
    }

    @Override
    public String getSubText() {
        return !fields.groupTypes.isEmpty() ? TEXT.act_genericFrom(getActionTitle(), getAmountRawOrAllString(), fields.getShortCardString(), fields.getGroupString())
                : EUIRM.strings.verbNumNoun(getActionTitle(), getAmountRawOrAllString(), fields.getShortCardString());
    }

    public abstract EUITooltip getActionTooltip();

    @Override
    public void use(PCLUseInfo info) {
        getActions().add(fields.getGenericPileAction(getAction(), info, extra))
                .addCallback(cards -> {
                    if (this.childEffect != null) {
                        info.setData(cards);
                        branch(info, cards);
                    }
                });
    }

    public abstract FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> getAction();
}
