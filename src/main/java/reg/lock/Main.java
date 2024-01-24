/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reg.lock;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.platform.Platforms;
import com.pi4j.util.Console;
import java.lang.reflect.InvocationTargetException;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Main {
    private static final Console console = new Console();
    static Context pi4j = null;

    @GetMapping("/unlockTest/{testString}")
    public String unlockTest(@PathVariable String testString){
        console.print("Received " + testString);
        return testString;
    }

    public static void main(String[] args) throws Exception {
        console.box("Hello Rasbian world !");
        try {
            pi4j = Pi4J.newAutoContext();
            SpringApplication.run(Main.class, args);
        } catch (Exception e) {
            console.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (pi4j != null) {
                pi4j.shutdown();
            }
        }
    }

    private void run(Context pi4j) throws Exception {
        Platforms platforms = pi4j.platforms();
        int PIN_LED = 22; // PIN 15 = BCM 22

        console.box("Pi4J PLATFORMS");
        console.println();
        platforms.describe().print(System.out);
        console.println();

        var ledConfig = DigitalOutput.newConfigBuilder(pi4j)
                        .id("led")
                        .name("LED Flasher")
                        .address(PIN_LED)
                        .shutdown(DigitalState.LOW)
                        .initial(DigitalState.LOW)
                        .provider("pigpio-digital-output");

        var led = pi4j.create(ledConfig);
        int counter = 0;
        while (true) {
            if (led.equals(DigitalState.HIGH)) {
                led.low();
                System.out.println("low"  + counter);
            } else {
                led.high();
                System.out.println("high" + counter);
            }
            Thread.sleep(2000);
            counter++;
        }
    }

}
