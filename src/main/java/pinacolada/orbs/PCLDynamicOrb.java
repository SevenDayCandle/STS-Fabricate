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
    public int form;

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
        if (this.img != null) {
            PCLRenderHelpers.drawCentered(sb, Color.WHITE, this.img, cX, cY, IMAGE_SIZE, IMAGE_SIZE, scale, angle);
        }
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

    public void setupMoves(PCLDynamicOrbData data) {
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
