package pinacolada.ui.editor.card;

import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.TextureCache;
import extendedui.ui.hitboxes.EUIHitbox;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PPrimary;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.special.primary.PCardPrimary_DealDamage;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomEffectPage;

public class PCLCustomAttackEffectPage extends PCLCustomEffectPage {
    public PCLCustomAttackEffectPage(PCLCustomEditEntityScreen<?, ?> screen, EUIHitbox hb, int index, String title, ActionT1<PSkill<?>> onUpdate) {
        super(screen, hb, index, title, onUpdate);
    }

    public PPrimary<?> makeRootSkill() {
        return new PCardPrimary_DealDamage();
    }

    @Override
    public TextureCache getTextureCache() {
        return PCLCoreImages.Menu.editorAttack;
    }

    public PSkill<?> getSourceEffect() {
        PSkill<?> base = screen instanceof PCLCustomCardEditCardScreen ? ((PCLCustomCardEditCardScreen) screen).currentDamage : null;
        return base != null ? base.makeCopy() : null;
    }
}
