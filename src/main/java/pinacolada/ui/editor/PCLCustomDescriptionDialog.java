package pinacolada.ui.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.screens.leaderboards.LeaderboardScreen;
import com.megacrit.cardcrawl.screens.options.OptionsPanel;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.PCLDynamicCardData;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.misc.LoadoutStrings;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLCustomLoadout;
import pinacolada.resources.loadout.PCLCustomLoadoutInfo;

import java.util.Arrays;
import java.util.HashMap;

public class PCLCustomDescriptionDialog extends EUIDialog<PCLCustomDescriptionDialog> {
    protected EUITextBoxInput textInput;
    protected EUISearchableDropdown<Settings.GameLanguage> languageDropdown;
    protected Settings.GameLanguage activeLanguage = Settings.language;
    protected int index;
    public String currentText = "";
    public HashMap<Settings.GameLanguage, String[]> currentLanguageMap;
    // TODO dropdown for linking to a specific effect extra or value
    // TODO legend for custom text options

    public PCLCustomDescriptionDialog(String headerText) {
        this(headerText, "");
    }

    public PCLCustomDescriptionDialog(String headerText, String descriptionText) {
        this(headerText, descriptionText, scale(500), scale(600));
    }

    public PCLCustomDescriptionDialog(String headerText, String descriptionText, float w, float h) {
        this(new EUIHitbox(Settings.WIDTH / 2.0F - w / 2f, Settings.HEIGHT / 2.0F - h / 2f, w, h), ImageMaster.OPTION_CONFIRM, headerText, descriptionText);
    }

    public PCLCustomDescriptionDialog(EUIHitbox hb, Texture backgroundTexture, String headerText, String descriptionText) {
        super(hb, backgroundTexture, headerText, descriptionText);

        textInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.images.darkSquare.texture(),
                new EUIHitbox(hb.x + hb.width / 4, hb.y + hb.height / 4, hb.width / 2, scale(250)))
                .setOnComplete(this::updateText)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.9f, 0.1f, false)
                .setFont(EUIFontHelper.cardTitleFontNormal, 0.7f);
        languageDropdown = (EUISearchableDropdown<Settings.GameLanguage>) new EUISearchableDropdown<Settings.GameLanguage>(new EUIHitbox(hb.x + hb.width / 4, textInput.hb.y + textInput.hb.height + scale(30), scale(95), scale(32))
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setOnChange(languages -> {
                    if (!languages.isEmpty()) {
                        this.updateLanguage(languages.get(0));
                    }
                })
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, OptionsPanel.TEXT[13].replace(":", ""))
                .setItems(Settings.GameLanguage.values())
                .setCanAutosizeButton(true)
                .setSelection(activeLanguage, false)
                .setTooltip(LeaderboardScreen.TEXT[7], PGR.core.strings.cetut_nameLanguage);
        textInput.label.setWrap(true);
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
    public PCLCustomDescriptionDialog getCancelValue() {
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
    public PCLCustomDescriptionDialog getConfirmValue() {
        return this;
    }

    @Override
    protected EUILabel getHeader(String headerText) {
        return new EUILabel(EUIFontHelper.buttonFont,
                new RelativeHitbox(hb, hb.width, hb.height, hb.width * 0.5f, hb.height * 0.89f))
                .setAlignment(0.5f, 0.5f, false)
                .setLabel(headerText);
    }

    protected String[] getStringsForLanguage(Settings.GameLanguage language) {
        return currentLanguageMap.getOrDefault(language,
                currentLanguageMap.getOrDefault(Settings.GameLanguage.ENG,
                        !currentLanguageMap.isEmpty() ? currentLanguageMap.entrySet().iterator().next().getValue() : new String[]{}));
    }

    public void open(EditorMaker<?,?> data, int index) {
        setActive(true);
        currentLanguageMap = data.createDescMap();
        this.index = index;
        updateLanguage(activeLanguage);
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        this.textInput.tryRender(sb);
        this.languageDropdown.tryRender(sb);
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        this.textInput.tryUpdate();
        this.languageDropdown.tryUpdate();
    }

    private void updateLanguage(Settings.GameLanguage language) {
        activeLanguage = language;
        textInput.setFont(language == Settings.language ? EUIFontHelper.cardTitleFontNormal : EUIFontHelper.createBoldFont(language, true, 27.0F, 2f, PCLCard.CARD_TYPE_COLOR, 3f, PCLCard.SHADOW_COLOR), 0.7f);
        String[] strings = getStringsForLanguage(activeLanguage);
        if (strings.length <= index) {
            textInput.setLabel("");
        }
        else {
            textInput.setLabel(strings[index]);
        }
    }

    private void updateText(String name) {
        String[] strings = getStringsForLanguage(activeLanguage);
        if (strings.length <= index) {
            strings = Arrays.copyOf(strings, index + 1);
        }
        strings[index] = name;
        currentLanguageMap.put(activeLanguage, strings);
    }
}
