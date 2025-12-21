public class Main {
    public static void main(String[] args) {

        LoadBalancer lb = new LoadBalancer(80);
        lb.start();

    }
}