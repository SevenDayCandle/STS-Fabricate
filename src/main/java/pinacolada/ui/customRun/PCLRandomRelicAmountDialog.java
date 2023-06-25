package pinacolada.ui.customRun;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIDialog;
import extendedui.ui.controls.EUITextBoxNumericalInput;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.resources.PGR;

import java.util.ArrayList;

public class PCLRandomRelicAmountDialog extends EUIDialog<PCLRandomRelicAmountDialog> {
    protected EUITextBoxNumericalInput inputCards;
    protected EUITextBoxNumericalInput inputColorless;

    public PCLRandomRelicAmountDialog(String headerText) {
        this(headerText, "");
    }

    public PCLRandomRelicAmountDialog(String headerText, String descriptionText) {
        this(headerText, descriptionText, scale(300), scale(390));
    }

    public PCLRandomRelicAmountDialog(String headerText, String descriptionText, float w, float h) {
        this(new EUIHitbox(Settings.WIDTH / 2.0F - w / 2f, Settings.HEIGHT / 2.0F - h / 2f, w, h), ImageMaster.OPTION_CONFIRM, headerText, descriptionText);
    }

    public PCLRandomRelicAmountDialog(EUIHitbox hb, Texture backgroundTexture, String headerText, String descriptionText) {
        super(hb, backgroundTexture, headerText, descriptionText);
        this.inputCards = (EUITextBoxNumericalInput) new EUITextBoxNumericalInput(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(hb.x + hb.width / 4, hb.y + hb.height / 1.8f, hb.width / 2, scale(54)))
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.sui_characterCards)
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontSmall, 1f);
        this.inputColorless = (EUITextBoxNumericalInput) new EUITextBoxNumericalInput(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(hb.x + hb.width / 4, inputCards.hb.y - inputCards.hb.height * 1.2f, hb.width / 2, scale(54)))
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIGameUtils.getColorName(AbstractCard.CardColor.COLORLESS))
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontSmall, 1f);
        this.inputCards.forceSetValue(0, false);
        this.inputColorless.forceSetValue(0, false);
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
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        this.inputCards.tryRender(sb);
        this.inputColorless.tryRender(sb);
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        this.inputCards.tryUpdate();
        this.inputColorless.tryUpdate();
    }

    @Override
    public PCLRandomRelicAmountDialog getConfirmValue() {
        return this;
    }

    @Override
    public PCLRandomRelicAmountDialog getCancelValue() {
        return null;
    }

    public int getCardCount() {
        return inputCards.getCachedValue();
    }

    public int getColorlessCount() {
        return inputColorless.getCachedValue();
    }

    public void open(ArrayList<AbstractRelic> relics) {
        setActive(true);
        inputCards.setLimits(0, EUIUtils.count(relics, c -> EUIGameUtils.getRelicColor(c.relicId) != AbstractCard.CardColor.COLORLESS));
        inputColorless.setLimits(0, EUIUtils.count(relics, c -> EUIGameUtils.getRelicColor(c.relicId) == AbstractCard.CardColor.COLORLESS));

        inputCards.forceSetValue(inputCards.getMax(), true);
        inputColorless.forceSetValue(inputColorless.getMax(), true);

        inputColorless.setActive(inputColorless.getMax() > 0);
    }
}
