package pinacolada.ui.cardEditor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.controls.EUISearchableDropdown;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.OriginRelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.cards.base.PCLCardBuilder;
import pinacolada.resources.PGR;
import pinacolada.skills.*;
import pinacolada.skills.skills.PDelay;
import pinacolada.skills.skills.PMultiCond;
import pinacolada.skills.skills.PMultiSkill;
import pinacolada.ui.common.PCLValueEditor;

import java.util.ArrayList;
import java.util.List;

import static pinacolada.ui.cardEditor.PCLCustomCardEditCardScreen.START_Y;

public class PCLCustomCardEffectPage extends PCLCustomCardEditorPage
{
    public static final float MENU_WIDTH = scale(200);
    public static final float MENU_HEIGHT = scale(40);
    public static final float OFFSET_EFFECT = -MENU_HEIGHT * 1.25f;
    public static final float OFFSET_AMOUNT = scale(10);

    public final PCLCardBuilder builder;
    protected PPrimary primaryCond;
    protected PMultiCond multiCond;
    protected PDelay delayMove;
    protected PMod modifier;
    protected PMultiSkill multiSkill;

    protected ActionT1<PSkill> onUpdate;
    protected ArrayList<ActionT0> toRemove = new ArrayList<>();
    protected ArrayList<PCond> lowerConditions = new ArrayList<>(EUIUtils.list((PCond) null));
    protected ArrayList<PSkill> lowerEffects = new ArrayList<>(EUIUtils.list((PSkill) null));
    protected ArrayList<PMod> lowerModifiers = new ArrayList<>(EUIUtils.list((PMod) null));
    protected ArrayList<List<PSkill>> originalConditions = new ArrayList<>();
    protected ArrayList<List<PSkill>> originalEffects = new ArrayList<>();
    protected ArrayList<List<PSkill>> originalModifiers = new ArrayList<>();
    protected ArrayList<PCLCustomCardEffectEditor> conditionEditors = new ArrayList<>();
    protected ArrayList<PCLCustomCardEffectEditor> effectEditors = new ArrayList<>();
    protected ArrayList<PCLCustomCardEffectEditor> modifierEditors = new ArrayList<>();
    protected EUIButton addCondition;
    protected EUIButton addEffect;
    protected EUIHitbox hb;
    protected EUILabel header;
    protected EUILabel conditionHeader;
    protected EUILabel effectHeader;
    protected EUILabel modifierHeader;
    protected EUISearchableDropdown<PPrimary> primaryConditions;
    protected EUIToggle ifElseToggle;
    protected EUIToggle orToggle;
    protected int editorIndex;
    protected PCLValueEditor choicesEditor;
    protected PCLValueEditor delayEditor;
    protected PSkill finalEffect;
    protected PSkill.PCLCardValueSource currentEffectSource = PSkill.PCLCardValueSource.MagicNumber;


