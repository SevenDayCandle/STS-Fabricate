package pinacolada.ui.editor.blight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.leaderboards.LeaderboardScreen;
import com.megacrit.cardcrawl.screens.options.OptionsPanel;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.BlightTier;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.blights.PCLCustomBlightSlot;
import pinacolada.blights.PCLDynamicBlightData;
import pinacolada.cards.base.PCLCard;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.ui.PCLValueEditor;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomGenericPage;

public class PCLCustomBlightPrimaryInfoPage extends PCLCustomGenericPage {
    protected PCLCustomBlightEditScreen effect;
    protected EUILabel header;
    protected EUITextBoxInput idInput;
    protected EUITextBoxInput nameInput;
    protected EUISearchableDropdown<Settings.GameLanguage> languageDropdown;
    protected EUIDropdown<BlightTier> tierDropdown;
    protected EUILabel idWarning;
    protected PCLValueEditor maxUpgrades;
    protected PCLValueEditor branchUpgrades;
    protected EUIToggle uniqueToggle;
    protected Settings.GameLanguage activeLanguage = Settings.language;

    public PCLCustomBlightPrimaryInfoPage(PCLCustomBlightEditScreen effect) {
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
        tierDropdown = new EUIDropdown<BlightTier>(new EUIHitbox(START_X, screenH(0.62f), MENU_WIDTH, MENU_HEIGHT), BlightTier::getName)
                .setOnChange(rarities -> {
                    if (!rarities.isEmpty()) {
                        effect.modifyAllBuilders((e, i) -> e.setTier(rarities.get(0)));
                    }
                })
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .setItems(BlightTier.values())
                .setTooltip(CardLibSortHeader.TEXT[0], PGR.core.strings.cetut_relicRarity);
        maxUpgrades = new PCLValueEditor(new EUIHitbox(screenW(0.262f), screenH(0.4f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cedit_maxUpgrades, this::modifyMaxUpgrades)
                .setLimits(-1, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.cedit_maxUpgrades, PGR.core.strings.cetut_maxUpgrades)
                .setHasInfinite(true, true);
        branchUpgrades = new PCLValueEditor(new EUIHitbox(screenW(0.362f), screenH(0.4f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cedit_branchUpgrade, (val) -> effect.modifyAllBuilders((e, i) -> e.setBranchFactor(val)))
                .setLimits(0, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.cedit_branchUpgrade, PGR.core.strings.cetut_branchUpgrade)
                .setHasInfinite(true, true);
        uniqueToggle = new EUIToggle(new EUIHitbox(screenW(0.462f), screenH(0.4f), MENU_WIDTH, MENU_HEIGHT))
                .setFont(FontHelper.cardDescFont_N, 0.9f)
                .setText(PGR.core.tooltips.unique.title)
                .setOnToggle(val -> effect.modifyAllBuilders((e, i) -> {
                    e.setUnique(val);
                }))
                .setTooltip(PGR.core.tooltips.unique);

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
                tierDropdown.makeTour(true),
                maxUpgrades.makeTour(true),
                branchUpgrades.makeTour(true),
                uniqueToggle.makeTour(true)
        );
    }

    protected void modifyMaxUpgrades(int val) {
        effect.modifyAllBuilders((e, i) -> e.setMaxUpgrades(val));
        effect.updateUpgradeEditorLimits(val);
    }

    @Override
    public void onOpen() {
        EUITourTooltip.queueFirstView(PGR.config.tourBlightPrimary, getTour());
    }

    @Override
    public void refresh() {
        PCLDynamicBlightData builder = effect.getBuilder();
        idInput.setLabel(StringUtils.removeStart(effect.getBuilder().ID, PCLCustomBlightSlot.getBaseIDPrefix(builder.cardColor)));
        nameInput.setLabel(builder.strings.NAME);
        tierDropdown.setSelection(builder.tier, false);
        maxUpgrades.setValue(builder.maxUpgradeLevel, false);
        branchUpgrades.setValue(builder.branchFactor, false);
        uniqueToggle.setToggle(builder.unique);

        effect.updateUpgradeEditorLimits(builder.maxUpgradeLevel);
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        header.tryRender(sb);
        idWarning.tryRender(sb);
        tierDropdown.tryRender(sb);
        languageDropdown.tryRender(sb);
        nameInput.tryRender(sb);
        idInput.tryRender(sb);
        maxUpgrades.tryRender(sb);
        branchUpgrades.tryRender(sb);
        uniqueToggle.tryRender(sb);
    }

    @Override
    public void updateImpl() {
        header.tryUpdate();
        idWarning.tryUpdate();
        tierDropdown.tryUpdate();
        languageDropdown.tryUpdate();
        nameInput.tryUpdate();
        idInput.tryUpdate();
        maxUpgrades.tryUpdate();
        branchUpgrades.tryUpdate();
        uniqueToggle.tryUpdate();
    }

    private void updateLanguage(Settings.GameLanguage language) {
        activeLanguage = language;
        nameInput.setFont(language == Settings.language ? FontHelper.cardTitleFont : EUIFontHelper.createBoldFont(language, true, 27.0F, 2f, PCLCard.CARD_TYPE_COLOR, 3f, PCLCard.SHADOW_COLOR), 0.7f)
                .setLabel(effect.getBuilder().getStringsForLanguage(activeLanguage).NAME);
    }

    private void validifyCardID(String cardID) {
        String fullID = PCLCustomBlightSlot.getBaseIDPrefix(effect.getBuilder().cardColor) + cardID;
        if (!fullID.equals(effect.currentSlot.ID) && PCLCustomBlightSlot.isIDDuplicate(fullID, effect.getBuilder().cardColor)) {
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
