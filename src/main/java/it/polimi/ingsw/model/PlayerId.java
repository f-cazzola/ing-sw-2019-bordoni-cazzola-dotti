package it.polimi.ingsw.model;

public enum PlayerId {
    BLUE {
        @Override
        public String playerIdName() {
            return "Banshee";
        }

        @Override
        public String playerId() {
            return "blue";
        }
    },

    GREEN {
        @Override
        public String playerIdName() {
            return "Sprog";
        }

        @Override
        public String playerId() {
            return "green";
        }
    },

    YELLOW {
        @Override
        public String playerIdName() {
            return ":D-struct-or";
        }

        @Override
        public String playerId() {
            return "yellow";
        }
    },

    VIOLET {
        @Override
        public String playerIdName() {
            return "Violet";
        }

        @Override
        public String playerId() {
            return "violet";
        }
    },

    GREY {
        @Override
        public String playerIdName() {
            return "Dozer";
        }

        @Override
        public String playerId() {
            return "grey";
        }
    };

    public abstract String playerIdName();

    public abstract String playerId();

}
