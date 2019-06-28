package it.polimi.ingsw.model;

import it.polimi.ingsw.model.command.*;
import it.polimi.ingsw.model.playerstate.ChoosingWeaponOptionState;
import it.polimi.ingsw.model.playerstate.ReadyToShootState;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class represents a weapon
 */
public class Weapon {
    private String name;
    private String description;
    private Map<Color, Integer> reloadingCost;
    private Map<Color, Integer> buyCost;
    private List<WeaponMode> weaponModes;
    private boolean extraMoveToDo = false;
    private boolean loaded = true;
    private WeaponMode selectedWeaponMode = null;
    private List<Player> targetPlayers = new ArrayList<>();
    private List<Square> targetSquares = new ArrayList<>();
    private int damageToDo;


    /**
     * This method returns the weapon's buy cost
     *
     * @return Map that represents the weapon's buy cost
     */
    public Map<Color, Integer> getWeaponBuyCost() {
        return buyCost == null ? new HashMap<>() : buyCost;
    }

    /**
     * This method returns the weapon's reloading cost
     *
     * @return Map that represents the weapon's reloading cost
     */
    public Map<Color, Integer> getReloadingCost() {
        return reloadingCost;
    }

    /**
     * This method returns true if the weapon is loaded
     *
     * @return true if weapon is loaded
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * This method returns the current selected weapon mode
     *
     * @return current weapon mode
     */
    public WeaponMode getSelectedWeaponMode() {
        return selectedWeaponMode;
    }

    /**
     * This method sets the weapon mode
     *
     * @param selectedWeaponMode weapon mode to use
     */
    public void setSelectedWeaponMode(WeaponMode selectedWeaponMode) {
        this.selectedWeaponMode = selectedWeaponMode;
        //set extraMove
        extraMoveToDo = selectedWeaponMode.isMoveShooter();
        damageToDo = selectedWeaponMode.getMaxNumberOfTargetPlayers();
        resetTargetLists();
    }

    /**
     * This method returns the weapon's name
     *
     * @return weapon's name
     */
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * This method removes the selected weapon mode and sets it to null
     */
    public void deselectWeaponMode() {
        selectedWeaponMode = null;
    }

    /**
     * This method returns all weapon mods
     *
     * @return List of all weapon's modes
     */
    public List<WeaponMode> getWeaponModes() {
        return weaponModes;
    }

    private List<WeaponCommand> getPossibleShootCommands(GameBoard gameboard, Player shooter, ReadyToShootState state) {
        List<WeaponCommand> possibleCommands = new ArrayList<>();
        if (selectedWeaponMode.isMoveTargetBeforeShoot())
            possibleCommands.addAll(getPossibleShootCommandsTargetCanMoveBeforeShoot(shooter, state));
        else if (selectedWeaponMode.isTargetPlayers() && selectedWeaponMode.isTargetSquare())
            possibleCommands.addAll(getPossibleShootCommandsMultiTarget(gameboard, shooter, state));
        else if (selectedWeaponMode.isTargetSquare())
            possibleCommands.addAll(getPossibleShootCommandsTargetSquare(gameboard, shooter, state));
        else if (selectedWeaponMode.isTargetPlayers())
            possibleCommands.addAll(getPossibleShootCommandsTargetPlayers(gameboard, shooter, state));
        else if (selectedWeaponMode.isTargetRoom())
            possibleCommands.addAll(getPossibleShootCommandsTargetRoom(shooter, state));
        return possibleCommands;
    }

    private List<WeaponCommand> getPossibleShootCommandsMultiTarget(GameBoard gameboard, Player shooter, ReadyToShootState state) {
        List<WeaponCommand> possibleCommands = new ArrayList<>();
        if (!targetSquares.isEmpty() && !targetPlayers.isEmpty()) {
            for (Square square : gameboard.getReachableSquare(targetPlayers.get(0).getPosition(), selectedWeaponMode.getMaxTargetMove())) { //generate a ShootCommand for each possible move of the target
                LinkedHashSet<EffectCommand> effectCommands = new LinkedHashSet<>();
                if (square.equals(targetSquares.get(0)))//if target player moves on target square
                    effectCommands.add(new EffectCommand(targetPlayers.get(0), selectedWeaponMode.getDamage(0) + selectedWeaponMode.getDamage(1), selectedWeaponMode.getMarks(), square, shooter.getId()));
                else
                    effectCommands.add(new EffectCommand(targetPlayers.get(0), selectedWeaponMode.getDamage(0), selectedWeaponMode.getMarks(), square, shooter.getId()));
                targetSquares.get(0).getHostedPlayers(new ArrayList<>(Arrays.asList(shooter, targetPlayers.get(0))))
                        .stream()
                        .filter(player -> !player.equals(shooter))
                        .forEach(player -> effectCommands.add(new EffectCommand(player, selectedWeaponMode.getDamage(1), selectedWeaponMode.getMarks(), player.getPosition(), shooter.getId()))); //damage players on target square (can be done outside loop)
                possibleCommands.add(new ShootCommand(state, new ArrayList<>(effectCommands), shooter));
            }
        }
        return possibleCommands;
    }

