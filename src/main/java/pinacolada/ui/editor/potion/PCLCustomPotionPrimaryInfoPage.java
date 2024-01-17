package pinacolada.ui.editor.potion;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.leaderboards.LeaderboardScreen;
import com.megacrit.cardcrawl.screens.options.OptionsPanel;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.ui.EUIBase;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.potions.PCLCustomPotionSlot;
import pinacolada.potions.PCLDynamicPotionData;
import pinacolada.potions.PCLPotion;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.ui.PCLValueEditor;
import pinacolada.ui.editor.PCLCustomColorEditor;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomGenericPage;
import pinacolada.utilities.GameUtilities;

import java.util.Arrays;
import java.util.List;

public class PCLCustomPotionPrimaryInfoPage extends PCLCustomGenericPage {
    protected PCLCustomPotionEditScreen effect;
    protected EUILabel header;
    protected EUITextBoxInput idInput;
    protected EUITextBoxInput nameInput;
    protected EUISearchableDropdown<Settings.GameLanguage> languageDropdown;
    protected EUIDropdown<AbstractPotion.PotionRarity> rarityDropdown;
    protected EUIDropdown<AbstractPotion.PotionSize> sizeDropdown;
    protected EUIDropdown<AbstractPotion.PotionEffect> effectDropdown;
    protected EUIDialogColorPicker colorPicker;
    protected EUILabel idWarning;
    protected PCLCustomColorEditor liquidColorEditor;
    protected PCLCustomColorEditor hybridColorEditor;
    protected PCLCustomColorEditor spotsColorEditor;
    protected PCLValueEditor maxUpgrades;
    protected PCLValueEditor branchUpgrades;
    protected Settings.GameLanguage activeLanguage = Settings.language;

    public PCLCustomPotionPrimaryInfoPage(PCLCustomPotionEditScreen effect) {
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
        rarityDropdown = new EUIDropdown<AbstractPotion.PotionRarity>(new EUIHitbox(START_X, screenH(0.62f), MENU_WIDTH, MENU_HEIGHT), EUIGameUtils::textForPotionRarity)
                .setOnChange(rarities -> {
                    if (!rarities.isEmpty()) {
                        effect.modifyAllBuilders((e, i) -> e.setRarity(rarities.get(0)));
                    }
                })
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .setItems(AbstractPotion.PotionRarity.values())
                .setTooltip(CardLibSortHeader.TEXT[0], PGR.core.strings.cetut_potionRarity);
        sizeDropdown = new EUIDropdown<AbstractPotion.PotionSize>(new EUIHitbox(rarityDropdown.hb.x + rarityDropdown.hb.width + SPACING_WIDTH, screenH(0.62f), MENU_WIDTH, MENU_HEIGHT)
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setOnChange(types -> {
                    if (!types.isEmpty()) {
                        effect.modifyAllBuilders((e, i) -> e.setSize(types.get(0)));
                    }
                })
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.potion_size)
                .setCanAutosizeButton(true)
                .setItems(AbstractPotion.PotionSize.values())
                .setTooltip(EUIRM.strings.potion_size, PGR.core.strings.cetut_potionSize);
        effectDropdown = new EUIDropdown<AbstractPotion.PotionEffect>(new EUIHitbox(sizeDropdown.hb.x + sizeDropdown.hb.width + SPACING_WIDTH, screenH(0.62f), MENU_WIDTH, MENU_HEIGHT)
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setOnChange(types -> {
                    if (!types.isEmpty()) {
                        effect.modifyAllBuilders((e, i) -> e.setEffect(types.get(0)));
                    }
                })
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.potion_visualEffect)
                .setCanAutosizeButton(true)
                .setItems(AbstractPotion.PotionEffect.values())
                .setTooltip(EUIRM.strings.potion_visualEffect, PGR.core.strings.cetut_potionEffect);

        colorPicker = new EUIDialogColorPicker(new EUIHitbox(Settings.WIDTH * 0.7f, (Settings.HEIGHT - EUIBase.scale(800)) / 2f, EUIBase.scale(460), EUIBase.scale(800)), EUIUtils.EMPTY_STRING, EUIUtils.EMPTY_STRING);
        colorPicker
                .setShowDark(false)
                .setActive(false);

        liquidColorEditor = new PCLCustomColorEditor(new EUIHitbox(START_X, screenH(0.5f), MENU_WIDTH, MENU_HEIGHT), PGR.core.strings.cedit_liquidColor,
                (e) -> this.openColorEditor(e, PCLPotion::manualAdjustLiquid),
                color -> {
            effect.modifyAllBuilders((e, i) -> e.setLiquidColor(color));
        })
                .setTooltip(PGR.core.strings.cedit_liquidColor, PGR.core.strings.cetut_potionColor);
        hybridColorEditor = new PCLCustomColorEditor(new EUIHitbox(liquidColorEditor.hb.x + liquidColorEditor.hb.width + SPACING_WIDTH * 3, screenH(0.5f), MENU_WIDTH, MENU_HEIGHT), PGR.core.strings.cedit_hybridColor,
                (e) -> this.openColorEditor(e, PCLPotion::manualAdjustHybrid),
                color -> {
            effect.modifyAllBuilders((e, i) -> e.setHybridColor(color));
        })
                .setTooltip(PGR.core.strings.cedit_hybridColor, PGR.core.strings.cetut_potionColor);
        spotsColorEditor = new PCLCustomColorEditor(new EUIHitbox(hybridColorEditor.hb.x + hybridColorEditor.hb.width + SPACING_WIDTH * 3, screenH(0.5f), MENU_WIDTH, MENU_HEIGHT), PGR.core.strings.cedit_spotsColor,
                (e) -> this.openColorEditor(e, PCLPotion::manualAdjustSpots),
                color -> {
            effect.modifyAllBuilders((e, i) -> e.setSpotsColor(color));
        })
                .setTooltip(PGR.core.strings.cedit_spotsColor, PGR.core.strings.cetut_potionColor);
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
                rarityDropdown.makeTour(true),
                sizeDropdown.makeTour(true),
                effectDropdown.makeTour(true),
                liquidColorEditor.makeTour(true),
                hybridColorEditor.makeTour(true),
                spotsColorEditor.makeTour(true),
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
        EUITourTooltip.queueFirstView(PGR.config.tourPotionPrimary, getTour());
    }

