package pinacolada.powers;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.utilities.ColoredString;
import pinacolada.interfaces.markers.FabricateItem;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrigger;

import java.util.ArrayList;

public class PCLDynamicPower extends PCLPointerPower implements FabricateItem {

    protected ArrayList<PCLDynamicPowerData> forms;
    public PCLDynamicPowerData builder;
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

    @Override
    protected ColoredString getSecondaryAmount(Color c) {
        switch (data.endTurnBehavior) {
            case SingleTurn:
            case SingleTurnNext:
                return new ColoredString(turns, Color.RED, c.a);
        }
        return null;
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
        clearSkills();
        for (PSkill<?> skill : data.moves) {
            if (!PSkill.isSkillBlank(skill)) {
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
