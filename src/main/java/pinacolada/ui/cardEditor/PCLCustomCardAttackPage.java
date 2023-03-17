package pinacolada.ui.cardEditor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.OriginRelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.effects.PCLAttackVFX;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.base.traits.PTrait_Damage;
import pinacolada.skills.skills.base.traits.PTrait_DamageMultiplier;
import pinacolada.skills.skills.base.traits.PTrait_HitCount;
import pinacolada.skills.skills.special.primary.PCardPrimary_DealDamage;

import java.util.List;

public class PCLCustomCardAttackPage extends PCLCustomCardEffectPage
{
    protected PCLCustomCardUpgradableEditor damageEditor;
    protected PCLCustomCardUpgradableEditor hitCountEditor;
    protected EUIDropdown<PCLAttackType> attackTypeDropdown;
    protected EUIDropdown<AbstractGameAction.AttackEffect> attackEffectDropdown;
    protected EUIToggle enableToggle;

    public PCLCustomCardAttackPage(PCLCustomCardEditCardScreen screen, PSkill<?> effect, EUIHitbox hb, int index, String title, ActionT1<PSkill<?>> onUpdate)
    {
        super(screen, effect, hb, index, title, onUpdate);
    }

    protected void setupComponents(PCLCustomCardEditCardScreen screen)
    {
        super.setupComponents(screen);
        effectGroup.setListFunc(PCLCustomCardAttackPage::getAvailableMoves);
        primaryConditions.setItems(new PCardPrimary_DealDamage()).setActive(false);
        delayEditor.setActive(false);

        enableToggle = (EUIToggle) new EUIToggle(new OriginRelativeHitbox(hb, MENU_WIDTH / 4, MENU_HEIGHT, 0, OFFSET_EFFECT))
                .setFont(EUIFontHelper.carddescriptionfontNormal, 0.9f)
                .setText(PGR.core.strings.cedit_enable)
                .setOnToggle(this::setMove);
        damageEditor = new PCLCustomCardUpgradableEditor(new OriginRelativeHitbox(hb, MENU_WIDTH / 4, MENU_HEIGHT, MENU_WIDTH, OFFSET_EFFECT * 1.3f)
                , PGR.core.strings.cedit_damage, (val, upVal) -> screen.modifyBuilder(e -> e.setDamage(val, upVal, e.hitCount, e.hitCountUpgrade)))
                .setLimits(0, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.cedit_upgrades, PGR.core.strings.cetut_amount);
        hitCountEditor = new PCLCustomCardUpgradableEditor(new OriginRelativeHitbox(hb, MENU_WIDTH / 4, MENU_HEIGHT, MENU_WIDTH * 1.5f, OFFSET_EFFECT * 1.3f)
                , EUIUtils.format(PGR.core.strings.cedit_hitCount, PGR.core.strings.cedit_damage), (val, upVal) -> screen.modifyBuilder(e -> e.setHitCount(val, upVal)))
                .setLimits(1, PSkill.DEFAULT_MAX);
        hitCountEditor.setTooltip(hitCountEditor.header.text, PGR.core.strings.cetut_hitCount);
        hitCountEditor.tooltip.setChild(damageEditor.tooltip);
        attackTypeDropdown = new EUIDropdown<PCLAttackType>(new OriginRelativeHitbox(hb, MENU_WIDTH, MENU_HEIGHT, MENU_WIDTH * 2.1f, OFFSET_EFFECT * 1.5f)
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setOnChange(targets -> {
                    if (!targets.isEmpty())
                    {
                        screen.modifyBuilder(e -> e.setAttackType(targets.get(0)));
                    }
                })
                .setLabelFunctionForOption(c -> c.getTooltip() != null ? c.getTooltip().title : "", false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_attackType)
                .setCanAutosizeButton(true)
                .setItems(EUIUtils.filter(PCLAttackType.values(), v -> v.getTooltip() != null))
                .setTooltip(PGR.core.strings.cedit_attackType, PGR.core.strings.cetut_attackType);
        attackEffectDropdown = new EUIDropdown<AbstractGameAction.AttackEffect>(new OriginRelativeHitbox(hb, MENU_WIDTH, MENU_HEIGHT, MENU_WIDTH * 3.3f, OFFSET_EFFECT * 1.5f)
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setOnChange(targets -> {
                    if (!targets.isEmpty() && primaryCond instanceof PCardPrimary_DealDamage)
                    {
                        ((PCardPrimary_DealDamage) primaryCond).fields.setAttackEffect(targets.get(0));
                        constructEffect();
                    }
                })
                .setLabelFunctionForOption(Enum::name, false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_attackEffect)
                .setCanAutosizeButton(true)
                .setItems(PCLAttackVFX.keys())
                .setTooltip(PGR.core.strings.cedit_attackEffect, PGR.core.strings.cetut_attackEffect);
    }

    public void refresh()
    {
        if (primaryCond instanceof PCardPrimary_DealDamage)
        {
            enableToggle.setToggle(true);
            attackEffectDropdown.setSelection(((PCardPrimary_DealDamage) primaryCond).fields.attackEffect, false);
        }
        else
        {
            enableToggle.setToggle(false);
        }
        attackTypeDropdown.setSelection(builder.attackType, false);

        damageEditor.setValue(builder.getDamage(0), builder.getDamageUpgrade(0));
        hitCountEditor.setValue(builder.getHitCount(0), builder.getHitCountUpgrade(0));
        conditionGroup.refresh();
        modifierGroup.refresh();
        effectGroup.refresh();

        repositionItems();
    }

    protected void setMove(boolean add)
    {
        if (add)
        {
            primaryCond = primaryConditions.getAllItems().get(0);
        }
        else
        {
            primaryCond = null;
        }
        constructEffect();
    }

    protected static List<PMove<?>> getAvailableMoves()
    {
        return EUIUtils.list(
                new PTrait_Damage(),
                new PTrait_DamageMultiplier(),
                new PTrait_HitCount()
        );
    }

    @Override
    public void updateImpl()
    {
        super.updateImpl();
        damageEditor.tryUpdate();
        hitCountEditor.tryUpdate();
        enableToggle.tryUpdate();
        attackTypeDropdown.tryUpdate();
        attackEffectDropdown.tryUpdate();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        super.renderImpl(sb);
        damageEditor.tryRender(sb);
        hitCountEditor.tryRender(sb);
        enableToggle.tryRender(sb);
        attackTypeDropdown.tryRender(sb);
        attackEffectDropdown.tryRender(sb);
    }


    @Override
    public TextureCache getTextureCache()
    {
        return PCLCoreImages.Menu.editorAttack;
    }
}
