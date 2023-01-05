package pinacolada.ui.cardEditor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import extendedui.EUIUtils;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLAttackType;
import pinacolada.cards.base.PCLCardBuilder;
import pinacolada.cards.base.PCLCardTagInfo;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.effects.AttackEffects;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;

import java.util.ArrayList;
import java.util.List;

public class PCLCustomCardAttributesPage extends PCLCustomCardEditorPage
{
    public static final int EFFECT_COUNT = 2;

    public static final float MENU_WIDTH = scale(160);
    public static final float MENU_HEIGHT = scale(40);
    public static final float SPACING_WIDTH = screenW(0.06f);
    protected static final float START_X = screenW(0.25f);
    protected static final float PAD_X = AbstractCard.IMG_WIDTH * 0.75f + Settings.CARD_VIEW_PAD_X;
    protected static final float PAD_Y = scale(10);

    protected List<PCLAffinity> availableAffinities;
    protected PCLCustomCardEditCardScreen effect;
    protected EUILabel header;
    protected EUIDropdown<PCLCardTagInfo> tagsDropdown;
    protected EUIDropdown<PCLAttackType> attackTypeDropdown;
    protected EUIDropdown<AbstractGameAction.AttackEffect> attackEffectDropdown;
    protected PCLCustomCardUpgradableEditor costEditor;
    protected PCLCustomCardUpgradableEditor damageEditor;
    protected PCLCustomCardUpgradableEditor blockEditor;
    protected PCLCustomCardUpgradableEditor magicNumberEditor;
    protected PCLCustomCardUpgradableEditor hpEditor;
    protected PCLCustomCardUpgradableEditor hitCountEditor;
    protected PCLCustomCardUpgradableEditor rightCountEditor;
    protected ArrayList<PCLCustomCardAffinityValueEditor> affinityEditors = new ArrayList<>();
    protected EUILabel upgradeLabel;
    protected EUILabel upgradeLabel2;

