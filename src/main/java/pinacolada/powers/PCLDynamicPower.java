package pinacolada.powers;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIUtils;
import extendedui.utilities.ColoredString;
import org.apache.commons.lang3.StringUtils;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.FabricateItem;
import pinacolada.interfaces.markers.TriggerConnection;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.relics.PCLDynamicRelic;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillContainer;
import pinacolada.skills.skills.PTrigger;
import pinacolada.skills.skills.base.primary.PTrigger_Interactable;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class PCLDynamicPower extends PCLPointerPower implements FabricateItem {

    public PCLDynamicPowerData builder;
    protected ArrayList<PCLDynamicPowerData> forms;
    public int form;

    public PCLDynamicPower(PCLDynamicPowerData data, AbstractCreature owner, AbstractCreature source, int amount) {
        super(data, owner, source, amount);
        this.builder = data;
        setupMoves(builder);
    }

    protected void findForms() {
        PCLCustomPowerSlot cSlot = PCLCustomPowerSlot.get(ID);
        if (cSlot != null) {
            this.forms = cSlot.builders;
        }
    }

    @Override
    public PCLDynamicPowerData getDynamicData() {
        return builder;
    }

    public PCLDynamicPower setForm(int form) {
        PCLDynamicPowerData lastBuilder = null;
        this.form = form;
        if (forms != null && forms.size() > form) {
            lastBuilder = forms.get(form);
        }
        if (lastBuilder != null && lastBuilder != this.builder) {
            this.builder = lastBuilder;
            setupMoves(this.builder);
        }
        return this;
    }

    public void setupMoves(PCLDynamicPowerData data) {
        for (PSkill<?> skill : data.moves) {
            if (skill != null) {
                PSkill<?> effect = skill.makeCopy();
                addUseMove(effect);
                if (effect instanceof PTrigger) {
                    ((PTrigger) effect).controller = this;
                    ((PTrigger) effect).forceResetUses();
                }

                PCLClickableUse use = effect.getClickable(this);
                if (use != null) {
                    triggerCondition = use;
                }
            }
        }
        updateDescription();
    }
}
