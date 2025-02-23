import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

abstract class Animal implements Runnable {
    protected final String name;
    protected final PettingZoo pettingZoo;
    private volatile boolean running = true;
    private List<Guest> attachedGuests = new ArrayList<>();
    private final Random random = new Random();

    public Animal(String name, PettingZoo pettingZoo) {
        this.name = name;
        this.pettingZoo = pettingZoo;
    }

    @Override
    public void run() {
        while (running && pettingZoo.isOpen()) {
            if (shouldMove()) {
                detachAllGuests();
                pettingZoo.move(this);
            }
            remainInPlace();
            if (!attachedGuests.isEmpty()) {
                Guest randomGuest = getRandomGuest();
                if (randomGuest != null && !randomGuest.feed()) {
                    shouldMove();
                }
            }
        }
    }

    protected boolean shouldMove() { return random.nextBoolean(); }

    protected void remainInPlace() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(70, 210));
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public synchronized boolean tryToAttach(Guest guest) {
        if (!running) return false;
        attachedGuests.add(guest);
        return true;
    }

    public synchronized void detach(Guest guest) { attachedGuests.remove(guest); }

    private void detachAllGuests() {
        for (Guest guest : new ArrayList<>(attachedGuests)) {
            guest.leave(this);
            detach(guest);
        }
    }

    private Guest getRandomGuest() {
        synchronized (attachedGuests) {
            if (attachedGuests.isEmpty()) return null;
            return attachedGuests.get(random.nextInt(attachedGuests.size()));
        }
    }

    public String getName() { return name; }

    public void stop() {
        running = false;
        detachAllGuests();
    }

    public abstract void cure();
    public abstract void cured();
}
