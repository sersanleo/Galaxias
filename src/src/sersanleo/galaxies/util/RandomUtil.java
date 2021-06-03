package src.sersanleo.galaxies.util;

import java.util.List;
import java.util.Random;

public final class RandomUtil {
	public final long seed;

	private final Random rnd;

	public RandomUtil(long seed) {
		this.seed = seed;
		this.rnd = new Random(seed);

		System.out.println("RandomUtil creado con semilla " + seed);
	}

	public RandomUtil() {
		this(new Random().nextLong());
	}

	public int random(int max) {
		return rnd.nextInt(max);
	}

	public <T> T random(List<T> list) {
		return list.get(random(list.size()));
	}

	public <T> T randomWeighted(List<WeightedObject<T>> list, int weights) {
		int index = rnd.nextInt(weights);

		int sum = 0;
		WeightedObject<T> obj = null;
		for (int i = 0; i < list.size(); i++) {
			obj = list.get(i);

			sum += obj.weight;

			if (sum > index)
				break;
		}

		return obj.object;
	}

	public int random(int min, int max) {
		return min + random(max - min);
	}

	public float randomFloat() {
		return rnd.nextFloat();
	}

	public float random(float max) {
		return randomFloat() * max;
	}

	public float random(float min, float max) {
		return min + random(max - min);
	}

	public static final class WeightedObject<T> {
		public final T object;
		public final int weight;

		public WeightedObject(T object, int weight) {
			this.object = object;
			this.weight = weight;
		}
	}
}