package pinacolada.ui.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
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
import pinacolada.misc.LoadoutStrings;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLCustomLoadout;
import pinacolada.resources.loadout.PCLCustomLoadoutInfo;

import java.util.HashMap;

public class PCLCustomLoadoutDialog extends EUIDialog<PCLCustomLoadoutDialog> {
    protected float MENU_WIDTH = scale(190);
    protected float MENU_HEIGHT = scale(32);
    protected EUITextBoxInput idInput;
    protected EUITextBoxInput nameInput;
    protected EUISearchableDropdown<Settings.GameLanguage> languageDropdown;
    protected EUILabel idWarning;
    protected Settings.GameLanguage activeLanguage = Settings.language;
    public PCLCustomLoadout loadout;
    public String currentID = "";
    public HashMap<Settings.GameLanguage, LoadoutStrings> currentLanguageMap;

    public PCLCustomLoadoutDialog(String headerText) {
        this(headerText, "");
    }

    public PCLCustomLoadoutDialog(String headerText, String descriptionText) {
        this(headerText, descriptionText, scale(330), scale(490));
    }

    public PCLCustomLoadoutDialog(String headerText, String descriptionText, float w, float h) {
        this(new EUIHitbox(Settings.WIDTH / 2.0F - w / 2f, Settings.HEIGHT / 2.0F - h / 2f, w, h), ImageMaster.OPTION_CONFIRM, headerText, descriptionText);
    }

    public PCLCustomLoadoutDialog(EUIHitbox hb, Texture backgroundTexture, String headerText, String descriptionText) {
        super(hb, backgroundTexture, headerText, descriptionText);
        idInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.images.longInput.texture(),
                new EUIHitbox(hb.x + hb.width / 4, hb.y + hb.height / 1.8f, MENU_WIDTH, MENU_HEIGHT))
                .setOnComplete(this::validifyCardID)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_idSuffix)
                .setHeaderSpacing(1.1f)
                .setBackgroundTexture(EUIRM.images.longInput.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.05f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f)
                .setFont(FontHelper.cardTitleFont, 0.7f)
                .setTooltip(PGR.core.strings.cedit_idSuffix, PGR.core.strings.cetut_idSuffix);
        idWarning = new EUILabel(EUIFontHelper.cardTitleFontSmall,
                new EUIHitbox(hb.x + hb.width / 4 + MENU_WIDTH, screenH(0.82f), MENU_WIDTH, MENU_HEIGHT))
                .setAlignment(0.5f, 0.0f, false)
                .setFontScale(0.8f).setColor(Settings.RED_TEXT_COLOR)
                .setLabel(PGR.core.strings.cedit_idSuffixWarning);
        idWarning.setActive(false);

        nameInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.images.longInput.texture(),
                new EUIHitbox(hb.x + hb.width / 4, idInput.hb.y - idInput.hb.height * 2f, MENU_WIDTH, MENU_HEIGHT))
                .setOnComplete(this::updateName)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, LeaderboardScreen.TEXT[7])
                .setHeaderSpacing(1.1f)
                .setBackgroundTexture(EUIRM.images.longInput.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.05f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f)
                .setFont(FontHelper.cardTitleFont, 0.7f)
                .setTooltip(LeaderboardScreen.TEXT[7], PGR.core.strings.cetut_nameLanguage);
        languageDropdown = (EUISearchableDropdown<Settings.GameLanguage>) new EUISearchableDropdown<Settings.GameLanguage>(new EUIHitbox(hb.x + hb.width / 4, nameInput.hb.y - nameInput.hb.height * 2f, MENU_WIDTH / 2f, MENU_HEIGHT)
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
    }

    public void create(AbstractCard.CardColor color) {
        PCLCustomLoadoutInfo info = new PCLCustomLoadoutInfo(currentID, EUIUtils.serialize(currentLanguageMap), color);
        PCLCustomLoadoutInfo.register(info);
        info.commit();
        info.loadout.refreshStrings();
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
    public PCLCustomLoadoutDialog getCancelValue() {
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
    public PCLCustomLoadoutDialog getConfirmValue() {
        return this;
    }

    protected LoadoutStrings getStringsForLanguage(Settings.GameLanguage language) {
        return currentLanguageMap.getOrDefault(language,
                currentLanguageMap.getOrDefault(Settings.GameLanguage.ENG,
                        currentLanguageMap.size() > 0 ? currentLanguageMap.entrySet().iterator().next().getValue() : PCLCustomLoadout.getInitialStrings()));
    }

    public void open(PCLCustomLoadout loadout, AbstractCard.CardColor color) {
        setActive(true);
        this.loadout = loadout;
        if (loadout != null) {
            currentID = loadout.ID;
            currentLanguageMap = new HashMap<>(loadout.languageMap);
            idInput.setText(currentID);
            updateLanguage(activeLanguage);
        }
        else {
            currentID = PCLCustomLoadoutInfo.makeNewID(color);
            idInput.setText(currentID);
            currentLanguageMap = new HashMap<>();
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        this.idInput.tryRender(sb);
        this.idWarning.tryRender(sb);
        this.nameInput.tryRender(sb);
        this.languageDropdown.tryRender(sb);
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        this.idInput.tryUpdate();
        this.idWarning.tryUpdate();
        this.nameInput.tryUpdate();
        this.languageDropdown.tryUpdate();
    }

    private void updateLanguage(Settings.GameLanguage language) {
        activeLanguage = language;
        nameInput.setFont(language == Settings.language ? EUIFontHelper.cardTitleFontNormal : EUIFontHelper.createBoldFont(language, true, 27.0F, 2f, PCLCard.CARD_TYPE_COLOR, 3f, PCLCard.SHADOW_COLOR), 0.7f)
                .setLabel(getStringsForLanguage(activeLanguage).NAME);
    }

    private void updateName(String name) {
        LoadoutStrings strings = getStringsForLanguage(activeLanguage);
        strings.NAME = name;
        currentLanguageMap.put(activeLanguage, strings);
    }

    private void validifyCardID(String cardID) {
        if (!currentID.equals(cardID) && PCLCustomLoadoutInfo.isIDDuplicate(currentID)) {
            idWarning.setActive(true);
            this.confirm.setInteractable(false);
        }
        else {
            idWarning.setActive(false);
            currentID = cardID;
            this.confirm.setInteractable(true);
        }
    }
}