    private List<WeaponCommand> getPossibleShootCommandsTargetRoom(Player shooter, ReadyToShootState state) {
        List<WeaponCommand> possibleCommands = new ArrayList<>();
        possibleCommands.add(createShootCommandGetDamageZero(shooter, state));
        return possibleCommands;
    }

    /**
     * Create a ShootCommand with all target players (same damage to everyone getDamage(0), no moves)
     */
    private ShootCommand createShootCommandGetDamageZero(Player shooter, ReadyToShootState state) {
        LinkedHashSet<EffectCommand> effectCommands = new LinkedHashSet<>();
        targetPlayers.stream()
                .filter(player -> !player.getId().equals(shooter.getId()))
                .forEach(player -> effectCommands.add(new EffectCommand(player, selectedWeaponMode.getDamage(0), selectedWeaponMode.getMarks(), player.getPosition(), shooter.getId())));
        return new ShootCommand(state, new ArrayList<>(effectCommands), shooter);
    }

    /**
     * Create a ShootCommand with all target players (no moves)
     */
    private ShootCommand createSimpleShootCommand(Player shooter, ReadyToShootState state) {
        return new ShootCommand(state, createSimpleEffectCommandList(shooter), shooter);
    }

    private List<EffectCommand> createSimpleEffectCommandList(Player shooter) {
        LinkedHashSet<EffectCommand> effectCommands = new LinkedHashSet<>();
        for (int i = 0; i < targetPlayers.size(); i++) {
            if (!targetPlayers.get(i).getId().equals(shooter.getId()))
                effectCommands.add(new EffectCommand(targetPlayers.get(i), selectedWeaponMode.getDamage(i), selectedWeaponMode.getMarks(), targetPlayers.get(i).getPosition(), shooter.getId()));
        }
        return new ArrayList<>(effectCommands);
    }

    private List<WeaponCommand> getPossibleShootCommandsTargetPlayersAdditionalDamage(Player shooter, ReadyToShootState state) {
        List<WeaponCommand> possibleCommands = new ArrayList<>();
        // generalize method?
        for (int j = 0; j < targetPlayers.size(); j++) {
            List<EffectCommand> effectCommands = new ArrayList<>();
            for (int i = 0; i < targetPlayers.size(); i++) {
                int damage = selectedWeaponMode.getDamage(i);
                if (i == j && selectedWeaponMode.getAdditionalDamageAvailable() == 1 || i != j && selectedWeaponMode.getAdditionalDamageAvailable() == 2 || selectedWeaponMode.getAdditionalDamageAvailable() == 2 && targetPlayers.size() == 2) {
                    damage += selectedWeaponMode.getMaxAdditionalDamagePerPlayer();
                }
                if (!targetPlayers.get(i).getId().equals(shooter.getId())) {
                    effectCommands.add(new EffectCommand(targetPlayers.get(i), damage, selectedWeaponMode.getMarks(), targetPlayers.get(i).getPosition(), shooter.getId()));
                }
                if (selectedWeaponMode.getAdditionalDamageAvailable() == 2 && targetPlayers.size() == 2)
                    break;
            }
            possibleCommands.add(new ShootCommand(state, effectCommands, shooter));
        }
        return possibleCommands;
    }

    private List<WeaponCommand> getPossibleShootCommandsTargetPlayers(GameBoard gameboard, Player shooter, ReadyToShootState state) {
        List<WeaponCommand> possibleCommands = new ArrayList<>();
        if (!selectedWeaponMode.isMoveTargetAfterShoot()) {
            if (name.equals("Power Glove")) { //shooter must move on last target square
                List<EffectCommand> effectCommands = createSimpleEffectCommandList(shooter);
                effectCommands.add(new EffectCommand(shooter, 0, 0, targetPlayers.get(targetPlayers.size() - 1).getPosition(), shooter.getId()));
                possibleCommands.add(new ShootCommand(state, effectCommands, shooter));
            } else if (selectedWeaponMode.getAdditionalDamageAvailable() > 0) {
                possibleCommands.addAll(getPossibleShootCommandsTargetPlayersAdditionalDamage(shooter, state));
            } else
                possibleCommands.add(createSimpleShootCommand(shooter, state));
        } else { //create a shoot command for each possible move of the target
            Player targetPlayer = targetPlayers.get(0);
            for (Square square : gameboard.getCardinalDirectionSquares(targetPlayer.getPosition(), selectedWeaponMode.getMaxTargetMove(), 0, false)) {
                List<EffectCommand> effectCommands = new ArrayList<>();
                effectCommands.add(new EffectCommand(targetPlayer, selectedWeaponMode.getDamage(0), selectedWeaponMode.getMarks(), square, shooter.getId()));
                possibleCommands.add(new ShootCommand(state, effectCommands, shooter));
            }
        }
        return possibleCommands;
    }

