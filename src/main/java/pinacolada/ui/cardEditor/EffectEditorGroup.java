package pinacolada.ui.cardEditor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.interfaces.delegates.FuncT0;
import extendedui.ui.EUIBase;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.OriginRelativeHitbox;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class EffectEditorGroup<T extends PSkill<?>> extends EUIBase {
    protected final Class<? extends PSkill> className;
    protected final String title;
    protected final PCLCustomEffectPage editor;
    protected ArrayList<T> lowerEffects = new ArrayList<>();
    protected ArrayList<PCLCustomEffectEditor<T>> editors = new ArrayList<>();
    protected FuncT0<List<? extends T>> listFunc = this::getAllEffects;

    public EffectEditorGroup(PCLCustomEffectPage editor, Class<? extends PSkill> className, String title) {
        this.editor = editor;
        this.className = className;
        this.title = title;
    }

    // Add a subeffect to this joint effect, and select the first effect in the list to prevent the user from saving a null effect
    // We can call construct effect directly here because this is itself called in a callback
    public PCLCustomEffectEditor<T> addEffectSlot() {
        lowerEffects.add(null);
        PCLCustomEffectEditor<T> effectEditor = new PCLCustomEffectEditor<T>(this, new OriginRelativeHitbox(editor.hb, PCLCustomEffectPage.MENU_WIDTH, PCLCustomEffectPage.MENU_HEIGHT, 0, 0), editors.size());
        editors.add(effectEditor);
        if (effectEditor.effects.size() > 0) {
            effectEditor.effects.setSelectedIndex(0);
        }
        editor.constructEffect();
        editor.refresh();
        return effectEditor;
    }

    public List<T> getAllEffects() {
        return (List<T>) (PGR.config.showIrrelevantProperties.get() ? PSkill.getEligibleEffects(className) : PSkill.getEligibleEffects(className, editor.screen.getBuilder().cardColor));
    }

    public void refresh() {
        for (PCLCustomEffectEditor<T> ce : editors) {
            ce.refresh();
        }
    }

    // Remove a subeffect from this joint effect
    // We can call construct effect directly here because this is itself called in a callback
    public void removeEffectSlot(int index) {
        if (lowerEffects.size() > index && editors.size() > index) {
            lowerEffects.remove(index);
            editors.remove(index);

            // Update editor indexes to reflect changes in the effects
            for (int i = 0; i < editors.size(); i++) {
                editors.get(i).updateIndex(i);
            }
            editor.constructEffect();
            editor.refresh();
        }
    }

    public void renderImpl(SpriteBatch sb) {
        for (PCLCustomEffectEditor<T> ce : editors) {
            ce.render(sb);
        }
    }

    public void updateImpl() {
        for (PCLCustomEffectEditor<T> ce : editors) {
            ce.update();
        }
    }

    public float reposition(float offsetY) {
        float offset = offsetY;
        for (PCLCustomEffectEditor<T> editor : editors) {
            editor.hb.setOffsetY(offset);
            editor.hb.update();
            offset += PCLCustomEffectPage.OFFSET_EFFECT * 2 + editor.getAdditionalHeight();
            for (EUIHoverable element : editor.activeElements) {
                element.hb.update();
            }
        }
        return offset;
    }

    public void setListFunc(FuncT0<List<? extends T>> listFunc) {
        this.listFunc = listFunc;
        for (PCLCustomEffectEditor<T> ce : editors) {
            ce.effects.setItems(ce.getEffects());
            ce.refresh();
        }
    }

    // Ensure that an editor exists for every subeffect for this card. Should be called after deconstructing the card JSON effect
    public void syncWithLower() {
        for (int i = 0; i < lowerEffects.size(); i++) {
            PCLCustomEffectEditor<T> effectEditor = new PCLCustomEffectEditor<T>(this, new OriginRelativeHitbox(editor.hb, PCLCustomEffectPage.MENU_WIDTH, PCLCustomEffectPage.MENU_HEIGHT, 0, 0), i);
            editors.add(effectEditor);
        }
    }
}