    public PCLCustomCardEffectPage(PCLCustomCardEditCardScreen screen, PSkill effect, EUIHitbox hb, int index, String title, ActionT1<PSkill> onUpdate)
    {
        this.builder = screen.getBuilder();
        this.editorIndex = index;
        this.onUpdate = onUpdate;
        this.hb = hb;
        this.scrollBar.setPosition(screenW(0.95f), screenH(0.5f));
        this.upperScrollBound = scale(550);

        initializeEffects(effect);

        this.header = new EUILabel(EUIFontHelper.cardtitlefontLarge, hb)
                .setAlignment(0.5f, 0.0f, false)
                .setFontScale(0.8f).setColor(Color.LIGHT_GRAY)
                .setLabel(title);

        float offsetY = OFFSET_EFFECT * 2f;
        primaryConditions = (EUISearchableDropdown<PPrimary>) new EUISearchableDropdown<PPrimary>(new OriginRelativeHitbox(hb, MENU_WIDTH, MENU_HEIGHT, 0, offsetY)
                , PSkill::getSampleText)
                .setOnChange(conditions -> {
                    if (!conditions.isEmpty())
                    {
                        primaryCond = conditions.get(0);
                    }
                    else
                    {
                        primaryCond = null;
                    }
                    constructEffect();
                })
                .setClearButtonOptions(true, true)
                .setCanAutosizeButton(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cardEditor.mainCondition)
                .setItems(EUIUtils.map(PSkill.getEligibleEffects(builder.cardColor, PLimit.class), bc -> primaryCond != null && bc.effectID.equals(primaryCond.effectID) ? primaryCond : bc));
        delayEditor = new PCLValueEditor(new OriginRelativeHitbox(hb, MENU_WIDTH / 4, MENU_HEIGHT, MENU_WIDTH * 1.5f, offsetY)
                , PGR.core.strings.cardEditor.turnDelay, (val) -> {
                    delayMove.setAmount(val);
                    constructEffect();
                })
                .setTooltip(PGR.core.strings.cardEditor.turnDelay, PGR.core.strings.cardEditorTutorial.effectTurnDelay)
                .setLimits(0, PSkill.DEFAULT_MAX);

        offsetY += OFFSET_EFFECT * 2;
        conditionHeader = new EUILabel(EUIFontHelper.cardtitlefontSmall, new OriginRelativeHitbox(hb, MENU_WIDTH / 4, MENU_HEIGHT, 0, offsetY))
                .setAlignment(0.5f, 0.0f, false)
                .setColor(Settings.BLUE_TEXT_COLOR)
                .setLabel(PGR.core.strings.cardEditor.condition)
                .setTooltip(PGR.core.strings.cardEditor.condition, PGR.core.strings.cardEditorTutorial.effectCondition);
        addCondition = new EUIButton(EUIRM.images.plus.texture(), new OriginRelativeHitbox(hb, scale(48), scale(48), MENU_WIDTH, offsetY))
                .setOnClick(() -> addEffectSlot(PCond.CONDITION_PRIORITY))
                .setClickDelay(0.02f);
        ifElseToggle = (EUIToggle) new EUIToggle(new OriginRelativeHitbox(hb, MENU_WIDTH / 4, MENU_HEIGHT, MENU_WIDTH * 2f, offsetY))
                .setFont(EUIFontHelper.carddescriptionfontNormal, 0.9f)
                .setText(PGR.core.strings.cardEditor.ifElseCondition)
                .setOnToggle(val -> {
                    multiCond.setAmount(val ? 1 : 0);
                    constructEffect();
                })
                .setTooltip(PGR.core.strings.cardEditor.ifElseCondition, PGR.core.strings.cardEditorTutorial.effectConditionIfElse);
        orToggle = (EUIToggle) new EUIToggle(new OriginRelativeHitbox(hb, MENU_WIDTH / 4, MENU_HEIGHT, MENU_WIDTH * 3f, offsetY))
                .setFont(EUIFontHelper.carddescriptionfontNormal, 0.9f)
                .setText(PGR.core.strings.cardEditor.orCondition)
                .setOnToggle(val -> {
                    multiCond.edit(f -> f.setOr(val));
                    constructEffect();
                })
                .setTooltip(PGR.core.strings.cardEditor.orCondition, PGR.core.strings.cardEditorTutorial.effectConditionOr);

        offsetY += OFFSET_EFFECT * 2f;
        for (int i = 0; i < lowerConditions.size(); i++)
        {
            conditionEditors.add(new PCLCustomCardEffectEditor(this, new OriginRelativeHitbox(hb, MENU_WIDTH, MENU_HEIGHT, 0, offsetY), PCond.CONDITION_PRIORITY, i));
            offsetY += OFFSET_EFFECT * 2;
        }

        offsetY += OFFSET_EFFECT;
        effectHeader = new EUILabel(EUIFontHelper.cardtitlefontSmall, new OriginRelativeHitbox(hb, MENU_WIDTH / 4, MENU_HEIGHT, 0, offsetY))
                .setAlignment(0.5f, 0.0f, false)
                .setColor(Settings.BLUE_TEXT_COLOR)
                .setLabel(PGR.core.strings.cardEditor.effect)
                .setTooltip(PGR.core.strings.cardEditor.effect, PGR.core.strings.cardEditorTutorial.effectEffect);
        addEffect = new EUIButton(EUIRM.images.plus.texture(), new OriginRelativeHitbox(hb, scale(48), scale(48), MENU_WIDTH, offsetY))
                .setOnClick(() -> addEffectSlot(PSkill.DEFAULT_PRIORITY))
                .setClickDelay(0.02f);
        choicesEditor = new PCLValueEditor(new OriginRelativeHitbox(hb, MENU_WIDTH / 4, MENU_HEIGHT, MENU_WIDTH * 2.5f, offsetY)
                , PGR.core.strings.cardEditor.choices, (val) -> {
                    modifier.setAmount(val);
                    constructEffect();
                })
                .setTooltip(PGR.core.strings.cardEditor.choices, PGR.core.strings.cardEditorTutorial.effectChoices)
                .setLimits(0, PSkill.DEFAULT_MAX);
        choicesEditor.header.hb.setOffset(-0.375f * MENU_WIDTH, MENU_HEIGHT * 0.5f);

        offsetY += OFFSET_EFFECT * 2f;
        for (int i = 0; i < lowerEffects.size(); i++)
        {
            effectEditors.add(new PCLCustomCardEffectEditor(this, new OriginRelativeHitbox(hb, MENU_WIDTH, MENU_HEIGHT, 0, offsetY), PSkill.DEFAULT_PRIORITY, i));
            offsetY += OFFSET_EFFECT * 2;
        }

        offsetY += OFFSET_EFFECT;
        modifierHeader = new EUILabel(EUIFontHelper.cardtitlefontSmall, new OriginRelativeHitbox(hb, MENU_WIDTH / 4, MENU_HEIGHT, 0, offsetY))
                .setAlignment(0.5f, 0.0f, false)
                .setColor(Settings.BLUE_TEXT_COLOR)
                .setTooltip(PGR.core.strings.cardEditor.modifier, PGR.core.strings.cardEditorTutorial.effectModifier)
                .setLabel(PGR.core.strings.cardEditor.modifier);

        offsetY += OFFSET_EFFECT * 2f;
        for (int i = 0; i < lowerModifiers.size(); i++)
        {
            modifierEditors.add(new PCLCustomCardEffectEditor(this, new OriginRelativeHitbox(hb, MENU_WIDTH, MENU_HEIGHT, 0, offsetY), PMod.MODIFIER_PRIORITY, i));
            offsetY += OFFSET_EFFECT * 2;
        }

        for (PCLCustomCardEffectEditor effectEditor : conditionEditors)
        {
            originalConditions.add(effectEditor.effects.getAllItems());
        }
        for (PCLCustomCardEffectEditor effectEditor : effectEditors)
        {
            originalEffects.add(effectEditor.effects.getAllItems());
        }
        for (PCLCustomCardEffectEditor effectEditor : modifierEditors)
        {
            originalModifiers.add(effectEditor.effects.getAllItems());
        }
    }

