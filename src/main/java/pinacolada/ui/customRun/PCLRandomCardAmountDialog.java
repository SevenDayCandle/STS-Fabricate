package pinacolada.ui.customRun;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
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
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class PCLRandomCardAmountDialog extends EUIDialog<PCLRandomCardAmountDialog>
{
    protected EUITextBoxNumericalInput inputCards;
    protected EUITextBoxNumericalInput inputColorless;
    protected EUITextBoxNumericalInput inputCurse;

    public PCLRandomCardAmountDialog(String headerText)
    {
        this(headerText, "");
    }

    public PCLRandomCardAmountDialog(String headerText, String descriptionText)
    {
        this(headerText, descriptionText, scale(300), scale(390));
    }

    public PCLRandomCardAmountDialog(String headerText, String descriptionText, float w, float h)
    {
        this(new EUIHitbox(Settings.WIDTH / 2.0F - w / 2f, Settings.HEIGHT / 2.0F - h / 2f, w, h), ImageMaster.OPTION_CONFIRM, headerText, descriptionText);
    }

    public PCLRandomCardAmountDialog(EUIHitbox hb, Texture backgroundTexture, String headerText, String descriptionText)
    {
        super(hb, backgroundTexture, headerText, descriptionText);
        this.inputCards = (EUITextBoxNumericalInput) new EUITextBoxNumericalInput(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(hb.x + hb.width / 4, hb.y + hb.height / 1.8f, hb.width / 2, scale(54)))
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.sui_characterCards)
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardtitlefontSmall, 1f);
        this.inputColorless = (EUITextBoxNumericalInput) new EUITextBoxNumericalInput(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(hb.x + hb.width / 4, inputCards.hb.y - inputCards.hb.height * 1.2f, hb.width / 2, scale(54)))
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, EUIGameUtils.getColorName(AbstractCard.CardColor.COLORLESS))
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardtitlefontSmall, 1f);
        this.inputCurse = (EUITextBoxNumericalInput) new EUITextBoxNumericalInput(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(hb.x + hb.width / 4, inputColorless.hb.y - inputColorless.hb.height * 1.2f, hb.width / 2, scale(54)))
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, EUIGameUtils.getColorName(AbstractCard.CardColor.CURSE))
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardtitlefontSmall, 1f);
        this.inputCards.forceSetValue(0, false);
        this.inputColorless.forceSetValue(0, false);
        this.inputCurse.forceSetValue(0, false);
    }

    protected EUIButton getConfirmButton() {
        return new EUIButton(ImageMaster.OPTION_YES,
                new RelativeHitbox(hb, scale(135), scale(70), hb.width * 0.15f, hb.height * 0.15f))
                .setFont(EUIFontHelper.cardtitlefontNormal, 0.8f)
                .setText(GridCardSelectScreen.TEXT[0])
                .setOnClick(() -> {
                    if (onComplete != null) {
                        onComplete.invoke(getConfirmValue());
                    }
                });
    }

    protected EUIButton getCancelButton() {
        return new EUIButton(ImageMaster.OPTION_NO,
                new RelativeHitbox(hb, scale(135), scale(70), hb.width * 0.85f, hb.height * 0.15f))
                .setFont(EUIFontHelper.cardtitlefontNormal, 0.8f)
                .setText(GridCardSelectScreen.TEXT[1])
                .setOnClick(() -> {
                    if (onComplete != null) {
                        onComplete.invoke(getCancelValue());
                    }
                });
    }

    @Override
    public PCLRandomCardAmountDialog getConfirmValue()
    {
        return this;
    }

    @Override
    public PCLRandomCardAmountDialog getCancelValue()
    {
        return null;
    }

    @Override
    public void updateImpl()
    {
        super.updateImpl();
        this.inputCards.tryUpdate();
        this.inputColorless.tryUpdate();
        this.inputCurse.tryUpdate();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        super.renderImpl(sb);
        this.inputCards.tryRender(sb);
        this.inputColorless.tryRender(sb);
        this.inputCurse.tryRender(sb);
    }

    public int getCardCount()
    {
        return inputCards.getCachedValue();
    }

    public int getColorlessCount()
    {
        return inputColorless.getCachedValue();
    }

    public int getCurseCount()
    {
        return inputCurse.getCachedValue();
    }

    public void open(ArrayList<AbstractCard> cards)
    {
        setActive(true);
        inputCards.setLimits(0, EUIUtils.count(cards, c -> !GameUtilities.isColorlessCardColor(c.color)));
        inputColorless.setLimits(0, EUIUtils.count(cards, c -> c.color == AbstractCard.CardColor.COLORLESS));
        inputCurse.setLimits(0, EUIUtils.count(cards, c -> c.color == AbstractCard.CardColor.CURSE));

        inputCards.forceSetValue(inputCards.getMax(), true);
        inputColorless.forceSetValue(inputColorless.getMax(), true);
        inputCurse.forceSetValue(inputCurse.getMax(), true);

        inputColorless.setActive(inputColorless.getMax() > 0);
        inputCurse.setActive(inputCurse.getMax() > 0);
    }
}
