package ru.galtsov;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = Application.run(Main.class);
        CoronaDesinfector desinfector = context.getObject(CoronaDesinfector.class, null);
        desinfector.start(new Room());
    }
}
