package pinacolada.skills.skills;

import pinacolada.resources.PGR;

public enum DelayTiming {
    EndOfTurnFirst,
    EndOfTurnLast,
    StartOfTurnFirst,
    StartOfTurnLast;

    public String getTitle() {
        switch (this) {
            case EndOfTurnFirst:
                return PGR.core.strings.ctype_turnEndFirst;
            case EndOfTurnLast:
                return PGR.core.strings.ctype_turnEndLast;
            case StartOfTurnFirst:
                return PGR.core.strings.ctype_turnStartFirst;
            case StartOfTurnLast:
                return PGR.core.strings.ctype_turnStartLast;
        }
        return null;
    }

    public boolean movesBeforePlayer() {
        switch (this) {
            case StartOfTurnFirst:
            case StartOfTurnLast:
                return true;
        }
        return false;
    }
}
