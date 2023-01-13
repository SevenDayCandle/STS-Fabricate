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
    protected EffectEditorGroup<PCond> conditionGroup;
    protected EffectEditorGroup<PMod> modifierGroup;
    protected EffectEditorGroup<PMove> effectGroup;
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
        this.conditionGroup = new EffectEditorGroup<>(this, PCond.class, PGR.core.strings.cardEditor.condition);
        this.modifierGroup = new EffectEditorGroup<>(this, PMod.class, PGR.core.strings.cardEditor.modifier);
        this.effectGroup = new EffectEditorGroup<>(this, PMove.class, PGR.core.strings.cardEditor.effect);

        initializeEffects(effect);

        this.header = new EUILabel(EUIFontHelper.cardtitlefontLarge, hb)
                .setAlignment(0.5f, 0.0f, false)
                .setFontScale(0.8f).setColor(Color.LIGHT_GRAY)
                .setLabel(title);

        primaryConditions = (EUISearchableDropdown<PPrimary>) new EUISearchableDropdown<PPrimary>(new OriginRelativeHitbox(hb, MENU_WIDTH, MENU_HEIGHT, 0, 0)
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
        delayEditor = new PCLValueEditor(new OriginRelativeHitbox(hb, MENU_WIDTH / 4, MENU_HEIGHT, MENU_WIDTH * 1.5f, 0)
                , PGR.core.strings.cardEditor.turnDelay, (val) -> {
                    delayMove.setAmount(val);
                    constructEffect();
                })
                .setTooltip(PGR.core.strings.cardEditor.turnDelay, PGR.core.strings.cardEditorTutorial.effectTurnDelay)
                .setLimits(0, PSkill.DEFAULT_MAX);

        conditionHeader = new EUILabel(EUIFontHelper.cardtitlefontSmall, new OriginRelativeHitbox(hb, MENU_WIDTH / 4, MENU_HEIGHT, 0, 0))
                .setAlignment(0.5f, 0.0f, false)
                .setColor(Settings.BLUE_TEXT_COLOR)
                .setLabel(PGR.core.strings.cardEditor.condition)
                .setTooltip(PGR.core.strings.cardEditor.condition, PGR.core.strings.cardEditorTutorial.effectCondition);
        addCondition = new EUIButton(EUIRM.images.plus.texture(), new OriginRelativeHitbox(hb, scale(48), scale(48), MENU_WIDTH, 0))
                .setOnClick(() -> conditionGroup.addEffectSlot())
                .setClickDelay(0.02f);
        ifElseToggle = (EUIToggle) new EUIToggle(new OriginRelativeHitbox(hb, MENU_WIDTH / 4, MENU_HEIGHT, MENU_WIDTH * 2f, 0))
                .setFont(EUIFontHelper.carddescriptionfontNormal, 0.9f)
                .setText(PGR.core.strings.cardEditor.ifElseCondition)
                .setOnToggle(val -> {
                    multiCond.setAmount(val ? 1 : 0);
                    constructEffect();
                })
                .setTooltip(PGR.core.strings.cardEditor.ifElseCondition, PGR.core.strings.cardEditorTutorial.effectConditionIfElse);
        orToggle = (EUIToggle) new EUIToggle(new OriginRelativeHitbox(hb, MENU_WIDTH / 4, MENU_HEIGHT, MENU_WIDTH * 3f, 0))
                .setFont(EUIFontHelper.carddescriptionfontNormal, 0.9f)
                .setText(PGR.core.strings.cardEditor.orCondition)
                .setOnToggle(val -> {
                    multiCond.edit(f -> f.setOr(val));
                    constructEffect();
                })
                .setTooltip(PGR.core.strings.cardEditor.orCondition, PGR.core.strings.cardEditorTutorial.effectConditionOr);

        effectHeader = new EUILabel(EUIFontHelper.cardtitlefontSmall, new OriginRelativeHitbox(hb, MENU_WIDTH / 4, MENU_HEIGHT, 0, 0))
                .setAlignment(0.5f, 0.0f, false)
                .setColor(Settings.BLUE_TEXT_COLOR)
                .setLabel(PGR.core.strings.cardEditor.effect)
                .setTooltip(PGR.core.strings.cardEditor.effect, PGR.core.strings.cardEditorTutorial.effectEffect);
        addEffect = new EUIButton(EUIRM.images.plus.texture(), new OriginRelativeHitbox(hb, scale(48), scale(48), MENU_WIDTH, 0))
                .setOnClick(() -> effectGroup.addEffectSlot())
                .setClickDelay(0.02f);
        choicesEditor = new PCLValueEditor(new OriginRelativeHitbox(hb, MENU_WIDTH / 4, MENU_HEIGHT, MENU_WIDTH * 2.5f, 0)
                , PGR.core.strings.cardEditor.choices, (val) -> {
                    modifier.setAmount(val);
                    constructEffect();
                })
                .setTooltip(PGR.core.strings.cardEditor.choices, PGR.core.strings.cardEditorTutorial.effectChoices)
                .setLimits(0, PSkill.DEFAULT_MAX);
        choicesEditor.header.hb.setOffset(-0.375f * MENU_WIDTH, MENU_HEIGHT * 0.5f);

        modifierHeader = new EUILabel(EUIFontHelper.cardtitlefontSmall, new OriginRelativeHitbox(hb, MENU_WIDTH / 4, MENU_HEIGHT, 0, 0))
                .setAlignment(0.5f, 0.0f, false)
                .setColor(Settings.BLUE_TEXT_COLOR)
                .setTooltip(PGR.core.strings.cardEditor.modifier, PGR.core.strings.cardEditorTutorial.effectModifier)
                .setLabel(PGR.core.strings.cardEditor.modifier);

        conditionGroup.syncWithLower();
        effectGroup.syncWithLower();
        modifierGroup.syncWithLower();
        repositionItems();
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
        offsetY = conditionGroup.reposition(offsetY);

        offsetY += OFFSET_EFFECT;
        effectHeader.hb.setOffsetY(offsetY);
        addEffect.hb.setOffsetY(offsetY);
        choicesEditor.hb.setOffsetY(offsetY);

        offsetY += OFFSET_EFFECT * 2f;
        offsetY = effectGroup.reposition(offsetY);

        offsetY += OFFSET_EFFECT;
        modifierHeader.hb.setOffsetY(offsetY);

        offsetY += OFFSET_EFFECT * 2f;
        offsetY = modifierGroup.reposition(offsetY);
    }

    protected void constructEffect()
    {
        finalEffect = new PMultiSkill()
                .setEffects(EUIUtils.mapAsNonnull(effectGroup.lowerEffects, e -> e != null ? e.makeCopy() : null))
                .setAmount(multiSkill.amount);
        modifier = modifierGroup.lowerEffects.size() > 0 ? modifierGroup.lowerEffects.get(0) : null;

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
            finalEffect = new PMultiCond().setEffects(EUIUtils.mapAsNonnull(conditionGroup.lowerEffects, e -> e != null ? (PCond) e.makeCopy() : null))
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
        conditionGroup.refresh();
        effectGroup.refresh();
        modifierGroup.refresh();

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

    protected void initializeEffects(PSkill effect)
    {
        deconstructEffect(effect);

        effectGroup.lowerEffects.clear();
        if (multiSkill != null)
        {
            for (PSkill skill : multiSkill.getSubEffects())
            {
                PMove move = EUIUtils.safeCast(skill, PMove.class);
                if (move != null)
                {
                    effectGroup.lowerEffects.add(move);
                }
            }
        }
        else
        {
            multiSkill = new PMultiSkill();
        }

        if (modifier != null)
        {
            if (modifierGroup.lowerEffects.size() == 0)
            {
                modifierGroup.lowerEffects.add(modifier);
            }
            else
            {
                modifierGroup.lowerEffects.set(0, modifier);
            }
        }

        conditionGroup.lowerEffects.clear();
        if (multiCond != null)
        {
            conditionGroup.lowerEffects.addAll(multiCond.getSubEffects());
        }
        else
        {
            multiCond = new PMultiCond();
        }
    }

    // Schedule the editor to update its effect at the end of updateImpl. Use this instead of calling constructEffect directly to avoid concurrent modification errors when the visible UI changes
    protected void scheduleConstruct()
    {
        toRemove.add(this::constructEffect);
    }

    protected void scheduleUpdate(ActionT0 update)
    {
        toRemove.add(update);
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
        conditionGroup.update();
        effectGroup.update();
        modifierGroup.update();

        // Actions that involve changing the number of editors/components present must be executed after all other update actions to avoid concurrent modification
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
        conditionGroup.render(sb);
        effectGroup.render(sb);
        modifierGroup.render(sb);
    }

    public static class EffectEditorGroup<T extends PSkill>
    {
        protected ArrayList<T> lowerEffects = new ArrayList<>();
        protected ArrayList<List<T>> originalEffects = new ArrayList<>();
        protected ArrayList<PCLCustomCardEffectEditor<T>> editors = new ArrayList<>();
        protected final Class<T> className;
        protected final String title;
        protected final PCLCustomCardEffectPage editor;

        public EffectEditorGroup(PCLCustomCardEffectPage editor, Class<T> className, String title)
        {
            this.editor = editor;
            this.className = className;
            this.title = title;
        }

        public void syncWithLower()
        {
            for (int i = 0; i < lowerEffects.size(); i++)
            {
                PCLCustomCardEffectEditor<T> effectEditor = new PCLCustomCardEffectEditor<T>(this, new OriginRelativeHitbox(editor.hb, MENU_WIDTH, MENU_HEIGHT, 0, 0), i);
                editors.add(effectEditor);
                originalEffects.add(effectEditor.effects.getAllItems());
            }
        }

        public void addEffectSlot()
        {
            lowerEffects.add(null);
            editors.add(new PCLCustomCardEffectEditor<T>(this, new OriginRelativeHitbox(editor.hb, MENU_WIDTH, MENU_HEIGHT, 0, 0), editors.size()));
            editor.repositionItems();
            editor.refresh();
        }

        public void removeEffectSlot(int index)
        {
            if (lowerEffects.size() > index && editors.size() > index)
            {
                lowerEffects.remove(index);
                editors.remove(index);

                // Update editor indexes to reflect changes in the effects
                for (int i = 0; i < editors.size(); i++)
                {
                    editors.get(i).index = i;
                }

                editor.repositionItems();
                editor.refresh();
            }
        }

        public float reposition(float offsetY)
        {
            for (PCLCustomCardEffectEditor<T> editor : editors)
            {
                editor.hb.setOffsetY(offsetY);
                offsetY += OFFSET_EFFECT * 2;
            }
            return offsetY;
        }

        public List<T> getEffects()
        {
            return PSkill.getEligibleEffects(editor.builder.cardColor, className);
        }

        public void refresh()
        {
            for (PCLCustomCardEffectEditor<T> ce : editors)
            {
                ce.refresh();
            }
        }

        public void update()
        {
            for (PCLCustomCardEffectEditor<T> ce : editors)
            {
                ce.tryUpdate();
            }
        }

        public void render(SpriteBatch sb)
        {
            for (PCLCustomCardEffectEditor<T> ce : editors)
            {
                ce.tryRender(sb);
            }
        }
    }
}
