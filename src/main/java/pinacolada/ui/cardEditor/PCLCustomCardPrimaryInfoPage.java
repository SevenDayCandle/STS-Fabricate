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
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.fields.CardTagItem;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLLoadout;
import pinacolada.skills.PSkill;
import pinacolada.ui.common.PCLValueEditor;
import pinacolada.utilities.GameUtilities;

import java.util.Arrays;
import java.util.List;

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
    protected EUITextBoxInput nameInput;
    protected EUISearchableDropdown<Settings.GameLanguage> languageDropdown;
    protected EUIDropdown<PCLCardTarget> targetDropdown;
    protected EUIDropdown<AbstractCard.CardRarity> raritiesDropdown;
    protected EUIDropdown<AbstractCard.CardType> typesDropdown;
    protected EUIDropdown<PCLLoadout> seriesDropdown;
    protected EUIDropdown<CardTagItem> flagsDropdown;
    protected PCLValueEditor maxUpgrades;
    protected EUIToggle uniqueToggle;
    protected Settings.GameLanguage activeLanguage = Settings.language;

    protected static List<AbstractCard.CardType> getEligibleTypes(AbstractCard.CardColor color)
    {
        if (GameUtilities.isPCLCardColor(color))
        {
            return Arrays.asList(AbstractCard.CardType.values());
        }
        return EUIUtils.filter(AbstractCard.CardType.values(), v -> v != PCLEnum.CardType.SUMMON);
    }

    public PCLCustomCardPrimaryInfoPage(PCLCustomCardEditCardScreen effect)
    {
        this.effect = effect;

        this.header = new EUILabel(EUIFontHelper.cardtitlefontLarge,
                new EUIHitbox(screenW(0.5f), screenH(0.93f), MENU_WIDTH, MENU_HEIGHT))
                .setAlignment(0.5f, 0.0f, false)
                .setFontScale(0.8f).setColor(Color.LIGHT_GRAY)
                .setLabel(PGR.core.strings.cardEditor.primaryInfo);

        nameInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.images.panelRoundedHalfH.texture(),
                new EUIHitbox(START_X, screenH(0.82f), MENU_WIDTH * 2.3f, MENU_HEIGHT * 1.65f))
                .setOnComplete(s -> {
                    effect.modifyAllBuilders(e -> e.setName(s).setLanguageMapEntry(activeLanguage));
                })
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, LeaderboardScreen.TEXT[7])
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.05f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f)
                .setFont(FontHelper.cardTitleFont, 0.7f);
        languageDropdown = (EUISearchableDropdown<Settings.GameLanguage>) new EUISearchableDropdown<Settings.GameLanguage>(new EUIHitbox(screenW(0.55f), screenH(0.83f), MENU_WIDTH, MENU_HEIGHT)
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
                .setSelection(activeLanguage, false);
        raritiesDropdown = new EUIDropdown<AbstractCard.CardRarity>(new EUIHitbox(START_X, screenH(0.72f), MENU_WIDTH, MENU_HEIGHT)
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setOnChange(rarities -> {
                    if (!rarities.isEmpty())
                    {
                        effect.modifyAllBuilders(e -> e.setRarity(rarities.get(0)));
                    }
                })
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .setItems(GameUtilities.getStandardRarities());
        typesDropdown = new EUIDropdown<AbstractCard.CardType>(new EUIHitbox(raritiesDropdown.hb.x + raritiesDropdown.hb.width + SPACING_WIDTH, screenH(0.72f), MENU_WIDTH, MENU_HEIGHT)
                , EUIGameUtils::textForType)
                .setOnChange(types -> {
                    if (!types.isEmpty())
                    {
                        // Pages need to refresh because changing card type affects available skill options or attributes
                        effect.modifyAllBuilders(e -> e.setType(types.get(0)));
                        effect.refreshPages();
                    }
                })
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[1])
                .setCanAutosizeButton(true)
                .setItems(getEligibleTypes(effect.getBuilder().cardColor));
        targetDropdown = new EUIDropdown<PCLCardTarget>(new EUIHitbox(typesDropdown.hb.x + typesDropdown.hb.width + SPACING_WIDTH, screenH(0.72f), MENU_WIDTH, MENU_HEIGHT)
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setOnChange(targets -> {
                    if (!targets.isEmpty())
                    {
                        effect.modifyAllBuilders(e -> e.setTarget(targets.get(0)));
                    }
                })
                .setLabelFunctionForOption(PCLCardTarget::getTitle, false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cardEditor.cardTarget)
                .setCanAutosizeButton(true)
                .setItems(PCLCardTarget.getAll());
        flagsDropdown = new EUISearchableDropdown<CardTagItem>(new EUIHitbox(START_X, screenH(0.6f), MENU_WIDTH, MENU_HEIGHT), cs -> cs.getTip().title)
                .setOnChange(selectedSeries -> {
                    effect.modifyAllBuilders(e -> e.setExtraTags(selectedSeries));
                })
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cardEditor.flags)
                .setCanAutosizeButton(true)
                .setIsMultiSelect(true)
                .setItems(CardTagItem.getCompatible(effect.currentSlot.slotColor))
                .setTooltip(PGR.core.strings.cardEditor.flags, PGR.core.strings.cardEditorTutorial.primaryFlags);
        seriesDropdown = new EUISearchableDropdown<PCLLoadout>(new EUIHitbox(flagsDropdown.hb.x + flagsDropdown.hb.width + SPACING_WIDTH, screenH(0.6f), MENU_WIDTH, MENU_HEIGHT), PCLLoadout::getName)
                .setOnChange(selectedSeries -> {
                    effect.modifyAllBuilders(e -> e.setLoadout(!selectedSeries.isEmpty() ? selectedSeries.get(0) : null));
                })
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.seriesUI.seriesUI)
                .setCanAutosizeButton(true)
                .setShowClearForSingle(true)
                .setTooltip(PGR.core.strings.seriesUI.seriesUI, PGR.core.strings.cardEditorTutorial.attrAffinity);

        seriesDropdown
                .setActive(GameUtilities.isPCLCardColor(effect.currentSlot.slotColor));

        maxUpgrades = new PCLValueEditor(new EUIHitbox(START_X, screenH(0.5f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cardEditor.maxUpgrades, (val) -> effect.modifyAllBuilders(e -> e.setMaxUpgrades(val)))
                .setLimits(-1, PSkill.DEFAULT_MAX);
        uniqueToggle = (EUIToggle) new EUIToggle(new EUIHitbox(screenW(0.35f), screenH(0.5f), MENU_WIDTH, MENU_HEIGHT))
                .setFont(EUIFontHelper.carddescriptionfontNormal, 0.9f)
                .setText(PGR.core.tooltips.unique.title)
                .setOnToggle(val -> effect.modifyAllBuilders(e -> {
                    e.setUnique(val);
                }))
                .setTooltip(PGR.core.tooltips.unique);

        PCLResources resources = PGR.getResources(effect.currentSlot.slotColor);
        if (resources != null)
        {
            seriesDropdown.setItems(PCLLoadout.getAll(effect.currentSlot.slotColor));
        }
        else
        {
            seriesDropdown.setActive(false);
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
        nameInput.setLabel(effect.getBuilder().strings.NAME);
        raritiesDropdown.setSelection(effect.getBuilder().cardRarity, false);
        typesDropdown.setSelection(effect.getBuilder().cardType, false);
        targetDropdown.setSelection(effect.getBuilder().cardTarget, false);
        seriesDropdown.setSelection(effect.getBuilder().loadout, false);
        flagsDropdown.setSelection(effect.getBuilder().extraTags, false);
        maxUpgrades.setValue(effect.getBuilder().maxUpgradeLevel, false);
        uniqueToggle.setToggle(effect.getBuilder().unique);
    }

    @Override
    public TextureCache getTextureCache()
    {
        return PGR.core.images.editorPrimary;
    }

    @Override
    public void updateImpl()
    {
        header.tryUpdate();
        maxUpgrades.tryUpdate();
        seriesDropdown.tryUpdate();
        flagsDropdown.tryUpdate();
        raritiesDropdown.tryUpdate();
        typesDropdown.tryUpdate();
        targetDropdown.tryUpdate();
        languageDropdown.tryUpdate();
        nameInput.tryUpdate();
        uniqueToggle.tryUpdate();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        header.tryRender(sb);
        maxUpgrades.tryRender(sb);
        raritiesDropdown.tryRender(sb);
        typesDropdown.tryRender(sb);
        targetDropdown.tryRender(sb);
        seriesDropdown.tryRender(sb);
        flagsDropdown.tryRender(sb);
        languageDropdown.tryRender(sb);
        nameInput.tryRender(sb);
        uniqueToggle.tryRender(sb);
    }

    private void updateLanguage(Settings.GameLanguage language)
    {
        activeLanguage = language;
        nameInput.setFont(language == Settings.language ? EUIFontHelper.cardtitlefontNormal : EUIFontHelper.createBoldFont(language, true, 27.0F, 2f, bc1, 3f, sc1), 0.7f)
                .setLabel(effect.getBuilder().getStringsForLanguage(activeLanguage).NAME);
    }
}
