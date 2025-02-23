public class FoodProducer implements Runnable {
    private final PettingZoo zoo;
    private volatile boolean running = true;

    public FoodProducer(PettingZoo zoo) { this.zoo = zoo; }

    @Override
    public void run() {
        while (running && zoo.isOpen()) {
            try {
                Thread.sleep(10);
                zoo.addFood(1);
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    public void stop() { running = false; }
}
