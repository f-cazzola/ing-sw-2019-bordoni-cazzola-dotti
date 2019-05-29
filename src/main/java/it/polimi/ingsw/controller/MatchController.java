package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.PlayerId;
import it.polimi.ingsw.model.command.Command;
import it.polimi.ingsw.view.ViewInterface;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class manages an entire match
 */
public class MatchController {
    private static final int[] POINTS_PER_KILL = {8, 6, 4, 2, 1};
    private static final int[] POINTS_PER_KILL_FLIPPED_BOARD = {2, 1};
    private static final int MIN_PLAYERS = 3;
    private static final int POINTS_PER_FIRST_BLOOD = 1;
    private final Match match;
    private final Map<PlayerId, ViewInterface> virtualViews = new LinkedHashMap<>();
    private final List<Player> players;

    public MatchController(Map<String, ViewInterface> lobby, int gameBoardNumber) {
        match = new Match(gameBoardNumber);
        PlayerId[] values = PlayerId.values();
        List<String> nicknames = new ArrayList<>(lobby.keySet());
        for (int i = 0; i < nicknames.size(); i++) {
            String nickname = nicknames.get(i);
            Player player = new Player(match, values[i], nickname);
            match.addPlayer(player, lobby.get(nickname));
            virtualViews.put(values[i], lobby.get(nickname));
        }
        players = match.getCurrentPlayers();
    }

    /**
     * This method orders a map following the official rules:
     * If multiple players dealt the same amount of damage, break the tie in favor of the player whose damage landed first
     *
     * @param counts            map to order (ID,numberof points/tokens)
     * @param orderByFirstBlood must be ordered by first blood
     * @return Leaderboard (ordered Map)
     */
    static Map<PlayerId, Long> sort(Map<PlayerId, Long> counts, List<PlayerId> orderByFirstBlood) {
        counts = counts.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        List<Long> duplicates = counts.values().stream().collect(Collectors.groupingBy(Function.identity()))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        PlayerId[] keys;
        if (!duplicates.isEmpty()) {
            keys = new PlayerId[counts.size()];
            Long[] values = new Long[counts.size()];
            int index = 0;
            for (Map.Entry<PlayerId, Long> mapEntry : counts.entrySet()) {
                keys[index] = mapEntry.getKey();
                values[index] = mapEntry.getValue();
                index++;
            }
            for (Long duplicate : duplicates) {
                int duplicateFrequency = Collections.frequency(Arrays.asList(values), duplicate);
                for (int j = 1; j < duplicateFrequency; j++) {
                    for (int i = 1; i < values.length; i++) {
                        if (values[i].equals(values[i - 1]) && orderByFirstBlood.indexOf(keys[i - 1]) > orderByFirstBlood.indexOf(keys[i])) {
                            PlayerId tempId = keys[i];
                            keys[i] = keys[i - 1];
                            keys[i - 1] = tempId;
                        }
                    }
                }
            }
            LinkedHashMap<PlayerId, Long> sortedMap = new LinkedHashMap<>();
            for (int i = 0; i < keys.length; i++) {
                sortedMap.put(keys[i], values[i]);
            }
            counts = sortedMap;
        }
        return counts;
    }

    /**
     * This method handles the first turn of each player
     */
    private void firstTurn() {
        players.stream().filter(currentPlayer -> !currentPlayer.isDisconnected()).forEachOrdered(currentPlayer -> {
            spawnFirstTime(currentPlayer);
            new TurnController(currentPlayer, virtualViews).runTurn();
            endTurnControls(currentPlayer);
        });
    }