    private List<WeaponCommand> getPossibleShootCommandsTargetSquareFlameThrower(Player shooter, ReadyToShootState state) {
        List<WeaponCommand> possibleCommands = new ArrayList<>();
        List<EffectCommand> effectCommands = new ArrayList<>();
        if (selectedWeaponMode.getMaxNumberOfTargetPlayers() != 4) {
            for (int i = 0; i < targetPlayers.size(); i++)
                effectCommands.add(new EffectCommand(targetPlayers.get(i), selectedWeaponMode.getDamage(i), selectedWeaponMode.getMarks(), targetPlayers.get(i).getPosition(), shooter.getId()));
        } else if (!targetSquares.isEmpty()) {
            for (Player targetPlayer : targetPlayers) {
                if (targetPlayer.getPosition() == targetSquares.get(0))
                    effectCommands.add(new EffectCommand(targetPlayer, selectedWeaponMode.getDamage(0), selectedWeaponMode.getMarks(), targetPlayer.getPosition(), shooter.getId()));
                if (targetPlayer.getPosition() != targetSquares.get(0))
                    effectCommands.add(new EffectCommand(targetPlayer, selectedWeaponMode.getDamage(1), selectedWeaponMode.getMarks(), targetPlayer.getPosition(), shooter.getId()));
            }
        }
        possibleCommands.add(new ShootCommand(state, effectCommands, shooter));
        return possibleCommands;
    }

    private List<WeaponCommand> getPossibleShootCommandsTargetSquareFragmentingWarhead(GameBoard gameBoard, Player shooter, ReadyToShootState state) {
        List<WeaponCommand> possibleCommands = new ArrayList<>();
        for (Square square : gameBoard.getReachableSquare(targetSquares.get(0), selectedWeaponMode.getMaxTargetMove())) {
            List<EffectCommand> effectCommands = new ArrayList<>();
            if (targetPlayers.size() > 1)
                effectCommands = IntStream.range(1, targetPlayers.size()).mapToObj(i -> new EffectCommand(targetPlayers.get(i), selectedWeaponMode.getDamage(i), selectedWeaponMode.getMarks(), targetPlayers.get(i).getPosition(), shooter.getId())).collect(Collectors.toList());
            effectCommands.add(new EffectCommand(targetPlayers.get(0), selectedWeaponMode.getDamage(0) + selectedWeaponMode.getDamage(1), selectedWeaponMode.getMarks(), square, shooter.getId()));
            possibleCommands.add(new ShootCommand(state, effectCommands, shooter));
        }
        return possibleCommands;
    }

    private List<WeaponCommand> getPossibleShootCommandsTargetSquare(GameBoard gameboard, Player shooter, ReadyToShootState state) {
        List<WeaponCommand> possibleCommands = new ArrayList<>();
        if (selectedWeaponMode.isCardinalDirectionMode()) {
            possibleCommands.addAll(getPossibleShootCommandsTargetSquareFlameThrower(shooter, state));
        } else if (selectedWeaponMode.isMoveTargetAfterShoot()) {
            possibleCommands.addAll(getPossibleShootCommandsTargetSquareFragmentingWarhead(gameboard, shooter, state));
        } else {
            possibleCommands.add(createShootCommandGetDamageZero(shooter, state));
        }
        return possibleCommands;
    }

    private List<WeaponCommand> getPossibleShootCommandsTargetCanMoveBeforeShoot(Player shooter, ReadyToShootState state) {
        List<WeaponCommand> possibleCommands = new ArrayList<>();
        List<EffectCommand> effectCommands = new ArrayList<>();
        if (selectedWeaponMode.isTargetSquare()) {
            for (int i = 0; i < targetPlayers.size(); i++) {
                if (selectedWeaponMode.getDamage().size() == 1)
                    effectCommands.add(new EffectCommand(targetPlayers.get(i), selectedWeaponMode.getDamage(0), selectedWeaponMode.getMarks(), targetSquares.get(0), shooter.getId()));
                if (selectedWeaponMode.getDamage().size() > 1)
                    effectCommands.add(new EffectCommand(targetPlayers.get(i), selectedWeaponMode.getDamage(i), selectedWeaponMode.getMarks(), targetSquares.get(0), shooter.getId()));
            }
        }
        possibleCommands.add(new ShootCommand(state, effectCommands, shooter));
        return possibleCommands;
    }

