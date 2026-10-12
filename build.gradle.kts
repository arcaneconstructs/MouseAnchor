import com.sun.imageio.plugins.jpeg.JPEG.vendor

plugins { 
    id("net.fabricmc.fabric-loom") version "1.18-SNAPSHOT"
}

version = "1.0.0.preview1"

dependencies {
    minecraft("com.mojang:minecraft:${sc.current.project}")
    
    implementation("net.fabricmc:fabric-loader:0.19.5")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
}

java {
    toolchain { 
        languageVersion = JavaLanguageVersion.of(25)
        vendor = JvmVendorSpec.MICROSOFT
    }
}

tasks.processResources {
    inputs.property("version", version)
    inputs.property("minecraft_version", sc.current.project)
    
    filesMatching("fabric.mod.json") {
        expand("version" to version, "minecraft_version" to sc.current.project)
    }
}

tasks.jar {
    archiveBaseName = "MouseAnchor"
    version = "$version+${sc.current.project}"
}