    /**
     * This method starts the match
     */
    public void runMatch() {
        firstTurn();
        int currentPlayerIndex = 0;
        while (!match.isLastTurn()) {
            Player currentPlayer = players.get(currentPlayerIndex);
            if (!currentPlayer.isDisconnected()) {
                new TurnController(currentPlayer, virtualViews).runTurn();
                endTurnControls(currentPlayer);
            }
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
        runFinalFrenzyTurn(currentPlayerIndex);
    }

    /**
     * This method shall be called at end of each player's turn. Checks disconnections, restores cards, scores points, respawns dead players.
     *
     * @param currentPlayer Player whose turn just ended
     */
    private void endTurnControls(Player currentPlayer) {
        checkDisconnections();
        match.restoreCards();
        int numberOfKills = 0;
        for (Player player : players) {
            if (player.isDead()) {
                numberOfKills++;
                player.addDeaths();
                calculateTrackScores(player);
                respawn(player);
            }
        }
        if (numberOfKills > 1) { //doublekill points
            currentPlayer.addPoints(numberOfKills - 1);
        }
    }

    /**
     * This method handles the last turn of each player
     *
     * @param currentPlayerIndex index of the first player to play the last turn
     */
    private void runFinalFrenzyTurn(int currentPlayerIndex) {
        //player with no damage get flipped board
        giveFlippedBoards();
        for (int i = 0; i < players.size(); i++) {
            if (currentPlayerIndex == 0)
                match.firstPlayerPlayedLastTurn();
            Player currentPlayer = players.get(currentPlayerIndex);
            if (!currentPlayer.isDisconnected()) {
                new TurnController(currentPlayer, virtualViews);
                endTurnControls(currentPlayer);
            }
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
        calculateFinalScores();
    }

    /**
     * This method make all players with no damage (including those who were just scored) flip their boards over.
     * They keep marks and ammo.
     */
    private void giveFlippedBoards() {
        players.stream()
                .filter(player -> player.getHealth().isEmpty())
                .forEach(Player::flipBoard);
    }

    /**
     * This method calculates the leaderboard
     * Break the tie in favor of the player who got the higher score on the killshot track
     */
    private void calculateFinalScores() {
        List<PlayerId> killShootTrackLeaderBoard = Arrays.asList(scoreAllBoards());
        Map<PlayerId, Long> leaderBoard = players.stream()
                .collect(Collectors.toMap(Player::getId, player -> (long) player.getPoints(), (a, b) -> b, LinkedHashMap::new));
        leaderBoard = sort(leaderBoard, killShootTrackLeaderBoard);
        //virtualViews.forEach(ViewInterface::update(leaderBoard));
    }

    /**
     * This method is called after  the  final  turn,  score  all  boards  that  still  have  damage  tokens.
     * Score  them  as  you  usually would, except, of course, they don't have killshots.
     * If you are playing with the final frenzy rules, don't forget that flipped boards offer no point for first blood.
     *
     * @return KillshootTrack leaderboard
     */
    private PlayerId[] scoreAllBoards() {
        for (Player player : players) {
            if (!player.isDead()) {
                calculateTrackScores(player);
            }
        }
        PlayerId[] killshotTrack = sortByPoints(match.getKillshotTrack());
        scoreKillshotTrackPoints(killshotTrack);
        return killshotTrack;
    }

    private void endMatch() {
        //TODO send leaderboard and end-game message
    }

    /**
     * This method adds the points for the deadPlayer's track. Adds marks to killshotTrack
     *
     * @param deadPlayer track to score owner.
     */
    private void calculateTrackScores(Player deadPlayer) {
        List<PlayerId> track = deadPlayer.getHealth();
        if (!deadPlayer.isFlippedBoard())
            Objects.requireNonNull(getPlayerById(track.get(0))).addPoints(POINTS_PER_FIRST_BLOOD);
        //marks for 12th dmg
        //extra token on killshottrack for 12th dmg


        PlayerId playerId11thDamage = track.get(Player.MAX_DAMAGE - 2);
        match.addKillshot(playerId11thDamage);
        if (track.get(Player.MAX_DAMAGE - 1) != null) {
            Objects.requireNonNull(getPlayerById(playerId11thDamage)).addMarks(1, deadPlayer.getId());
            match.addKillshot(playerId11thDamage);
        }
        scoreTrackPoints(deadPlayer, sortByPoints(track));
    }

    /**
     * This method return an array of PlayerId ordered by most points scored on this track
     *
     * @param track track to score
     * @return Array ordered by first blood
     */
    private PlayerId[] sortByPoints(List<PlayerId> track) {
        List<PlayerId> orderByFirstBlood = track.stream().distinct().collect(Collectors.toList());

        Map<PlayerId, Long> counts = track.stream()
                .collect(Collectors.groupingBy(a -> a, Collectors.counting()));

        counts = sort(counts, orderByFirstBlood);
        return counts.keySet().toArray(new PlayerId[0]);
    }

    /**
     * This method adds the points for number of damage tokens
     *
     * @param deadPlayer player dead
     * @param keys       must be sorted by rules
     */
    private void scoreTrackPoints(Player deadPlayer, PlayerId[] keys) {
        final int[] pointsPerKill;
        int offset = 0;
        if (deadPlayer.isFlippedBoard()) pointsPerKill = POINTS_PER_KILL_FLIPPED_BOARD;
        else {
            pointsPerKill = POINTS_PER_KILL;
            offset = deadPlayer.getDeaths();
        }
        for (int i = 0; i < keys.length; i++) {
            int points;
            if (i + offset >= pointsPerKill.length)
                points = pointsPerKill[pointsPerKill.length - 1];
            else
                points = pointsPerKill[i + offset];
            Objects.requireNonNull(getPlayerById(keys[i])).addPoints(points);
        }
    }

    /**
     * This method assigns points for the killshoot track
     *
     * @param keys sorted by rules
     */
    private void scoreKillshotTrackPoints(PlayerId[] keys) {
        final int[] pointsPerKill = POINTS_PER_KILL;
        for (int i = 0; i < keys.length; i++) {
            int points;
            if (i >= pointsPerKill.length)
                points = pointsPerKill[pointsPerKill.length - 1];
            else
                points = pointsPerKill[i];
            Objects.requireNonNull(getPlayerById(keys[i])).addPoints(points);
        }
    }

    /**
     * This method handles the respawn phase
     *
     * @param currentPlayer player to respawn
     */
    private void respawn(Player currentPlayer) {
        List<Command> commands = new ArrayList<>(currentPlayer.getRespawnCommands());
        int selectedCommand = virtualViews.get(currentPlayer.getId()).sendCommands(commands, false);
        commands.get(selectedCommand).execute();
    }

    /**
     * This method handles the spawn before the first turn starts
     *
     * @param currentPlayer player to spawn
     */
    private void spawnFirstTime(Player currentPlayer) {
        List<Command> commands = new ArrayList<>(currentPlayer.getSpawnCommands());
        int selectedCommand = virtualViews.get(currentPlayer.getId()).sendCommands(commands, false);
        commands.get(selectedCommand).execute();
    }

    /**
     * This method checks if min number of players is connected or ends the match
     */
    private void checkDisconnections() {
        int counter = 0;
        for (Player player : players) {
            if (!player.isDisconnected())
                counter++;
        }
        if (counter < MIN_PLAYERS) {
            //end match
            calculateFinalScores();
            endMatch();
        }
    }

    private Player getPlayerById(PlayerId playerId) {
        for (Player player : players)
            if (player.getId().equals(playerId))
                return player;
        return null;
    }

}