    private List<WeaponCommand> getPossibleSelectTargetCommands(GameBoard gameboard, Player shooter, ReadyToShootState state) {
        List<WeaponCommand> possibleCommands = new ArrayList<>();
        if (selectedWeaponMode.isMoveTargetBeforeShoot())
            possibleCommands.addAll(getPossibleSelectTargetCommandsTargetCanMoveBeforeShoot(gameboard, shooter, state));
        else if (selectedWeaponMode.isTargetPlayers() && selectedWeaponMode.isTargetSquare())
            possibleCommands.addAll(getPossibleSelectTargetCommandsMultiTarget(gameboard, shooter, state));
        else if (selectedWeaponMode.isTargetSquare())
            possibleCommands.addAll(getPossibleSelectTargetCommandsTargetSquare(gameboard, shooter, state));
        else if (selectedWeaponMode.isTargetPlayers())
            possibleCommands.addAll(getPossibleSelectTargetCommandsTargetPlayers(gameboard, shooter, state));
        else if (selectedWeaponMode.isTargetRoom())
            possibleCommands.addAll(getPossibleSelectTargetCommandsTargetRoom(gameboard, shooter, state));
        return possibleCommands;
    }

    private List<WeaponCommand> getPossibleSelectTargetCommandsTargetCanMoveBeforeShoot(GameBoard gameboard, Player shooter, ReadyToShootState state) {
        List<WeaponCommand> possibleCommands = new ArrayList<>();
        if (targetSquares.isEmpty()) { //select a square (first call)
            List<Square> visibleSquares = gameboard.getVisibleSquares(shooter.getPosition(), selectedWeaponMode.getMaxTargetDistance(), selectedWeaponMode.getMinTargetDistance(), false);
            visibleSquares.stream()
                    .filter(square -> !gameboard.getReachableSquaresWithOtherPlayers(square, selectedWeaponMode.getMaxTargetMove(), shooter).isEmpty())
                    .filter(square -> !square.equals(shooter.getPosition()) || selectedWeaponMode.getMaxTargetDistance() <= 0)
                    .forEach(square -> {
                        if (selectedWeaponMode.getMaxTargetDistance() == 0 && selectedWeaponMode.getMinTargetDistance() == 0)
                            targetSquares.add(square);
                        else
                            possibleCommands.add(new SelectTargetSquareCommand(state, square));
                    });
        }
        if (!targetSquares.isEmpty()) { //pick target players if not already selected (second call)
            List<Player> otherPlayersOnReachableSquares = gameboard.getOtherPlayersOnReachableSquares(targetSquares.get(0), selectedWeaponMode.getMaxTargetMove(), shooter);
            for (Player possibleTargetPlayer : otherPlayersOnReachableSquares)
                if (!targetPlayers.contains(possibleTargetPlayer) && !possibleTargetPlayer.getId().equals(shooter.getId()))
                    possibleCommands.add(new SelectTargetPlayerCommand(state, possibleTargetPlayer));
        }
        return possibleCommands;
    }

    private List<WeaponCommand> getPossibleSelectTargetCommandsMultiTarget(GameBoard gameboard, Player shooter, ReadyToShootState state) {
        List<WeaponCommand> possibleCommands = new ArrayList<>();
        if (targetPlayers.isEmpty())
            gameboard.getVisibleTargets(shooter, selectedWeaponMode.getMaxTargetDistance(), selectedWeaponMode.getMinTargetDistance())
                    .stream()
                    .filter(possibleTargetPlayer -> !possibleTargetPlayer.getId().equals(shooter.getId()))
                    .forEach(player -> possibleCommands.add(new SelectTargetPlayerCommand(state, player)));
        if (targetSquares.isEmpty())
            gameboard.getVisibleSquares(shooter.getPosition(), selectedWeaponMode.getMaxTargetDistance(), selectedWeaponMode.getMinTargetDistance(), false).forEach(square -> possibleCommands.add(new SelectTargetSquareCommand(state, square)));
        return possibleCommands;
    }

    private List<WeaponCommand> getPossibleSelectTargetCommandsTargetRoom(GameBoard gameboard, Player shooter, ReadyToShootState state) {
        //Furnace basic mode
        List<WeaponCommand> possibleCommands = new ArrayList<>();
        if (targetSquares.isEmpty()) { //get squares at distance 1 accessible through doors.
            List<Square> possibleRooms = new ArrayList<>();
            for (Square square : gameboard.getSquareInOtherVisibleRooms(shooter.getPosition())) { //rooms must have players to damage
                for (Square squareInTheSameRoom : gameboard.getRoomSquares(square)) {
                    if (squareInTheSameRoom.hasOtherPlayers(shooter)) {
                        possibleRooms.add(square);
                        break;
                    }
                }
            }
            possibleRooms.forEach(square -> possibleCommands.add(new SelectTargetSquareCommand(state, square)));
        } else if (targetPlayers.isEmpty()) //add all players in the room
            gameboard.getRoomSquares(targetSquares.get(0)).forEach(square -> targetPlayers.addAll(square.getHostedPlayers()));
        return possibleCommands;
    }

