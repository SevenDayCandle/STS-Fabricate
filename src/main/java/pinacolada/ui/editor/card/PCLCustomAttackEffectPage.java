package pinacolada.ui.editor.card;

import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.TextureCache;
import extendedui.ui.hitboxes.EUIHitbox;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomEffectPage;

public class PCLCustomAttackEffectPage extends PCLCustomEffectPage {
    public PCLCustomAttackEffectPage(PCLCustomEditEntityScreen<?, ?, ?> screen, EUIHitbox hb, int index, String title) {
        super(screen, hb, index, title);
    }

    @Override
    public TextureCache getTextureCache() {
        return PCLCoreImages.Menu.editorAttack;
    }
}
