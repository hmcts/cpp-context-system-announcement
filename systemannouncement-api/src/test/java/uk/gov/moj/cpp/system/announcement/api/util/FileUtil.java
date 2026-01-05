package uk.gov.moj.cpp.system.announcement.api.util;

import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.lang.String.format;

public final class FileUtil {

    private FileUtil() {
    }

    public static byte[] getFileContent(final String fileName) throws Exception {
        return Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(fileName).toURI()));
    }

    public static String getFileContent(final String path, final Map<String, Object> namedPlaceholders) throws Exception {
        return new StrSubstitutor(namedPlaceholders).replace(new String(getFileContent(path)));
    }

    public static String getFileContentsAsString(final String fileName) {
        final StringBuilder sb = new StringBuilder();
        getBufferedReader(getReader(getResourceAsStream(fileName))).lines().forEach(sb::append);
        return sb.toString();
    }

    public static String getFileContentsAsString(final String fileName, final Object... placeholders) {
        final StringBuilder sb = new StringBuilder();
        getBufferedReader(getReader(getResourceAsStream(fileName))).lines().forEach(sb::append);
        return format(sb.toString(), placeholders);
    }

    public static BufferedReader getBufferedReader(final InputStreamReader inputStreamReader) {
        return new BufferedReader(inputStreamReader);
    }

    public static InputStreamReader getReader(final InputStream fileStream) {
        return new InputStreamReader(fileStream);
    }

    public static InputStream getResourceAsStream(String fileName) {
        return FileUtil.class.getClassLoader().getResourceAsStream(fileName);
    }

    public static List<String> getFileContentsAsList(final String fileName) {
        return getBufferedReader(getReader(getResourceAsStream(fileName))).lines().collect(Collectors.toList());
    }

    public static byte[] zipFileContent(final String filePathToZip, final String fileNameToCreate) throws Exception {
        final byte[] expectedDocumentContent = getFileContent(filePathToZip);

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (final ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            final ZipEntry zipEntry = new ZipEntry(fileNameToCreate);
            zipEntry.setSize(expectedDocumentContent.length);
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(expectedDocumentContent);
        }

        return byteArrayOutputStream.toByteArray();
    }
}
