package trreproduction;

import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.TinyUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.objectweb.asm.commons.Remapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class FieldRemappingTest {
    @TempDir
    Path tempDir;

    @BeforeEach
    void setup() throws IOException {
        Path inputJar = tempDir.resolve("input.jar");
        try (InputStream in = FieldRemappingTest.class.getResourceAsStream("/input.jar")) {
            Files.copy(in, inputJar);
        }
    }

    @Test
    void whatLoomDoes(@TempDir Path tempDir) throws IOException {
        Path inputJar = tempDir.resolve("input.jar");
        Path outputJar = tempDir.resolve("output.jar");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(FieldRemappingTest.class.getResourceAsStream("/mappings.tiny")))) {
            TinyRemapper tr = TinyRemapper.newRemapper()
                .withMappings(TinyUtils.createTinyMappingProvider(reader, "named", "intermediary"))
                .build();

            try (OutputConsumerPath outputConsumer = new OutputConsumerPath.Builder(outputJar).build()) {
                // Notice the order of calls here and compare with the other one!
                tr.readInputs(inputJar);
                Remapper remapper = tr.getRemapper();
                assertThat(remapper.mapFieldName("sample/SampleClass", "x", "I")).isEqualTo("field_1");
                tr.apply(outputConsumer);
            } finally {
                tr.finish();
            }
        }
    }

    @Test
    void whatSeemsToWork(@TempDir Path tempDir) throws IOException {
        Path inputJar = tempDir.resolve("input.jar");
        Path outputJar = tempDir.resolve("output.jar");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(FieldRemappingTest.class.getResourceAsStream("/mappings.tiny")))) {
            TinyRemapper tr = TinyRemapper.newRemapper()
                .withMappings(TinyUtils.createTinyMappingProvider(reader, "named", "intermediary"))
                .build();

            try (OutputConsumerPath outputConsumer = new OutputConsumerPath.Builder(outputJar).build()) {
                // Notice the order of calls here and compare with the other one!
                tr.readInputs(inputJar);
                tr.apply(outputConsumer);
                Remapper remapper = tr.getRemapper();
                assertThat(remapper.mapFieldName("sample/SampleClass", "x", "I")).isEqualTo("field_1");
            } finally {
                tr.finish();
            }
        }
    }
}
