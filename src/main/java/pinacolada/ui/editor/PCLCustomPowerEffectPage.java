package pinacolada.ui.editor;

import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.TextureCache;
import extendedui.ui.hitboxes.EUIHitbox;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PPrimary;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.base.primary.PTrigger_When;

public class PCLCustomPowerEffectPage extends PCLCustomEffectPage {



    public PCLCustomPowerEffectPage(PCLCustomEditEntityScreen<?, ?> screen, EUIHitbox hb, int index, String title, ActionT1<PSkill<?>> onUpdate) {
        super(screen, hb, index, title, onUpdate);
    }

    public PPrimary<?> makeRootSkill() {
        return new PTrigger_When();
    }

    @Override
    public TextureCache getTextureCache() {
        return PCLCoreImages.Menu.editorPower;
    }

    public PSkill<?> getSourceEffect() {
        PSkill<?> base = screen.currentPowers.get(editorIndex);
        return base != null ? base.makeCopy() : null;
    }
}
