package pinacolada.ui.editor.relic;

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
import extendedui.EUIUtils;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.effects.screen.PCLCustomDeletionConfirmationEffect;
import pinacolada.effects.screen.PCLCustomLoadoutEditEffect;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.relics.PCLDynamicRelicData;
import pinacolada.relics.PCLRelicData;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLCustomLoadout;
import pinacolada.resources.loadout.PCLCustomLoadoutInfo;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.ui.PCLValueEditor;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomGenericPage;
import pinacolada.ui.editor.PCLCustomLoadoutDialog;

import java.util.Arrays;
import java.util.Collections;

public class PCLCustomRelicPrimaryInfoPage extends PCLCustomGenericPage {
    protected PCLCustomRelicEditScreen effect;
    protected EUIButton addLoadoutButton;
    protected EUIButton editLoadoutButton;
    protected EUIButton deleteLoadoutButton;
    protected EUIButton resetLoadoutValue;
    protected EUILabel header;
    protected EUITextBoxInput idInput;
    protected EUITextBoxInput nameInput;
    protected EUISearchableDropdown<Settings.GameLanguage> languageDropdown;
    protected EUISearchableDropdown<AbstractRelic> replacementDropdown;
    protected EUIDropdown<AbstractRelic.RelicTier> tierDropdown;
    protected EUIDropdown<AbstractRelic.LandingSound> sfxDropdown;
    protected EUIDropdown<PCLLoadout> loadoutDropdown;
    protected EUILabel idWarning;
    protected PCLValueEditor maxUpgrades;
    protected PCLValueEditor branchUpgrades;
    protected PCLValueEditor loadoutValue;
    protected Settings.GameLanguage activeLanguage = Settings.language;

    public PCLCustomRelicPrimaryInfoPage(PCLCustomRelicEditScreen effect) {
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
        tierDropdown = new EUIDropdown<AbstractRelic.RelicTier>(new EUIHitbox(START_X, screenH(0.62f), MENU_WIDTH, MENU_HEIGHT), EUIGameUtils::textForRelicTier)
                .setOnChange(rarities -> {
                    if (!rarities.isEmpty()) {
                        effect.modifyAllBuilders((e, i) -> e.setTier(rarities.get(0)));
                    }
                })
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .setItems(AbstractRelic.RelicTier.values())
                .setTooltip(CardLibSortHeader.TEXT[0], PGR.core.strings.cetut_relicRarity);
        sfxDropdown = new EUIDropdown<AbstractRelic.LandingSound>(new EUIHitbox(tierDropdown.hb.x + tierDropdown.hb.width + SPACING_WIDTH, screenH(0.62f), MENU_WIDTH, MENU_HEIGHT)
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setOnChange(types -> {
                    if (!types.isEmpty()) {
                        effect.modifyAllBuilders((e, i) -> e.setSfx(types.get(0)));
                    }
                })
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.relic_landingSound)
                .setCanAutosizeButton(true)
                .setItems(AbstractRelic.LandingSound.values())
                .setTooltip(EUIRM.strings.relic_landingSound, PGR.core.strings.cetut_landingSound);

        loadoutDropdown = new EUISearchableDropdown<PCLLoadout>(new EUIHitbox(START_X, screenH(0.52f), MENU_WIDTH, MENU_HEIGHT), (item -> {
            String res = item.getName();
            return StringUtils.isEmpty(res) ? EUIRM.strings.ui_na : res;
        }))
                .setOnChange(selectedSeries -> {
                    setLoadout(!selectedSeries.isEmpty() ? selectedSeries.get(0) : null);
                })
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.sui_seriesUI)
                .setCanAutosizeButton(true)
                .setShowClearForSingle(true)
                .setTooltip(PGR.core.strings.sui_seriesUI, "");
        addLoadoutButton = new EUIButton(EUIRM.images.plus.texture(), new EUIHitbox(START_X, screenH(0.52f), MENU_HEIGHT * 0.75f, MENU_HEIGHT * 0.75f))
                .setOnClick(() -> this.openLoadoutCreator(PGR.core.strings.cedit_newLoadout, null))
                .setTooltip(PGR.core.strings.cedit_newLoadout, "");
        editLoadoutButton = new EUIButton(PCLCoreImages.Menu.edit.texture(), new EUIHitbox(START_X, screenH(0.52f), MENU_HEIGHT * 0.75f, MENU_HEIGHT * 0.75f))
                .setOnClick(() -> this.openLoadoutCreator(PGR.core.strings.cedit_renameItem, EUIUtils.safeCast(effect.getBuilder().loadout, PCLCustomLoadout.class)))
                .setTooltip(PGR.core.strings.cedit_renameItem, "");
        deleteLoadoutButton = new EUIButton(PCLCoreImages.Menu.delete.texture(), new EUIHitbox(START_X, screenH(0.52f), MENU_HEIGHT * 0.75f, MENU_HEIGHT * 0.75f))
                .setOnClick(() -> this.openLoadoutDelete(EUIUtils.safeCast(effect.getBuilder().loadout, PCLCustomLoadout.class)))
                .setTooltip(PGR.core.strings.cedit_deleteItem, "");

