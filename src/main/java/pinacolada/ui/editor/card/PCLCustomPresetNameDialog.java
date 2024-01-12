package pinacolada.ui.editor.card;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.leaderboards.LeaderboardScreen;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import extendedui.EUIRM;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIDialog;
import extendedui.ui.controls.EUITextBoxInput;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.resources.PGR;

public class PCLCustomPresetNameDialog extends EUIDialog<String> {
    protected float MENU_WIDTH = scale(190);
    protected float MENU_HEIGHT = scale(32);
    protected EUITextBoxInput nameInput;

    public PCLCustomPresetNameDialog(String headerText) {
        this(headerText, "");
    }

    public PCLCustomPresetNameDialog(String headerText, String descriptionText) {
        this(headerText, descriptionText, scale(330), scale(490));
    }

    public PCLCustomPresetNameDialog(String headerText, String descriptionText, float w, float h) {
        this(new EUIHitbox(Settings.WIDTH / 2.0F - w / 2f, Settings.HEIGHT / 2.0F - h / 2f, w, h), ImageMaster.OPTION_CONFIRM, headerText, descriptionText);
    }

    public PCLCustomPresetNameDialog(EUIHitbox hb, Texture backgroundTexture, String headerText, String descriptionText) {
        super(hb, backgroundTexture, headerText, descriptionText);
        nameInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.images.longInput.texture(),
                new EUIHitbox(hb.x + hb.width / 4, hb.y + hb.height / 1.8f, MENU_WIDTH, MENU_HEIGHT))
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, LeaderboardScreen.TEXT[7])
                .setHeaderSpacing(1.1f)
                .setBackgroundTexture(EUIRM.images.longInput.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.05f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f)
                .setFont(FontHelper.cardTitleFont, 0.7f)
                .setTooltip(LeaderboardScreen.TEXT[7], PGR.core.strings.cetut_nameLanguage);
    }

    protected EUIButton getCancelButton() {
        return new EUIButton(ImageMaster.OPTION_NO,
                new RelativeHitbox(hb, scale(135), scale(70), hb.width * 0.85f, hb.height * 0.15f))
                .setLabel(EUIFontHelper.cardTitleFontNormal, 0.8f, GridCardSelectScreen.TEXT[1])
                .setOnClick(() -> {
                    if (onComplete != null) {
                        onComplete.invoke(getCancelValue());
                    }
                });
    }

    @Override
    public String getCancelValue() {
        return null;
    }

    protected EUIButton getConfirmButton() {
        return new EUIButton(ImageMaster.OPTION_YES,
                new RelativeHitbox(hb, scale(135), scale(70), hb.width * 0.15f, hb.height * 0.15f))
                .setLabel(EUIFontHelper.cardTitleFontNormal, 0.8f, GridCardSelectScreen.TEXT[0])
                .setOnClick(() -> {
                    if (onComplete != null) {
                        onComplete.invoke(getConfirmValue());
                    }
                });
    }

    @Override
    public String getConfirmValue() {
        return nameInput.label.text;
    }

    public void open(String name) {
        setActive(true);
        nameInput.setText(name);
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        this.nameInput.tryRender(sb);
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        this.nameInput.tryUpdate();
    }
}
