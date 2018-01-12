import { SituationDto } from './dto';
import { RulesJson } from 'src/rules';



/**
 * States that a card can be in.
 * 
 * The states 'OWNED' and 'PLANNED' are the major states that can be explicitly set by the user.
 * Only the 'OWNED' state is persisted. All other states are calculated at run time.
 */
export enum State {
    /** The player currently owns this card. */
    OWNED,

    /** The player plans to buy this card. */
    PLANNED,

    /** This card would be a perfectly viable choice to buy, but the player has made no such plans as of yet. */
    ABSENT,

    /** Buying this card is discouraged, because it would make it impossible to reach the total winning points by
     *  civilization cards. The original game variant includes a limit on the number of civilization cards a player
     *  can buy. */
    DISCOURAGED,

    /** Buying this card is impossible because its prerequisites aren't met. */
    PREREQFAILED,

    /** Buying this card should not be possible due to insufficient resources. */
    UNAFFORDABLE
}


export class Situation {
    public dao: SituationDto;
    public states: Map<string, State>;

    constructor(pDao: SituationDto, pVariant: RulesJson) {
        this.dao = pDao;
        this.states = this.initializeStates(pDao, pVariant);
    }

    private initializeStates(pDao: SituationDto, pVariant: RulesJson): Map<string, State> {
        const result: Map<string, State> = new Map();
        for (let cardId of Object.keys(pVariant.cards)) {
            let state: State = State.ABSENT;
            if (pDao.ownedCards.indexOf(cardId) >= 0) {
                state = State.OWNED;
            }
            result.set(cardId, state);
        }
        return result;
    }
}
