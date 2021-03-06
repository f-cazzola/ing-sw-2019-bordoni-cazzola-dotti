package it.polimi.ingsw.view.commandmessage;

/**
 * Contains all possible type of command message that server can send to client.
 */
public enum CommandType {
    DONE {
        public String getString() {
            return "Avanti";
        }
    }, GRAB_TILE {
        public String getString() {
            return "Raccogli le munizioni";
        }
    }, MOVE {
        public String getString() {
            return "Muovi in ";
        }
    }, PAY {
        public String getString() {
            return "Conferma pagamento";
        }
    }, RESPAWN {
        public String getString() {
            return "Rigenera usando ";
        }
    }, SELECT_AGGREGATE_ACTION {
        public String getString() {
            return "";
        }
    }, SELECT_AMMO_PAYMENT {
        public String getString() {
            return "Paga con la munizione ";
        }
    }, SELECT_BUYING_WEAPON {
        public String getString() {
            return "Acquista l'arma ";
        }
    }, SELECT_DISCARD_WEAPON {
        public String getString() {
            return "Scarta l'arma ";
        }
    }, SELECT_POWER_UP {
        public String getString() {
            return "Usa il potenziamento ";
        }
    }, SELECT_POWER_UP_PAYMENT {
        public String getString() {
            return "Paga con il pontenziamento ";
        }
    }, SELECT_RELOADING_WEAPON {
        public String getString() {
            return "Ricarica l'arma ";
        }
    }, SELECT_SCOPE {
        public String getString() {
            return "Usa ";
        }
    }, SELECT_SHOOT_ACTION {
        public String getString() {
            return "Spara ad un avversario";
        }
    }, SELECT_TARGET_PLAYER {
        public String getString() {
            return "Scegli come obiettivo ";
        }
    }, SELECT_TARGET_SQUARE {
        public String getString() {
            return "Scegli il riquadro ";
        }
    }, SELECT_WEAPON {
        public String getString() {
            return "Scegli l'arma ";
        }
    }, SELECT_WEAPON_MODE {
        public String getString() {
            return "Spara in modalità ";
        }
    }, SHOOT {
        public String getString() {
            return "Conferma Shoot Action: ";
        }
    }, USE_NEWTON {
        public String getString() {
            return "Sposta l'obiettivo in ";
        }
    }, USE_SCOPE {
        public String getString() {
            return "Usa il Mirino";
        }
    }, USE_TAGBACK_GRENADE {
        public String getString() {
            return "Sei stato colpito, rispondi con ";
        }
    }, USE_TELEPORT {
        public String getString() {
            return "Teletrasportati in ";
        }
    }, UNDO {
        public String getString() {
            return "Indietro";
        }
    }, END_TURN {
        public String getString() {
            return "Fine turno";
        }
    };

    /**
     * Gets the string associated to a command type.
     *
     * @return the string associated to a command type
     */
    public abstract String getString();
}
