package pinacolada.ui.cardEditor;

import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.hitboxes.EUIHitbox;
import pinacolada.skills.PPrimary;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.base.primary.PTrigger_When;

public class PCLCustomPowerEffectPage extends PCLCustomEffectPage {
    public PCLCustomPowerEffectPage(PCLCustomEditEntityScreen<?, ?> screen, EUIHitbox hb, int index, String title, ActionT1<PSkill<?>> onUpdate) {
        super(screen, hb, index, title, onUpdate);
    }

    public PPrimary<?> makeRootSkill()
    {
        return new PTrigger_When();
    }
}
