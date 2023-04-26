package pinacolada.relics;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
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

    public PCLPointerRelic(String id, RelicTier tier, LandingSound sfx) {
        super(id, tier, sfx);
    }

    public PCLPointerRelic(String id, RelicTier tier, LandingSound sfx, AbstractPlayer.PlayerClass pc) {
        super(id, tier, sfx, pc);
    }

    public PCLPointerRelic(String id, Texture texture, RelicTier tier, LandingSound sfx) {
        super(id, texture, tier, sfx);
    }

    public PCLPointerRelic(String id, Texture texture, RelicTier tier, LandingSound sfx, AbstractPlayer.PlayerClass pc) {
        super(id, texture, tier, sfx, pc);
    }

    @Override
    public Skills getSkills() {
        return skills;
    }    public void setup() {
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
    public int xValue() {
        return counter;
    }

    @Override
    public EUITooltip getTooltip() {
        return super.getTooltip();
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
    }    @Override
    public void atPreBattle() {
        super.atPreBattle();
        for (PSkill<?> effect : getEffects()) {
            // TODO create special skills for relics to distinguish at start of battle from perpetual effects
            effect.subscribeChildren();
            PCLClickableUse use = effect.getClickable(this);
            if (use != null) {
                triggerCondition = use;
            }
        }
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



    public float atBlockModify(float block, AbstractCard c) {
        return atBlockModify(CombatManager.playerSystem.generateInfo(c, player, player), block, c);
    }

    public float atBlockModify(PCLUseInfo info, float block, AbstractCard c) {
        refresh(info);
        for (PSkill<?> effect : getEffects()) {
            block = effect.modifyBlock(info, block);
        }
        return block;
    }



    public float atDamageModify(PCLUseInfo info, float damage, AbstractCard c) {
        refresh(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyDamage(info, damage);
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

    public float atHealModify(PCLUseInfo info, float damage, AbstractCard c) {
        refresh(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyHeal(info, damage);
        }
        return damage;
    }

    public void refresh(PCLUseInfo info) {
        for (PSkill<?> effect : getEffects()) {
            effect.refresh(info, true);
        }
    }


}
