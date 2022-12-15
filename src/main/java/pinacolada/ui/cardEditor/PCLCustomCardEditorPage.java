package pinacolada.ui.cardEditor;

import extendedui.ui.TextureCache;
import extendedui.ui.controls.EUICanvas;

public abstract class PCLCustomCardEditorPage extends EUICanvas
{
    abstract public String getTitle();

    abstract public void refresh();

    abstract public TextureCache getTextureCache();

    public String getIconText() {return "";}
}
