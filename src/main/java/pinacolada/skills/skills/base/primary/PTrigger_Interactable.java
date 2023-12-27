package pinacolada.skills.skills.base.primary;

import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.providers.ClickableProvider;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.powers.PCLClickableUse;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardGeneric;
import pinacolada.skills.skills.PTrigger;

import java.util.Collections;

@VisibleSkill
public class PTrigger_Interactable extends PTrigger {

    public static final PSkillData<PField_CardGeneric> DATA = register(PTrigger_Interactable.class, PField_CardGeneric.class, -1, DEFAULT_MAX)
            .noTarget();

    public PTrigger_Interactable() {
        this(1);
    }

    public PTrigger_Interactable(int maxUses) {
        super(DATA, PCLCardTarget.None, maxUses);
    }

    public PTrigger_Interactable(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public PCLClickableUse getClickable(ClickableProvider provider) {
        return new PCLClickableUse(provider, getChild(), amount <= 0 ? -1 : amount, !fields.not, true);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return PGR.core.tooltips.interactable.title;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String main = (source instanceof PointerProvider && ((PointerProvider) source).getEffects().contains(this) && !(source instanceof AbstractPower)) ? PCLCoreStrings.colorString("o", PGR.core.strings.misc_rightClick) : PGR.core.tooltips.interactable.title;
        if (amount < 0) {
            return EUIRM.strings.adjNoun(TEXT.subjects_infinite, main);
        }
        String sub = amount > 1 || fields.not ? super.getSubText(perspective, requestor) : EUIUtils.EMPTY_STRING;
        return sub.isEmpty() ? main : sub + COMMA_SEPARATOR + main;
    }

    @Override
    public PTrigger_Interactable scanForTips(String source) {
        if (tips == null) {
            tips = Collections.singletonList(new EUIKeywordTooltip(PGR.core.tooltips.interactable.title, TEXT.cetut_interactable));
        }
        return this;
    }

    // No-Op, should not subscribe children
    @Override
    public void subscribeChildren() {
    }
}
