package pinacolada.ui.editor.power;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
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
import pinacolada.powers.PCLCustomPowerSlot;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.ui.PCLValueEditor;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomGenericPage;
import pinacolada.ui.editor.PCLCustomUpgradableEditor;
import pinacolada.utilities.GameUtilities;

import java.util.Arrays;
import java.util.List;

public class PCLCustomPowerPrimaryInfoPage extends PCLCustomGenericPage {
    protected PCLCustomPowerEditPowerScreen effect;
    protected EUILabel header;
    protected EUITextBoxInput idInput;
    protected EUITextBoxInput nameInput;
    protected EUITextBoxInput flavorInput; // TODO implement this once you have multi-line input available
    protected EUISearchableDropdown<Settings.GameLanguage> languageDropdown;
    protected EUIDropdown<AbstractPower.PowerType> typeDropdown;
    protected EUIDropdown<PCLPowerData.Behavior> endTurnBehaviorDropdown;
    protected EUILabel idWarning;
    protected PCLCustomUpgradableEditor minMaxAmount;
    protected PCLValueEditor priority;
    protected Settings.GameLanguage activeLanguage = Settings.language;

    public PCLCustomPowerPrimaryInfoPage(PCLCustomPowerEditPowerScreen effect) {
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
        typeDropdown = new EUIDropdown<AbstractPower.PowerType>(new EUIHitbox(START_X, screenH(0.62f), MENU_WIDTH, MENU_HEIGHT), GameUtilities::textForPowerType)
                .setOnChange(types -> {
                    if (!types.isEmpty()) {
                        effect.modifyAllBuilders((e, i) -> e.setType(types.get(0)));
                    }
                })
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[1])
                .setItems(AbstractPower.PowerType.values())
                .setTooltip(CardLibSortHeader.TEXT[1], PGR.core.strings.cetut_powerType);
        endTurnBehaviorDropdown = new EUIDropdown<PCLPowerData.Behavior>(new EUIHitbox(typeDropdown.hb.x + typeDropdown.hb.width + SPACING_WIDTH, screenH(0.62f), MENU_WIDTH, MENU_HEIGHT)
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setOnChange(types -> {
                    if (!types.isEmpty()) {
                        effect.modifyAllBuilders((e, i) -> e.setEndTurnBehavior(types.get(0)));
                    }
                })
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.power_turnBehavior)
                .setCanAutosizeButton(true)
                .setItems(PCLPowerData.Behavior.values())
                .setTooltip(PGR.core.strings.power_turnBehavior, PGR.core.strings.cetut_powerTurnBehavior);
        minMaxAmount = new PCLCustomUpgradableEditor(new EUIHitbox(START_X, screenH(0.41f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cedit_minMaxStacks, this::modifyMaxUpgrades)
                .setLimits(-PSkill.DEFAULT_MAX, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.cedit_minMaxStacks, PGR.core.strings.cetut_powerMinMaxStacks);
        priority = new PCLValueEditor(new EUIHitbox(screenW(0.35f), screenH(0.4f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.power_priority, (val) -> effect.modifyAllBuilders((e, i) -> e.setPriority(val)))
                .setLimits(-PSkill.DEFAULT_MAX, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.power_priority, PGR.core.strings.cetut_powerPriority)
                .setHasInfinite(true, true);

        refresh();
    }

    @Override
    public TextureCache getTextureCache() {
        return PCLCoreImages.Menu.editorPrimary;
    }

    public String getTitle() {
        return header.text;
    }

    protected void modifyMaxUpgrades(int min, int max) {
        effect.modifyAllBuilders((e, i) -> e.setLimits(min, max));
        effect.upgradeToggle.setActive(max > 0);
    }

    @Override
    public void onOpen() {
        EUITourTooltip.queueFirstView(PGR.config.tourRelicPrimary,
                idInput.makeTour(true),
                nameInput.makeTour(true),
                languageDropdown.makeTour(true),
                typeDropdown.makeTour(true),
                endTurnBehaviorDropdown.makeTour(true));
    }

    @Override
    public void refresh() {
        idInput.setLabel(StringUtils.removeStart(effect.getBuilder().ID, PCLCustomPowerSlot.BASE_POWER_ID));
        nameInput.setLabel(effect.getBuilder().strings.NAME);
        typeDropdown.setSelection(effect.getBuilder().type, false);
        endTurnBehaviorDropdown.setSelection(effect.getBuilder().endTurnBehavior, false);
        minMaxAmount.setValue(effect.getBuilder().minAmount, effect.getBuilder().maxAmount, false);
        priority.setValue(effect.getBuilder().priority, false);

        effect.upgradeToggle.setActive(effect.getBuilder().maxAmount > 0);
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        header.tryRender(sb);
        idWarning.tryRender(sb);
        typeDropdown.tryRender(sb);
        endTurnBehaviorDropdown.tryRender(sb);
        languageDropdown.tryRender(sb);
        nameInput.tryRender(sb);
        idInput.tryRender(sb);
        minMaxAmount.tryRender(sb);
        priority.tryRender(sb);
    }

    @Override
    public void updateImpl() {
        header.tryUpdate();
        idWarning.tryUpdate();
        typeDropdown.tryUpdate();
        endTurnBehaviorDropdown.tryUpdate();
        languageDropdown.tryUpdate();
        nameInput.tryUpdate();
        idInput.tryUpdate();
        minMaxAmount.tryUpdate();
        priority.tryUpdate();
    }

    private void updateLanguage(Settings.GameLanguage language) {
        activeLanguage = language;
        nameInput.setFont(language == Settings.language ? EUIFontHelper.cardTitleFontNormal : EUIFontHelper.createBoldFont(language, true, 27.0F, 2f, PCLCard.CARD_TYPE_COLOR, 3f, PCLCard.SHADOW_COLOR), 0.7f)
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
