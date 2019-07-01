package it.polimi.ingsw.model;

/**
 * Contains all the possible ammo-cubes' colors
 * <li>{@link #BLUE}</li>
 * <li>{@link #RED}</li>
 * <li>{@link #YELLOW}</li>
 */
public enum Color {
    /**
     * Blue color
     */
    BLUE {
        @Override
        public String colorName() {
            return "Blue";
        }
    },

    /**
     * Red color
     */
    RED {
        @Override
        public String colorName() {
            return "Red";
        }
    },

    /**
     * Yellow color
     */
    YELLOW {
        @Override
        public String colorName() {
            return "Yellow";
        }
    };

    /**
     * Returns Color's Name
     *
     * @return color's name
     */
    public abstract String colorName();
}