    protected void repositionItems()
    {
        float offsetY = OFFSET_EFFECT * 2f;
        primaryConditions.hb.setOffsetY(offsetY);
        delayEditor.hb.setOffsetY(offsetY);
        offsetY += OFFSET_EFFECT * 2;
        conditionHeader.hb.setOffsetY(offsetY);
        addCondition.hb.setOffsetY(offsetY);
        ifElseToggle.hb.setOffsetY(offsetY);
        orToggle.hb.setOffsetY(offsetY);

        offsetY += OFFSET_EFFECT * 2f;
        for (PCLCustomCardEffectEditor editor : conditionEditors)
        {
            editor.hb.setOffsetY(offsetY);
            offsetY += OFFSET_EFFECT * 2;
        }

        offsetY += OFFSET_EFFECT;
        effectHeader.hb.setOffsetY(offsetY);
        addEffect.hb.setOffsetY(offsetY);
        choicesEditor.hb.setOffsetY(offsetY);

        offsetY += OFFSET_EFFECT * 2f;
        for (PCLCustomCardEffectEditor editor : effectEditors)
        {
            editor.hb.setOffsetY(offsetY);
            offsetY += OFFSET_EFFECT * 2;
        }

        offsetY += OFFSET_EFFECT;
        modifierHeader.hb.setOffsetY(offsetY);

        offsetY += OFFSET_EFFECT * 2f;
        for (PCLCustomCardEffectEditor editor : modifierEditors)
        {
            editor.hb.setOffsetY(offsetY);
            offsetY += OFFSET_EFFECT * 2;
        }

        refresh();
    }

