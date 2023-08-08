package pinacolada.skills.delay;

import pinacolada.resources.PGR;

public enum DelayTiming {
    // Order used for ordering items in delay preview
    StartOfTurnFirst,
    StartOfTurnLast,
    EndOfTurnFirst,
    EndOfTurnLast,
    ;

    public String getDesc() {
        switch (this) {
            case EndOfTurnFirst:
            case EndOfTurnLast:
                return PGR.core.strings.cond_atEndOfTurn();
            case StartOfTurnFirst:
            case StartOfTurnLast:
                return PGR.core.strings.cond_atStartOfTurn();
        }
        return null;
    }

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
