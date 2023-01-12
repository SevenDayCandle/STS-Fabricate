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
import pinacolada.interfaces.markers.PSkillAttribute;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrigger;
import pinacolada.skills.skills.base.primary.PTrigger_Passive;
import pinacolada.skills.skills.special.moves.PMove_StackCustomPower;
import pinacolada.ui.common.PCLValueEditor;

import java.util.ArrayList;

public class PCLCustomCardPowerPage extends PCLCustomCardEffectPage
{
    protected PCLValueEditor usesPerTurn;
    protected ArrayList<EUIButton> quickAddButtons = new ArrayList<>();


    public PCLCustomCardPowerPage(PCLCustomCardEditCardScreen screen, PSkill effect, EUIHitbox hb, int index, String title, ActionT1<PSkill> onUpdate)
    {
        super(screen, effect, hb, index, title, onUpdate);
        delayEditor.setActive(false);
        primaryConditions
                .setItems(EUIUtils.map(PTrigger.getEligibleEffects(builder.cardColor, PTrigger.class), bc -> primaryCond != null && bc.effectID.equals(primaryCond.effectID) ? primaryCond : bc))
                .autosize();
        usesPerTurn = new PCLValueEditor(new OriginRelativeHitbox(hb, MENU_WIDTH / 4, MENU_HEIGHT, MENU_WIDTH * 3.2f, OFFSET_EFFECT * 2f)
                , PGR.core.strings.combat.uses, (val) -> {
            if (primaryCond != null)
            {
                primaryCond.setAmount(val);
                constructEffect();
            }
        })
                .setLimits(-1, PSkill.DEFAULT_MAX)
                .setValue(-1, false)
                .setHasInfinite(true, true);

        for (int i = 0; i < screen.effectPages.size(); i++)
        {
            int finalI = i;
            quickAddButtons.add(new EUIButton(EUIRM.images.hexagonalButton.texture(), new EUIHitbox(hb.x + MENU_WIDTH * 6f, hb.y + MENU_HEIGHT * (-finalI + 2.5f), MENU_WIDTH, MENU_HEIGHT))
                    .setColor(Color.GRAY)
                    .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.GRAY)
                    .setFont(EUIFontHelper.cardtitlefontSmall, 0.8f)
                    .setText(EUIUtils.format(PGR.core.strings.cardEditor.addTo, EUIUtils.format(PGR.core.strings.cardEditor.effectX, i + 1)))
                    .setOnClick(() -> {
                        PCLCustomCardEffectPage effectPage = screen.effectPages.get(finalI);
                        PMove_StackCustomPower powerApplyEffect = null;
                        PCLCustomCardEffectEditor current = null;
                        for (PCLCustomCardEffectEditor editor : effectPage.effectEditors)
                        {
                            current = editor;
                            powerApplyEffect = (PMove_StackCustomPower) EUIUtils.find(current.effects.getCurrentItems(), e -> e instanceof PMove_StackCustomPower);
                            if (current.effects.getCurrentItems().isEmpty() || powerApplyEffect != null)
                            {
                                break;
                            }
                        }

                        if (current != null)
                        {
                            if (powerApplyEffect == null)
                            {
                                powerApplyEffect = (PMove_StackCustomPower) EUIUtils.find(current.effects.getAllItems(), e -> e instanceof PMove_StackCustomPower);
                            }
                            if (powerApplyEffect != null)
                            {
                                powerApplyEffect.fields.setIndexes(screen.powerPages.indexOf(this));
                                current.effects.setSelection(powerApplyEffect, true);
                            }
                        }
                    }));
        }
    }

    public void refresh()
    {
        for (PCLCustomCardEffectEditor ce : modifierEditors)
        {
            ce.refresh();
        }

        if (primaryCond != null)
        {
            primaryConditions.setSelection(primaryCond, false);
        }

        if (primaryCond instanceof PTrigger)
        {
            usesPerTurn.setValue(primaryCond.amount, false);
        }

        for (int i = 0; i < conditionEditors.size(); i++)
        {
            conditionEditors.get(i).effects.setItems(EUIUtils.filter(originalConditions.get(i), ef ->
                    (primaryCond instanceof PTrigger_Passive) == ef instanceof PSkillAttribute));
            conditionEditors.get(i).refresh();
        }

        for (int i = 0; i < effectEditors.size(); i++)
        {
            effectEditors.get(i).effects.setItems(EUIUtils.filter(originalEffects.get(i), ef ->
                    (primaryCond instanceof PTrigger_Passive) == ef instanceof PSkillAttribute));
            effectEditors.get(i).refresh();
        }
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
        return PGR.core.images.editorPower;
    }

}
