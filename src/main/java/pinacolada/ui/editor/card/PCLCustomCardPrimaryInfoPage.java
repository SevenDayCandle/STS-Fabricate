package pinacolada.ui.editor.card;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
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
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.PCLDynamicCardData;
import pinacolada.cards.base.fields.CardFlag;
import pinacolada.cards.base.fields.PCLCustomFlagInfo;
import pinacolada.effects.screen.PCLCustomDeletionConfirmationEffect;
import pinacolada.effects.screen.PCLCustomFlagEditEffect;
import pinacolada.effects.screen.PCLCustomLoadoutEditEffect;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLCustomLoadout;
import pinacolada.resources.loadout.PCLCustomLoadoutInfo;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.ui.PCLValueEditor;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomFlagDialog;
import pinacolada.ui.editor.PCLCustomGenericPage;
import pinacolada.ui.editor.PCLCustomLoadoutDialog;
import pinacolada.utilities.GameUtilities;

import java.util.Arrays;
import java.util.List;

public class PCLCustomCardPrimaryInfoPage extends PCLCustomGenericPage {
    protected PCLCustomCardEditScreen effect;
    protected EUIButton addLoadoutButton;
    protected EUIButton editLoadoutButton;
    protected EUIButton deleteLoadoutButton;
    protected EUIButton addFlagButton;
    protected EUIButton resetLoadoutValue;
    protected EUILabel header;
    protected EUITextBoxInput idInput;
    protected EUITextBoxInput nameInput;
    protected EUISearchableDropdown<Settings.GameLanguage> languageDropdown;
    protected EUIDropdown<AbstractCard.CardRarity> raritiesDropdown;
    protected EUIDropdown<AbstractCard.CardType> typesDropdown;
    protected EUIDropdown<PCLLoadout> loadoutDropdown;
    protected EUIDropdown<CardFlag> flagsDropdown;
    protected EUILabel idWarning;
    protected PCLValueEditor maxUpgrades;
    protected PCLValueEditor maxCopies;
    protected PCLValueEditor branchUpgrades;
    protected PCLValueEditor loadoutValue;
    protected EUIToggle uniqueToggle;
    protected EUIToggle soulboundToggle;
    protected Settings.GameLanguage activeLanguage = Settings.language;

    public PCLCustomCardPrimaryInfoPage(PCLCustomCardEditScreen effect) {
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
        raritiesDropdown = new EUIDropdown<AbstractCard.CardRarity>(new EUIHitbox(START_X, screenH(0.62f), MENU_WIDTH, MENU_HEIGHT), EUIGameUtils::textForRarity)
                .setOnChange(rarities -> {
                    if (!rarities.isEmpty()) {
                        effect.modifyAllBuilders((e, i) -> e.setRarity(rarities.get(0)));
                    }
                })
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .setItems(getEligibleRarities())
                .setTooltip(CardLibSortHeader.TEXT[0], PGR.core.strings.cetut_rarity);
        typesDropdown = new EUIDropdown<AbstractCard.CardType>(new EUIHitbox(raritiesDropdown.hb.x + raritiesDropdown.hb.width + SPACING_WIDTH, screenH(0.62f), MENU_WIDTH, MENU_HEIGHT)
                , EUIGameUtils::textForType)
                .setOnChange(types -> {
                    if (!types.isEmpty()) {
                        // Pages need to refresh because changing card type affects available skill options or attributes
                        effect.modifyAllBuilders((e, i) -> e.setType(types.get(0)));
                        effect.refreshPages();
                    }
                })
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[1])
                .setCanAutosizeButton(true)
                .setItems(getEligibleTypes(effect.getBuilder().cardColor))
                .setTooltip(CardLibSortHeader.TEXT[1], PGR.core.strings.cetut_type);

