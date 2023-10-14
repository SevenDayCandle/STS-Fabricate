package pinacolada.ui.editor.blight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.leaderboards.LeaderboardScreen;
import com.megacrit.cardcrawl.screens.options.OptionsPanel;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.BlightTier;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.blights.PCLCustomBlightSlot;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.ui.PCLValueEditor;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomGenericPage;

public class PCLCustomBlightPrimaryInfoPage extends PCLCustomGenericPage {
    protected PCLCustomBlightEditScreen effect;
    protected EUILabel header;
    protected EUITextBoxInput idInput;
    protected EUITextBoxInput nameInput;
    protected EUITextBoxInput flavorInput; // TODO implement this once you have multi-line input available
    protected EUISearchableDropdown<Settings.GameLanguage> languageDropdown;
    protected EUIDropdown<BlightTier> tierDropdown;
    protected EUILabel idWarning;
    protected PCLValueEditor maxUpgrades;
    protected PCLValueEditor branchUpgrades;
    protected EUIToggle uniqueToggle;
    protected Settings.GameLanguage activeLanguage = Settings.language;

    public PCLCustomBlightPrimaryInfoPage(PCLCustomBlightEditScreen effect) {
        this.effect = effect;

        this.header = new EUILabel(EUIFontHelper.cardTitleFontLarge,
                new EUIHitbox(screenW(0.5f), PCLCustomEditEntityScreen.START_Y, MENU_WIDTH, MENU_HEIGHT))
                .setAlignment(0.5f, 0.0f, false)
                .setFontScale(0.8f).setColor(Color.LIGHT_GRAY)
                .setLabel(PGR.core.strings.cedit_primaryInfo);

        idInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.images.longInput.texture(),
                new EUIHitbox(START_X, screenH(0.82f), MENU_WIDTH * 3f, MENU_HEIGHT * 1.15f))
                .setOnComplete(this::validifyCardID)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_idSuffix)
                .setHeaderSpacing(1.1f)
                .setBackgroundTexture(EUIRM.images.longInput.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.05f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f)
                .setFont(FontHelper.cardTitleFont, 0.7f)
                .setTooltip(PGR.core.strings.cedit_idSuffix, PGR.core.strings.cetut_idSuffix);
        idWarning = new EUILabel(EUIFontHelper.cardTitleFontSmall,
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
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, LeaderboardScreen.TEXT[7])
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
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, OptionsPanel.TEXT[13].replace(":", ""))
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
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
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
                .setFont(EUIFontHelper.cardDescriptionFontNormal, 0.9f)
                .setText(PGR.core.tooltips.unique.title)
                .setOnToggle(val -> effect.modifyAllBuilders((e, i) -> {
                    e.setUnique(val);
                }))
                .setTooltip(PGR.core.tooltips.unique);

        refresh();
    }

    @Override
    public TextureCache getTextureCache() {
        return PCLCoreImages.Menu.editorPrimary;
    }

    public String getTitle() {
        return header.text;
    }

    protected void modifyMaxUpgrades(int val) {
        effect.modifyAllBuilders((e, i) -> e.setMaxUpgrades(val));
        effect.upgradeToggle.setActive(val != 0);
    }

    @Override
    public void onOpen() {
        EUITourTooltip.queueFirstView(PGR.config.tourBlightPrimary,
                idInput.makeTour(true),
                nameInput.makeTour(true),
                languageDropdown.makeTour(true),
                tierDropdown.makeTour(true),
                maxUpgrades.makeTour(true),
                branchUpgrades.makeTour(true),
                uniqueToggle.makeTour(true)
        );
    }

    @Override
    public void refresh() {
        idInput.setLabel(StringUtils.removeStart(effect.getBuilder().ID, PCLCustomBlightSlot.getBaseIDPrefix(effect.getBuilder().cardColor)));
        nameInput.setLabel(effect.getBuilder().strings.NAME);
        tierDropdown.setSelection(effect.getBuilder().tier, false);
        maxUpgrades.setValue(effect.getBuilder().maxUpgradeLevel, false);
        branchUpgrades.setValue(effect.getBuilder().branchFactor, false);
        uniqueToggle.setToggle(effect.getBuilder().unique);

        effect.upgradeToggle.setActive(effect.getBuilder().maxUpgradeLevel != 0);
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
        nameInput.setFont(language == Settings.language ? EUIFontHelper.cardTitleFontNormal : EUIFontHelper.createBoldFont(language, true, 27.0F, 2f, PCLCard.CARD_TYPE_COLOR, 3f, PCLCard.SHADOW_COLOR), 0.7f)
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
