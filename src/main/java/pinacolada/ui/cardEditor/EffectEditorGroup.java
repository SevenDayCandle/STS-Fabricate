package pinacolada.ui.cardEditor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.interfaces.delegates.FuncT0;
import extendedui.ui.EUIBase;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.OriginRelativeHitbox;
import pinacolada.skills.PSkill;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class EffectEditorGroup<T extends PSkill<?>> extends EUIBase
{
    protected ArrayList<T> lowerEffects = new ArrayList<>();
    protected ArrayList<PCLCustomCardEffectEditor<T>> editors = new ArrayList<>();
    protected FuncT0<List<? extends T>> listFunc = this::getAllEffects;
    protected final Class<? extends PSkill> className;
    protected final String title;
    protected final PCLCustomCardEffectPage editor;

    public EffectEditorGroup(PCLCustomCardEffectPage editor, Class<? extends PSkill> className, String title)
    {
        this.editor = editor;
        this.className = className;
        this.title = title;
    }

    // Ensure that an editor exists for every subeffect for this card. Should be called after deconstructing the card JSON effect
    public void syncWithLower()
    {
        for (int i = 0; i < lowerEffects.size(); i++)
        {
            PCLCustomCardEffectEditor<T> effectEditor = new PCLCustomCardEffectEditor<T>(this, new OriginRelativeHitbox(editor.hb, PCLCustomCardEffectPage.MENU_WIDTH, PCLCustomCardEffectPage.MENU_HEIGHT, 0, 0), i);
            editors.add(effectEditor);
        }
    }

    // Add a subeffect to this joint effect, and select the first effect in the list to prevent the user from saving a null effect
    public PCLCustomCardEffectEditor<T> addEffectSlot()
    {
        lowerEffects.add(null);
        PCLCustomCardEffectEditor<T> effectEditor = new PCLCustomCardEffectEditor<T>(this, new OriginRelativeHitbox(editor.hb, PCLCustomCardEffectPage.MENU_WIDTH, PCLCustomCardEffectPage.MENU_HEIGHT, 0, 0), editors.size());
        editors.add(effectEditor);
        if (effectEditor.effects.size() > 0)
        {
            effectEditor.effects.setSelectedIndex(0);
        }
        editor.refresh();
        return effectEditor;
    }

    public void removeEffectSlot(int index)
    {
        if (lowerEffects.size() > index && editors.size() > index)
        {
            lowerEffects.remove(index);
            editors.remove(index);

            // Update editor indexes to reflect changes in the effects
            for (int i = 0; i < editors.size(); i++)
            {
                editors.get(i).updateIndex(i);
            }

            editor.refresh();
        }
    }

    public float reposition(float offsetY)
    {
        float offset = offsetY;
        for (PCLCustomCardEffectEditor<T> editor : editors)
        {
            editor.hb.setOffsetY(offset);
            editor.hb.update();
            offset += PCLCustomCardEffectPage.OFFSET_EFFECT * 2 + editor.getAdditionalHeight();
            for (EUIHoverable element : editor.activeElements)
            {
                element.hb.update();
            }
        }
        return offset;
    }

    public List<T> getAllEffects()
    {
        return (List<T>) PSkill.getEligibleEffects(editor.screen.getBuilder().cardColor, className);
    }

    public void setListFunc(FuncT0<List<? extends T>> listFunc)
    {
        this.listFunc = listFunc;
        for (PCLCustomCardEffectEditor<T> ce : editors)
        {
            ce.effects.setItems(ce.getEffects());
            ce.refresh();
        }
    }

    public void refresh()
    {
        for (PCLCustomCardEffectEditor<T> ce : editors)
        {
            ce.refresh();
        }
    }

    public void updateImpl()
    {
        for (PCLCustomCardEffectEditor<T> ce : editors)
        {
            ce.update();
        }
    }

    public void renderImpl(SpriteBatch sb)
    {
        for (PCLCustomCardEffectEditor<T> ce : editors)
        {
            ce.render(sb);
        }
    }
}
