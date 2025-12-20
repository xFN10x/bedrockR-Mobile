package fn10.bedrockrmobile.utils;

import java.util.Random;

import fn10.bedrockr.utils.Greetings;

public class CompatGreetings extends Greetings {

    /**
     * Lets you get a greeting without nextInt(int, int) (which doesn't work below api 34)
     */
    public static Greeting getCompatGreeting() {
        Random random = new Random();

        return GREETINGS[random.nextInt(GREETINGS.length-1)];
    }

    public static Greeting GetGreeting() {
        return getCompatGreeting();
    }

}