    private void openColorEditor(PCLCustomColorEditor editor, ActionT2<PCLPotion, Color> onChange) {
        Color prev = editor.getColor().cpy();
        colorPicker
                .setOnChange((res) -> {
                    if (effect.preview != null) {
                        onChange.invoke(effect.preview, res.getReturnColor());
                    }
                })
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
        PCLDynamicPotionData builder = effect.getBuilder();

        idInput.setLabel(StringUtils.removeStart(builder.ID, PCLCustomPotionSlot.getBaseIDPrefix(builder.cardColor)));
        nameInput.setLabel(builder.strings.NAME);
        rarityDropdown.setSelection(builder.rarity, false);
        sizeDropdown.setSelection(builder.size, false);
        effectDropdown.setSelection(builder.effect, false);
        liquidColorEditor.setColor(builder.liquidColor, false);
        hybridColorEditor.setColor(builder.hybridColor, false);
        spotsColorEditor.setColor(builder.spotsColor, false);
        maxUpgrades.setValue(builder.maxUpgradeLevel, false);
        branchUpgrades.setValue(builder.branchFactor, false);

        effect.updateUpgradeEditorLimits(builder.maxUpgradeLevel);
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        header.tryRender(sb);
        idWarning.tryRender(sb);
        rarityDropdown.tryRender(sb);
        sizeDropdown.tryRender(sb);
        effectDropdown.tryRender(sb);
        liquidColorEditor.tryRender(sb);
        hybridColorEditor.tryRender(sb);
        spotsColorEditor.tryRender(sb);
        languageDropdown.tryRender(sb);
        nameInput.tryRender(sb);
        idInput.tryRender(sb);
        maxUpgrades.tryRender(sb);
        branchUpgrades.tryRender(sb);
        colorPicker.tryRender(sb);
    }

    @Override
    public void updateImpl() {
        header.tryUpdate();
        idWarning.tryUpdate();
        rarityDropdown.tryUpdate();
        sizeDropdown.tryUpdate();
        effectDropdown.tryUpdate();
        liquidColorEditor.tryUpdate();
        hybridColorEditor.tryUpdate();
        spotsColorEditor.tryUpdate();
        languageDropdown.tryUpdate();
        nameInput.tryUpdate();
        idInput.tryUpdate();
        maxUpgrades.tryUpdate();
        branchUpgrades.tryUpdate();
        colorPicker.tryUpdate();
    }

    private void updateLanguage(Settings.GameLanguage language) {
        activeLanguage = language;
        nameInput.setFont(language == Settings.language ? FontHelper.cardTitleFont : EUIFontHelper.createBoldFont(language, true, 27.0F, 2f, PCLCard.CARD_TYPE_COLOR, 3f, PCLCard.SHADOW_COLOR), 0.7f)
                .setLabel(effect.getBuilder().getStringsForLanguage(activeLanguage).NAME);
    }

    private void validifyCardID(String cardID) {
        String fullID = PCLCustomPotionSlot.getBaseIDPrefix(effect.getBuilder().cardColor) + cardID;
        if (!fullID.equals(effect.currentSlot.ID) && PCLCustomPotionSlot.isIDDuplicate(fullID, effect.getBuilder().cardColor)) {
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
