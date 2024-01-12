package pinacolada.ui.editor.orb;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.leaderboards.LeaderboardScreen;
import com.megacrit.cardcrawl.screens.options.OptionsPanel;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.EUIBase;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.effects.PCLSFX;
import pinacolada.orbs.PCLDynamicOrbData;
import pinacolada.powers.PCLCustomPowerSlot;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.ui.PCLValueEditor;
import pinacolada.ui.editor.PCLCustomColorEditor;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomGenericPage;

public class PCLCustomOrbPrimaryInfoPage extends PCLCustomGenericPage {
    protected PCLCustomOrbEditScreen effect;
    protected EUILabel header;
    protected EUITextBoxInput idInput;
    protected EUITextBoxInput nameInput;
    protected EUISearchableDropdown<Settings.GameLanguage> languageDropdown;
    protected EUIDialogColorPicker colorPicker;
    protected EUIDropdown<String> sfxDropdown;
    protected EUILabel idWarning;
    protected PCLValueEditor rotationSpeed;
    protected PCLValueEditor maxUpgrades;
    protected PCLCustomColorEditor flashColor1Editor;
    protected PCLCustomColorEditor flashColor2Editor;
    protected Settings.GameLanguage activeLanguage = Settings.language;

    public PCLCustomOrbPrimaryInfoPage(PCLCustomOrbEditScreen effect) {
        this.effect = effect;

        this.header = new EUILabel(FontHelper.cardTitleFont,
                new EUIHitbox(screenW(0.5f), PCLCustomEditEntityScreen.START_Y, MENU_WIDTH, MENU_HEIGHT))
                .setAlignment(0.5f, 0.0f, false)
                .setFontScale(0.8f).setColor(Color.LIGHT_GRAY)
                .setLabel(PGR.core.strings.cedit_primaryInfo);

        idInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.images.longInput.texture(),
                new EUIHitbox(START_X, screenH(0.82f), MENU_WIDTH * 3f, MENU_HEIGHT * 1.15f))
                .setOnComplete(this::validifyCardID)
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_idSuffix)
                .setHeaderSpacing(1.1f)
                .setBackgroundTexture(EUIRM.images.longInput.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.05f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f)
                .setFont(FontHelper.cardTitleFont, 0.7f)
                .setTooltip(PGR.core.strings.cedit_idSuffix, PGR.core.strings.cetut_idSuffix);
        idWarning = new EUILabel(FontHelper.topPanelAmountFont,
                new EUIHitbox(START_X + MENU_WIDTH * 2.5f, screenH(0.82f), MENU_WIDTH, MENU_HEIGHT))
                .setAlignment(0.5f, 0.0f, false)
                .setFontScale(0.8f).setColor(Settings.RED_TEXT_COLOR)
                .setLabel(PGR.core.strings.cedit_idSuffixWarning);
        idWarning.setActive(false);

        nameInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.images.longInput.texture(),
                new EUIHitbox(START_X, screenH(0.72f), MENU_WIDTH * 3f, MENU_HEIGHT * 1.15f))
                .setOnComplete(s -> {
                    effect.modifyAllBuilders((e, i) -> e.setName(s).setLanguageMapEntry(activeLanguage));
                })
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, LeaderboardScreen.TEXT[7])
                .setHeaderSpacing(1.1f)
                .setBackgroundTexture(EUIRM.images.longInput.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.05f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f)
                .setFont(FontHelper.cardTitleFont, 0.7f)
                .setTooltip(LeaderboardScreen.TEXT[7], PGR.core.strings.cetut_nameLanguage);
        languageDropdown = (EUISearchableDropdown<Settings.GameLanguage>) new EUISearchableDropdown<Settings.GameLanguage>(new EUIHitbox(screenW(0.55f), screenH(0.73f), MENU_WIDTH, MENU_HEIGHT)
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setOnChange(languages -> {
                    if (!languages.isEmpty()) {
                        this.updateLanguage(languages.get(0));
                    }
                })
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, OptionsPanel.TEXT[13].replace(":", ""))
                .setItems(Settings.GameLanguage.values())
                .setCanAutosizeButton(true)
                .setSelection(activeLanguage, false)
                .setTooltip(LeaderboardScreen.TEXT[7], PGR.core.strings.cetut_nameLanguage);
        sfxDropdown = new EUIDropdown<String>(new EUIHitbox(START_X, screenH(0.62f), MENU_WIDTH, MENU_HEIGHT))
                .setOnChange(types -> {
                    if (!types.isEmpty()) {
                        String type = types.get(0);
                        effect.modifyAllBuilders((e, i) -> e.setSfx(type));
                    }
                })
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_orbSfx)
                .setCanAutosizeButton(true)
                .setItems(PCLSFX.getAll())
                .setTooltip(PGR.core.strings.cedit_orbSfx, PGR.core.strings.cetut_orbSfx);
        rotationSpeed = new PCLValueEditor(new EUIHitbox(screenW(0.462f), screenH(0.62f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cedit_orbRotation, (val -> {
                    effect.modifyAllBuilders((e, i) -> e.setRotationSpeed(val));
                }))
                .setLimits(-PSkill.DEFAULT_MAX, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.cedit_orbRotation, PGR.core.strings.cetut_orbRotation)
                .setHasInfinite(false, true);

        colorPicker = new EUIDialogColorPicker(new EUIHitbox(Settings.WIDTH * 0.7f, (Settings.HEIGHT - EUIBase.scale(800)) / 2f, EUIBase.scale(460), EUIBase.scale(800)), EUIUtils.EMPTY_STRING, EUIUtils.EMPTY_STRING);
        colorPicker
                .setShowDark(false)
                .setActive(false);

        flashColor1Editor = new PCLCustomColorEditor(new EUIHitbox(START_X, screenH(0.5f), MENU_WIDTH, MENU_HEIGHT), EUIUtils.format(PGR.core.strings.cedit_orbFlash, 1),
                this::openColorEditor,
                color -> {
                    effect.modifyAllBuilders((e, i) -> e.setFlareColor1(color));
                })
                .setTooltip(EUIUtils.format(PGR.core.strings.cedit_orbFlash, 1), PGR.core.strings.cetut_orbFlash);
        flashColor2Editor = new PCLCustomColorEditor(new EUIHitbox(flashColor1Editor.hb.x + flashColor1Editor.hb.width + SPACING_WIDTH * 3, screenH(0.5f), MENU_WIDTH, MENU_HEIGHT), EUIUtils.format(PGR.core.strings.cedit_orbFlash, 2),
                this::openColorEditor,
                color -> {
                    effect.modifyAllBuilders((e, i) -> e.setFlareColor2(color));
                })
                .setTooltip(EUIUtils.format(PGR.core.strings.cedit_orbFlash, 2), PGR.core.strings.cetut_orbFlash);

        maxUpgrades = new PCLValueEditor(new EUIHitbox(screenW(0.262f), screenH(0.4f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cedit_maxUpgrades, this::modifyMaxUpgrades)
                .setLimits(-1, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.cedit_maxUpgrades, PGR.core.strings.cetut_maxUpgrades)
                .setHasInfinite(true, true);

        refresh();
    }

    @Override
    public TextureCache getTextureCache() {
        return EUIRM.images.tag;
    }

    public String getTitle() {
        return header.text;
    }

    @Override
    public EUITourTooltip[] getTour() {
        return EUIUtils.array(
                idInput.makeTour(true),
                nameInput.makeTour(true),
                languageDropdown.makeTour(true),
                sfxDropdown.makeTour(true),
                rotationSpeed.makeTour(true),
                flashColor1Editor.makeTour(true),
                flashColor2Editor.makeTour(true),
                maxUpgrades.makeTour(true)
        );
    }

    private void modifyMaxUpgrades(int val) {
        effect.modifyAllBuilders((e, i) -> e.setMaxUpgrades(val));
        effect.updateUpgradeEditorLimits(val);
    }

    @Override
    public void onOpen() {
        EUITourTooltip.queueFirstView(PGR.config.tourPowerPrimary, getTour());
    }

    private void openColorEditor(PCLCustomColorEditor editor) {
        Color prev = editor.getColor().cpy();
        colorPicker
                .setOnComplete((res) -> {
                    colorPicker.setActive(false);
                    if (res == null) {
                        editor.setColor(prev, true);
                    }
                    else {
                        editor.setColor(res.getReturnColor(), true);
                    }
                })
                .setHeaderText(editor.header.text)
                .setActive(true);
        colorPicker.open(prev);
    }

    @Override
    public void refresh() {
        PCLDynamicOrbData builder = effect.getBuilder();

        idInput.setLabel(StringUtils.removeStart(builder.ID, PCLCustomPowerSlot.BASE_POWER_ID));
        nameInput.setLabel(builder.strings.NAME);
        sfxDropdown.setSelection(builder.sfx, false);
        rotationSpeed.setValue((int) builder.rotationSpeed, false);
        flashColor1Editor.setColor(builder.flareColor1, false);
        flashColor2Editor.setColor(builder.flareColor2, false);
        maxUpgrades.setValue(builder.maxUpgradeLevel, false);

        effect.updateUpgradeEditorLimits(builder.maxUpgradeLevel);
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        header.tryRender(sb);
        idWarning.tryRender(sb);
        rotationSpeed.tryRender(sb);
        sfxDropdown.tryRender(sb);
        languageDropdown.tryRender(sb);
        nameInput.tryRender(sb);
        idInput.tryRender(sb);
        flashColor1Editor.tryRender(sb);
        flashColor2Editor.tryRender(sb);
        maxUpgrades.tryRender(sb);
        colorPicker.tryRender(sb);
    }

    @Override
    public void updateImpl() {
        header.tryUpdate();
        idWarning.tryUpdate();
        rotationSpeed.tryUpdate();
        sfxDropdown.tryUpdate();
        languageDropdown.tryUpdate();
        nameInput.tryUpdate();
        idInput.tryUpdate();
        flashColor1Editor.tryUpdate();
        flashColor2Editor.tryUpdate();
        maxUpgrades.tryUpdate();
        colorPicker.tryUpdate();
    }

    private void updateLanguage(Settings.GameLanguage language) {
        activeLanguage = language;
        nameInput.setFont(language == Settings.language ? FontHelper.cardTitleFont : EUIFontHelper.createBoldFont(language, true, 27.0F, 2f, PCLCard.CARD_TYPE_COLOR, 3f, PCLCard.SHADOW_COLOR), 0.7f)
                .setLabel(effect.getBuilder().getStringsForLanguage(activeLanguage).NAME);
    }

    private void validifyCardID(String cardID) {
        String fullID = PCLCustomPowerSlot.BASE_POWER_ID + cardID;
        if (!fullID.equals(effect.currentSlot.ID) && PCLCustomPowerSlot.isIDDuplicate(fullID)) {
            idWarning.setActive(true);
            effect.saveButton.setInteractable(false);
        }
        else {
            idWarning.setActive(false);
            effect.modifyAllBuilders((e, i) -> e.setID(fullID));
            effect.saveButton.setInteractable(true);
        }
    }
}
