public class GuineaPig extends Animal {
    public GuineaPig(String name, PettingZoo pettingZoo) { super(name, pettingZoo); }

    @Override
    public void cure() { System.out.println(name + " the Guinea Pig is being cured."); }

    @Override
    public void cured() { System.out.println(name + " the Guinea Pig is cured."); }
}
