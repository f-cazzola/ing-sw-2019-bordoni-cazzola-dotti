package it.polimi.ingsw.model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents the game map
 */
public class GameBoard {
    /**
     * Number of rows
     */
    static final int ROWS = 3;
    /**
     * Number of columns
     */
    static final int COLUMNS = 4;
    private int gameBoardId;
    private Square[][] board;
    private Map<Color, SpawnSquare> spawns = new LinkedHashMap<>();
    private List<TurretSquare> turrets = new ArrayList<>();
    private List<Square> squareList = new ArrayList<>();

    /**
     * Initialize {@link #spawns} {@link #turrets} {@link #squareList} after json initialization
     */
    void initialize() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                Square square = board[i][j];
                if (square != null) {
                    if (square.getColor() != null)
                        spawns.put(square.getColor(), (SpawnSquare) square);
                    else
                        turrets.add((TurretSquare) square);
                    squareList.add(square);
                }
            }
        }
    }

    /**
     * Gets a square given its coordinates
     *
     * @param row is the row's value, first is 0
     * @param col is the column's value, first is 0
     * @return the square placed in specified coordinates
     * @author Bordoni
     */
    Square getSquare(int row, int col) {
        return board[row][col];
    }

    /**
     * Gets a spawn square by its color
     *
     * @param color is the color of the spawn square requested
     * @return the uniqe spawn square of that color
     * @author Bordoni
     */
    public SpawnSquare getSpawn(Color color) {
        return spawns.get(color);
    }

    /**
     * Gets all turret squares
     *
     * @return list of turret squares
     */
    public List<TurretSquare> getTurrets() {
        return turrets;
    }

    /**
     * Gets a square adjacent to {@code current} in the {@code dir} direction
     *
     * @param current is the starting position
     * @param dir     is the direction detected
     * @return the square near to current in dir direction
     * @author Bordoni
     */
    private Square getAdjacentSquare(Square current, CardinalDirection dir) {
        switch (dir) {
            case NORTH:
                return this.getSquare(current.getRow() - 1, current.getCol());
            case EAST:
                return this.getSquare(current.getRow(), current.getCol() + 1);
            case SOUTH:
                return this.getSquare(current.getRow() + 1, current.getCol());
            case WEST:
                return this.getSquare(current.getRow(), current.getCol() - 1);
        }
        return current;
    }

    /**
     * @param list     is the list passed by caller, needed for recursion
     * @param position the square which room is detected
     * @author Bordoni
     */
    private void getSameRoomSquares(List<Square> list, Square position) {
        Square adjacent;
        for (CardinalDirection dir : CardinalDirection.values())
            if (position.getConnection(dir) == Connection.SAME_ROOM) {
                adjacent = this.getAdjacentSquare(position, dir);
                if (!list.contains(adjacent)) {
                    list.add(adjacent);
                    this.getSameRoomSquares(list, adjacent);
                }
            }
    }

    /**
     * Gets the squares which are in the same room of {@code square}
     *
     * @param square square in the room wanted
     * @return List of squares
     */
    List<Square> getRoomSquares(Square square) {
        List<Square> list = new ArrayList<>();
        getSameRoomSquares(list, square);
        return list;
    }

    /**
     * Gets visible squares
     *
     * @param position       starting position
     * @param maxRange       maximum distance of returned squares
     * @param minRange       minimum distance of returned squares
     * @param onlyWithPlayer excluded squares where there are no players
     * @return give the list of visible squares according to input parameters
     * @author supernivem
     */
    @SuppressWarnings("squid:S3776")
    List<Square> getVisibleSquares(Square position, int maxRange, int minRange, boolean onlyWithPlayer) {
        Square adjacent;
        List<Square> list = new ArrayList<>();
        list.add(position);
        if (maxRange > 0) {
            CardinalDirection[] values = CardinalDirection.values();
            for (CardinalDirection dir : values) {
                if (position.getConnection(dir).isAccessible(false)) {
                    adjacent = this.getAdjacentSquare(position, dir);
                    if (!list.contains(adjacent)) {
                        list.add(adjacent);
                        if (maxRange > 1)
                            this.getSameRoomSquares(list, adjacent);
                    }
                }
            }
        }
        if (minRange > 0) {
            list.remove(position);
            if (minRange > 1)
                Arrays.stream(CardinalDirection.values())
                        .filter(dir -> position.getConnection(dir).isAccessible(false))
                        .map(dir -> getAdjacentSquare(position, dir))
                        .forEach(list::remove);
        }
        if (!onlyWithPlayer)
            return list;
        return list.stream().filter(s -> !s.getHostedPlayers().isEmpty()).collect(Collectors.toList());
    }

    /**
     * Gets squares only in the four {@link CardinalDirection}
     *
     * @param position    is the position from where get straight direction squares
     * @param maxRange    maximum distance of gotten squares
     * @param minRange    minimum distance of gotten squares
     * @param ignoreWalls specify if ignore or consider walls
     * @return List of squares in the 4 basic cardinal directions (diagonal squares are not returned)
     * @author supernivem
     */
    public List<Square> getCardinalDirectionSquares(Square position, int maxRange, int minRange, boolean ignoreWalls) {
        Square adjacent;
        List<Square> list = new ArrayList<>();
        if (minRange == 0)
            list.add(position);
        if (maxRange > 0) {
            for (CardinalDirection dir : CardinalDirection.values())
                if (position.getConnection(dir).isAccessible(ignoreWalls)) {
                    adjacent = this.getAdjacentSquare(position, dir);
                    list.add(adjacent);
                    if (maxRange > 1)
                        getStraightSquares(list, adjacent, maxRange - 1, ignoreWalls, dir);
                }
        }
        return list;
    }

    /**
     * Gets players on squares only in the four {@link CardinalDirection}
     *
     * @param shooter     Player that is searching for other player (his position is used to get the other players)
     * @param maxRange    maximum distance of gotten squares
     * @param minRange    minimum distance of gotten squares
     * @param ignoreWalls specify if ignore or consider walls
     * @return List of players on squares in the 4 basic cardinal directions (diagonal squares are not returned)
     */
    List<Player> getPlayersOnCardinalDirectionSquares(Player shooter, int maxRange, int minRange, boolean ignoreWalls) {
        List<Player> players = new ArrayList<>();
        getCardinalDirectionSquares(shooter.getPosition(), maxRange, minRange, ignoreWalls)
                .stream()
                .map(square -> square.getHostedPlayers(shooter))
                .forEach(players::addAll);
        return players.stream().distinct().collect(Collectors.toList());
    }

    /**
     * Gets straight squares on {@code list}
     *
     * @param list        is a list passed by caller, needed to allow recursion
     * @param position    is the position from where get straight direction squares
     * @param maxRange    maximum distance of got squares
     * @param ignoreWalls specify if ignore or consider walls
     * @param dir         is the direction in which get squares
     * @author supernivem
     */
    private void getStraightSquares(List<Square> list, Square position, int maxRange, boolean ignoreWalls, CardinalDirection dir) {
        Square next;
        if (maxRange > 0 && position.getConnection(dir).isAccessible(ignoreWalls)) {
            next = this.getAdjacentSquare(position, dir);
            list.add(next);
            getStraightSquares(list, next, maxRange - 1, ignoreWalls, dir);
        }
    }

    /**
     * Gets reachable squares
     *
     * @param position starting position
     * @param maxMoves number of steps allowed
     * @return the list of squares reachable in at most maxMoves steps
     * @author supernivem
     */
    List<Square> getReachableSquare(Square position, int maxMoves) {
        List<Square> list = new ArrayList<>();
        getReachableSquare(position, list, maxMoves);
        return list;
    }

    /**
     * Gets reachable squares that have at least another player on
     *
     * @param position starting position
     * @param maxMoves max distance of the squares
     * @param player   player to exclude
     * @return the list of squares reachable in at most maxMoves steps with at least another player on
     */
    List<Square> getReachableSquaresWithOtherPlayers(Square position, int maxMoves, Player player) {
        List<Square> reachableSquares = getReachableSquare(position, maxMoves);
        List<Square> squaresWithOtherPlayers = new ArrayList<>();
        for (Square square : reachableSquares)
            if (square.hasOtherPlayers(player))
                squaresWithOtherPlayers.add(square);
        return squaresWithOtherPlayers;
    }

    /**
     * Gets reachable squares
     *
     * @param position starting position
     * @param maxMoves number of steps allowed
     * @param list     is a list passed by caller, needed to allow recursion
     * @author supernivem
     */
    private void getReachableSquare(Square position, List<Square> list, int maxMoves) {
        Square adjacent;
        int furtherMove = maxMoves - 1;
        if (!list.contains(position))
            list.add(position);
        if (maxMoves > 0) {
            for (CardinalDirection dir : CardinalDirection.values())
                if (position.getConnection(dir).isAccessible(false)) {
                    adjacent = this.getAdjacentSquare(position, dir);
                    this.getReachableSquare(adjacent, list, furtherMove);
                }
        }
    }

    /**
     * Gets all visible targets
     *
     * @param shooter     Player who's searching for targets
     * @param maxDistance Max targets distance
     * @param minDistance Min targets distance
     * @return List of possible visible players
     */
    List<Player> getVisibleTargets(Player shooter, int maxDistance, int minDistance) {
        List<Player> targets = new ArrayList<>();
        getVisibleSquares(shooter.getPosition(), maxDistance, minDistance, true)
                .stream()
                .map(s -> s.getHostedPlayers(shooter))
                .forEach(targets::addAll);
        return targets.stream().distinct().collect(Collectors.toList());
    }

    /**
     * Gets accessible directions from the current {@code position}
     *
     * @param position current position
     * @return a list of accessible direction
     * @author supernivem
     */
    List<CardinalDirection> getAccessibleDirection(Square position) {
        List<CardinalDirection> dir = new ArrayList<>();
        for (CardinalDirection c : CardinalDirection.values())
            if (position.getConnection(c).isAccessible(false))
                dir.add(c);
        return dir;
    }

    /**
     * This method returns the players on reachable squares
     *
     * @param position starting position
     * @param maxMoves max distance of other players
     * @param player   player to exclude
     * @return list of other players on reachable squares
     */
    List<Player> getOtherPlayersOnReachableSquares(Square position, int maxMoves, Player player) {
        List<Square> reachableSquares = getReachableSquare(position, maxMoves);
        List<Player> players = new ArrayList<>();
        for (Square square : reachableSquares)
            if (square.hasOtherPlayers(player))
                players.addAll(square.getHostedPlayers(player));
        return players.stream().distinct().collect(Collectors.toList());
    }

    /**
     * This method returns the square adjacent to the second square reachable in the same direction ( first square must be on the same column or the same row of second square)
     *
     * @param firstSquare  FirstSquare
     * @param secondSquare Second Square
     * @param ignoreWall   true to ignore walls
     * @return Square adjacent to the second square in the same direction
     */
    Square getThirdSquareInTheSameDirection(Square firstSquare, Square secondSquare, boolean ignoreWall) {
        if (firstSquare == secondSquare)
            return null;
        final int firstSquareRow = firstSquare.getRow();
        final int firstSquareCol = firstSquare.getCol();
        final int secondSquareRow = secondSquare.getRow();
        final int secondSquareCol = secondSquare.getCol();
        if (firstSquareRow == secondSquareRow) {
            if (firstSquareCol < secondSquareCol && secondSquare.getConnection(CardinalDirection.EAST).isAccessible(ignoreWall)) {
                return board[firstSquareRow][secondSquareCol + 1];
            }
            if (firstSquareCol > secondSquareCol && secondSquare.getConnection(CardinalDirection.WEST).isAccessible(ignoreWall))
                return board[firstSquareRow][secondSquareCol - 1];
        }
        if (firstSquareCol == secondSquareCol) {
            if (firstSquareRow < secondSquareRow && secondSquare.getConnection(CardinalDirection.SOUTH).isAccessible(ignoreWall))
                return board[secondSquareRow + 1][firstSquareCol];
            if (firstSquareRow > secondSquareRow && secondSquare.getConnection(CardinalDirection.NORTH).isAccessible(ignoreWall))
                return board[secondSquareRow - 1][firstSquareCol];
        }
        return null;
    }

    /**
     * This method returns all other players in the same direction (based on shooter position and on targetPlayersToExclude players position)
     *
     * @param shooter                Player who's searching for other players
     * @param targetPlayersToExclude list of players to exclude
     * @param maxTargetDistance      max targets  distance
     * @param minTargetDistance      min targets distance
     * @param ignoreWalls            if true walls are ignored
     * @return List of the players
     */
    List<Player> getPlayersInTheSameDirection(Player shooter, List<Player> targetPlayersToExclude, int maxTargetDistance, int minTargetDistance, boolean ignoreWalls) {
        List<Player> players = new ArrayList<>();
        CardinalDirection direction = getCardinalDirection(shooter.getPosition(), targetPlayersToExclude.get(0).getPosition());
        List<Square> squaresInADirectionIgnoringWalls = getSquaresInADirection(shooter.getPosition(), direction, maxTargetDistance, minTargetDistance, ignoreWalls);
        List<Player> playersToExclude = new ArrayList<>(targetPlayersToExclude);
        playersToExclude.add(shooter);
        for (Square square : squaresInADirectionIgnoringWalls) {
            players.addAll(square.getHostedPlayers(playersToExclude));
        }
        return players;
    }

    private CardinalDirection getCardinalDirection(Square origin, Square target) {
        if (origin.getRow() == target.getRow()) {
            if (origin.getCol() <= target.getCol())
                return CardinalDirection.EAST;
            return CardinalDirection.WEST;
        }
        if (origin.getCol() == target.getCol()) {
            if (origin.getRow() <= target.getRow())
                return CardinalDirection.SOUTH;
            return CardinalDirection.NORTH;
        }
        return null;
    }

    /**
     * Gets squares in a direction starting from Origin
     *
     * @param origin            Current square
     * @param direction         Direction of a target square from the Origin (ex. square 2.2 is WEST from 2.3)
     * @param maxTargetDistance max distance of a square from origin
     * @param minTargetDistance min distance of a square from origin
     * @param ignoreWalls       true if walls must be ignored
     * @return squares in the direction specified starting from Origin
     */
    private List<Square> getSquaresInADirection(Square origin, CardinalDirection direction, int maxTargetDistance, int minTargetDistance, boolean ignoreWalls) {
        List<Square> squares = new ArrayList<>();
        Square square = origin;
        if (minTargetDistance > 0) {
            for (int i = 0; i < minTargetDistance; i++)
                if (square.getConnection(direction).isAccessible(ignoreWalls))
                    square = getAdjacentSquare(square, direction);
        } else
            squares.add(origin);
        int cont = minTargetDistance;
        while (square.getConnection(direction).isAccessible(ignoreWalls) && cont < maxTargetDistance) {
            square = getAdjacentSquare(square, direction);
            squares.add(square);
            cont++;
        }
        return squares;
    }

    /**
     * This method returns squares at distance 1 accessible through doors.
     *
     * @param position Current position
     * @return List of squares ( a square for each room )
     */
    List<Square> getSquareInOtherVisibleRooms(Square position) {
        List<Square> list = new ArrayList<>();
        for (CardinalDirection cardinalDirection : CardinalDirection.values())
            if (position.getConnection(cardinalDirection).isDoor())
                list.add(getAdjacentSquare(position, cardinalDirection));
        return list;
    }

    /**
     * This method returns a list that contains all squares of the game board
     *
     * @return List containing the squares
     */
    public List<Square> getSquareList() {
        return squareList;
    }

    /**
     * Gets all spawns
     *
     * @return list of spawns
     */
    public List<SpawnSquare> getSpawnSquares() {
        return new ArrayList<>(spawns.values());
    }

    /**
     * Gets ID
     *
     * @return Gameboard ID
     */
    public int getId() {
        return gameBoardId;
    }
}