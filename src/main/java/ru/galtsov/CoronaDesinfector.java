package ru.galtsov;

/**
 * @author Evgeny Borisov
 */
@Singleton
public class CoronaDesinfector {

    @InjectByType
    private Announcer announcer;
    @InjectByType(componentName = "angryPoliceman")
    private Policeman policeman;


    public void start(Room room) {
        announcer.announce("Начинаем дезинфекцию, всё вон!");
        policeman.makePeopleLeaveRoom();
        desinfect(room);
        announcer.announce("Рискните зайти обратно");
    }

    private void desinfect(Room room){
        System.out.println("зачитывается молитва: 'корона изыди!' - молитва прочитана, вирус низвергнут в ад");
    }
}
