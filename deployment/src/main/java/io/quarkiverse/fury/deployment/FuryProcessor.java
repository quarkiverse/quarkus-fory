package io.quarkiverse.fury.deployment;

import java.util.List;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.DotName;

import io.quarkiverse.fury.FuryBuildTimeConfig;
import io.quarkiverse.fury.FuryProducer;
import io.quarkiverse.fury.FuryRecorder;
import io.quarkiverse.fury.FurySerialization;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class FuryProcessor {

    private static final String FEATURE = "fury";
    private static final DotName FURY_SERIALIZATION = DotName.createSimple(FurySerialization.class.getName());

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    public void findSerializableClasses(
            CombinedIndexBuildItem combinedIndex, BuildProducer<FurySerializerBuildItem> pojoProducer) {
        combinedIndex.getIndex().getAnnotations(FURY_SERIALIZATION).stream()
                .filter(annotation -> annotation.target().kind() == AnnotationTarget.Kind.CLASS)
                .forEach(i -> pojoProducer.produce(new FurySerializerBuildItem(i.target().asClass())));
    }

    @BuildStep
    void unremovableBeans(
            BuildProducer<AdditionalBeanBuildItem> beanProducer,
            BuildProducer<UnremovableBeanBuildItem> unremovableBeans) {
        beanProducer.produce(AdditionalBeanBuildItem.unremovableOf(FuryProducer.class));
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    public void registerClasses(
            FuryBuildItem fury, List<FurySerializerBuildItem> classes, FuryRecorder recorder) {
        for (FurySerializerBuildItem item : classes) {
            recorder.registerClass(fury.getFury(), item.getClazz(), item.getClassId(), item.getSerializer());
        }
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    public FuryBuildItem setup(
            FuryBuildTimeConfig config, BeanContainerBuildItem beanContainer, FuryRecorder recorder) {
        return new FuryBuildItem(recorder.createFury(config, beanContainer.getValue()));
    }
}
