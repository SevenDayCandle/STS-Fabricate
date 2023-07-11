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
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.tags.CardFlag;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.ui.PCLValueEditor;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomGenericPage;
import pinacolada.utilities.GameUtilities;

import java.util.Arrays;
import java.util.List;

public class PCLCustomCardPrimaryInfoPage extends PCLCustomGenericPage {
    protected static final float START_X = screenW(0.25f);
    protected static final float PAD_X = AbstractCard.IMG_WIDTH * 0.75f + Settings.CARD_VIEW_PAD_X;
    protected static final float PAD_Y = scale(10);
    public static final int EFFECT_COUNT = 2;
    public static final float MENU_WIDTH = scale(160);
    public static final float MENU_HEIGHT = scale(40);
    public static final float SPACING_WIDTH = screenW(0.02f);
    protected PCLCustomCardEditCardScreen effect;
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
    protected EUIToggle uniqueToggle;
    protected EUIToggle soulboundToggle;
    protected Settings.GameLanguage activeLanguage = Settings.language;

    public PCLCustomCardPrimaryInfoPage(PCLCustomCardEditCardScreen effect) {
        this.effect = effect;

        this.header = new EUILabel(EUIFontHelper.cardTitleFontLarge,
                new EUIHitbox(screenW(0.5f), PCLCustomEditEntityScreen.START_Y, MENU_WIDTH, MENU_HEIGHT))
                .setAlignment(0.5f, 0.0f, false)
                .setFontScale(0.8f).setColor(Color.LIGHT_GRAY)
                .setLabel(PGR.core.strings.cedit_primaryInfo);

        idInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.images.longInput.texture(),
                new EUIHitbox(START_X, screenH(0.82f), MENU_WIDTH * 2.3f, MENU_HEIGHT * 1.15f))
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

