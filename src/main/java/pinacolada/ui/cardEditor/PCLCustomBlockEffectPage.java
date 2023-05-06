package pinacolada.ui.cardEditor;

import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.hitboxes.EUIHitbox;
import pinacolada.skills.PPrimary;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.special.primary.PCardPrimary_GainBlock;

public class PCLCustomBlockEffectPage extends PCLCustomEffectPage {
    public PCLCustomBlockEffectPage(PCLCustomEditEntityScreen<?, ?> screen, EUIHitbox hb, int index, String title, ActionT1<PSkill<?>> onUpdate) {
        super(screen, hb, index, title, onUpdate);
    }

    public PPrimary<?> makeRootSkill()
    {
        return new PCardPrimary_GainBlock();
    }
}
