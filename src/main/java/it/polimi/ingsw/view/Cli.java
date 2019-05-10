package it.polimi.ingsw.view;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.utils.Observer;

public class Cli implements Observer {

    private final static int INNERWIDTH = 11;
    private int height;
    private int width;
    private Match match;
    private PlayerId client;

    public Cli(int height, int width, Match match, PlayerId client) {
        this.height = height;
        this.width = width;
        this.match = match;
        this.client = client;
    }

    private void displayBoard() {
        int squareHeight = 5;
        for (int i = 0; i < height; i++)
            for (int k = 0; k < squareHeight; k++) {
                for (int j = 0; j < width; j++) {
                    displaySquare(match.getBoard().getSquare(i, j), k);
                    System.out.printf(" ");
                }
                System.out.printf(" ");
                displayRightSideInformation(i * squareHeight + k);
                System.out.printf("%n");
            }
    }

    private void displaySquare(Square square, int squareRow) {
        boolean isMiddle = false;
        if (square == null) {
            for (int i = 0; i < INNERWIDTH + 2; i++)
                System.out.printf(" ");
            return;
        }
        switch (squareRow) {
            case 0:
                displayHorizontalConnection(square.getConnection(CardinalDirection.NORTH));
                break;
            case 2:
                isMiddle = true;
            case 1:
            case 3:
                displayVerticalConnection(square.getConnection(CardinalDirection.WEST), isMiddle);
                System.out.printf(String.format("%-" + INNERWIDTH + "s", displaySquareInformation(square, squareRow)).substring(0, INNERWIDTH));
                displayVerticalConnection(square.getConnection(CardinalDirection.EAST), isMiddle);
                break;
            default: //which is case 4
                displayHorizontalConnection(square.getConnection(CardinalDirection.SOUTH));
                break;
        }
    }

    private void displayHorizontalConnection(Connection side) {
        System.out.printf("+");
        for (int i = 0; i < INNERWIDTH; i++)
            switch (side) {
                case WALL:
                    System.out.printf("-");
                    break;
                case MAP_BORDER:
                    System.out.printf("=");
                    break;
                case SAME_ROOM:
                    System.out.printf(" ");
                    break;
                case DOOR:
                    if (i < INNERWIDTH / 2 - 1 || i > INNERWIDTH / 2 + 1)
                        System.out.printf("-");
                    else
                        System.out.printf(" ");
            }
        System.out.printf("+");
    }

    private void displayVerticalConnection(Connection side, boolean door) {
        switch (side) {
            case WALL:
                System.out.printf("|");
                break;
            case MAP_BORDER:
                System.out.printf("H");
                break;
            case SAME_ROOM:
                System.out.printf(" ");
                break;
            case DOOR:
                if (door)
                    System.out.printf(" ");
                else
                    System.out.printf("|");
        }
    }

    private String displaySquareInformation(Square square, int row) {
        switch (row) {
            case 1:
                if (square.getColor() != null)
                    return " " + square.getColor().colorName() + "Spawn";
                TurretSquare turret = (TurretSquare) square;
                return " Ammo: " + turret.getAmmoTile().toString();
            case 2:
                String out = "";
                for (Player p : square.getHostedPlayers())
                    out = out + " " + p.getId().playerIdName().substring(0, 1);
                return out;
            default:
        }
        return " ";
    }

    //TODO: implement SpawnSquare.getWeapons

    private void displayRightSideInformation(int row) {
        switch (row) {
            case 1:
                System.out.printf("BlueSpawn weapons: ");
                match.getBoard().getSpawn(Color.BLUE).getWeapons().forEach(weapon -> System.out.printf("%s, ", weapon.toString()));
                break;
            case 2:
                System.out.printf("RedSpawn weapons: ");
                match.getBoard().getSpawn(Color.RED).getWeapons().forEach(weapon -> System.out.printf("%s, ", weapon.toString()));
                break;
            case 3:
                System.out.printf("YellowSpawn weapons: ");
                match.getBoard().getSpawn(Color.YELLOW).getWeapons().forEach(weapon -> System.out.printf("%s, ", weapon.toString()));
                break;
            case 5:
                System.out.printf("Killshots: ");
                match.getKillshotTrack().forEach(id -> System.out.printf("%s ", id.playerIdName().substring(0, 1)));
                System.out.printf("(%d skulls left)", match.getDeathsCounter());
                break;
            case 7:
                System.out.printf("%s (%s)", client.playerIdName().toUpperCase(), match.getPlayer(client).toString());
                break;
            case 8:
                System.out.printf("Dead %d times", match.getPlayer(client).getDeaths());
                break;
            case 9:
                System.out.printf("Weapons: ");
                match.getPlayer(client).getWeapons().forEach(weapon -> System.out.printf(weapon.toString() + (weapon.isLoaded() ? ", " : "(UNLOADED), ")));
                break;
            case 10:
                System.out.printf("Powerups: ");
                match.getPlayer(client).getPowerUps().forEach(powerUp -> System.out.printf("%s ", powerUp.toString()));
                break;
            case 11:
                System.out.printf("Ammos: ");
                match.getPlayer(client).getAmmo().forEach((color, value) -> System.out.printf("%d %s, ", value, color.colorName()));
                break;
            case 12:
                System.out.printf("Damages: ");
                match.getPlayer(client).getHealth().forEach(id -> System.out.print(id.playerIdName().substring(0, 1)));
                if (match.getPlayer(client).getHealth().size() > 2)
                    System.out.printf("(azioni adrenaliniche lv %d sbloccate)", (match.getPlayer(client).getHealth().size() > 5 ? 2 : 1));
                else
                    System.out.printf(" (azioni adrenaliniche bloccate)");
                break;
            case 13:
                System.out.printf("Marks: ");
                match.getPlayer(client).getMarks().forEach((id, n) -> System.out.printf("%d from %s, ", n, id.playerIdName()));
            default:
        }
    }

    private void displayEnemiesInformation(Player enemy) {
        System.out.printf("%n%s (%s) has %d powerups. Ammos:", enemy.getId().playerIdName().toUpperCase(), enemy.toString(), enemy.getPowerUps().size());
        enemy.getAmmo().forEach((color, value) -> System.out.printf(" %d %s,", value, color.colorName()));
        System.out.printf("%n     Weapons: ");
        enemy.getWeapons().forEach(weapon -> System.out.printf("%s, ", (weapon.isLoaded() ? "XXXXXX" : weapon.toString())));
        System.out.printf("%n     Damages: ");
        enemy.getHealth().forEach(id -> System.out.printf("%s ", id.playerIdName().substring(0, 1)));
        System.out.printf("Marks: ");
        enemy.getMarks().forEach((id, n) -> System.out.printf("%d from %s, ", n, id.playerIdName()));
        System.out.printf("(dead %s times)", enemy.getDeaths());
    }

    @Override
    public void update(String message) {
        System.out.println(message);
        displayBoard();
        for (Player player : match.getCurrentPlayers())
            if (player != match.getPlayer(client))
                displayEnemiesInformation(player);
    }
}
