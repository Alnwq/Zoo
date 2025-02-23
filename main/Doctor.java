
import java.util.*;
import java.util.concurrent.*;

public class Doctor implements Runnable {
    private final List<Animal> sickAnimals = new ArrayList<>();
    private final Object lock = new Object();
    private boolean running = true;

    @Override
    public void run() {
        while (running || !sickAnimals.isEmpty()) {
            Animal animalToTreat = null;

            synchronized (lock) {
                if (!sickAnimals.isEmpty()) {
                    animalToTreat = sickAnimals.remove(0);
                } else {
                    try {
                        lock.wait(1000); 
                    } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }
            }

            if (animalToTreat != null) { treatAnimal(animalToTreat); }
        }
    }

    public void addSickAnimal(Animal animal) {
        synchronized (lock) {
            sickAnimals.add(animal);
            lock.notify(); 
        }
    }

    private void treatAnimal(Animal animal) {
        System.out.println("Doctor is treating " + animal.getName());
        animal.cure();
        animal.cured();
    }

    public void stop() {
        running = false;
        synchronized (lock) { lock.notifyAll(); }
    }
}
