package pinacolada.ui.editor;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.EUICanvas;

public abstract class PCLCustomGenericPage extends EUICanvas {
    protected static final float START_X = screenW(0.25f);
    protected static final float PAD_X = AbstractCard.IMG_WIDTH * 0.75f + Settings.CARD_VIEW_PAD_X;
    protected static final float PAD_Y = scale(10);
    public static final float MENU_WIDTH = scale(160);
    public static final float MENU_HEIGHT = scale(40);
    public static final float SPACING_WIDTH = screenW(0.02f);

    public String getIconText() {
        return "";
    }

    public void onOpen() {

    }

    abstract public TextureCache getTextureCache();

    abstract public String getTitle();

    abstract public void refresh();
}
