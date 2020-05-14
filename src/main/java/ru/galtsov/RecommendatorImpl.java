package ru.galtsov;


@Singleton
public class RecommendatorImpl implements Recommendator {

    @InjectProperty(name = "wisky")
    private String alcohol;

    public RecommendatorImpl() {
        System.out.println("recommendator was created");
    }

    @Override
    public void recommend() {
        System.out.println("to protect from covid-2019, drink "+alcohol);
    }
}
