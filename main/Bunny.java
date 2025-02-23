public class Bunny extends Animal {
    public Bunny(String name, PettingZoo pettingZoo) {
        super(name, pettingZoo);
    }

    @Override
    public void cure() { System.out.println(name + " the Bunny is being cured."); }

    @Override
    public void cured() { System.out.println(name + " the Bunny is cured."); }
}
