/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.golden;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationModule;
import jakarta.xml.bind.JAXB;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TimeZone;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

/**
 * Golden-file payload checks: marshal a fully-populated DTO instance to XML and JSON and compare against a saved
 * reference file, using semantic (not byte-for-byte) comparison - so reformatting doesn't cause false failures, but a
 * real structural change (different element/field names, different nesting, different types) does. The intent is to
 * have a safety net for a future swap of the underlying JAX-RS/JAXB/Jackson libraries: run these against the old stack
 * once to record the golden files, then again after the swap - a failure here means the wire format changed, which is
 * exactly what a client depending on this REST API would also notice.
 *
 * <p>Uses the same JSON setup Jersey's {@code jersey-media-json-jackson} uses in production (an {@link ObjectMapper}
 * with {@link JakartaXmlBindAnnotationModule} registered, so the same {@code @XmlElement}/{@code @XmlAttribute}/etc.
 * annotations drive both XML and JSON output) - not a separately-configured mapper that might drift from what's
 * actually served.
 *
 * <p>If a golden file doesn't exist yet, it's written and the check fails on purpose - forcing a deliberate "inspect
 * what got recorded, then re-run" step rather than silently trusting an auto-generated fixture.
 */
public final class GoldenPayloadSupport {

    private static final Path GOLDEN_DIR = Path.of("src/test/resources/golden-payloads");

    // Date fields (e.g. RESTRule's validAfter/validBefore) render through JAXB's default java.util.Date binding
    // and through plain Date.toString() getters, both of which use the JVM's default time zone - pinned to UTC here
    // so the recorded/compared payloads don't depend on what time zone the machine running the test happens to be
    // in (a dev laptop in CET produced different golden files than a UTC CI runner otherwise).
    private static final TimeZone GOLDEN_TIME_ZONE = TimeZone.getTimeZone("UTC");

    private static final ObjectMapper JSON = new ObjectMapper()
            .registerModule(new JakartaXmlBindAnnotationModule())
            .enable(SerializationFeature.INDENT_OUTPUT);

    private GoldenPayloadSupport() {}

    public static String toXml(Object value) {
        TimeZone previous = TimeZone.getDefault();
        TimeZone.setDefault(GOLDEN_TIME_ZONE);
        try {
            StringWriter w = new StringWriter();
            JAXB.marshal(value, w);
            return w.toString();
        } finally {
            TimeZone.setDefault(previous);
        }
    }

    public static String toJson(Object value) {
        TimeZone previous = TimeZone.getDefault();
        TimeZone.setDefault(GOLDEN_TIME_ZONE);
        try {
            return JSON.writeValueAsString(value);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            TimeZone.setDefault(previous);
        }
    }

    public static void assertMatchesGoldenXml(String name, Object value) throws IOException {
        String actual = toXml(value);
        Path file = GOLDEN_DIR.resolve(name + ".xml");

        if (!Files.exists(file)) {
            record(file, actual);
            return;
        }

        String expected = Files.readString(file, StandardCharsets.UTF_8);
        Diff diff = DiffBuilder.compare(expected)
                .withTest(actual)
                .ignoreWhitespace()
                .checkForSimilar()
                .build();
        if (diff.hasDifferences()) {
            fail("XML payload for '" + name + "' differs from " + file + ":\n" + diff + "\n\nActual:\n" + actual);
        }
    }

    public static void assertMatchesGoldenJson(String name, Object value) throws IOException {
        String actual = toJson(value);
        Path file = GOLDEN_DIR.resolve(name + ".json");

        if (!Files.exists(file)) {
            record(file, actual);
            return;
        }

        String expected = Files.readString(file, StandardCharsets.UTF_8);
        JsonNode expectedNode = JSON.readTree(expected);
        JsonNode actualNode = JSON.readTree(actual);
        assertEquals(
                expectedNode,
                actualNode,
                "JSON payload for '" + name + "' differs from " + file + "\n\nActual:\n" + actual);
    }

    private static void record(Path file, String content) throws IOException {
        Files.createDirectories(GOLDEN_DIR);
        Files.writeString(file, content, StandardCharsets.UTF_8);
        fail("No golden file existed at " + file + " - wrote one from the current output. Inspect it, then"
                + " re-run to actually verify against it.");
    }
}
