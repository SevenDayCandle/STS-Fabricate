package pinacolada.ui.cardEditor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.OriginRelativeHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PCond;
import pinacolada.skills.PMod;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrigger;
import pinacolada.skills.skills.base.moves.PMove_StackCustomPower;
import pinacolada.skills.skills.base.primary.PTrigger_Interactable;
import pinacolada.skills.skills.base.primary.PTrigger_Passive;
import pinacolada.skills.skills.base.primary.PTrigger_When;
import pinacolada.ui.PCLValueEditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class PCLCustomCardPowerPage extends PCLCustomCardEffectPage
{
    protected PCLValueEditor usesPerTurn;
    protected ArrayList<EUIButton> quickAddButtons;

    public PCLCustomCardPowerPage(PCLCustomCardEditCardScreen screen, EUIHitbox hb, int index, String title, ActionT1<PSkill<?>> onUpdate)
    {
        super(screen, hb, index, title, onUpdate);
        updateEffectItemNames();
    }

    protected void setupComponents(PCLCustomCardEditCardScreen screen)
    {
        super.setupComponents(screen);
        delayEditor.setActive(false);
        primaryConditions
                .setItems(EUIUtils.map(
                        PGR.config.showIrrelevantProperties.get() ? PTrigger.getEligibleEffects(PTrigger.class) : PTrigger.getEligibleEffects(PTrigger.class, screen.getBuilder().cardColor),
                        bc -> primaryCond != null && bc.effectID.equals(primaryCond.effectID) ? primaryCond : bc))
                .setTooltipFunction(this::getTooltip)
                .setOnChange(conditions -> {
                    if (!conditions.isEmpty())
                    {
                        primaryCond = conditions.get(0);
                    }
                    else
                    {
                        primaryCond = null;
                    }
                    updateEffectItemNames();
                    scheduleConstruct();
                })
                .setShowClearForSingle(false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_trigger)
                .autosize();
        // Ensure that a primary cond is always selected
        if (primaryCond == null)
        {
            primaryConditions.setSelectionIndices(Collections.singletonList(0), true);
        }

        usesPerTurn = new PCLValueEditor(new OriginRelativeHitbox(hb, MENU_WIDTH / 4, MENU_HEIGHT, MENU_WIDTH * 3.2f, OFFSET_EFFECT * 2f)
                , PGR.core.strings.combat_uses, (val) -> {
                    if (primaryCond != null)
                    {
                        primaryCond.setAmount(val);
                        scheduleConstruct();
                    }
                })
                .setLimits(-1, PSkill.DEFAULT_MAX)
                .setValue(-1, false)
                .setHasInfinite(true, true);

        quickAddButtons = new ArrayList<>();
        for (int i = 0; i < screen.effectPages.size(); i++)
        {
            quickAddButtons.add(new EUIButton(EUIRM.images.hexagonalButton.texture(), new EUIHitbox(hb.x + MENU_WIDTH * 6f, hb.y + MENU_HEIGHT * (-i - 0.5f), MENU_WIDTH, MENU_HEIGHT))
                    .setColor(Color.GRAY)
                    .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.GRAY)
                    .setFont(EUIFontHelper.cardtitlefontSmall, 0.8f)
                    .setText(EUIUtils.format(PGR.core.strings.cedit_addTo, EUIUtils.format(PGR.core.strings.cedit_effectX, i + 1)))
                    .setOnClick(i, (index, __) -> {
                        addPowerToEffect(screen, index);
                    }));
        }
    }

    protected Collection<EUITooltip> getTooltip(PSkill<?> item)
    {
        if (item instanceof PTrigger_Interactable)
        {
            return Collections.singletonList(PGR.core.tooltips.interactable);
        }
        if (item instanceof PTrigger_Passive)
        {
            return Collections.singletonList(new EUITooltip(PGR.core.strings.cond_passive(), PGR.core.strings.cetut_passive));
        }
        if (item instanceof PTrigger_When)
        {
            return Collections.singletonList(new EUITooltip(StringUtils.capitalize(PGR.core.strings.cond_whenSingle(PGR.core.strings.subjects_x)), PGR.core.strings.cetut_when));
        }
        return Collections.emptyList();
    }

    // Force refresh the row names
    public void updateEffectItemNames()
    {
        for (PCLCustomCardEffectEditor<PCond<?>> e : conditionGroup.editors)
        {
            e.effects.refreshText();
            e.effects.sortByLabel();
        }
        for (PCLCustomCardEffectEditor<PMod<?>> e : modifierGroup.editors)
        {
            e.effects.refreshText();
            e.effects.sortByLabel();
        }
        for (PCLCustomCardEffectEditor<PMove<?>> e : effectGroup.editors)
        {
            e.effects.refreshText();
            e.effects.sortByLabel();
        }
    }

    public PSkill<?> getSourceEffect()
    {
        return screen.currentPowers.get(editorIndex);
    }

    protected void addPowerToEffect(PCLCustomCardEditCardScreen screen, int index)
    {
        PCLCustomCardEffectPage effectPage = screen.effectPages.get(index);
        PMove_StackCustomPower powerApplyEffect = null;
        PCLCustomCardEffectEditor<PMove<?>> current = effectPage.effectGroup.addEffectSlot();
        if (current != null)
        {
            powerApplyEffect = (PMove_StackCustomPower) EUIUtils.find(current.effects.getAllItems(), e -> e instanceof PMove_StackCustomPower);
            if (powerApplyEffect != null)
            {
                powerApplyEffect.fields.setIndexes(screen.powerPages.indexOf(this));
                current.effects.setSelection(powerApplyEffect, true);
            }
            current.refresh();
            screen.openPageAtIndex(screen.pages.indexOf(effectPage));
        }
    }

    public void refresh()
    {
        // TODO filter effects based on eligibility
        conditionGroup.refresh();
        modifierGroup.refresh();
        effectGroup.refresh();

        if (primaryCond != null)
        {
            primaryConditions.setSelection(e -> e.getClass().equals(primaryCond.getClass()), false);
        }

        if (primaryCond instanceof PTrigger)
        {
            usesPerTurn.setValue(primaryCond.amount, false);
        }

        repositionItems();
    }

    @Override
    public void updateImpl()
    {
        super.updateImpl();
        usesPerTurn.tryUpdate();
        for (EUIButton b : quickAddButtons)
        {
            b.tryUpdate();
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        super.renderImpl(sb);
        usesPerTurn.tryRender(sb);
        for (EUIButton b : quickAddButtons)
        {
            b.tryRender(sb);
        }
    }

    @Override
    public TextureCache getTextureCache()
    {
        return PCLCoreImages.Menu.editorPower;
    }

}
