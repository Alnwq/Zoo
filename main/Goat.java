public class Goat extends Animal {
    public Goat(String name, PettingZoo pettingZoo) { super(name, pettingZoo); }

    @Override
    public void cure() { System.out.println(name + " the Goat is being cured."); }

    @Override
    public void cured() { System.out.println(name + " the Goat is cured."); }
}
