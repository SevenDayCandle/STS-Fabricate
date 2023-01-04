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
import pinacolada.skills.skills.base.triggers.PTrigger_Passive;
import pinacolada.skills.skills.special.moves.PMove_StackCustomPower;
import pinacolada.ui.common.PCLValueEditor;

import java.util.ArrayList;
import java.util.List;

public class PCLCustomCardPowerPage extends PCLCustomCardEffectPage
{

    protected PCLValueEditor usesPerTurn;
    protected ArrayList<EUIButton> quickAddButtons = new ArrayList<>();


    public PCLCustomCardPowerPage(PCLCustomCardEditCardScreen screen, PSkill effect, EUIHitbox hb, int index, String title, ActionT1<PSkill> onUpdate)
    {
        super(screen, effect, hb, index, title, onUpdate);
        delayEditor.setActive(false);
        primaryConditions
                .setItems(EUIUtils.map(PTrigger.getEligibleTriggers(builder.cardColor, PTrigger.TRIGGER_PRIORITY), bc -> currentEffects[0] != null && bc.effectID.equals(currentEffects[0].effectID) ? currentEffects[0] : bc))
                .autosize();
        usesPerTurn = new PCLValueEditor(new OriginRelativeHitbox(hb, MENU_WIDTH / 4, MENU_HEIGHT, MENU_WIDTH * 3.2f, OFFSET_EFFECT * 2f)
                , PGR.core.strings.combat.uses, (val) -> {
            if (currentEffects[0] instanceof PTrigger)
            {
                currentEffects[0].setAmount(val);
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
                        PSkill powerApplyEffect = null;
                        PCLCustomCardEffectEditor current = null;
                        for (PCLCustomCardEffectEditor editor : effectPage.effectEditors)
                        {
                            current = editor;
                            powerApplyEffect = EUIUtils.find(current.effects.getCurrentItems(), e -> e instanceof PMove_StackCustomPower);
                            if (current.effects.getCurrentItems().isEmpty() || powerApplyEffect != null)
                            {
                                break;
                            }
                        }

                        if (current != null)
                        {
                            if (powerApplyEffect == null)
                            {
                                powerApplyEffect = EUIUtils.find(current.effects.getAllItems(), e -> e instanceof PMove_StackCustomPower);
                            }
                            if (powerApplyEffect != null)
                            {
                                List<Integer> currentPowers = current.customPowers.getCurrentItems();
                                if (!currentPowers.contains(screen.powerPages.indexOf(this)))
                                {
                                    currentPowers.add(screen.powerPages.indexOf(this));
                                }
                                current.effects.setSelection(powerApplyEffect, true);
                                current.customPowers.setSelection(screen.powerPages.indexOf(this), true);
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

        if (currentEffects[0] != null)
        {
            primaryConditions.setSelection(currentEffects[0], false);
        }

        if (currentEffects[0] instanceof PTrigger)
        {
            usesPerTurn.setValue(currentEffects[0].amount, false);
        }

        for (int i = 0; i < conditionEditors.size(); i++)
        {
            conditionEditors.get(i).effects.setItems(EUIUtils.filter(originalConditions.get(i), ef ->
                    (currentEffects[0] instanceof PTrigger_Passive) == ef instanceof PSkillAttribute));
            conditionEditors.get(i).refresh();
        }

        for (int i = 0; i < effectEditors.size(); i++)
        {
            effectEditors.get(i).effects.setItems(EUIUtils.filter(originalEffects.get(i), ef ->
                    (currentEffects[0] instanceof PTrigger_Passive) == ef instanceof PSkillAttribute));
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