        replacementDropdown = (EUISearchableDropdown<AbstractRelic>) new EUISearchableDropdown<AbstractRelic>(new EUIHitbox(START_X, screenH(0.42f), MENU_WIDTH, MENU_HEIGHT), relic -> relic.name)
                .setOnChange(selectedSeries -> {
                    effect.modifyAllBuilders((e, i) -> e.setReplacementIDs(EUIUtils.arrayMapAsNonnull(selectedSeries, String.class, s -> s.relicId)));
                })
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_relicReplace)
                .setCanAutosizeButton(true)
                .setIsMultiSelect(true)
                .setTooltip(PGR.core.strings.cedit_relicReplace, PGR.core.strings.cetut_relicReplace);
        maxUpgrades = new PCLValueEditor(new EUIHitbox(screenW(0.262f), screenH(0.32f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cedit_maxUpgrades, this::modifyMaxUpgrades)
                .setLimits(-1, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.cedit_maxUpgrades, PGR.core.strings.cetut_maxUpgrades)
                .setHasInfinite(true, true);
        branchUpgrades = new PCLValueEditor(new EUIHitbox(screenW(0.362f), screenH(0.32f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cedit_branchUpgrade, (val) -> effect.modifyAllBuilders((e, i) -> e.setBranchFactor(val)))
                .setLimits(0, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.cedit_branchUpgrade, PGR.core.strings.cetut_branchUpgrade)
                .setHasInfinite(true, true);
        loadoutValue = new PCLValueEditor(new EUIHitbox(screenW(0.462f), screenH(0.32f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cedit_loadoutValue, (val) -> effect.modifyAllBuilders((e, i) -> e.setLoadoutValue(val)))
                .setLimits(0, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.cedit_loadoutValue, PGR.core.strings.cetut_loadoutValue)
                .setHasInfinite(true, true);
        resetLoadoutValue = new EUIButton(PCLCoreImages.Core.backArrow.texture(), new EUIHitbox(screenW(0.502f), screenH(0.323f), MENU_HEIGHT * 0.75f, MENU_HEIGHT * 0.75f))
                .setOnClick(() -> {
                    int val = PCLRelicData.getValueForRarity(effect.getBuilder().tier);
                    loadoutValue.setValue(val, true);
                })
                .setTooltip(PGR.core.strings.loadout_reset, "");

        editLoadoutButton.setActive(false);
        deleteLoadoutButton.setActive(false);
        refreshLoadoutItems();
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
                sfxDropdown.makeTour(true),
                replacementDropdown.makeTour(true),
                maxUpgrades.makeTour(true),
                branchUpgrades.makeTour(true)
        );
    }

    private void modifyMaxUpgrades(int val) {
        effect.modifyAllBuilders((e, i) -> e.setMaxUpgrades(val));
        effect.updateUpgradeEditorLimits(val);
    }

    @Override
    public void onOpen() {
        EUITourTooltip.queueFirstView(PGR.config.tourRelicPrimary, getTour());
        if (replacementDropdown.size() == 0) {
            replacementDropdown.setItems(PCLCustomEditEntityScreen.getAvailableRelics(effect.getBuilder().cardColor));
        }
    }

    protected void openLoadoutCreator(String title, PCLCustomLoadout loadout) {
        effect.currentDialog = new PCLCustomLoadoutEditEffect(title, loadout, effect.getBuilder().cardColor)
                .addCallback(this::registerLoadout);
    }

    protected void openLoadoutDelete(PCLCustomLoadout loadout) {
        if (loadout != null) {
            effect.currentDialog = new PCLCustomDeletionConfirmationEffect<>(loadout)
                    .addCallback((v) -> {
                        if (v != null) {
                            if (effect.getBuilder().loadout == loadout) {
                                setLoadout(null);
                            }
                            v.info.wipe();
                            refreshLoadoutItems();
                        }
                    });
        }
    }


    @Override
    public void refresh() {
        PCLDynamicRelicData builder = effect.getBuilder();

        idInput.setLabel(StringUtils.removeStart(builder.ID, PCLCustomRelicSlot.getBaseIDPrefix(builder.cardColor)));
        nameInput.setLabel(builder.strings.NAME);
        tierDropdown.setSelection(builder.tier, false);
        sfxDropdown.setSelection(builder.sfx, false);
        maxUpgrades.setValue(builder.maxUpgradeLevel, false);
        branchUpgrades.setValue(builder.branchFactor, false);
        loadoutValue.setValue(builder.getLoadoutValue(), false);

        if (replacementDropdown.size() == 0) {
            replacementDropdown.setItems(PCLCustomEditEntityScreen.getAvailableRelics(effect.getBuilder().cardColor));
        }

        String[] replacements = builder.replacementIDs;
        if (replacements != null) {
            replacementDropdown.setSelection(Arrays.asList(replacements), r -> r.relicId, false);
        }
        else {
            replacementDropdown.setSelection(Collections.emptyList(), false);
        }

        effect.updateUpgradeEditorLimits(builder.maxUpgradeLevel);
    }

    private void refreshLoadoutItems() {
        loadoutDropdown.setItems(PCLLoadout.getAll(effect.currentSlot.slotColor));
        loadoutDropdown.sortByLabel();
        loadoutDropdown
                .setActive(loadoutDropdown.size() > 0);
        addLoadoutButton.setPosition(loadoutDropdown.getClearButtonHitbox().cX + loadoutDropdown.getClearButtonHitbox().width, loadoutDropdown.getClearButtonHitbox().cY)
                .setActive(loadoutDropdown.isActive);
        editLoadoutButton.setPosition(addLoadoutButton.hb.cX + addLoadoutButton.hb.width, addLoadoutButton.hb.cY);
        deleteLoadoutButton.setPosition(editLoadoutButton.hb.cX + editLoadoutButton.hb.width, editLoadoutButton.hb.cY);
    }

    private void registerLoadout(PCLCustomLoadoutDialog dialog) {
        if (dialog != null) {
            if (dialog.loadout != null) {
                dialog.loadout.languageMap = dialog.currentLanguageMap;
                dialog.loadout.refreshStrings();
                dialog.loadout.commitChanges();
                effect.rebuildItem();
                refreshLoadoutItems();
            }
            else {
                PCLCustomLoadoutInfo info = new PCLCustomLoadoutInfo(dialog.currentID, EUIUtils.serialize(dialog.currentLanguageMap), effect.getBuilder().cardColor);
                PCLCustomLoadoutInfo.register(info);
                info.commit();
                refreshLoadoutItems();
            }
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        header.tryRender(sb);
        idWarning.tryRender(sb);
        tierDropdown.tryRender(sb);
        sfxDropdown.tryRender(sb);
        languageDropdown.tryRender(sb);
        nameInput.tryRender(sb);
        idInput.tryRender(sb);
        loadoutDropdown.tryRender(sb);
        addLoadoutButton.tryRender(sb);
        editLoadoutButton.tryRender(sb);
        deleteLoadoutButton.tryRender(sb);
        replacementDropdown.tryRender(sb);
        maxUpgrades.tryRender(sb);
        branchUpgrades.tryRender(sb);
        loadoutValue.tryRender(sb);
        resetLoadoutValue.tryRender(sb);
    }

    protected void setLoadout(PCLLoadout loadout) {
        effect.modifyAllBuilders((e, i) -> e.setLoadout(loadout));
        editLoadoutButton.setActive(loadoutDropdown.isActive && loadout instanceof PCLCustomLoadout);
        deleteLoadoutButton.setActive(editLoadoutButton.isActive);
    }

    @Override
    public void updateImpl() {
        header.tryUpdate();
        idWarning.tryUpdate();
        tierDropdown.tryUpdate();
        sfxDropdown.tryUpdate();
        replacementDropdown.tryUpdate();
        loadoutDropdown.tryUpdate();
        addLoadoutButton.tryUpdate();
        editLoadoutButton.tryUpdate();
        deleteLoadoutButton.tryUpdate();
        languageDropdown.tryUpdate();
        nameInput.tryUpdate();
        idInput.tryUpdate();
        maxUpgrades.tryUpdate();
        branchUpgrades.tryUpdate();
        loadoutValue.tryUpdate();
        resetLoadoutValue.tryUpdate();
    }

    private void updateLanguage(Settings.GameLanguage language) {
        activeLanguage = language;
        nameInput.setFont(language == Settings.language ? FontHelper.cardTitleFont : EUIFontHelper.createBoldFont(language, true, 27.0F, 2f, PCLCard.CARD_TYPE_COLOR, 3f, PCLCard.SHADOW_COLOR), 0.7f)
                .setLabel(effect.getBuilder().getStringsForLanguage(activeLanguage).NAME);
    }

    private void validifyCardID(String cardID) {
        String fullID = PCLCustomRelicSlot.getBaseIDPrefix(effect.getBuilder().cardColor) + cardID;
        if (!fullID.equals(effect.currentSlot.ID) && PCLCustomRelicSlot.isIDDuplicate(fullID, effect.getBuilder().cardColor)) {
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
