package pinacolada.relics;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIInputManager;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.providers.ClickableProvider;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.powers.PCLClickableUse;
import pinacolada.skills.PSkill;
import pinacolada.skills.Skills;
import pinacolada.utilities.GameUtilities;

public class PCLPointerRelic extends PCLRelic implements PointerProvider, ClickableProvider {
    public Skills skills;
    public PCLClickableUse triggerCondition;

    public PCLPointerRelic(PCLRelicData data) {
        super(data);
    }

    public PCLPointerRelic(PCLRelicData data, Texture texture, RelicTier tier, LandingSound sfx) {
        super(data, texture, tier, sfx);
    }

    public float atBlockModify(PCLUseInfo info, float block, AbstractCard c) {
        refresh(info);
        for (PSkill<?> effect : getEffects()) {
            block = effect.modifyBlock(info, block);
        }
        return block;
    }

    public float atBlockModify(float block, AbstractCard c) {
        return atBlockModify(CombatManager.playerSystem.generateInfo(c, player, player), block, c);
    }

    public float atDamageModify(PCLUseInfo info, float damage, AbstractCard c) {
        refresh(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyDamage(info, damage);
        }
        return damage;
    }

    public float atHealModify(PCLUseInfo info, float damage, AbstractCard c) {
        refresh(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyHeal(info, damage);
        }
        return damage;
    }

    public float atHitCountModify(PCLUseInfo info, float damage, AbstractCard c) {
        refresh(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyHitCount(info, damage);
        }
        return damage;
    }

    public float atMagicNumberModify(PCLUseInfo info, float damage, AbstractCard c) {
        refresh(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyMagicNumber(info, damage);
        }
        return damage;
    }

    public float atRightCountModify(PCLUseInfo info, float damage, AbstractCard c) {
        refresh(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyRightCount(info, damage);
        }
        return damage;
    }

    // Gets called before skills are initialized
    @Override
    public String getUpdatedDescription() {
        if (skills == null) {
            skills = new Skills();
            setup();
        }
        try {
            return EUIUtils.joinStrings(" ", EUIUtils.map(getEffects(), PSkill::getPowerText));
        }
        catch (Exception e) {
            return "";
        }
    }

    @Override
    public void atPreBattle() {
        super.atPreBattle();
        for (PSkill<?> effect : getEffects()) {
            effect.subscribeChildren();
            PCLClickableUse use = effect.getClickable(this);
            if (use != null) {
                triggerCondition = use;
            }
        }
    }

    @Override
    public void atBattleStartPreDraw() {
        super.atBattleStartPreDraw();
        for (PSkill<?> effect : getEffects()) {
            effect.triggerOnStartOfBattleForRelic();
        }
    }

    @Override
    public String getID() {
        return relicId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Skills getSkills() {
        return skills;
    }

    @Override
    public int xValue() {
        return counter;
    }

    @Override
    public EUITooltip getTooltip() {
        return super.getTooltip();
    }

    public void refresh(PCLUseInfo info) {
        for (PSkill<?> effect : getEffects()) {
            effect.refresh(info, true);
        }
    }

    public void setup() {
    }

    @Override
    public void update() {
        super.update();

        if (GameUtilities.inBattle() && hb.hovered && EUIInputManager.rightClick.isJustPressed() && triggerCondition != null && triggerCondition.interactable()) {
            triggerCondition.targetToUse(1);
        }
    }

    public float atDamageModify(float block, AbstractCard c) {
        return atDamageModify(CombatManager.playerSystem.generateInfo(c, player, player), block, c);
    }


}
