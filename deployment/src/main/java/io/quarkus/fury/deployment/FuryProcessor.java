package io.quarkus.fury.deployment;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.fury.runtime.FurySerialization;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;

import java.util.Collection;

class FuryProcessor {

    private static final String FEATURE = "fury";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    public void findSerializableClasses(CombinedIndexBuildItem combinedIndex,
                                        BuildProducer<FurySerializerBuildItem> pojoProducer) {
        IndexView index = combinedIndex.getIndex();
        DotName serializableAnnotation = DotName.createSimple(FurySerialization.class.getName());
        Collection<ClassInfo> serializableClasses = index.getAnnotations(serializableAnnotation)
          .stream()
          .map(annotation -> annotation.target().asClass())
          .toList();
        for (ClassInfo classInfo : serializableClasses) {
            pojoProducer.produce(new FurySerializerBuildItem(classInfo));
        }
    }
}
