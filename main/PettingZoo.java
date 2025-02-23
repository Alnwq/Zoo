import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;
import java.util.Collections;
import java.util.concurrent.*;

public class PettingZoo implements Runnable {
    private final List<Animal> animals = new ArrayList<>();
    private final List<Animal> stable = Collections.synchronizedList(new ArrayList<>());
    private final List<Animal> runway = Collections.synchronizedList(new ArrayList<>());
    private final Queue<Guest> waitingGuests = new ConcurrentLinkedQueue<>();
    private final BlockingQueue<Integer> foodSupply = new LinkedBlockingQueue<>(10);
    private final Doctor doctor = new Doctor();
    private final Object lock = new Object();
    private volatile boolean isOpen = true;
    private final Random random = new Random();

    public PettingZoo() {
        for (int i = 0; i < 3; i++) {
            Goat goat = new Goat("Goat-" + i, this);
            animals.add(goat);
            stable.add(goat);
        }
        for (int i = 0; i < 5; i++) {
            Bunny bunny = new Bunny("Bunny-" + i, this);
            animals.add(bunny);
            stable.add(bunny);
        }
        for (int i = 0; i < 10; i++) {
            GuineaPig guineaPig = new GuineaPig("GuineaPig-" + i, this);
            animals.add(guineaPig);
            stable.add(guineaPig);
        }
    }

    @Override
    public void run() {
        Thread doctorThread = new Thread(doctor);
        doctorThread.start();

        FoodProducer foodProducer = new FoodProducer(this);
        Thread producerThread = new Thread(foodProducer);
        producerThread.start();

        List<Thread> animalThreads = new ArrayList<>();
        for (Animal animal : animals) {
            Thread thread = new Thread(animal);
            animalThreads.add(thread);
            thread.start();
        }

        try {
            Thread.sleep(500);
            while (isOpen || !waitingGuests.isEmpty()) {
                synchronized (lock) { lock.wait(10); }
            }
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        isOpen = false;
        doctor.stop();
        foodProducer.stop();

        long deadline = System.currentTimeMillis() + 10000;
        for (Thread thread : animalThreads) {
            try {
                long waitTime = deadline - System.currentTimeMillis();
                if (waitTime > 0) { thread.join(waitTime); }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        try {
            doctorThread.join();
            producerThread.join();
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public void enter(Guest guest) {
        waitingGuests.add(guest);
        System.out.println("Guest entered: " + guest.toString());
    }

    public void exit(Guest guest) {
        waitingGuests.remove(guest);
        System.out.println("Guest exited: " + guest.toString());
    }

    public void move(Animal animal) {
        synchronized (this) {
            if (stable.contains(animal)) {
                stable.remove(animal);
                runway.add(animal);
                System.out.println(animal.getName() + " moved to runway");
            } else if (runway.contains(animal)) {
                runway.remove(animal);
                stable.add(animal);
                System.out.println(animal.getName() + " moved to stable");
            }
        }
    }

    public Animal getRandomAnimal() {
        synchronized (runway) {
            if (runway.isEmpty()) return null;
            return runway.get(random.nextInt(runway.size()));
        }
    }

    public void markSick(Animal animal) { doctor.addSickAnimal(animal); }

    public int buyPetFood(int patience) {
        try {
            Integer food = foodSupply.poll(patience, TimeUnit.MILLISECONDS);
            return food != null ? food : 0;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return 0;
        }
    }

    public void addFood(int amount) {
        try {
            if (!foodSupply.offer(amount, 100, TimeUnit.MILLISECONDS)) {
                System.out.println("Food wasted - supply is full");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void close() {
         isOpen = false;
        System.out.println("Zoo is closing");
    }
    public boolean isOpen() { return isOpen; }
}