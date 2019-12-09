package net.djkieras.garage.component;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinProvider;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.platform.Platform;

public class GpioManager {

	private static final GpioController GPIO_CONTROLLER = GpioFactory.getInstance();
	// TODO inject pin provider
	public static PinProvider PIN_PROVIDER = new RaspiPin();

	public GpioPinDigitalOutput initializePin(int pinAddress, String pinOutputName, PinState defaultPinState,
			PinState pinShutdownState) {
		Pin pin = PIN_PROVIDER.getPinByAddress(pinAddress);
		return initializePin(pin, pinOutputName, defaultPinState, pinShutdownState);
	}

	public GpioPinDigitalOutput initializePin(String pinName, String pinOutputName, PinState defaultPinState,
			PinState pinShutdownState) {
		Pin pin = PIN_PROVIDER.getPinByName(pinName);
		return initializePin(pin, pinOutputName, defaultPinState, pinShutdownState);
	}

	public GpioPinDigitalOutput initializePin(Pin pin, String pinOutputName, PinState defaultPinState,
			PinState pinShutdownState) {
		final GpioPinDigitalOutput pinOutput = GPIO_CONTROLLER.provisionDigitalOutputPin(pin, pinOutputName,
				defaultPinState);
		pinOutput.setShutdownOptions(true, pinShutdownState);
		return pinOutput;
	}

	public void clearPinProvisions() {
		if (GPIO_CONTROLLER.getProvisionedPins() != null && GPIO_CONTROLLER.getProvisionedPins().size() > 0) {
			GPIO_CONTROLLER.unprovisionPin(GPIO_CONTROLLER.getProvisionedPins()
					.toArray(new GpioPin[GPIO_CONTROLLER.getProvisionedPins().size()]));
		}
	}

	public void test() throws Exception {
		System.out.println("<--Pi4J--> GPIO Control Example ... started.");

		System.setProperty("pi4j.platform", Platform.SIMULATED.getId());

		// create gpio controller
		final GpioController gpio = GpioFactory.getInstance();

		// provision gpio pin #01 as an output pin and turn on
		final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.HIGH);

		// set shutdown state for this pin
		pin.setShutdownOptions(true, PinState.LOW);

		System.out.println("--> GPIO state should be: ON");

		Thread.sleep(5000);

		// turn off gpio pin #01
		pin.low();
		System.out.println("--> GPIO state should be: OFF");

		Thread.sleep(5000);

		// toggle the current state of gpio pin #01 (should turn on)
		pin.toggle();
		System.out.println("--> GPIO state should be: ON");

		Thread.sleep(5000);

		// toggle the current state of gpio pin #01 (should turn off)
		pin.toggle();
		System.out.println("--> GPIO state should be: OFF");

		Thread.sleep(5000);

		// turn on gpio pin #01 for 1 second and then off
		System.out.println("--> GPIO state should be: ON for only 1 second");
		pin.pulse(1000, true); // set second argument to 'true' use a blocking
								// call

		// stop all GPIO activity/threads by shutting down the GPIO controller
		// (this method will forcefully shutdown all GPIO monitoring threads and
		// scheduled tasks)
		gpio.shutdown();

		System.out.println("Exiting ControlGpioExample");
	}

};;