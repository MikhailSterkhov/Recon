package org.itzstonlex.recon.annotation;

import org.itzstonlex.recon.annotation.type.Property;

public class PropertyTests {

    @Property(key = "github")
    private String githubLink;

    @Property(key = "author", defaultValue = "ItzStonlex")
    private String author;

    private static void initProperties() {
        ReconProperty reconProperty = new ReconProperty();

        reconProperty.set("github", "https://github.com/ItzStonlex/Recon");
        reconProperty.set("key", "value");
    }

    public static void main(String[] args) {

        // Property init.
        initProperties();

        // Instance fields tests.
        PropertyTests propertyTests = new PropertyTests();
        AnnotatedReconScanner.scanInstance(propertyTests);

        System.out.println("github: " + propertyTests.githubLink);
        System.out.println("author: " + propertyTests.author);
    }
}