    private List<WeaponCommand> getPossibleSelectTargetCommandsTargetPlayersVisibleStandard(GameBoard gameboard, Player shooter, ReadyToShootState state) {
        List<Player> visibleTargets = gameboard.getVisibleTargets(shooter, selectedWeaponMode.getMaxTargetDistance(), selectedWeaponMode.getMinTargetDistance());
        for (Player targetPlayer1 : targetPlayers) {
            visibleTargets = visibleTargets.stream()
                    .filter(newTarget -> !targetPlayer1.getId().equals(newTarget.getId()))
                    .collect(Collectors.toList());
        }
        if (selectedWeaponMode.isEachTargetOnDifferentSquares()) //shockwave basiceffect
        {
            for (Player targetPlayer1 : targetPlayers) {
                visibleTargets = visibleTargets.stream()
                        .filter(newTarget -> !targetPlayer1.getPosition().equals(newTarget.getPosition()))
                        .collect(Collectors.toList());
            }
        }
        return visibleTargets.stream()
                .filter(possibleTargetPlayer -> !possibleTargetPlayer.getId().equals(shooter.getId()))
                .map(player -> new SelectTargetPlayerCommand(state, player)).collect(Collectors.toList());
    }

    private List<WeaponCommand> getPossibleSelectTargetCommandsTargetPlayersVisibleByOtherTarget(GameBoard gameboard, Player shooter, ReadyToShootState state) {
        List<WeaponCommand> possibleCommands = new ArrayList<>();
        List<Player> visibleTargets = gameboard.getVisibleTargets(targetPlayers.get(targetPlayers.size() - 1), selectedWeaponMode.getMaxTargetDistance(), selectedWeaponMode.getMinTargetDistance());
        for (Player targetPlayer : targetPlayers) {
            visibleTargets = visibleTargets.stream()
                    .filter(visibleTarget -> !visibleTarget.getId().equals(shooter.getId()) && !visibleTarget.getId().equals(targetPlayer.getId()))
                    .collect(Collectors.toList());
        }
        visibleTargets.stream()
                .filter(possibleTargetPlayer -> !possibleTargetPlayer.getId().equals(shooter.getId()))
                .forEach(player -> possibleCommands.add(new SelectTargetPlayerCommand(state, player)));
        return possibleCommands;
    }

    private List<WeaponCommand> getPossibleSelectTargetCommandsTargetPlayersVisible(GameBoard gameboard, Player shooter, ReadyToShootState state) {
        List<WeaponCommand> possibleCommands = new ArrayList<>();
        if (targetPlayers.isEmpty()) {
            List<Player> visibleTargets = gameboard.getVisibleTargets(shooter, selectedWeaponMode.getMaxTargetDistance(), selectedWeaponMode.getMinTargetDistance());
            if (selectedWeaponMode.getName().contains("tsunami")) //directly pick targets
                targetPlayers.addAll(visibleTargets);
            else
                visibleTargets.stream()
                        .filter(possibleTargetPlayer -> !possibleTargetPlayer.getId().equals(shooter.getId()))
                        .forEach(player -> possibleCommands.add(new SelectTargetPlayerCommand(state, player)));
        } else {
            if (name.equals("Hellion") && targetPlayers.size() == 1) //add other players on the same square (shooter cant be on this square)
                targetPlayers.addAll(targetPlayers.get(0).getPosition().getHostedPlayers(targetPlayers.get(0)));
            else if (selectedWeaponMode.isTargetVisibleByOtherTarget()) { //THOR
                possibleCommands.addAll(getPossibleSelectTargetCommandsTargetPlayersVisibleByOtherTarget(gameboard, shooter, state));
            } else if (!selectedWeaponMode.getName().contains("tsunami")) { //otherweapons
                possibleCommands.addAll(getPossibleSelectTargetCommandsTargetPlayersVisibleStandard(gameboard, shooter, state));
            }
        }
        return possibleCommands;
    }

