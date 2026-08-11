plugins {
    kotlin("jvm") version "2.4.10"
    application
    id("org.jooq.jooq-codegen-gradle") version "3.21.7"
    id("io.freefair.lombok") version "9.5.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

val jooqVersion = "3.21.7"
val postgresDriverVersion = "42.7.13"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation("org.jooq:jooq:$jooqVersion")
    implementation("com.zaxxer:HikariCP:7.1.0")
    implementation(platform("org.mongodb:mongodb-driver-bom:5.9.1"))
    implementation("org.mongodb:mongodb-driver-sync")
    implementation("org.mongodb:bson-kotlinx")
    implementation("org.mongodb:mongodb-driver-kotlin-extensions")
    implementation("org.eclipse.jgit:org.eclipse.jgit:7.7.1.202607240634-r")
    implementation(platform("io.micrometer:micrometer-bom:1.17.0"))
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.gitlab4j:gitlab4j-api:6.1.0")
    implementation("org.apache.commons:commons-compress:1.28.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")


    runtimeOnly("org.postgresql:postgresql:42.7.13")
    runtimeOnly("org.postgresql:postgresql:42.7.13")
    runtimeOnly("org.postgresql:postgresql:$postgresDriverVersion")

    testImplementation(kotlin("test"))

    jooqCodegen("org.postgresql:postgresql:$postgresDriverVersion")
}

application {
    mainClass = "org.example.MainKt"
}

jooq {
    // Configuration shared by both databases
    configuration {
        generator {
            name = "org.jooq.codegen.KotlinGenerator"

            database {
                name = "org.jooq.meta.postgres.PostgresDatabase"
                includes = ".*"
                excludes = "flyway_schema_history"
            }
        }
    }

    executions {
        create("autofeedback") {
            configuration {
                jdbc {
                    driver = "org.postgresql.Driver"
                    url = providers.environmentVariable("CUSTOMERS_DB_URL")
                        .getOrElse("jdbc:postgresql://localhost:5432/autofeedback_live_data")
                    user = providers.environmentVariable("CUSTOMERS_DB_USER")
                        .getOrElse("postgres")
                    password = providers.environmentVariable("CUSTOMERS_DB_PASSWORD")
                        .getOrElse("password")
                }

                generator {
                    database {
                        inputSchema = "public"
                    }

                    target {
                        packageName = "com.example.jooq.autofeedback"
                        directory = layout.buildDirectory
                            .dir("generated-src/jooq/autofeedback")
                            .get()
                            .asFile
                            .absolutePath
                    }
                }
            }
        }

        create("classrooms") {
            configuration {
                jdbc {
                    driver = "org.postgresql.Driver"
                    url = providers.environmentVariable("BILLING_DB_URL")
                        .getOrElse("jdbc:postgresql://localhost:5433/classrooms_live_data")
                    user = providers.environmentVariable("BILLING_DB_USER")
                        .getOrElse("postgres")
                    password = providers.environmentVariable("BILLING_DB_PASSWORD")
                        .getOrElse("password")
                }

                generator {
                    database {
                        inputSchema = "public"
                    }

                    target {
                        packageName = "com.example.jooq.classrooms"
                        directory = layout.buildDirectory
                            .dir("generated-src/jooq/classrooms")
                            .get()
                            .asFile
                            .absolutePath
                    }
                }
            }
        }
    }
}

tasks.named("compileKotlin") {
    // Aggregate task that runs every configured execution
    dependsOn(tasks.named("jooqCodegen"))
}

tasks.test {
    useJUnitPlatform()
}
