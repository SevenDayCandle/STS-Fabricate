package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.BlightHelper;
import extendedui.EUIRM;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.PCLEffects;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.fields.PField_Blight;
import pinacolada.skills.fields.PField_Blight;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@VisibleSkill
public class PMove_RemoveBlight extends PMove<PField_Blight> implements OutOfCombatMove {
    public static final PSkillData<PField_Blight> DATA = register(PMove_RemoveBlight.class, PField_Blight.class, 1, Integer.MAX_VALUE)
            .noTarget();

    public PMove_RemoveBlight() {
        super(DATA);
    }

    public PMove_RemoveBlight(Collection<String> blights) {
        super(DATA);
        fields.blightIDs.addAll(blights);
    }

    public PMove_RemoveBlight(String... blights) {
        super(DATA);
        fields.blightIDs.addAll(Arrays.asList(blights));
    }

    protected void doEffect(PCLUseInfo info) {
        if (fields.isFilterEmpty() && source instanceof AbstractBlight) {
            AbstractDungeon.player.blights.remove(source);
        }
        else {
            int limit = fields.blightIDs.isEmpty() ? refreshAmount(info) : AbstractDungeon.player.blights.size();
            ArrayList<AbstractBlight> toRemove = new ArrayList<>();
            for (AbstractBlight r : AbstractDungeon.player.blights) {
                if (fields.getFullBlightFilter().invoke(r)) {
                    toRemove.add(r);
                    limit -= 1;
                }
                if (limit <= 0) {
                    break;
                }
            }
            GameUtilities.removeBlights(toRemove);
        }
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_remove(TEXT.subjects_blight);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String base = fields.blightIDs.isEmpty() ?
                fields.isFilterEmpty() ? TEXT.subjects_this : EUIRM.strings.numNoun(getAmountRawString(requestor), fields.getFullBlightString(requestor))
                : fields.getFullBlightString(requestor);
        return TEXT.act_remove(base);
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }

    @Override
    public PMove_RemoveBlight onAddToCard(AbstractCard card) {
        super.onAddToCard(card);
        if (card instanceof KeywordProvider) {
            List<EUIKeywordTooltip> tips = ((KeywordProvider) card).getTips();
            if (tips != null) {
                for (String r : fields.blightIDs) {
                    AbstractBlight blight = BlightHelper.getBlight(r);
                    if (blight != null) {
                        tips.add(new EUIKeywordTooltip(blight.name, blight.description));
                    }
                }
            }
        }
        return this;
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        order.callback(() -> this.doEffect(info)).addCallback(() -> {
            super.use(info, order);
        });
    }

    @Override
    public void useOutsideOfBattle(PCLUseInfo info) {
        // Use callback to avoid concurrent modification if called from blight
        PCLEffects.Queue.callback(() -> this.doEffect(info))
                .addCallback(() -> {
                    super.useOutsideOfBattle(info);
                });
    }
}
