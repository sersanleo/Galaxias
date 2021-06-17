package src.sersanleo.galaxies.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import src.sersanleo.galaxies.AppConfig;

public final class RandomUtil {
	public final long seed;

	public final Random random;

	public RandomUtil(long seed) {
		this.seed = seed;
		this.random = new Random(seed);
		if (AppConfig.DEBUG)
			System.out.println("RandomUtil creado con semilla " + seed);
	}

	public RandomUtil() {
		this(new Random().nextLong());
	}

	public int random(int max) {
		return random.nextInt(max);
	}

	public <T> T random(List<T> list) {
		return list.get(random(list.size()));
	}

	public <T> T randomWeighted(Collection<WeightedObject<T>> collection, double weights) {
		double index = random.nextDouble() * weights;

		double sum = 0;
		WeightedObject<T> obj = null;
		Iterator<WeightedObject<T>> it = collection.iterator();
		while (it.hasNext()) {
			obj = it.next();

			sum += obj.weight;

			if (sum > index)
				break;
		}
		return obj.object;
	}

	public int random(int min, int max) {
		int range = max - min;
		if (range == 0)
			return min;
		else
			return min + random(range);
	}

	public float randomFloat() {
		return random.nextFloat();
	}

	public float random(float max) {
		return randomFloat() * max;
	}

	public float random(float min, float max) {
		return min + random(max - min);
	}

	public static final class WeightedObject<T> {
		public final T object;
		public final double weight;

		public WeightedObject(T object, double weight) {
			this.object = object;
			this.weight = weight;
		}
	}
}