    private List<WeaponCommand> getPossibleSelectTargetCommandsTargetPlayersCardinalDirection(GameBoard gameboard, Player shooter, ReadyToShootState state) {
        List<WeaponCommand> possibleCommands = new ArrayList<>();
        if (selectedWeaponMode.isTargetVisibleByShooter()) {//PowerGlove rocketfistmode
            if (targetPlayers.isEmpty())
                gameboard.getVisibleTargets(shooter, selectedWeaponMode.getMaxTargetDistance(), selectedWeaponMode.getMinTargetDistance())
                        //.stream()
                        ///.filter(possibleTargetPlayer -> !possibleTargetPlayer.getId().equals(shooter.getId()))
                        .forEach(player -> possibleCommands.add(new SelectTargetPlayerCommand(state, player)));
            else {
                Square squareInTheSameDirection = gameboard.getThirdSquareInTheSameDirection(shooter.getPosition(), targetPlayers.get(0).getPosition(), false);
                if (squareInTheSameDirection != null)
                    squareInTheSameDirection.getHostedPlayers()
                            //.stream()
                            //.filter(possibleTargetPlayer -> !possibleTargetPlayer.getId().equals(shooter.getId()))
                            .forEach(player -> possibleCommands.add(new SelectTargetPlayerCommand(state, player)));
            }
        } else { //RAILGUN
            if (targetPlayers.isEmpty())
                gameboard.getPlayersOnCardinalDirectionSquares(shooter, selectedWeaponMode.getMaxTargetDistance(), selectedWeaponMode.getMinTargetDistance(), true)
                        .stream()
                        .filter(possibleTargetPlayer -> !possibleTargetPlayer.getId().equals(shooter.getId()))
                        .forEach(player -> possibleCommands.add(new SelectTargetPlayerCommand(state, player)));
            else  //target another player in the same direction
            {
                if (targetPlayers.get(0).getPosition().equals(shooter.getPosition())) { //samesquare check
                    gameboard.getPlayersOnCardinalDirectionSquares(shooter, selectedWeaponMode.getMaxTargetDistance(), selectedWeaponMode.getMinTargetDistance(), true)
                            .stream()
                            .distinct()
                            .filter(possibleTargetPlayer -> !possibleTargetPlayer.getId().equals(shooter.getId()))
                            .filter(possibleTargetPlayer -> !possibleTargetPlayer.getId().equals(targetPlayers.get(0).getId()))
                            .forEach(player -> possibleCommands.add(new SelectTargetPlayerCommand(state, player)));
                }
                gameboard.getPlayersInTheSameDirection(shooter, targetPlayers, selectedWeaponMode.getMaxTargetDistance(), selectedWeaponMode.getMinTargetDistance(), true)
                        .stream()
                        .filter(possibleTargetPlayer -> !possibleTargetPlayer.getId().equals(shooter.getId()))
                        .filter(possibleTargetPlayer -> !possibleTargetPlayer.getId().equals(targetPlayers.get(0).getId()))
                        .forEach(player -> possibleCommands.add(new SelectTargetPlayerCommand(state, player)));
            }
        }
        return possibleCommands;
    }

    private List<WeaponCommand> getPossibleSelectTargetCommandsTargetPlayersNotVisible(GameBoard gameboard, Player shooter, ReadyToShootState state) {
        List<Player> allOtherPlayers = new ArrayList<>(shooter.getMatch().getCurrentPlayers());
        allOtherPlayers.remove(shooter);
        allOtherPlayers.removeAll(gameboard.getVisibleTargets(shooter, GameBoard.ROWS * GameBoard.COLUMNS - 1, 0));
        return allOtherPlayers.stream()
                .map(player -> new SelectTargetPlayerCommand(state, player)).collect(Collectors.toList());
    }

    private List<WeaponCommand> getPossibleSelectTargetCommandsTargetPlayers(GameBoard gameboard, Player shooter, ReadyToShootState state) {
        List<WeaponCommand> possibleCommands = new ArrayList<>();
        if (selectedWeaponMode.isTargetVisibleByShooter() && !selectedWeaponMode.isCardinalDirectionMode())
            possibleCommands.addAll(getPossibleSelectTargetCommandsTargetPlayersVisible(gameboard, shooter, state));
        else if (selectedWeaponMode.isCardinalDirectionMode())
            possibleCommands.addAll(getPossibleSelectTargetCommandsTargetPlayersCardinalDirection(gameboard, shooter, state));
        else if (!selectedWeaponMode.isTargetVisibleByShooter())
            possibleCommands.addAll(getPossibleSelectTargetCommandsTargetPlayersNotVisible(gameboard, shooter, state));
        return possibleCommands;
    }

