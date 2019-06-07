package de.kjosu.jnstinct.activation;

public enum Squash {

	Logistic((x, derivate) -> {
		final double fx = 1 / (1 + Math.exp(-x));
		return (derivate) ? fx * (1 - fx) : fx;
	}),
	Tanh((x, derivate) -> {
		return (derivate) ? 1 - Math.pow(Math.tanh(x), 2) : Math.tanh(x);
	}),
	Identity((x, derivate) -> {
		return (derivate) ? 1 : x;
	}),
	Step((x, derivate) -> {
		return (derivate) ? 0 : ((x > 0) ? 1 : 0);
	}),
	Relu((x, derivate) -> {
		return (derivate) ? ((x > 0) ? 1 : 0) : ((x > 0) ? x : 0);
	}),
	SoftSign((x, derivate) -> {
		final double d = 1 + Math.abs(x);
		return (derivate) ? x / Math.pow(d, 2) : x / d;
	}),
	Sinusoid((x, derivate) -> {
		return (derivate) ? Math.cos(x) : Math.sin(x);
	}),
	Gaussian((x, derivate) -> {
		final double d = Math.exp(-Math.pow(x, 2));
		return (derivate) ? -2 * x * d : d;
	}),
	BentIdentity((x, derivate) -> {
		final double d = Math.sqrt(Math.pow(x, 2) + 1);
		return (derivate) ? x / (2 * d) + 1 : (d - 1) / 2 + x;
	}),
	Bipolar((x, derivate) -> {
		return (derivate) ? 0 : ((x > 0) ? 1 : -1);
	}),
	BipolarSigmoid((x, derivate) -> {
		final double d = 2 / (1 + Math.exp(-x)) - 1;
		return (derivate) ? 1 / 2 * (1 + d) * (1 - d) : d;
	}),
	HardTanh((x, derivate) -> {
		return (derivate) ? ((x > -1 && x < 1) ? 1 : 0) : Math.max(-1, Math.min(1, x));
	}),
	Absolute((x, derivate) -> {
		return (derivate) ? ((x < 0) ? -1 : 1) : Math.abs(x);
	}),
	Inverse((x, derivate) -> {
		return (derivate) ? -1 : 1 - x;
	}),
	Selu((x, derivate) -> {
		final double alpha = 1.6732632423543772848170429916717;
		final double scale = 1.0507009873554804934193349852946;
		final double fx = (x > 0) ? x : alpha * Math.exp(x) - alpha;
		return (derivate) ? ((x > 0) ? scale : (fx + alpha) * scale) : fx * scale;
	});

	private ActivationIF function;

	Squash(final ActivationIF function) {
		this.function = function;
	}

	public double activate(final double x, final boolean derivate) {
		return function.activate(x, derivate);
	}
}
