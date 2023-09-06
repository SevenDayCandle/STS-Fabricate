package pinacolada.ui.editor.potion;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.leaderboards.LeaderboardScreen;
import com.megacrit.cardcrawl.screens.options.OptionsPanel;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.controls.EUISearchableDropdown;
import extendedui.ui.controls.EUITextBoxInput;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.effects.screen.PCLCustomColorPickerEffect;
import pinacolada.potions.PCLCustomPotionSlot;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.ui.PCLValueEditor;
import pinacolada.ui.editor.PCLCustomColorEditor;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomGenericPage;
import pinacolada.utilities.GameUtilities;

import java.util.Arrays;
import java.util.List;

public class PCLCustomPotionPrimaryInfoPage extends PCLCustomGenericPage {
    protected PCLCustomPotionEditPotionScreen effect;
    protected EUILabel header;
    protected EUITextBoxInput idInput;
    protected EUITextBoxInput nameInput;
    protected EUISearchableDropdown<Settings.GameLanguage> languageDropdown;
    protected EUIDropdown<AbstractPotion.PotionRarity> rarityDropdown;
    protected EUIDropdown<AbstractPotion.PotionSize> sizeDropdown;
    protected EUIDropdown<AbstractPotion.PotionEffect> effectDropdown;
    protected EUILabel idWarning;
    protected PCLCustomColorEditor liquidColorEditor;
    protected PCLCustomColorEditor hybridColorEditor;
    protected PCLCustomColorEditor spotsColorEditor;
    protected PCLValueEditor maxUpgrades;
    protected PCLValueEditor branchUpgrades;
    protected Settings.GameLanguage activeLanguage = Settings.language;

    public PCLCustomPotionPrimaryInfoPage(PCLCustomPotionEditPotionScreen effect) {
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
        rarityDropdown = new EUIDropdown<AbstractPotion.PotionRarity>(new EUIHitbox(START_X, screenH(0.62f), MENU_WIDTH, MENU_HEIGHT), EUIGameUtils::textForPotionRarity)
                .setOnChange(rarities -> {
                    if (!rarities.isEmpty()) {
                        effect.modifyAllBuilders((e, i) -> e.setRarity(rarities.get(0)));
                    }
                })
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .setItems(getEligibleRarities())
                .setTooltip(CardLibSortHeader.TEXT[0], PGR.core.strings.cetut_potionRarity);
        sizeDropdown = new EUIDropdown<AbstractPotion.PotionSize>(new EUIHitbox(rarityDropdown.hb.x + rarityDropdown.hb.width + SPACING_WIDTH, screenH(0.62f), MENU_WIDTH, MENU_HEIGHT)
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setOnChange(types -> {
                    if (!types.isEmpty()) {
                        effect.modifyAllBuilders((e, i) -> e.setSize(types.get(0)));
                    }
                })
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.potion_size)
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
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.potion_visualEffect)
                .setCanAutosizeButton(true)
                .setItems(AbstractPotion.PotionEffect.values())
                .setTooltip(EUIRM.strings.potion_visualEffect, PGR.core.strings.cetut_potionEffect);
        liquidColorEditor = new PCLCustomColorEditor(new EUIHitbox(START_X, screenH(0.5f), MENU_WIDTH, MENU_HEIGHT), PGR.core.strings.cedit_liquidColor,
                this::openColorEditor, color -> {
            effect.modifyAllBuilders((e, i) -> e.setLiquidColor(color));
        })
                .setTooltip(PGR.core.strings.cedit_liquidColor, PGR.core.strings.cetut_potionColor);
        hybridColorEditor = new PCLCustomColorEditor(new EUIHitbox(liquidColorEditor.hb.x + liquidColorEditor.hb.width + SPACING_WIDTH * 3, screenH(0.5f), MENU_WIDTH, MENU_HEIGHT), PGR.core.strings.cedit_hybridColor,
                this::openColorEditor, color -> {
            effect.modifyAllBuilders((e, i) -> e.setHybridColor(color));
        })
                .setTooltip(PGR.core.strings.cedit_hybridColor, PGR.core.strings.cetut_potionColor);
        spotsColorEditor = new PCLCustomColorEditor(new EUIHitbox(hybridColorEditor.hb.x + hybridColorEditor.hb.width + SPACING_WIDTH * 3, screenH(0.5f), MENU_WIDTH, MENU_HEIGHT), PGR.core.strings.cedit_spotsColor,
                this::openColorEditor, color -> {
            effect.modifyAllBuilders((e, i) -> e.setSpotsColor(color));
        })
                .setTooltip(PGR.core.strings.cedit_spotsColor, PGR.core.strings.cetut_potionColor);
        maxUpgrades = new PCLValueEditor(new EUIHitbox(START_X, screenH(0.4f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cedit_maxUpgrades, this::modifyMaxUpgrades)
                .setLimits(-1, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.cedit_maxUpgrades, PGR.core.strings.cetut_maxUpgrades)
                .setHasInfinite(true, true);
        branchUpgrades = new PCLValueEditor(new EUIHitbox(screenW(0.35f), screenH(0.4f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cedit_branchUpgrade, (val) -> effect.modifyAllBuilders((e, i) -> e.setBranchFactor(val)))
                .setLimits(0, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.cedit_branchUpgrade, PGR.core.strings.cetut_branchUpgrade)
                .setHasInfinite(true, true);

        refresh();
    }

    public static List<AbstractPotion.PotionRarity> getEligibleRarities() {
        return PGR.config.showIrrelevantProperties.get() ? Arrays.asList(AbstractPotion.PotionRarity.values()) : GameUtilities.getStandardPotionTiers();
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
        EUITourTooltip.queueFirstView(PGR.config.tourRelicPrimary,
                idInput.makeTour(true),
                nameInput.makeTour(true),
                languageDropdown.makeTour(true),
                rarityDropdown.makeTour(true),
                sizeDropdown.makeTour(true));
    }

    protected void openColorEditor(PCLCustomColorEditor editor) {
        effect.currentDialog = new PCLCustomColorPickerEffect(editor.header.text, editor.getColor())
                .addCallback(editor::setColor);
    }

    @Override
    public void refresh() {
        idInput.setLabel(StringUtils.removeStart(effect.getBuilder().ID, PCLCustomPotionSlot.getBaseIDPrefix(effect.getBuilder().cardColor)));
        nameInput.setLabel(effect.getBuilder().strings.NAME);
        rarityDropdown.setSelection(effect.getBuilder().rarity, false);
        sizeDropdown.setSelection(effect.getBuilder().size, false);
        effectDropdown.setSelection(effect.getBuilder().effect, false);
        liquidColorEditor.setColor(effect.getBuilder().liquidColor, false);
        hybridColorEditor.setColor(effect.getBuilder().hybridColor, false);
        spotsColorEditor.setColor(effect.getBuilder().spotsColor, false);
        maxUpgrades.setValue(effect.getBuilder().maxUpgradeLevel, false);
        branchUpgrades.setValue(effect.getBuilder().branchFactor, false);

        effect.upgradeToggle.setActive(effect.getBuilder().maxUpgradeLevel != 0);
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
    }

    private void updateLanguage(Settings.GameLanguage language) {
        activeLanguage = language;
        nameInput.setFont(language == Settings.language ? EUIFontHelper.cardTitleFontNormal : EUIFontHelper.createBoldFont(language, true, 27.0F, 2f, PCLCard.CARD_TYPE_COLOR, 3f, PCLCard.SHADOW_COLOR), 0.7f)
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
