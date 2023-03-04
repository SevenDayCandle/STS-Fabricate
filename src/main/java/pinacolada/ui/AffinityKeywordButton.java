package pinacolada.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.EUI;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.EUIBase;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.hitboxes.EUIHitbox;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.affinity.GenericFlashEffect;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.PCLRenderHelpers;

public class AffinityKeywordButton extends EUIBase
{
    public static final float ICON_SIZE = scale(48);
    protected static final Color PANEL_COLOR = new Color(0.3f, 0.3f, 0.3f, 1f);
    protected static final TextureCache STAR_TEXTURE = PCLCoreImages.starFg;
    public PCLAffinity type;
    public EUIButton backgroundButton;
    public int currentLevel;
    public float borderRotation;
    public boolean showBorders = true;
    public boolean showNumbers = false;
    protected ActionT1<AffinityKeywordButton> onClick;
    protected ActionT1<AffinityKeywordButton> onRightClick;
    protected Texture borderTexture;
    protected Texture borderBGTexture;
    protected Texture borderFGTexture;
    protected float radiusBG;

    public AffinityKeywordButton(EUIHitbox hb, PCLAffinity affinity)
    {
        type = affinity;

        backgroundButton = new EUIButton(affinity.getIcon(), hb)
                .setColor(currentLevel == 0 ? PANEL_COLOR : Color.WHITE)
                .setOnClick(() -> {
                    if (this.onClick != null)
                    {
                        this.onClick.invoke(this);
                    }
                })
                .setOnRightClick(() -> {
                    if (this.onRightClick != null)
                    {
                        this.onRightClick.invoke(this);
                    }
                });
        radiusBG = backgroundButton.hb.width;

    }

    public void flash()
    {
        PCLEffects.List.add(new GenericFlashEffect(this, true));
    }

    public void reset(boolean invoke)
    {
        currentLevel = 0;
        backgroundButton.setColor(PANEL_COLOR);
        if (this.onClick != null && invoke)
        {
            this.onClick.invoke(this);
        }
    }

    public AffinityKeywordButton setAffinity(PCLAffinity affinity)
    {
        type = affinity;
        backgroundButton.setBackground(affinity.getIcon());

        return this;
    }

    public AffinityKeywordButton setAlpha(float currentAlpha, float targetAlpha)
    {
        backgroundButton.setAlpha(currentAlpha, targetAlpha);
        return this;
    }

    public AffinityKeywordButton setLevel(int level)
    {
        currentLevel = level;
        backgroundButton.setColor(currentLevel == 0 ? PANEL_COLOR : Color.WHITE);
        switch (currentLevel)
        {
            case 1:
                borderTexture = PGR.core.images.borderNormal.texture();
                borderBGTexture = null;
                borderFGTexture = null;
                radiusBG = backgroundButton.hb.width;
                break;
            case 2:
                borderTexture = PGR.core.images.borderWeak.texture();
                borderBGTexture = PGR.core.images.borderBG3.texture();
                borderFGTexture = null;
                radiusBG = backgroundButton.hb.width;
                break;
            case 3:
                borderTexture = PGR.core.images.borderHighlight.texture();
                borderBGTexture = PGR.core.images.borderBG2.texture();
                borderFGTexture = PGR.core.images.borderFG.texture();
                radiusBG = backgroundButton.hb.width * 1.125f;
                break;
            default:
                borderTexture = null;
                borderBGTexture = null;
                borderFGTexture = null;
                radiusBG = backgroundButton.hb.width;
        }
        return this;
    }

    public AffinityKeywordButton setOnClick(ActionT1<AffinityKeywordButton> onClick)
    {
        this.onClick = onClick;

        return this;
    }

    public AffinityKeywordButton setOnRightClick(ActionT1<AffinityKeywordButton> onRightClick)
    {
        this.onRightClick = onRightClick;

        return this;
    }

    public AffinityKeywordButton setOptions(boolean showBorders, boolean showNumbers)
    {
        this.showBorders = showBorders;
        this.showNumbers = showNumbers;

        return this;
    }

    @Override
    public void updateImpl()
    {
        backgroundButton.setInteractable(PCLEffects.isEmpty()).updateImpl();
        if (currentLevel > 2)
        {
            borderRotation = EUI.timeMulti(-20);
        }
        else if (currentLevel > 1)
        {
            borderRotation = EUI.timeMulti(8);
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        if (borderBGTexture != null && showBorders)
        {
            PCLRenderHelpers.drawCentered(sb,
                    backgroundButton.background.color,
                    borderBGTexture,
                    backgroundButton.hb.cX,
                    backgroundButton.hb.cY,
                    radiusBG,
                    radiusBG,
                    0.78f,
                    borderRotation);
        }
        backgroundButton.renderImpl(sb);
        if (showBorders)
        {
            if (borderTexture != null)
            {
                sb.draw(borderTexture,
                        backgroundButton.hb.x, backgroundButton.hb.y,
                        backgroundButton.hb.width / 2f, backgroundButton.hb.height / 2f,
                        backgroundButton.hb.width, backgroundButton.hb.height,
                        backgroundButton.background.scaleX, backgroundButton.background.scaleY,
                        borderRotation, 0, 0,
                        borderTexture.getWidth(), borderTexture.getHeight(), false, false
                );
            }
            if (borderFGTexture != null)
            {
                sb.draw(borderFGTexture,
                        backgroundButton.hb.x, backgroundButton.hb.y,
                        backgroundButton.hb.width / 2f, backgroundButton.hb.height / 2f,
                        backgroundButton.hb.width, backgroundButton.hb.height,
                        backgroundButton.background.scaleX, backgroundButton.background.scaleY,
                        -borderRotation, 0, 0,
                        borderFGTexture.getWidth(), borderFGTexture.getHeight(), false, false
                );
            }
        }
        if (type == PCLAffinity.Star)
        {
            Texture star = STAR_TEXTURE.texture();
            sb.draw(star,
                    backgroundButton.hb.x, backgroundButton.hb.y,
                    backgroundButton.hb.width / 2f, backgroundButton.hb.height / 2f,
                    backgroundButton.hb.width, backgroundButton.hb.height,
                    backgroundButton.background.scaleX, backgroundButton.background.scaleY,
                    0, 0, 0,
                    star.getWidth(), star.getHeight(), false, false
            );
        }
        if (showNumbers && currentLevel > 0)
        {
            FontHelper.renderFontRightTopAligned(sb, FontHelper.tipHeaderFont, String.valueOf(currentLevel),
                    backgroundButton.hb.x + backgroundButton.hb.width, backgroundButton.hb.cY, Settings.CREAM_COLOR);
        }
    }
}
