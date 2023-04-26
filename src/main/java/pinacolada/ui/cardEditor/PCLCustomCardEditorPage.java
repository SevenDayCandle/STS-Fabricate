package pinacolada.ui.cardEditor;

import extendedui.ui.TextureCache;
import extendedui.ui.controls.EUICanvas;

public abstract class PCLCustomCardEditorPage extends EUICanvas {
    public String getIconText() {
        return "";
    }

    abstract public TextureCache getTextureCache();

    abstract public String getTitle();

    abstract public void refresh();
}
