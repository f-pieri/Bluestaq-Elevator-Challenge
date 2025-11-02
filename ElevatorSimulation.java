import java.util.*;

public class ElevatorSimulation {
    
    enum Direction { UP, DOWN, IDLE }

    static class Elevator {
        private int currentFloor;
        private Direction direction;
        private boolean doorsOpen;
        private boolean emergency;

        private final TreeSet<Integer> upStops = new TreeSet<>();

        private final TreeSet<Integer> downStops = new TreeSet<> (Comparator.reverseOrder()); 

        public Elevator(int startFloor) {
            this.currentFloor = startFloor;
            this.direction = Direction.IDLE;
            this.doorsOpen = false;
            this.emergency = false;
        }

        public void call(int floor, Direction requestedDirection) {
            if (emergency) return;
            if (requestedDirection == Direction.UP) upStops.add(floor);
            else if (requestedDirection == Direction.DOWN) downStops.add(floor);
            chooseDirectionIfIdle();
        }

        public void selectFloor(int floor) {
            if (emergency) return;
            if (floor == currentFloor) {
                openDoors();
                return;
            }
            if (floor > currentFloor) upStops.add(floor);
            else downStops.add(floor);
            chooseDirectionIfIdle();
        }

        private void chooseDirectionIfIdle() {
            if (direction != Direction.IDLE) return;
            if (!upStops.isEmpty()) direction = Direction.UP;
            else if (!downStops.isEmpty()) direction = Direction.DOWN;
        }

        public void tick() {
            if (emergency) {
                System.out.print("EMERGENCY STOP at floor " + currentFloor + ". Doors " + (doorsOpen ? "open" : "closed"));
                return;
            }

            if (doorsOpen) {
                closeDoors();
                return;
            }

            if (upStops.isEmpty() && downStops.isEmpty()) {
                direction = Direction.IDLE;
                System.out.print(status());
                return;
            }

            if (direction == Direction.IDLE) chooseDirectionIfIdle();

            if (direction == Direction.UP) {
                currentFloor++;
                System.out.println("Moved up to " + currentFloor);

                if (upStops.remove(currentFloor) || downStops.remove(currentFloor)) {
                    openDoors();
                }

                if (upStops.isEmpty() && !downStops.isEmpty() && !doorsOpen) {
                    direction = Direction.DOWN;
                }
            }
            else if (direction == Direction.DOWN) {
                currentFloor--;
                System.out.print("Moved down to " + currentFloor);

                if (downStops.remove(currentFloor) || upStops.remove(currentFloor)) {
                    openDoors();
                }

                if (downStops.isEmpty() && !upStops.isEmpty() && !doorsOpen) {
                    direction = Direction.UP;
                }
            }

            if (upStops.isEmpty() && downStops.isEmpty() && !doorsOpen) {
                direction = Direction.IDLE;
            }
        }

        private void openDoors() {
            doorsOpen = true;
            System.out.println("Doors opening at floor " + currentFloor + ". Cleared requests for this floor.");

            upStops.remove(currentFloor);
            downStops.remove(currentFloor);
        }

        private void closeDoors() {
            doorsOpen = false;
            System.out.print("Doors closed at floor " + currentFloor + ".");

            if (upStops.isEmpty() && downStops.isEmpty()) direction = Direction.IDLE;
            else if (direction == Direction.IDLE) chooseDirectionIfIdle();
        }

        public void emergencyStop() {
            emergency = true;
            direction = Direction.IDLE;
            openDoors();
            System.out.print("Emergency stop engaged at floor " + currentFloor);
        }

        public void resetEmergency() {
            emergency = false;
            doorsOpen = false;
            System.out.print("Emergency cleared at floor" + currentFloor);
            chooseDirectionIfIdle();
        }

        public String status() {
            return String.format("Floor=%d, Direction=%s, UpPending=%s, DownPending=%s", currentFloor, direction, doorsOpen, upStops, downStops);
        }
    }
}