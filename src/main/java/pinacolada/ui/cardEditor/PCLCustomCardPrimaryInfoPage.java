package pinacolada.ui.cardEditor;

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
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.CardTagItem;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.ui.PCLValueEditor;
import pinacolada.utilities.GameUtilities;

import java.util.Arrays;
import java.util.List;

import static pinacolada.ui.cardEditor.PCLCustomCardEditCardScreen.START_Y;

public class PCLCustomCardPrimaryInfoPage extends PCLCustomCardEditorPage
{
    public static final int EFFECT_COUNT = 2;

    public static final float MENU_WIDTH = scale(160);
    public static final float MENU_HEIGHT = scale(40);
    public static final float SPACING_WIDTH = screenW(0.02f);
    protected static final float START_X = screenW(0.25f);
    protected static final float PAD_X = AbstractCard.IMG_WIDTH * 0.75f + Settings.CARD_VIEW_PAD_X;
    protected static final float PAD_Y = scale(10);
    protected static final Color bc1 = new Color(0.35F, 0.35F, 0.35F, 1.0F);
    protected static final Color sc1 = new Color(0, 0, 0, 0.25f);

    protected PCLCustomCardEditCardScreen effect;
    protected EUILabel header;
    protected EUITextBoxInput idInput;
    protected EUITextBoxInput nameInput;
    protected EUISearchableDropdown<Settings.GameLanguage> languageDropdown;
    protected EUIDropdown<PCLCardTarget> targetDropdown;
    protected EUIDropdown<AbstractCard.CardRarity> raritiesDropdown;
    protected EUIDropdown<AbstractCard.CardType> typesDropdown;
    protected EUIDropdown<PCLLoadout> loadoutDropdown;
    protected EUIDropdown<CardTagItem> flagsDropdown;
    protected EUILabel idWarning;
    protected PCLValueEditor maxUpgrades;
    protected PCLValueEditor maxCopies;
    protected EUIToggle uniqueToggle;
    protected EUIToggle soulboundToggle;
    protected Settings.GameLanguage activeLanguage = Settings.language;

    // Colorless/Curse should not be able to see Summon in the card editor
    protected static List<AbstractCard.CardType> getEligibleTypes(AbstractCard.CardColor color)
    {
        if (GameUtilities.isPCLOnlyCardColor(color))
        {
            return Arrays.asList(AbstractCard.CardType.values());
        }
        return EUIUtils.filter(AbstractCard.CardType.values(), v -> v != PCLEnum.CardType.SUMMON);
    }

