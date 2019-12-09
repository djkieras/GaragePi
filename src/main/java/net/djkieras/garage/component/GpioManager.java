package net.djkieras.garage.component;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinProvider;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

@SuppressWarnings("static-access")
public class GpioManager {

	private PinProvider pinProvider;
	// TODO inject pin provider
	//public static PinProvider PIN_PROVIDER = new RaspiPin();

	public GpioManager() {
		this.pinProvider = new RaspiPin();
	}
	
	public GpioManager(PinProvider pinProvider) {
		this.pinProvider = pinProvider;
	}
	
	private GpioController getGpioController() {
		return GpioFactory.getInstance();
	}
	
	public void setPinProvider(PinProvider pinProvider) {
		this.pinProvider = pinProvider;
	}
	
	public PinProvider getPinProvider() {
		return this.pinProvider;
	}
	
	public GpioPin getPin(int pinAddress) {
		return getGpioController().getProvisionedPin(getPinProvider().getPinByAddress(pinAddress));
	}
	
	public GpioPin getPin(Pin pin) {
		return getGpioController().getProvisionedPin(pin);
	}
	
	public GpioPin getPin(String pinName) {
		return getGpioController().getProvisionedPin(pinName);
	}
	
	public GpioPinDigitalOutput registerPinDefaultOff(int pinAddress, String pinOutputName) {
		Pin pin = getPinProvider().getPinByAddress(pinAddress);
		return registerPin(pin, pinOutputName, PinState.LOW, PinState.LOW);
	}

	public GpioPinDigitalOutput registerPinDefaultOff(String pinName, String pinOutputName) {
		Pin pin = getPinProvider().getPinByName(pinName);
		return registerPin(pin, pinOutputName, PinState.LOW, PinState.LOW);
	}

	public GpioPinDigitalOutput registerPinDefaultOn(int pinAddress, String pinOutputName) {
		Pin pin = getPinProvider().getPinByAddress(pinAddress);
		return registerPin(pin, pinOutputName, PinState.HIGH, PinState.LOW);
	}

	public GpioPinDigitalOutput registerPinDefaultOn(String pinName, String pinOutputName) {
		Pin pin = getPinProvider().getPinByName(pinName);
		return registerPin(pin, pinOutputName, PinState.HIGH, PinState.LOW);
	}
	
	public GpioPinDigitalOutput registerPin(Pin pin, String pinOutputName, PinState defaultPinState,
			PinState pinShutdownState) {
		final GpioPinDigitalOutput pinOutput = getGpioController().provisionDigitalOutputPin(pin, pinOutputName,
				defaultPinState);
		pinOutput.setShutdownOptions(true, pinShutdownState);
		return pinOutput;
	}
	
	public void deregisterPin(String pinName) {
		deregisterPin(getPin(pinName));
	}
	
	public void deregisterPin(int pinAddress) {
		deregisterPin(getPin(pinAddress));
	}
	
	public void deregisterPin(Pin pin) {
		deregisterPin(getPin(pin));
	}
	
	public void deregisterPin(GpioPin pin) {
		getGpioController().unprovisionPin(pin);
	}
	
	public void deregisterAllPins() {
		if (getGpioController().getProvisionedPins() != null && getGpioController().getProvisionedPins().size() > 0) {
			getGpioController().unprovisionPin(getGpioController().getProvisionedPins()
					.toArray(new GpioPin[getGpioController().getProvisionedPins().size()]));
		}
	}
	
	public void momentaryPinActivation(GpioPinDigitalOutput pin, int milliseconds) {
		pin.pulse(milliseconds, true);
	}
	
	public void shutdownGpioController() {
		if (! getGpioController().isShutdown()) {
			getGpioController().shutdown();
		}
	}
	
	public void finalize() {
		shutdownGpioController();
	}
	
}