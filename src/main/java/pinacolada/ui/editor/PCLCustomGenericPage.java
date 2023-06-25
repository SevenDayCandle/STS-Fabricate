package pinacolada.ui.editor;

import extendedui.ui.TextureCache;
import extendedui.ui.controls.EUICanvas;

public abstract class PCLCustomGenericPage extends EUICanvas {
    public String getIconText() {
        return "";
    }

    public void onOpen() {

    }

    abstract public TextureCache getTextureCache();

    abstract public String getTitle();

    abstract public void refresh();
}
