package pinacolada.orbs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.interfaces.markers.FabricateItem;
import pinacolada.powers.PCLClickableUse;
import pinacolada.powers.PCLDynamicPower;
import pinacolada.powers.PCLDynamicPowerData;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrigger;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.ArrayList;

public class PCLDynamicOrb extends PCLPointerOrb implements FabricateItem {

    protected ArrayList<PCLDynamicOrbData> forms;
    public PCLDynamicOrbData builder;

    public PCLDynamicOrb(PCLDynamicOrbData data) {
        super(data);
        this.builder = data;
        setupMoves(builder);
    }

    @Override
    public PCLDynamicOrbData getDynamicData() {
        return builder;
    }

    @Override
    public void render(SpriteBatch sb) {
        PCLRenderHelpers.drawCentered(sb, Color.WHITE, this.img, cX, cY + this.bobEffect.y, IMAGE_SIZE, IMAGE_SIZE, scale * 0.5f, angle);
        super.render(sb);
    }

    public PCLDynamicOrb setForm(int form) {
        PCLDynamicOrbData lastBuilder = null;
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
