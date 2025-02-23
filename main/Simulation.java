
import java.util.*;
import java.util.concurrent.*;

public class Simulation {
    public static void main(String[] args) throws InterruptedException {
        PettingZoo pettingZoo = new PettingZoo();

        Thread zooThread = new Thread(pettingZoo);
        zooThread.start();

        ExecutorService guestPool = Executors.newFixedThreadPool(300);
        List<Guest> guests = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            Guest guest = new Guest("Guest-" + i, pettingZoo);
            guests.add(guest);
            guestPool.submit(guest);
        }

        Thread.sleep(5000);
        pettingZoo.close();

        guestPool.shutdown();
        guestPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

        zooThread.join();

        System.out.println("Simulation has finished");
    }
}
