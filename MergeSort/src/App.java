import bridges.base.SLelement;
import bridges.connect.Bridges;

public class App {
    public static void main(String[] args) throws Exception {
        Bridges bridges = new Bridges(8, "xrogers", "1002662683963");
        
        SLelement sle0 = new SLelement("Hello", "");
        SLelement sle1 = new SLelement("World", "");

        sle0.setNext(sle1);

        sle0.getVisualizer().setColor("black");
        sle0.getVisualizer().setOpacity((float) 0.5);
        sle1.getVisualizer().setColor("green");

        bridges.setDataStructure(sle0);
        bridges.visualize();
    }
}