    public PCLCustomCardAttributesPage(PCLCustomCardEditCardScreen effect)
    {
        this.effect = effect;
        availableAffinities = new ArrayList<>(PCLAffinity.getAvailableAffinitiesAsList(effect.currentSlot.slotColor));
        if (availableAffinities.size() > 0)
        {
            availableAffinities.add(PCLAffinity.Star);
        }

        this.header = new EUILabel(EUIFontHelper.cardtitlefontSmall,
                new EUIHitbox(screenW(0.5f), screenH(0.93f), MENU_WIDTH, MENU_HEIGHT))
                .setAlignment(0.5f, 0.0f, false)
                .setFont(EUIFontHelper.cardtitlefontLarge, 0.8f).setColor(Color.LIGHT_GRAY)
                .setLabel(PGR.core.strings.cardEditor.attributes);

        attackTypeDropdown = new EUIDropdown<PCLAttackType>(new EUIHitbox(START_X, screenH(0.72f), MENU_WIDTH, MENU_HEIGHT)
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setOnChange(targets -> {
                    if (!targets.isEmpty())
                    {
                        effect.modifyBuilder(e -> e.setAttackType(targets.get(0)));
                    }
                })
                .setLabelFunctionForOption(c -> c.getTooltip() != null ? c.getTooltip().title : "", false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cardEditor.attackType)
                .setCanAutosizeButton(true)
                .setItems(EUIUtils.filter(PCLAttackType.values(), v -> v.getTooltip() != null));
        attackEffectDropdown = new EUIDropdown<AbstractGameAction.AttackEffect>(new EUIHitbox(screenW(0.4f), screenH(0.72f), MENU_WIDTH, MENU_HEIGHT)
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setOnChange(targets -> {
                    if (!targets.isEmpty())
                    {
                        effect.modifyBuilder(e -> e.setAttackEffect(targets.get(0)));
                    }
                })
                .setLabelFunctionForOption(Enum::name, false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cardEditor.attackEffect)
                .setCanAutosizeButton(true)
                .setItems(AttackEffects.keys());
        tagsDropdown = new EUIDropdown<PCLCardTagInfo>(new EUIHitbox(START_X, screenH(0.6f), MENU_WIDTH * 1.2f, MENU_HEIGHT))
                .setOnChange(tags -> effect.modifyBuilder(e -> e.setTags(tags)))
                .setLabelFunctionForOption(item -> item.tag.getTip().getTitleOrIcon() + " " + item.tag.getTip().title, true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cardEditor.tags)
                .setIsMultiSelect(true)
                .setCanAutosize(true, true);
        tagsDropdown.setLabelFunctionForButton((list, __) -> tagsDropdown.makeMultiSelectString(item -> item.tag.getTooltip().getTitleOrIcon()), null, true)
                .setHeaderRow(new PCLCustomCardTagEditorHeaderRow(tagsDropdown))
                .setRowFunction(PCLCustomCardTagEditorRow::new)
                .setRowWidthFunction((a, b, c) -> a.calculateRowWidth() + MENU_HEIGHT * 6)
                .setItems(EUIUtils.map(PCLCardTag.getAll(), t -> t.make(1, 1)))
                .setTooltip(PGR.core.strings.cardEditor.tags, EUIUtils.joinStrings(EUIUtils.DOUBLE_SPLIT_LINE, PGR.core.strings.cardEditorTutorial.attrTags1, PGR.core.strings.cardEditorTutorial.attrTags2));

        // Number editors

        float curW = START_X;
        upgradeLabel = new EUILabel(EUIFontHelper.cardtitlefontSmall,
                new EUIHitbox(curW, screenH(0.45f) - MENU_HEIGHT * 0.8f, MENU_WIDTH / 4, MENU_HEIGHT))
                .setAlignment(0.5f, 0.0f, false)
                .setFont(EUIFontHelper.cardtitlefontSmall, 0.6f).setColor(Color.LIGHT_GRAY)
                .setLabel(PGR.core.strings.cardEditor.upgrades);
        curW += SPACING_WIDTH;
        costEditor = new PCLCustomCardUpgradableEditor(new EUIHitbox(curW, screenH(0.45f), MENU_WIDTH / 4, MENU_HEIGHT)
                , CardLibSortHeader.TEXT[3], (val, upVal) -> effect.modifyBuilder(e -> e.setCosts(val).setCostUpgrades(upVal)))
                .setLimits(-2, PSkill.DEFAULT_MAX);
        curW += SPACING_WIDTH;
        damageEditor = new PCLCustomCardUpgradableEditor(new EUIHitbox(curW, screenH(0.45f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cardEditor.damage, (val, upVal) -> effect.modifyBuilder(e -> e.setDamage(val, upVal, e.hitCount, e.hitCountUpgrade)))
                .setLimits(0, PSkill.DEFAULT_MAX);
        curW += SPACING_WIDTH;
        blockEditor = new PCLCustomCardUpgradableEditor(new EUIHitbox(curW, screenH(0.45f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cardEditor.block, (val, upVal) -> effect.modifyBuilder(e -> e.setBlock(val, upVal, e.rightCount, e.rightCountUpgrade)))
                .setLimits(0, PSkill.DEFAULT_MAX);
        curW += SPACING_WIDTH;
        hitCountEditor = new PCLCustomCardUpgradableEditor(new EUIHitbox(curW, screenH(0.45f), MENU_WIDTH / 4, MENU_HEIGHT)
                , EUIUtils.format(PGR.core.strings.cardEditor.hitCount, PGR.core.strings.cardEditor.damage), (val, upVal) -> effect.modifyBuilder(e -> e.setHitCount(val, upVal)))
                .setLimits(1, PSkill.DEFAULT_MAX);
        curW += SPACING_WIDTH;
        rightCountEditor = new PCLCustomCardUpgradableEditor(new EUIHitbox(curW, screenH(0.45f), MENU_WIDTH / 4, MENU_HEIGHT)
                , EUIUtils.format(PGR.core.strings.cardEditor.hitCount, PGR.core.strings.cardEditor.block), (val, upVal) -> effect.modifyBuilder(e -> e.setRightCount(val, upVal)))
                .setLimits(1, PSkill.DEFAULT_MAX);
        curW += SPACING_WIDTH;
        magicNumberEditor = new PCLCustomCardUpgradableEditor(new EUIHitbox(curW, screenH(0.45f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cardEditor.magicNumber, (val, upVal) -> effect.modifyBuilder(e -> e.setMagicNumber(val, upVal)))
                .setLimits(0, PSkill.DEFAULT_MAX);
        curW += SPACING_WIDTH;
        hpEditor = new PCLCustomCardUpgradableEditor(new EUIHitbox(curW, screenH(0.45f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cardEditor.secondaryNumber, (val, upVal) -> effect.modifyBuilder(e -> e.setHp(val, upVal)))
                .setLimits(0, PSkill.DEFAULT_MAX);

        // Affinity editors

        curW = START_X;
        upgradeLabel2 = new EUILabel(EUIFontHelper.cardtitlefontSmall,
                new EUIHitbox(curW, screenH(0.32f) - MENU_HEIGHT * 0.8f, MENU_WIDTH / 4, MENU_HEIGHT))
                .setAlignment(0.5f, 0.0f, false)
                .setFont(EUIFontHelper.cardtitlefontSmall, 0.6f).setColor(Color.LIGHT_GRAY)
                .setLabel(PGR.core.strings.cardEditor.upgrades);
        boolean canShowLabels = availableAffinities.size() > 0;
        upgradeLabel2.setActive(canShowLabels);

        curW += SPACING_WIDTH;
        for (PCLAffinity affinity : availableAffinities)
        {
            affinityEditors.add(new PCLCustomCardAffinityValueEditor(new EUIHitbox(curW, screenH(0.32f), MENU_WIDTH / 4, MENU_HEIGHT)
                    , affinity, (af, val, upVal) -> effect.modifyBuilder(e -> e.setAffinities(af, val, upVal))));
            curW += SPACING_WIDTH;
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
        PCLCardBuilder builder = effect.getBuilder();
        boolean isSummon = builder.cardType == PCLEnum.CardType.SUMMON;

        costEditor.setValue(builder.getCost(0), builder.getCostUpgrade(0));
        damageEditor.setValue(builder.getDamage(0), builder.getDamageUpgrade(0));
        blockEditor.setValue(builder.getBlock(0), builder.getBlockUpgrade(0));
        hitCountEditor.setValue(builder.getHitCount(0), builder.getHitCountUpgrade(0));
        rightCountEditor.setValue(builder.getRightCount(0), builder.getRightCountUpgrade(0));
        tagsDropdown.setSelection(EUIUtils.filter(builder.tags.values(), i -> i.get(0) != 0 || i.getUpgrade(0) != 0), false);
        attackTypeDropdown.setSelection(builder.attackType, false);
        attackEffectDropdown.setSelection(builder.attackEffect, false);
        magicNumberEditor.setValue(builder.getMagicNumber(0), builder.getMagicNumberUpgrade(0)).setActive(isSummon);
        hpEditor.setValue(builder.getHp(0), builder.getHpUpgrade(0)).setActive(isSummon);

        List<PCLCardTagInfo> infos = tagsDropdown.getAllItems();
        ArrayList<Integer> selection = new ArrayList<>();
        for (int i = 0; i < infos.size(); i++)
        {
            PCLCardTagInfo info = infos.get(i);
            if (builder.tags.containsKey(info.tag))
            {
                PCLCardTagInfo other = builder.tags.get(info.tag);
                info.set(0, other.get(0));
                info.setUpgrade(0, other.getUpgrade(0));
                selection.add(i);
            }
        }
        tagsDropdown.setSelectionIndices(selection, false);

        for (int i = 0; i < availableAffinities.size(); i++)
        {
            PCLAffinity a = availableAffinities.get(i);
            affinityEditors.get(i).setValue(builder.affinities.getLevel(a), builder.affinities.getUpgrade(a), false);
        }
    }

    @Override
    public TextureCache getTextureCache()
    {
        return PGR.core.images.editorAttribute;
    }

    @Override
    public void updateImpl()
    {
        header.tryUpdate();
        costEditor.tryUpdate();
        damageEditor.tryUpdate();
        blockEditor.tryUpdate();
        magicNumberEditor.tryUpdate();
        hpEditor.tryUpdate();
        hitCountEditor.tryUpdate();
        rightCountEditor.tryUpdate();
        tagsDropdown.tryUpdate();
        attackTypeDropdown.tryUpdate();
        attackEffectDropdown.tryUpdate();
        for (PCLCustomCardAffinityValueEditor aEditor : affinityEditors)
        {
            aEditor.tryUpdate();
        }
        upgradeLabel.tryUpdate();
        upgradeLabel2.tryUpdate();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        header.tryRender(sb);
        upgradeLabel.tryRender(sb);
        upgradeLabel2.tryRender(sb);
        for (PCLCustomCardAffinityValueEditor aEditor : affinityEditors)
        {
            aEditor.tryRender(sb);
        }
        costEditor.tryRender(sb);
        damageEditor.tryRender(sb);
        blockEditor.tryRender(sb);
        magicNumberEditor.tryRender(sb);
        hpEditor.tryRender(sb);
        hitCountEditor.tryRender(sb);
        rightCountEditor.tryRender(sb);
        attackTypeDropdown.tryRender(sb);
        attackEffectDropdown.tryRender(sb);
        tagsDropdown.tryRender(sb);
    }
}