    protected void constructEffect()
    {
        finalEffect = new PMultiSkill()
                .setEffects(EUIUtils.mapAsNonnull(lowerEffects, e -> e != null ? e.makeCopy() : null))
                .setAmount(multiSkill.amount);
        modifier = lowerModifiers.size() > 0 ? lowerModifiers.get(0) : null;

        if (modifier != null)
        {
            finalEffect = (modifier.makeCopy())
                    .setChild(finalEffect);
        }
        if (delayMove != null && delayMove.amount > 0)
        {
            finalEffect = (delayMove.makeCopy()).setChild(finalEffect);
        }
        if (multiCond != null)
        {
            finalEffect = new PMultiCond().setEffects(EUIUtils.mapAsNonnull(lowerConditions, e -> e != null ? (PCond) e.makeCopy() : null))
                    .edit(f -> f.setOr(multiCond.fields.or))
                    .setChild(finalEffect)
                    .setAmount(multiCond.amount);
        }
        if (primaryCond != null)
        {
            finalEffect = (primaryCond.makeCopy())
                    .setChild(finalEffect);
        }
        if (onUpdate != null)
        {
            onUpdate.invoke(finalEffect);
        }
        refresh();
    }

    protected void deconstructEffect(PSkill effect)
    {
        if (effect == null)
        {
            return;
        }

        if (effect instanceof PLimit)
        {
            primaryCond = (PLimit) effect.makeCopy();
        }
        else if (effect instanceof PMultiCond)
        {
            multiCond = (PMultiCond) effect.makeCopy();
        }
        else if (effect instanceof PDelay)
        {
            delayMove = (PDelay) effect.makeCopy();
        }
        else if (effect instanceof PMod)
        {
            modifier = (PMod) effect.makeCopy();
        }
        else if (effect instanceof PMultiSkill)
        {
            multiSkill = (PMultiSkill) effect.makeCopy();
        }
        deconstructEffect(effect.getChild());
    }

    public String getTitle()
    {
        return header.text;
    }

    public void refresh()
    {
        for (PCLCustomCardEffectEditor ce : conditionEditors)
        {
            ce.refresh();
        }
        for (PCLCustomCardEffectEditor ce : effectEditors)
        {
            ce.refresh();
        }
        for (PCLCustomCardEffectEditor ce : modifierEditors)
        {
            ce.refresh();
        }

        if (primaryCond != null)
        {
            primaryConditions.setSelection(primaryCond, false);
        }
        if (delayMove == null)
        {
            delayMove = new PDelay();
        }
        delayEditor.setValue(delayMove.amount, false);
        choicesEditor.setValue(multiSkill.amount, false);
        ifElseToggle.setToggle(multiCond.amount == 1);
        orToggle.setToggle(multiCond.fields.or);
    }

    @Override
    public TextureCache getTextureCache()
    {
        return PGR.core.images.editorEffect;
    }

    @Override
    public String getIconText() {return String.valueOf(editorIndex + 1);}

    protected void addEffectSlot(int priority)
    {
        List<? extends PSkill> list = getEffectList(priority);
        list.add(null);
        List<PCLCustomCardEffectEditor> editors = getEditorList(priority);
        editors.add(new PCLCustomCardEffectEditor(this, new OriginRelativeHitbox(hb, MENU_WIDTH, MENU_HEIGHT, 0, 0), priority, editors.size()));
        repositionItems();
    }

