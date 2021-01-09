# Heretere's Config Handler

<p align="left">
    <a href="https://jitpack.io/#heretere/hch">
        <img alt="JitPack" src="https://img.shields.io/jitpack/v/github/heretere/hch?style=for-the-badge">
    </a>
    <a href="#" onclick="return false;">
        <img alt="Lines of code" src="https://img.shields.io/tokei/lines/github/heretere/hch?style=for-the-badge">
    </a>
    <a href="#" onclick="return false;">
        <img alt="GitHub code size in bytes" src="https://img.shields.io/github/languages/code-size/heretere/hch?style=for-the-badge">
    </a>
    <a href="https://github.com/heretere/hch/LICENSE">
        <img alt="GitHub license" src="https://img.shields.io/github/license/heretere/hch?style=for-the-badge">
    </a>
</p>

Heretere's Config Handler is a lazy config loading library. The goal of this library is to allow config file generation
and processing without worrying about structure. The primary way to define config values is through annotations, however
there is also builder support whenever you need to dynamically declare config values.

---

# Features

- Generate and load config files using annotations
- Generate and load config files using builders
- Automatically structured config files. So you don't have to worry about declaration order
- Create your own serializer and deserializer to handle different types

#### Supported Configuration Types

- TOML
- YAML

---

# Examples

Here is an example main class to show you want it looks like to declare config values.

```java
import com.heretere.hch.MultiConfigHandler;
import com.heretere.hch.ProcessorType;
import com.heretere.hch.collection.ConfigList;
import com.heretere.hch.processor.exception.InvalidTypeException;
import com.heretere.hch.structure.annotation.Comment;
import com.heretere.hch.structure.annotation.ConfigFile;
import com.heretere.hch.structure.annotation.Key;
import com.heretere.hch.structure.annotation.Section;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * You can make configs with a builder or with annotations this class shows both ways.
 */
@ConfigFile("hch/example.toml")
@Comment("Comment 1")
@Comment("Comment 2")
@Comment("Comment 3")
@Section("hch.example") //This is optional you only need to declare a section if you want to add comments to it
public class Example extends JavaPlugin {
    private final MultiConfigHandler handler;

    /* Type 1 Annotations */
    @Key("hch.example.boolean")
    @Comment("You can attach comments to any config variable")
    @Comment("The value assigned to the variable is used as the default value in a config")
    private Boolean value = false;

    @Key("hch.example.name")
    @Comment("To define multiple comments you just")
    @Comment("add multiple comment annotations")
    private String name = "HCH";

    @Key("hch.example.enum")
    @Comment("You can easily define enum types")
    private ExampleEnum enumTest = ExampleEnum.EXAMPLE;

    @Key("hch.example.list")
    @Comment("Lists are supported as well.")
    private ConfigList<String> listTest = ConfigList.newInstance(String.class, "A", "B", "C");

    public Example() {
        //MultiConfigHandler takes one argument which is a Path.
        //The path is used as the base directory for creating all the other config files.
        this.handler = new MultiConfigHandler(this.getDataFolder().toPath());
    }

    @Override
    public void onEnable() {
        //This loads any previously registered config classes.
        this.handler.load();
        //#loadConfigClass takes two arguments the first being the class instance to load the annotations of
        // and the second is what type of config to generate.
        try {
            this.handler.loadConfigClass(this, ProcessorType.TOML);
        } catch (IllegalAccessException | InvalidTypeException e) {
            //Any loading errors are passed here to be handled.
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        //This will save all the config files
        //The value of the annotated variable at the time of this method call is what is saved to the config file.
        this.handler.unload();
    }

    private enum ExampleEnum {
        EXAMPLE
    }
}
```

The class above will generate this config file:
Since we selected TOML to generate, it generated a toml file.

```toml
# Comment 1
# Comment 2
# Comment 3
[hch.example]
# You can attach comments to any config variable
# The value assigned to the variable is used as the default value in a config
boolean = false
# You can easily define enum types
enum = "EXAMPLE"
# Lists are supported as well.
list = ["A", 
        "B", 
        "C"]
# To define multiple comments you just
# add multiple comment annotations
name = "HCH"
```

---

# Usage

##### Maven

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml

<dependency>
    <groupId>com.github.heretere</groupId>
    <artifactId>hch</artifactId>
    <version>Version</version>
</dependency>
```

##### Gradle

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

```groovy
dependencies {
    implementation 'com.github.heretere:hch:Version'
}
```