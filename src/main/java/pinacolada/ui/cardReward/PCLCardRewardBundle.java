package pinacolada.ui.cardReward;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.TipHelper;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.utilities.EUIFontHelper;
import pinacolada.utilities.PCLRenderHelpers;

public class PCLCardRewardBundle {
    public final AbstractCard card;
    public final ActionT1<PCLCardRewardBundle> onSelect;
    public float textOffsetX;
    public float textOffsetY;
    public float iconOffsetX;
    public float iconOffsetY;
    public Color textColor;
    public String title;
    public Texture icon;
    public String tooltipHeader;
    public String tooltipBody;
    public Hitbox tooltipHB;
    public int amount;

    public PCLCardRewardBundle(AbstractCard card, ActionT1<PCLCardRewardBundle> onSelect) {
        this.card = card;
        this.onSelect = onSelect;
        this.tooltipHB = new Hitbox(0, 0, AbstractCard.RAW_W, AbstractCard.RAW_H);
    }

    public void acquired() {
        if (onSelect != null) {
            onSelect.invoke(this);
        }
    }

    public void open() {

    }

    public void render(SpriteBatch sb) {
        BitmapFont font = EUIFontHelper.buttonFont;
        font.getData().setScale(card.drawScale * 0.8f);
        PCLRenderHelpers.drawOnCardAuto(sb, card, icon, iconOffsetX, iconOffsetY, icon.getWidth(), icon.getHeight());
        PCLRenderHelpers.writeOnCard(sb, card, font, title, textOffsetX, textOffsetY, textColor);
        PCLRenderHelpers.resetFont(font);
        tooltipHB.render(sb);
    }

    public PCLCardRewardBundle setAmount(int amount) {
        this.amount = amount;

        return this;
    }

    public PCLCardRewardBundle setIcon(Texture icon, float iconOffsetX, float iconOffsetY) {
        this.icon = icon;
        this.iconOffsetX = iconOffsetX;
        this.iconOffsetY = iconOffsetY;

        return this;
    }

    public PCLCardRewardBundle setText(String text, Color textColor, float textOffsetX, float textOffsetY) {
        this.title = text;
        this.textColor = textColor.cpy();
        this.textOffsetX = textOffsetX;
        this.textOffsetY = textOffsetY;

        return this;
    }

    public PCLCardRewardBundle setTooltip(String header, String body) {
        this.tooltipHeader = header;
        this.tooltipBody = body;

        return this;
    }

    public void update() {
        if (tooltipBody != null) {
            tooltipHB.resize(card.drawScale * AbstractCard.IMG_WIDTH, card.drawScale * AbstractCard.IMG_HEIGHT * 0.15f);
            tooltipHB.move(card.current_x, card.current_y + (textOffsetY * card.drawScale * Settings.scale));
            tooltipHB.update();

            if (tooltipHB.hovered) {
                TipHelper.renderGenericTip(tooltipHB.x + tooltipHB.width * 0.7f, tooltipHB.cY, tooltipHeader, tooltipBody);
            }
        }
    }
}