    public PCLCustomCardPrimaryInfoPage(PCLCustomCardEditCardScreen effect)
    {
        this.effect = effect;

        this.header = new EUILabel(EUIFontHelper.cardtitlefontLarge,
                new EUIHitbox(screenW(0.5f), START_Y, MENU_WIDTH, MENU_HEIGHT))
                .setAlignment(0.5f, 0.0f, false)
                .setFontScale(0.8f).setColor(Color.LIGHT_GRAY)
                .setLabel(PGR.core.strings.cedit_primaryInfo);

        idInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.images.panelRoundedHalfH.texture(),
                new EUIHitbox(START_X, screenH(0.82f), MENU_WIDTH * 2.3f, MENU_HEIGHT * 1.65f))
                .setOnComplete(this::validifyCardID)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_idSuffix)
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.05f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f)
                .setFont(FontHelper.cardTitleFont, 0.7f)
                .setTooltip(PGR.core.strings.cedit_idSuffix, PGR.core.strings.cetut_idSuffix);
        idWarning = new EUILabel(EUIFontHelper.cardtitlefontSmall,
                new EUIHitbox(START_X + MENU_WIDTH * 2.5f, screenH(0.82f), MENU_WIDTH, MENU_HEIGHT))
                .setAlignment(0.5f, 0.0f, false)
                .setFontScale(0.8f).setColor(Settings.RED_TEXT_COLOR)
                .setLabel(PGR.core.strings.cedit_primaryInfo);
        idWarning.setActive(false);

        nameInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.images.panelRoundedHalfH.texture(),
                new EUIHitbox(START_X, screenH(0.72f), MENU_WIDTH * 2.3f, MENU_HEIGHT * 1.65f))
                .setOnComplete(s -> {
                    effect.modifyAllBuilders(e -> e.setName(s).setLanguageMapEntry(activeLanguage));
                })
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, LeaderboardScreen.TEXT[7])
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.05f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f)
                .setFont(FontHelper.cardTitleFont, 0.7f)
                .setTooltip(LeaderboardScreen.TEXT[7], PGR.core.strings.cetut_nameLanguage);
        languageDropdown = (EUISearchableDropdown<Settings.GameLanguage>) new EUISearchableDropdown<Settings.GameLanguage>(new EUIHitbox(screenW(0.55f), screenH(0.73f), MENU_WIDTH, MENU_HEIGHT)
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setOnChange(languages -> {
                    if (!languages.isEmpty())
                    {
                        this.updateLanguage(languages.get(0));
                    }
                })
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, OptionsPanel.TEXT[13].replace(":", ""))
                .setItems(Settings.GameLanguage.values())
                .setCanAutosizeButton(true)
                .setSelection(activeLanguage, false)
                .setTooltip(LeaderboardScreen.TEXT[7], PGR.core.strings.cetut_nameLanguage);
        raritiesDropdown = new EUIDropdown<AbstractCard.CardRarity>(new EUIHitbox(START_X, screenH(0.62f), MENU_WIDTH, MENU_HEIGHT)
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setOnChange(rarities -> {
                    if (!rarities.isEmpty())
                    {
                        effect.modifyAllBuilders(e -> e.setRarityType(rarities.get(0), e.cardType));
                    }
                })
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .setItems(GameUtilities.getStandardRarities())
                .setTooltip(CardLibSortHeader.TEXT[0], PGR.core.strings.cetut_rarity);
        typesDropdown = new EUIDropdown<AbstractCard.CardType>(new EUIHitbox(raritiesDropdown.hb.x + raritiesDropdown.hb.width + SPACING_WIDTH, screenH(0.62f), MENU_WIDTH, MENU_HEIGHT)
                , EUIGameUtils::textForType)
                .setOnChange(types -> {
                    if (!types.isEmpty())
                    {
                        // Pages need to refresh because changing card type affects available skill options or attributes
                        effect.modifyAllBuilders(e -> e.setRarityType(e.cardRarity, types.get(0)));
                        effect.refreshPages();
                    }
                })
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[1])
                .setCanAutosizeButton(true)
                .setItems(getEligibleTypes(effect.getBuilder().cardColor))
                .setTooltip(CardLibSortHeader.TEXT[1], PGR.core.strings.cetut_type);
        targetDropdown = new EUIDropdown<PCLCardTarget>(new EUIHitbox(typesDropdown.hb.x + typesDropdown.hb.width + SPACING_WIDTH, screenH(0.62f), MENU_WIDTH, MENU_HEIGHT)
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setOnChange(targets -> {
                    if (!targets.isEmpty())
                    {
                        effect.modifyAllBuilders(e -> e.setTarget(targets.get(0)));
                    }
                })
                .setLabelFunctionForOption(PCLCardTarget::getTitle, false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_cardTarget)
                .setCanAutosizeButton(true)
                .setItems(PCLCardTarget.getAll())
                .setTooltip(PGR.core.strings.cedit_cardTarget, PGR.core.strings.cetut_cardTarget);
        flagsDropdown = new EUISearchableDropdown<CardTagItem>(new EUIHitbox(START_X, screenH(0.5f), MENU_WIDTH, MENU_HEIGHT), cs -> cs.getTip().title)
                .setOnChange(selectedSeries -> {
                    effect.modifyAllBuilders(e -> e.setExtraTags(selectedSeries));
                })
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_flags)
                .setCanAutosizeButton(true)
                .setIsMultiSelect(true)
                .setItems(CardTagItem.getCompatible(effect.currentSlot.slotColor))
                .setTooltip(PGR.core.strings.cedit_flags, PGR.core.strings.cetut_primaryFlags);

        loadoutDropdown = new EUISearchableDropdown<PCLLoadout>(new EUIHitbox(flagsDropdown.hb.x + flagsDropdown.hb.width + SPACING_WIDTH, screenH(0.5f), MENU_WIDTH, MENU_HEIGHT), PCLLoadout::getName)
                .setOnChange(selectedSeries -> {
                    effect.modifyAllBuilders(e -> e.setLoadout(!selectedSeries.isEmpty() ? selectedSeries.get(0) : null));
                })
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.sui_seriesUI)
                .setCanAutosizeButton(true)
                .setShowClearForSingle(true)
                .setTooltip(PGR.core.strings.sui_seriesUI, PGR.core.strings.cetut_attrAffinity);
        loadoutDropdown
                .setActive(GameUtilities.isPCLCardColor(effect.currentSlot.slotColor) && loadoutDropdown.size() > 0);

        maxUpgrades = new PCLValueEditor(new EUIHitbox(START_X, screenH(0.4f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cedit_maxUpgrades, (val) -> effect.modifyAllBuilders(e -> e.setMaxUpgrades(val)))
                .setLimits(-1, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.cedit_maxUpgrades, PGR.core.strings.cetut_maxUpgrades);
        maxCopies = new PCLValueEditor(new EUIHitbox(screenW(0.35f), screenH(0.4f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cedit_maxCopies, (val) -> effect.modifyAllBuilders(e -> e.setMaxCopies(val)))
                .setLimits(-1, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.cedit_maxCopies, PGR.core.strings.cetut_maxCopies);
        uniqueToggle = (EUIToggle) new EUIToggle(new EUIHitbox(screenW(0.45f), screenH(0.4f), MENU_WIDTH, MENU_HEIGHT))
                .setFont(EUIFontHelper.carddescriptionfontNormal, 0.9f)
                .setText(PGR.core.tooltips.unique.title)
                .setOnToggle(val -> effect.modifyAllBuilders(e -> {
                    e.setUnique(val);
                }))
                .setTooltip(PGR.core.tooltips.unique);
        soulboundToggle = (EUIToggle) new EUIToggle(new EUIHitbox(screenW(0.53f), screenH(0.4f), MENU_WIDTH, MENU_HEIGHT))
                .setFont(EUIFontHelper.carddescriptionfontNormal, 0.9f)
                .setText(PGR.core.tooltips.soulbound.title)
                .setOnToggle(val -> effect.modifyAllBuilders(e -> {
                    e.setRemovableFromDeck(!val);
                }))
                .setTooltip(PGR.core.tooltips.soulbound);

        PCLResources<?,?,?> resources = PGR.getResources(effect.currentSlot.slotColor);
        if (resources != null)
        {
            loadoutDropdown.setItems(PCLLoadout.getAll(effect.currentSlot.slotColor));
        }
        else
        {
            loadoutDropdown.setActive(false);
        }

        refresh();
    }

    public String getTitle()
    {
        return header.text;
    }

    @Override
    public void refresh()
    {
        idInput.setLabel(StringUtils.removeStart(effect.getBuilder().ID, PCLCustomCardSlot.getBaseIDPrefix(effect.getBuilder().cardColor)));
        nameInput.setLabel(effect.getBuilder().strings.NAME);
        raritiesDropdown.setSelection(effect.getBuilder().cardRarity, false);
        typesDropdown.setSelection(effect.getBuilder().cardType, false);
        targetDropdown.setSelection(effect.getBuilder().cardTarget, false);
        loadoutDropdown.setSelection(effect.getBuilder().loadout, false);
        flagsDropdown.setSelection(effect.getBuilder().extraTags, false);
        maxUpgrades.setValue(effect.getBuilder().maxUpgradeLevel, false);
        maxCopies.setValue(effect.getBuilder().maxCopies, false);
        uniqueToggle.setToggle(effect.getBuilder().unique);
        soulboundToggle.setToggle(!effect.getBuilder().removableFromDeck);
    }

    @Override
    public TextureCache getTextureCache()
    {
        return PCLCoreImages.editorPrimary;
    }

    @Override
    public void updateImpl()
    {
        header.tryUpdate();
        idWarning.tryUpdate();
        maxUpgrades.tryUpdate();
        maxCopies.tryUpdate();
        loadoutDropdown.tryUpdate();
        flagsDropdown.tryUpdate();
        raritiesDropdown.tryUpdate();
        typesDropdown.tryUpdate();
        targetDropdown.tryUpdate();
        languageDropdown.tryUpdate();
        nameInput.tryUpdate();
        idInput.tryUpdate();
        uniqueToggle.tryUpdate();
        soulboundToggle.tryUpdate();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        header.tryRender(sb);
        idWarning.tryRender(sb);
        maxUpgrades.tryRender(sb);
        maxCopies.tryRender(sb);
        raritiesDropdown.tryRender(sb);
        typesDropdown.tryRender(sb);
        targetDropdown.tryRender(sb);
        loadoutDropdown.tryRender(sb);
        flagsDropdown.tryRender(sb);
        languageDropdown.tryRender(sb);
        nameInput.tryRender(sb);
        idInput.tryRender(sb);
        uniqueToggle.tryRender(sb);
        soulboundToggle.tryRender(sb);
    }

    private void validifyCardID(String cardID)
    {
        String fullID = PCLCustomCardSlot.getBaseIDPrefix(effect.getBuilder().cardColor) + cardID;
        if (PCLCustomCardSlot.isIDDuplicate(fullID, effect.getBuilder().cardColor))
        {
            idWarning.setActive(true);
            effect.saveButton.setInteractable(false);
        }
        else
        {
            idWarning.setActive(false);
            effect.modifyAllBuilders(e -> e.setID(fullID));
            effect.saveButton.setInteractable(true);
        }
    }

    private void updateLanguage(Settings.GameLanguage language)
    {
        activeLanguage = language;
        nameInput.setFont(language == Settings.language ? EUIFontHelper.cardtitlefontNormal : EUIFontHelper.createBoldFont(language, true, 27.0F, 2f, bc1, 3f, sc1), 0.7f)
                .setLabel(effect.getBuilder().getStringsForLanguage(activeLanguage).NAME);
    }
}
