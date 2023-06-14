package pinacolada.ui.combat;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.EUIUtils;
import extendedui.ui.controls.EUITutorialPage;
import pinacolada.powers.PCLClickableUse;
import pinacolada.resources.PGR;

// TODO add basic matching logic
public class PCLEmptyMeter extends PCLPlayerMeter {
    public static final String ID = createFullID(PGR.core, PCLEmptyMeter.class);
    protected PCLClickableUse skips;

    public PCLEmptyMeter() {
        super(ID, null, 12);
        infoIcon.setActive(false);
        draggablePanel.setActive(false);
        draggableIcon.setActive(false);
    }

    @Override
    public String getInfoMainDescrption() {
        return "";
    }

    @Override
    public EUITutorialPage[] getInfoPages() {
        return EUIUtils.array(
                new EUITutorialPage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.tooltips.affinityGeneral.title), PGR.core.strings.tutorial_affinityTutorial),
                new EUITutorialPage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.strings.cedit_tags), PGR.core.strings.tutorial_tagTutorial)
        );
    }

    @Override
    public String getInfoTitle() {
        return "";
    }

    // RENDER NOTHIN
    @Override
    public void renderImpl(SpriteBatch sb) {
    }
}