    private List<WeaponCommand> getPossibleSelectTargetCommandsTargetSquareFlameThrower(GameBoard gameBoard, Player shooter, ReadyToShootState state) {
        List<WeaponCommand> possibleCommands = new ArrayList<>();
        final int maxTargets = 4;
        if (targetSquares.size() == 1) {
            if (!targetPlayers.isEmpty() || selectedWeaponMode.getMaxNumberOfTargetPlayers() == maxTargets) { //pick 2nd square after players on the 1st one
                Square secondTargetSquare = gameBoard.getThirdSquareInTheSameDirection(shooter.getPosition(), targetSquares.get(0), false);
                if (secondTargetSquare != null && secondTargetSquare.hasOtherPlayers(shooter)) //ask possible 2nd square in the same direction (flameth)
                    possibleCommands.add(new SelectTargetSquareCommand(state, secondTargetSquare));
            } else if (selectedWeaponMode.getMaxNumberOfTargetPlayers() != maxTargets) { //select max 1 player per square
                List<Player> possibleTargetPlayers = new ArrayList<>(targetSquares.get(0).getHostedPlayers(shooter));
                possibleTargetPlayers.stream()
                        .filter(possibleTargetPlayer -> !possibleTargetPlayer.getId().equals(shooter.getId()))
                        .distinct()
                        .forEach(player -> possibleCommands.add(new SelectTargetPlayerCommand(state, player)));
            }
            if (selectedWeaponMode.getMaxNumberOfTargetPlayers() == maxTargets)
                targetPlayers.addAll(targetSquares.get(0).getHostedPlayers(shooter));
        } else { //manage 2nd square
            if (selectedWeaponMode.getMaxNumberOfTargetPlayers() != maxTargets) {
                targetSquares.get(1).getHostedPlayers(shooter).stream()
                        .filter(possibleTargetPlayer -> !possibleTargetPlayer.getId().equals(targetPlayers.get(0).getId()))
                        .forEach(player -> possibleCommands.add(new SelectTargetPlayerCommand(state, player)));
            } else if (!targetPlayers.containsAll(targetSquares.get(1).getHostedPlayers(shooter)))
                targetPlayers.addAll(targetSquares.get(1).getHostedPlayers(shooter));
        }
        targetPlayers = targetPlayers.stream().distinct().collect(Collectors.toList());
        return possibleCommands;
    }

    private List<WeaponCommand> getPossibleSelectTargetCommandsTargetSquareFragmentingWarhead(Player shooter, ReadyToShootState state) {
        List<WeaponCommand> possibleCommands = new ArrayList<>();
        List<Player> possibleTargetPlayers = new ArrayList<>();
        //add other players on the same square
        if (targetPlayers.isEmpty()) { //commands to target 1st player
            targetSquares.stream().map(square -> square.getHostedPlayers(shooter)).forEach(possibleTargetPlayers::addAll);
            possibleTargetPlayers.stream()
                    .filter(possibleTargetPlayer -> !possibleTargetPlayer.getId().equals(shooter.getId()))
                    .forEach(player -> possibleCommands.add(new SelectTargetPlayerCommand(state, player)));
        } else if (targetPlayers.size() == 1) //add other players on the square (fragmenting warhead effect)
            targetPlayers.addAll(targetPlayers.get(0).getPosition().getHostedPlayers(new ArrayList<>(Arrays.asList(shooter, targetPlayers.get(0)))));
        return possibleCommands;
    }

    private List<WeaponCommand> getPossibleSelectTargetCommandsTargetSquare(GameBoard gameboard, Player shooter, ReadyToShootState state) {
        List<WeaponCommand> possibleCommands = new ArrayList<>();
        if (targetSquares.isEmpty()) { //selectSquare(s) first
            List<Square> possibleTargetSquares = new ArrayList<>();
            if (selectedWeaponMode.isCardinalDirectionMode())
                possibleTargetSquares.addAll(gameboard.getVisibleSquares(shooter.getPosition(), selectedWeaponMode.getMinTargetDistance(), selectedWeaponMode.getMinTargetDistance(), true));
            else
                possibleTargetSquares.addAll(gameboard.getVisibleSquares(shooter.getPosition(), selectedWeaponMode.getMaxTargetDistance(), selectedWeaponMode.getMinTargetDistance(), true));
            possibleTargetSquares.stream().filter(square -> square.hasOtherPlayers(shooter)).forEach(square -> possibleCommands.add(new SelectTargetSquareCommand(state, square)));
        } else {
            if (selectedWeaponMode.isCardinalDirectionMode()) {
                possibleCommands.addAll(getPossibleSelectTargetCommandsTargetSquareFlameThrower(gameboard, shooter, state));
            } else if (selectedWeaponMode.getName().contains("fragmenting warhead")) {//rocketlaunch
                possibleCommands.addAll(getPossibleSelectTargetCommandsTargetSquareFragmentingWarhead(shooter, state));
            } else if (targetPlayers.isEmpty()) //Other modes target all players
                targetSquares.stream().map(square -> square.getHostedPlayers(shooter)).forEach(targetPlayers::addAll);
        }
        return possibleCommands;
    }

    /**
     * This method returns the possible commands to execute ( ExtraMoveCommands, SelectTargetCommands, ShootCommands)
     *
     * @param gameboard Gameboard
     * @param shooter   Player who is using the weapon
     * @param state     Shooter's state
     * @return List of all possible commands to execute
     */
    public List<Command> getPossibleCommands(GameBoard gameboard, Player shooter, ReadyToShootState state) {
        List<Command> possibleCommands = new ArrayList<>();
        if (hasExtraMove())
            possibleCommands.addAll(getPossibleExtraMoveCommands(gameboard, shooter, state));
        if (!hasMaximumTargets() && hasDamageToDo())
            possibleCommands.addAll(getPossibleSelectTargetCommands(gameboard, shooter, state));
        if (hasSufficientTargets() && hasDamageToDo())
            possibleCommands.addAll(getPossibleShootCommands(gameboard, shooter, state));
        return possibleCommands;
    }

