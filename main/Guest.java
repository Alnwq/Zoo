import java.util.Random;

public class Guest implements Runnable {
    private final String name;
    private final PettingZoo pettingZoo;
    private int food = 0;
    private Animal currentAnimal = null;
    private final Random random = new Random();

    public Guest(String name, PettingZoo pettingZoo) {
        this.name = name;
        this.pettingZoo = pettingZoo;
    }

    @Override
    public void run() {
        System.out.println(name + " entered the zoo");
        pettingZoo.enter(this);
        
        int purchasedFood = pettingZoo.buyPetFood(random.nextInt(100));
        if (purchasedFood <= 0) {
            System.out.println(name + " has nothing to do here");
            pettingZoo.exit(this);
            return;
        }
        food = purchasedFood;

        while (pettingZoo.isOpen() && food > 0) {
            standStill();
            Animal animal = pettingZoo.getRandomAnimal();
            if (animal != null && animal.tryToAttach(this)) {
                currentAnimal = animal;
                petAnimal();
            }
        }

        if (currentAnimal != null) { currentAnimal.detach(this); }
        pettingZoo.exit(this);
        System.out.println(name + " left the zoo");
    }

    private void petAnimal() {
        while (currentAnimal != null && food > 0) {
            try {
                Thread.sleep(100);
                if (!shouldStay()) {
                    currentAnimal.detach(this);
                    currentAnimal = null;
                } else {
                    System.out.println(name + " is petting " + currentAnimal.getName());
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    private void standStill() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean feed() {
        if (food > 0) {
            food--;
            return true;
        }
        return false;
    }

    public void leave(Animal animal) {
        if (currentAnimal == animal) { currentAnimal = null; }
    }

    private boolean shouldStay() { return random.nextBoolean(); }
}