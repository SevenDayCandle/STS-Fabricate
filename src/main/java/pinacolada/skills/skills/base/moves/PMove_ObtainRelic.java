package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.annotations.VisibleRelic;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.fields.PField_Relic;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@VisibleRelic
public class PMove_ObtainRelic extends PMove<PField_Relic> {
    public static final PSkillData<PField_Relic> DATA = register(PMove_ObtainRelic.class, PField_Relic.class)
            .selfTarget();

    public PMove_ObtainRelic() {
        super(DATA);
    }

    public PMove_ObtainRelic(Collection<String> relics) {
        super(DATA);
        fields.relicIDs.addAll(relics);
    }

    public PMove_ObtainRelic(String... relics) {
        super(DATA);
        fields.relicIDs.addAll(Arrays.asList(relics));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.act_obtain(TEXT.subjects_x);
    }

    @Override
    public String getSubText() {
        return TEXT.act_obtain(fields.random ? fields.getRelicIDOrString() : fields.getRelicIDAndString());
    }

    @Override
    public PMove_ObtainRelic onAddToCard(AbstractCard card) {
        super.onAddToCard(card);
        if (card instanceof KeywordProvider) {
            List<EUIKeywordTooltip> tips = ((KeywordProvider) card).getTips();
            if (tips != null) {
                for (String r : fields.relicIDs) {
                    AbstractRelic relic = RelicLibrary.getRelic(r);
                    if (relic != null) {
                        tips.add(new EUIKeywordTooltip(relic.name, relic.description));
                    }
                }
            }
        }
        return this;
    }

    @Override
    public void use(PCLUseInfo info) {
        for (String r : fields.relicIDs) {
            AbstractRelic relic = RelicLibrary.getRelic(r);
            if (relic != null) {
                getActions().obtainRelic(relic.makeCopy());
            }
        }

        super.use(info);
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }
}
