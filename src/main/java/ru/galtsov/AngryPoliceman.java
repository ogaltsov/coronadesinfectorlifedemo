package ru.galtsov;

@Singleton(componentName = "angryPoliceman")
public class AngryPoliceman implements Policeman {

    @Override
    public void makePeopleLeaveRoom() {
        System.out.println("Всех убью! Вон пошли");
    }
}
