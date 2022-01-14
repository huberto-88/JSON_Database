import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int duration = scanner.nextInt();
        int foodCostPerDay = scanner.nextInt();
        int flightCost = scanner.nextInt();
        int nightCost = scanner.nextInt();

        System.out.println(duration * foodCostPerDay + (duration - 1) * nightCost + flightCost * 2);
    }
}