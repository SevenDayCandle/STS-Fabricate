package pinacolada.ui.editor.card;

import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.TextureCache;
import extendedui.ui.hitboxes.EUIHitbox;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PPrimary;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.special.primary.PCardPrimary_GainBlock;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomEffectPage;

public class PCLCustomBlockEffectPage extends PCLCustomEffectPage {
    public PCLCustomBlockEffectPage(PCLCustomEditEntityScreen<?, ?> screen, EUIHitbox hb, int index, String title, ActionT1<PSkill<?>> onUpdate) {
        super(screen, hb, index, title, onUpdate);
    }

    public PPrimary<?> makeRootSkill() {
        return new PCardPrimary_GainBlock();
    }

    @Override
    public TextureCache getTextureCache() {
        return PCLCoreImages.Menu.editorBlock;
    }

    public PSkill<?> getSourceEffect() {
        PSkill<?> base = screen instanceof PCLCustomCardEditCardScreen ? ((PCLCustomCardEditCardScreen) screen).currentBlock : null;
        return base != null ? base.makeCopy() : null;
    }
}