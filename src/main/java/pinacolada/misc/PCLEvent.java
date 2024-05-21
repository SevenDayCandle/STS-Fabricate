package pinacolada.misc;

import basemod.abstracts.events.PhasedEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import pinacolada.resources.PGR;

public abstract class PCLEvent extends PhasedEvent {
    public EventStrings strings;

    public PCLEvent(String id) {
        this(id, PGR.getEventStrings(id), PGR.getEventImage(id));
    }

    public PCLEvent(String id, EventStrings strings, String imgUrl) {
        super(id, strings.NAME, imgUrl);
        this.strings = strings;
    }
}