        // TODO allow editing name for only the current form
        nameInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.images.longInput.texture(),
                new EUIHitbox(START_X, screenH(0.72f), MENU_WIDTH * 2.3f, MENU_HEIGHT * 1.15f))
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
                        effect.modifyAllBuilders((e, i) -> e.setRarityType(rarities.get(0), e.cardType));
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
                        effect.modifyAllBuilders((e, i) -> e.setRarityType(e.cardRarity, types.get(0)));
                        effect.refreshPages();
                    }
                })
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[1])
                .setCanAutosizeButton(true)
                .setItems(getEligibleTypes(effect.getBuilder().cardColor))
                .setTooltip(CardLibSortHeader.TEXT[1], PGR.core.strings.cetut_type);
        flagsDropdown = new EUISearchableDropdown<CardFlag>(new EUIHitbox(typesDropdown.hb.x + typesDropdown.hb.width + SPACING_WIDTH, screenH(0.62f), MENU_WIDTH, MENU_HEIGHT), cs -> cs.getTip().title)
                .setOnChange(selectedSeries -> {
                    effect.modifyAllBuilders((e, i) -> e.setFlags(selectedSeries));
                })
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_flags)
                .setCanAutosizeButton(true)
                .setIsMultiSelect(true)
                .setItems(PGR.config.showIrrelevantProperties.get() ? CardFlag.getAll() : CardFlag.getAll(effect.currentSlot.slotColor))
                .setTooltip(PGR.core.strings.cedit_flags, PGR.core.strings.cetut_primaryFlags);

        loadoutDropdown = new EUISearchableDropdown<PCLLoadout>(new EUIHitbox(START_X, screenH(0.5f), MENU_WIDTH, MENU_HEIGHT), PCLLoadout::getName)
                .setOnChange(selectedSeries -> {
                    effect.modifyAllBuilders((e, i) -> e.setLoadout(!selectedSeries.isEmpty() ? selectedSeries.get(0) : null));
                })
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.sui_seriesUI)
                .setCanAutosizeButton(true)
                .setShowClearForSingle(true)
                .setTooltip(PGR.core.strings.sui_seriesUI, PGR.core.strings.cetut_attrAffinity);

        maxUpgrades = new PCLValueEditor(new EUIHitbox(START_X, screenH(0.4f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cedit_maxUpgrades, (val) -> effect.modifyAllBuilders((e, i) -> e.setMaxUpgrades(val)))
                .setLimits(-1, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.cedit_maxUpgrades, PGR.core.strings.cetut_maxUpgrades)
                .setHasInfinite(true, true);
        maxCopies = new PCLValueEditor(new EUIHitbox(screenW(0.35f), screenH(0.4f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cedit_maxCopies, (val) -> effect.modifyAllBuilders((e, i) -> e.setMaxCopies(val)))
                .setLimits(-1, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.cedit_maxCopies, PGR.core.strings.cetut_maxCopies)
                .setHasInfinite(true, true);
        branchUpgrades = new PCLValueEditor(new EUIHitbox(screenW(0.45f), screenH(0.4f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cedit_branchUpgrade, (val) -> effect.modifyAllBuilders((e, i) -> e.setBranchFactor(val)))
                .setLimits(0, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.cedit_branchUpgrade, PGR.core.strings.cetut_branchUpgrade)
                .setHasInfinite(true, true);
        uniqueToggle = new EUIToggle(new EUIHitbox(screenW(0.53f), screenH(0.4f), MENU_WIDTH, MENU_HEIGHT))
                .setFont(EUIFontHelper.cardDescriptionFontNormal, 0.9f)
                .setText(PGR.core.tooltips.unique.title)
                .setOnToggle(val -> effect.modifyAllBuilders((e, i) -> {
                    e.setUnique(val);
                }))
                .setTooltip(PGR.core.tooltips.unique);
        soulboundToggle = new EUIToggle(new EUIHitbox(screenW(0.61f), screenH(0.4f), MENU_WIDTH, MENU_HEIGHT))
                .setFont(EUIFontHelper.cardDescriptionFontNormal, 0.9f)
                .setText(PGR.core.tooltips.soulbound.title)
                .setOnToggle(val -> effect.modifyAllBuilders((e, i) -> {
                    e.setRemovableFromDeck(!val);
                }))
                .setTooltip(PGR.core.tooltips.soulbound);

        loadoutDropdown.setItems(PCLLoadout.getAll(effect.currentSlot.slotColor));
        loadoutDropdown
                .setActive(loadoutDropdown.size() > 0);

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
    public void onOpen() {
        EUITourTooltip.queueFirstView(PGR.config.tourCardPrimary,
                idInput.makeTour(true),
                nameInput.makeTour(true),
                languageDropdown.makeTour(true),
                raritiesDropdown.makeTour(true),
                typesDropdown.makeTour(true),
                flagsDropdown.makeTour(true),
                maxUpgrades.makeTour(true),
                maxCopies.makeTour(true),
                branchUpgrades.makeTour(true),
                uniqueToggle.makeTour(true),
                soulboundToggle.makeTour(true));
    }

    @Override
    public TextureCache getTextureCache() {
        return PCLCoreImages.Menu.editorPrimary;
    }

    public String getTitle() {
        return header.text;
    }

    @Override
    public void refresh() {
        idInput.setLabel(StringUtils.removeStart(effect.getBuilder().ID, PCLCustomCardSlot.getBaseIDPrefix(effect.getBuilder().cardColor)));
        nameInput.setLabel(effect.getBuilder().strings.NAME);
        raritiesDropdown.setSelection(effect.getBuilder().cardRarity, false);
        typesDropdown.setSelection(effect.getBuilder().cardType, false);
        loadoutDropdown.setSelection(effect.getBuilder().loadout, false);
        flagsDropdown.setSelection(effect.getBuilder().flags, false);
        maxUpgrades.setValue(effect.getBuilder().maxUpgradeLevel, false);
        branchUpgrades.setValue(effect.getBuilder().branchFactor, false);
        maxCopies.setValue(effect.getBuilder().maxCopies, false);
        uniqueToggle.setToggle(effect.getBuilder().unique);
        soulboundToggle.setToggle(!effect.getBuilder().removableFromDeck);

        effect.upgradeToggle.setActive(effect.getBuilder().maxUpgradeLevel != 0);
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
        flagsDropdown.tryRender(sb);
        languageDropdown.tryRender(sb);
        nameInput.tryRender(sb);
        idInput.tryRender(sb);
        branchUpgrades.tryRender(sb);
        uniqueToggle.tryRender(sb);
        soulboundToggle.tryRender(sb);
    }

    @Override
    public void updateImpl() {
        header.tryUpdate();
        idWarning.tryUpdate();
        maxUpgrades.tryUpdate();
        maxCopies.tryUpdate();
        loadoutDropdown.tryUpdate();
        flagsDropdown.tryUpdate();
        raritiesDropdown.tryUpdate();
        typesDropdown.tryUpdate();
        languageDropdown.tryUpdate();
        nameInput.tryUpdate();
        idInput.tryUpdate();
        branchUpgrades.tryUpdate();
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
