package pinacolada.ui.editor.card;

import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.TextureCache;
import extendedui.ui.hitboxes.EUIHitbox;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomEffectPage;

public class PCLCustomBlockEffectPage extends PCLCustomEffectPage {
    public PCLCustomBlockEffectPage(PCLCustomEditEntityScreen<?, ?> screen, EUIHitbox hb, int index, String title, ActionT1<PSkill<?>> onUpdate) {
        super(screen, hb, index, title, onUpdate);
    }

    public PSkill<?> getSourceEffect() {
        PSkill<?> base = screen instanceof PCLCustomCardEditScreen ? ((PCLCustomCardEditScreen) screen).currentBlock : null;
        return base != null ? base.makeCopy() : null;
    }

    @Override
    public TextureCache getTextureCache() {
        return PCLCoreImages.Menu.editorBlock;
    }
}
