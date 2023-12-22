package pinacolada.powers;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.utilities.ColoredString;
import pinacolada.interfaces.markers.EditorMaker;
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

    @Override
    public void setupMoves(EditorMaker<?,?> data) {
        clearSkills();
        int exDescInd = -1;
        for (PSkill<?> skill : data.getMoves()) {
            exDescInd += 1;
            if (!PSkill.isSkillBlank(skill)) {
                PSkill<?> effect = skill.makeCopy();
                putCustomDesc(effect, exDescInd);
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
        for (PSkill<?> pe : data.getPowers()) {
            exDescInd += 1;
            if (PSkill.isSkillBlank(pe)) {
                continue;
            }
            PSkill<?> pec = pe.makeCopy();
            putCustomDesc(pec, exDescInd);
            addPowerMove(pec);
        }
        updateDescription();
    }
}