        flagsDropdown = new EUISearchableDropdown<CardFlag>(new EUIHitbox(typesDropdown.hb.x + typesDropdown.hb.width + SPACING_WIDTH, screenH(0.62f), MENU_WIDTH, MENU_HEIGHT), cs -> cs.getTooltip().title)
                .setOnChange(selectedSeries -> {
                    effect.modifyAllBuilders((e, i) -> e.setFlags(selectedSeries));
                })
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_flags)
                .setCanAutosizeButton(true)
                .setIsMultiSelect(true)
                .setItems(PGR.config.showIrrelevantProperties.get() ? CardFlag.getAll() : CardFlag.getAll(effect.currentSlot.slotColor))
                .setTooltip(PGR.core.strings.cedit_flags, PGR.core.strings.cetut_primaryFlags)
                .setRowFunction((a, b, c, d) -> new EditDeleteDropdownRow<CardFlag, PCLCustomFlagInfo>(a, b, c, PCLCustomFlagInfo.get(c.ID), d, f -> this.openFlagCreator(PGR.core.strings.cedit_renameItem, f), this::openFlagDelete))
                .setRowWidthFunction((a, b, c) -> a.calculateRowWidth() + MENU_HEIGHT * 5);
        addFlagButton = new EUIButton(EUIRM.images.plus.texture(), new EUIHitbox(flagsDropdown.hb.x + flagsDropdown.hb.width, screenH(0.62f), MENU_HEIGHT * 0.75f, MENU_HEIGHT * 0.75f))
                .setOnClick(() -> this.openFlagCreator(PGR.core.strings.cedit_newFlag, null))
                .setTooltip(PGR.core.strings.cedit_newFlag, "");

        loadoutDropdown = new EUISearchableDropdown<PCLLoadout>(new EUIHitbox(START_X, screenH(0.51f), MENU_WIDTH, MENU_HEIGHT), (item -> {
                    String res = item.getName();
                    return StringUtils.isEmpty(res) ? EUIRM.strings.ui_na : res;
                }))
                .setOnChange(selectedSeries -> {
                    setLoadout(!selectedSeries.isEmpty() ? selectedSeries.get(0) : null);
                })
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.sui_seriesUI)
                .setCanAutosizeButton(true)
                .setShowClearForSingle(true)
                .setTooltip(PGR.core.strings.sui_seriesUI, "");
        addLoadoutButton = new EUIButton(EUIRM.images.plus.texture(), new EUIHitbox(START_X, screenH(0.51f), MENU_HEIGHT * 0.75f, MENU_HEIGHT * 0.75f))
                .setOnClick(() -> this.openLoadoutCreator(PGR.core.strings.cedit_newLoadout, null))
                .setTooltip(PGR.core.strings.cedit_newLoadout, "");
        editLoadoutButton = new EUIButton(PCLCoreImages.Menu.edit.texture(), new EUIHitbox(START_X, screenH(0.51f), MENU_HEIGHT * 0.75f, MENU_HEIGHT * 0.75f))
                .setOnClick(() -> this.openLoadoutCreator(PGR.core.strings.cedit_renameItem, EUIUtils.safeCast(effect.getBuilder().loadout, PCLCustomLoadout.class)))
                .setTooltip(PGR.core.strings.cedit_renameItem, "");
        deleteLoadoutButton = new EUIButton(PCLCoreImages.Menu.delete.texture(), new EUIHitbox(START_X, screenH(0.51f), MENU_HEIGHT * 0.75f, MENU_HEIGHT * 0.75f))
                .setOnClick(() -> this.openLoadoutDelete(EUIUtils.safeCast(effect.getBuilder().loadout, PCLCustomLoadout.class)))
                .setTooltip(PGR.core.strings.cedit_deleteItem, "");

        maxUpgrades = new PCLValueEditor(new EUIHitbox(screenW(0.262f), screenH(0.4f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cedit_maxUpgrades, this::modifyMaxUpgrades)
                .setLimits(-1, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.cedit_maxUpgrades, PGR.core.strings.cetut_maxUpgrades)
                .setHasInfinite(true, true);
        maxCopies = new PCLValueEditor(new EUIHitbox(screenW(0.362f), screenH(0.4f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cedit_maxCopies, (val) -> effect.modifyAllBuilders((e, i) -> e.setMaxCopies(val)))
                .setLimits(-1, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.cedit_maxCopies, PGR.core.strings.cetut_maxCopies)
                .setHasInfinite(true, true);
        branchUpgrades = new PCLValueEditor(new EUIHitbox(screenW(0.462f), screenH(0.4f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cedit_branchUpgrade, (val) -> effect.modifyAllBuilders((e, i) -> e.setBranchFactor(val)))
                .setLimits(0, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.cedit_branchUpgrade, PGR.core.strings.cetut_branchUpgrade)
                .setHasInfinite(true, true);
        loadoutValue = new PCLValueEditor(new EUIHitbox(screenW(0.562f), screenH(0.4f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cedit_loadoutValue, (val) -> effect.modifyAllBuilders((e, i) -> e.setLoadoutValue(val)))
                .setLimits(0, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.cedit_loadoutValue, PGR.core.strings.cetut_loadoutValue)
                .setHasInfinite(true, true);
        resetLoadoutValue = new EUIButton(PCLCoreImages.Core.backArrow.texture(), new EUIHitbox(screenW(0.602f), screenH(0.403f), MENU_HEIGHT * 0.75f, MENU_HEIGHT * 0.75f))
                .setOnClick(() -> {
                    int val = PCLCardData.getValueForRarity(effect.getBuilder().cardRarity);
                    loadoutValue.setValue(val, true);
                })
                .setTooltip(PGR.core.strings.loadout_reset, "");
        uniqueToggle = new EUIToggle(new EUIHitbox(screenW(0.24f), screenH(0.35f), MENU_WIDTH, MENU_HEIGHT))
                .setFont(EUIFontHelper.cardDescriptionFontNormal, 0.9f)
                .setText(PGR.core.tooltips.unique.title)
                .setOnToggle(val -> effect.modifyAllBuilders((e, i) -> {
                    e.setUnique(val);
                }))
                .setTooltip(PGR.core.tooltips.unique);
        soulboundToggle = new EUIToggle(new EUIHitbox(screenW(0.34f), screenH(0.35f), MENU_WIDTH, MENU_HEIGHT))
                .setFont(EUIFontHelper.cardDescriptionFontNormal, 0.9f)
                .setText(PGR.core.tooltips.soulbound.title)
                .setOnToggle(val -> effect.modifyAllBuilders((e, i) -> {
                    e.setRemovableFromDeck(!val);
                }))
                .setTooltip(PGR.core.tooltips.soulbound);

        editLoadoutButton.setActive(false);
        deleteLoadoutButton.setActive(false);
        refreshFlagItems();
        refreshLoadoutItems();
        refresh();
    }

    public static List<AbstractCard.CardRarity> getEligibleRarities() {
        return PGR.config.showIrrelevantProperties.get() ? Arrays.asList(AbstractCard.CardRarity.values()) : GameUtilities.getStandardCardRarities();
    }

    // Colorless/Curse should not be able to see Summon in the card editor
    public static List<AbstractCard.CardType> getEligibleTypes(AbstractCard.CardColor color) {
        if (GameUtilities.isPCLOnlyCardColor(color) || PGR.config.showIrrelevantProperties.get()) {
            return Arrays.asList(AbstractCard.CardType.values());
        }
        return EUIUtils.filter(AbstractCard.CardType.values(), v -> v != PCLEnum.CardType.SUMMON);
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
                raritiesDropdown.makeTour(true),
                typesDropdown.makeTour(true),
                flagsDropdown.makeTour(true),
                maxUpgrades.makeTour(true),
                maxCopies.makeTour(true),
                branchUpgrades.makeTour(true),
                loadoutValue.makeTour(true),
                uniqueToggle.makeTour(true),
                soulboundToggle.makeTour(true)
        );
    }

    protected void modifyMaxUpgrades(int val) {
        effect.modifyAllBuilders((e, i) -> e.setMaxUpgrades(val));
        effect.upgradeToggle.setLimits(0, val < 0 ? PSkill.DEFAULT_MAX : val).setActive(val != 0);
    }

    @Override
    public void onOpen() {
        EUITourTooltip.queueFirstView(PGR.config.tourCardPrimary, getTour());
    }

    protected void openFlagCreator(String title, PCLCustomFlagInfo flag) {
        effect.currentDialog = new PCLCustomFlagEditEffect(title, flag)
                .addCallback(this::registerFlag);
    }

    protected void openFlagDelete(PCLCustomFlagInfo flag) {
        effect.currentDialog = new PCLCustomDeletionConfirmationEffect<>(flag)
                .addCallback((v) -> {
                    if (v != null) {
                        List<CardFlag> flags = effect.getBuilder().flags;
                        if (flags != null) {
                            flags.remove(v.flag);
                            effect.modifyAllBuilders((e, i) -> e.setFlags(flags));
                        }
                        v.wipe();
                        refreshFlagItems();
                    }
                });
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
        PCLDynamicCardData builder = effect.getBuilder();
        idInput.setLabel(StringUtils.removeStart(builder.ID, PCLCustomCardSlot.getBaseIDPrefix(builder.cardColor)));
        nameInput.setLabel(builder.strings.NAME);
        raritiesDropdown.setSelection(builder.cardRarity, false);
        typesDropdown.setSelection(builder.cardType, false);
        loadoutDropdown.setSelection(builder.loadout, false);
        flagsDropdown.setSelection(builder.flags, false);
        maxUpgrades.setValue(builder.maxUpgradeLevel, false);
        branchUpgrades.setValue(builder.branchFactor, false);
        loadoutValue.setValue(builder.getLoadoutValue(), false);
        maxCopies.setValue(builder.maxCopies, false);
        uniqueToggle.setToggle(builder.unique);
        soulboundToggle.setToggle(!builder.removableFromDeck);

        effect.upgradeToggle.setLimits(0, builder.maxUpgradeLevel < 0 ? PSkill.DEFAULT_MAX : builder.maxUpgradeLevel).setValue(effect.currentBuilder, false).setActive(builder.maxUpgradeLevel != 0);
        editLoadoutButton.setActive(loadoutDropdown.isActive && builder.loadout instanceof PCLCustomLoadout);
        deleteLoadoutButton.setActive(editLoadoutButton.isActive);
    }

    protected void refreshFlagItems() {
        flagsDropdown.setItems(PGR.config.showIrrelevantProperties.get() ? CardFlag.getAll() : CardFlag.getAll(effect.currentSlot.slotColor));
        flagsDropdown.sortByLabel();
        flagsDropdown
                .setActive(flagsDropdown.size() > 0);
        addFlagButton.setPosition(flagsDropdown.getClearButtonHitbox().cX + flagsDropdown.getClearButtonHitbox().width, flagsDropdown.getClearButtonHitbox().cY);
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

    private void registerFlag(PCLCustomFlagDialog dialog) {
        if (dialog != null) {
            if (dialog.flag != null) {
                dialog.flag.languageMap = dialog.currentLanguageMap;
                dialog.flag.commit();
                effect.rebuildItem();
                refreshFlagItems();
            }
            else {
                PCLCustomFlagInfo info = new PCLCustomFlagInfo(dialog.currentID, dialog.currentLanguageMap, AbstractCard.CardColor.COLORLESS);
                PCLCustomFlagInfo.register(info);
                info.commit();
                refreshFlagItems();
            }
        }
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
        maxUpgrades.tryRender(sb);
        maxCopies.tryRender(sb);
        raritiesDropdown.tryRender(sb);
        typesDropdown.tryRender(sb);
        loadoutDropdown.tryRender(sb);
        addFlagButton.tryRender(sb);
        addLoadoutButton.tryRender(sb);
        editLoadoutButton.tryRender(sb);
        deleteLoadoutButton.tryRender(sb);
        flagsDropdown.tryRender(sb);
        languageDropdown.tryRender(sb);
        nameInput.tryRender(sb);
        idInput.tryRender(sb);
        branchUpgrades.tryRender(sb);
        loadoutValue.tryRender(sb);
        resetLoadoutValue.tryRender(sb);
        uniqueToggle.tryRender(sb);
        soulboundToggle.tryRender(sb);
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
        maxUpgrades.tryUpdate();
        maxCopies.tryUpdate();
        loadoutDropdown.tryUpdate();
        addLoadoutButton.tryUpdate();
        editLoadoutButton.tryUpdate();
        deleteLoadoutButton.tryUpdate();
        flagsDropdown.tryUpdate();
        addFlagButton.tryUpdate();
        raritiesDropdown.tryUpdate();
        typesDropdown.tryUpdate();
        languageDropdown.tryUpdate();
        nameInput.tryUpdate();
        idInput.tryUpdate();
        branchUpgrades.tryUpdate();
        loadoutValue.tryUpdate();
        resetLoadoutValue.tryUpdate();
        uniqueToggle.tryUpdate();
        soulboundToggle.tryUpdate();
    }

    private void updateLanguage(Settings.GameLanguage language) {
        activeLanguage = language;
        nameInput.setFont(language == Settings.language ? EUIFontHelper.cardTitleFontNormal : EUIFontHelper.createBoldFont(language, true, 27.0F, 2f, PCLCard.CARD_TYPE_COLOR, 3f, PCLCard.SHADOW_COLOR), 0.7f)
                .setLabel(effect.getBuilder().getStringsForLanguage(activeLanguage).NAME);
    }

    private void validifyCardID(String cardID) {
        String fullID = PCLCustomCardSlot.getBaseIDPrefix(effect.getBuilder().cardColor) + cardID;
        if (!fullID.equals(effect.currentSlot.ID) && PCLCustomCardSlot.isIDDuplicate(fullID, effect.getBuilder().cardColor)) {
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
