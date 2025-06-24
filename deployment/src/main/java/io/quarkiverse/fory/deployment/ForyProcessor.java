package io.quarkiverse.fory.deployment;

import java.util.List;
import java.util.Optional;

import jakarta.ws.rs.RuntimeType;

import org.apache.fory.serializer.Serializer;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.DotName;

import io.quarkiverse.fory.ForyBuildTimeConfig;
import io.quarkiverse.fory.ForyProducer;
import io.quarkiverse.fory.ForyRecorder;
import io.quarkiverse.fory.ForySerialization;
import io.quarkiverse.fory.ForySerializer;
import io.quarkiverse.fory.ReactiveForySerializer;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.Capability;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.resteasy.common.spi.ResteasyJaxrsProviderBuildItem;
import io.quarkus.resteasy.reactive.spi.MessageBodyReaderBuildItem;
import io.quarkus.resteasy.reactive.spi.MessageBodyWriterBuildItem;

class ForyProcessor {

    private static final String FEATURE = "fory";
    private static final DotName FURY_SERIALIZATION = DotName.createSimple(ForySerialization.class.getName());

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    public void findSerializableClasses(
            CombinedIndexBuildItem combinedIndex, BuildProducer<ForySerializerBuildItem> pojoProducer) {
        combinedIndex.getIndex().getAnnotations(FURY_SERIALIZATION).stream()
                .filter(annotation -> annotation.target().kind() == AnnotationTarget.Kind.CLASS)
                .forEach(i -> ForySerializerBuildItem.buildItems(i.target().asClass()).forEach(pojoProducer::produce));
    }

    @BuildStep
    void unremovableBeans(BuildProducer<AdditionalBeanBuildItem> beanProducer) {
        beanProducer.produce(AdditionalBeanBuildItem.unremovableOf(ForyProducer.class));
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    public void registerClasses(ForyBuildTimeConfig configs,
            ForyBuildItem fory, List<ForySerializerBuildItem> classes, ForyRecorder recorder) {
        for (ForySerializerBuildItem item : classes) {
            Class<? extends Serializer> serializer = null;
            if (item.getSerializer() != Serializer.class)
                serializer = item.getSerializer();
            recorder.registerClass(fory.getFory(), item.getClazz(), item.getClassId(), serializer);
        }

        if (configs.registerClassNames().isPresent()) {
            for (String name : configs.registerClassNames().get().split(",")) {
                recorder.registerClassByName(fory.getFory(), name, -1, Optional.empty());
            }
        }

        for (var config : configs.registerClass().entrySet()) {
            recorder.registerClassByName(fory.getFory(), config.getKey(), config.getValue().classId(),
                    config.getValue().serializer());
        }
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    public ForyBuildItem setup(
            ForyBuildTimeConfig config, BeanContainerBuildItem beanContainer, ForyRecorder recorder) {
        return new ForyBuildItem(recorder.createFory(config, beanContainer.getValue()));
    }

    @BuildStep
    public void registerResteasyIntegration(Capabilities capabilities,
            BuildProducer<ResteasyJaxrsProviderBuildItem> resteasyJaxrsProviderBuildItemBuildProducer,
            BuildProducer<MessageBodyReaderBuildItem> additionalReaders,
            BuildProducer<MessageBodyWriterBuildItem> additionalWriters) {
        if (capabilities.isPresent(Capability.RESTEASY)) {
            resteasyJaxrsProviderBuildItemBuildProducer
                    .produce(new ResteasyJaxrsProviderBuildItem(ForySerializer.class.getName()));
        } else if (capabilities.isPresent(Capability.RESTEASY_REACTIVE)) {
            registerHandler(RuntimeType.SERVER, additionalReaders, additionalWriters);
        }
        if (capabilities.isPresent(Capability.REST_CLIENT_REACTIVE)) {
            registerHandler(RuntimeType.CLIENT, additionalReaders, additionalWriters);
        }
    }

    private void registerHandler(RuntimeType type,
            BuildProducer<MessageBodyReaderBuildItem> additionalReaders,
            BuildProducer<MessageBodyWriterBuildItem> additionalWriters) {
        additionalReaders.produce(new MessageBodyReaderBuildItem.Builder(
                ReactiveForySerializer.class.getName(), Object.class.getName())
                .setMediaTypeStrings(List.of("application/fory", "application/*+fory"))
                .setRuntimeType(type)
                .setBuiltin(true).build());
        additionalWriters.produce(new MessageBodyWriterBuildItem.Builder(
                ReactiveForySerializer.class.getName(), Object.class.getName())
                .setMediaTypeStrings(List.of("application/fory", "application/*+fory"))
                .setRuntimeType(type)
                .setBuiltin(true).build());
    }
}