    private List<MoveCommand> getPossibleExtraMoveCommands(GameBoard gameBoard, Player shooter, ReadyToShootState state) {
        Square currentPosition = shooter.getPosition();
        List<MoveCommand> list = new ArrayList<>();
        List<Square> shooterAccessibleSquare = shooter.getAccessibleSquare(selectedWeaponMode.getMaxShooterMove());
        shooterAccessibleSquare.remove(currentPosition);
        for (Square square : shooterAccessibleSquare) {
            shooter.untracedMove(square);
            if (!loaded || !getPossibleSelectTargetCommands(gameBoard, shooter, state).isEmpty()) {
                MoveCommand moveCommand = new MoveCommand(shooter, square, state);
                list.add(moveCommand);
            }
            shooter.untracedMove(currentPosition);
        }
        return list;
    }

    private boolean hasMaximumTargets() {
        return selectedWeaponMode.getMaxNumberOfTargetPlayers() == targetPlayers.size();
    }

    private boolean hasSufficientTargets() {
        return selectedWeaponMode.getMinNumberOfTargetPlayers() <= targetPlayers.size();
    }

    /**
     * This method returns the commands to select which weapon mode to use.
     *
     * @param player Player who is using the weapon
     * @param state  Shooter's state
     * @return List of commands to select a weapon mode.
     */
    public List<SelectWeaponModeCommand> getSelectWeaponModeCommands(Player player, ChoosingWeaponOptionState state) {
        return weaponModes.stream()
                .map(weaponMode -> new SelectWeaponModeCommand(player, state, weaponMode))
                .collect(Collectors.toList());
    }

    /**
     * This method adds a target player to the target list
     *
     * @param targetPlayer player to be added
     */
    public void addTargetPlayer(Player targetPlayer) {
        targetPlayers.add(targetPlayer);
    }

    /**
     * This method adds a target square to the target list
     *
     * @param targetSquare Square to be added
     */
    public void addTargetSquare(Square targetSquare) {
        targetSquares.add(targetSquare);
    }

    /**
     * This method removes the player from the target list
     *
     * @param targetPlayer Player to be removed
     */
    public void removeTargetPlayer(Player targetPlayer) {
        targetPlayers.remove(targetPlayer);
    }

    /**
     * This method removes the square from the targets list
     *
     * @param targetSquare Square to be removed
     */
    public void removeTargetSquare(Square targetSquare) {
        targetSquares.remove(targetSquare);
    }

    /**
     * This method reloads the weapon
     */
    public void reload() {
        loaded = true;
    }

    /**
     * This method unloads the weapon
     */
    public void unload() {
        loaded = false;
    }

    /**
     * This method returns true if the weapon can generate MoveCommands when invoked
     *
     * @return true if weapon can move its owner
     */
    public boolean hasExtraMove() {
        if (selectedWeaponMode.getName().contains("slice and dice"))
            return extraMoveToDo && !hasMaximumTargets();
        return extraMoveToDo && !isSelectingTargets();
    }

    /**
     * This method marks that extra moves have been used
     */
    public void useExtraMoves() {
        extraMoveToDo = false;
    }

    /**
     * This method resets the extraMoves
     */
    public void resetMoves() {
        extraMoveToDo = true;
    }

    /**
     * This method removes the selected weapon mode and sets it to null
     *
     * @param weaponMode Weapon mode to deselect
     */
    public void deselectWeaponMode(WeaponMode weaponMode) {
        if (selectedWeaponMode.equals(weaponMode))
            selectedWeaponMode = null;
    }

    /**
     * This method returns true if the weapon can shoot again
     *
     * @return True if weapon can shoot
     */
    public boolean hasDamageToDo() {
        return damageToDo > 0;
    }

    /**
     * This method returns true if a target square or a player has been selected
     *
     * @return True if target has been selected
     */
    private boolean isSelectingTargets() {
        return !targetPlayers.isEmpty() || !targetSquares.isEmpty();
    }

    /**
     * This method must be called when executing a ShootCommand
     */
    public void shoot() {
        if (selectedWeaponMode.getName().contains("slice and dice") && extraMoveToDo && targetPlayers.size() == 1)
            damageToDo--;
        else {
            damageToDo = 0;
            unload();
        }
        resetTargetLists();
    }

    private void resetTargetLists() {
        targetPlayers = new ArrayList<>();
        targetSquares = new ArrayList<>();
    }

    /**
     * Must be called to instantiate some fields which are not saved on json
     */
    public void postDeserialization() {
        resetTargetLists();
        loaded = true;
        weaponModes.forEach(WeaponMode::postDeserialization);
    }
}
