package pinacolada.ui.cardEditor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.OriginRelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrigger;
import pinacolada.skills.skills.base.moves.PMove_StackCustomPower;
import pinacolada.ui.PCLValueEditor;

import java.util.ArrayList;

public class PCLCustomCardPowerPage extends PCLCustomCardEffectPage
{
    protected PCLValueEditor usesPerTurn;
    protected ArrayList<EUIButton> quickAddButtons;

    public PCLCustomCardPowerPage(PCLCustomCardEditCardScreen screen, PSkill<?> effect, EUIHitbox hb, int index, String title, ActionT1<PSkill<?>> onUpdate)
    {
        super(screen, effect, hb, index, title, onUpdate);
    }

    protected void setupComponents(PCLCustomCardEditCardScreen screen)
    {
        super.setupComponents(screen);
        delayEditor.setActive(false);
        primaryConditions
                .setItems(EUIUtils.map(PTrigger.getEligibleEffects(builder.cardColor, PTrigger.class), bc -> primaryCond != null && bc.effectID.equals(primaryCond.effectID) ? primaryCond : bc))
                .setShowClearForSingle(false)
                .setSelectionIndices(EUIUtils.list(0), false)
                .autosize();
        primaryCond = primaryConditions.getAllItems().get(0);

        usesPerTurn = new PCLValueEditor(new OriginRelativeHitbox(hb, MENU_WIDTH / 4, MENU_HEIGHT, MENU_WIDTH * 3.2f, OFFSET_EFFECT * 2f)
                , PGR.core.strings.combat_uses, (val) -> {
                    if (primaryCond != null)
                    {
                        primaryCond.setAmount(val);
                        constructEffect();
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
        if (primaryCond != null)
        {
            primaryConditions.setSelection(primaryCond, false);
        }

        if (primaryCond instanceof PTrigger)
        {
            usesPerTurn.setValue(primaryCond.amount, false);
        }

        // TODO filter effects based on eligibility
        conditionGroup.refresh();
        modifierGroup.refresh();
        effectGroup.refresh();

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
        return PCLCoreImages.editorPower;
    }

}