    protected void removeEffectSlot(int priority, int index)
    {
        List<? extends PSkill> list = getEffectList(priority);
        List<PCLCustomCardEffectEditor> editors = getEditorList(priority);
        if (list.size() > index && editors.size() > index)
        {
            list.remove(index);
            editors.remove(index);

            // Update editor indexes to reflect changes in the effects
            for (int i = 0; i < editors.size(); i++)
            {
                editors.get(i).index = i;
            }

            repositionItems();
        }
    }

    protected List<? extends PSkill> getEffectList(int priority)
    {
        switch (priority)
        {
            case PCond.CONDITION_PRIORITY:
                return lowerConditions;
            case PMod.MODIFIER_PRIORITY:
                return lowerModifiers;
            default:
                return lowerEffects;
        }
    }

    protected List<PCLCustomCardEffectEditor> getEditorList(int priority)
    {
        switch (priority)
        {
            case PCond.CONDITION_PRIORITY:
                return conditionEditors;
            case PMod.MODIFIER_PRIORITY:
                return modifierEditors;
            default:
                return effectEditors;
        }
    }

    protected void initializeEffects(PSkill effect)
    {
        deconstructEffect(effect);

        lowerEffects.clear();
        if (multiSkill != null)
        {
            lowerEffects.addAll(multiSkill.getSubEffects());
        }
        else
        {
            multiSkill = new PMultiSkill();
        }

        if (modifier != null)
        {
            if (lowerModifiers.size() == 0)
            {
                lowerModifiers.add(modifier);
            }
            else
            {
                lowerModifiers.set(0, modifier);
            }
        }

        lowerConditions.clear();
        if (multiCond != null)
        {
            lowerConditions.addAll(multiCond.getSubEffects());
        }
        else
        {
            multiCond = new PMultiCond();
        }
    }

    @Override
    public void updateImpl()
    {
        super.updateImpl();
        this.hb.targetCy = START_Y + (scale(scrollDelta));
        this.header.tryUpdate();
        this.conditionHeader.tryUpdate();
        this.effectHeader.tryUpdate();
        this.modifierHeader.tryUpdate();
        this.addCondition.tryUpdate();
        this.addEffect.tryUpdate();
        this.primaryConditions.tryUpdate();
        this.delayEditor.tryUpdate();
        this.choicesEditor.tryUpdate();
        this.ifElseToggle.tryUpdate();
        this.orToggle.tryUpdate();
        for (PCLCustomCardEffectEditor ce : conditionEditors)
        {
            ce.tryUpdate();
        }
        for (PCLCustomCardEffectEditor ce : effectEditors)
        {
            ce.tryUpdate();
        }
        for (PCLCustomCardEffectEditor ce : modifierEditors)
        {
            ce.tryUpdate();
        }

        for (ActionT0 action : toRemove)
        {
            action.invoke();
        }
        toRemove.clear();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        super.renderImpl(sb);
        this.header.tryRender(sb);
        this.conditionHeader.tryRender(sb);
        this.effectHeader.tryRender(sb);
        this.modifierHeader.tryRender(sb);
        this.addCondition.tryRender(sb);
        this.addEffect.tryRender(sb);
        this.primaryConditions.tryRender(sb);
        this.delayEditor.tryRender(sb);
        this.choicesEditor.tryRender(sb);
        this.ifElseToggle.tryRender(sb);
        this.orToggle.tryRender(sb);
        for (PCLCustomCardEffectEditor ce : conditionEditors)
        {
            ce.tryRender(sb);
        }
        for (PCLCustomCardEffectEditor ce : effectEditors)
        {
            ce.tryRender(sb);
        }
        for (PCLCustomCardEffectEditor ce : modifierEditors)
        {
            ce.tryRender(sb);
        }
    }
}
