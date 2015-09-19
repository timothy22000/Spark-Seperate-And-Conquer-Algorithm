package CO880.testing.algorithm_v1;

import org.apache.spark.serializer.KryoRegistrator;

import com.esotericsoftware.kryo.Kryo;

public class Registrator implements KryoRegistrator {

	@Override
	public void registerClasses(Kryo arg0) {
		arg0.register(Attribute.class);
		arg0.register(Class.class);
		arg0.register(ClassFrequency.class);
		arg0.register(Rule.class);
		arg0.register(RuleEvaluator.class);
		arg0.register(RuleGenerator.class);
	}
}
