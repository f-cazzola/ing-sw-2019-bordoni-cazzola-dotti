package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.model.CardinalDirection;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Connection;
import it.polimi.ingsw.model.PlayerId;
import it.polimi.ingsw.view.*;

/**
 * This class manages the printing of game on command line interface.
 */
@SuppressWarnings("squid:S106")
public class CliManager {

    private static final int INNERWIDTH = 11;
    private static final int SEPARATOR_LENGTH = 100;
    private static final int MAX_DAMAGE_SHOWABLE = 12;
    private static final String SEPARATOR = "█";
    private static final String VERTICAL_WALL = "║";
    private static final String HORIZONTAL_WALL = "═";
    private static final String CORNER_BOTTOM_RIGHT = "╝";
    private static final String CORNER_BOTTOM_LEFT = "╚";
    private static final String CORNER_TOP_RIGHT = "╗";
    private static final String CORNER_TOP_LEFT = "╔";
    private static final String EMPTY_DAMAGE = "_";
    private static final String SPACE = " ";
    private static final int SQUARE_HEIGHT = 5;
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[91m";
    private static final String ANSI_GREEN = "\u001B[92m";
    private static final String ANSI_YELLOW = "\u001B[93m";
    private static final String ANSI_BLUE = "\u001B[94m";

    /**
     * Shows all parts of game.
     *
     * @param modelView is the instance containing the actual match information
     */
    public void displayAll(ModelView modelView) {
        for (int i = 0; i < SEPARATOR_LENGTH; i++)
            System.out.print(SEPARATOR);
        System.out.println(SEPARATOR);
        for (int i = 0; i < ModelView.HEIGHT; i++)
            for (int k = 0; k < SQUARE_HEIGHT; k++) {
                for (int j = 0; j < ModelView.WIDTH; j++) {
                    displaySquare(modelView.getSquareBoard(i, j), k);
                }
                System.out.print(SPACE);
                displayRightSideInformation(i * SQUARE_HEIGHT + k, modelView);
                System.out.printf("%n");
            }
        for (PlayerView player : modelView.getEnemies().values())
            displayEnemiesInformation(player, player.getId() == modelView.getMatch().getPlayerOnDuty());
    }

    /**
     * Manages the printing of a single square of the board.
     *
     * @param square      is the square to be shown
     * @param printingRow is the cli's row of the single square that is being shown
     */
    private void displaySquare(SquareView square, int printingRow) {
        if (square == null) {
            for (int i = 0; i < INNERWIDTH + 3; i++)
                System.out.print(SPACE);
            return;
        }
        switch (printingRow) {
            case 0:
                displayTopHorizontalConnection(square);
                break;
            case 4:
                displayBottomHorizontalConnection(square);
                break;
            default:
                displayVerticalConnection(square.getConnection(CardinalDirection.WEST), printingRow == 2, square.getRow());
                System.out.print(displaySquareInformation(square, printingRow));
                displayVerticalConnection(square.getConnection(CardinalDirection.EAST), printingRow == 2, square.getRow());
                System.out.print(SPACE);
        }
    }

    /**
     * Manages the printing of the top-horizontal connection of a square.
     *
     * @param square is the square whose connection is being shown
     */
    private void displayTopHorizontalConnection(SquareView square) {
        displayCorner(square.getConnection(CardinalDirection.WEST), square.getConnection(CardinalDirection.NORTH), CORNER_TOP_LEFT);
        displayHorizontalConnection(square.getConnection(CardinalDirection.NORTH), square.getCol());
        displayCorner(square.getConnection(CardinalDirection.EAST), square.getConnection(CardinalDirection.NORTH), CORNER_TOP_RIGHT);
        displayAdditionalSpace(square.getConnection(CardinalDirection.EAST), square.getConnection(CardinalDirection.NORTH));
    }

    /**
     * Manages the printing of the bottom-horizontal connection of a square.
     *
     * @param square is the square whose connection is being shown
     */
    private void displayBottomHorizontalConnection(SquareView square) {
        displayCorner(square.getConnection(CardinalDirection.WEST), square.getConnection(CardinalDirection.SOUTH), CORNER_BOTTOM_LEFT);
        displayHorizontalConnection(square.getConnection(CardinalDirection.SOUTH), square.getCol());
        displayCorner(square.getConnection(CardinalDirection.EAST), square.getConnection(CardinalDirection.SOUTH), CORNER_BOTTOM_RIGHT);
        displayAdditionalSpace(square.getConnection(CardinalDirection.EAST), square.getConnection(CardinalDirection.SOUTH));
    }

    /**
     * Manages the printing of the corner of a square.
     *
     * @param vertical   is the vertical connection consecutive to the corner
     * @param horizontal is the horizontal connection consecutive to the corner
     * @param corner     is the default corner, printing if both vertical and horizontal connection are walls or doors
     */
    private void displayCorner(Connection vertical, Connection horizontal, String corner) {
        if (vertical == Connection.SAME_ROOM) {
            if (horizontal == Connection.SAME_ROOM) System.out.print(SPACE);
            else System.out.print(HORIZONTAL_WALL);
        } else {
            if (horizontal == Connection.SAME_ROOM) System.out.print(VERTICAL_WALL);
            else System.out.print(corner);
        }
    }

    /**
     * Manages the printing of horizontal additional spaces between squares.
     *
     * @param vertical   is the right-side connection of the square
     * @param horizontal is the horizontal connection of the square
     */
    private void displayAdditionalSpace(Connection vertical, Connection horizontal) {
        System.out.print((vertical == Connection.SAME_ROOM && horizontal != Connection.SAME_ROOM) ? HORIZONTAL_WALL : SPACE);
    }

    /**
     * Manages the printing of the horizontal connection of a square.
     *
     * @param side   is the horizontal connection that is being shown
     * @param column is the number of square's column on the board
     */
    private void displayHorizontalConnection(Connection side, int column) {
        for (int i = 0; i < INNERWIDTH; i++)
            switch (side) {
                case WALL:
                    System.out.print(HORIZONTAL_WALL);
                    break;
                case MAP_BORDER:
                    System.out.print((i < INNERWIDTH / 2 || i > INNERWIDTH / 2) ? HORIZONTAL_WALL : ConcreteView.getHorizontalCoordinateName(column));
                    break;
                case SAME_ROOM:
                    System.out.print(SPACE);
                    break;
                case DOOR:
                    System.out.print((i < INNERWIDTH / 2 - 1 || i > INNERWIDTH / 2 + 1) ? HORIZONTAL_WALL : SPACE);
            }
    }

    /**
     * Manages the printing of the vertical connection of a square.
     *
     * @param side     is the vertical connection that is being shown
     * @param isMiddle tells if the method is printing the middle-vertical segment of the connection
     *                 (it is used for the printing of doors and board's coordinates)
     * @param row      is the number of square's row on the board
     */
    private void displayVerticalConnection(Connection side, boolean isMiddle, int row) {
        switch (side) {
            case WALL:
                System.out.print(VERTICAL_WALL);
                break;
            case MAP_BORDER:
                System.out.print(isMiddle ? ConcreteView.getVerticalCoordinateName(row) : VERTICAL_WALL);
                break;
            case SAME_ROOM:
                System.out.print(SPACE);
                break;
            case DOOR:
                System.out.print(isMiddle ? SPACE : VERTICAL_WALL);
        }
    }

    /**
     * Manages the building as a string of the various information about a square.
     *
     * @param square is the square whose information is being shown
     * @param row    is the cli's row that is being printed
     * @return the string containing information about a given square
     */
    private String displaySquareInformation(SquareView square, int row) {
        switch (row) {
            case 1:
                if (square.getColor() != null)
                    return " " + setDisplayColored(square.getColor()) + square.getColor().colorName().substring(0, 3) + "Spawn  " + ANSI_RESET;
                TurretSquareView turret = (TurretSquareView) square;
                return " Ammo: " + displayColoredAmmoTile(turret.getAmmoTile().toString()) + " ";
            case 2:
                StringBuilder out = new StringBuilder();
                for (PlayerId p : square.getHostedPlayers())
                    out.append(" ").append(p.playerIdName(), 0, 1);
                return String.format("%-" + INNERWIDTH + "s", out.toString()).substring(0, INNERWIDTH);
            default:
                return String.format("%-" + INNERWIDTH + "s", " ").substring(0, INNERWIDTH);
        }
    }

    /**
     * Manages the printing of all information at the right-side of board.
     *
     * @param row       is the cli's row that is being printed
     * @param modelView is the model from which information is taken
     */

    private void displayRightSideInformation(int row, ModelView modelView) {
        switch (row) {
            case 1:
                System.out.print(ANSI_BLUE + "BlueSpawn weapons: " + ANSI_RESET);
                modelView.getWeaponsOnSpawn(Color.BLUE).forEach(weapon -> System.out.print(weapon.getName() + "; "));
                break;
            case 2:
                System.out.print(ANSI_RED + "RedSpawn weapons: " + ANSI_RESET);
                modelView.getWeaponsOnSpawn(Color.RED).forEach(weapon -> System.out.print(weapon.getName() + "; "));
                break;
            case 3:
                System.out.print(ANSI_YELLOW + "YellowSpawn weapons: " + ANSI_RESET);
                modelView.getWeaponsOnSpawn(Color.YELLOW).forEach(weapon -> System.out.print(weapon.getName() + "; "));
                break;
            case 5:
                System.out.print("Killshots: ");
                modelView.getMatch().getKillshotTrack().forEach(id -> System.out.print(id.playerIdName().substring(0, 1) + " "));
                System.out.print("(" + modelView.getMatch().getDeathsCounter() + " skulls left)");
                break;
            case 7:
                System.out.print(getPlayerIdAndNickname(modelView.getMe(), modelView.getMe().getId() == modelView.getMatch().getPlayerOnDuty()));
                break;
            case 8:
                System.out.print("Dead " + modelView.getMe().getDeaths() + " times");
                break;
            case 9:
                System.out.print("Weapons: ");
                modelView.getMe().getWeapons().forEach(weapon -> System.out.print(weapon.getName() + (weapon.isLoaded() ? "; " : " (UNLOADED); ")));
                break;
            case 10:
                System.out.print("Powerups: ");
                modelView.getMe().getPowerUps().forEach(powerUp -> System.out.print(setDisplayColored(powerUp.getColor()) + powerUp.getName() + " " + powerUp.getColor().colorName() + ANSI_RESET + "; "));
                break;
            case 11:
                System.out.print("Ammos: ");
                modelView.getMe().getAmmo().forEach((color, value) -> {
                    if (value > 0)
                        System.out.print(setDisplayColored(color) + value + " " + color.colorName() + ANSI_RESET + "; ");
                });
                break;
            case 12:
                System.out.print("Damages: ");
                printDamages(modelView.getMe());
                if (modelView.getMatch().isLastTurn())
                    System.out.print("(final frenzy)");
                else if (modelView.getMe().isFirstAdrenalina())
                    if (modelView.getMe().isSecondAdrenalina())
                        System.out.print("(adrenaline action lv 2 unlocked)");
                    else
                        System.out.print("(adrenaline action lv 1 unlocked)");
                else
                    System.out.print("(adrenaline action locked)");
                break;
            case 13:
                System.out.print("Marks: ");
                modelView.getMe().getMarks().forEach((id, n) -> System.out.print(n + " from " + id.playerIdName() + "; "));
            default:
        }
    }


    /**
     * Manages the printing of all information at the bottom-side of board (about enemies).
     *
     * @param enemy    is the enemy whom information is being shown
     * @param isOnDuty tells if an enemy is on duty during match
     */
    private void displayEnemiesInformation(PlayerView enemy, boolean isOnDuty) {
        System.out.print("\n" + getPlayerIdAndNickname(enemy, isOnDuty) + (enemy.isDisconnected() ? " - DISCONNESSO " : " ") + "has " + enemy.getPowerUps().size() + " powerups.");
        System.out.print(" Ammos: ");
        enemy.getAmmo().forEach((color, value) -> {
            if (value > 0)
                System.out.print(setDisplayColored(color) + value + " " + color.colorName() + ANSI_RESET + "; ");
        });
        System.out.print("\n     Weapons: ");
        enemy.getWeapons().forEach(weapon -> System.out.print(weapon.isLoaded() ? "XXXXXX; " : weapon.getName() + "; "));
        System.out.print("\n     Damages: ");
        printDamages(enemy);
        System.out.print("Marks: ");
        enemy.getMarks().forEach((id, n) -> System.out.print(n + " from " + id.playerIdName() + " "));
        System.out.println("(dead " + enemy.getDeaths() + " times)");
    }

    /**
     * Gets the ANSI-code setter associated to a given colour.
     *
     * @param color is the color need to be set
     * @return the ANSI code
     */
    private String setDisplayColored(Color color) {
        switch (color) {
            case BLUE:
                return ANSI_BLUE;
            case RED:
                return ANSI_RED;
            case YELLOW:
                return ANSI_YELLOW;
            default:
                return ANSI_RESET;
        }
    }

    /**
     * Manages the colouring of an ammotile's acronym
     *
     * @param ammoTile is the string representing an ammotile to be colored
     * @return the string representing an ammotile with ANSI color setters
     */
    private String displayColoredAmmoTile(String ammoTile) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < ammoTile.length(); i++) {
            if (ammoTile.substring(i, i + 1).equals(Color.BLUE.colorName().substring(0, 1)))
                out.append(ANSI_BLUE).append(ammoTile, i, i + 1).append(ANSI_RESET);
            else if (ammoTile.substring(i, i + 1).equals(Color.RED.colorName().substring(0, 1)))
                out.append(ANSI_RED).append(ammoTile, i, i + 1).append(ANSI_RESET);
            else if (ammoTile.substring(i, i + 1).equals(Color.YELLOW.colorName().substring(0, 1)))
                out.append(ANSI_YELLOW).append(ammoTile, i, i + 1).append(ANSI_RESET);
            else out.append(ammoTile, i, i + 1);
        }
        return out.toString();
    }

    /**
     * Gets player's id and nickname as a string and make it green coloured it if that player is on duty.
     *
     * @param player   is the player whom id and nickname are requested
     * @param isOnDuty tells if the player is on duty
     * @return the (colored) string containing id and nickname of a given player
     */
    private String getPlayerIdAndNickname(PlayerView player, boolean isOnDuty) {
        return (isOnDuty ? ANSI_GREEN : "") + player.getId().playerIdName().toUpperCase() + " (" + player.getNickname() + ")" + ANSI_RESET;
    }

    /**
     * Manages the printing of a player's damages.
     *
     * @param player is the player whom damages are being printed
     */
    private void printDamages(PlayerView player) {
        player.getHealth().forEach(id -> System.out.print(id.playerIdName().substring(0, 1) + " "));
        for (int i = 0; i + player.getHealth().size() < MAX_DAMAGE_SHOWABLE; i++)
            System.out.print(EMPTY_DAMAGE + SPACE);
    }
}
