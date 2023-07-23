package pinacolada.skills.skills.base.primary;

import com.megacrit.cardcrawl.relics.AbstractRelic;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.providers.ClickableProvider;
import pinacolada.powers.PCLClickableUse;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardGeneric;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PTrigger;

@VisibleSkill
public class PTrigger_Interactable extends PTrigger {

    public static final PSkillData<PField_CardGeneric> DATA = register(PTrigger_Interactable.class, PField_CardGeneric.class, -1, DEFAULT_MAX)
            .selfTarget();

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

    // No-Op, should not subscribe children
    @Override
    public void subscribeChildren() {
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        String sub = amount > 1 || fields.not ? super.getSubText(perspective) : "";
        String main = source instanceof AbstractRelic ? PCLCoreStrings.colorString("o", PGR.core.strings.misc_rightClick) : PGR.core.tooltips.interactable.title;
        return sub.isEmpty() ? main : sub + COMMA_SEPARATOR + main;
    }
}
