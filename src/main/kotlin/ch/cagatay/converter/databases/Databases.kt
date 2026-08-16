package ch.cagatay.converter.databases

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bson.Document
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL

data class DatabaseSettings(
    val url: String,
    val username: String,
    val password: String,
    val maximumPoolSize: Int = 10
)

data class MongoSettings(
    val connectionString: String,
    val databaseName: String = "autofeedback-test-tool-fs26-eval"
)

class Databases private constructor(
    autofeedbackSettings: DatabaseSettings,
    classroomSettings: DatabaseSettings,
    autofeedbackTestToolSettings: MongoSettings

) : AutoCloseable {

    companion object {
        var instance = Databases(
            autofeedbackSettings = DatabaseSettings(
                url = "jdbc:postgresql://localhost:5432/autofeedback_live_data",
                username = "postgres",
                password = "password"
            ),
            classroomSettings = DatabaseSettings(
                url = "jdbc:postgresql://localhost:5433/classrooms_live_data",
                username = "postgres",
                password = "password"
            ),
            autofeedbackTestToolSettings = MongoSettings(
                connectionString = "mongodb://localhost:27017",
            )
        )

        private fun requiredEnvironmentVariable(name: String): String =
            System.getenv(name)
                ?: error("Required environment variable $name is missing")

    }

    private val autoFeedbackDataSource =
        createDataSource("autofeedback-pool", autofeedbackSettings)

    private val classroomsDataSource =
        createDataSource("classrooms-pool", classroomSettings)

    private val autofeedbackTestToolClient: MongoClient =
        MongoClients.create(autofeedbackTestToolSettings.connectionString)

    val autofeedbackDsl: DSLContext =
        DSL.using(autoFeedbackDataSource, SQLDialect.POSTGRES)

    val classroomsDsl: DSLContext =
        DSL.using(classroomsDataSource, SQLDialect.POSTGRES)

    val autofeedbackTestToolDatabase: MongoDatabase =
        autofeedbackTestToolClient.getDatabase(autofeedbackTestToolSettings.databaseName)

    fun verifyConnections() {
        autoFeedbackDataSource.connection.use { connection ->
            check(connection.isValid(5)) {
                "Could not connect to customers database"
            }
        }

        classroomsDataSource.connection.use { connection ->
            check(connection.isValid(5)) {
                "Could not connect to billing database"
            }
        }

        autofeedbackTestToolDatabase.runCommand(Document("ping", 1))

        println("Connections are successfully verified")
    }

    override fun close() {
        autoFeedbackDataSource.close()
        classroomsDataSource.close()
        autofeedbackTestToolClient.close()
    }

    private fun createDataSource(
        poolName: String,
        settings: DatabaseSettings
    ): HikariDataSource {
        val config = HikariConfig().apply {
            jdbcUrl = settings.url
            username = settings.username
            password = settings.password

            this.poolName = poolName
            maximumPoolSize = settings.maximumPoolSize
            connectionTimeout = 30_000
            validationTimeout = 5_000
        }

        return HikariDataSource(config)
    }
}