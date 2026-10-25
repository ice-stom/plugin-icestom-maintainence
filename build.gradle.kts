plugins {
    id("java")
}

group = "com.example"

repositories {
    mavenCentral()
    mavenLocal()
}

val runIcestom: Configuration by configurations.creating

dependencies {
    val icestomVersion = "io.gitlab.icestom:IceStom:0.4.0"

    compileOnly(icestomVersion)
    runIcestom(icestomVersion)
}

tasks.register<JavaExec>("runIcestom") {
    group = "icestom"
    description = "Runs an Icestom server with this plugin loaded from its compiled classes."

    dependsOn(tasks.named("classes"))

    mainClass.set("io.gitlab.icestom.icestom.IceStom")
    classpath = runIcestom

    val runDir = layout.projectDirectory.dir("run").asFile
    workingDir = runDir
    doFirst { runDir.mkdirs() }

    val classesDir = sourceSets.main.get().output.classesDirs.first()
    systemProperty("io.gitlab.icestom.development", classesDir.absolutePath)
    standardInput = System.`in`
}