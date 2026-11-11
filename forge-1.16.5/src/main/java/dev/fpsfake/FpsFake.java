package dev.fpsfake;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Lógica do FPS falso. Java puro, sem nada do Minecraft (igual em todas as versões). */
public final class FpsFake {
	private static final int DEFAULT_MIN = 200;
	private static final int DEFAULT_MAX = 300;
	/** O Minecraft de verdade atualiza o contador de FPS 1 vez por segundo. */
	private static final long CHANGE_EVERY_MS = 1000L;
	private static final Pattern FPS_PREFIX = Pattern.compile("^\\d+ fps");

	private static Path config;
	private static volatile boolean enabled = false;
	private static volatile int minFps = DEFAULT_MIN;
	private static volatile int maxFps = DEFAULT_MAX;
	private static double current = (DEFAULT_MIN + DEFAULT_MAX) / 2.0;
	private static long nextChangeAt = 0L;

	private FpsFake() {}

	public static void init(Path configFile) {
		config = configFile;
		load();
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setEnabled(boolean value) {
		enabled = value;
		if (value) {
			reset();
		}
		save();
	}

	public static int getMin() {
		return minFps;
	}

	public static int getMax() {
		return maxFps;
	}

	public static void setRange(int a, int b) {
		minFps = Math.max(1, Math.min(a, b));
		maxFps = Math.max(1, Math.max(a, b));
		reset();
		save();
	}

	private static synchronized void reset() {
		int lo = minFps;
		int hi = maxFps;
		current = lo >= hi ? lo : ThreadLocalRandom.current().nextDouble(lo, hi);
		nextChangeAt = 0L;
	}

	/** Valor falso atual: muda 1 vez por segundo, andando aos poucos dentro da faixa. */
	public static synchronized int nextFakeFps() {
		int lo = minFps;
		int hi = maxFps;
		if (lo >= hi) {
			return lo;
		}
		long now = System.currentTimeMillis();
		if (now >= nextChangeAt) {
			nextChangeAt = now + CHANGE_EVERY_MS;
			double range = hi - lo;
			double next = current + ThreadLocalRandom.current().nextGaussian() * range * 0.04;
			if (next < lo) {
				next = lo + (lo - next);
			}
			if (next > hi) {
				next = hi - (next - hi);
			}
			current = Math.max(lo, Math.min(hi, next));
		}
		return (int) Math.round(current);
	}

	/** Troca o número no começo de uma linha do F3 do tipo "123 fps ..." pelo falso. */
	public static String patch(String line) {
		if (!enabled || line == null) {
			return line;
		}
		Matcher m = FPS_PREFIX.matcher(line);
		if (!m.find()) {
			return line;
		}
		return nextFakeFps() + " fps" + line.substring(m.end());
	}

	// ---------- textos dos comandos ----------

	public static String statusLine() {
		return "\u00a77FPS Fake: " + (enabled ? "\u00a7aLIGADO" : "\u00a7cDESLIGADO")
			+ " \u00a77(" + minFps + " a " + maxFps + " FPS)";
	}

	/** /fpsfake [on|off] */
	public static String commandFpsfake(String[] args) {
		if (args.length >= 1 && args[0].equalsIgnoreCase("on")) {
			setEnabled(true);
			return statusLine();
		}
		if (args.length >= 1 && args[0].equalsIgnoreCase("off")) {
			setEnabled(false);
			return statusLine();
		}
		return statusLine() + "\n\u00a77Uso: /fpsfake on|off  e  /fps <min> <max>";
	}

	/** /fps [min max] */
	public static String commandFps(String[] args) {
		if (args.length == 0) {
			return statusLine();
		}
		if (args.length == 2) {
			try {
				int a = Integer.parseInt(args[0]);
				int b = Integer.parseInt(args[1]);
				if (a >= 1 && b >= 1) {
					setRange(a, b);
					return "\u00a77FPS Fake: \u00a7aoscilando entre " + minFps + " e " + maxFps + " FPS";
				}
			} catch (NumberFormatException ignored) {
			}
		}
		return "\u00a7cUso: /fps <min> <max>  (ex.: /fps 200 300)";
	}

	// ---------- config ----------

	private static void load() {
		if (config == null || !Files.exists(config)) {
			return;
		}
		try (InputStream in = Files.newInputStream(config)) {
			Properties p = new Properties();
			p.load(in);
			enabled = Boolean.parseBoolean(p.getProperty("enabled", "false"));
			int lo = Integer.parseInt(p.getProperty("min", String.valueOf(DEFAULT_MIN)).trim());
			int hi = Integer.parseInt(p.getProperty("max", String.valueOf(DEFAULT_MAX)).trim());
			minFps = Math.max(1, Math.min(lo, hi));
			maxFps = Math.max(1, Math.max(lo, hi));
			reset();
		} catch (IOException | RuntimeException ignored) {
		}
	}

	private static void save() {
		if (config == null) {
			return;
		}
		try {
			if (config.getParent() != null) {
				Files.createDirectories(config.getParent());
			}
			try (OutputStream out = Files.newOutputStream(config)) {
				Properties p = new Properties();
				p.setProperty("enabled", Boolean.toString(enabled));
				p.setProperty("min", Integer.toString(minFps));
				p.setProperty("max", Integer.toString(maxFps));
				p.store(out, "FPS Fake");
			}
		} catch (IOException ignored) {
		}
	}
}
