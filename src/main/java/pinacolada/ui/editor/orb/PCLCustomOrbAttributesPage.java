package pinacolada.ui.editor.orb;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.controls.EUIDropdownRow;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLDynamicCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTagInfo;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.orbs.PCLDynamicOrbData;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.delay.DelayTiming;
import pinacolada.ui.PCLValueEditor;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomGenericPage;
import pinacolada.ui.editor.PCLCustomUpgradableEditor;
import pinacolada.ui.editor.card.PCLCustomCardAffinityValueEditor;
import pinacolada.ui.editor.card.PCLCustomCardEditScreen;
import pinacolada.ui.editor.card.PCLCustomCardTagEditorHeaderRow;
import pinacolada.ui.editor.card.PCLCustomCardTagEditorRow;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PCLCustomOrbAttributesPage extends PCLCustomGenericPage {
    protected static final float START_X = screenW(0.25f);
    public static final float MENU_WIDTH = scale(160);
    public static final float MENU_HEIGHT = scale(40);
    public static final float SPACING_WIDTH = screenW(0.06f);
    protected PCLCustomOrbEditScreen screen;
    protected EUILabel header;
    protected EUIDropdown<DelayTiming> timingDropdown;
    protected PCLCustomUpgradableEditor baseEvoke;
    protected PCLCustomUpgradableEditor basePassive;
    protected EUIToggle applyFocusToEvokeToggle;
    protected EUIToggle applyFocusToPassiveToggle;
    protected EUILabel upgradeLabel;

    public PCLCustomOrbAttributesPage(PCLCustomOrbEditScreen screen) {
        this.screen = screen;

        this.header = new EUILabel(EUIFontHelper.cardTitleFontSmall,
                new EUIHitbox(screenW(0.5f), PCLCustomEditEntityScreen.START_Y, MENU_WIDTH, MENU_HEIGHT))
                .setAlignment(0.5f, 0.0f, false)
                .setFont(EUIFontHelper.cardTitleFontLarge, 0.8f).setColor(Color.LIGHT_GRAY)
                .setLabel(PGR.core.strings.cedit_attributes);

        timingDropdown = new EUIDropdown<DelayTiming>(new EUIHitbox(START_X, screenH(0.8f), MENU_WIDTH, MENU_HEIGHT)
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setOnChange(targets -> {
                    if (!targets.isEmpty()) {
                        screen.modifyBuilder(e -> e.setTiming(targets.get(0)));
                    }
                })
                .setLabelFunctionForOption(DelayTiming::getTitle, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.tooltips.timing.title)
                .setCanAutosizeButton(true)
                .setItems(DelayTiming.values())
                .setTooltip(PGR.core.tooltips.timing);

        // Number editors
        float curW = START_X;
        upgradeLabel = new EUILabel(EUIFontHelper.cardTitleFontSmall,
                new EUIHitbox(curW, screenH(0.7f) - MENU_HEIGHT * 0.8f, MENU_WIDTH / 4, MENU_HEIGHT))
                .setAlignment(0.5f, 0.0f, false)
                .setFont(EUIFontHelper.cardTitleFontSmall, 0.6f).setColor(Color.LIGHT_GRAY)
                .setLabel(PGR.core.strings.cedit_upgrades)
                .setTooltip(PGR.core.strings.cedit_upgrades, PGR.core.strings.cetut_amount);
        curW += SPACING_WIDTH;
        basePassive = new PCLCustomUpgradableEditor(new EUIHitbox(curW, screenH(0.7f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.tooltips.trigger.title, (val, upVal) -> screen.modifyAllBuilders((e, i) -> e.setBasePassiveForForm(screen.currentBuilder, screen.getTempBuilders().size(), val, upVal)))
                .setLimits(0, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.tooltips.trigger.title, PGR.core.strings.cetut_amount);
        curW += SPACING_WIDTH;
        baseEvoke = new PCLCustomUpgradableEditor(new EUIHitbox(curW, screenH(0.7f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.tooltips.evoke.title, (val, upVal) -> screen.modifyAllBuilders((e, i) -> e.setBaseEvokeForForm(screen.currentBuilder, screen.getTempBuilders().size(), val, upVal)))
                .setLimits(-2, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.tooltips.evoke.title, PGR.core.strings.cetut_amount);

        basePassive.setOnTab(() -> baseEvoke.start());

        // Toggle editors

        applyFocusToPassiveToggle = new EUIToggle(new EUIHitbox(START_X, screenH(0.6f), MENU_WIDTH, MENU_HEIGHT))
                .setFont(EUIFontHelper.cardDescriptionFontNormal, 0.9f)
                .setText(PGR.core.strings.cedit_orbFocusPassive)
                .setOnToggle(val -> screen.modifyBuilder((e) -> {
                    e.setApplyFocusToPassive(val);
                }))
                .setTooltip(new EUITooltip(PGR.core.strings.cedit_orbFocusPassive, PGR.core.strings.cetut_orbFocusPassive));
        applyFocusToEvokeToggle = new EUIToggle(new EUIHitbox(START_X, screenH(0.57f), MENU_WIDTH, MENU_HEIGHT))
                .setFont(EUIFontHelper.cardDescriptionFontNormal, 0.9f)
                .setText(PGR.core.strings.cedit_orbFocusEvoke)
                .setOnToggle(val -> screen.modifyBuilder((e) -> {
                    e.setApplyFocusToEvoke(val);
                }))
                .setTooltip(new EUITooltip(PGR.core.strings.cedit_orbFocusEvoke, PGR.core.strings.cetut_orbFocusEvoke));

        refresh();
    }

    public static ArrayList<PCLAffinity> getEligibleAffinities(AbstractCard.CardColor color) {
        ArrayList<PCLAffinity> availableAffinities = new ArrayList<>(PGR.config.showIrrelevantProperties.get() ? Arrays.asList(PCLAffinity.basic()) : PCLAffinity.getAvailableAffinitiesAsList(color, false));
        if (availableAffinities.size() > 0) {
            availableAffinities.add(PCLAffinity.Star);
        }
        return availableAffinities;
    }

    // Colorless/Curse should not be able to see Summon in the card editor
    public static List<PCLCardTarget> getEligibleTargets(AbstractCard.CardColor color) {
        if (GameUtilities.isPCLOnlyCardColor(color) || PGR.config.showIrrelevantProperties.get()) {
            return PCLCardTarget.getAll();
        }
        return EUIUtils.filterInPlace(PCLCardTarget.getAll(), PCLCardTarget::vanillaCompatible);
    }

    @Override
    public TextureCache getTextureCache() {
        return EUIRM.images.tag;
    }

    @Override
    public String getTitle() {
        return header.text;
    }

    @Override
    public EUITourTooltip[] getTour() {
        return EUIUtils.array(
                timingDropdown.makeTour(true),
                upgradeLabel.makeTour(true),
                applyFocusToPassiveToggle.makeTour(true),
                applyFocusToEvokeToggle.makeTour(true)
        );
    }

    @Override
    public void onOpen() {
        EUITourTooltip.queueFirstView(PGR.config.tourCardAttribute, getTour());
    }

    @Override
    public void refresh() {
        PCLDynamicOrbData builder = screen.getBuilder();
        int form = screen.currentBuilder;

        basePassive.setValue(builder.getBasePassive(form), builder.getBasePassiveUpgrade(form));
        baseEvoke.setValue(builder.getBaseEvoke(form), builder.getBaseEvokeUpgrade(form));

        timingDropdown.setSelection(builder.timing, false);

        applyFocusToPassiveToggle.setToggle(builder.applyFocusToPassive);
        applyFocusToEvokeToggle.setToggle(builder.applyFocusToEvoke);
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        header.tryRender(sb);
        upgradeLabel.tryRender(sb);
        applyFocusToPassiveToggle.tryRender(sb);
        applyFocusToEvokeToggle.tryRender(sb);
        basePassive.tryRender(sb);
        baseEvoke.tryRender(sb);
        timingDropdown.tryRender(sb);
    }

    @Override
    public void updateImpl() {
        header.tryUpdate();
        applyFocusToPassiveToggle.tryUpdate();
        applyFocusToEvokeToggle.tryUpdate();
        basePassive.tryUpdate();
        baseEvoke.tryUpdate();
        timingDropdown.tryUpdate();
        upgradeLabel.tryUpdate();
    }
}
